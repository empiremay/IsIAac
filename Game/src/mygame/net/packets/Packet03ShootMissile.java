package mygame.net.packets;

import java.awt.Color;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet03ShootMissile extends Packet {

	private String username;		//Propietary of the missile
	int x;
	int y;
	double size;
	int direction;
	String missileColor;
	int avance;
	double missileReduction;
	
	public Packet03ShootMissile(byte[] data) {
		super(03);
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
	
	public Packet03ShootMissile(String username, int x, int y, double size, int direction, String missileColor, int avance, double missileReduction) {
		super(03);
		this.username=username;
		this.x=x;
		this.y=y;
		this.size=size;
		this.direction=direction;
		this.missileColor=missileColor;
		this.avance=avance;
		this.missileReduction=missileReduction;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		//server.sendDataToAllClients(getData());
		server.sendDataToAllClientsExceptUsername(getData(), username);
	}

	public byte[] getData() {
		String data="03"+this.username+","+this.x+","+this.y+","+this.size+","+this.direction+","+this.missileColor+","+this.avance+","+this.missileReduction;
		return data.getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public double getSize() {
		return size;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public Color getMissileColor() {
		Color color=Color.BLACK;	//Default missile color
		switch(missileColor) {
			default:
			case "RED": color=Color.RED; break;
			case "CYAN": color=Color.CYAN; break;
		}
		return color;
	}
	
	public int getAvance() {
		return avance;
	}
	
	public double getMissileReduction() {
		return missileReduction;
	}
}