package com.vitor.combate_mortal.entity;

import com.vitor.combate_mortal.inputs.Inputs;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import com.vitor.combate_mortal.main.GamePanel;
import com.vitor.combate_mortal.main.Util;
import com.vitor.combate_mortal.states.Jogo;

public class Player {

    Inputs inp;
    GamePanel gp;
    public int x, y, width, height;
    public int chao = 640;

    // VARIAVEIS PARA DIFERENCIAR OS PLAYERS
    public Player playerComparacao;
    public int chaveEsquerda, chaveDireita, chaveCima, chaveBaixo, chaveSoco1, chaveSoco2, chaveChute1, chaveChute2, xInicial;

    // VARIAVEIS DE MOVIMENTAÇÃO
    public boolean up=false, down=false, left=false, right=false, jump=false, agachado=false,
            jumpPressed=false, movingPressed=false, movendo = false, ganhou=false;
    public float velocidade = 8.0f;

    // PULANDO / GRAVIDADE
    public float airSpeed= 0f, jumpSpeed = -22f, gravity = 0.8f;
    public boolean noAr;

    // VERIFICA SE O JOGADOR ESTÁ DO LADO ESQUERDO DA TELA (EM RELAÇÃO AO OUTRO PLAYER)
    public boolean isLeft;

    // REPRESENTA A PARTE TANGIVEL DO PLAYER
    public Rectangle hurtbox, hitbox;

    // INDEX DO PERSONAGEM (0=RAIDEN, 1=SUBZERO, 2=SCORPION, 3=MILEENA, 4=KITANA, 5=REPTILE)
    public int personagem = 0, igual = 0;
    public Animation anim;

    // VARIAVEIS DE ATAQUE
    public boolean attack=false, punch=false, punch2=false, kick=false, kick2=false,
            punchPressed=false, kickPressed=false,punch2Pressed=false, kick2Pressed=false, atacado=false;
    long attackStart, attackNow, tempoAtaque, atacadoStart, atacadoNow, tempoAtacado = 300;
    public int tipoAtaque, cooldownMax = 15, tempoCooldown = 0, vida = 180, maxVida = 180;
    public boolean hitConnectedThisAttack = false;

    // VARIÁVEIS DOS ESPECIAIS
    public boolean especial1=false, especial2=false;
    public int xProjetil, yProjetil, widthProjetil, heightProjetil, direcaoInicialEspecial;
    public long specialStart, specialNow, specialCooldown = 2800;
    // PARA AS COMBINAÇÕES DE BOTÕES
    int socoFracoBuffer = 0, socoForteBuffer = 0, chuteFracoBuffer = 0,
            chuteForteBuffer = 0, bufferMax = 2;

    int gapAgachado;

    // CONSTRUTOR
    public Player(GamePanel gp, int x, int esqueda, int direita, int cima, int baixo,
                  int soco1, int soco2, int chute1, int chute2) {
        this.gp = gp;
        this.xInicial = x;
        anim = new Animation(gp);
        hurtbox = new Rectangle();
        hitbox = new Rectangle();
        this.chaveEsquerda = esqueda;
        this.chaveDireita = direita;
        this.chaveCima = cima;
        this.chaveBaixo = baixo;
        this.chaveSoco1 = soco1;
        this.chaveSoco2 = soco2;
        this.chaveChute1 = chute1;
        this.chaveChute2 = chute2;
    }

    public void definirPlayerComparacao(Player player){
        this.playerComparacao = player;
    }

    public void resetPos(){
        x = xInicial;
    }

    public void selAnimations(int personagem, int igual) {
        // APLICANDO O TAMANHO DO PERSONAGEM SELECIONADO
        if(personagem == 3 || personagem == 4)
            height = 260;
        else
            height = 270;

        if(personagem == 3 || personagem == 4)
            gapAgachado = 30;
        else
            gapAgachado = 20;

        width = 100;
        this.personagem = personagem;
        this.igual = igual;
        vida = maxVida;
        anim.setValores(this);

        y = chao - height;
        hurtbox.setBounds(x, y, width, height);
    }

