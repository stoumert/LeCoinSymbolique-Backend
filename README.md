# LeCoinSymbolique - Backend Java Spring Boot

## Description
LeCoinSymbolique est une application web développée avec Java Spring Boot.  
Cette API permet la gestion des annonces avec des fonctionnalités avancées comme l'authentification sécurisée, le téléchargement d'images, l'envoi de mails et l'utilisation de WebSocket.

---

## Prérequis
- Java 17 (tester avec java 24)
- Maven
- Docker

---

## Technologies utilisées
- Spring Boot 3.4.5
- MariaDB
- JWT (Json Web Token)
- Spring Security
- WebSocket
- Lombok
- MapStruct
- Spring Search
- SMTP4Dev (pour l'émulation de serveur mail)
- Adminer (pour la visualisation de la base de données)
- ImgScalr (pour le redimensionnement des images)

---

## Installation et Lancement

### 1. Lancer les conteneurs Docker
Mettez-vous dans le dossier ```resources ``` puis executez la commande suivante 
```bash
  docker-compose up -d
```
Cela va démarrer :

Une base de données MariaDB (port 3307)

Un client Adminer (port 8081)

Un serveur SMTP4Dev (port 9025 pour SMTP, port 9081 pour l'interface web)

Une fois que les conteneurs lancés, on pourra lancer application depuis le fichier ```DemoApplication```

## Si une erreur survient au lancement :

- Vérifiez que le port 8080 n'est pas utiliser par une autre application
```bash
  lsof -i :8080
```

---
- Connectez-vous au http://localhost:8081/
- Utilisateur : root
- Mot de passe : PrinceYann@123
- Par la suite, vérifiez que la base de donnée exite 
- Si elle n'existe pas crée la en lui attribuant le nom ```LeCoinSymbolique```
---

# Test
Pour tester les différentes routes de notre api il vous faudra Installer Postman.
https://www.postman.com/

Toutes les requêtes vers api doivent être de la forme
http://localhost:8080/api/ $laRouteVersLeController

