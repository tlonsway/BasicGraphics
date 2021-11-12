package Game.sound;
import org.lwjgl.*;
import org.lwjgl.openal.*;
public class SoundBuffer {
	private final long bufID;
	public SoundBuffer(String AudioFile) {
		bufID = alGenBuffers();
		
	}
}
