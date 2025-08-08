#version 330 core

in vec2 TexCoord;
out vec4 FragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 textureColor = texture(textureSampler, TexCoord);

    // Effet de fade ou de glow subtil pour la fin
    float alpha = textureColor.a;

    // Vous pouvez ajouter des effets ici si nécessaire
    FragColor = vec4(textureColor.rgb, alpha);
}