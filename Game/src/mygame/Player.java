package mygame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mygame.net.packets.Packet02Move;
import mygame.net.packets.Packet03ShootMissile;
import mygame.net.packets.Packet04UpdateMissile;
import mygame.net.packets.Packet06PlayerCollision;

public class Player {

	InputHandler input;
	String username;
	
	static final int PLAYER_WIDTH=20;
	static final int PLAYER_HEIGHT=20;
	static final int MAX_LIFE=100;
	static Random rnd=new Random();
	long tiempoUltimoMisil=0;
	long tiempoEntreMisiles=200;	//200
	int missileSize=50;//PLAYER_WIDTH/2+1;
	int missileSeparation=2;
	String missileColor="RED";
	int avanceMisil=5;		//5
	double missileReduction=0.125*2;	//0.125;
	int life=0;
	int damagedLife=0;	//Vida en rojo al dañarse y en amarillo al dañarse mucho
	int regenLife;
	String damagedLifeColor="RED";
	String color="BLUE";
	int redDuration=10;
	int currentRedDuration=redDuration;
	
	int damagedLifeDuration=20;
	int currentDamagedLifeDuration=damagedLifeDuration;
	boolean damagedLifeDecreasing=false;
	
	int regenLifeDuration=20;
	int currentRegenLifeDuration=regenLifeDuration;
	boolean regenLifeDecreasing=false;
	
	public List<Missile> missiles=new ArrayList<Missile>();
	
	public int x=0;
	public int y=0;
	
	Player(int x, int y, InputHandler input, String username, String missileColor) {
		this.x=x;
		this.y=y;
		this.input=input;
		this.username=username;
		this.missileColor=missileColor;
		this.life=MAX_LIFE;
	}
	
	void UpdatePlayerPosition() {
		int newX=x;
		int newY=y;
		boolean keyDown=false;
		
		//Update color
		if(this.color=="RED") {	//El color ROJO aparecerá durante 3 fotogramas
			--currentRedDuration;
			if(currentRedDuration==0) {
				this.color="BLUE";
				currentRedDuration=redDuration;
			}
		}
		//Update damagedLife
		if(damagedLife>0) {
			--currentDamagedLifeDuration;
			if(currentDamagedLifeDuration==0) {
				//Start decreasing damagedLife
				damagedLifeDecreasing=true;
				currentDamagedLifeDuration=damagedLifeDuration;
			}
		}
		if(damagedLifeDecreasing) {
			--damagedLife;
			if(damagedLife<=0) {
				damagedLifeDecreasing=false;
				damagedLifeColor="RED";
			}
		}
		//Update regenLife
		if(regenLife>0) {
			--currentRegenLifeDuration;
			if(currentRegenLifeDuration==0) {
				//Start decreasing regenLife
				regenLifeDecreasing=true;
				currentRegenLifeDuration=regenLifeDuration;
			}
		}
		if(regenLifeDecreasing) {
			--regenLife;
			if(regenLife<=0) {
				regenLifeDecreasing=false;
			}
		}
		
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
				missiles.add(new Missile(username, x+PLAYER_WIDTH/2, y-missileSeparation-missileSize/2, missileSize, 0, missileColorClient, avanceMisil, missileReduction));
				
				Packet03ShootMissile packet=new Packet03ShootMissile(username, x+PLAYER_WIDTH/2, y-missileSeparation-missileSize/2, missileSize, 0, missileColor, avanceMisil, missileReduction);	//Send username so the server knows the missiles of that username
				packet.writeData(MainClass.game.socketClient);
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_S)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				//ONLY FOR CLIENT
				missiles.add(new Missile(username, x+PLAYER_WIDTH/2, y+PLAYER_HEIGHT+missileSeparation+missileSize/2, missileSize, 1, missileColorClient, avanceMisil, missileReduction));
				
