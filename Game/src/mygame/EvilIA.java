package mygame;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.lang.Math;

public class EvilIA {
	
	int x;
	int y;
	double life;	//from 0 to 100
	static final int WIDTH=20;
	static final int HEIGHT=20;
	long tiempoUltimoMisil=0;
	long tiempoEntreMisiles=400;
	int missileSize=WIDTH/2+1;
	int missileSeparation=2;
	Color missileColor=Color.BLACK;
	int avanceMisil=3;
	double missileReduction=0.0625;
	int longitudCuadrante=100;	//Para Missiles
	int avance=5;
	int distanceFromPlayer=80;	//Para Player
	
	public EvilIA(int x, int y, double life) {
		this.x=x;
		this.y=y;
		this.life=life;
	}
	
	public void Draw(Graphics bbg) {
		bbg.setColor(Color.DARK_GRAY);
		bbg.fillRect(x, y, WIDTH, HEIGHT);
	}
	
	public void UpdateMovement(List<Missile> playerMissiles, Player player) {
		int scoreIzq=0;
		int scoreDer=0;
		int scoreArr=0;
		int scoreAbj=0;
		
		for(int i=0; i<playerMissiles.size(); i++) {
			Missile m=playerMissiles.get(i);
			if((((m.getX()+m.getSize()/2)>(x-longitudCuadrante)) && ((m.getY()-m.getSize()/2)<(y+HEIGHT)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<x)) || (((m.getX()+m.getSize()/2)>(MainClass.WINDOW_WIDTH-1-(longitudCuadrante-x))) && ((m.getY()-m.getSize()/2)<(y+HEIGHT)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(MainClass.WINDOW_WIDTH-1)))) {	//Hay colisión
				if(m.getDir()==3) {
					scoreIzq+=m.getSize()*2;
				}
				else {
					scoreIzq+=m.getSize();
				}
			}
			if((((m.getX()+m.getSize()/2)>(x+WIDTH)) && ((m.getY()-m.getSize()/2)<(y+HEIGHT)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(x+WIDTH+longitudCuadrante))) || (((m.getX()+m.getSize()/2)>0) && ((m.getY()-m.getSize()/2)<(y+HEIGHT)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(longitudCuadrante-(MainClass.WINDOW_WIDTH-1-(x+WIDTH)))))) {	//Hay colisión
				if(m.getDir()==2) {
					scoreDer+=m.getSize()*2;
				}
				else {
					scoreDer+=m.getSize();
				}
			}
			if((((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<y) && ((m.getY()+m.getSize()/2)>(y-longitudCuadrante)) && ((m.getX()-m.getSize()/2)<(x+WIDTH))) || (((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(MainClass.WINDOW_HEIGHT-1)) && ((m.getY()+m.getSize()/2)>(MainClass.WINDOW_HEIGHT-1-(longitudCuadrante-y))) && ((m.getX()-m.getSize()/2)<(x+WIDTH)))) {	//Hay colisión
				if(m.getDir()==1) {
					scoreArr+=m.getSize()*2;
				}
				else {
					scoreArr+=m.getSize();
				}
			}
			if((((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(y+HEIGHT+longitudCuadrante)) && ((m.getY()+m.getSize()/2)>(y+HEIGHT)) && ((m.getX()-m.getSize()/2)<(x+WIDTH))) || (((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(longitudCuadrante-(MainClass.WINDOW_HEIGHT-1-(y+HEIGHT)))) && ((m.getY()+m.getSize()/2)>0) && ((m.getX()-m.getSize()/2)<(x+WIDTH)))) {
				if(m.getDir()==0) {
					scoreAbj+=m.getSize()*2;
				}
				else {
					scoreAbj+=m.getSize();
				}
			}
		}
		
		MoveAwayFromPlayer(player);

		/*if(scoreAbj==0 && scoreArr==0 && scoreIzq==0 && scoreDer==0) {
			return;
		}
		if(scoreDer<=scoreArr && scoreDer<=scoreIzq && scoreDer<=scoreAbj) {
			MoveRight();
			return;
		}
		if(scoreAbj<=scoreArr && scoreAbj<=scoreIzq && scoreAbj<=scoreDer) {
			MoveDown();
			return;
		}
		if(scoreArr<=scoreAbj && scoreArr<=scoreIzq && scoreArr<=scoreDer) {
			MoveUp();
			return;
		}
		if(scoreIzq<=scoreArr && scoreIzq<=scoreAbj && scoreIzq<=scoreDer) {
			MoveLeft();
			return;
		}*/
	}
	
	void MoveAwayFromPlayer(Player player) {
		if(Math.sqrt(Math.pow(x-player.getX(), 2) + Math.pow(y-player.getY(), 2))<=distanceFromPlayer) {
			MoveUp();
		}
	}
	
	void MoveUp() {
		y-=avance;
		if(y<=-HEIGHT/2) {
			y=MainClass.WINDOW_HEIGHT-HEIGHT/2;
		}
	}
	
	void MoveDown() {
		y+=avance;
		if(y>=MainClass.WINDOW_HEIGHT-HEIGHT/2) {
			y=-HEIGHT/2;
		}
	}
	
	void MoveLeft() {
		x-=avance;
		if(x<=-WIDTH/2) {
			x=MainClass.WINDOW_WIDTH-WIDTH/2;
		}
	}
	
	void MoveRight() {
		x+=avance;
		if(x>=MainClass.WINDOW_WIDTH-WIDTH/2) {
			x=-WIDTH/2;
		}
	}
	
	public void Update(List<Missile> playerMissiles, Player player, List<Missile> eviliaMissiles) {
		//Comprobar posibles colisiones con misiles de Player 1
		for(int i=0; i<playerMissiles.size(); i++) {
			Missile m=playerMissiles.get(i);
			if(((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(y+HEIGHT)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(x+WIDTH))) {	//Hay colisión
				life-=m.getSize()*3;
				playerMissiles.remove(i);
			}
		}
		UpdateMovement(playerMissiles, player);
		//Disparar en función de donde esté Player 1
		if((x+missileSize)>player.getX() && (x-missileSize)<player.getX()) {	//Disparar arriba o abajo
			if(y>player.getY()) {	//Disparar arriba
				if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
					eviliaMissiles.add(new Missile(x+WIDTH/2, y-missileSeparation-missileSize/2, missileSize, 0, missileColor, avanceMisil, missileReduction));
					tiempoUltimoMisil=System.currentTimeMillis();
				}
			}
			else {					//Disparar abajo
				if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
					eviliaMissiles.add(new Missile(x+WIDTH/2, y+HEIGHT+missileSeparation+missileSize/2, missileSize, 1, missileColor, avanceMisil, missileReduction));
					tiempoUltimoMisil=System.currentTimeMillis();
				}
			}
		}
		if((y+missileSize)>player.getY() && (y-missileSize)<player.getY()) {	//Disparar izquierda o derecha
			if(x>player.getX()) {	//Disparar izquierda
				if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
					eviliaMissiles.add(new Missile(x-missileSeparation-missileSize/2, y+HEIGHT/2, missileSize, 2, missileColor, avanceMisil, missileReduction));
					tiempoUltimoMisil=System.currentTimeMillis();
				}
			}
			else {					//Disparar derecha
				if((System.currentTimeMillis() - tiempoUltimoMisil) >= tiempoEntreMisiles) {
					eviliaMissiles.add(new Missile(x+WIDTH+missileSeparation+missileSize/2, y+HEIGHT/2, missileSize, 3, missileColor, avanceMisil, missileReduction));
					tiempoUltimoMisil=System.currentTimeMillis();
				}
			}
		}
	}
	
	public boolean isDead() {
		return life<=0;
	}
}
