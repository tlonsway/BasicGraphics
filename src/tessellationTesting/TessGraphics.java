package tessellationTesting;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.jblas.FloatMatrix;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL41.*;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import Game.GameData.GameObject;
import Game.GameData.KeyboardManager;
import Game.GameData.MouseManager;
import Game.GameData.ObjectGeneration;
import Game.Graphics.Mesh;
import Game.Graphics.Operations;
import Game.Graphics.Polygon;
import Game.Graphics.Shader;
import engine.Camera;
public class TessGraphics {
	int[] dimensions;
	long window;
	int tessProg;
	Camera cam;
	public TessGraphics(int[] dimensions) {
		this.dimensions = dimensions;
		window = start(dimensions, "Tesselation");
		cam = new Camera(this.dimensions);
		//making the pipeline
		Shader vs = new Shader("Shaders/tesselation/vs.glsl", GL_VERTEX_SHADER);
		Shader tcs = new Shader("Shaders/tesselation/fs.glsl", GL_TESS_CONTROL_SHADER);
		Shader tes = new Shader("Shaders/tesselation/fs.glsl", GL_TESS_EVALUATION_SHADER);
		Shader fs = new Shader("Shaders/tesselation/fs.glsl", GL_FRAGMENT_SHADER);
		
		tessProg = glCreateProgram();
		glAttachShader(tessProg, vs.getShader());
		glAttachShader(tessProg, tcs.getShader());
		glAttachShader(tessProg, tes.getShader());
		glAttachShader(tessProg, fs.getShader());
		glLinkProgram(tessProg);
		
		glDeleteShader(vs.getShader());
		glDeleteShader(tcs.getShader());
		glDeleteShader(tes.getShader());
		glDeleteShader(fs.getShader());
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
		});
	}
	public void loop() {
		glfwMakeContextCurrent(window);
		while(!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		}
	}
	public static long start(int[] screenDims, String windowTitle) {
		glfwInit();
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_SAMPLES, 4);
		
		GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwWindowHint(GLFW_RED_BITS, mode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
		glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
		
		//long window = glfwCreateWindow(screenDims[0],screenDims[1],windowTitle,glfwGetPrimaryMonitor(),NULL); //FULLSCREEN
		long window = glfwCreateWindow(screenDims[0],screenDims[1],windowTitle,NULL,NULL); //WINDOWED
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		if (glfwRawMouseMotionSupported())
		    glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glfwSwapInterval(1); //enable v-sync
		return window;
	}
}
