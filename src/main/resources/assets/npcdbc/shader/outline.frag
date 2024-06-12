#version 120
varying vec4 vertexPos;
varying vec4 modelView;
varying vec2 texCoord;
varying vec4 originalColor;


uniform float time;
uniform vec2 u_resolution;
uniform sampler2D mainTexture;
uniform sampler2D noiseTexture;
uniform vec4 innerColor;
uniform vec4 outerColor;
uniform float noiseSize;
uniform float range;
uniform float threshold;
uniform float noiseSpeed;
uniform float throbSpeed;

uniform float blurKernel[9];
uniform vec2 texRes;
uniform float blurIntensity;

vec3 gaussianBlur(vec2 uv){
    vec2 texelSize = vec2(1/(texRes.xy /1));
    const int kernelSize = 1;
    const float kernelSum = 16.;
    vec3 blurColor;

    for (int x = -kernelSize; x <= kernelSize; x++){
        for (int y= -kernelSize; y<=kernelSize;y++){
            vec2 texelOffset = uv + vec2(x, y) * texelSize;
            blurColor += texture2D(noiseTexture, texelOffset).rgb * blurKernel[(y+1)*3 + x+1];
        }
    }
    return blurColor / kernelSum;
}

void main() {
    vec2 uv = texCoord;
    //  uv.x += time* speed *0.3;
    uv.y -= time* noiseSpeed *0.3;
    uv = uv / noiseSize;
    vec3 color;
    vec3 noise =gaussianBlur(uv);


    float t = smoothstep(threshold-range, threshold+range, noise.g);
    color = mix(innerColor, outerColor, t).rgb;


    float alphaGlow =  max(noise.r * 2, 0.5) + sin(time*throbSpeed) * 0.2;

    // vec4 mainSample = texture2D(mainTexture,texCoord);
    // if(mainSample.a < 0.5)
    // discard;
    //  color = mix(color, mainSample.rgb * col.rgb,(sin(time * 0.5) + 1)*0.5);

    gl_FragColor = vec4(color.rgb, 1.);
}


