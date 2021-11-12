package Game.sound;

import static org.lwjgl.openal.AL11.*;
import org.lwjgl.openal.*;
import static org.lwjgl.openal.AL.*;
import static org.lwjgl.openal.ALC.*;
import static org.lwjgl.openal.ALC11.*;

public class SoundBuffer {
	private final long bufID;
	public SoundBuffer(String AudioFile) {
		bufID = alGenBuffers();
		
	}
}
