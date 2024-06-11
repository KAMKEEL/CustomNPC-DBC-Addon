#version 120

varying vec2 texCoord;

uniform sampler2D texture1;

void main() {
    vec4 color1 = texture2D(texture1, texCoord);
    gl_FragColor = color1;
}
