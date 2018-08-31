package mygame;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class Rectangle {
	
	int x;
	int y;
	int xSize;
	int ySize;
	double life;		//from 0 to 100
	
	public Rectangle(int x, int y, int xSize, int ySize, double life) {
		this.x=x;
		this.y=y;
		this.xSize=xSize;
		this.ySize=ySize;
		this.life=life;
	}
	
	public void Draw(Graphics bbg) {
		bbg.setColor(new Color(55+200-(int)life*2, 55+(int)life*2, 55));
		bbg.fillRect(x, y, xSize, ySize);
	}
	
	public void Update(List<Missile> playerMissiles, List<Missile> eviliaMissiles) {
		//Comprobar posibles colisiones con misiles de Player 1
		for(int i=0; i<playerMissiles.size(); i++) {
			Missile m=playerMissiles.get(i);
			if(((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(y+ySize)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(x+xSize))) {	//Hay colisión
				life-=m.getSize()*2;
				playerMissiles.remove(i);
			}
		}
		//Comprobar posibles colisiones con misiles de evilIA
		for(int i=0; i<eviliaMissiles.size(); i++) {
			Missile m=eviliaMissiles.get(i);
			if(((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(y+ySize)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(x+xSize))) {	//Hay colisión
				life-=m.getSize()*2;
				eviliaMissiles.remove(i);
			}
		}
	}
	
	public boolean isDead() {
		return life<=0;
	}
}
