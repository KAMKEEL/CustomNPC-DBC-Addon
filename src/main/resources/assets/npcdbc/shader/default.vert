#version 400 core

in vec3 vertexPosition;
in vec3 colors;
in vec2 texCoords;

out vec3 col;
out vec3 fragPosition;



void main(void) {

    gl_Position = vec4(vertexPosition, 1.0);
    fragPosition = vertexPosition;// Pass vertex position to fragment shader
    col = colors;

}


//#version 120
//varying vec4 vertexPos;
//varying vec3 normal;
//varying vec2 texCoord;
//varying vec4 originalColor;
//
//
//
//void main() {
//    vertexPos = gl_Vertex;
//    normal = gl_Normal;
//    texCoord = vec2(gl_MultiTexCoord0);
//    originalColor = gl_Color;
//
//
//    gl_Position = gl_ModelViewProjectionMatrix* gl_Vertex;
//}
