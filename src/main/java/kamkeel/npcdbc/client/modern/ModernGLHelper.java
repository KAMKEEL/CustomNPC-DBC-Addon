package kamkeel.npcdbc.client.modern;

import kamkeel.npcdbc.client.shader.ShaderHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class ModernGLHelper {
    private static Map<Integer, ModernModel> loadedModels = new HashMap();
    public static ModernModel quad;
    public static int quaa;

    public static void init() {
        checkGLError(" befoooooooooooore glGenVertexArrays");
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
          quad = createVAO(vertices, indices);

        boolean bo = GLContext.getCapabilities().OpenGL33;
//
//        float[] vertices = { //
//            -0.5f, -0.5f, 0f, 1, 0, 0, //color
//            0.5f, -0.5f, 0f, 0, 1, 0,        //color
//            0.5f, 0.5f, 0f, 0, 0, 1,   //color
//            -0.5f, 0.5f, 0f, 1, 1, 1};     //color
//        int[] indices = {0, 1, 2, 0, 2, 3};
//        GL30.glGenVertexArrays();
//        quaa = GL30.glGenVertexArrays();
//        GL30.glBindVertexArray(quaa);
//
//
//        int vertexVBO = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
//
//
//        // float[] vertices = new float[]{-0.75f, -0.75f, 0f, 0.75f, -0.75f, 0f, 0f, 0.75f, 0f};
//
//
//        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
//        buffer.put(vertices).flip();
//
//        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
//        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * 4, 0);
//        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * 4, 3 * 4);
//        GL20.glEnableVertexAttribArray(0);
//        GL20.glEnableVertexAttribArray(1);
//        //  GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//
//        int elementVBO = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementVBO);
//        IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.length);
//        intBuffer.put(indices).flip();
//        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);
//        //  GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
//
//
//        ShaderHelper.useShader(ShaderHelper.modern);
//        //    GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
//        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
//        ShaderHelper.releaseShader();
//        GL20.glDisableVertexAttribArray(0);
//
//        GL30.glBindVertexArray(0);
//        // GL30.glDeleteVertexArrays(quad);
//        System.out.println();
    }

    public static ModernModel createVAO(float[] data, int[] indices) {

        int vao = GL30.glGenVertexArrays(); //each VAO has unique id
        vao = GL30.glGenVertexArrays(); //each VAO has unique id

        checkGLError("glGenVertexArrays");
        GL30.glBindVertexArray(vao); //change current active VAO to this vaoID
        checkGLError("1");

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

        checkGLError("12");
       // GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
      //  GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        checkGLError("13");
        ModernModel model = new ModernModel(vao, vbo, ebo, indices.length);
        loadedModels.put(vao, model);
        return model;
    }

    private static void checkGLError(String message) {
        int error;
        while ((error = GL11.glGetError()) != GL11.GL_NO_ERROR) {
            System.err.println("GL ERROR [" + message + "]: " + error);
        }
    }

    public static void render() {

       // GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
      //  GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
      //  GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
       // GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);


      //  GL30.glBindVertexArray(quad.VAO);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);

//        checkGLError("hehe");
//        ShaderHelper.useShader(ShaderHelper.modern);
//        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);


        // int vao = GL30.glGenVertexArrays();
        //GL30.glBindVertexArray(vao);
        //GL30.glBindVertexArray(0);
//        GL30.glBindVertexArray(quad.VAO);
//        checkGLError("hih");
//        GL20.glEnableVertexAttribArray(0);
//        GL20.glEnableVertexAttribArray(1);
//        GL20.glEnableVertexAttribArray(2);

        // Draw elements (assuming using GL_TRIANGLES and an EBO)
//        GL11.glDrawElements(GL11.GL_TRIANGLES, quad.indexCount, GL11.GL_UNSIGNED_INT, 0);
//
//
//        GL30.glBindVertexArray(0);
//        ShaderHelper.releaseShader();

        boolean bo = false;
        if (bo) {

            //  quad = GL30.glGenVertexArrays();
            int vertexVBO = GL15.glGenBuffers();
            // GL30.glBindVertexArray(quad);
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

    public static void delete() { //memory management, cleans up vaos and vbos from memory
        for (ModernModel model : loadedModels.values())
            model.destroy();
    }
}
