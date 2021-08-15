package game;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.jblas.*;

import java.io.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Shader {
	
	String shaderCode;
	int shaderPointer;
	int type;
	
	public Shader(String fileName, int shaderType) {
		File shaderFile = new File(fileName);
		if (!shaderFile.exists()) {
			System.err.println("Shader with path: " + fileName + " could not be found");
			return;
		}
		if (!(shaderType == GL_VERTEX_SHADER || shaderType == GL_FRAGMENT_SHADER)) {
			System.err.println("Shader of type" + shaderType + " is not supported");
			return;
		}
		
		try {
			BufferedReader fReader = new BufferedReader(new FileReader(shaderFile));
			String line = fReader.readLine();
			while(line != null) {
				shaderCode = shaderCode + line + "\n"; 
				line = fReader.readLine();
			}
			if (shaderCode.equals("")) {
				System.err.println("Loaded shader file was empty");
				return;
			}
		} catch (Exception e) {
			System.err.println("An error occured while loading shader from disk");
			e.printStackTrace();
		}
		
		shaderPointer = glCreateShader(shaderType);
		glShaderSource(shaderPointer, shaderCode);
		glCompileShader(shaderPointer);
		
		
		
	}
	
	
	
}
