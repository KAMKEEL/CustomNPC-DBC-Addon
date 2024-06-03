#version 120

varying vec2 texCoord;
varying vec3 vertPos;
varying vec4 clippingPos;


void main() {
    gl_Position = gl_ModelViewProjectionMatrix* gl_Vertex;
    vertPos = gl_Vertex.xyz;
    texCoord = vec2(gl_MultiTexCoord0);
    clippingPos = gl_Position;
}
