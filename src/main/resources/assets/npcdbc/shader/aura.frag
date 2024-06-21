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
uniform sampler2D noiseTexture;

uniform vec3 center;
const float frameWidth = 1./4;
const float aura[] = float[](0, 0.5, 0.7, 0.97);

void main() {

    vec4 currentFrame = texture2D(mainTexture, texCoord);
    if (currentFrame.r <= 0)
    discard;

    vec4 nextFrame = texture2D(mainTexture, vec2(texCoord.x +frameWidth, texCoord.y));
    vec4 frameColor = mix(currentFrame, nextFrame, fract(speed));


    vec3 color =  frameColor.rrr;
    if (frameColor.r >=aura[3]){
        color *= color1.rgb;
        color *= color1.a;

    } else if (frameColor.r >=aura[2]){
        color*= color2.rgb;
        color *=color2.a;

    } else if (frameColor.r > aura[1]){
        color *= color3.rgb;
        color *=color3.a;

    } else if (frameColor.r > 0){
        color *=color4.rgb;
        color *=color4.a;
    }


    gl_FragColor = vec4(color, 1);
}
