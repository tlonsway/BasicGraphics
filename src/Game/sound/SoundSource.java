package Game.sound;
import static org.lwjgl.openal.AL10.*;

public class SoundSource {
	private final int sourceID;
	public SoundSource(boolean looping, boolean relative) {
		sourceID = alGenSources();
		if(looping) {
			alSourcei(sourceID, AL_LOOPING, AL_TRUE);
		}
		if(relative) {
			alSourcei(sourceID, AL_LOOPING, AL_TRUE);
		}
	}
	
	public void setBuffer(int bufferID) {
		stop();
		alSourcei(sourceID, AL_BUFFER, bufferID);
	}
	
	public void play() {
		alSourcePlay(sourceID);
	}
	
	public boolean isPlaying() {
		return alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING;
	}
	
	public void pause() {
        alSourcePause(sourceID);
    }
	
	public void stop() {
        alSourceStop(sourceID);
    }
	
	public void cleanup() {
        stop();
        alDeleteSources(sourceID);
    }
	
	public void setPosition(float x, float y, float z) {
        alSource3f(sourceID, AL_POSITION, x, y, z);
    }

    public void setSpeed(float x, float y, float z) {
        alSource3f(sourceID, AL_VELOCITY, x, y, z);
    }

    public void setGain(float gain) {
        alSourcef(sourceID, AL_GAIN, gain);
    }
	
}
