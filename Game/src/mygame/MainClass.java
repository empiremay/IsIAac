package mygame;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import mygame.net.GameClient;
import mygame.net.GameServer;
import mygame.net.packets.*;

import java.util.*;

public class MainClass extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public static final int WINDOW_WIDTH=500;
	public static final int WINDOW_HEIGHT=500;
	public static MainClass game;
	static Random rnd=new Random();
	boolean isRunning=true;
	int fps=60;
	BufferedImage backBuffer;
	Insets insets;
	public InputHandler input;
	public WindowHandler windowHandler;
	Map<String, Integer> playersHP=new HashMap<String, Integer>();
	
	List<PlayerMP> players=new ArrayList<PlayerMP>();		//MP Players, including player1
	List<Missile> eviliaMissiles=new ArrayList<Missile>();
	List<Rectangle> rectangles=new ArrayList<Rectangle>();
	List<EvilIA> evilias=new ArrayList<EvilIA>();
	
	public GameClient socketClient;
	public GameServer socketServer;
	
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
		//Initialize first game things
		game=this;
		setTitle("Game");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		insets=getInsets();
		setSize(insets.left+WINDOW_WIDTH+insets.right, insets.top+WINDOW_HEIGHT+insets.bottom);
		backBuffer=new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		input=new InputHandler(this);
		windowHandler=new WindowHandler(this);
		
		
		//Initialize net things
		String ip_publica="localhost";//"90.168.229.154";
		if(JOptionPane.showConfirmDialog(this, "Do you want to run the server?")==0) {
			socketServer=new GameServer(this);
			socketServer.start();
		}
		socketClient=new GameClient(this, ip_publica);
		socketClient.start();
		
		String username=JOptionPane.showInputDialog(this, "Your username:");
		
		String[] missileColors= {"RED", "CYAN"};
		String missileColor=(String)JOptionPane.showInputDialog(null, "Choose projectile color:", "Elegir", JOptionPane.QUESTION_MESSAGE, null, missileColors, missileColors[0]);
		
		PlayerMP player=new PlayerMP(WINDOW_WIDTH/2, WINDOW_HEIGHT/2, input, username, missileColor, null, -1);
		players.add(player);
		
		Packet00Login loginPacket=new Packet00Login(player.getUsername(), player.getMissileColor());
		if(socketServer!=null) {
			socketServer.addConnection((PlayerMP)player, loginPacket);
		}
		//socketClient.sendData("ping".getBytes());
		loginPacket.writeData(socketClient);
		
		//InitializeRectangles();
		//InitializeEvilIA();
	}

	public void initializeRectangles(int x, int y, int xSize, int ySize, double life) {
		rectangles.add(new Rectangle(x, y, xSize, ySize, life));
	}
	
	void InitializeEvilIA() {
		evilias.add(new EvilIA(rnd.nextInt(WINDOW_WIDTH-EvilIA.WIDTH), rnd.nextInt(WINDOW_HEIGHT-EvilIA.HEIGHT), 100));
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
			r.Update(players, eviliaMissiles);
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
			}
		}
	}
	
	/*void UpdateEvilIA() {
		for(int i=0; i<evilias.size(); i++) {
			EvilIA e=evilias.get(i);
			e.Update(player1.GetPlayer1Missiles(), player1, eviliaMissiles);
			if(e.isDead()) {
				evilias.remove(i);
			}
		}
	}*/
	
	void UpdatePlayerPosition() {		//Only position
		players.get(0).UpdatePlayerPosition();
	}
	
	void UpdatePlayerMissiles() {
		players.get(0).UpdatePlayerMissiles();
	}
	
	void UpdatePlayerCollisions() {
		players.get(0).UpdatePlayerCollisions(players);
	}
	
	public void AddPlayerMP(PlayerMP player) {
		players.add(player);
	}
	
	public void RemovePlayerMP(String username) {
		int index=0;
		for(int i=0; i<players.size(); i++) {
			if(username.equals(players.get(i).getUsername())) {
				index=i;
			}
		}
		players.remove(index);
	}
	
	private int getPlayerMPindex(String username) {
		int index=0;
		for(int i=0; i<players.size(); i++) {
			if(username.equals(players.get(i).getUsername())) {
				break;
			}
			index++;
		}
		return index;
	}
	
	public void updatePlayerPosition(String username, int x, int y) {	//Updates "username" player position
		int index=getPlayerMPindex(username);
		players.get(index).x=x;
		players.get(index).y=y;
	}
	
	public void shootMissile(String username, int x, int y, double size, int direction, Color color, int avance, double missileReduction) {
		int index=getPlayerMPindex(username);
		Missile m=new Missile(username, x, y, (int)size, direction, color, avance, missileReduction);
		players.get(index).AddMissile(m);
	}
	
	public void updateMissile(String username, Missile m, int missileIndex) {
		int index=getPlayerMPindex(username);
		if(players.get(index).missiles.size()<=missileIndex) {return;}
		players.get(index).missiles.get(missileIndex).Update();
		if(players.get(index).missiles.get(missileIndex).isDead()) {
			players.get(index).missiles.remove(missileIndex);
		}
	}
	
	public void playerCollision(String username, int life, int missileIndex) {
		int index=getPlayerMPindex(username);
		//players.get(index).setLife(life);
		players.get(index).missiles.remove(missileIndex);	//CORREGIR
	}
	
	public void sendLifeBar(String username, int life) {
		playersHP.put(username, life);
	}
	
	public void quitLifeBar(String username) {
		playersHP.remove(username);
	}
	
	void Update() {
		UpdatePlayerPosition();
		UpdatePlayerMissiles();
		UpdatePlayerCollisions();
		//UpdateEvilIA();
		//UpdateEvilIAMissiles();
		//UpdateRectangles();
	}
	
	void DrawMissiles(Graphics bbg) {
		//Draw Player missiles
		for(int i=0; i<players.size(); i++) {
			PlayerMP p=players.get(i);
			List<Missile> missiles=p.GetMissiles();
			for(int j=0; j<missiles.size(); j++) {
				Missile m=missiles.get(j);
				m.Draw(bbg);
			}
		}
		
		//Draw evilIA missiles
		/*for(int i=0; i<eviliaMissiles.size(); i++) {
			Missile m=eviliaMissiles.get(i);
			m.Draw(bbg);
		}*/
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
	
	void DrawPlayerMPs(Graphics bbg) {
		for(int i=0; i<players.size(); i++) {
			PlayerMP p=players.get(i);
			p.Draw(bbg);
		}
	}
	
	void DrawPlayerLife(Graphics bbg) {
		players.get(0).DrawLifeBar(bbg);
	}
	
	void DrawPlayerMPsLife(Graphics bbg) {
		int yOffset=0;
		for(Map.Entry<String, Integer> entry: playersHP.entrySet()) {
			DrawPlayerMPLife(entry.getKey(), entry.getValue(), bbg, yOffset);
			yOffset+=50;
		}
	}
	
	void DrawPlayerMPLife(String username, int life, Graphics bbg, int yOffset) {
		Graphics2D bbg2=(Graphics2D)bbg;
		int lineThickness=2;
		bbg2.setStroke(new BasicStroke(lineThickness));
		int x_separation=30;
		int y_separation=30+yOffset;
		int bar_width=75;
		int bar_height=15;
		
		int currentLife=life;
		if(life<0) {
			currentLife=0;
		}
		currentLife*=(double)bar_width/100;
		
		//Nombre
		bbg2.setColor(Color.BLACK);
		bbg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		bbg2.drawString(username.toUpperCase(), x_separation, y_separation);
		
		y_separation+=5;
		//Vida
		bbg2.setColor(Color.GREEN);
		bbg2.fillRect(x_separation, y_separation, currentLife, bar_height);
		bbg2.setColor(Color.BLACK);
		//bbg2.fillRect(x_separation+currentLife, y_separation, bar_width-currentLife, bar_height);
		
		//Marco exterior
		bbg2.setColor(Color.DARK_GRAY);
		bbg2.drawRect(x_separation, y_separation, bar_width, bar_height);
	}
	
	void Draw() {
		Graphics g=getGraphics();
		Graphics bbg=backBuffer.getGraphics();
		
		bbg.setColor(Color.WHITE);
		bbg.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

		DrawMissiles(bbg);
		DrawPlayerMPs(bbg);		//Jugadores online
		//DrawEvilIAs(bbg);
		DrawRectangles(bbg);
		//Latest thing to draw is life:
		DrawPlayerLife(bbg);
		DrawPlayerMPsLife(bbg);
		
		g.drawImage(backBuffer, insets.left, insets.top, this);
	}
	
	public static void main(String[] args) {
		MainClass game=new MainClass();
		game.run();
		System.exit(0);
	}
}
