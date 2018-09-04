package mygame;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import mygame.net.packets.Packet01Disconnect;

public class WindowHandler implements WindowListener {
	
	private final MainClass game;
	
	public WindowHandler(MainClass game) {
		this.game=game;
		this.game.addWindowListener(this);
	}

	public void windowActivated(WindowEvent event) {
		
	}

	public void windowClosed(WindowEvent event) {
		
	}

	public void windowClosing(WindowEvent event) {
		Packet01Disconnect packet=new Packet01Disconnect(this.game.players.get(0).getUsername());
		packet.writeData(this.game.socketClient);
	}

	public void windowDeactivated(WindowEvent event) {
		
	}

	public void windowDeiconified(WindowEvent event) {

	}

	public void windowIconified(WindowEvent event) {
		
	}

	public void windowOpened(WindowEvent event) {
		
	}	
}
