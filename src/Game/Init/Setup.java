package Game.Init;

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
		glViewport(0, 0, 1920, 1080);
		String glvendor = glGetString(GL_VENDOR);
		String glrenderer = glGetString(GL_RENDERER);
		System.out.println("DEVICE IN USE: " + glvendor + " : " + glrenderer);
		glfwSwapInterval(1); //enable v-sync
		return window;
	}
}
