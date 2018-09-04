package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet00Login extends Packet {

	private String username;
	private String missileColor;
	
	public Packet00Login(byte[] data) {
		super(00);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.missileColor=dataArray[1];
	}
	
	public Packet00Login(String username, String missileColor) {
		super(00);
		this.username=username;
		this.missileColor=missileColor;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("00"+this.username+","+this.missileColor).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getMissileColor() {
		return missileColor;
	}
}
