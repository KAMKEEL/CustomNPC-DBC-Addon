#version 120
varying vec4 vertexPos;
varying vec2 texCoord;
varying vec4 modelView;
uniform float time;
uniform sampler2D noiseTexture;





void main() {
    vec4 noiseColor = 5 * texture2D(noiseTexture, texCoord);
    float displacement = sin(time * 2)  * 0.055;
    float size = 1 + displacement;

    mat4 scalingMatrix = mat4(
    size, 0.0, 0.0, 0.0,
    0.0, size, 0.0, 0.0,
    0.0, 0.0, size, 0.0,
    0.0, 0.0, 0.0, 1.0
    );

    gl_Position = gl_ModelViewProjectionMatrix* scalingMatrix*gl_Vertex;//vec4(newPosition, 1.0);

    vertexPos = gl_Vertex;
    texCoord = vec2(gl_MultiTexCoord0);
    modelView = gl_ModelViewMatrix * vertexPos;
}
