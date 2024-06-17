package kamkeel.npcdbc.client.modern;

import kamkeel.npcdbc.client.shader.ShaderHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModernGLHelper {
    private static List<Integer> vaos = new ArrayList<Integer>();
    private static List<Integer> vbos = new ArrayList<Integer>();
    public static int quad;

    public static void init() {
        //  float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,};
        // float[] normals = {0.0f, 0.0f, 1.0f,};
        // float[] texCoords = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        //  int[] indices = {0, 1, 2, 0, 2, 3};

        float[] vertices = {
                // Position          // Normal            // Tex Coords  // Color
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,    // Bottom-left
                -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,    // Top-left
                0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,     // Top-right
                0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,     // Bottom-right
        };

        int[] indices = {0, 1, 2, 2, 3, 0};
        //  quad = createVAO(vertices, indices);
    }

    public static ModernModel createVAO(float[] data, int[] indices) {
        int vaoID = GL30.glGenVertexArrays(); //each VAO has unique id
        GL30.glBindVertexArray(vaoID); //change current active VAO to this vaoID
        vaos.add(vaoID);

        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        int stride = 8 * 4;
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, stride, 3 * 4);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, stride, 6 * 4);
        GL20.glEnableVertexAttribArray(2);

        // GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, stride, 8 * 4);
        // GL20.glEnableVertexAttribArray(3);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


        int elementVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementVBO);
        IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.length);
        intBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        return new ModernModel(vaoID, vboID, elementVBO, indices.length);
    }

    public static void render() {
        ShaderHelper.useShader(ShaderHelper.modern);
       //  GL30.glBindVertexArray(quad);

        String s = GL11.glGetString(GL11.GL_VERSION);

        // Enable vertex attributes
        GL20.glEnableVertexAttribArray(0); // Position attribute
        GL20.glEnableVertexAttribArray(1); // Normal attribute
      //  GL20.glEnableVertexAttribArray(2); // Texture coordinate attribute
        //     GL20.glEnableVertexAttribArray(3); // Texture coordinate attribute

        //GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, quad.EBO);

        // Draw elements (assuming using GL_TRIANGLES and an EBO)
        //  GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);

        // Disable vertex attributes after rendering
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
       // GL20.glDisableVertexAttribArray(2);
        //  GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
        ShaderHelper.releaseShader();
        // Unbind VAO
        // GL30.glBindVertexArray(0);

        if (quad == 0) {

            quad = GL30.glGenVertexArrays();
            int vertexVBO = GL15.glGenBuffers();
            GL30.glBindVertexArray(quad);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);


            // float[] vertices = new float[]{-0.75f, -0.75f, 0f, 0.75f, -0.75f, 0f, 0f, 0.75f, 0f};

            float[] vertices = { //
                    -0.5f, -0.5f, 0f, 1, 0, 0, //color
                    0.5f, -0.5f, 0f, 0, 1, 0,        //color
                    0.5f, 0.5f, 0f, 0, 0, 1,   //color
                    -0.5f, 0.5f, 0f, 1, 1, 1};     //color
            int[] indices = {0, 1, 2, 0, 2, 3};

            FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
            buffer.put(vertices).flip();

            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * 4, 0);
            GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * 4, 3 * 4);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            int elementVBO = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementVBO);
            IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.length);
            intBuffer.put(indices).flip();
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);
            // GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);


            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            ShaderHelper.useShader(ShaderHelper.modern);
              GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
            GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
            ShaderHelper.releaseShader();
            GL20.glDisableVertexAttribArray(0);

            GL30.glBindVertexArray(0);
           // GL30.glDeleteVertexArrays(quad);
           // GL15.glDeleteBuffers(vertexVBO);
        }
    }

    private static void createAttributeVBO(float[] data, int attId, int coordsize) {
        int vboID = GL15.glGenBuffers(); //make new buffer and bind if
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        vbos.add(vboID);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip(); //prep
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); //store data in buffer
        //store buffer in attribute list
        GL20.glVertexAttribPointer(attId, coordsize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void createIndexEBO(int[] indices) {
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);

        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices);
        buffer.flip(); //prep
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        vbos.add(vboID);

    }

    public static void delete() { //memory management, cleans up vaos and vbos from memory
        for (int a : vaos)
            GL30.glDeleteVertexArrays(a);
        for (int a : vbos)
            GL15.glDeleteBuffers(a);
    }
}
