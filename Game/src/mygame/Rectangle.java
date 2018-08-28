package mygame;
import java.awt.*;
import java.util.*;

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
		bbg.setColor(new Color(55+200-(int)life*2, 0, 55+(int)life*2));
		bbg.fillRect(x, y, xSize, ySize);
	}
	
	public void Update(ArrayList missiles) {
		//Comprobar posibles colisiones con misiles
		for(int i=0; i<missiles.size(); i++) {
			Missile m=(Missile)missiles.get(i);
			if(((m.getX()+m.getSize()/2)>x) && ((m.getY()-m.getSize()/2)<(y+ySize)) && ((m.getY()+m.getSize()/2)>y) && ((m.getX()-m.getSize()/2)<(x+xSize))) {	//Hay colisión
				life-=m.getSize()*2;
				missiles.remove(i);
			}
		}
	}
	
	public boolean isDead() {
		return life<=0;
	}
}
