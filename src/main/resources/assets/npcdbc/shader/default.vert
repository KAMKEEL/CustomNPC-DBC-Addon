#version 120
varying vec4 vertexPos;
varying vec3 normal;
varying vec2 texCoord;
varying vec4 originalColor;



void main() {
    vertexPos = gl_Vertex;
    normal = gl_Normal;
    texCoord = vec2(gl_MultiTexCoord0);
    originalColor = gl_Color;


    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
