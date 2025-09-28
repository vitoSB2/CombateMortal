package com.vitor.combate_mortal.states;

import com.vitor.combate_mortal.entity.Player1;
import com.vitor.combate_mortal.entity.Player2;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

import com.vitor.combate_mortal.main.GamePanel;
import com.vitor.combate_mortal.main.Util;

public class Jogo implements StateMethods{

    GamePanel gp;
    public static Player1 p1;
    public static Player2 p2;
    int vencedor;
    BufferedImage bg, lifeBar, specialBar;
    BufferedImage[] nomes_esq, nomes_dir;

    // Constantes de dano
    private static final int DANO_SOCO_FRACO = 10;
    private static final int DANO_CHUTE_FRACO = 10;
    private static final int DANO_SOCO_FORTE = 15;
    private static final int DANO_CHUTE_FORTE = 15;
    private static final int DANO_ESPECIAL_1 = 20;
    private static final int DANO_ESPECIAL_2 = 25;

    // INTERVALO PRA PASSAR PRA TELA DE VITÓRIA
    long endStart, endNow;
    public boolean ended = false;

    boolean musica = false;
    Clip musicaArena;

    public Jogo(GamePanel gp) {
        this.gp = gp;
        setImages();
        p1 = new Player1(gp, 100);
        p2 = new Player2(gp, 1000);
    }

    public void update() {
        if(!musica){
            musica = true;
            musicaArena = Util.play("batalha");
        }
        p1.update();
        p2.update();
        checkCollisions();
    }

    private void checkCollisions() {
        // Verifica colisão P1 atacando P2
        if (p1.attack && p1.hitbox.intersects(p2.hurtbox) && !p1.hitConnectedThisAttack) {
            int dano = 0;
            switch (p1.tipoAtaque) {
                case 1: // Soco Fraco
                    dano = DANO_SOCO_FRACO;
                    break;
                case 2: // Chute Fraco
                    dano = DANO_CHUTE_FRACO;
                    break;
                case 3: // Soco Forte
                    dano = DANO_SOCO_FORTE;
                    break;
                case 4: // Chute Forte
                    dano = DANO_CHUTE_FORTE;
                    break;
                case 5:
                    dano = DANO_ESPECIAL_1;
                    break;
                case 6:
                    dano = DANO_ESPECIAL_2;
                    break;
            }
            p2.vida -= dano;
            p2.initAtacado();
            if (p2.vida <= 0) {
                //p2.vida = 0;
                vencedor = 1;
                ended = true;
                p1.ganhou=true;
            }
            p1.hitConnectedThisAttack = true;
            Util.play("acerto");
        }

        // Verifica colisão P2 atacando P1
        if (p2.attack && p2.hitbox.intersects(p1.hurtbox) && !p2.hitConnectedThisAttack) {
            int dano = 0;
            switch (p2.tipoAtaque) {
                case 1: // Soco Fraco
                    dano = DANO_SOCO_FRACO;
                    break;
                case 2: // Chute Fraco
                    dano = DANO_CHUTE_FRACO;
                    break;
                case 3: // Soco Forte
                    dano = DANO_SOCO_FORTE;
                    break;
                case 4: // Chute Forte
                    dano = DANO_CHUTE_FORTE;
                    break;
                case 5:
                    dano = DANO_ESPECIAL_1;
                    break;
                case 6:
                    dano = DANO_ESPECIAL_2;
                    break;
            }
            p1.vida -= dano;
            p1.initAtacado();
            if (p1.vida <= 0) {
                //p1.vida = 0;
                vencedor = 2;
                ended = true;
                p2.ganhou=true;
            }
            p2.hitConnectedThisAttack = true;
            Util.play("acerto");
        }

        // VERIFICA SE O JOGO ACABOU E DÁ UM INTERVALO PARA ANUNCIAR O VENCEDOR
        if(ended) {
            endNow = System.currentTimeMillis();

            if(endStart <= 0) {
                endStart = System.currentTimeMillis();
                p1.anim.estado = 0;
                p1.anim.tick = 0;
                p2.anim.estado = 0;
                p2.anim.tick = 0;
            }

            if(endNow - endStart >= 3000) {
                musicaArena.stop();
                GameStates.gameState = GameStates.VITORIA;
                if(vencedor == 1) gp.vitoria.setVencedor(p1.personagem, 0);
                else if(vencedor == 2){
                    if(p1.personagem == p2.personagem)
                        gp.vitoria.setVencedor(p2.personagem, 1);
                    else
                        gp.vitoria.setVencedor(p2.personagem, 0);
                }
            }
        }
    }


    public void resetJogo() {
        musica = false;
        ended = false;
        vencedor = 0;
        endNow = 0;
        endStart = 0;
        p1.ganhou = false;
        p2.ganhou = false;
    }


