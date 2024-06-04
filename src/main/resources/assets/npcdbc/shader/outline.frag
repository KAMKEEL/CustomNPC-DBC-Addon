#version 120
varying vec4 vertexPos;
varying vec4 modelView;
varying vec2 texCoord;


uniform float time;
uniform vec2 u_resolution;
uniform sampler2D noiseTexture;
uniform vec4 innerColor;
uniform vec4 outerColor;
uniform float noiseSize;
uniform float range;
uniform float threshold;
uniform float noiseSpeed;
uniform float throbSpeed;

void main() {
    vec2 uv = texCoord;
    //  uv.x += time* speed *0.3;
    uv.y -= time* noiseSpeed *0.3;
    vec4 noise = texture2D(noiseTexture, uv / noiseSize);
    
    float t = smoothstep(threshold-range, threshold+range, noise.g);
    vec4 mixedColor = mix(innerColor, outerColor, t);


    float alphaGlow =  max(noise.r * 2, 0.5) + sin(time*throbSpeed) * 0.2;
    gl_FragColor = vec4(mixedColor.xyz, alphaGlow);
}
