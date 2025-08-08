package render;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glUniformMatrix4fv;

public class Shader {
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    private final Map<String, Integer> uniforms = new HashMap<>();

    public Shader(String shaderName) {
        try {
            String vertexPath = "src/main/resources/shaders/" + shaderName + ".vs.glsl";
            String fragmentPath = "src/main/resources/shaders/" + shaderName + ".fs.glsl";

            String vertexSource = new String(Files.readAllBytes(Paths.get(vertexPath)));
            String fragmentSource = new String(Files.readAllBytes(Paths.get(fragmentPath)));

            compile(vertexSource, fragmentSource);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des shaders: " + e.getMessage());
            loadDefaultShader();
        }

        switch (shaderName) {
            case "background" -> {
                createUniform("time");
                createUniform("resolution");
            }
            case "player" -> {
                createUniform("transformationMatrix");
                createUniform("viewMatrix");
                createUniform("projectionMatrix");
                createUniform("textureSample");
            }
            case "platforms" -> {
                createUniform("transformationMatrix");
                createUniform("viewMatrix");
                createUniform("projectionMatrix");
            }
            case "THE_END" -> {
                createUniform("transformationMatrix");
                createUniform("viewMatrix");
                createUniform("projectionMatrix");
                createUniform("textureSampler");
            }
            case "portal" -> {
                createUniform("transformationMatrix");
                createUniform("viewMatrix");
                createUniform("projectionMatrix");
                createUniform("time");
                createUniform("resolution");
            }
        }
    }

    public void compile(String vertexSource, String fragmentSource) {
        // Compiler le vertex shader
        vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexSource);
        glCompileShader(vertexShaderID);

        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Erreur vertex shader: " + glGetShaderInfoLog(vertexShaderID));
            return;
        }

        // Compiler le fragment shader
        fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentSource);
        glCompileShader(fragmentShaderID);

        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Erreur fragment shader: " + glGetShaderInfoLog(fragmentShaderID));
            return;
        }

        // Créer le programme
        programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        glLinkProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Erreur linking: " + glGetProgramInfoLog(programID));
            return;
        }

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
            System.err.println("Erreur validation: " + glGetProgramInfoLog(programID));
        }
    }

    public void createUniform(String name) {
        int location = glGetUniformLocation(programID, name);
        if (location < 0) {
            System.err.println("Uniform not found: " + name);
        }
        uniforms.put(name, location);
    }

    public void setUniform1f(String name, float value) {
        Integer location = uniforms.get(name);
        if (location == null) {
            System.err.println("Uniform not found: " + name);
            return;
        }
        glUniform1f(location, value);
    }

    public void setUniform2f(String name, float x, float y) {
        Integer location = uniforms.get(name);
        if (location == null) {
            System.err.println("Uniform not found: " + name);
            return;
        }
        glUniform2f(location, x, y);
    }

    public void setUniformMat4f(String name, FloatBuffer matrix) {
        Integer location = uniforms.get(name);
        if (location == null) {
            System.err.println("Uniform not found: " + name);
            return;
        }
        glUniformMatrix4fv(location, false, matrix);
    }

    private void loadDefaultShader() {
        // Crée un shader par défaut en hardcoded
        String defaultVertex = """
                #version 330 core
                in vec3 position;
                void main() { gl_Position = vec4(position, 1.0); }""";

        String defaultFragment = """
                #version 330 core
                out vec4 fragColor;
                void main() { fragColor = vec4(1.0, 0.0, 1.0, 1.0); }"""; // Rose shocking

        compile(defaultVertex, defaultFragment);
    }

    public void use() {
        glUseProgram(programID);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void cleanup() {
        stop();
        uniforms.clear();
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programID);
    }
}