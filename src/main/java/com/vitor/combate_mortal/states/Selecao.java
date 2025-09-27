package com.vitor.combate_mortal.states;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.vitor.combate_mortal.main.GamePanel;

public class Selecao implements StateMethods {

    GamePanel gp;
    BufferedImage bg;
    BufferedImage[] players;
    BufferedImage[][] lutadores;
    int indexPlayer = 0, estadoPlayer = 0, lutador1 = -1, lutador2= -1, espera = 0, igual = 0;
    boolean terminou = false;

    public Selecao(GamePanel gp) {
        this.gp = gp;
        setImages();
    }

    public void update() {
        if(terminou)
            espera++;

        if(espera >= 100) {
            criarPlayers();
            GameStates.gameState = GameStates.JOGO;
        }
    }

    public void draw(Graphics g) {

        g.drawImage(bg, 0, 0, 1200, 762, null);

        if(lutador1 != -1)
            g.drawImage(lutadores[lutador1][0], 141, 291, 210, 285, null);

        if(lutador2 != -1)
            g.drawImage(lutadores[lutador2][igual], 849, 291, 210, 285, null);

        if(estadoPlayer <= 1)
            if(indexPlayer < 3)
                g.drawImage(players[estadoPlayer], 420, 162+((indexPlayer%3)*180), 180, 180, null);
            else
                g.drawImage(players[estadoPlayer], 600, 162+((indexPlayer%3)*180), 180, 180, null);

    }

    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
            if(indexPlayer != 2 && indexPlayer != 5)
                indexPlayer++;
            else
                if(indexPlayer == 2)
                    indexPlayer = 0;
                else
                    indexPlayer = 3;
        }

        if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            if(indexPlayer != 0 && indexPlayer != 3)
                indexPlayer--;
            else
                if(indexPlayer == 0)
                    indexPlayer = 2;
                else
                    indexPlayer = 5;
        }

        if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
            if(indexPlayer < 3)
                indexPlayer+=3;
            else
                indexPlayer-=3;
        }

        if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if(indexPlayer > 2)
                indexPlayer-=3;
            else
                indexPlayer+=3;
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER && !terminou) {
            if(estadoPlayer != 1) {
                estadoPlayer++;
                lutador1 = indexPlayer;
            }else {
                if(!terminou){
                    if(lutador1 == indexPlayer) igual = 1;
                    lutador2 = indexPlayer;
                    estadoPlayer++;
                    terminou = true;
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    // IMPORTANDO AS IMAGENS E COLOCANDO ELAS EM BUFFERED IMAGES
    public void setImages() {

        BufferedImage lutadores_atlas = null, players_atlas = null, nomePlayers_atlas = null;
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/selecao/selecao_tela.png"));
            nomePlayers_atlas = ImageIO.read(getClass().getResourceAsStream("/props/nome_barra_de_vida.png"));
            players_atlas = ImageIO.read(getClass().getResourceAsStream("/selecao/selecao_players.png"));
            lutadores_atlas = ImageIO.read(getClass().getResourceAsStream("/selecao/selecao_lutadores.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        players = new BufferedImage[2];
        lutadores = new BufferedImage[6][2];
        gp.jogo.nomes = new BufferedImage[6];

        for(int i=0; i<6; i++) {
            if(i<2)
                players[i] = players_atlas.getSubimage(i*60, 0, 60, 60);
            gp.jogo.nomes[i] = nomePlayers_atlas.getSubimage(0, i*10, 72, 10);
        }
        for(int i=0; i<6; i++)
            for(int j=0; j<2; j++)
                lutadores[i][j] = lutadores_atlas.getSubimage(i*70, j*95, 70, 95);
    }

    public void criarPlayers() {
        Jogo.p1.selAnimations(lutador1, igual);
        Jogo.p1.resetPos();
        Jogo.p2.selAnimations(lutador2, igual);
        Jogo.p2.resetPos();
    }

    public void resetSelecao(){
        indexPlayer = 0;
        estadoPlayer = 0;
        lutador1 = -1;
        lutador2= -1;
        espera = 0;
        igual = 0;
        terminou = false;
    }

}