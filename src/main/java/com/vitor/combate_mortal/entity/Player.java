package com.vitor.combate_mortal.entity;

import com.vitor.combate_mortal.inputs.Inputs;
import java.awt.Rectangle;
import com.vitor.combate_mortal.main.GamePanel;

public class Player {

    Inputs inp;
    GamePanel gp;
    public int x, y, width, height;
    public int chao = 640;

    // VARIAVEIS DE MOVIMENTAÇÃO
    public boolean up=false, down=false, left=false, right=false, jump=false, agachado=false,
            jumpPressed=false, movingPressed=false, movendo = false, ganhou=false;
    public float velocidade = 6.0f;

    // PULANDO / GRAVIDADE
    public float airSpeed= 0f, jumpSpeed = -18f, gravity = 0.5f;
    public boolean noAr;

    // VERIFICA SE O JOGADOR ESTÁ DO LADO ESQUERDO DA TELA (EM RELAÇÃO AO OUTRO PLAYER)
    public boolean isLeft;

    // REPRESENTA A PARTE TANGIVEL DO PLAYER
    public Rectangle hurtbox, hitbox;

    // INDEX DO PERSONAGEM (0=RAIDEN, 1=SUBZERO, 2=SCORPION, 3=MILEENA)
    public int personagem = 0, igual = 0;
    public Animation anim;

    // VARIAVEIS DE ATAQUE
    public boolean attack=false, punch=false, punch2=false, kick=false, kick2=false,
            punchPressed=false, kickPressed=false, atacado=false;
    long attackStart, attackNow, tempoAtaque, atacadoStart, atacadoNow, specialStart,
            specialNow, specialCooldown = 3000;
    public int tipoAtaque, vida = 120;
    public boolean hitConnectedThisAttack = false;

    // VARIÁVEIS DOS ESPECIAIS
    public boolean especial1=false, especial2=false;
    public int xProjetil, yProjetil, widthProjetil, heightProjetil, direcaoInicialEspecial;

    int gapAgachado;

    // CONSTRUTOR
    public Player(GamePanel gp, int x) {
        this.gp = gp;
        this.x = x;
        anim = new Animation(gp);
        hurtbox = new Rectangle();
        hitbox = new Rectangle();
    }

    public void selAnimations(int personagem, int igual) {
        // APLICANDO O TAMANHO DO PERSONAGEM SELECIONADO
        if(personagem == 3 || personagem == 4)
            height = 260;
        else
            height = 270;

        if(personagem == 3 || personagem == 4)
            gapAgachado = 40;
        else
            gapAgachado = 20;

        width = 100;
        this.personagem = personagem;
        this.igual = igual;
        vida = 120;
        anim.setValores(this);

        y = chao - height;
        hurtbox.setBounds(x, y, width, height);
    }

    public void endAttack() {
        atacado = false;
        attack = false;
        punch = false;
        punch2 = false;
        kick = false;
        kick2 = false;
        especial1 = false;
        especial2 = false;
        if(noAr) anim.estado = 1;
        else if(agachado) anim.estado = 2;
        else anim.estado =0;
    }

    public void updateAttack() {
        // ATAQUE
        if (attack) {
            attackNow = System.currentTimeMillis();
            if(personagem != 2)
                if(especial1) updateProjetil();
            if(personagem == 1 || personagem == 2)
                if(especial2) avancar();
            if(personagem == 2)
                if(especial1) avancar();

            criarHitBox();
            // INTERVALO DE ACORDO COM O TIPO DE ATAQUE PARA ATACAR NOVAMENTE
            if (attackNow - attackStart >= tempoAtaque){
                if(especial1 || especial2)
                    if(direcaoInicialEspecial == 0)
                        x -= 12;
                    else
                        x += 12;
                endAttack();// ENCERRA O ATAQUE, INDEPENDENTE DO TIPO
            }

        }
    }

    public void updateAtacado() {
        if (atacado) {
            atacadoNow = System.currentTimeMillis();
            // INTERVALO DE ACORDO COM O TIPO DE ATAQUE PARA ATACAR NOVAMENTE
            if (atacadoNow - atacadoStart >= 250)
                endAttack();// ENCERRA O ATAQUE, INDEPENDENTE DO TIPO
        }
    }

