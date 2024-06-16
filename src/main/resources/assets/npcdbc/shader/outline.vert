#version 120
varying vec4 vertexPos;
varying vec2 texCoord;
varying vec4 modelView;
uniform float time;
uniform sampler2D noiseTexture;
uniform float throbSpeed;

varying vec4 originalColor;



void main() {
    vec4 noiseColor = 5 * texture2D(noiseTexture, texCoord);
    float displacement =  sin(time * throbSpeed*2)  * 0.055;
    float size = 1 + displacement;

    mat4 scalingMatrix = mat4(
    size, 0.0, 0.0, 0.0,
    0.0, 1., 0.0, 0.0,
    0.0, 0.0, size, 0.0,
    0.0, 0.0, 0.0, 1.0
    );

    //vec3 newPosition = gl_Vertex.xyz + gl_Normal * displacement;
    gl_Position = gl_ModelViewProjectionMatrix* scalingMatrix * gl_Vertex;

    vertexPos = gl_Vertex;
    texCoord = vec2(gl_MultiTexCoord0);
    modelView = gl_ModelViewMatrix * vertexPos;
    originalColor = gl_Color;
}
