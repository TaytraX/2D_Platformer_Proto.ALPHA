package entity;

import engine.ThreadManager;
import laucher.Main;
import render.DisplayManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {
    private final Vector2f position;
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();

    private final Vector2f offset = new Vector2f(0, 1.0f); // Décalage (joueur un peu en bas)
    private final Vector2f deadZone = new Vector2f(2.0f, 1.5f); // Zone morte
    private float currentFOV = 3.0f;
    private final float fovStep = 0.2f;

    public Camera() {
        position = new Vector2f(0, 0);
    }

    public void followPlayer(PlayerState playerState, float deltaTime) {
        if (playerState == null) return;

        Vector2f playerPos = playerState.position;

        // Position cible avec offset
        Vector2f targetPos = new Vector2f(
                playerPos.x + offset.x,
                playerPos.y + offset.y
        );

        // Calcul de la distance
        Vector2f distance = new Vector2f(
                targetPos.x - position.x,
                targetPos.y - position.y
        );

        // Dead zone : ne pas bouger si le joueur est proche du centre
        // Vitesse de suivi
        float followSpeed = 2.0f;
        if (Math.abs(distance.x) > deadZone.x) {
            float moveX = distance.x > 0 ?
                    (distance.x - deadZone.x) :
                    (distance.x + deadZone.x);
            position.x += moveX * followSpeed * deltaTime;
        }

        if (Math.abs(distance.y) > deadZone.y) {
            float moveY = distance.y > 0 ?
                    (distance.y - deadZone.y) :
                    (distance.y + deadZone.y);
            position.y += moveY * followSpeed * deltaTime;
        }
    }

    public Matrix4f getProjectionMatrix() {
        DisplayManager display = Main.getWindow().getDisplayManager();
        float worldWidth = display.getWorldWidth() * currentFOV;
        float worldHeight = display.getWorldHeight() * currentFOV;

        return projectionMatrix.identity().ortho(
                -worldWidth/2, worldWidth/2,
                -worldHeight/2, worldHeight/2,
                0, 1
        );
    }

    public void increaseFOV() {
        // Zoom out maximum
        float maxFOV = 5.0f;
        currentFOV = Math.min(currentFOV + fovStep, maxFOV);
    }

    public void decreaseFOV() {
        // Zoom in maximum
        float minFOV = 0.2f;
        currentFOV = Math.max(currentFOV - fovStep, minFOV);
    }

    public void resetFOV() {
        currentFOV = 1.0f;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix.identity().translation(-position.x, -position.y, 0);
    }
}