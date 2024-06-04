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

uniform sampler2D noiseTexture;
uniform float time;


void main() {
    vec4 noiseColor = texture2D(noiseTexture, texCoord*2);
    float displacement = fract(noiseColor.r + time) / 10;
    vec3 newPosition = gl_Vertex.xyz + gl_Normal.xyz *displacement;
    gl_Position = gl_ModelViewProjectionMatrix* gl_Vertex ;//vec4(newPosition, 1.0);
    vertPos = gl_Vertex.xyz;

    texCoord = vec2(gl_MultiTexCoord0);
    clippingPos = gl_Position;

    fragNormal = gl_Normal.xyz;// Pass vertex normal


    // Transform vertex position to clip space
    //gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
