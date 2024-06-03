#version 120

varying vec3 vertPos;
varying vec2 texCoord;
varying vec3 clippingPos;

uniform vec4 rgba;
uniform float time;
uniform sampler2D mainTexture;
uniform sampler2D noiseTexture;

uniform vec3 center;

void main() {
    vec4 mainColor = texture2D(mainTexture, texCoord);
    vec4 noiseColor = texture2D(noiseTexture, texCoord*2);


    if (mainColor.a < 0.25)
    discard;

    gl_FragColor = vec4(0, sin(time) * 3, sin(time) * 3, 0.5);
    // gl_FragColor = texel;

    float oscillating_value = (sin(time) + 1.0) * 0.5;

    // Define two colors to alternate between
    vec3 color1 = vec3(1 - vertPos.xyz);// Red
    vec3 color2 = vec3(noiseColor.xyz);// Blue

    // Mix the two colors based on the oscillating value
    // vec3 final_color = mix(color1, color2, oscillating_value);
    // float distance = 1 - length(vertPos /1.25);
    // gl_FragColor = vec4(final_color.xyz, 0.5);
    // gl_FragColor = vec4(vec3(0, 1.0, 1.0)*distance, 1);
    //  gl_FragColor = vec4(final_color.rgb, noiseColor.a);

    float distance = length(vertPos);

    // Define glow color and intensity
    vec3 glowColor = vec3(0.5, 1, 1);// Blue glow color
    float glowIntensity = 0.5;

    // Calculate distortion based on time
    float distortion = sin(time * 4) * 0.05;// Adjust the amplitude and frequency as needed

    // Add some noise to the distance calculation based on time
    float noisyDistance = distance + distortion;

    // Calculate glow effect based on noisy distance
    vec3 glow = glowColor * glowIntensity / (noisyDistance * noisyDistance);

    // Output final fragment color with glow effect
   // gl_FragColor = vec4(glow, noiseColor.a);
    gl_FragColor = mix(mainColor,noiseColor,noiseColor.b);
}