    // INICIA O ATAQUE
    public void initAttack(long tempo, int tipo) {

        specialNow = System.currentTimeMillis();

        if (tipo == 5 && specialNow - specialStart < specialCooldown) {
            return;
        }

        if (tipo == 6 && specialNow - specialStart < specialCooldown) {
            return;
        }

        attack = true;
        anim.estado = 0;
        anim.tick = 0;
        tempoAtaque = tempo;
        attackStart = System.currentTimeMillis();
        hitConnectedThisAttack = false;

        if(tipo == 1) punch = true;
        else if(tipo == 2) kick = true;
        else if(tipo == 3) punch2 = true;
        else if(tipo == 4) kick2 = true;
        else if(tipo == 5){
            anim.estadoAsset = 0;
            especial1 = true;
            if(isLeft) direcaoInicialEspecial = 0;
            else direcaoInicialEspecial = 1;
            if(personagem == 0){
                if(isLeft)
                    xProjetil = x+138+width;
                else xProjetil = x-138-140;
                yProjetil = y+54;
                widthProjetil = 140;
                heightProjetil = 54;
                anim.gapXProjetil = 30;
                anim.gapYProjetil = 24;
                anim.velAssets = 12;

            } else if(personagem == 1){
                if(isLeft)
                    xProjetil = x+100+width;
                else xProjetil = x-100-300;
                yProjetil = y+55;
                widthProjetil = 300;
                heightProjetil = 64;
                anim.gapXProjetil = 110;
                anim.gapYProjetil = 54;
                anim.velAssets = 10;

            }else if(personagem == 3){
                if(isLeft)
                    xProjetil = x-50+width;
                else xProjetil = x-50-212;
                yProjetil = y+54;
                widthProjetil = 212;
                heightProjetil = 52;
                anim.gapXProjetil = 24;
                anim.gapYProjetil = 80;
                anim.velAssets = 12;
            }
            specialStart = System.currentTimeMillis();
        } else if(tipo == 6){
            anim.estadoAsset = 0;
            especial2 = true;
            if(isLeft) direcaoInicialEspecial = 0;
            else direcaoInicialEspecial = 1;

            specialStart = System.currentTimeMillis();
        }

        tipoAtaque = tipo;
    }

    // INICIA O TEMPO DE ATACADO
    public void initAtacado() {
        atacado = true;
        anim.estado = 0;
        atacadoStart = System.currentTimeMillis();
    }

    public void updateProjetil(){
        if(direcaoInicialEspecial == 0)
            xProjetil += 8;
        else
            xProjetil -= 8;
    }

    public void avancar(){
        if(direcaoInicialEspecial == 0)
            x += 10;
        else
            x -= 10;
    }