				Packet03ShootMissile packet=new Packet03ShootMissile(username, x+PLAYER_WIDTH/2, y+PLAYER_HEIGHT+missileSeparation+missileSize/2, missileSize, 1, missileColor, avanceMisil, missileReduction);	//Send username so the server knows the missiles of that username
				packet.writeData(MainClass.game.socketClient);
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_A)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				//ONLY FOR CLIENT
				missiles.add(new Missile(username, x-missileSeparation-missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 2, missileColorClient, avanceMisil, missileReduction));
				
				Packet03ShootMissile packet=new Packet03ShootMissile(username, x-missileSeparation-missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 2, missileColor, avanceMisil, missileReduction);	//Send username so the server knows the missiles of that username
				packet.writeData(MainClass.game.socketClient);
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_D)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				//ONLY FOR CLIENT
				missiles.add(new Missile(username, x+PLAYER_WIDTH+missileSeparation+missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 3, missileColorClient, avanceMisil, missileReduction));
				
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
	
	void UpdatePlayerCollisions(List<PlayerMP> players) {
		//Missile collisions
		for(int j=0; j<players.size(); j++) {
			//if(players.get(j).getUsername().equals(username)==false) {
				List<Missile> playerMissiles=players.get(j).GetMissiles();
				for(int i=0; i<playerMissiles.size(); i++) {
					Missile m=playerMissiles.get(i);
					if(((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(y+PLAYER_HEIGHT)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(x+PLAYER_WIDTH))) {	//Hay colisión
						int random=rnd.nextInt(2);
						if(random==0) {
							UpdateHP((int)(life-m.getSize()));
						}
						else {
							UpdateHP((int)(life+m.getSize()));
						}
						playerMissiles.remove(i);
						Enrojecer();
						//Mandar paquete al server
						//Packet06PlayerCollision packet=new Packet06PlayerCollision(username, life, i);
						//packet.writeData(MainClass.game.socketClient);
					}
				}
			//}
		}
	}
	
	void UpdateHP(int life) {
		int damage=this.life-life;
		int regen=life-this.life;
		if(life<0) {
			this.life=0;
		}
		else {
			if(life>MAX_LIFE) {
				this.life=MAX_LIFE;
			}
			else {
				this.life=life;
				//Mi vida baja
				if(damage>0) {
					this.damagedLife+=damage;
					//Si la relación de daño es mayor del x%, damagedLife será amarilla
					int percentage=33;
					if(damage>(percentage*MAX_LIFE/100)) {
						damagedLifeColor="YELLOW";
					}
				}
				//Mi vida sube
				if(regen>0) {
					this.damagedLife-=regen;
					if(this.damagedLife<0) {
						this.damagedLife=0;
					}
					this.regenLife+=regen;
				}
			}
		}
		//Comprobación final
		if(this.life+this.damagedLife>MAX_LIFE) {
			this.damagedLife=MAX_LIFE-this.life;
		}
		if(regenLife>this.life) {
			regenLife=0;
		}
	}
	
	void Enrojecer() {
		this.color="RED";
	}
	
	void Draw(Graphics bbg) {
		//Display player circle
		Color color=Color.BLACK;
		switch(this.color) {
			default:
			case "BLUE": color=Color.BLUE; break;
			case "RED": color=Color.RED; break;
		}
		bbg.setColor(color);
		bbg.fillRect(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
		//Display username
		Graphics2D bbg2=(Graphics2D)bbg;
		bbg2.setColor(Color.BLUE);
		bbg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		bbg2.drawString(username, x-username.length()*2, y-15);
	}
	
	void DrawLifeBar(Graphics bbg) {
		Graphics2D bbg2=(Graphics2D)bbg;
		int lineThickness=2;
		bbg2.setStroke(new BasicStroke(lineThickness));
		int x_separation=30;
		int y_separation=40;
		int bar_width=100;
		int bar_height=20;
		
		int currentLife=life;
		if(life<0) {
			currentLife=0;
		}
		
		//Life
		bbg2.setColor(new Color(34, 230, 22));
		bbg2.fillRect(MainClass.WINDOW_WIDTH-1-x_separation-bar_width, y_separation, currentLife-regenLife, bar_height);
		//Regen Life
		bbg2.setColor(new Color(226, 254, 255));
		bbg2.fillRect(MainClass.WINDOW_WIDTH-1-x_separation-bar_width+(currentLife-regenLife), y_separation, regenLife, bar_height);
		//Damaged Life
		switch(damagedLifeColor) {
			default:
			case "RED": bbg2.setColor(new Color(255, 6, 28)); break;
			case "YELLOW": bbg2.setColor(new Color(255, 104, 0)); break;
		}
		bbg2.fillRect(MainClass.WINDOW_WIDTH-1-x_separation-(bar_width-currentLife), y_separation, damagedLife, bar_height);
		//No Life
		bbg2.setColor(Color.BLACK);
		bbg2.fillRect(MainClass.WINDOW_WIDTH-1-x_separation-(bar_width-(currentLife+damagedLife)), y_separation, bar_width-(currentLife+damagedLife), bar_height);
		
		//Marco exterior
		bbg2.setColor(Color.DARK_GRAY);
		bbg2.drawRect(MainClass.WINDOW_WIDTH-1-x_separation-bar_width, y_separation, bar_width, bar_height);
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
	
	public int getLife() {
		return this.life;
	}
}
