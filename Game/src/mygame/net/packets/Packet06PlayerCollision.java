package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet06PlayerCollision extends Packet {	//Usado tambien para actualizaciones de vida

	String username;
	int missileIndex;
	
	public Packet06PlayerCollision(byte[] data) {
		super(06);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.missileIndex=Integer.parseInt(dataArray[1]);
	}
	
	public Packet06PlayerCollision(String username, int missileIndex) {
		super(06);
		this.username=username;
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
		return ("06"+this.username+","+this.missileIndex).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getMissileIndex() {
		return missileIndex;
	}
}
