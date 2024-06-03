#version 120

varying vec2 texCoord;
varying vec3 vertPos;
varying vec4 clippingPos;
varying vec3 normals;
varying vec4 colors;

// Output varying variables
varying vec3 fragPos;// Fragment position
varying vec3 fragNormal;// Fragment normal
varying vec2 fragTexCoord;// Fragment texture coordinates

void main() {
    gl_Position = gl_ModelViewProjectionMatrix* gl_Vertex;
    vertPos = gl_Vertex.xyz;

    texCoord = vec2(gl_MultiTexCoord0);
    clippingPos = gl_Position;

    fragNormal = gl_Normal.xyz;// Pass vertex normal


    // Transform vertex position to clip space
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
