#version 330 core

layout(location = 0) in vec2 position;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec2 fragCoord;
out vec2 worldPos;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(position, 0.0, 1.0);
    worldPos = worldPosition.xy;

    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    // Coordonnées normalisées pour les effets
    fragCoord = position * 0.5 + 0.5;
}