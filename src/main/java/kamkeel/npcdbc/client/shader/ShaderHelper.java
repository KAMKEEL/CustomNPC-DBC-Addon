/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Apr 9, 2014, 11:20:26 PM (GMT)]
 */
package kamkeel.npcdbc.client.shader;

import cpw.mods.fml.common.FMLLog;
import kamkeel.npcdbc.client.ClientEventHandler;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ShaderHelper {

	private static final int VERT = ARBVertexShader.GL_VERTEX_SHADER_ARB;
	private static final int FRAG = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;

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

	public static void loadShaders(boolean reload) {
		if (!useShaders())
			return;

		if (reload)
			deleteShaders();

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
	}

	public static void deleteShaders() {
		ARBShaderObjects.glDeleteObjectARB(pylonGlow);
		ARBShaderObjects.glDeleteObjectARB(enchanterRune);
		ARBShaderObjects.glDeleteObjectARB(doppleganger);
		ARBShaderObjects.glDeleteObjectARB(halo);
		ARBShaderObjects.glDeleteObjectARB(dopplegangerBar);
		ARBShaderObjects.glDeleteObjectARB(terraPlateRune);
		ARBShaderObjects.glDeleteObjectARB(filmGrain);
		ARBShaderObjects.glDeleteObjectARB(gold);
		ARBShaderObjects.glDeleteObjectARB(categoryButton);
		ARBShaderObjects.glDeleteObjectARB(aura);
	}

	public static void useShader(int shader, IShaderUniform uniforms) {
		if (!useShaders())
			return;

		//binds shader
		ARBShaderObjects.glUseProgramObjectARB(shader);

		if (shader != 0) { //loads all uniforms
			int time = ARBShaderObjects.glGetUniformLocationARB(shader, "time");
			ARBShaderObjects.glUniform1iARB(time, ClientEventHandler.ticksInGame);
			if (uniforms != null)
				uniforms.load(shader);
		}


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

	// Most of the code taken from the LWJGL wiki
	// http://lwjgl.org/wiki/index.php?title=GLSL_Shaders_with_LWJGL

	private static int createProgram(String vert, String frag) {
		int vertId = 0, fragId = 0, program = 0;
		if (vert != null)
			vertId = createShader(vert, VERT);
		if (frag != null)
			fragId = createShader(frag, FRAG);

		program = ARBShaderObjects.glCreateProgramObjectARB();
		if (program == 0)
			return 0;

		if (vert != null)
			ARBShaderObjects.glAttachObjectARB(program, vertId);
		if (frag != null)
			ARBShaderObjects.glAttachObjectARB(program, fragId);

		ARBShaderObjects.glLinkProgramARB(program);
		if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
			FMLLog.log(Level.ERROR, getLogInfo(program));
			return 0;
		}

		ARBShaderObjects.glValidateProgramARB(program);
		if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			FMLLog.log(Level.ERROR, getLogInfo(program));
			return 0;
		}

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
				throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

			return shader;
		} catch (Exception e) {
			ARBShaderObjects.glDeleteObjectARB(shader);
			e.printStackTrace();
			return -1;
		}
	}

	private static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	private static String readFile(String filename) throws Exception {
		StringBuilder source = new StringBuilder();
		InputStream in = ShaderHelper.class.getResourceAsStream(filename);
		Exception exception = null;
		BufferedReader reader;

		if (in == null)
			return "";

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

}
