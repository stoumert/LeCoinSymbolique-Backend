package com.example.demo.service;

import com.example.demo.Entites.Client;
import com.example.demo.Entites.Role;
import com.example.demo.Entites.Validation;
import com.example.demo.TypeRole;
import com.example.demo.dto.ClientDTO;
import com.example.demo.exception.EmailException;
import com.example.demo.exception.PasswordException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UsernameException;
import com.example.demo.mapper.ClientMapper;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.Instant;
import java.util.*;

@AllArgsConstructor // Génère automatiquement un constructeur avec un paramètre pour tous les attributs de la class
@Service //pour dire a spring boot que c'est class de logique métier
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private ValidationService validationService;
    private ClientMapper clientMapper; //on injecte la dépendance pour pouvoir mappé le client en clientdto et vis versa

    public void inscription(Client user) throws Exception {
        if (user.getPassword().length() < 7) {
            throw new PasswordException("Le mot de passe doit contenir au moins 7 caractères");
        }
        if (user.getPassword().contains(user.getUsername())
                || user.getPassword().contains(user.getFullName())) {
            throw new PasswordException("Le mot de passe ne doit pas contenir votre pseudo ni votre nom");
        }
        if (!isStudentEmail(user.getEmail())) {
            throw new EmailException("Veuillez utiliser une adresse e-mail universitaire valide");
        }

        Optional<Client> userWithMail = userRepository.findByEmail(user.getEmail());
        if (userWithMail.isPresent()) {
            throw new EmailException("Votre mail existe deja");
        }
        Optional<Client> userWithUsername = userRepository.findByUsername(user.getUsername());
        if (userWithUsername.isPresent()) {
            throw new RuntimeException("Votre username existe deja");
        }
        if (!user.getLocation().isEmpty()){
            setLongitudeAndLatitude(user.getLocation(),user);
        }
        String passwordCrypt = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordCrypt);
        Role userRole = new Role();
        userRole.setRoleType(TypeRole.USER);
        user.setRole(userRole);
        Client client = userRepository.save(user);
        validationService.save(client);
    }
    public void updatePhoneNumber(String phoneNumber, String username) {
        if (userRepository.findClientByPhoneNumber(phoneNumber).isPresent()) {
            throw new RuntimeException("le numero de telephone est deja attribué a un autre utilisateur");
        }
        Client client = this.userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("utilisateur invalide"));
        client.setPhoneNumber(phoneNumber);
        this.userRepository.save(client);
        validationService.savePhoneNumber(client);
    }

    public boolean isStudentEmail(String email) {
        if (email == null || !email.contains("@")) return false;

        String domain = email.substring(email.indexOf("@")).toLowerCase();
        return getStudentEmailDomains().contains(domain);
    }

    private static List<String> getStudentEmailDomains() {
        return Arrays.asList(
                "@etu.univ-amu.fr", "@etud.univ-lille.fr", "@etu.u-bordeaux.fr",
                "@etu.univ-grenoble-alpes.fr", "@etu.umontpellier.fr", "@etu.univ-montp3.fr",
                "@etu.unistra.fr", "@etu.univ-lorraine.fr", "@etu.univ-st-etienne.fr",
                "@etu.univ-poitiers.fr", "@etu.unilim.fr", "@etud.univ-angers.fr",
                "@etu.univ-reims.fr", "@etu.univ-orleans.fr", "@etu.univ-tours.fr",
                "@etud.u-psud.fr", "@ens-paris-saclay.fr", "@etudiant.univ-rennes1.fr",
                "@etudiant.univ-rennes2.fr", "@etud.univ-paris13.fr", "@etu.univ-paris1.fr",
                "@etu.unicaen.fr", "@etu.univ-cotedazur.fr", "@etu.univ-reunion.fr",
                "@etu.u-paris.fr", "@etu.u-pec.fr", "@etudiants.u-paris2.fr",
                "@etudiant.univ-lr.fr"
        );
    }

    public void activation(Map<String, String> activation) {
        Validation validation=  this.validationService.findByValidationCode(activation.get("validation_code"));
        if (Instant.now().isAfter(validation.getExpiration())){
            throw new RuntimeException("Le code à expiré");
        }
        Client client = this.userRepository.findById(validation.getClient().getId())
                .orElseThrow(()->new RuntimeException("utilisateur invalide"));
        client.setStatus(true);
        this.userRepository.save(client);
    }

    public void activationPhoneNumber(Map<String, String> activation) {
        Validation validation=  this.validationService.findByValidationCode(activation.get("validation_code"));
        if (Instant.now().isAfter(validation.getExpiration())){
            throw new RuntimeException("Le code à expiré");
        }
        Client client = this.userRepository.findById(validation.getClient().getId())
                .orElseThrow(()->new RuntimeException("utilisateur invalide"));
        client.setPhoneNumberVerified(true);
        this.userRepository.save(client);
    }
    @Override
    public Client loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("aucun user ne correspond a cette username"));

    }

    /**
     *
     * @param id
     * @return un clientDTO qui correspond à id sinon elle léve une exception
     */
    public ClientDTO findById(int id) {
        return this.clientMapper.toDto(this.userRepository.findById(id).orElseThrow(()->new UserNotFoundException("utilisateur invalide")));
    }

    /**
     *
     * @param username
     * @return un clientDTO qui correspond au username sinon elle léve une exception
     */
    public ClientDTO findByUsername(String username) {
        return this.clientMapper.toDto(this.userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("utilisateur invalide")));
    }

    /**
     * méthode qui nous permet de réinisiliser le mot de passe elle géner un code de validation et l'envoie à client
     * @param email
     */
    public void reinitializePassword(Map<String, String> email) {
        Client client = this.userRepository.findByEmail(email.get("email")).orElseThrow(()->new UserNotFoundException("utilisateur invalide"));
        validationService.save(client);
    }

    /**
     * cette méthode mettre à jour le mot de passe oublié par l'utilisateur
     * @param password (email,mot de passe, le code de validation)
     */
    public void setPassword(Map<String, String> password) {

        String newPassword = password.get("new_password");
        String validationCode = password.get("validation_code");
        String email = password.get("email");
        Client client = this.userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("utilisateur invalide"));
        if (newPassword.length() < 7) {
            throw new RuntimeException("le message dois avoir en moins 7 caractéres");
        }
        Validation validation = validationService.findByValidationCode(validationCode);
        if (Instant.now().isAfter(validation.getExpiration())){
            throw new RuntimeException("la code à expiré");
        }
        if (!Objects.equals(client.getId(), validation.getClient().getId())){
            throw new RuntimeException("vous ne pouvez pas modifier un autre utilisateur");
        }
        String passwordCrypt = passwordEncoder.encode(newPassword);
        client.setPassword(passwordCrypt);
        this.userRepository.save(client);
    }

    private void setLongitudeAndLatitude(String localisation, Client client) throws Exception {
        Map<String, String> longitudeAndLatitude = LocalisationService.getCoordinateWithLocalisation(localisation);
        client.setLongitude(Double.parseDouble(longitudeAndLatitude.get("lon")));
        client.setLatitude(Double.parseDouble(longitudeAndLatitude.get("lat")));
        this.userRepository.save(client);
    }

    public ClientDTO updateClient(Client client, Principal principal) throws AccessDeniedException {
        Client clientToUpdate = this.userRepository.findById(client.getId()).orElseThrow(()->new UserNotFoundException("utilisateur invalide"));
        if (!client.getId().equals(this.userRepository.findByUsername(principal.getName()).orElseThrow().getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cet utilisateur.");
        }
        if (client.getLocation() != null){
            clientToUpdate.setLocation(client.getLocation());
        }
        if (client.getPhoneNumber() != null){
            clientToUpdate.setPhoneNumber(client.getPhoneNumber());
        }
        return this.clientMapper.toDto(this.userRepository.save(clientToUpdate));
    }

    public void updatePassword(Map<String, String> password, Principal principal) {
        Client clientToUpdate = this.userRepository.findByUsername(principal.getName()).orElseThrow(()->new UserNotFoundException("utilisateur invalide"));
        if (password.get("current_password") != null ){
            if (!passwordEncoder.matches(password.get("current_password"),clientToUpdate.getPassword())){
                throw new PasswordException("Le mot de passe actuel ne correspond pas");
            }
        }
        if(password.get("new_password") != null){
            String newPassword = password.get("new_password");
            if (newPassword.length() < 7) {
                throw new PasswordException("Le mot de passe doit contenir en moins une taille de 7");
            }
            if (newPassword.contains(clientToUpdate.getUsername())){
                throw new PasswordException("Le mot de passe ne doit pas contenir votre pseudo");
            }if (newPassword.contains(clientToUpdate.getFullName())){
                throw new PasswordException("Le mot de passe ne doit pas contenir votre nom");
            }if (passwordEncoder.matches(newPassword,clientToUpdate.getPassword())){
                throw new PasswordException("Le nouveau mot de passe ne doit pas être identique au mot de passe actuel");
            }
            else {
                clientToUpdate.setPassword(passwordEncoder.encode(newPassword));
                this.userRepository.save(clientToUpdate);
            }
        }
    }
}
