package entity;

import engine.Engine;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    public long windowId;

    public Player(long windowId) {
        this.windowId = windowId;
    }

    public void update(float deltaTime) {
        @NotNull PlayerState state = Engine.playerState;
        // Détection des touches
        boolean leftPressed = glfwGetKey(windowId, GLFW_KEY_A) == GLFW_PRESS;
        boolean rightPressed = glfwGetKey(windowId, GLFW_KEY_D) == GLFW_PRESS;
        boolean jumpPressed = glfwGetKey(windowId, GLFW_KEY_SPACE) == GLFW_PRESS ||
                               glfwGetKey(windowId, GLFW_KEY_W) == GLFW_PRESS;

        // Déterminez les mouvements en fonction de l'État au sol
        state.moveLeft = leftPressed && !rightPressed;
        state.moveRight = rightPressed && !leftPressed;

        state.jump = jumpPressed && state.isGrounded;

        // 3. Appliquer les mouvements
        applyMovement(deltaTime);
        state.facingRight = state.moveRight || (!state.moveLeft && state.facingRight);

        if (state.jump && !state.isGrounded) {
            state.jump = true;
        }
    }

    public void applyMovement(float deltaTime) {
        Vector2f newVelocity = Engine.playerState.velocity;

        // Constantes d'accélération
        final float move = Engine.playerState.force;

        // Gestion accélération horizontale
        if (Engine.playerState.moveLeft) {
            newVelocity.x -= move * deltaTime; // Limite gauche
        } else if (Engine.playerState.moveRight) {
                newVelocity.x += move * deltaTime;  // Limite droite
        }

        // Saut inchangé
        if (Engine.playerState.jump) {
            newVelocity.y = Engine.playerState.force;
        }
    }
}