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
	static Random rnd=new Random();
	boolean isRunning=true;
	int fps=60;
	BufferedImage backBuffer;
	Insets insets;
	public InputHandler input;
	public WindowHandler windowHandler;
	
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
		//String ip_publica="84.125.220.44";
		if(JOptionPane.showConfirmDialog(this, "Do you want to run the server?")==0) {
			socketServer=new GameServer(this);
			socketServer.start();
		}
		/*else {
			ip_publica=JOptionPane.showInputDialog("Pon tu ip publica aqui");
		}*/
		socketClient=new GameClient(this, "localhost");
		socketClient.start();
		String username=JOptionPane.showInputDialog(this, "Your username:");
		PlayerMP player=new PlayerMP(WINDOW_WIDTH/2, WINDOW_HEIGHT/2, input, username, null, -1);
		players.add(player);
		Packet00Login loginPacket=new Packet00Login(player.getUsername());
		if(socketServer!=null) {
			socketServer.addConnection((PlayerMP)player, loginPacket);
		}
		//socketClient.sendData("ping".getBytes());
		loginPacket.writeData(socketClient);
		
		InitializeRectangles();
		//InitializeEvilIA();
	}
	
	void InitializeRectangles() {
		int ancho=rnd.nextInt(40)+20;
		int alto=rnd.nextInt(40)+20;
		rectangles.add(new Rectangle(rnd.nextInt(WINDOW_WIDTH-ancho), rnd.nextInt(WINDOW_HEIGHT-ancho), ancho, alto, 100));
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
	
	void UpdatePlayerMPsPosition() {		//Only position
		for(int i=0; i<players.size(); i++) {
			PlayerMP p=players.get(i);
			p.UpdatePlayerPosition();
		}
	}
	
	void UpdatePlayerMPsMissiles() {
		for(int i=0; i<players.size(); i++) {
			PlayerMP p=players.get(i);
			p.UpdatePlayerMissiles();
		}
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
	
	void Update() {
		UpdatePlayerMPsPosition();
		UpdatePlayerMPsMissiles();
		//UpdateEvilIA();
		//UpdateEvilIAMissiles();
		UpdateRectangles();
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
	
	void DrawPlayerMPs(Graphics bbg) {
		for(int i=0; i<players.size(); i++) {
			PlayerMP p=players.get(i);
			p.Draw(bbg);
		}
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
		
		g.drawImage(backBuffer, insets.left, insets.top, this);
	}
	
	public static void main(String[] args) {
		MainClass game=new MainClass();
		game.run();
		System.exit(0);
	}
}