    // CRIA AS HITBOX DE ACORDO COM O ATAQUE
    public void criarHitBox() {
        // ATAQUES EM PÉ
        // SOCO FRACO
        if(tipoAtaque == 1) {
            if(!noAr && !agachado) {
                // RAIDEN
                if(personagem == 0)
                    if(isLeft) hitbox.setBounds(x+width, y+12, 94, 44);
                    else hitbox.setBounds(x-94, y+12, 94, 44);
                    // MILEENA
                else if(personagem == 3)
                    if(isLeft) hitbox.setBounds(x+width, y+52, 100, 38);
                    else hitbox.setBounds(x-100, y+52, 100, 38);
                    // SUBZERO E SCORPION
                else
                if(isLeft) hitbox.setBounds(x+width, y+12, 92, 46);
                else hitbox.setBounds(x-92, y+12, 92, 46);
            }
            // CHUTE FRACO
        } else if (tipoAtaque == 2) {
            // RAIDEN
            if(!noAr && !agachado) {
                if(personagem == 0)
                    if(isLeft) hitbox.setBounds(x+width, y+72, 86, 40);
                    else hitbox.setBounds(x-86, y+72, 86, 40);
                    // MILEENA
                else if(personagem == 3)
                    if(isLeft) hitbox.setBounds(x+width, y+76, 56, 30);
                    else hitbox.setBounds(x-56, y+76, 56, 30);
                    // SUBZERO E SCORPION
                else
                if(isLeft) hitbox.setBounds(x+width, y+70, 90, 36);
                else hitbox.setBounds(x-90, y+70, 90, 36);
            }
            // SOCO FORTE
        } else if (tipoAtaque == 3) {
            if(!noAr && !agachado) {
                // RAIDEN
                if(personagem == 0)
                    if(anim.estado <= 1 || anim.estado >= 5)
                        if(isLeft) hitbox.setBounds(x+width, y+24, 86, 48);
                        else hitbox.setBounds(x-86, y+24, 86, 48);
                    else
                    if(isLeft) hitbox.setBounds(x+width, y-14, 52, 90);
                    else hitbox.setBounds(x-52, y-14, 52, 90);
                    // MILEENA
                else if(personagem == 3)
                    if(isLeft) hitbox.setBounds(x+width, y+10, 50, 46);
                    else hitbox.setBounds(x-50, y+10, 50, 46);
                    // SUBZERO E SCORPION
                else
                if(isLeft) hitbox.setBounds(x+width, y-16, 40, 110);
                else hitbox.setBounds(x-40, y-16, 40, 110);
            }
            // CHUTE FORTE
        } else if (tipoAtaque == 4) {
            if(!noAr && !agachado) {
                // RAIDEN
                if(personagem == 0)
                    if(anim.estado <= 1 || anim.estado >= 5)
                        if(isLeft) hitbox.setBounds(x+width, y+104, 50, 54);
                        else hitbox.setBounds(x-50, y+104, 50, 54);
                    else
                    if(isLeft) hitbox.setBounds(x+width, y-8, 80, 96);
                    else hitbox.setBounds(x-80, y-8, 80, 96);
                    // MILEENA
                else if(personagem == 3)
                    if(anim.estado >= 2 || anim.estado <= 4)
                        if(isLeft) hitbox.setBounds(x+width+40, y-10, 80, 70);
                        else hitbox.setBounds(x-80-40, y-10, 80, 70);
                    else hitbox.setBounds(0,0,0,0);
                    // SUBZERO E SCORPION
                else
                if(isLeft) hitbox.setBounds(x+width, y+14, 46, 104);
                else hitbox.setBounds(x-46, y+14, 46, 104);
            }
        }

        // ATAQUES AGACHADO E NO AR
        // SOCO
        if(tipoAtaque == 1 || tipoAtaque == 3) {
            // SOCO AGACHADO
            if(agachado) {
                // RAIDEN
                if(personagem == 0)
                    if(isLeft) hitbox.setBounds(x+width, y-18, 90, 40);
                    else hitbox.setBounds(x-90, y-18, 90, 40);
                    // MILEENA
                else if(personagem == 3)
                    if(isLeft) hitbox.setBounds(x+width, y+12, 52, 36);
                    else hitbox.setBounds(x-52, y+12, 52, 36);
                    // SUBZERO E SCORPION
                else
                if(isLeft) hitbox.setBounds(x+width, y-12, 94, 36);
                else hitbox.setBounds(x-94, y-12, 94, 36);

                // SOCO PULANDO
            } else if(noAr) {
                // RAIDEN
                if(personagem == 0)
                    if(isLeft) hitbox.setBounds(x+width, y+28, 136, 44);
                    else hitbox.setBounds(x-136, y+28, 136, 44);
                    // MILEENA
                else if(personagem == 3)
                    if(isLeft) hitbox.setBounds(x+width+60, y+64, 90, 40);
                    else hitbox.setBounds(x-90-60, y+64, 90, 40);
                    // SUBZERO E SCORPION
                else
                if(isLeft) hitbox.setBounds(x+width, y+3, 100, 56);
                else hitbox.setBounds(x-100, y+3, 100, 56);
            }
            // CHUTE
        } else if (tipoAtaque == 2 || tipoAtaque == 4) {
            // CHUTE AGACHADO
            if(agachado) {
                // RAIDEN
                if(personagem == 0)
                    if(anim.estado >= 4 || anim.estado <= 6)
                        if(isLeft) hitbox.setBounds(x+width, y-14, 84, 80);
                        else hitbox.setBounds(x-84, y-14, 84, 80);
                    else hitbox.setBounds(0,0,0,0);
                    // MILEENA
                else if(personagem == 3)
                    if(isLeft) hitbox.setBounds(x+width, y+34, 100, 40);
                    else hitbox.setBounds(x-100, y+34, 100, 40);
                    // SUBZERO E SCORPION
                else
                if(isLeft) hitbox.setBounds(x+width, y-26, 76, 110);
                else hitbox.setBounds(x-76, y-26, 76, 110);

                // CHUTE PULANDO
            } else if(noAr) {
                // RAIDEN
                if(personagem == 0)
                    if(isLeft) hitbox.setBounds(x+width, y+80, 68, 50);
                    else hitbox.setBounds(x-68, y+80, 68, 50);
                    // MILEENA
                else if(personagem == 3)
                    if(isLeft) hitbox.setBounds(x+width+26, y+80, 80, 52);
                    else hitbox.setBounds(x-80-26, y+80, 80, 52);
                    // SUBZERO E SCORPION
                else
                if(isLeft) hitbox.setBounds(x+width, y+64, 66, 50);
                else hitbox.setBounds(x-66, y+64, 66, 50);
            }
        }

        // ESPECIAIS 1 E 2
        if(especial1){
            if(personagem != 2)
                hitbox.setBounds(xProjetil, yProjetil, widthProjetil, heightProjetil);
            else
            if(isLeft) hitbox.setBounds(x+width, y+202, 86, 82);
            else hitbox.setBounds(x-86, y+202, 86, 82);
        } else if (especial2){
            if(personagem == 0)
                if(isLeft) hitbox.setBounds(x+width, y-40, 200, 230);
                else hitbox.setBounds(x-200, y-40, 200, 230);
            else if(personagem == 1 || personagem == 2)
                if(isLeft) hitbox.setBounds(x+width, y+208, 88, 60);
                else hitbox.setBounds(x-88, y+208, 88, 60);
            else if(personagem == 3)
                if(isLeft) hitbox.setBounds(x+width, y+38, 142, 48);
                else hitbox.setBounds(x-142, y+38, 142, 48);
        }
    }
}
