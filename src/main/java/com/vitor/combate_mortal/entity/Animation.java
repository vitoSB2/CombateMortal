package com.vitor.combate_mortal.entity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.vitor.combate_mortal.main.GamePanel;

public class Animation {

    GamePanel gp;
    public BufferedImage[][] sprites, assets;
    public int indexParado, indexAndando, indexDefesa, indexPulando, indexAgachado, indexSoco, indexSoco2,
            indexChute, indexChute2, indexSocoAgachado, indexChuteAgachado, indexSocoPulando, indexChutePulando,
            indexAtacado, indexAtacadoAgachado, indexAtacadoPulando, indexAsset1, indexAsset2, indexAsset3,
            indexEspecial1, indexEspecial2, indexQueda, indexVitoria;
    public Player player;
    public int spriteHeight, spriteWidth, gapX, gapY, estado=0, estadoMax=5, velSprites=5, tick = 0,
            estadoAsset = 0, estadoMaxAsset = 0, tickAsset = 0, velAssets = 12, gapXProjetil, gapYProjetil;

    public Animation(GamePanel gp){
        this.gp = gp;
    }

    public void setValores(Player player){
        this.player = player;
        spriteHeight = 155;
        if(player.personagem == 3 || player.personagem == 4) spriteWidth = 155;
        else spriteWidth = 176;
        importAtlas();
    }

    public BufferedImage getSprite() {
        // AUMENTA O TICK (A CADA 4 TICK O ESTADO MUDA)
        tick++;

        // DEFINE O MAX DE ESTADO DE ACORDO COM A SITUAÇÃO
        if(player.noAr) {
            if(player.punch || player.punch2) estadoMax = indexSocoPulando;
            else if(player.kick || player.kick2) estadoMax = indexChutePulando;
            else if(player.atacado) estadoMax = indexAtacadoPulando;
            else estadoMax = indexPulando;

        } else if(player.agachado) {
            if(player.punch || player.punch2) estadoMax = indexSocoAgachado;
            else if(player.kick || player.kick2) estadoMax = indexChuteAgachado;
            else if(player.atacado) estadoMax = indexAtacadoAgachado;
            else estadoMax = indexAgachado;

        } else if(player.movendo) {
            estadoMax = indexAndando;

        } else {
            if(player.punch) estadoMax = indexSoco;
            else if(player.kick) estadoMax = indexChute;
            else if(player.punch2) estadoMax = indexSoco2;
            else if(player.kick2) estadoMax = indexChute2;
            else if(player.especial1) estadoMax = indexEspecial1;
            else if(player.especial2) estadoMax = indexEspecial2;
            else if(player.atacado) estadoMax = indexAtacado;
            else estadoMax = indexParado;
        }
        if(gp.jogo.ended && !player.noAr)
            if(player.ganhou) estadoMax = indexVitoria;
            else estadoMax = indexQueda;

        // CONDIÇÃO DE MUDANÇA DE ESTADO
        if(tick >= velSprites) {
            estado++;
            if(estado >= estadoMax) {
                if(player.noAr) {
                    if(player.punch || player.punch) estado = 4;
                    else if(player.kick || player.kick2) estado = 4;
                    else if(player.atacado) estado = 2;
                    else estado = 1;
                }
                else if(player.agachado) {
                    if(player.punch || player.punch2) estado = 4;
                    else if(player.kick || player.kick2) estado = 8;
                    else if(player.atacado) estado = 4;
                    else estado = 2;
                }
                else if(player.punch) estado = 4;
                else if(player.kick) estado = 6;
                else if(player.punch2) estado = indexSoco2-1;
                else if(player.kick2) estado = indexChute2-1;
                else if(player.especial1) estado = indexEspecial1-1;
                else if(player.especial2) estado = indexEspecial2-1;
                else if(player.atacado) estado = 4;
                else estado = 0;

                if(gp.jogo.ended && !player.noAr)
                    if(player.personagem == 0)
                        if(player.ganhou) estado = indexVitoria-2;
                        else estado = indexQueda-1;
                    else
                    if(player.ganhou) estado = indexVitoria-1;
                    else estado = indexQueda-1;
            }
            tick = 0;
        }

        // RETORNA O SPRTE DE ACORDO COM A SITUAÇÃO E O ESTADO DO PLAYER
        if(gp.jogo.ended && !player.noAr)
            if(player.ganhou) return sprites[23][estado];
            else return sprites[19][estado];

        if(player.noAr) {
            if(player.punch || player.punch2) return sprites[16][estado];
            else if(player.kick || player.kick2) return sprites[17][estado];
            else if(player.atacado) return sprites[18][estado];
            else return sprites[15][estado];

        } else if(player.agachado) {
            if(player.punch || player.punch2) return sprites[10][estado];
            else if(player.kick || player.kick2) return sprites[12][estado];
            else if(player.atacado) return sprites[14][estado];
            else return sprites[8][estado];

        } else if(player.movendo) {
            return sprites[1][estado];

        } else {
            if (player.punch) return sprites[3][estado];
            else if (player.kick) return sprites[5][estado];
            else if (player.punch2) return sprites[4][estado];
            else if (player.kick2) return sprites[6][estado];
            else if(player.especial1) return sprites[21][estado];
            else if(player.especial2) return sprites[22][estado];
            else if(player.atacado) return sprites[7][estado];
            else return sprites[0][estado];
        }
    }

