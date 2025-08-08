package render;

import engine.GameLogger;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class THE_END {

    private int VAO;
    private final Texture texture;

    private final FloatBuffer matrixBuffer = org.lwjgl.BufferUtils.createFloatBuffer(16);
    private final Matrix4f transformationMatrix = new Matrix4f();
    private final Shader shader;

    public THE_END() {
        shader = new Shader("THE_END");
        texture = new Texture("THE_END");
        initialize();
    }

    public void initialize() {
        VAO = glGenVertexArrays();
        int VBO = glGenBuffers();
        int EBO = glGenBuffers();
        int textureVBO = glGenBuffers();

        float[] vertices = {
                -1.0f, -1.0f,  // Bas gauche
                1.0f, -1.0f,  // Bas droit
                1.0f,  1.0f,  // Haut droit
                -1.0f,  1.0f   // Haut gauche
        };

        FloatBuffer vertexBuffer = org.lwjgl.BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        int[] indices = {
                0, 1, 2,  // Premier triangle
                2, 3, 0   // Deuxième triangle
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

    public void render() {
        try {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

            shader.use();

            matrixBuffer.clear();
            transformationMatrix.get(matrixBuffer);
            shader.setUniformMat4f("transformationMatrix", matrixBuffer);

            matrixBuffer.clear();
            shader.setUniformMat4f("projectionMatrix", matrixBuffer);

            matrixBuffer.clear();
            shader.setUniformMat4f("viewMatrix", matrixBuffer);

            glBindVertexArray(VAO);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);
        } catch (Exception e) {
            GameLogger.error("Erreur dans le rendu du joueur", e);
        }
    }
}