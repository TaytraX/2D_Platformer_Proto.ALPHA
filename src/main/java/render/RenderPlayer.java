package render;

import engine.AABB;
import engine.GameLogger;
import engine.Renderable;
import engine.ThreadManager;
import entity.AnimationController;
import entity.AnimationState;
import entity.Camera;
import entity.PlayerState;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.GL30C.glBindBuffer;
import static org.lwjgl.opengl.GL30C.glBufferData;
import static org.lwjgl.opengl.GL30C.glGenBuffers;
import static org.lwjgl.opengl.GL30C.glBindTexture;

public class RenderPlayer implements Renderable {

    private int VAO;
    private final AnimationController animationController;
    private AnimationState currentAnimation;
    private Texture currentTexture;

    private final FloatBuffer matrixBuffer = org.lwjgl.BufferUtils.createFloatBuffer(16);
    private final Matrix4f transformationMatrix = new Matrix4f();
    private final Shader shader;

    private Map<AnimationState, Texture> animationTextures;

    public RenderPlayer() {
        shader = new Shader("player");
        animationController = new AnimationController();
        try {
            animationTextures = Map.of(
                    AnimationState.WALKING_RIGHT, new Texture("player_Walking_Right"),
                    AnimationState.WALKING_LEFT,  new Texture("player_Walking_Left"),
                    AnimationState.JUMPING,       new Texture("player_jump"),
                    AnimationState.JUMPING_LEFT,  new Texture("player_Left_jump"),
                    AnimationState.JUMPING_RIGHT, new Texture("player_Rigth_jump"),
                    AnimationState.LANDING,       new Texture("player_isLanding"),
                    AnimationState.SKIDDING,      new Texture("player_isSkidding"),
                    AnimationState.FALLING,       new Texture("player_isFalling"),
                    AnimationState.RISING,        new Texture("player_isRising"),
                    AnimationState.IDLE,          new Texture("player")
            );
        }catch (Exception e) {
            e.printStackTrace();
        }

        assert animationTextures != null;
        currentTexture = animationTextures.get(AnimationState.IDLE);
        currentAnimation = AnimationState.IDLE;

        initialize();
    }

    @Override
    public void initialize() {

        VAO = glGenVertexArrays();
        int VBO = glGenBuffers();
        int EBO = glGenBuffers();
        int textureVBO = glGenBuffers();

        // Géométrie du joueur (quad 2D)
        float[] vertices = {
               -0.4f,  -0.6f,  // Bas gauche
               -0.4f,   0.6f,  // Haut gauche
                0.4f,   0.6f,  // Haut droit
                0.4f,  -0.6f   // Bas droit
        };

        FloatBuffer vertexBuffer = org.lwjgl.BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        int[] indices = {
                0, 1, 3,  // Premier triangle
                3, 1, 2   // Deuxième triangle
        };

        IntBuffer indexBuffer = org.lwjgl.BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        float[] textureCoords = {
                0.0f, 1.0f,  // Bas gauche -> correspond au haut de l'image
                0.0f, 0.0f,  // Haut gauche -> correspond au bas de l'image
                1.0f, 0.0f,  // Haut droit -> correspond au bas de l'image
                1.0f, 1.0f   // Bas droit -> correspond au haut de l'image
        };

        FloatBuffer textureBuffer = org.lwjgl.BufferUtils.createFloatBuffer(textureCoords.length);
        textureBuffer.put(textureCoords).flip();

        glBindVertexArray(VAO);

        glBindBuffer(GL30C.GL_ARRAY_BUFFER, VBO);
        glBufferData(GL30C.GL_ARRAY_BUFFER, vertexBuffer, GL30C.GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL30C.GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL30C.GL_ARRAY_BUFFER, textureVBO);
        glBufferData(GL30C.GL_ARRAY_BUFFER, textureBuffer, GL30C.GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL30C.GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL30C.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL30C.GL_STATIC_DRAW);

        glBindVertexArray(0);

    }

    @Override
    public void render(Camera camera, float deltaTime) {
        try {
            PlayerState currentState = ThreadManager.playerState.get();
            if(currentState == null) {
                GameLogger.error("PlayerState est null dans render !", null);
                return;
            }


            AABB playerAABB = currentState.getAABB();
            transformationMatrix.identity().translation(
                    playerAABB.getMinX() + playerAABB.halfSize().x,
                    playerAABB.getMinY() + playerAABB.halfSize().y,
                    0.0f
            );

            AnimationState newAnimation = animationController.processTransiteAnimation(
                    currentState,
                    currentAnimation
            );


            if(newAnimation != currentAnimation) {
                currentTexture = animationTextures.get(newAnimation);
                currentAnimation = newAnimation;
            }

            Texture texture = currentTexture;

            // Vérification des textures
            if (animationTextures == null || animationTextures.isEmpty()) {
                GameLogger.error("Textures d'animation manquantes !", null);
                return;
            }

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

            shader.use();

            matrixBuffer.clear();
            transformationMatrix.get(matrixBuffer);
            shader.setUniformMat4f("transformationMatrix", matrixBuffer);

            matrixBuffer.clear();
            camera.getProjectionMatrix().get(matrixBuffer);
            shader.setUniformMat4f("projectionMatrix", matrixBuffer);

            matrixBuffer.clear();
            camera.getViewMatrix().get(matrixBuffer);
            shader.setUniformMat4f("viewMatrix", matrixBuffer);

            glBindVertexArray(VAO);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);
        } catch (Exception e) {
            GameLogger.error("Erreur dans le rendu du joueur", e);
        }
    }

    @Override
    public void cleanup() {
        shader.cleanup();
        for (Texture texture : animationTextures.values()) {
            texture.cleanUp();
        }
    }
}