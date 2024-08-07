#version 120

varying vec3 vertPos;
varying vec2 texCoord;
varying vec3 clippingPos;

uniform vec4 color1;
uniform vec4 color2;
uniform vec4 color3;
uniform vec4 color4;

uniform float speed;
uniform float pitch;

uniform sampler2D mainTexture;
uniform sampler2D noiseTexture;
uniform sampler2D cross;


uniform vec3 center;
const float frameWidth = 1./4;
const float aura[] = float[](0, 0.5, 0.7, 0.97);

vec4 adjustColor(vec4 color){
    vec3 newColor = color.rrr;
    if (color.r >=aura[3]){
        newColor *= color1.rgb;
        newColor *= color1.a;

    } else if (color.r >=aura[2]){
        newColor *= color2.rgb;
        newColor *= color2.a;

    } else if (color.r > aura[1]){
        newColor *= color3.rgb;
        newColor *=color3.a;

    } else if (color.r > 0){
        newColor *= color4.rgb;
        newColor *= color4.a;
    }

    return vec4(newColor, 1.0);
}

void main() {

    vec4 currentFrame = texture2D(mainTexture, texCoord);
    currentFrame = adjustColor(currentFrame);

    vec4 nextFrame = adjustColor(texture2D(mainTexture, vec2(texCoord.x +frameWidth, texCoord.y)));
    vec4 color = mix(currentFrame, nextFrame, fract(speed));

    float absPitch = abs(pitch);

    if (absPitch > 60){
        float factor = 0.0;
        vec4 currentCross = adjustColor(texture2D(cross, texCoord));
        vec4 nextCross = adjustColor(texture2D(cross, vec2(texCoord.x +frameWidth, texCoord.y)));
        vec4 crossColor = mix(currentCross, nextCross, fract(speed));

        factor = (absPitch -60) / 30;
        factor = clamp(factor * 2, 0.0, 1.0);

        color = mix(color * max(0, (1- factor)), crossColor , factor);

    }

    gl_FragColor = vec4(color.rgb, 1);
}
