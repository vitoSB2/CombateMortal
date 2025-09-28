package com.vitor.combate_mortal.entity;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import com.vitor.combate_mortal.main.GamePanel;
import com.vitor.combate_mortal.main.Util;
import com.vitor.combate_mortal.states.Jogo;

public class Player1 extends Player implements PlayerMethods {

    // CONSTRUTOR
    public Player1(GamePanel gp, int x) {
        super(gp, x);
        // P1 COMEÇA DO LADO ESQUERDO
        isLeft = true;
    }

    // ATUALIZA O JOGADOR
    public void update() {
        specialNow = System.currentTimeMillis();

        // ATUALIZA O LADO DO JOGADOR
        if (x > Jogo.p2.x) isLeft = false;
        else isLeft = true;

        // ATUALIZA O TEMPO DE ATACADO
        updateAtacado();

        // ATUALIZA O TEMPO DE ATAQUE
        updateAttack();
        if(attack)
            if(hurtbox.intersects(Jogo.p2.hurtbox)){
                if(x>Jogo.p2.x) x += 12;
                else if(x<Jogo.p2.x) x -= 12;
            }

        // ATUALIZA A POSIÇÃO
        updatePos();

        // ATUALIZANDO OS VALORES DA HURTBOX
        hurtbox.setBounds(x, y, width, height);
    }


    // PARTE GRAFICA
    public void draw(Graphics g) {

        // DO LANDO ESQUERDO (DESENHA A IMAGEM NORMALMENTE)
        if (isLeft) {
            if (noAr)
                g.drawImage(anim.getSprite(), (hurtbox.x) - anim.gapX, (hurtbox.y) - anim.gapY - height,
                        anim.spriteWidth * 2, anim.spriteHeight * 2, null);
            else if(agachado)
                g.drawImage(anim.getSprite(), (hurtbox.x) - anim.gapX, (hurtbox.y) - anim.gapY - height + (gapAgachado*2),
                        anim.spriteWidth * 2, anim.spriteHeight * 2, null);
            else
                g.drawImage(anim.getSprite(), (hurtbox.x) - anim.gapX, (hurtbox.y) - anim.gapY, anim.spriteWidth * 2,
                        anim.spriteHeight * 2, null);

            if(personagem != 2){
                if(especial1){
                    g.drawImage(anim.getSpriteAsset(), xProjetil-anim.gapXProjetil, yProjetil-anim.gapYProjetil,
                            anim.getSpriteAsset().getWidth()*2, anim.getSpriteAsset().getHeight()*2, null);
                } else if(especial2 && personagem == 0){
                    g.drawImage(anim.getSpriteAsset(), (int)hitbox.getX(), (int)hitbox.getY(),
                            (int)hitbox.getWidth(), (int)hitbox.getHeight(), null);
                }
            }

            // DO LANDO DIREITO (DESENHA A IMAGEM INVERTIDA)
        } else {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform transform = new AffineTransform();

            // INVERTE HORIZONTALMENTE
            transform.scale(-1, 1);
            // AJUSTA A POSIÇÃO
            if (noAr)
                transform.translate(-((hurtbox.x) + width + anim.gapX), (hurtbox.y) - anim.gapY - height);
            else if(agachado)
                transform.translate(-((hurtbox.x) + width + anim.gapX), (hurtbox.y) - anim.gapY - height  + (gapAgachado*2));
            else
                transform.translate(-((hurtbox.x) + width + anim.gapX), (hurtbox.y) - anim.gapY);
            // DEFINE A ESCALA
            transform.scale(2, 2);
            // DESENHA A IMAGEM INVERTIDA
            g2d.drawImage(anim.getSprite(), transform, null);

            if(personagem != 2){
                AffineTransform projTransform = new AffineTransform();
                if(especial1){
                    projTransform.translate(xProjetil+(int)hitbox.getWidth()+anim.gapXProjetil, yProjetil - anim.gapYProjetil);
                    projTransform.scale(-2, 2);
                    g2d.drawImage(anim.getSpriteAsset(), projTransform, null);
                } else if(especial2 && personagem == 0){
                    projTransform.translate((int)hitbox.getX()+(int)hitbox.getWidth(), (int)hitbox.getY());
                    projTransform.scale(-2, 2);
                    g2d.drawImage(anim.getSpriteAsset(), projTransform, null);
                }
            }
        }
    }

