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
    BufferedImage[] players, lutadores;
    int indexPlayer = 0, estadoPlayer = 0, lutador1 = -1, lutador2= -1, espera = 0;
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
            g.drawImage(lutadores[lutador1], 189, 282, 210, 285, null);

        if(lutador2 != -1)
            g.drawImage(lutadores[lutador2], 801, 282, 210, 285, null);

        if(estadoPlayer <= 1)
            g.drawImage(players[estadoPlayer], 505, 27+(indexPlayer*177), 192, 177, null);
    }

    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
            if(indexPlayer != 3)
                indexPlayer++;
            else
                indexPlayer = 0;
        }

        if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            if(indexPlayer != 0)
                indexPlayer--;
            else
                indexPlayer = 3;
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER && !terminou) {
            if(estadoPlayer != 1) {
                estadoPlayer++;
                lutador1 = indexPlayer;
            }else {
                if(!terminou)
                    if(lutador1 == indexPlayer) lutador2 = indexPlayer+4;
                    else lutador2 = indexPlayer;
                estadoPlayer++;
                terminou = true;
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
        lutadores = new BufferedImage[8];
        gp.jogo.nomes = new BufferedImage[4];

        for(int i=0; i<4; i++) {
            if(i<2)
                players[i] = players_atlas.getSubimage(i*128, 0, 64, 59);
            gp.jogo.nomes[i] = nomePlayers_atlas.getSubimage(0, i*10, 72, 10);
        }
        for(int i=0; i<8; i++)
            lutadores[i] = lutadores_atlas.getSubimage(i*70, 0, 70, 95);
    }

    public void criarPlayers() {
        Jogo.p1.selAnimations(lutador1);
        Jogo.p1.resetPos();
        Jogo.p2.selAnimations(lutador2);
        Jogo.p2.resetPos();
    }

    public void resetSelecao(){
        indexPlayer = 0;
        estadoPlayer = 0;
        lutador1 = -1;
        lutador2= -1;
        espera = 0;
        terminou = false;
    }

}