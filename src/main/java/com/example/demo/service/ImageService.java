package com.example.demo.service;

import com.example.demo.Entites.Annonce;
import com.example.demo.Entites.Image;
import com.example.demo.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Classe de service pour la gestion des opérations liées aux images telles que la sauvegarde,
 * la compression, la récupération et la suppression des images. Gère l'intégration avec
 * le système de fichiers et le référentiel d'images.
 */
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${uploads.dir:uploads}")
    private String uploadDir;
    private final ImageRepository imageRepository;

    private static final int MAX_IMAGE_WIDTH = 1200;
    private static final int MAX_IMAGE_HEIGHT = 1200;
    private static final float COMPRESSION_QUALITY = 0.8f; // 80% de qualité

    @Transactional
    public List<Image> saveImage(List<MultipartFile> files, Annonce annonce) {
        return files.stream()
                .map(file -> {
                    try {
                        return saveAndCompressImage(file, annonce);
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur de traitement d'image: " + e.getMessage());
                    }
                })
                .toList();
    }
    @Transactional
    public void deleteImage(Image image) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Path imagePath = uploadPath.resolve(image.getUrl().substring(1));
        Files.deleteIfExists(imagePath);
        imageRepository.delete(image);
    }

    public List<Image> getImagesByAnnonce(Annonce annonce) {
        return imageRepository.findAllByAnnonce_Id(annonce.getId());
    }

    /**
     * Sauvegarde et compresse un fichier image associé à une annonce spécifique.
     * La méthode valide l'image, crée les répertoires de fichiers nécessaires,
     * génère un nom de fichier unique, compresse l'image et la sauvegarde dans
     * l'espace de stockage spécifié. L'image sauvegardée est associée à
     * l'annonce donnée et stockée dans la base de données.
     *
     * @param file le fichier image à sauvegarder et compresser
     * @param annonce l'entité annonce à laquelle associer l'image
     * @return l'entité Image sauvegardée après compression et association avec l'annonce
     * @throws IOException en cas d'erreur lors du traitement ou de la compression du fichier
     */
    private Image saveAndCompressImage(MultipartFile file, Annonce annonce) throws IOException {
        // Validation du fichier
        validateImage(file);

        // Création du répertoire si inexistant
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Génération du nom de fichier
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID() + extension;

        // Compression et sauvegarde
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage compressedImage = compressImage(originalImage);

        Path outputPath = uploadPath.resolve(filename);
        saveCompressedImage(compressedImage, outputPath, extension);

        // Détermination du type MIME
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = determineContentType(extension);
        }

        // Création de l'entité Image
        Image image = new Image();
        image.setUrl("/uploads/" + filename);
        image.setImageType(contentType); // Ceci est le champ obligatoire
        image.setAnnonce(annonce);

        return imageRepository.save(image);
    }

    private String determineContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    /**
     * Compresse l'image donnée en la redimensionnant proportionnellement à une
     * largeur et une hauteur maximales prédéfinies, tout en maintenant la
     * qualité d'origine.
     *
     * @param originalImage l'image originale à compresser
     * @return une nouvelle BufferedImage qui est la version compressée de l'image originale
     */

    private BufferedImage compressImage(BufferedImage originalImage) {

        return Scalr.resize(
                originalImage,
                Scalr.Method.QUALITY,
                Scalr.Mode.AUTOMATIC,
                MAX_IMAGE_WIDTH,
                MAX_IMAGE_HEIGHT
        );
    }

    /**
     * Sauvegarde une version compressée de l'image fournie vers le chemin de sortie spécifié,
     * en utilisant l'extension de fichier donnée pour déterminer le format.
     * Prend en charge la compression JPEG avec une qualité configurable et utilise le
     * traitement par défaut pour les autres formats d'image.
     *
     * @param image la BufferedImage à compresser et sauvegarder
     * @param outputPath le chemin du fichier où l'image compressée sera sauvegardée
     * @param extension l'extension du fichier (par ex., ".jpg", ".png") indiquant le format de l'image
     * @throws IOException si une erreur survient pendant la compression ou la sauvegarde de l'image
     */

    private void saveCompressedImage(BufferedImage image, Path outputPath, String extension) throws IOException {
        String formatName = extension.replace(".", "").toUpperCase();

        // Configuration de la compression pour JPEG
        if (Arrays.asList("JPG", "JPEG").contains(formatName)) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(COMPRESSION_QUALITY);

                try (ImageOutputStream output = ImageIO.createImageOutputStream(outputPath.toFile())) {
                    writer.setOutput(output);
                    writer.write(null, new IIOImage(image, null, null), param);
                }
                writer.dispose();
                return;
            }
        }

        // Fallback pour PNG et autres formats
        ImageIO.write(image, formatName, outputPath.toFile());
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Le fichier n'est pas une image valide");
        }

        List<String> allowedTypes = Arrays.asList(
                "image/jpeg",
                "image/png",
                "image/webp"
        );

        if (!allowedTypes.contains(contentType)) {
            throw new RuntimeException("Type d'image non supporté. Utilisez JPEG, PNG ou WebP");
        }
    }
}
