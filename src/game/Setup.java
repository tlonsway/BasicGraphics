package game;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.jblas.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Setup {
	//start() returns long linked to window
	public static long start(int[] screenDims, String windowTitle) {
		glfwInit();
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_SAMPLES, 4);
		long window = glfwCreateWindow(screenDims[0],screenDims[1],windowTitle,NULL,NULL);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		if (glfwRawMouseMotionSupported())
		    glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glfwSwapInterval(1); //enable v-sync
		return window;
	}
}
