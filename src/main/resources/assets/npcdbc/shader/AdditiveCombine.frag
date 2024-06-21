#version 120

varying vec2 texCoord;

uniform sampler2D texture1;
uniform sampler2D texture2;

void main() {
    vec4 color1 = texture2D(texture1, texCoord);
    vec4 color2 = texture2D(texture2, texCoord);

    // if(color2.a > 0)
    //  gl_FragColor = color1 * color2 * 10.;
    // else{
    //color2 = color2 / (color2 + vec4(1.));
    float exposure = 1;
    color2.rgb = vec3(1.0) - exp(-color2.rgb * exposure);//tone mapping

    // color2.rgb = pow(color2.rgb, vec3(1./1.3));//gamma correction
    vec4 color;
    color = color1 + color2;
    gl_FragColor =color;

}
