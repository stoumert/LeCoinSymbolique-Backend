package com.example.demo.Controller;

import com.example.demo.Entites.Client;
import com.example.demo.configuration.JwtService;
import com.example.demo.dto.AuthentificationDTO;
import com.example.demo.dto.ClientDTO;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Map;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping()
public class ClientController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @PostMapping(path = "inscription",consumes = MediaType.APPLICATION_JSON_VALUE)
    public void inscription(@RequestBody Client user) throws Exception {
        userService.inscription(user);
    }
    @PostMapping(path="activation",consumes = MediaType.APPLICATION_JSON_VALUE)
    public void validation(@RequestBody Map<String, String> activation) {
        this.userService.activation(activation);
    }
    @PostMapping(path = "connexion",consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
         Authentication authentificate = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authentificationDTO.username()
                                , authentificationDTO.password()
                        )
        );

        if (authentificate.isAuthenticated()){
            Map<String, String> accessTokenAndRefreshToken =  this.jwtService.generateToken(authentificationDTO.username());
            //log.info(bearer.toString());
            return accessTokenAndRefreshToken;
        }
        return null;
    }
    @PostMapping(path = "refresh-token")
    public Map<String, String> refreshToken(@RequestBody Map<String, String> refreshToken) {
        if (
                jwtService.isRefreshToken(refreshToken.get("refreshToken")) &&
                !jwtService.isTokenExpired(refreshToken.get("refreshToken")
                ))
        {
            return Map.of("bearer", jwtService.buildAccessToken(refreshToken.get("username")));
        }
    return null;
    }
    @PostMapping(path = "deconnexion")
    public void deconnexion(Principal principal) {
        jwtService.deconnect(principal);
    }
    @PostMapping(path = "password-oublier")
    public void passwordOublier(@RequestBody Map<String, String> email) {
        userService.reinitializePassword(email);
    }
    @PostMapping(path = "password-modification")
    public void passwordModification(@RequestBody Map<String, String> password) {
        userService.setPassword(password);
    }

    @PostMapping(path = "update_phone_number")
    public void updatePhoneNumber(@RequestBody Map<String, String> phoneNumber) {
        String username = phoneNumber.get("username");
        String phoneNumberNew = phoneNumber.get("phoneNumber");
        userService.updatePhoneNumber(phoneNumberNew, username);
    }

    @PostMapping(path = "activation_phone_number")
    public void activationPhoneNumber(@RequestBody Map<String, String> phoneNumber) {
        userService.activationPhoneNumber(phoneNumber);
    }
    @GetMapping(path = "user/{id}")
    public @ResponseBody ClientDTO findById(@PathVariable int id) {
        ClientDTO client = this.userService.findById(id);
        return client;
    }
    @GetMapping(path = "username/{username}")
    public @ResponseBody ClientDTO findByUserName(@PathVariable String username) {
        return this.userService.findByUsername(username);
    }

    @GetMapping("/me")
    public @ResponseBody ClientDTO getMe(Principal principal) {
        String username = principal.getName();
        return userService.findByUsername(username);
    }
    @PostMapping(path = "update")
    public @ResponseBody ClientDTO update(@RequestBody Client client, Principal principal) throws AccessDeniedException {
        return userService.updateClient(client, principal);
    }
    @PostMapping(path = "update/password")
    public void updatePassword(@RequestBody Map<String, String> password, Principal principal) {
        userService.updatePassword(password,principal);
    }
}
