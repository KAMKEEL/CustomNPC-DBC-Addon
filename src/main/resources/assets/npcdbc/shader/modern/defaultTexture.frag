#version 330

in vec3 col;
in vec3 fragPosition;
in mat4 modelView2;
in mat4 proj;

out vec4 outColor;
uniform sampler2D textureSampler;



void main(void){
   outColor =  vec4(0.1);
    // outColor = vec4(1.,1.,0,1.);// texture(textureSampler, passTextCoords);
}