    public BufferedImage getSpriteAsset(){
        tickAsset++;
        // DEFINE O MAX DE ESTADO DE ACORDO COM A SITUAÇÃO
        if(player.personagem == 0){
            if(player.especial1) estadoMaxAsset = indexAsset2;
            if(player.especial2) estadoMaxAsset = indexAsset3;
        } else if (player.personagem == 1 || player.personagem > 2)
            if(player.especial1) estadoMaxAsset = indexAsset1;

        if(!player.isLeft) velAssets = velAssets-1;
        // CONDIÇÃO DE MUDANÇA DE ESTADO
        if(tickAsset >= velAssets) {
            estadoAsset++;
            if(estadoAsset >= estadoMaxAsset){
                if(player.personagem == 0){
                    if(player.especial1) estadoAsset = indexAsset2-3;
                    if(player.especial2) estadoAsset = indexAsset3-1;
                } else if (player.personagem == 1){
                    if(player.especial1) estadoAsset = indexAsset1-2;
                } else if (player.personagem == 3 || player.personagem == 4){
                    if(player.especial1) estadoAsset = indexAsset1-3;
                }

            }
            tickAsset = 0;
        }
        // RETORNA O SPRTE DE ACORDO COM A SITUAÇÃO E O ESTADO DO PLAYER
        if(player.personagem == 0){
            if(player.especial1) return assets[1][estadoAsset];
            if(player.especial2) return assets[2][estadoAsset];
        } else if (player.personagem == 1 || player.personagem > 2)
            if(player.especial1) return assets[0][estadoAsset];

        return assets[0][0];
    }

