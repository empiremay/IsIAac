package mygame;
import java.util.List;

import mygame.net.packets.Packet06PlayerCollision;
import mygame.net.packets.Packet13UpdateHP;

import java.util.ArrayList;
import java.awt.*;

public class Rectangle {
	
	int x;
	int y;
	int xSize;
	int ySize;
	double life;		//from 0 to 100
	double maxLife;
	
	public Rectangle(int x, int y, int xSize, int ySize, double life) {
		this.x=x;
		this.y=y;
		this.xSize=xSize;
		this.ySize=ySize;
		this.life=life;
		this.maxLife=life;
	}
	
	public void Draw(Graphics bbg) {
		if(life<=0) {
			life=0;
		}
		double normalizedLife=life*255/maxLife;
		bbg.setColor(new Color(255-(int)normalizedLife, (int)normalizedLife, 55));
		bbg.fillRect(x, y, xSize, ySize);
	}
	
	public void Update(List<PlayerMP> players, List<Missile> eviliaMissiles) {
		//Comprobar posibles colisiones con los misiles de todos los jugadores
		for(int i=0; i<players.size(); i++) {
			PlayerMP p=players.get(i);
			List<Missile> missiles=p.GetMissiles();
			for(int j=0; j<missiles.size(); j++) {
				Missile m=missiles.get(j);
				if(((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(y+ySize)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(x+xSize))) {	//Hay colisión
					life-=m.getSize()*2;
					missiles.remove(j);
					if(life<=0) {
						//Regenerar vida del jugador en función de maxLife
						//int newLife=p.getLife()+(int)(maxLife/15);
						//p.UpdateHP(newLife);
						Packet13UpdateHP packet=new Packet13UpdateHP(p.getUsername(), -maxLife/15);	//- dado que es regeneración
						packet.writeData(MainClass.game.socketClient);
					}
				}
			}
		}
		//Comprobar posibles colisiones con misiles de evilIA
		/*for(int i=0; i<eviliaMissiles.size(); i++) {
			Missile m=eviliaMissiles.get(i);
			if(((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(y+ySize)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(x+xSize))) {	//Hay colisión
				life-=m.getSize()*2;
				eviliaMissiles.remove(i);
			}
		}*/
	}
	
	public boolean isDead() {
		return life<=0;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getXSize() {
		return xSize;
	}
	
	public int getYSize() {
		return ySize;
	}
	
	public double getLife() {
		return life;
	}
}
