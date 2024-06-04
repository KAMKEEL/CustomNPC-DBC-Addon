#version 120

uniform vec2 u_resolution;
varying vec3 vertexPos;
uniform float time;
varying vec4 fragColor;
void main() {
    vec2 st = gl_FragCoord.xy / u_resolution;
    // gl_FragColor  = vec4(0, (st)* 3, (1-pow(length(vertexPos - vec3(0,0.75,0)),0.65))) ;
    //  gl_FragColor = vec4(fragColor.rgb, (1-pow(length(vertexPos - vec3(0, 0.75, 0)), 0.85)) + 0.01);
    // Calculate the RGB components cycling over time
    float posVariation = length(vertexPos) * 0.4;

    // Calculate the RGB components cycling over time and space
    float red = 0.5 + 0.5 * sin(time * 2.0 + posVariation - 2.0);
    float green = 0.75 + 0.25 * sin(time * 2.0 + posVariation + 2.0);
    float blue = 0.5 + 0.5 * sin(time * 2.0 + posVariation + 4.0);

  // gl_FragColor = vec4(0.3, st.x + (sin(time * 4) + 1)*0.5, st.y+(sin(time) + 5)*0.75, max(1- length(vertexPos - vec3(0, 0.75, 0)) *1.15, 0.3));
    
    vec3 color1 = vec3(0,1,1);
    vec3 color2 = vec3(1,1,1);
    vec3 new = mix(color1,color2,vertexPos.y +0.3);
    float alpha =  max(1- length(vertexPos - vec3(0, 0.75, 0)) *1.35, 0.2);
    float alphaGlow = 0.5 +1 * (sin(time *4)) * 0.25;
    gl_FragColor = vec4(new, alphaGlow);
}