    // IMPORTA O ATLAS DE SPRITES DO PERSONAGEM SELECIONADO
    public void importAtlas() {
        // IMPORTANDO A IMAGEM
        BufferedImage atlas = null;
        try {
            if (player.igual == 1 && !player.isLeft) {
                if(player.personagem == 0)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/raiden_atlas_alt.png"));
                else if(player.personagem == 1)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/subzero_atlas_alt.png"));
                else if(player.personagem == 2)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/scorpion_atlas_alt.png"));
                else if(player.personagem == 3)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/mileena_atlas_alt.png"));
                else if(player.personagem == 4)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/kitana_atlas_alt.png"));
                else if(player.personagem == 5)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/reptile_atlas_alt.png"));
            } else {
                if(player.personagem == 0)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/raiden_atlas.png"));
                else if(player.personagem == 1)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/subzero_atlas.png"));
                else if(player.personagem == 2)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/scorpion_atlas.png"));
                else if(player.personagem == 3)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/mileena_atlas.png"));
                else if(player.personagem == 4)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/kitana_atlas.png"));
                else if(player.personagem == 5)
                    atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/reptile_atlas.png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (atlas == null) {
            throw new IllegalStateException("Atlas não carregado para personagem: " + player.personagem + " igual: " + player.igual);
        }

        // APLICANDO A QUANTIDADE DE SPRITES PARA CADA ANIMAÇÃO DE ACORDO COM O PERSONAGEM
        switch(player.personagem) {
            case 0:
                gapX = 80;
                gapY = 20;
                indexParado = 6;
                indexEspecial1 = 4;
                indexEspecial2 = 4;
                indexVitoria = 8;
                break;
            case 1:
                gapX = 94;
                gapY = 20;
                indexParado = 7;
                indexChute2 = 8;
                indexEspecial1 = 4;
                indexEspecial2 = 3;
                indexVitoria = 3;
                break;
            case 2:
                gapX = 94;
                gapY = 20;
                indexParado = 7;
                indexChute2 = 8;
                indexEspecial1 = 6;
                indexEspecial2 = 3;
                indexVitoria = 3;
                break;
            case 3:
                gapX = 50;
                gapY = 26;
                indexParado = 7;
                indexChute2 = 6;
                indexEspecial1 = 4;
                indexEspecial2 = 6;
                indexVitoria = 12;
                break;
            case 4:
                gapX = 50;
                gapY = 26;
                indexParado = 7;
                indexChute2 = 6;
                indexEspecial1 = 4;
                indexEspecial2 = 6;
                indexVitoria = 11;
                break;
            case 5:
                gapX = 94;
                gapY = 20;
                indexParado = 7;
                indexChute2 = 8;
                indexEspecial1 = 3;
                indexEspecial2 = 3;
                indexVitoria = 4;
                break;
        }

        // APLICANDO A QUANTIDADE GENÉRICA DE SPRITES PARA CADA ANIMAÇÃO (IGUAIS PARA TODOS OS PERSONAGENS)
        indexAndando = 8;
        indexPulando = 8;
        indexAgachado = 3;
        indexAtacadoPulando = 2;
        indexQueda = 6;

        // CRIANDO O ARRAY DE SPRITES
        int max;
        if(player.personagem == 3) max = 12;
        else if(player.personagem == 4) max = 11;
        else max = 8;
        sprites = new BufferedImage[24][12];

        // COLOCANDO AS SPRITES NO ARRAY
        for(int i=0; i<24; i++)
            for(int j=0; j<max; j++)
                sprites[i][j] = atlas.getSubimage(j*spriteWidth, i*spriteHeight, spriteWidth, spriteHeight);


        // COLOCANDO AS SPRITES QUE VÃO E VOLTAM NO ARRAY
        //PARADO
        if(player.personagem != 2 && player.personagem != 4 && player.personagem != 5) {
            for(int i=1; i<indexParado-1; i++)
                sprites[0][indexParado + i-1] = sprites[0][indexParado - i-1];

            indexParado += indexParado-2;
        }

        //SOCO FRACO
        sprites[3][3] = sprites[3][1];
        sprites[3][4] = sprites[3][0];
        indexSoco = 5;

        // CHUTE FRACO
        sprites[5][4] = sprites[5][2];
        sprites[5][5] = sprites[5][1];
        sprites[5][6] = sprites[5][0];
        indexChute = 7;

        // SOCO AGACHADO
        sprites[10][3] = sprites[10][1];
        sprites[10][4] = sprites[10][0];
        indexSocoAgachado = 5;

        // CHUTE AGACHADO
        sprites[12][5] = sprites[12][3];
        sprites[12][6] = sprites[12][2];
        sprites[12][7] = sprites[12][1];
        sprites[12][8] = sprites[12][0];
        indexChuteAgachado = 9;

        // SOCO PULANDO
        sprites[16][3] = sprites[16][1];
        sprites[16][4] = sprites[16][0];
        indexSocoPulando = 5;

        // CHUTE PULANDO
        sprites[17][3] = sprites[17][1];
        sprites[17][4] = sprites[17][0];
        indexChutePulando = 5;

        // SOCO FORTE
        if(player.personagem == 3 || player.personagem == 4) {
            sprites[4][3] = sprites[4][1];
            sprites[4][4] = sprites[4][0];
            indexSoco2 = 5;
        } else {
            sprites[4][4] = sprites[4][2];
            sprites[4][5] = sprites[4][1];
            sprites[4][6] = sprites[4][0];
            indexSoco2 = 7;
        }

        // CHUTE FORTE
        if(player.personagem == 0) {
            sprites[6][4] = sprites[6][2];
            sprites[6][5] = sprites[6][1];
            sprites[6][6] = sprites[6][0];
            indexChute2 = 7;
        }

        // ATACADO
        sprites[7][3] = sprites[7][1];
        sprites[7][4] = sprites[7][0];
        indexAtacado = 5;

        // ATACADO AGACHADO
        sprites[14][3] = sprites[14][1];
        sprites[14][4] = sprites[14][0];
        indexAtacadoAgachado = 5;

        // IMPORTANDO A IMAGEM DOS ASSETS (RAIOS, FACAS, GELO)
        try {
            if(player.personagem == 0)
                atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/raiden_raios.png"));
            else if(player.personagem == 1)
                atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/subzero_gelo.png"));
            else if(player.personagem == 3 || player.personagem == 4)
                atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/mileena_facas.png"));
            else if(player.personagem == 5)
                atlas = ImageIO.read(getClass().getResourceAsStream("/spritesAtlas/reptile_cuspe.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(player.personagem == 0){
            assets = new BufferedImage[3][8];
            for(int i=0; i<6; i++){
                assets[0][i] = atlas.getSubimage(i*238, 0, 238, 93);
                assets[2][i] = atlas.getSubimage(i*100, 143, 100, 115);
            }
            for(int i=0; i<8; i++)
                assets[1][i] = atlas.getSubimage(i*100, 93, 100, 50);
            indexAsset1 = 6;
            indexAsset2 = 8;
            indexAsset3 = 6;
            sprites[21][4] = sprites[20][0];
            sprites[21][5] = sprites[20][1];
            sprites[21][6] = sprites[20][2];
            sprites[21][7] = sprites[20][3];
            sprites[21][8] = sprites[20][4];
            indexEspecial1 = 9;
        } else if(player.personagem == 1){
            assets = new BufferedImage[1][12];
            for(int i=0; i<12; i++)
                assets[0][i] = atlas.getSubimage(i*212, 0, 212, 86);
            indexAsset1 = 7;
        } else if(player.personagem == 3 || player.personagem == 4){
            assets = new BufferedImage[1][5];
            for(int i=0; i<5; i++)
                assets[0][i] = atlas.getSubimage(i*118, 0, 118, 106);
            indexAsset1 = 5;
        } else if(player.personagem == 5){
            assets = new BufferedImage[1][13];
            for(int i=0; i<13; i++)
                assets[0][i] = atlas.getSubimage(i*110, 0, 110, 64);
            indexAsset1 = 13;
        }

    }
}