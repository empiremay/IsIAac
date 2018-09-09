package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet06PlayerCollision extends Packet {

	String username;
	int life;
	int missileIndex;
	
	public Packet06PlayerCollision(byte[] data) {
		super(06);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.life=Integer.parseInt(dataArray[1]);
	}
	
	public Packet06PlayerCollision(String username, int life, int missileIndex) {
		super(06);
		this.username=username;
		this.life=life;
		this.missileIndex=missileIndex;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		//server.sendDataToAllClients(getData());
		server.sendDataToAllClientsExceptUsername(getData(), username);
	}

	public byte[] getData() {
		return ("06"+this.username+","+this.life+","+this.missileIndex).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getLife() {
		return life;
	}
	
	public int getMissileIndex() {
		return missileIndex;
	}
}
