#version 400 core

in vec2 fragTextureCoord;
out vec4 fragColour;

uniform sampler2D textureSample;

void main() {
    vec4 textureColor = texture(textureSample, fragTextureCoord);

    // Éliminer le blanc ET le vert clair qui peuvent être des artefacts
    float whiteThreshold = 0.9;
    float greenThreshold = 0.8;

    if (textureColor.a < 0.1) discard;

    // Détection des couleurs très sombres (parfois des artefacts)
    bool isVeryDark = (textureColor.r < 0.1 &&
    textureColor.g < 0.1 &&
    textureColor.b < 0.1);

    fragColour = textureColor;
}