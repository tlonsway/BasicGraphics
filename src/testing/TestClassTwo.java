package testing;

import java.awt.*;
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

public class TestClassTwo {
	static long window;
	static String vertexShaderSource = "#version 410 core\n" +
		    "layout (location = 0) in vec3 aPos;\n" +
		    "layout (location = 1) in vec3 aColor;\n" +
		    "out vec3 ourColor;\n" +
		    "void main()\n" +
		    "{\n" +
		    "   gl_Position = vec4(aPos, 1.0);\n" +
		    "   ourColor = aColor;\n" +
		    "}\0";
	
	static String fragmentShaderSource = "#version 410 core\n" +
		    "out vec4 FragColor;\n" +
		    "in vec3 ourColor;\n" +
		    "void main()\n" +
		    "{\n" +
		    "   FragColor = vec4(ourColor, 1.0f);\n" +
		    "}\n\0";
	public static void main(String[] args) {
		
		glfwInit();
		glfwDefaultWindowHints();
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
	    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	    window = glfwCreateWindow(800, 600, "Triangles!", NULL, NULL);
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
	    float[] vertices = { 0.5f, -0.5f, 0.0f, 0, 0, 0, 
	    					-0.5f, -0.5f, 0.0f, 0, 0, 0, 
	    					 0.0f,  0.5f, 0.0f, 0, 0, 0, 
	    					 1.0f, 0.5f, 0, 0, 0, 0};
	    float[] texCoords = {
	    	    0.0f, 0.0f,  // lower-left corner  
	    	    1.0f, 0.0f,  // lower-right corner
	    	    0.5f, 1.0f   // top-center corner
	    	};	    		
	    int[] indices = new int[] {0, 1, 2, 0, 2, 3}; 
	    						   //0, 2, 3};
	    int VBO, VAO, EBO;
	    VAO = glGenVertexArrays();
	    VBO = glGenBuffers();
	    EBO = glGenBuffers();
	    
	    glBindVertexArray(VAO);
	    glBindBuffer(GL_ARRAY_BUFFER, VBO);
	    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
	    
	    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
	    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW); 
	    
	    glVertexAttribPointer(0, 3, GL_FLOAT, false, 24, 0l);
	    glEnableVertexAttribArray(0);
	    
	    glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 12l);
	    glEnableVertexAttribArray(1);
	    
	    //glBindBuffer(GL_ARRAY_BUFFER, 0); 
	    //glBindVertexArray(0); 
	    glfwShowWindow(window);
	    //Timer time = new Timer();
	    //Thread thread = new Thread(time);
	    glUseProgram(shaderProgram);
	    while (!glfwWindowShouldClose(window))
	    {
	        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
	        glClear(GL_COLOR_BUFFER_BIT);
	        
	        //glUseProgram(shaderProgram);
	        /*
	        float timeValue = (float)glfwGetTime();
	        float greenValue = (float)(Math.sin(timeValue) / 2.0f) + 0.5f;
	        int vertexColorLocation = glGetUniformLocation(shaderProgram, "ourColor");
	        glUniform4f(vertexColorLocation, 0.0f, greenValue, 0.0f, 1.0f);
	        */
	        glBindVertexArray(VAO);
	        //glDrawArrays(GL_TRIANGLES, 0, 3);
	        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
	        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
	        //glBindVertexArray(0);
	        glfwSwapBuffers(window);
	        glfwPollEvents();
	    }
	    
	}
	
	public void getPixelValue(int x, int y) {
		try {
			Robot r = new Robot();
			Color c = r.getPixelColor(x, y);
			System.out.print("(R value * 2 - 256)/256: " + (c.getRed()*2.0-256)/256 + " ");
	        System.out.print("(G value * 2 - 256)/256: " + (c.getGreen()*2.0-256)/256 + " ");
	        System.out.print("(B value * 2 - 256)/256: " + (c.getBlue()*2.0-256)/256 + " ");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
