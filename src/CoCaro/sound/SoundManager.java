package CoCaro.sound;

import javax.sound.sampled.*;

public class SoundManager {

    public static void play(String file){

        try{

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            SoundManager.class.getResource(
                                    "/assets/" + file));

            Clip clip = AudioSystem.getClip();

            clip.open(audio);

            clip.start();

        }catch(Exception e){

            e.printStackTrace();
        }
    }
}