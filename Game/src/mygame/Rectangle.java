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
				}
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
