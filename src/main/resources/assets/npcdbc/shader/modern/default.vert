#version 330

in vec3 vertexPosition;
in vec3 colors;
in vec2 texCoords;

out mat4 modelView2;
out mat4 proj;

//out vec3 col;
//out vec3 fragPosition;

uniform mat4 modelView;
uniform mat4 projection;


void main(void) {

    gl_Position = modelView* projection* vec4(vertexPosition, 1.0);
  //  fragPosition  vertexPosition;// Pass vertex position to fragment shader
   // col = colors;
    modelView2 = modelView;
    proj = projection;
}
