#version 120


uniform vec4 rgba;
uniform int time;
void main() {
    //gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
    gl_FragColor=vec4(rgba);
}
