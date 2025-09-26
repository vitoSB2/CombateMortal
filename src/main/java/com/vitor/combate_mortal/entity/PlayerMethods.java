package com.vitor.combate_mortal.entity;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

public interface PlayerMethods {

    public void update();

    public void draw(Graphics g);

    public void keyPressed(KeyEvent e);

    public void keyReleased(KeyEvent e);

    public void keyTyped(KeyEvent e);

}