    // MOVIMENTAÇÃO
    public void updatePos() {
        movendo = false;

        // PULO
        if (jump && !agachado)
            jump();

        // AGACHAR
        if (!noAr && !attack) {
            if (down) {
                if (!agachado) {
                    anim.estado = 0;
                    height = (height / 2)+gapAgachado;
                    y = chao-height;
                }
                agachado = true;
            } else {
                if (agachado) {
                    height = (height-gapAgachado)* 2;
                    y = chao-height;
                }
                agachado = false;
            }
        }

        // CASO O JOGADOR NÃO ESTEJA PRESSIONANDO NENHUM BOTÃO DE MOVIMENTAÇÃO
        if (!noAr) {
            if (!left && !right)
                return;
            // OU OS DOIS AO MESMO TEMPO
            if (left && right)
                return;
            // OU ESTEJA AGACHADO
            if (agachado)
                return;
        }

        // MOVIMENTAÇÃO HORIZONTAL
        float xVelocidade = 0f; // VELOCIDADE DA MOVIMENTAÇÃO HORIZONTAL
        if (left)
            if (x >= 0)
                xVelocidade -= velocidade;
        if (right)
            if (x <= gp.getWidth() - width)
                xVelocidade += velocidade;

        // MOVIMENTAÇÃO VERTICAL (PULO E GRAVIDADE)
        if (noAr) {
            y += airSpeed;
            airSpeed += gravity;

            // VERFICA SE ATINGIU O CHÃO
            if (y >= chao - height) {
                y = chao - height;
                noAr = false;
                jump = false;
                airSpeed = 0f;
                y = y - height;
                height = height * 2;
                anim.estado = 0;
            }
        }

        // VERIFICA COLISÃO HORIZONTAL
        hurtbox.setBounds(x + (int) xVelocidade + 1, y, width, height);
        if (!hurtbox.intersects(Jogo.p2.hurtbox) && !atacado) {
            if (!agachado && !attack)
                updatePosX(xVelocidade);
            if (noAr && attack)
                updatePosX(xVelocidade);
        } else
            // VERIFICA COLISÃO VERTICAL (CASO ESTEJA NO AR)
            if (noAr)
                if (hurtbox.intersects(Jogo.p2.hurtbox)) {
                    if (x < Jogo.p2.x)
                        if (x >= 0)
                            x -= velocidade;
                        else {
                            x += velocidade;
                            if (x <= gp.getWidth() - width)
                                Jogo.p2.x += velocidade;
                        }
                    else if (x <= gp.getWidth() - width)
                        x += velocidade;
                    else {
                        x -= velocidade;
                        if (x >= 0)
                            Jogo.p2.x -= velocidade;
                    }
                }
    }

    // FUNÇÃO QUE ATUALIZA A POSIÇÃO HORIZONTAL DO JOGADOR
    private void updatePosX(float xVelocidade) {
        x += xVelocidade;
        movendo = true;
    }

    // FUNÇÃO QUE INICIA O PULO (CASO O JOGADOR NÃO ESTEJA NO AR)
    private void jump() {
        if (noAr || attack)
            return;

        anim.estado = 0;
        noAr = true;
        airSpeed = jumpSpeed;
        height = height / 2;
        y += height;
    }

    // INPUTS
    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_R || e.getKeyCode() == KeyEvent.VK_T ||
                e.getKeyCode() == KeyEvent.VK_F || e.getKeyCode() == KeyEvent.VK_G)
                && !atacado && !attack)
            Util.play("ataque");

        // INPUTS DE MOVIMENTAÇÃO
        if (e.getKeyCode() == KeyEvent.VK_W) {
            if (!jumpPressed) {
                jump = true;
                jumpPressed = true;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            left = true;
            if (!movingPressed) {
                movingPressed = true;
                if (!noAr && !agachado && !attack)
                    anim.estado = 0;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_S)
            if (!noAr)
                down = true;
        if (e.getKeyCode() == KeyEvent.VK_D) {
            right = true;
            if (!movingPressed) {
                movingPressed = true;
                if (!noAr && !agachado && !attack)
                    anim.estado = 0;
            }
        }

        // INPUTS DE ATAQUE
        // SOCO FRACO
        if (e.getKeyCode() == KeyEvent.VK_R){
            if (!attack && !punchPressed && !atacado)
                if(noAr && airSpeed < 8)
                    initAttack(300, 1);
                else if (!noAr)
                    initAttack(300, 1);
            punchPressed = true;
        }

        // SOCO FORTE
        if (e.getKeyCode() == KeyEvent.VK_T){
            if (!attack && !punchPressed && !atacado)
                if(noAr && airSpeed < 8)
                    initAttack(300, 3);
                else if(agachado)
                    initAttack(300, 3);
                else if (!noAr)
                    initAttack(400, 3);
            punchPressed = true;
        }

        // CHUTE FRACO
        if (e.getKeyCode() == KeyEvent.VK_F){
            if (!attack && !kickPressed && !atacado)
                if(noAr && airSpeed < 8)
                    initAttack(300, 2);
                else if (!noAr)
                    initAttack(400, 2);
            kickPressed = true;
        }

        // CHUTE FORTE
        if (e.getKeyCode() == KeyEvent.VK_G) {
            if (!attack && !kickPressed && !atacado)
                if(noAr && airSpeed < 8)
                    initAttack(300, 4);
                else if(agachado)
                    initAttack(400, 4);
                else if (!noAr)
                    initAttack(500, 4);
            kickPressed = true;
        }

        // TESTE ESPECIAL
        if (e.getKeyCode() == KeyEvent.VK_Y){
            if (!attack && !atacado && !noAr && !agachado){
                if(personagem == 0)
                    initAttack(800, 5);
                else if(personagem == 1)
                    initAttack(500, 5);
                else if(personagem == 2)
                    initAttack(600, 5);
                else if(personagem == 3 || personagem == 4)
                    initAttack(1000, 5);
                else if(personagem == 5)
                    initAttack(1000, 5);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_H){
            if (!attack && !atacado && !noAr && !agachado){
                initAttack(600, 6);
            }
        }

    }

    public void keyReleased(KeyEvent e) {
        // INPUTS DE MOVIMENTAÇÃO
        if (e.getKeyCode() == KeyEvent.VK_W) jumpPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_A) {
            left = false;
            movingPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) down = false;
        if (e.getKeyCode() == KeyEvent.VK_D) {
            right = false;
            movingPressed = false;
        }

        // INPUTS DE ATAQUE
        if (e.getKeyCode() == KeyEvent.VK_R)
            punchPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_T)
            punchPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_F)
            kickPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_G)
            kickPressed = false;
    }

    public void keyTyped(KeyEvent e) {

    }

    public void resetPos(){
        x = 250;
    }

}