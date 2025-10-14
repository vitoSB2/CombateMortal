package com.vitor.combate_mortal.states;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

import com.vitor.combate_mortal.main.GamePanel;
import com.vitor.combate_mortal.main.Util;

public class Vitoria implements StateMethods{

    private GamePanel gp;
    BufferedImage bg, press_msg;
    BufferedImage[] mensagem;
    BufferedImage[][] lutadores;
    boolean pisca = true, musica = false;
    int vencedor, igual;
    long lastTime, currentTime;
    Clip musicaVitoria;

    public Vitoria(GamePanel gp) {
        this.gp = gp;
        setImages();
    }

    private void setImages() {
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/telaVitoria/tela_vitoria.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            press_msg = ImageIO.read(getClass().getResourceAsStream("/telaInicial/tela_inicial_mensagem.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage lutadores_atlas = null, mensagem_atlas = null;
        lutadores = new BufferedImage[6][2];
        mensagem = new BufferedImage[6];

        try {
            lutadores_atlas = ImageIO.read(getClass().getResourceAsStream("/selecao/selecao_lutadores.png"));
            mensagem_atlas = ImageIO.read(getClass().getResourceAsStream("/telaVitoria/mensagem_vitoria.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i<6; i++) {
            for(int j = 0; j < 2; j++)
                lutadores[i][j] = lutadores_atlas.getSubimage(i*70, j*95, 70, 95);
            mensagem[i] = mensagem_atlas.getSubimage(0, i*17, 162, 17);
        }
    }

    @Override
    public void update() {
        if(!musica) {
            musica = true;
            musicaVitoria = Util.play("selecao_musica");
            musicaVitoria.loop(-1);
        }

        currentTime = System.currentTimeMillis();
        if(currentTime - lastTime >= 500){
            if(pisca)pisca = false;
            else pisca = true;
            lastTime = currentTime;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(bg, 0, 0, 1200, 762, null);

        g.drawImage(lutadores[vencedor][igual], 485, 200, 225, 300, null);

        g.drawImage(mensagem[vencedor], 357, 54, mensagem[vencedor].getWidth()*3, mensagem[vencedor].getHeight()*3, gp);

        if(pisca) g.drawImage(press_msg, 396, 609, press_msg.getWidth()*3, press_msg.getHeight()*3, gp);
    }

    private void resetGame() {
        gp.jogo.resetJogo();
        gp.selecao.resetSelecao();

        if(musicaVitoria != null){
            musicaVitoria.stop();
            musica = false;
        } else {
            Inicio.musicaTema.stop();
        }
        Util.play("fim_da_selecao");
        GameStates.gameState = GameStates.SELECAO;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
            resetGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public void setVencedor(int vencedor, int igual) {
        this.vencedor = vencedor;
        this.igual = igual;
        lastTime = System.currentTimeMillis();
    }

}