package com.vitor.combate_mortal.main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Util {

    public static Clip play(String nomeDoAudio) {
        try {
            URL audioUrl = Util.class.getResource("/sounds/" + nomeDoAudio + ".wav");
            if (audioUrl == null) {
                System.err.println("O arquivo de áudio não foi encontrado: " + nomeDoAudio);
                return null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioUrl);
            Clip audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.start();

            return audioClip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
