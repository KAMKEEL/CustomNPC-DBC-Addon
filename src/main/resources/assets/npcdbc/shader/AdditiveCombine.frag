#version 120

varying vec2 texCoord;

uniform sampler2D sceneTexture;
uniform sampler2D bloomTexture;
uniform float exposure;

void main() {
    vec4 sceneColor = texture2D(sceneTexture, texCoord);
    vec4 bloomColor = texture2D(bloomTexture, texCoord);

    // if(color2.a > 0)
    //  gl_FragColor = color1 * color2 * 10.;
    // else{
    //color2 = color2 / (color2 + vec4(1.));

    bloomColor.rgb = vec3(1.0) - exp(-bloomColor.rgb * exposure);//tone mapping

    // color2.rgb = pow(color2.rgb, vec3(1./1.3));//gamma correction
    vec4 color;
    color = sceneColor + bloomColor;
    gl_FragColor =color;

}
