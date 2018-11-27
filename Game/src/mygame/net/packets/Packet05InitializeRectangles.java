package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet05InitializeRectangles extends Packet {

	int x;
	int y;
	int xSize;
	int ySize;
	double life;		//from 0 to 100
	String username;
	
	public Packet05InitializeRectangles(byte[] data) {
		super(05);
		String[] dataArray=readData(data).split(",");
		this.x=Integer.parseInt(dataArray[0]);
		this.y=Integer.parseInt(dataArray[1]);
		this.xSize=Integer.parseInt(dataArray[2]);
		this.ySize=Integer.parseInt(dataArray[3]);
		this.life=Double.parseDouble(dataArray[4]);
		this.username=dataArray[5];
	}
	
	public Packet05InitializeRectangles(int x, int y, int xSize, int ySize, double life, String username) {
		super(05);
		this.x=x;
		this.y=y;
		this.xSize=xSize;
		this.ySize=ySize;
		this.life=life;
		this.username=username;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToOnlyUsername(getData(), username);
	}

	public byte[] getData() {
		return ("05"+this.x+","+this.y+","+this.xSize+","+this.ySize+","+this.life+","+this.username).getBytes();
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