    // ATUALIZA O JOGADOR
    public void update() {
        specialNow = System.currentTimeMillis();

        // ATUALIZA O LADO DO JOGADOR
        if (x > playerComparacao.x) isLeft = false;
        else isLeft = true;

        // ATUALIZA PARA COMEÇAR OS ATAQUES
        updateComecoAtaque();

        // ATUALIZA O TEMPO DE ATACADO
        updateAtacado();

        // ATUALIZA O TEMPO DE ATAQUE
        if(attack)
            if(hurtbox.intersects(playerComparacao.hurtbox)){
                if(x>playerComparacao.x) x += 12;
                else if(x<playerComparacao.x) x -= 12;
            }
        updateAttack();

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
        if (!hurtbox.intersects(playerComparacao.hurtbox) && !atacado) {
            if (!agachado && !attack)
                updatePosX(xVelocidade);
            if (noAr && attack)
                updatePosX(xVelocidade);
        } else
            // VERIFICA COLISÃO VERTICAL (CASO ESTEJA NO AR)
            if (noAr)
                if (hurtbox.intersects(playerComparacao.hurtbox)) {
                    if (x < playerComparacao.x)
                        if (x >= 0)
                            x -= velocidade;
                        else {
                            x += velocidade;
                            if (x <= gp.getWidth() - width)
                                playerComparacao.x += velocidade;
                        }
                    else if (x <= gp.getWidth() - width)
                        x += velocidade;
                    else {
                        x -= velocidade;
                        if (x >= 0)
                            playerComparacao.x -= velocidade;
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

    // ATUALIZA PARA COMEÇAR OS ATAQUES
    public void updateComecoAtaque() {
        if(tempoCooldown == 0) {
            // ESPECIAIS
            if (socoFracoBuffer > 0 && socoForteBuffer > 0 && !attack && !atacado) {
                comecarEspecial1();
                socoForteBuffer = 0;
                socoFracoBuffer = 0;
            }
            if (chuteFracoBuffer > 0 && chuteForteBuffer > 0 && !attack && !atacado) {
                comecarEspecial2();
                chuteFracoBuffer = 0;
                chuteForteBuffer = 0;
            }

            // SOCOS
            if (socoFracoBuffer == 1)
                comecarSoco();
            if (socoForteBuffer == 1)
                comecarSoco2();
            // CHUTES
            if (chuteFracoBuffer == 1)
                comecarChute();
            if (chuteForteBuffer == 1)
                comecarChute2();

            // DECREMENTANDO
            if (socoFracoBuffer > 0)
                socoFracoBuffer--;
            if (socoForteBuffer > 0)
                socoForteBuffer--;
            if (chuteFracoBuffer > 0)
                chuteFracoBuffer--;
            if (chuteForteBuffer > 0)
                chuteForteBuffer--;
        } else {
            tempoCooldown--;
        }
    }

    // COMEÇAR ATAQUES
    public void comecarSoco() {
        if (!attack && !atacado)
            if(noAr && airSpeed < 8)
                initAttack(300, 1);
            else if (!noAr)
                initAttack(300, 1);
    }
    public void comecarSoco2() {
        if (!attack && !atacado)
            if(noAr && airSpeed < 8)
                initAttack(300, 3);
            else if(agachado)
                initAttack(300, 3);
            else if (!noAr)
                initAttack(400, 3);
    }
    public void comecarChute() {
        if (!attack && !atacado)
            if(noAr && airSpeed < 8)
                initAttack(300, 2);
            else if (!noAr)
                initAttack(400, 2);
    }
    public void comecarChute2() {
        if (!attack && !atacado)
            if(noAr && airSpeed < 8)
                initAttack(300, 4);
            else if(agachado)
                initAttack(400, 4);
            else if (!noAr)
                initAttack(500, 4);
    }
    public void comecarEspecial1() {
        if (!attack && !atacado && !noAr && !agachado)
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
    public void comecarEspecial2() {
        if (!attack && !atacado && !noAr && !agachado)
            initAttack(600, 6);
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
        tempoCooldown = cooldownMax;
    }

    public void updateAttack() {
        // ATAQUE
        if (attack) {
            attackNow = System.currentTimeMillis();
            if(personagem != 2)
                if(especial1) updateProjetil();
            if(personagem == 1 || personagem == 2 || personagem == 5)
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
            if (atacadoNow - atacadoStart >= tempoAtacado)
                endAttack();// ENCERRA O ATAQUE, INDEPENDENTE DO TIPO
        }
    }

    // INICIA O ATAQUE
    public void initAttack(long tempo, int tipo) {

        if (tipo == 5 && specialNow - specialStart < specialCooldown)
            return;
        if (tipo == 6 && specialNow - specialStart < specialCooldown)
            return;

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
                    xProjetil = x+60+width;
                else xProjetil = x-60-160;
                yProjetil = y+55;
                widthProjetil = 160;
                heightProjetil = 64;
                anim.gapXProjetil = 200;
                anim.gapYProjetil = 54;
                anim.velAssets = 10;

            }else if(personagem == 3 || personagem == 4){
                if(isLeft)
                    xProjetil = x-50+width;
                else xProjetil = x-50-212;
                yProjetil = y+54;
                widthProjetil = 212;
                heightProjetil = 52;
                anim.gapXProjetil = 24;
                anim.gapYProjetil = 80;
                anim.velAssets = 12;

            }else if(personagem == 5){
                if(isLeft)
                    xProjetil = x+(96);
                else xProjetil = x-(96);
                yProjetil = y+(31);
                widthProjetil = 106*2;
                heightProjetil = 7*3;
                anim.gapXProjetil = 9;
                anim.gapYProjetil = 28*2;
                anim.velAssets = 16;
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
        Util.play("ataque");
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
                    // MILEENA E KITANA
                else if(personagem == 3 || personagem == 4)
                    if(isLeft) hitbox.setBounds(x+width, y+52, 100, 38);
                    else hitbox.setBounds(x-100, y+52, 100, 38);
                    // SUBZERO, SCORPION E REPTILE
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
                    // MILEENA E KITANA
                else if(personagem == 3 || personagem == 4)
                    if(isLeft) hitbox.setBounds(x+width, y+76, 56, 30);
                    else hitbox.setBounds(x-56, y+76, 56, 30);
                    // SUBZERO, SCORPION E REPTILE
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
                    // MILEENA E KITANA
                else if(personagem == 3 || personagem == 4)
                    if(isLeft) hitbox.setBounds(x+width, y+10, 50, 46);
                    else hitbox.setBounds(x-50, y+10, 50, 46);
                    // SUBZERO, SCORPION E REPTILE
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
                    // MILEENA E KITANA
                else if(personagem == 3 || personagem == 4)
                    if(anim.estado >= 2 || anim.estado <= 4)
                        if(isLeft) hitbox.setBounds(x+width+40, y-10, 80, 70);
                        else hitbox.setBounds(x-80-40, y-10, 80, 70);
                    else hitbox.setBounds(0,0,0,0);
                    // SUBZERO, SCORPION E REPTILE
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
                    // MILEENA E KITANA
                else if(personagem == 3 || personagem == 4)
                    if(isLeft) hitbox.setBounds(x+width, y+12, 52, 36);
                    else hitbox.setBounds(x-52, y+12, 52, 36);
                    // SUBZERO, SCORPION E REPTILE
                else
                    if(isLeft) hitbox.setBounds(x+width, y-12, 94, 36);
                    else hitbox.setBounds(x-94, y-12, 94, 36);

                // SOCO PULANDO
            } else if(noAr) {
                // RAIDEN
                if(personagem == 0)
                    if(isLeft) hitbox.setBounds(x+width, y+28, 136, 44);
                    else hitbox.setBounds(x-136, y+28, 136, 44);
                    // MILEENA E KITANA
                else if(personagem == 3 || personagem == 4)
                    if(isLeft) hitbox.setBounds(x+width+60, y+64, 90, 40);
                    else hitbox.setBounds(x-90-60, y+64, 90, 40);
                    // SUBZERO, SCORPION E REPTILE
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
                    // MILEENA E KITANA
                else if(personagem == 3 || personagem == 4)
                    if(isLeft) hitbox.setBounds(x+width, y+34, 100, 40);
                    else hitbox.setBounds(x-100, y+34, 100, 40);
                    // SUBZERO, SCORPION E REPTILE
                else
                    if(isLeft) hitbox.setBounds(x+width, y-26, 76, 110);
                    else hitbox.setBounds(x-76, y-26, 76, 110);

                // CHUTE PULANDO
            } else if(noAr) {
                // RAIDEN
                if(personagem == 0)
                    if(isLeft) hitbox.setBounds(x+width, y+80, 68, 50);
                    else hitbox.setBounds(x-68, y+80, 68, 50);
                    // MILEENA E KITANA
                else if(personagem == 3 || personagem == 4)
                    if(isLeft) hitbox.setBounds(x+width+26, y+80, 80, 52);
                    else hitbox.setBounds(x-80-26, y+80, 80, 52);
                    // SUBZERO, SCORPION E REPTILE
                else
                    if(isLeft) hitbox.setBounds(x+width, y+64, 66, 50);
                    else hitbox.setBounds(x-66, y+64, 66, 50);
            }
        }

        // ESPECIAIS 1 E 2
        if(especial1) {
            if(personagem != 2)
                hitbox.setBounds(xProjetil, yProjetil, widthProjetil, heightProjetil);
            else
                if(isLeft) hitbox.setBounds(x+width, y+202, 86, 82);
                else hitbox.setBounds(x-86, y+202, 86, 82);

        } else if (especial2) {
            if(personagem == 0)
                if(isLeft) hitbox.setBounds(x+width, y-40, 200, 230);
                else hitbox.setBounds(x-200, y-40, 200, 230);
            else if(personagem == 1 || personagem == 2 || personagem == 5)
                if(isLeft) hitbox.setBounds(x+width, y+208, 88, 60);
                else hitbox.setBounds(x-88, y+208, 88, 60);
            else if(personagem == 3  || personagem == 4)
                if(isLeft) hitbox.setBounds(x+width, y+38, 142, 48);
                else hitbox.setBounds(x-142, y+38, 142, 48);
        }
    }

    // INPUTS
    public void keyPressed(KeyEvent e) {
        // INPUTS DE MOVIMENTAÇÃO
        if (e.getKeyCode() == chaveCima) {
            if (!jumpPressed) {
                jump = true;
                jumpPressed = true;
            }
        }
        if (e.getKeyCode() == chaveEsquerda) {
            left = true;
            if (!movingPressed) {
                movingPressed = true;
                if (!noAr && !agachado && !attack)
                    anim.estado = 0;
            }
        }
        if (e.getKeyCode() == chaveBaixo)
            if (!noAr)
                down = true;
        if (e.getKeyCode() == chaveDireita) {
            right = true;
            if (!movingPressed) {
                movingPressed = true;
                if (!noAr && !agachado && !attack)
                    anim.estado = 0;
            }
        }

        // INPUTS DE ATAQUE
        // SOCO FRACO
        if (e.getKeyCode() == chaveSoco1){
            if(socoFracoBuffer == 0 && !punchPressed){
                punchPressed = true;
                if(!attack && !atacado)
                    socoFracoBuffer = bufferMax;
            }
        }

        // SOCO FORTE
        if (e.getKeyCode() == chaveSoco2){
            if(socoForteBuffer == 0 && !punch2Pressed){
                punch2Pressed = true;
                if(!attack && !atacado)
                    socoForteBuffer = bufferMax;
            }
        }

        // CHUTE FRACO
        if (e.getKeyCode() == chaveChute1){
            if(chuteFracoBuffer == 0 && !kickPressed){
                kickPressed = true;
                if(!attack && !atacado)
                    chuteFracoBuffer = bufferMax;
            }
        }

        // CHUTE FORTE
        if (e.getKeyCode() == chaveChute2) {
            if(chuteForteBuffer == 0 && !kick2Pressed){
                kick2Pressed = true;
                if(!attack && !atacado)
                    chuteForteBuffer = bufferMax;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        // INPUTS DE MOVIMENTAÇÃO
        if (e.getKeyCode() == chaveCima) jumpPressed = false;
        if (e.getKeyCode() == chaveEsquerda) {
            left = false;
            movingPressed = false;
        }
        if (e.getKeyCode() == chaveBaixo) down = false;
        if (e.getKeyCode() == chaveDireita) {
            right = false;
            movingPressed = false;
        }

        // INPUTS DE ATAQUE
        if (e.getKeyCode() == chaveSoco1)
            punchPressed = false;
        if (e.getKeyCode() == chaveSoco2)
            punch2Pressed = false;
        if (e.getKeyCode() == chaveChute1)
            kickPressed = false;
        if (e.getKeyCode() == chaveChute2)
            kick2Pressed = false;
    }

    public void keyTyped(KeyEvent e) {}
}