    public void draw(Graphics g) {
        // FUNDO DA ARENA DE BATALHA
        g.drawImage(bg, 0, 0, 1200, 762, gp);

        // MENSAGEM DE VITÓRIA
        if(ended)
            if(vencedor == 1)
                g.drawImage(gp.vitoria.mensagem[p1.personagem], 357, 200, gp.vitoria.mensagem[p1.personagem].getWidth()*3, gp.vitoria.mensagem[p1.personagem].getHeight()*3, gp);
            else if(vencedor == 2)
                g.drawImage(gp.vitoria.mensagem[p2.personagem], 357, 200, gp.vitoria.mensagem[p2.personagem].getWidth()*3, gp.vitoria.mensagem[p2.personagem].getHeight()*3, gp);

        //HUD DE VIDA E NOME DOS LUTADORES
        // BARRAS DE VIDA
        g.drawImage(lifeBar, 20, 80, lifeBar.getWidth()*3, lifeBar.getHeight()*3, gp);
        g.drawImage(lifeBar, gp.getWidth() - lifeBar.getWidth()*3 - 20, 80, lifeBar.getWidth()*3, lifeBar.getHeight()*3, gp);
        // PARTE VERDE (REPRESENTA A QUANTIDADE DE VIDA
        g.setColor(new Color(0, 180, 0));
        g.fillRect(29, 89, (486*p1.vida)/120, lifeBar.getHeight()*3-18);
        g.fillRect(gp.getWidth() - 29 - (486 * p2.vida) / 120, 89, (486 * p2.vida) / 120, lifeBar.getHeight()*3-18);

        // NOME DOS PERSONAGENS NA BARRA DE VIDA
        g.drawImage(nomes_esq[p1.personagem], 60, 91, nomes_esq[0].getWidth()*3, nomes_esq[0].getHeight()*3, gp);
        g.drawImage(nomes_dir[p2.personagem], gp.getWidth() - nomes_dir[0].getWidth()*3 - 60, 91, nomes_dir[0].getWidth()*3, nomes_dir[0].getHeight()*3, gp);

        // BARRAS DE ESPECIAL
        g.drawImage(specialBar, 20, 150,  specialBar.getWidth()*3, specialBar.getHeight()*3, gp);
        g.drawImage(specialBar, gp.getWidth() - specialBar.getWidth()*3 - 20, 150,  specialBar.getWidth()*3, specialBar.getHeight()*3, gp);
        // PARTE AZUL
        g.setColor(new Color(0, 0, 180));
        if((p1.specialNow - p1.specialStart) < p1.specialCooldown){
            float quantEsp = (float)(p1.specialNow - p1.specialStart) / (float)p1.specialCooldown;
            g.fillRect(26, 156, (int)(((specialBar.getWidth()*3)-12)*quantEsp), specialBar.getHeight()*3-18);
        } else
            g.fillRect(26, 156, ((specialBar.getWidth()*3)-12),  specialBar.getHeight()*3-18);

        if((p2.specialNow - p2.specialStart) < p2.specialCooldown){
            float quantEsp = (float)(p2.specialNow - p2.specialStart) / (float)p2.specialCooldown;
            int widthEsp = (int)(((specialBar.getWidth()*3)-12)*quantEsp);
            g.fillRect((gp.getWidth()-specialBar.getWidth()*3-14)+(((specialBar.getWidth()*3)-12)-widthEsp), 156, widthEsp, specialBar.getHeight()*3-18);
        } else
            g.fillRect(gp.getWidth()-specialBar.getWidth()*3-14, 156, (specialBar.getWidth()*3)-12,  specialBar.getHeight()*3-18);

        // OS PERSONAGENS
        p1.draw(g);
        p2.draw(g);

        // DELIMITAÇÃO DA HURTBOX E HITBOX DE CADA PLAYER
//        g.setColor(new Color(0, 180, 0));
//        g.drawRect(p1.hurtbox.x, p1.hurtbox.y, p1.hurtbox.width, p1.hurtbox.height);
//        g.drawRect(p2.hurtbox.x, p2.hurtbox.y, p2.hurtbox.width, p2.hurtbox.height);
//        g.setColor(new Color(180, 0, 0));
//        if (p1.attack)
//            g.drawRect(p1.hitbox.x, p1.hitbox.y, p1.hitbox.width, p1.hitbox.height);
//        if (p2.attack)
//            g.drawRect(p2.hitbox.x, p2.hitbox.y, p2.hitbox.width, p2.hitbox.height);
    }

    public void keyPressed(KeyEvent e) {
        if(ended)return;
        p1.keyPressed(e);
        p2.keyPressed(e);

    }

    public void keyReleased(KeyEvent e) {
        p1.keyReleased(e);
        p2.keyReleased(e);

    }

    public void keyTyped(KeyEvent e) {
        if(ended)return;
        p1.keyTyped(e);
        p2.keyTyped(e);
    }

    public void setImages() {
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/props/arena_bg.png"));
            lifeBar = ImageIO.read(getClass().getResourceAsStream("/props/barra_de_vida.png"));
            specialBar = ImageIO.read(getClass().getResourceAsStream("/props/barra_de_especial.png"));
            nomes_esq = new BufferedImage[6];
            nomes_dir = new BufferedImage[6];
            for (int i = 0; i < nomes_esq.length; i++) {
                if(nomes_esq[i] == null)
                    nomes_esq[i] = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                if(nomes_dir[i] == null)
                    nomes_dir[i] = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}