package entity;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    public long windowId;

    public Player(long windowId) {
        this.windowId = windowId;
    }

    public PlayerState update(@NotNull PlayerState state, float deltaTime) {
        // Détection des touches
        boolean leftPressed = glfwGetKey(windowId, GLFW_KEY_A) == GLFW_PRESS;
        boolean rightPressed = glfwGetKey(windowId, GLFW_KEY_D) == GLFW_PRESS;
        boolean jumpPressed = glfwGetKey(windowId, GLFW_KEY_SPACE) == GLFW_PRESS ||
                               glfwGetKey(windowId, GLFW_KEY_W) == GLFW_PRESS;

        // Déterminez les mouvements en fonction de l'État au sol
        boolean moveLeft = leftPressed && !rightPressed;
        boolean moveRight = rightPressed && !leftPressed;

        boolean jump = jumpPressed && state.isGrounded();

        // 3. Appliquer les mouvements
        Vector2f newVelocity = applyMovement(state, moveLeft, moveRight, jump, deltaTime);
        boolean facingRight = moveRight || (!moveLeft && state.facingRight());

        // Retourner le state avec les actions déterminées
        return new PlayerState(
                state.position(),
                newVelocity,
                state.jumpVelocity(),
                state.isGrounded(),
                state.wasGrounded(),
                state.previousVelocity(),
                state.animationState(),
                facingRight,
                moveLeft,      // ← Action déterminée
                moveRight,     // ← Action déterminée
                jump,
                state.force(),
                state.blockedHorizontally(),
                System.currentTimeMillis()
        );
    }

    public Vector2f applyMovement(PlayerState state, boolean moveLeft, boolean moveRight, boolean jump, float deltaTime) {
        Vector2f newVelocity = new Vector2f(state.velocity());

        // Constantes d'accélération
        final float move = state.force();

        // Gestion accélération horizontale
        if (moveLeft) {
            newVelocity.x -= move * deltaTime; // Limite gauche
        } else if (moveRight) {
                newVelocity.x += move * deltaTime;  // Limite droite
        }

        // Saut inchangé
        if (jump) {
            newVelocity.y = state.force();
        }

        return newVelocity;
    }
}