package kamkeel.npcdbc.client.modern;

import kamkeel.npcdbc.client.shader.ShaderHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ModernGLHelper {

    public static ModernModel createVAO(float[] data, int[] indices) {
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        int ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.length);
        intBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);

        int stride = 8 * 4;
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, stride, 3 * 4);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, stride, 6 * 4);
        GL20.glEnableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        ModernModel model = new ModernModel(vao, vbo, ebo, indices.length);
        ModernModels.loadedModels.put(vao, model);
        return model;
    }


    public static void drawWorkingQuad() {
        float[] vertices = { //
            -0.5f, -0.5f, 0f, 1, 0, 0, //color
            0.5f, -0.5f, 0f, 1, 1, 0,        //color
            0.5f, 0.5f, 0f, 0, 1, 1,   //color
            -0.5f, 0.5f, 0f, 1, 1, 1};     //color
        int[] indices = {0, 1, 2, 0, 2, 3};

        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        int vertexVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        int elementVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementVBO);
        IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.length);
        intBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * 4, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * 4, 3 * 4);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


        GL30.glBindVertexArray(vao);
        ShaderHelper.useShader(ShaderHelper.modern, () -> {
            ShaderHelper.uniformMatrix4x4("modelView", ShaderHelper.getModelView());
            ShaderHelper.uniformMatrix4x4("projection", ShaderHelper.getProjection());
        });

        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);

        ShaderHelper.releaseShader();
        GL30.glBindVertexArray(0);
    }
}
