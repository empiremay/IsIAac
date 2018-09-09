package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet07SendLifeBar extends Packet {

	String username;
	int life;
	
	public Packet07SendLifeBar(byte[] data) {
		super(07);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.life=Integer.parseInt(dataArray[1]);
	}
	
	public Packet07SendLifeBar(String username, int life) {
		super(07);
		this.username=username;
		this.life=life;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		//server.sendDataToAllClients(getData());
		server.sendDataToAllClientsExceptUsername(getData(), username);
	}

	public byte[] getData() {
		return ("07"+this.username+","+this.life).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getLife() {
		return life;
	}
}
