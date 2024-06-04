#version 120

uniform sampler2D noiseTexture;
uniform float time;
varying vec2 texCoord;
varying vec3 vertexPos;
varying vec4 fragColor; // Color passed to the fragment shader

vec3 hsv2rgb(float h, float s, float v) {
    vec3 rgb = vec3(1.0, 1.0, 1.0);
    if (s == 0.0) {
        rgb = vec3(v); // grey
    } else {
        h = mod(h, 1.0) * 6.0; // range [0, 6)
        int i = int(h);
        float f = h - float(i);
        float p = v * (1.0 - s);
        float q = v * (1.0 - s * f);
        float t = v * (1.0 - s * (1.0 - f));
        if (i == 0) rgb = vec3(v, t, p);
        else if (i == 1) rgb = vec3(q, v, p);
        else if (i == 2) rgb = vec3(p, v, t);
        else if (i == 3) rgb = vec3(p, q, v);
        else if (i == 4) rgb = vec3(t, p, v);
        else if (i == 5) rgb = vec3(v, p, q);
    }
    return rgb;
}

void main() {
    texCoord = vec2(gl_MultiTexCoord0);

    vec4 noiseColor = 5 * texture2D(noiseTexture, texCoord);
    float displacement = (noiseColor.r + sin(time * 4)) * 0.01358135 - 0.035;

    vec3 newPosition = gl_Vertex.xyz + gl_Normal.xyz *displacement;
 //   newPosition.y+=sin(newPosition.x * 50) / 8;
    gl_Position = gl_ModelViewProjectionMatrix* vec4(newPosition, 1.0);
    vertexPos = gl_Vertex.xyz;


    // Cycle through the hue over time
    float hue = mod(time * 1.0, 1.0); // Adjust speed with the multiplier

    // Full saturation and value for bright colors
    float saturation = 1.0;
    float value = 1.0;

    // Convert HSV to RGB
    vec3 rgb = hsv2rgb(hue, saturation, value);

    // Pass the color to the fragment shader
    fragColor = vec4(rgb, 1.0);


}
