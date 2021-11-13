package Game.sound;

import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.openal.*;
import java.util.*;
import Game.Graphics.Camera;
import java.nio.*;
import static org.lwjgl.openal.ALC11.*;

public class SoundManager {
	private long device;
	private long context; 
	private SoundListener listener;
	private HashMap<String, SoundSource> sources;
	private HashMap<String, SoundBuffer> buffers;
 	public SoundManager(Camera cam) {
 		try {
			init(); 
		}catch(Exception e) {
			e.printStackTrace();
		}
		listener = new SoundListener(cam);
		sources = new HashMap<>();
		buffers = new HashMap<>();
	}
 	
 	public void init() throws Exception {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }
 	
 	public void cleanUp() {
 		for(String key : sources.keySet()) {
 			sources.get(key).cleanup();
 		}
 		for(String key : buffers.keySet()) {
 			buffers.get(key).delete();
 		}
 	}
 	
 	public void setSourcePosition(String sourceName, float x, float y, float z) {
 		sources.get(sourceName).setPosition(x, y, z);
 	}
 	
 	public void updateListner() {
 		listener.update();
 	}
 	
 	public void addBuffer(String file, String soundName) {
 		SoundBuffer buffer = new SoundBuffer(file);
 		buffers.put(soundName, buffer);
 	}
 	
 	public void addSource(String soundName, String sourceName, boolean looping, boolean relative) {
 		int bufferID = buffers.get(soundName).getBufferID();
 		SoundSource ss = new SoundSource(looping, relative);
 		ss.setBuffer(bufferID);
 		sources.put(sourceName, ss);
 	}
 	
 	public void playSound(String sourceName) {
 		sources.get(sourceName).play();
 	}
 	
 	public void pauseSound(String sourceName) {
 		sources.get(sourceName).pause();
 	}
 	
 	public void stopSound(String sourceName) {
 		sources.get(sourceName).stop();
 	}
}
