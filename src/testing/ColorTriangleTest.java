package testing;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ColorTriangleTest {
	
	static String vertexShaderSource = "#version 410 core\n"
			+ "layout (location = 0) in vec3 aPos;\n"
			//+ "layout (location = 1) in vec3 aColor;\n"
			+ "out vec4 ourColor;\n"
			+ "uniform mat4 transform;\n"
			+ "uniform mat4 projection;\n"
			+ "vec4 tempVec;\n"
			+ "void main() {\n"
			+ "tempVec = projection * transform * vec4(aPos,1.0);\n"
			//+ "ourColor = (projection * transform * vec4(aPos,1.0)).w;\n"
			//+ "ourColor = (transform * vec4(aPos,1.0)).x;\n"
			+ "ourColor = vec4((tempVec.x+1)/2,(tempVec.y+1)/2,(tempVec.z+1)/2,1.0f);\n"
			+ "gl_Position = transform * vec4(aPos,1.0);"
			//+ "gl_Position = vec4(tempVec.x,tempVec.y,0f,1.0f);\n"
			//+ "ourColor = aColor;\n"
			+ "}";
	
	static String fragmentShaderSource = "#version 410 core\n"
			+ "out vec4 FragColor;\n"
			+ "in vec4 ourColor;\n"
			+ "void main() {\n"
			+ "FragColor = ourColor;\n"
			+ "}";
	
	
	public static void main(String[] args) {
		glfwInit();
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4);
	    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		long window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);
		
		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);
		
		int shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		glLinkProgram(shaderProgram);
		
		
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		
		glUseProgram(shaderProgram);
		
		/*float[] vertices = new float[] {0.2f, 0.2f, 0.0f,  1.0f, 0.0f, 0.0f,
			    -0.2f, -0.2f, 0.0f,  0.0f, 1.0f, 0.0f,
			     0.0f,  0.2f, 0.0f,  0.0f, 0.0f, 1.0f};*/
		/*float[] vertices = new float[] {-0.62165248f, 0.62165248f, 1.4024024f,
				0.62165248f, 0.62165248f, 1.4024024f,
			     0.0f,  -0.62165248f, 1.4024024f,};*/
		/*float[] vertices = new float[] {0.4f, 0.4f, 0.5f,
			    -0.4f, -0.4f, 0.5f,
			     0.0f,  0.4f, 0.5f};*/
		float[] vertices = new float[] {0.61568f, 0.61568f, -1.5f,
			    -0.6235f, -0.6235f, -1.5f,
			     0.0039f, 0.61568f, -1.5f};
		
		int VBO, VAO;
		VBO = glGenBuffers();
		VAO = glGenVertexArrays();
		
		glBindVertexArray(VAO);
		glBindBuffer(GL_ARRAY_BUFFER,VBO);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0l);
		glEnableVertexAttribArray(0);
		//glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 12l);
		//glEnableVertexAttribArray(1);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(VAO);
		
		Camera cam = new Camera(new int[] {800,600});
		
		int transformLoc = glGetUniformLocation(shaderProgram, "transform");
		if (transformLoc == -1) {
			System.err.println("Could not locate uniform transform");
		}
		glUniformMatrix4fv(transformLoc, false, cam.getCamMatFlat());
		
		//float[] projectMat = Projection2.getProjectionMatSimplified(0, 1, 1, 10);
		Projection proj = new Projection();
		float[] projectMat = proj.getProjMat();
		
		System.out.println("Projection Matrix:");
		int t = 0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				System.out.print(projectMat[t] + " ");
				t++;
			}
			System.out.print("\n");
		}
		System.out.println("Camera Matrix:");
		t = 0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				System.out.print(cam.getCamMatFlat()[t] + " ");
				t++;
			}
			System.out.print("\n");
		}
		
		
		int projectLoc = glGetUniformLocation(shaderProgram, "projection");
		
		if (projectLoc == -1) {
			System.err.println("Could not locate uniform projection");
		}
		
		glUniformMatrix4fv(projectLoc, false, projectMat);
		
		
		int err = glGetError(); 
		System.out.println("ERR: " + err);
		
		//cam.translate(0f, 0f, -2f);
		
		(new Thread(new MouseThread())).start();
		glEnable(GL_DEPTH_TEST);
		glfwShowWindow(window);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		while (!glfwWindowShouldClose(window)) {
			//err = glGetError(); 
			//System.out.println("ERR: " + err);
			glClear(GL_COLOR_BUFFER_BIT);
			glUseProgram(shaderProgram);
			glDrawArrays(GL_TRIANGLES, 0, 3);
			glfwSwapBuffers(window);
			glfwPollEvents();
			//cam.translate(0f, 0f, 0f);
			//cam.rotate('x', 0.01f);
			glUniformMatrix4fv(transformLoc, false, cam.getCamMatFlat());
		}
		
	}
	
}
