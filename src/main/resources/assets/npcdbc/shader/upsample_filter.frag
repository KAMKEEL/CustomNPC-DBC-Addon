#version 120

uniform sampler2D mainTexture;
uniform vec2 u_resolution;
varying vec2 texCoord;


vec4 TentFilter(vec2 uv, vec4 sampleScale){

    vec2 texelSize = vec2(1./(u_resolution.xy));
    vec4 d = texelSize.xyxy * vec4(1.0, 1.0, -1.0, 0.0) * sampleScale;

    vec4 pixel;

    pixel =  texture2D(mainTexture, uv - d.xy);
    pixel += texture2D(mainTexture, uv - d.wy) * 2.0;
    pixel += texture2D(mainTexture, uv - d.zy);

    pixel += texture2D(mainTexture, uv + d.zw) * 2.0;
    pixel += texture2D(mainTexture, uv) * 4.0;
    pixel += texture2D(mainTexture, uv + d.xw) * 2.0;

    pixel += texture2D(mainTexture, uv + d.zy);
    pixel += texture2D(mainTexture, uv + d.wy) * 2.0;
    pixel += texture2D(mainTexture, uv + d.xy);

    return pixel / 16.;
}

void main() {
    vec3 color = TentFilter(texCoord, vec4(1.)).rgb;

    gl_FragColor = vec4(color, 1.0);
}

