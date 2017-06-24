#version 330

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uWorld;
uniform vec3 uCameraPosition;

in vec3 aPosition;
in vec3 aNormal;



out vec3 vNormal;
out vec3 vViewPath;
out vec3 aFragPos;

void main() {
    vec4 worldPos = uWorld * vec4(aPosition.x, aPosition.y, aPosition.z, 1.0);
    gl_Position =  uProjection * uView * worldPos * vec4(1.0, 1.0, 1.0, 1.0);
    vNormal = (uWorld * vec4(aNormal, 0.0)).xyz;
    vViewPath = uCameraPosition - worldPos.xyz;
    aFragPos = worldPos.xyz;
    //    gl_Position =  uProjection * uView * uWorld * vec4(aPosition, 1.0);
}