package mygame;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.util.*;

public class MainClass extends JFrame {
	
	static final int WINDOW_WIDTH=500;
	static final int WINDOW_HEIGHT=500;
	static final int PLAYER_WIDTH=20;
	static final int PLAYER_HEIGHT=20;
	static Random rnd=new Random();
	boolean isRunning=true;
	int fps=60;
	BufferedImage backBuffer;
	Insets insets;
	InputHandler input;
	long tiempoUltimoMisil=0;
	long tiempoEntreMisiles=200;
	int missileSize=PLAYER_WIDTH/2+1;
	int missileSeparation=2;
	Color missileColor=Color.RED;
	int avanceMisil=5;		//5
	double missileReduction=0.125;	//0.125;
	
	List<Missile> playerMissiles=new ArrayList<Missile>();
	List<Missile> eviliaMissiles=new ArrayList<Missile>();
	List<Rectangle> rectangles=new ArrayList<Rectangle>();
	List<EvilIA> evilias=new ArrayList<EvilIA>();
	
	int x=0;
	int y=0;
	
	public void run() {
		Initialize();
		while(isRunning) {
			long time=System.currentTimeMillis();
			Update();
			Draw();
			time=(1000/fps) - (System.currentTimeMillis()-time);
			if(time>0) {
				try {
					Thread.sleep(time);
				} catch(InterruptedException e) {}
			}
		}
		setVisible(false);
	}
	
	void Initialize() {
		setTitle("Game");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		insets=getInsets();
		setSize(insets.left+WINDOW_WIDTH+insets.right, insets.top+WINDOW_HEIGHT+insets.bottom);
		backBuffer=new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		input=new InputHandler(this);
		
		InitializeRectangles();
		InitializeEvilIA();
	}
	
	void InitializeRectangles() {
		int ancho=rnd.nextInt(40)+20;
		int alto=rnd.nextInt(40)+20;
		rectangles.add(new Rectangle(rnd.nextInt(WINDOW_WIDTH-ancho), rnd.nextInt(WINDOW_HEIGHT-ancho), ancho, alto, 100));
	}
	
	void InitializeEvilIA() {
		evilias.add(new EvilIA(rnd.nextInt(WINDOW_WIDTH-EvilIA.WIDTH), rnd.nextInt(WINDOW_HEIGHT-EvilIA.HEIGHT), 100));
	}
	
	void UpdatePlayerPosition() {
		if(input.isKeyDown(KeyEvent.VK_RIGHT)) {
			x+=5;
			if(x>=WINDOW_WIDTH-PLAYER_WIDTH/2) {
				x=-PLAYER_WIDTH/2;
			}
		}
		if(input.isKeyDown(KeyEvent.VK_LEFT)) {
			x-=5;
			if(x<=-PLAYER_WIDTH/2) {
				x=WINDOW_WIDTH-PLAYER_WIDTH/2;
			}
		}
		if(input.isKeyDown(KeyEvent.VK_UP)) {
			y-=5;
			if(y<=-PLAYER_HEIGHT/2) {
				y=WINDOW_HEIGHT-PLAYER_HEIGHT/2;
			}
		}
		if(input.isKeyDown(KeyEvent.VK_DOWN)) {
			y+=5;
			if(y>=WINDOW_HEIGHT-PLAYER_HEIGHT/2) {
				y=-PLAYER_HEIGHT/2;
			}
		}
	}
	
