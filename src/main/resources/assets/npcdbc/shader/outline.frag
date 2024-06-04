#version 120
varying vec4 vertexPos;
varying vec4 modelView;
varying vec2 texCoord;

uniform float time;
uniform vec2 u_resolution;
uniform sampler2D noiseTexture;

void main() {
    vec2 uv = texCoord;
    uv.x += time*0.3;
    uv.y -= time*0.3;

    vec4 noise = texture2D(noiseTexture, uv *0.5);


    float range = .25;
    float threshold = .55;
    float t = smoothstep(threshold-range, threshold+range, noise.g);
    vec4 blue = vec4(0, 1, 1, 1);
    vec4 mixedColor = mix(blue, vec4(1, 1, 1, 1), t);



    float alphaGlow =  max(noise.r * 2, 0.5) + sin(time*2) * 0.2;
    gl_FragColor = vec4(mixedColor.xyz, alphaGlow);
}
