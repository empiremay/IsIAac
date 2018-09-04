package mygame;
import java.awt.Component;
import java.awt.event.*;

public class InputHandler implements KeyListener {
	
	boolean[] keys;
	
	public InputHandler(MainClass game) {
		game.addKeyListener(this);
		keys=new boolean[256];
	}
	
	public boolean isKeyDown(int keyCode) {
		if(keyCode>0 && keyCode<256) {
			return keys[keyCode];
		}
		return false;
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()>0 && e.getKeyCode()<256) {
			keys[e.getKeyCode()]=true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()>0 && e.getKeyCode()<256) {
			keys[e.getKeyCode()]=false;
		}
	}
	
	public void keyTyped(KeyEvent e) {}
}
