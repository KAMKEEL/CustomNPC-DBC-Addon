#version 120

varying vec3 vertPos;
varying vec2 texCoord;
varying vec3 clippingPos;

varying vec4 glColor;

uniform vec2 resolution;

uniform vec4 color1;
uniform vec4 color2;
uniform vec4 color3;
uniform vec4 color4;

uniform float speed;

layout(binding=0) uniform sampler2D mainTexture;
layout(binding=1) uniform sampler2D auraSmall;
layout(binding=2) uniform sampler2D auraMediumSmall;
layout(binding=3) uniform sampler2D auraMediumLarge;
//uniform sampler2D cross;

const float frameWidth = 1./6;
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

    vec4 currentFrame = texture2D(mainTexture, texCoord) * glColor;
    vec4 small = texture2D(auraSmall, gl_FragCoord.xy / resolution);
    vec4 med1 = texture2D(auraMediumSmall, gl_FragCoord.xy / resolution);
    vec4 med2 = texture2D(auraMediumLarge, gl_FragCoord.xy / resolution);
    if (currentFrame.a < 0.0005){
        discard;
    }


    vec4 color = currentFrame * color1;
    vec4 tempColor;
    if(small.a >= 0.005){
        color = mix(vec4(color.rgb, min(color.a, 1.0)), small*color2, small.a);
    }
    if(med1.a >= 0.005){
        color = mix(vec4(color.rgb, min(color.a, 1.0)), med1*color3, med1.a);
    }
//    if(med2.a >= 0.005){
//        color = mix(color, med2*color4, med2.a*2.5);
//    }



    //gl_FragDepth = 0;
    gl_FragColor = color;
}
