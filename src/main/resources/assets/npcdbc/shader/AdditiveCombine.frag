#version 120

varying vec2 texCoord;

uniform sampler2D texture1;
uniform sampler2D texture2;

void main() {
    vec4 color1 = texture2D(texture1, texCoord);
    vec4 color2 = texture2D(texture2, texCoord);

    // if(color2.a > 0)
    //  gl_FragColor = color1 * color2 * 10.;
    // else{
    gl_FragColor = color1 + color2 * 2.;

    // }

}
