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



void main() {
    vec2 uv = texCoord;
    //  uv.x += time* speed *0.3;
    uv.y -= time* noiseSpeed *0.3;
    uv = uv / noiseSize;
    vec3 color;
    vec3 noise;
    noise =texture2D(noiseTexture, uv).rgb;


    float t = smoothstep(threshold-range, threshold+range, noise.g);
    color = mix(innerColor, outerColor, t).rgb;


    float alphaGlow =  max(noise.r * 2, 0.5) + sin(time*throbSpeed) * 0.2;

    // vec4 mainSample = texture2D(mainTexture,texCoord);
    // if(mainSample.a < 0.5)
    // discard;
    //  color = mix(color, mainSample.rgb * col.rgb,(sin(time * 0.5) + 1)*0.5);

    gl_FragColor = vec4(color.rgb, 1.);
}


