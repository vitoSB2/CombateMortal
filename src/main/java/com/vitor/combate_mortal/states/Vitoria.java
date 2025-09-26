package com.vitor.combate_mortal.states;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.vitor.combate_mortal.main.GamePanel;

public class Vitoria implements StateMethods{

    private GamePanel gp;
    BufferedImage bg, press_msg;
    BufferedImage[] lutadores, mensagem;
    boolean pisca = true;
    int vencedor;
    long lastTime, currentTime;

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
        lutadores = new BufferedImage[8];
        mensagem = new BufferedImage[4];

        try {
            lutadores_atlas = ImageIO.read(getClass().getResourceAsStream("/selecao/selecao_lutadores.png"));
            mensagem_atlas = ImageIO.read(getClass().getResourceAsStream("/telaVitoria/mensagem_vitoria.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i<8; i++) {
            lutadores[i] = lutadores_atlas.getSubimage(i*70, 0, 70, 95);
            if(i<4) mensagem[i] = mensagem_atlas.getSubimage(0, i*17, 162, 17);
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
        g.drawImage(bg, 0, 0, 1200, 762, null);

        g.drawImage(lutadores[vencedor], 485, 200, 225, 300, null);

        if(vencedor>=4)
            g.drawImage(mensagem[vencedor-4], 357, 54, mensagem[vencedor-4].getWidth()*3, mensagem[vencedor-4].getHeight()*3, gp);
        else
            g.drawImage(mensagem[vencedor], 357, 54, mensagem[vencedor].getWidth()*3, mensagem[vencedor].getHeight()*3, gp);

        if(pisca) g.drawImage(press_msg, 396, 609, press_msg.getWidth()*3, press_msg.getHeight()*3, gp);
    }

    private void resetGame() {
        gp.jogo.resetJogo();
        gp.selecao.resetSelecao();
        GameStates.gameState = GameStates.SELECAO;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            resetGame();
            //A IDEIA É RESETAR TODOS OS VALORES, COMO POSIÇÃO DOS JOGADORES, VIDA, VARIÁVEIS DO ESTADO SELECÃO, ETC.
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

    public void setVencedor(int vencedor) {
        this.vencedor = vencedor;
        lastTime = System.currentTimeMillis();
    }

}