package testing;

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

public class ProjectionTest {

	static long window;
	static boolean wDown = false;
	static boolean aDown = false;
	static boolean sDown = false;
	static boolean dDown = false;
	static boolean spaceDown = false;
	static boolean ctrlDown = false;
	
	
	static String vertexShaderSource = "#version 410 core\n"
			+ "layout (location = 0) in vec3 aPos;\n"
			+ "uniform mat4 fullMat;\n"
			+ "void main() {"
			+ "gl_Position = fullMat * vec4(aPos,1.0);\n;"
			+ "}";
	static String fragmentShaderSource = "#version 410 core\n"
			+ "out vec4 FragColor;\n"
			+ "void main() {\n"
			+ "FragColor = vec4(1.0f,0.2f,0.2f,1.0f);\n"
			+ "}";
	
	public static void main(String[] args) {
		
		int[] screenDims = new int[] {1920,1080};
		
		
		glfwInit();
		glfwDefaultWindowHints();
		window = glfwCreateWindow(screenDims[0],screenDims[1],"Projection Test",NULL,NULL);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		int vShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vShader, vertexShaderSource);
		glCompileShader(vShader);
		
		int fShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fShader, fragmentShaderSource);
		glCompileShader(fShader);
		
		int sProg = glCreateProgram();
		glAttachShader(sProg, vShader);
		glAttachShader(sProg, fShader);
		glLinkProgram(sProg);
		
		glUseProgram(sProg);
		
		float[] vert = new float[] {-0.1f,0.1f,-1.2f,
									0f,-0.1f,-1.2f,
									0.1f,0.1f,-1.2f};
		int VBO,VAO;
		VBO = glGenBuffers();
		VAO = glGenVertexArrays();
		
		glBindVertexArray(VAO);
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vert, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0,3,GL_FLOAT,false,12,0l);
		glEnableVertexAttribArray(0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(VAO);
		
		Camera cam = new Camera(screenDims);
		
		
		//Projection proj = new Projection();
		Projection proj = new Projection(80f, 0.1f, 10f, screenDims);
		//float[] projectMat = proj.getProjMat();
		
		float[] fullMatTemp = combineMats(proj.getProjMatFMat(),cam.getCamMat());
		
		int fullMatLoc = glGetUniformLocation(sProg, "fullMat");
		glUniformMatrix4fv(fullMatLoc, false, fullMatTemp);
		
		int err = glGetError();
		System.out.println("ERR:" + err);
		
		
		glfwSwapInterval(1);
		glfwShowWindow(window);
		glClearColor(0.0f,0.0f,0.0f,1.0f);
		
		glUseProgram(sProg);
		
		
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
			if (key==GLFW_KEY_W && action == GLFW_PRESS) {
				wDown=true;
			}
			if (key==GLFW_KEY_W && action == GLFW_RELEASE) {
				wDown=false;
			}
			if (key==GLFW_KEY_A && action == GLFW_PRESS) {
				aDown=true;
			}
			if (key==GLFW_KEY_A && action == GLFW_RELEASE) {
				aDown=false;
			}
			if (key==GLFW_KEY_S && action == GLFW_PRESS) {
				sDown=true;
			}
			if (key==GLFW_KEY_S && action == GLFW_RELEASE) {
				sDown=false;
			}
			if (key==GLFW_KEY_D && action == GLFW_PRESS) {
				dDown=true;
			}
			if (key==GLFW_KEY_D && action == GLFW_RELEASE) {
				dDown=false;
			}
			if (key==GLFW_KEY_SPACE && action == GLFW_PRESS) {
				spaceDown=true;
			}
			if (key==GLFW_KEY_SPACE && action == GLFW_RELEASE) {
				spaceDown=false;
			}
			if (key==GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS) {
				ctrlDown=true;
			}
			if (key==GLFW_KEY_LEFT_CONTROL && action == GLFW_RELEASE) {
				ctrlDown=false;
			}
		});
		
		while(!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT);
			glDrawArrays(GL_TRIANGLES,0,3);
			glfwSwapBuffers(window);
			glfwPollEvents();
			//cam.rotate('y',0.01f);
			if (wDown) {
				cam.translate(0f, 0f, 0.01f);
			}
			if (aDown) {
				cam.translate(0.01f, 0f, 0.f);
			}
			if (sDown) {
				cam.translate(0f, 0f, -0.01f);
			}
			if (dDown) {
				cam.translate(-0.01f, 0f, 0.f);
			}
			if(spaceDown) {
				cam.translate(0.0f, -0.01f, 0f);
			}
			if(ctrlDown) {
				cam.translate(0f,  0.01f,  0f);
			}
			
			
			float[] fullMat = combineMats(proj.getProjMatFMat(),cam.getCamMat());
			glUniformMatrix4fv(fullMatLoc, false, fullMat);
		}
	}
	
	private static float[] combineMats(FloatMatrix projmat, FloatMatrix camMat) {
		FloatMatrix res = projmat.mmul(camMat);
		float[] ret = new float[16];
		int t = 0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				ret[t] = res.get(c,r);
				t++;
			}
		}
		return ret;
	}
}