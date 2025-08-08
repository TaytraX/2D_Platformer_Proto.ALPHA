#version 330 core

in vec2 fragCoord;
in vec2 worldPos;

uniform float time;
uniform vec2 resolution;

out vec4 fragColor;

// Fonction de bruit simple
float noise(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

// Bruit fractal pour plus de complexité
float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;

    for(int i = 0; i < 4; i++) {
        value += amplitude * noise(p);
        p *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

void main() {
    vec2 uv = fragCoord;

    // Animation temporelle
    float t = time * 0.5;

    // Coordonnées déformées pour l'effet de distorsion
    vec2 distortedUV = uv + vec2(
    sin(uv.y * 10.0 + t) * 0.02,
    cos(uv.x * 8.0 + t * 0.7) * 0.03
    );

    // Plusieurs couches de bruit animé
    float noise1 = fbm(distortedUV * 4.0 + vec2(t * 0.3, t * 0.2));
    float noise2 = fbm(distortedUV * 8.0 + vec2(-t * 0.2, t * 0.4));
    float noise3 = fbm(distortedUV * 16.0 + vec2(t * 0.5, -t * 0.3));

    // Combinaison des bruits
    float finalNoise = noise1 * 0.5 + noise2 * 0.3 + noise3 * 0.2;

    // Effet de flamme verticale
    float flame = pow(1.0 - abs(uv.x - 0.5) * 2.0, 2.0);
    flame *= smoothstep(0.0, 0.3, uv.y) * smoothstep(1.0, 0.7, uv.y);

    // Couleurs du portail Nether
    vec3 color1 = vec3(0.4, 0.1, 0.8);  // Violet foncé
    vec3 color2 = vec3(0.8, 0.3, 1.0);  // Violet clair
    vec3 color3 = vec3(0.2, 0.0, 0.4);  // Violet très foncé

    // Mélange des couleurs basé sur le bruit
    vec3 color = mix(color3, color1, finalNoise);
    color = mix(color, color2, noise2 * flame);

    // Effet de particules scintillantes
    float sparkle = step(0.95, noise(worldPos * 50.0 + t * 10.0));
    color += sparkle * vec3(1.0, 0.8, 1.0) * 0.5;

    // Effet de bordure plus intense
    float border = 1.0 - smoothstep(0.8, 1.0, length(uv - 0.5) * 2.0);
    color *= border;

    // Alpha pour la transparence
    float alpha = (finalNoise + flame) * border * 0.8;

    fragColor = vec4(color, alpha);
}