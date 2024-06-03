#version 120

varying vec3 vertPos;
varying vec2 texCoord;

uniform vec4 rgba;
uniform float time;
uniform sampler2D bgl_RenderedTexture;

void main() {
    vec4 texel = texture2D(bgl_RenderedTexture, texCoord);

    if (texel.a < 0.55)
    discard;

    gl_FragColor = vec4(0, sin(time) * 3, sin(time) * 3, 0.5);
    // gl_FragColor = texel;

    float oscillating_value = (sin(time * 10) + 1.0) * 0.5;

    // Define two colors to alternate between
    vec3 color1 = vec3(1 - vertPos.xyz);// Red
    vec3 color2 = vec3(vertPos.xyz);// Blue

    // Mix the two colors based on the oscillating value
    vec3 final_color = mix(color1, color2, oscillating_value);

    gl_FragColor = vec4(final_color, 1.0);
}
