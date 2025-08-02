package render;

import engine.AABB;
import engine.Renderable;
import engine.GameLogger;
import entity.Camera;
import laucher.Main;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class RenderMap implements Renderable {

    private int VAO;
    private final Shader shader;

    private final FloatBuffer matrixBuffer = org.lwjgl.BufferUtils.createFloatBuffer(16);
    private final Matrix4f transformationMatrix = new Matrix4f();

    public RenderMap() {
        shader = new Shader("platforms");
    }

    @Override
    public void initialize() {

        VAO = glGenVertexArrays();
        int VBO = glGenBuffers();
        int EBO = glGenBuffers();
        // Quad unitaire centré en (0,0) avec taille 1x1
        float[] vertices = {
                -0.5f, -0.5f,  // Bas gauche
                -0.5f,  0.5f,  // Haut gauche
                0.5f,  0.5f,  // Haut droit
                0.5f, -0.5f   // Bas droit
        };

        FloatBuffer vertexBuffer = org.lwjgl.BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        IntBuffer indexBuffer = org.lwjgl.BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        GL30C.glBindVertexArray(VAO);

        glBindBuffer(GL30C.GL_ARRAY_BUFFER, VBO);
        glBufferData(GL30C.GL_ARRAY_BUFFER, vertexBuffer, GL30C.GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL30C.GL_FLOAT, false, 0, 0);
        GL20C.glEnableVertexAttribArray(0);

        glBindBuffer(GL30C.GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL30C.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL30C.GL_STATIC_DRAW);

        GL30C.glBindVertexArray(0);
    }

    @Override
    public void render(Camera camera, float deltaTime) {
        try {
            List<AABB> allPlatforms = Main.getEngine().getLevelNearPlayer();

            shader.use();

            // Matrices communes
            matrixBuffer.clear();
            camera.getProjectionMatrix().get(matrixBuffer);
            shader.setUniformMat4f("projectionMatrix", matrixBuffer);

            matrixBuffer.clear();
            camera.getViewMatrix().get(matrixBuffer);
            shader.setUniformMat4f("viewMatrix", matrixBuffer);

            glBindVertexArray(VAO);

            for (AABB platform : allPlatforms) {
                // Transformation: Position + Scale
                transformationMatrix.identity()
                        .translation(platform.position().x, platform.position().y, 0.0f)
                        .scale(platform.halfSize().x * 2.0f, platform.halfSize().y * 2.0f, 1.0f);

                matrixBuffer.clear();
                transformationMatrix.get(matrixBuffer);
                shader.setUniformMat4f("transformationMatrix", matrixBuffer);

                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            }

            glBindVertexArray(0);
        } catch (Exception e) {
            GameLogger.error("Erreur dans le rendu du joueur", e);
            // Ne pas faire crash le jeu, juste ignorer ce frame
        }
    }

    @Override
    public void cleanup() {

    }
}