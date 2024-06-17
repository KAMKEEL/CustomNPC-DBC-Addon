#version 400 core

in vec3 col;
in vec3 fragPosition;

out vec4 outColor;
uniform sampler2D textureSampler;



void main(void){
    outColor = vec4(col.rgb,1.);// texture(textureSampler, passTextCoords);
}


//#version 120
//
//varying vec2 texCoord;
//
//uniform sampler2D texture1;
//
//void main() {
//    vec4 color1 = texture2D(texture1, texCoord);
//    gl_FragColor = color1;
//}