	void UpdatePlayerMissiles() {
		if(input.isKeyDown(KeyEvent.VK_W)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				playerMissiles.add(new Missile(x+PLAYER_WIDTH/2, y-missileSeparation-missileSize/2, missileSize, 0, missileColor, avanceMisil, missileReduction));
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_S)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				playerMissiles.add(new Missile(x+PLAYER_WIDTH/2, y+PLAYER_HEIGHT+missileSeparation+missileSize/2, missileSize, 1, missileColor, avanceMisil, missileReduction));
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_A)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				playerMissiles.add(new Missile(x-missileSeparation-missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 2, missileColor, avanceMisil, missileReduction));
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		if(input.isKeyDown(KeyEvent.VK_D)) {
			if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
				playerMissiles.add(new Missile(x+PLAYER_WIDTH+missileSeparation+missileSize/2, y+PLAYER_HEIGHT/2, missileSize, 3, missileColor, avanceMisil, missileReduction));
				tiempoUltimoMisil=System.currentTimeMillis();
			}
		}
		
		for(int i=0; i<playerMissiles.size(); i++) {
			Missile m=playerMissiles.get(i);
			m.Update();
			if(m.isDead()) {
				playerMissiles.remove(i);
			}
		}
	}
	
	void UpdateEvilIAMissiles() {
		for(int i=0; i<eviliaMissiles.size(); i++) {
			Missile m=eviliaMissiles.get(i);
			m.Update();
			if(m.isDead()) {
				eviliaMissiles.remove(i);
			}
		}
	}
	
	void UpdateRectangles() {
		for(int i=0; i<rectangles.size(); i++) {
			Rectangle r=rectangles.get(i);
			r.Update(playerMissiles, eviliaMissiles);
			if(r.isDead()) {
				rectangles.remove(i);
				int ancho=rnd.nextInt(40)+20;
				int alto=rnd.nextInt(40)+20;
				rectangles.add(new Rectangle(rnd.nextInt(WINDOW_WIDTH-ancho), rnd.nextInt(WINDOW_HEIGHT-ancho), ancho, alto, 100));
				if(rnd.nextInt(4)==0) {
					//Generar otro rectángulo
					ancho=rnd.nextInt(40)+20;
					alto=rnd.nextInt(40)+20;
					rectangles.add(new Rectangle(rnd.nextInt(WINDOW_WIDTH-ancho), rnd.nextInt(WINDOW_HEIGHT-ancho), ancho, alto, 100));
				}
				//Mejorar arma
				missileSize++;
			}
		}
	}
	
	void UpdateEvilIA() {
		for(int i=0; i<evilias.size(); i++) {
			EvilIA e=evilias.get(i);
			e.Update(playerMissiles, this, eviliaMissiles);
			if(e.isDead()) {
				evilias.remove(i);
			}
		}
	}
	
	void Update() {
		UpdatePlayerPosition();
		UpdatePlayerMissiles();
		UpdateEvilIA();
		UpdateEvilIAMissiles();
		UpdateRectangles();
	}
	
	void DrawPlayer(Graphics bbg) {
		bbg.setColor(Color.BLUE);
		bbg.fillRect(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
	
	void DrawMissiles(Graphics bbg) {
		//Draw Player 1 missiles
		for(int i=0; i<playerMissiles.size(); i++) {
			Missile m=playerMissiles.get(i);
			m.Draw(bbg);
		}
		//Draw evilIA missiles
		for(int i=0; i<eviliaMissiles.size(); i++) {
			Missile m=eviliaMissiles.get(i);
			m.Draw(bbg);
		}
	}
	
	void DrawRectangles(Graphics bbg) {
		for(int i=0; i<rectangles.size(); i++) {
			Rectangle r=rectangles.get(i);
			r.Draw(bbg);
		}
	}
	
	void DrawEvilIAs(Graphics bbg) {
		for(int i=0; i<evilias.size(); i++) {
			EvilIA e=evilias.get(i);
			e.Draw(bbg);
		}
	}
	
	void Draw() {
		Graphics g=getGraphics();
		Graphics bbg=backBuffer.getGraphics();
		
		bbg.setColor(Color.WHITE);
		bbg.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		DrawPlayer(bbg);	//Player 1
		DrawMissiles(bbg);
		DrawEvilIAs(bbg);
		DrawRectangles(bbg);
		
		g.drawImage(backBuffer, insets.left, insets.top, this);
	}
	
	public int getX() {return x;}
	
	public int getY() {return y;}
	
	public static void main(String[] args) {
		MainClass game=new MainClass();
		game.run();
		System.exit(0);
	}
}
