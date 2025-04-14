package com.example.authservice.repository;

import com.example.authservice.model.Token;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Référentiel pour stocker les jetons d'authentification
 * Dans une implémentation réelle, cela serait remplacé par une base de données
 */
@Repository
public class TokenRepository {

    private final Map<String, Token> tokens = new ConcurrentHashMap<>();

    /**
     * Trouve un jeton par son identifiant
     * @param tokenId Identifiant du jeton
     * @return Jeton trouvé ou null
     */
    public Token findByTokenId(String tokenId) {
        return tokens.get(tokenId);
    }

    /**
     * Sauvegarde un jeton
     * @param token Jeton à sauvegarder
     * @return Jeton sauvegardé
     */
    public Token save(Token token) {
        tokens.put(token.getTokenId(), token);
        return token;
    }

    /**
     * Supprime un jeton
     * @param tokenId Identifiant du jeton à supprimer
     */
    public void deleteByTokenId(String tokenId) {
        tokens.remove(tokenId);
    }

    /**
     * Supprime tous les jetons expirés
     */
    public void deleteExpiredTokens() {
        tokens.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
