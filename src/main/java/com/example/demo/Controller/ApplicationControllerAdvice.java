package com.example.demo.Controller;

import com.example.demo.exception.EmailException;
import com.example.demo.exception.NotFoundJwt;
import com.example.demo.exception.PriceException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * ApplicationControllerAdvice est une classe centrale pour la gestion des exceptions dans l'application.
 * Elle est annotée avec {@code @RestControllerAdvice}, ce qui en fait un gestionnaire d'exceptions pour
 * tous les contrôleurs de l'application. Cette classe fournit une gestion personnalisée pour différents types
 * d'exceptions et renvoie des réponses d'erreur structurées et significatives au client.
 *
 * Gestion des Exceptions :
 * - Gère les exceptions liées à l'authentification telles que {@code BadCredentialsException},
 *   {@code MalformedJwtException}, {@code SignatureException}, {@code ExpiredJwtException},
 *   et {@code NotFoundJwt}.
 * - Gère les exceptions génériques telles que {@code Exception} et {@code UnsupportedOperationException}.
 * - Fournit une gestion personnalisée pour les exceptions liées aux ressources comme {@code NoResourceFoundException}.
 *
 * Comportement des Réponses :
 * - Les exceptions liées à l'accès non autorisé renvoient généralement un statut HTTP 401 Unauthorized avec
 *   des détails d'erreur structurés dans la réponse.
 * - Les exceptions de ressource non trouvée renvoient un statut HTTP 404 Not Found avec des détails d'erreur structurés.
 * - En cas d'exceptions génériques, une map de réponse simple avec les informations d'erreur est fournie.
 *
 * Réponses Personnalisées :
 * - Personnalise le {@code ProblemDetail} pour les exceptions, incluant des détails tels que les messages d'erreur,
 *   les métadonnées d'erreur supplémentaires et les détails spécifiques aux exceptions.
 *
 * Cette classe advice assure une gestion cohérente des erreurs au niveau de l'application et fournit des
 * réponses informatives et conviviales.
 */

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = BadCredentialsException.class)
    public @ResponseBody ProblemDetail badCredentialsException(BadCredentialsException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                UNAUTHORIZED,
                "Les identifiants sont invalides"
        );
        problemDetail.setProperty("erreur","Nous n'avons pas pu vous identifier");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = {MalformedJwtException.class, SignatureException.class})
    public @ResponseBody ProblemDetail signatureException(Exception exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                UNAUTHORIZED,
                "La signature du token est invalide"
        );
        problemDetail.setProperty("erreur","Nous n'avons pas pu identifier la signature");
        problemDetail.setDetail(exception.getMessage() );
        return problemDetail;
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = ExpiredJwtException.class)
    public @ResponseBody ProblemDetail expiredJwtException(ExpiredJwtException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                UNAUTHORIZED,
                "Le token à expiré"
        );
        problemDetail.setProperty("erreur","Le token expired");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = NotFoundJwt.class)
    public @ResponseBody ProblemDetail NotFoundJwt(NotFoundJwt exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                UNAUTHORIZED,
                "token not found"
        );
        problemDetail.setProperty("erreur","token not found");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler({Exception.class, UnsupportedOperationException.class})
    public @ResponseBody Map<String, String> errorException(Exception e) {
        return Map.of("erreur",e.getMessage());
    }
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(value = NoResourceFoundException.class)
    public @ResponseBody ProblemDetail noResourceFoundException(NoResourceFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                NOT_FOUND,
                "La resource n\'existe pas"
        );
        problemDetail.setProperty("erreur","La resource n\'existe pas");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
    @ResponseStatus(UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public @ResponseBody ProblemDetail httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                UNSUPPORTED_MEDIA_TYPE,
                "le type de media n'est pas supporter"
        );
        problemDetail.setProperty("erreur","le type de media n'est pas supporter");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(value = PriceException.class)
    public @ResponseBody ProblemDetail priceException(PriceException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                NOT_ACCEPTABLE,
                "Le prix n'est pas valide"
        );
        problemDetail.setProperty("erreur","Le prix n'est pas valide");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
    @ResponseStatus(PRECONDITION_FAILED)
    @ExceptionHandler(value = EmailException.class)
    public @ResponseBody ProblemDetail emailException(EmailException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                PRECONDITION_FAILED,
                "email non valide"
        );
        problemDetail.setProperty("erreur","email non valide");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

}

