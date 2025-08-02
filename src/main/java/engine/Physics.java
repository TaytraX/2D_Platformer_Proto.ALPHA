package engine;

import entity.PlayerState;
import org.joml.Vector2f;

import java.util.List;

public class Physics {
    public static final float GRAVITY = -8.0f;
    private static final float X_COLLISION_TOLERANCE = 0.01f;
    private static final float Y_COLLISION_TOLERANCE = 0.03f;


    public PlayerState update(PlayerState currentState,  List<AABB> platforms, float deltaTime) {
        Vector2f newVelocity = new Vector2f(currentState.velocity());
        Vector2f newPosition = new Vector2f(currentState.position());
        boolean isGrounded = false;
        Vector2f jumpVelocity = currentState.jumpVelocity();

        // Calcul de la vitesse max basée uniquement sur la gravité
        float maxSpeed = 45.0f / (float) Math.sqrt(Math.abs(GRAVITY));

        newVelocity.x = Math.max(-maxSpeed, Math.min(maxSpeed, newVelocity.x));
        newPosition.x += newVelocity.x * deltaTime;

        AABB playerAABB = new AABB(newPosition, PlayerState.PLAYER_SIZE);

        // 1. ÉTAPE X : Traiter toutes les plateformes pour X

        for (AABB platform : platforms) {
            if(playerAABB.collidesWith(platform)) {
                float overlapX = playerAABB.getOverlapX(platform);
                float overlapY = playerAABB.getOverlapY(platform);

                if (overlapX < overlapY - X_COLLISION_TOLERANCE) {
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

        float impactVelocity = newVelocity.y;
        boolean wasGrounded = currentState.isGrounded();

        playerAABB = new AABB(newPosition, PlayerState.PLAYER_SIZE);

        for (AABB platform : platforms) {
            if(playerAABB.collidesWith(platform)) {
                float overlapX = playerAABB.getOverlapX(platform);
                float overlapY = playerAABB.getOverlapY(platform);

                if (overlapY < overlapX - Y_COLLISION_TOLERANCE) {
                    // Traiter collision verticale
                    if (newVelocity.y < 0) {
                        newVelocity.y = 0;
                        newPosition.y = platform.getMaxY() + PlayerState.PLAYER_SIZE.y;
                        isGrounded = true;
                    } else if (newVelocity.y > 0) {
                        newVelocity.y = 0;
                        newPosition.y = platform.getMinY() - PlayerState.PLAYER_SIZE.y;
                    }
                }
            }
        }

        // Décélération exponentielle basée sur la gravité
        if (isGrounded && !currentState.moveLeft() && !currentState.moveRight()) {
            float frictionRate = Math.abs(GRAVITY) * 0.30f; // 30% de la gravité
            newVelocity.x *= Math.max(0, 1.0f - frictionRate * deltaTime);
        }

        // Capturer la vitesse au moment du saut
        if (!currentState.isGrounded() && isGrounded) {
            // Le joueur vient d'atterrir, reset jumpVelocity
            jumpVelocity = null;
        } else if (currentState.isGrounded() && !isGrounded) {
            // Le joueur vient de sauter, capturer sa vitesse actuelle
            jumpVelocity = new Vector2f(newVelocity.x, 0); // Seulement X
        }

        // réduction du contrôle aérien en simulant la loi de conserve du momentum
        if (!isGrounded && jumpVelocity != null && newVelocity.x != 0) {
            float airControlFriction = Math.abs(GRAVITY) * 0.20f;

            // Remettre la vitesse X à celle du saut comme base
            newVelocity.x = jumpVelocity.x;

            // Permettre le changement de direction avec friction
            if ((currentState.moveLeft() && jumpVelocity.x > 0) ||
                    (currentState.moveRight() && jumpVelocity.x < 0)) {

                if (currentState.moveLeft()) {
                    newVelocity.x -= airControlFriction * deltaTime;
                } else {
                    newVelocity.x += airControlFriction * deltaTime;
                }
            }
        }

        return new PlayerState(
                newPosition,
                newVelocity,
                jumpVelocity,
                isGrounded,
                wasGrounded,
                impactVelocity,
                currentState.animationState(),
                currentState.facingRight(),
                currentState.moveLeft(),
                currentState.moveRight(),
                currentState.jump(),
                currentState.force(),
                currentState.timestamp()
        );
    }
}