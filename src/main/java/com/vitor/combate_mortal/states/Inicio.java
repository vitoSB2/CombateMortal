package com.vitor.combate_mortal.states;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.vitor.combate_mortal.main.GamePanel;

public class Inicio implements StateMethods{

    GamePanel gp;
    BufferedImage bg, press_msg;
    boolean pisca = true;
    long lastTime, currentTime;
    public Inicio(GamePanel gp){
        this.gp = gp;
        setImages();
    }

    public void setImages() {
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/telaInicial/tela_inicial.png"));
            press_msg = ImageIO.read(getClass().getResourceAsStream("/telaInicial/tela_inicial_mensagem.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        currentTime = System.currentTimeMillis();
        if(currentTime - lastTime >= 500){
            if(pisca)pisca = false;
            else pisca = true;
            lastTime = currentTime;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(bg, 0, 0, gp.width, gp.height, null);
        if(pisca) g.drawImage(press_msg, (gp.width-press_msg.getWidth()*2)/2, 680, press_msg.getWidth()*2, press_msg.getHeight()*2, gp);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            GameStates.gameState = GameStates.SELECAO;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
