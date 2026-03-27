package engine;

import entity.PlayerState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.List;

public class Physics {
    public static final float GRAVITY = -8.0f;
    private static final float X_COLLISION_TOLERANCE = 0.01f;
    private static final float Y_COLLISION_TOLERANCE = 0.03f;


    public void update(@NotNull List<AABB> platforms, float deltaTime) {
        PlayerState currentState = Engine.playerState;
        Vector2f newVelocity = currentState.velocity;
        Vector2f newPosition = currentState.position;
        currentState.isGrounded = false;
        Vector2f jumpVelocity = currentState.velocity;
        boolean blockedHorizontally = currentState.blockedHorizontally;

        // Calcul de la vitesse max basée uniquement sur la gravité
        float maxSpeed = 45.0f / (float) Math.sqrt(Math.abs(GRAVITY));

        newVelocity.x = Math.max(-maxSpeed, Math.min(maxSpeed, newVelocity.x));
        newPosition.x += newVelocity.x * deltaTime;

        AABB playerAABB = Engine.playerState.aabb;

        // 1. ÉTAPE X : Traiter toutes les plateformes pour X

        for (AABB platform : platforms) {
            if(playerAABB.collidesWith(platform)) {
                float overlapX = playerAABB.getOverlapX(platform);
                float overlapY = playerAABB.getOverlapY(platform);

                if (overlapX < overlapY - X_COLLISION_TOLERANCE) {
                    blockedHorizontally = true;
                    // Traiter collision horizontale
                    if (newVelocity.x > 0) {
                        newVelocity.x = 0;
                        newPosition.x = platform.getMinX() - PlayerState.PLAYER_SIZE.x;
                    } else if (newVelocity.x < 0) {
                        newVelocity.x = 0;
                        newPosition.x = platform.getMaxX() + PlayerState.PLAYER_SIZE.x;
                    }
                }
            }
        }

        // 2. ÉTAPE Y : Appliquer le mouvement Y puis tester toutes les plateformes

        // Appliquer la gravité
        newVelocity.y += GRAVITY * deltaTime;
        newPosition.y += newVelocity.y * deltaTime;

        boolean wasGrounded = currentState.isGrounded;

        playerAABB = currentState.aabb;

        for (AABB platform : platforms) {
            if(playerAABB.collidesWith(platform)) {
                float overlapX = playerAABB.getOverlapX(platform);
                float overlapY = playerAABB.getOverlapY(platform);

                if (overlapY < overlapX - Y_COLLISION_TOLERANCE) {
                    // Traiter collision verticale
                    if (newVelocity.y < 0) {
                        newVelocity.y = 0;
                        newPosition.y = platform.getMaxY() + PlayerState.PLAYER_SIZE.y;
                        currentState.isGrounded = true;
                    } else if (newVelocity.y > 0) {
                        newVelocity.y = 0;
                        newPosition.y = platform.getMinY() - PlayerState.PLAYER_SIZE.y;
                    }
                }
            }
        }

        // Décélération exponentielle basée sur la gravité
        if (currentState.isGrounded && !currentState.moveLeft && !currentState.moveRight) {
            float frictionRate = Math.abs(GRAVITY) * 0.50f; // 30% de la gravité
            newVelocity.x *= Math.max(0, 1.0f - frictionRate * deltaTime);
        }

        // Capturer la vitesse au moment du saut
        if (!currentState.isGrounded && wasGrounded) {
            // Le joueur vient d'atterrir, reset jumpVelocity
            jumpVelocity = null;
        } else if (currentState.isGrounded && !wasGrounded) {
            // Le joueur vient de sauter, capturer sa vitesse actuelle
            jumpVelocity = new Vector2f(newVelocity.x, 0); // Seulement X
        }

        // Réinitialiser quand le joueur touche le sol
        if (currentState.isGrounded) {
            blockedHorizontally = false;
        }

        // réduction du contrôle aérien en simulant la loi de conserve du momentum
        if (!currentState.isGrounded && jumpVelocity != null) {
            float airControlFriction = Math.abs(GRAVITY) * 0.20f;

            // conservation du momentum jusqu'à ce qu'il y ai collisions horizontales
            if (!blockedHorizontally) {
                newVelocity.x = jumpVelocity.x;

                if ((currentState.moveLeft && jumpVelocity.x > 0) ||
                        (currentState.moveRight && jumpVelocity.x < 0)) {

                    if (currentState.moveLeft) {
                        newVelocity.x -= airControlFriction * deltaTime;
                    } else {
                        newVelocity.x += airControlFriction * deltaTime;
                    }
                }
            }else newVelocity.x = 0; // arrêt forcé dès qu'il y ai collisions horizontales et que le joueur n'est pas au sol
        }
    }
}