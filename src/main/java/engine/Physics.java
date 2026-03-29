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
        boolean isGrounded = false;
        boolean blockedHorizontally = Engine.playerState.blockedHorizontally;

        // Calcul de la vitesse max basée uniquement sur la gravité
        float maxSpeed = 45.0f / (float) Math.sqrt(Math.abs(GRAVITY));

        Engine.playerState.velocity.x = Math.max(-maxSpeed, Math.min(maxSpeed, Engine.playerState.velocity.x));
        Engine.playerState.position.x += Engine.playerState.velocity.x * deltaTime;

        // 1. ÉTAPE X : Traiter toutes les plateformes pour X

        for (AABB platform : platforms) {
            if(Engine.playerState.aabb.collidesWith(platform)) {
                float overlapX = Engine.playerState.aabb.getOverlapX(platform);
                float overlapY = Engine.playerState.aabb.getOverlapY(platform);

                if (overlapX < overlapY - X_COLLISION_TOLERANCE) {
                    blockedHorizontally = true;
                    // Traiter collision horizontale
                    if (Engine.playerState.velocity.x > 0) {
                        Engine.playerState.velocity.x = 0;
                        Engine.playerState.position.x = platform.getMinX() - PlayerState.PLAYER_SIZE.x;
                    } else if (Engine.playerState.velocity.x < 0) {
                        Engine.playerState.velocity.x = 0;
                        Engine.playerState.position.x = platform.getMaxX() + PlayerState.PLAYER_SIZE.x;
                    }
                }
            }
        }

        // 2. ÉTAPE Y : Appliquer le mouvement Y puis tester toutes les plateformes

        // Appliquer la gravité
        Engine.playerState.velocity.y += GRAVITY * deltaTime;
        Engine.playerState.position.y += Engine.playerState.velocity.y * deltaTime;

        boolean wasGrounded = Engine.playerState.isGrounded;

        for (AABB platform : platforms) {
            if(Engine.playerState.aabb.collidesWith(platform)) {
                float overlapX = Engine.playerState.aabb.getOverlapX(platform);
                float overlapY = Engine.playerState.aabb.getOverlapY(platform);

                if (overlapY < overlapX - Y_COLLISION_TOLERANCE) {
                    // Traiter collision verticale
                    if (Engine.playerState.velocity.y < 0) {
                        Engine.playerState.velocity.y = 0;
                        Engine.playerState.position.y = platform.getMaxY() + PlayerState.PLAYER_SIZE.y;
                        isGrounded = true;
                    } else if (Engine.playerState.velocity.y > 0) {
                        Engine.playerState.velocity.y = 0;
                        Engine.playerState.position.y = platform.getMinY() - PlayerState.PLAYER_SIZE.y;
                    }
                }
            }
        }

        // Décélération exponentielle basée sur la gravité
        if (isGrounded && !Engine.playerState.moveLeft && !Engine.playerState.moveRight) {
            float frictionRate = Math.abs(GRAVITY) * 0.50f; // 30% de la gravité
            Engine.playerState.velocity.x *= Math.max(0, 1.0f - frictionRate * deltaTime);
        }

        // Capturer la vitesse au moment du saut
        if (!Engine.playerState.isGrounded && isGrounded) {
            // Le joueur vient d'atterrir, reset jumpVelocity
            Engine.playerState.jumpVelocity = null;
        } else if (Engine.playerState.isGrounded && !isGrounded) {
            // Le joueur vient de sauter, capturer sa vitesse actuelle
            Engine.playerState.jumpVelocity = new Vector2f(Engine.playerState.velocity.x, 0); // Seulement X
        }

        // Réinitialiser quand le joueur touche le sol
        if (isGrounded) {
            blockedHorizontally = false;
        }

        // réduction du contrôle aérien en simulant la loi de conserve du momentum
        if (!isGrounded && Engine.playerState.jumpVelocity != null) {
            float airControlFriction = Math.abs(GRAVITY) * 0.20f;

            // conservation du momentum jusqu'à ce qu'il y ai collisions horizontales
            if (!blockedHorizontally) {
                Engine.playerState.velocity.x = Engine.playerState.jumpVelocity.x;

                if ((Engine.playerState.moveLeft && Engine.playerState.jumpVelocity.x > 0) ||
                        (Engine.playerState.moveRight && Engine.playerState.jumpVelocity.x < 0)) {

                    if (Engine.playerState.moveLeft) {
                        Engine.playerState.velocity.x -= airControlFriction * deltaTime;
                    } else {
                        Engine.playerState.velocity.x += airControlFriction * deltaTime;
                    }
                }
            } else {
                Engine.playerState.velocity.x = 0; // arrêt forcé dès qu'il y ai collisions horizontales et que le joueur n'est pas au sol
                Engine.playerState.jumpVelocity = null;
            }
        }

        Engine.playerState.isGrounded = isGrounded;
        Engine.playerState.wasGrounded = wasGrounded;
    }
}