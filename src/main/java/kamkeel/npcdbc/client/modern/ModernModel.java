package kamkeel.npcdbc.client.modern;

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

    public void destroy() {
        GL15.glDeleteBuffers(EBO);
        GL15.glDeleteBuffers(VBO);
        GL30.glDeleteVertexArrays(VAO);
    }
}
