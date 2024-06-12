#version 120

varying vec2 texCoord;

uniform sampler2D textureMain;
uniform sampler2D textureBlur;
uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform sampler2D texture4;
uniform sampler2D texture5;
uniform sampler2D texture6;
uniform sampler2D texture7;



void main() {
    vec4 color1 = texture2D(texture1, texCoord);
    vec4 color2 = texture2D(texture2, texCoord);


    vec4 finalColor = vec4(0.0);

    // Sample from each texture and combine colors
    finalColor += texture2D(textureMain, texCoord);
    //  finalColor += texture2D(textureMain, texCoord);
    finalColor += texture2D(textureBlur, texCoord);
    finalColor += texture2D(texture0, texCoord);
    finalColor += texture2D(texture1, texCoord);
    finalColor += texture2D(texture2, texCoord);
    finalColor += texture2D(texture3, texCoord);
    finalColor += texture2D(texture4, texCoord);
    finalColor += texture2D(texture5, texCoord);
    finalColor += texture2D(texture6, texCoord);
    finalColor += texture2D(texture7, texCoord);

    gl_FragColor = finalColor /3.0;

}
