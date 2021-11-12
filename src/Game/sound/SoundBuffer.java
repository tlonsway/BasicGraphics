package Game.sound;

import org.lwjgl.stb.*;
import java.nio.*;
import static org.lwjgl.openal.AL10.*;

public class SoundBuffer {
	private final int bufID;
	public SoundBuffer(String AudioFileName) {
		bufID = alGenBuffers();
		try(STBVorbisInfo info = STBVorbisInfo.malloc()){
			ShortBuffer pcm = IOUtil.readVorbis(AudioFileName, 32 * 1024, info);
            alBufferData(bufID, AL_FORMAT_MONO16, pcm, info.sample_rate());
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 
	public int getBufferID() {
		return bufID;
	}
	public void delete() {
		alDeleteBuffers(bufID);
	}
}
