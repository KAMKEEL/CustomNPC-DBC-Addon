#version 120

varying vec3 vertPos;
varying vec2 texCoord;
varying vec3 clippingPos;

uniform vec4 color1;
uniform vec4 color2;
uniform vec4 color3;
uniform vec4 color4;

uniform float speed;

uniform sampler2D mainTexture;
//uniform sampler2D cross;

const float frameWidth = 1./4;
const float aura[] = float[](0, 0.5, 0.7, 0.97);

vec4 adjustColor(vec4 color){
    vec4 newColor = vec4(color.rgb, 1.0);
    if (color.a >= aura[3]){
        newColor *= color1;

    } else if (color.a >= aura[2]){
        newColor *= color2;

    } else if (color.a > aura[1]){
        newColor *= color3;

    } else if (color.a > 0){
        newColor *= color4;
    }

    return newColor;
}

void main() {

    vec4 currentFrame = texture2D(mainTexture, texCoord);

    if (currentFrame.a < 0.1){
        discard;
    }

    vec4 nextFrame = texture2D(mainTexture, vec2(texCoord.x +frameWidth, texCoord.y));

    if (nextFrame.a < 0.1){
        nextFrame = currentFrame;
    }

    vec4 color = mix(adjustColor(currentFrame), adjustColor(nextFrame), fract(speed));

    //gl_FragDepth = 0;
    gl_FragColor = color;
}
