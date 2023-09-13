#include <GLES/egl.h>
#include <GLES/gl.h>
#include <GLES/glext.h>
#include <jni.h>

struct CVertex {
    float x, y, z;
    float r, g, b, a;
    float u, v;
};

extern "C"
JNIEXPORT jint JNICALL
Java_com_monobogdan_allzombiesarebastards_z2d_z2dgl_allocTexture(JNIEnv *env, jclass clazz, jint w,
                                                                 jint h, jint size, jboolean rgba,
                                                                 jobject pixels) {
    GLuint tex = 0;
    glGenTextures(1, &tex);

    glBindTexture(GL_TEXTURE_2D, tex);
    void* ptr = env->GetDirectBufferAddress(pixels);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, rgba ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, ptr);

    return tex;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_monobogdan_allzombiesarebastards_z2d_z2dgl_init(JNIEnv *env, jclass clazz) {

}
extern "C"
JNIEXPORT void JNICALL
Java_com_monobogdan_allzombiesarebastards_z2d_z2dgl_destroyTexture(JNIEnv *env, jclass clazz,
                                                                   jint ptr) {
    glDeleteTextures(1, (GLuint*)&ptr);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_monobogdan_allzombiesarebastards_z2d_z2dgl_resizeViewport(JNIEnv *env, jclass clazz,
                                                                   jint x, jint y, jint w, jint h) {
    glViewport(x, y, w, h);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrthof(0, (float)w, (float)h, 0, 0, 1);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_monobogdan_allzombiesarebastards_z2d_z2dgl_drawSprite(JNIEnv *env, jclass clazz, jint tex,
                                                               jfloat x, jfloat y, jfloat w,
                                                               jfloat h, jfloat rot, jbyte r,
                                                               jbyte g, jbyte b, jbyte a) {
    CVertex verts[6];
    float _r = (float)r / 255.0f;
    float _g = (float)g / 255.0f;
    float _b = (float)b / 255.0f;
    float _a = (float)a / 255.0f;

    verts[0] = { 0, 0, _r, _g, _b, _a, 0, 0};
    verts[1] = { 1, 0, _r, _g, _b, _a, 1, 0};
    verts[2] = { 1, 1, _r, _g, _b, _a, 1, 1};
    verts[3] = { 0, 0, _r, _g, _b, _a, 0, 0};
    verts[4] = { 0, 1, _r, _g, _b, _a, 0, 1};
    verts[5] = { 1, 1, _r, _g, _b, _a, 1, 1};


}