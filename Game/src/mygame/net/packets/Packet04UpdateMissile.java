package mygame.net.packets;

import mygame.Missile;
import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet04UpdateMissile extends Packet {

	private String username;		//Propietary of the missile
	Missile m;
	//Missile data:
	int x;
	int y;
	double size;
	int direction;
	String missileColor;
	int avance;
	double missileReduction;
	
	public Packet04UpdateMissile(byte[] data) {
		super(04);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.x=Integer.parseInt(dataArray[1]);
		this.y=Integer.parseInt(dataArray[2]);
		this.size=Double.parseDouble(dataArray[3]);
		this.direction=Integer.parseInt(dataArray[4]);
		this.missileColor=dataArray[5];
		this.avance=Integer.parseInt(dataArray[6]);
		this.missileReduction=Double.parseDouble(dataArray[7]);
	}
	
	public Packet04UpdateMissile(String username, Missile m) {
		super(04);
		this.username=username;
		this.m=m;
		this.x=m.getX();
		this.y=m.getY();
		this.size=m.getSize();
		this.direction=m.getDir();
		this.missileColor=m.getColor();
		this.avance=m.getAvance();
		this.missileReduction=m.getMissileReduction();
	}
	
	public void writeData(GameClient client) {
		client.sendData(getData());
	}
	
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}
	
	public byte[] getData() {
		return ("04"+this.username+","+this.x+","+this.y+","+this.size+","+this.direction+","+this.missileColor+","+this.avance+","+this.missileReduction).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public Missile getMissile() {
		return m;
	}
}
