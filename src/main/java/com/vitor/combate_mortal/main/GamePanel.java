package com.vitor.combate_mortal.main;

import com.vitor.combate_mortal.inputs.Inputs;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import com.vitor.combate_mortal.states.GameStates;
import com.vitor.combate_mortal.states.Inicio;
import com.vitor.combate_mortal.states.Jogo;
import com.vitor.combate_mortal.states.Selecao;
import com.vitor.combate_mortal.states.Vitoria;

public class GamePanel extends JPanel implements Runnable{

    Thread gameThread;
    Inputs inp;
    Frame frame;
    public Jogo jogo;
    public Selecao selecao;
    public Vitoria vitoria;
    public Inicio inicio;
    public int width = 1200, height = 762;

    public GamePanel() {
        initClasses();

        this.setPreferredSize(new Dimension(width, height));
        frame = new Frame(this);
        frame.setLocationRelativeTo(null);
        this.setFocusable(true);
        this.requestFocusInWindow();
        inp = new Inputs(this);
        this.addKeyListener(inp);

        iniciaThread();
    }

    public void initClasses() {
        jogo = new Jogo(this);
        selecao = new Selecao(this);
        vitoria = new Vitoria(this);
        inicio = new Inicio(this);
    }

    public void iniciaThread(){
        gameThread = new Thread(this);
        gameThread.setDaemon(true);
        gameThread.start();
    }


    public void run() {
        //ATUALIZA O PANEL A 60fps
        long intervalo = 1000000000 / 60;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            if(currentTime - lastTime >= intervalo){
                update();
                repaint();
                lastTime = currentTime;
            }
        }
    }

    public void update() {

        switch (GameStates.gameState) {
            case SELECAO:
                selecao.update();
                break;
            case JOGO:
                jogo.update();
                break;
            case VITORIA:
                vitoria.update();
                break;
            case INICIO:
                inicio.update();
                break;
            default:
                break;

        }
    }

    public void paintComponent(Graphics g) {
        //É CHAMADA SEMPRE QUE O MÉTODO REPAINT É CHAMADO

        switch (GameStates.gameState) {
            case SELECAO:
                selecao.draw(g);
                break;
            case JOGO:
                jogo.draw(g);
                break;
            case VITORIA:
                vitoria.draw(g);
                break;
            case INICIO:
                inicio.draw(g);
                break;
            default:
                break;

        }
    }

    public Jogo getJogo() {
        return jogo;
    }

    public Selecao getSelecao() {
        return selecao;
    }

    public Vitoria getVitoria() {
        return vitoria;
    }

}
