package kamkeel.npcdbc.client.modern;

import kamkeel.npcdbc.client.shader.IShaderUniform;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class ModernModel {
    public int VAO, VBO, EBO, indexCount;

    public ModernModel(int VAO, int VBO, int EBO, int indexCount) {
        this.VAO = VAO;
        this.VBO = VBO;
        this.EBO = EBO;
        this.indexCount = indexCount;
    }

    public void bind() {
        GL30.glBindVertexArray(VAO);
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void render(int shaderProgram, IShaderUniform uniforms) {
        try {
            bind();           // System.out.println("g0" + GL11.glGetError());

            ShaderHelper.useShader(shaderProgram, uniforms);
            //  System.out.println("g" + GL11.glGetError());
            ShaderHelper.uniformMatrix4x4("modelView", ShaderHelper.getModelView());
            // System.out.println("g1" + GL11.glGetError());

            ShaderHelper.uniformMatrix4x4("projection", ShaderHelper.getProjection());
            // System.out.println("g2" + GL11.glGetError());

            GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);          //  System.out.println("g3" + GL11.glGetError());

            ShaderHelper.releaseShader();
        unbind();
        } catch (Exception e) {
            e.printStackTrace();
            ModernGLHelper.drawWorkingQuad();
        }
    }
    public void destroy() {
        GL15.glDeleteBuffers(EBO);
        GL15.glDeleteBuffers(VBO);
        GL30.glDeleteVertexArrays(VAO);
    }
}
