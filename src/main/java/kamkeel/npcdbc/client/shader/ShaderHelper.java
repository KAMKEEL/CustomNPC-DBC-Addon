package kamkeel.npcdbc.client.shader;

import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public final class ShaderHelper {

	private static final int VERT = ARBVertexShader.GL_VERTEX_SHADER_ARB;
	private static final int FRAG = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
	private static List<Integer> programs = new ArrayList<>();
    public static boolean optifineShadersLoaded;
	public static int currentProgram;
    public static int defaultTexture = 0;

	public static int pylonGlow = 0;
	public static int enchanterRune = 0;
	public static int manaPool = 0;
	public static int doppleganger = 0;
	public static int halo = 0;
	public static int dopplegangerBar = 0;
	public static int terraPlateRune = 0;
	public static int filmGrain = 0;
	public static int gold = 0;
	public static int categoryButton = 0;

	public static int aura = 0;
	public static int outline = 0;
	public static int perlinNoise = 0;
    public static int blur = 0;
    public static int additiveCombine = 0;
    public static int downsample13 = 0;
    public static int upsampleTent = 0;

	public static void loadShaders(boolean reload) {
        if (!useShaders())
			return;
        areOptifineShadersLoaded();

		if (reload)
			deleteShaders();
        defaultTexture = createProgram(ShaderResources.DEFAULT_VERT, ShaderResources.DEFAULT_TEXTURE_FRAG);

		pylonGlow = createProgram(null, ShaderResources.PYLON_GLOW_FRAG);
		enchanterRune = createProgram(null, ShaderResources.ENCHANTER_RUNE_FRAG);
		manaPool = createProgram(null, ShaderResources.MANA_POOL_FRAG);
		doppleganger = createProgram(ShaderResources.DOPLLEGANGER_VERT, ShaderResources.DOPLLEGANGER_FRAG);
		halo = createProgram(null, ShaderResources.HALO_FRAG);
		dopplegangerBar = createProgram(null, ShaderResources.DOPLLEGANGER_BAR_FRAG);
		terraPlateRune = createProgram(null, ShaderResources.TERRA_PLATE_RUNE_FRAG);
		filmGrain = createProgram(null, ShaderResources.FILM_GRAIN_FRAG);
		gold = createProgram(null, ShaderResources.GOLD_FRAG);
		categoryButton = createProgram(null, ShaderResources.CATEGORY_BUTTON_FRAG);

		aura = createProgram(ShaderResources.AURA_VERT, ShaderResources.AURA_FRAG);
		outline = createProgram(ShaderResources.OUTLINE_VERT, ShaderResources.OUTLINE_FRAG);
		perlinNoise = createProgram(ShaderResources.PERLIN_VERT, ShaderResources.PERLIN_FRAG);

		blur = createProgram(ShaderResources.DEFAULT_VERT, ShaderResources.BLUR_FRAG);
        additiveCombine = createProgram(ShaderResources.DEFAULT_VERT, ShaderResources.ADDITIVE_COMBINE_FRAG);
        downsample13 = createProgram(ShaderResources.DEFAULT_VERT, ShaderResources.DOWNSAMPLE_13TAP_FRAG);
        upsampleTent = createProgram(ShaderResources.DEFAULT_VERT, ShaderResources.UPSAMPLE_FILTER);
    }


	public static void useShader(int shader, IShaderUniform uniforms) {
		if (!useShaders())
			return;
		ARBShaderObjects.glUseProgramObjectARB(currentProgram = shader);

        if (shader != 0) {
			uniform1f("time", ClientProxy.getTimeSinceStart());
			uniformVec2("u_resolution", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

            if (uniforms != null)
                loadUniforms(uniforms);
		}
	}

    public static void loadUniforms(IShaderUniform uniforms) {
        uniforms.load();
	}

	public static void useShader(int shader) {
		useShader(shader, null);
	}

	public static void releaseShader() {
		useShader(0);
	}

	public static boolean useShaders() {
		return ConfigDBCClient.UseShaders && OpenGlHelper.shadersSupported;
	}

    private static int createProgram(String vert, String frag) {
		int vertexShader = 0, fragmentShader = 0, program = 0;
		if (vert != null)
			vertexShader = createShader(vert, VERT);
		if (frag != null)
			fragmentShader = createShader(frag, FRAG);

		program = ARBShaderObjects.glCreateProgramObjectARB();
		if (program == 0)
			return 0;

		if (vert != null)
			ARBShaderObjects.glAttachObjectARB(program, vertexShader);
		if (frag != null)
			ARBShaderObjects.glAttachObjectARB(program, fragmentShader);

		ARBShaderObjects.glLinkProgramARB(program);
		if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            CommonProxy.LOGGER.error(getLogInfo(program));
			return 0;
		}

		ARBShaderObjects.glValidateProgramARB(program);
		if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            CommonProxy.LOGGER.error(getLogInfo(program));
			return 0;
		}

		ARBShaderObjects.glDeleteObjectARB(vertexShader);
		ARBShaderObjects.glDeleteObjectARB(fragmentShader);

		programs.add(program);
		return program;
	}

	private static int createShader(String filename, int shaderType) {
		int shader = 0;
		try {
			shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

			if (shader == 0)
				return 0;

			ARBShaderObjects.glShaderSourceARB(shader, readFile(filename));
			ARBShaderObjects.glCompileShaderARB(shader);

			if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                CommonProxy.LOGGER.error("Error creating shader: " + getLogInfo(shader));

			return shader;
		} catch (Exception e) {
			ARBShaderObjects.glDeleteObjectARB(shader);
			e.printStackTrace();
			return -1;
		}
	}

	public static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	private static String readFile(String filename) throws Exception {
		StringBuilder source = new StringBuilder();
        InputStream in = ShaderHelper.class.getResourceAsStream(filename);
		Exception exception = null;
		BufferedReader reader;

        if (in == null) {
            CommonProxy.LOGGER.error("Resource not found: " + filename);
            return "";
        }

		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			Exception innerExc = null;
			try {
				String line;
				while ((line = reader.readLine()) != null)
					source.append(line).append('\n');
			} catch (Exception exc) {
				exception = exc;
			} finally {
				try {
					reader.close();
				} catch (Exception exc) {
					if (innerExc == null)
						innerExc = exc;
					else
						exc.printStackTrace();
				}
			}

			if (innerExc != null)
				throw innerExc;
		} catch (Exception exc) {
			exception = exc;
		} finally {
			try {
				in.close();
			} catch (Exception exc) {
				if (exception == null)
					exception = exc;
				else
					exc.printStackTrace();
			}

			if (exception != null)
				throw exception;
		}

		return source.toString();
	}

	public static void deleteShaders() {
		for (Integer p : programs)
			ARBShaderObjects.glDeleteObjectARB(p);
	}

    public static boolean areOptifineShadersLoaded() {
        try {
            Class<?> shaders = Class.forName("shadersmod.client.Shaders");
            try {
                String shaderPack = (String) shaders.getMethod("getShaderPackName").invoke(null);
                if (shaderPack != null) {
                    return optifineShadersLoaded = true;
                }
            } catch (Exception e) {
                ClientProxy.LOGGER.warn("Failed to get shader pack name");
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {

        }
        return optifineShadersLoaded = false;
    }

    //////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//Uniform helpers
	public static void uniform1f(String name, float x) {
		int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
		ARBShaderObjects.glUniform1fARB(uniformLocation, x);
	}

    public static void uniform1i(String name, int x) {
        int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
        ARBShaderObjects.glUniform1iARB(uniformLocation, x);
    }
	public static void uniformArray(String name, float[] array) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length);
		buffer.put(array);
		buffer.flip();

		uniformBuffer(name, buffer);
	}

	public static void uniformArray(String name, int[] array) {
		IntBuffer buffer = BufferUtils.createIntBuffer(array.length);
		buffer.put(array);
		buffer.flip();
		uniformBuffer(name, buffer);
	}

	public static void uniformVec2(String name, float x, float y) {
		int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
		ARBShaderObjects.glUniform2fARB(uniformLocation, x, y);
	}

	public static void uniformVec3(String name, float x, float y, float z) {
		int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
		ARBShaderObjects.glUniform3fARB(uniformLocation, x, y, z);
	}

	public static void uniformVec4(String name, float x, float y, float z, float w) {
		int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
		ARBShaderObjects.glUniform4fARB(uniformLocation, x, y, z, w);
	}

	public static void loadTextureUnit(int textureUnit, String textureLoc) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(GL13.GL_TEXTURE0 + textureUnit);
		noppes.npcs.client.ClientProxy.bindTexture(new ResourceLocation(textureLoc));
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public static void uniformTexture(String name, int textureUnit, String textureLoc) {
		ShaderHelper.loadTextureUnit(textureUnit, textureLoc);
		int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
		ARBShaderObjects.glUniform1iARB(uniformLocation, textureUnit);
	}

    public static void uniformTexture(String name, int textureUnit, int textureID) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(GL13.GL_TEXTURE0 + textureUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
        ARBShaderObjects.glUniform1iARB(uniformLocation, textureUnit);
    }


    public static void uniformTextureResolution(String name, String textureLoc) {
		int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		noppes.npcs.client.ClientProxy.bindTexture(new ResourceLocation(textureLoc));

		float width = GL11.glGetTexLevelParameterf(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		float height = GL11.glGetTexLevelParameterf(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
		uniformVec2(name, width, height);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
	}

	public static void uniformColor(String name, int color, float alpha) {
		float r = (color >> 16 & 255) / 255f;
		float g = (color >> 8 & 255) / 255f;
		float b = (color & 255) / 255f;
		uniformVec4(name, r, g, b, alpha);
	}

	public static void uniformBuffer(String name, FloatBuffer buffer) {
		int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
		ARBShaderObjects.glUniform1ARB(uniformLocation, buffer);
	}

	public static void uniformBuffer(String name, IntBuffer buffer) {
		int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(currentProgram, name);
		ARBShaderObjects.glUniform1ARB(uniformLocation, buffer);
	}


}
/* BUILT-IN GLSL 120 VERTEX ATTRIBUTES

gl_Vertex: This attribute represents the position of the vertex in object space. It is a vec4 attribute containing the x, y, z, and w coordinates of the vertex.

gl_Normal: This attribute represents the normal vector of the vertex. It is a vec3 attribute used for lighting calculations to determine how light interacts with the surface.

gl_Color: This attribute represents the color of the vertex. It is a vec4 attribute containing the RGBA color values.

gl_MultiTexCoord0, gl_MultiTexCoord1, ..., gl_MultiTexCoord7: These attributes represent texture coordinates for multiple texture units. Each attribute is a vec4 containing the s, t, r, and q components of the texture coordinate.

gl_SecondaryColor: This attribute represents the secondary color of the vertex. It is a vec4 attribute used for two-sided lighting calculations.

gl_FogCoord: This attribute represents the fog coordinate of the vertex. It is a float attribute used to determine fog effects.
*/
