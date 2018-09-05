package mygame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import mygame.net.packets.Packet02Move;
import mygame.net.packets.Packet03ShootMissile;
import mygame.net.packets.Packet04UpdateMissile;

public class Player {

	InputHandler input;
	String username;
	
	static final int PLAYER_WIDTH=20;
	static final int PLAYER_HEIGHT=20;
	long tiempoUltimoMisil=0;
	long tiempoEntreMisiles=200;	//200
	int missileSize=PLAYER_WIDTH/2+1;
	int missileSeparation=2;
	String missileColor="RED";
	int avanceMisil=5;		//5
	double missileReduction=0.125;	//0.125;
	
	public List<Missile> missiles=new ArrayList<Missile>();
	
	public int x=0;
	public int y=0;
	
	Player(InputHandler input, String username) {
		this.input=input;
		this.username=username;
	}
	
	Player(int x, int y, InputHandler input, String username, String missileColor) {
		this.x=x;
		this.y=y;
		this.input=input;
		this.username=username;
		this.missileColor=missileColor;
	}
	
	void UpdatePlayerPosition() {
		int newX=x;
		int newY=y;
		boolean keyDown=false;
		
		if(input==null) {
			return;
		}
		if(input.isKeyDown(KeyEvent.VK_RIGHT)) {
			newX+=5;
			if(newX>=MainClass.WINDOW_WIDTH-PLAYER_WIDTH/2) {
				newX=-PLAYER_WIDTH/2;
			}
			keyDown=true;
		}
		if(input.isKeyDown(KeyEvent.VK_LEFT)) {
			newX-=5;
			if(newX<=-PLAYER_WIDTH/2) {
				newX=MainClass.WINDOW_WIDTH-PLAYER_WIDTH/2;
			}
			keyDown=true;
		}
		if(input.isKeyDown(KeyEvent.VK_UP)) {
			newY-=5;
			if(newY<=-PLAYER_HEIGHT/2) {
				newY=MainClass.WINDOW_HEIGHT-PLAYER_HEIGHT/2;
			}
			keyDown=true;
		}
		if(input.isKeyDown(KeyEvent.VK_DOWN)) {
			newY+=5;
			if(newY>=MainClass.WINDOW_HEIGHT-PLAYER_HEIGHT/2) {
				newY=-PLAYER_HEIGHT/2;
			}
			keyDown=true;
		}
		x=newX;
		y=newY;		//Update my positions (show on screen but they are not reflected by the server)
		
		if(keyDown) {
			//Send player position to the server
			Packet02Move packet=new Packet02Move(username, newX, newY);
			packet.writeData(MainClass.game.socketClient);
		}
	}
	
	void UpdatePlayerMissiles() {
		if(input==null) {
			return;
		}
		
		Color missileColorClient=Color.BLACK;
		switch(missileColor) {
			default:
			case "RED": missileColorClient=Color.RED; break;
			case "CYAN": missileColorClient=Color.CYAN; break;
		}
		
		if(input.isKeyDown(KeyEvent.VK_W)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				//ONLY FOR CLIENT
				missiles.add(new Missile(x+PLAYER_WIDTH/2, y-missileSeparation-missileSize/2, missileSize, 0, missileColorClient, avanceMisil, missileReduction));
				
				Packet03ShootMissile packet=new Packet03ShootMissile(username, x+PLAYER_WIDTH/2, y-missileSeparation-missileSize/2, missileSize, 0, missileColor, avanceMisil, missileReduction);	//Send username so the server knows the missiles of that username
				packet.writeData(MainClass.game.socketClient);
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_S)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				//ONLY FOR CLIENT
				missiles.add(new Missile(x+PLAYER_WIDTH/2, y+PLAYER_HEIGHT+missileSeparation+missileSize/2, missileSize, 1, missileColorClient, avanceMisil, missileReduction));
				
				Packet03ShootMissile packet=new Packet03ShootMissile(username, x+PLAYER_WIDTH/2, y+PLAYER_HEIGHT+missileSeparation+missileSize/2, missileSize, 1, missileColor, avanceMisil, missileReduction);	//Send username so the server knows the missiles of that username
				packet.writeData(MainClass.game.socketClient);
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_A)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				//ONLY FOR CLIENT
				missiles.add(new Missile(x-missileSeparation-missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 2, missileColorClient, avanceMisil, missileReduction));
				
				Packet03ShootMissile packet=new Packet03ShootMissile(username, x-missileSeparation-missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 2, missileColor, avanceMisil, missileReduction);	//Send username so the server knows the missiles of that username
				packet.writeData(MainClass.game.socketClient);
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_D)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				//ONLY FOR CLIENT
				missiles.add(new Missile(x+PLAYER_WIDTH+missileSeparation+missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 3, missileColorClient, avanceMisil, missileReduction));
				
				Packet03ShootMissile packet=new Packet03ShootMissile(username, x+PLAYER_WIDTH+missileSeparation+missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 3, missileColor, avanceMisil, missileReduction);	//Send username so the server knows the missiles of that username
				packet.writeData(MainClass.game.socketClient);
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		
		for(int i=0; i<missiles.size(); i++) {
			Missile m=missiles.get(i);
			//ONLY FOR CLIENT
			m.Update();
			if(m.isDead()) {
				missiles.remove(i);
			}
			//END ONLY FOR CLIENT
			Packet04UpdateMissile packet=new Packet04UpdateMissile(username, m, i);
			packet.writeData(MainClass.game.socketClient);
		}
	}
	
	void Draw(Graphics bbg) {
		//Display player circle
		bbg.setColor(Color.BLUE);
		bbg.fillRect(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
		//Display username
		Graphics2D bbg2=(Graphics2D)bbg;
		bbg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		bbg2.drawString(username, x-username.length()*2, y-15);
	}
	
	public List<Missile> GetMissiles() {
		return missiles;
	}
	
	public int getWidth() {
		return PLAYER_WIDTH;
	}
	
	public int getHeight() {
		return PLAYER_HEIGHT;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getMissileColor() {
		return missileColor;
	}
	
	public void AddMissile(Missile m) {
		missiles.add(m);
	}
}
