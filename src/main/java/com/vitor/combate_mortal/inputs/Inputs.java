package com.vitor.combate_mortal.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import com.vitor.combate_mortal.main.GamePanel;
import com.vitor.combate_mortal.states.GameStates;

//teste
public class Inputs implements KeyListener{

    GamePanel gp;
    public Inputs(GamePanel gp) {
        this.gp = gp;
    }

    public void keyTyped(KeyEvent e) {

        switch(GameStates.gameState) {
            case SELECAO:
                gp.getSelecao().keyTyped(e);
                break;
            case JOGO:
                gp.getJogo().keyTyped(e);
                break;
            case VITORIA:
                gp.getVitoria().keyTyped(e);
                break;
            case INICIO:
                gp.getVitoria().keyTyped(e);
                break;
            default:
                break;
        }
    }

    public void keyPressed(KeyEvent e) {

        switch(GameStates.gameState) {
            case SELECAO:
                gp.getSelecao().keyPressed(e);
                break;
            case JOGO:
                gp.getJogo().keyPressed(e);
                break;
            case VITORIA:
                gp.getVitoria().keyPressed(e);
                break;
            case INICIO:
                gp.getVitoria().keyPressed(e);
                break;
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent e) {

        switch(GameStates.gameState) {
            case SELECAO:
                gp.getSelecao().keyReleased(e);
                break;
            case JOGO:
                gp.getJogo().keyReleased(e);
                break;
            case VITORIA:
                gp.getVitoria().keyReleased(e);
                break;
            case INICIO:
                gp.getVitoria().keyReleased(e);
                break;
            default:
                break;
        }
    }

}
