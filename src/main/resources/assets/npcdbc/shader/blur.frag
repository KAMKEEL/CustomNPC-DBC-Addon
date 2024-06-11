#version 120
varying vec2 texCoord;

uniform vec2 u_resolution;
uniform sampler2D mainTexture;
uniform int horizontal;
uniform float blurIntensity;
const int kernelSize = 5;
const float weights[11] = float[](0.0093, 0.028002, 0.065984, 0.121703, 0.175713, 0.198596, 0.175713, 0.121703, 0.065984, 0.028002, 0.0093);


void main() {
    vec4 blurColor = vec4(0);
    vec2 resolution = u_resolution / blurIntensity;
    vec2 texelSize = horizontal == 1? vec2(1./resolution.x, 0) : vec2(0, 1./resolution.y);

    for (int x = -kernelSize; x<= kernelSize; x++){
        blurColor += texture2D(mainTexture, texCoord + float(x) * texelSize) * weights[x+kernelSize];
    }

    gl_FragColor =blurColor;
}
