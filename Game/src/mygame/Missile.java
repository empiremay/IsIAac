package mygame;
import java.awt.*;

public class Missile {
	
	int x;
	int y;
	double size;
	int direction;	//up 0, down 1, left 2, right 3
	Color color;
	int avance;
	double missileReduction;
	
	public Missile(int x, int y, int size, int direction, Color color, int avance, double missileReduction) {
		this.x=x;
		this.y=y;
		this.size=size;
		this.direction=direction;
		this.color=color;
		this.avance=avance;
		this.missileReduction=missileReduction;
	}
	
	public void Draw(Graphics bbg) {
		bbg.setColor(color);
		bbg.fillOval(x-(int)(size/2), y-(int)(size/2), (int)size, (int)size);
	}
	
	public void Update() {
		size-=missileReduction;
		if(direction==0) {
			y-=avance;
			if(y<=-(int)(size/2)) {
				y=MainClass.WINDOW_HEIGHT-(int)(size/2);
			}
		}
		if(direction==1) {
			y+=avance;
			if(y>=MainClass.WINDOW_HEIGHT-(int)(size/2)) {
				y=-(int)(size/2);
			}
		}
		if(direction==2) {
			x-=avance;
			if(x<=-(int)(size/2)) {
				x=MainClass.WINDOW_WIDTH-(int)(size/2);
			}
		}
		if(direction==3) {
			x+=avance;
			if(x>=MainClass.WINDOW_WIDTH-(int)(size/2)) {
				x=-(int)(size/2);
			}
		}
	}
	
	public boolean isDead() {
		return size<=0;
	}
	
	public double getSize() {
		return size;
	}
	
	public int getX() {return x;}
	
	public int getY() {return y;}
	
	public int getDir() {return direction;}
}
