package testing;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TriangleTest {
	static long window;
	static String vertexShaderSource = "#version 410 core\n" + 
			"layout (location = 0) in vec3 aPos;\n" + 
			"void main()\n" + 
			"{\n" + 
			"gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" + 
			"}";
	
	static String fragmentShaderSource = "#version 410 core\n"
			+ "out vec4 FragColor;\n"
			+ "void main()\n"
			+ "{\n"
			+ "FragColor = vec4(1.0f,0.0f,0.0f,1.0f);"
			+ "}";
	public static void main(String[] args) {
		
		glfwInit();
		glfwDefaultWindowHints();
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
	    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	    window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
	    glfwMakeContextCurrent(window);
	    GL.createCapabilities();
	    //glViewport(0, 0, 800, 600);
	    
	    int vertexShader = glCreateShader(GL_VERTEX_SHADER);
	    glShaderSource(vertexShader,vertexShaderSource);
	    glCompileShader(vertexShader);
	    int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
	    glShaderSource(fragmentShader,fragmentShaderSource);
	    glCompileShader(fragmentShader);
	    int shaderProgram = glCreateProgram();
	    glAttachShader(shaderProgram, vertexShader);
	    glAttachShader(shaderProgram, fragmentShader);
	    glLinkProgram(shaderProgram);
	    glDeleteShader(vertexShader);
	    glDeleteShader(fragmentShader);
	    float[] vertices = {-0.5f,-0.5f,0.0f,0.5f,-0.5f,0.0f,0.0f,0.5f,0.0f};
	    int VBO, VAO;
	    VAO = glGenVertexArrays();
	    VBO = glGenBuffers();
	    glBindVertexArray(VAO);
	    glBindBuffer(GL_ARRAY_BUFFER, VBO);
	    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
	    glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0l);
	    glEnableVertexAttribArray(0);
	    glBindBuffer(GL_ARRAY_BUFFER, 0); 
	    glBindVertexArray(0); 
	    glfwShowWindow(window);
	    while (!glfwWindowShouldClose(window))
	    {
	        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
	        glClear(GL_COLOR_BUFFER_BIT);
	        glUseProgram(shaderProgram);
	        glBindVertexArray(VAO);
	        glDrawArrays(GL_TRIANGLES, 0, 3);
	        glfwSwapBuffers(window);
	        glfwPollEvents();
	    }
	    
	}
	
	
	
}
