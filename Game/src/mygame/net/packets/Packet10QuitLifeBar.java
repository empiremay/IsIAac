package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet10QuitLifeBar extends Packet {

	String username;
	
	public Packet10QuitLifeBar(byte[] data) {
		super(10);
		this.username=readData(data);
	}
	
	public Packet10QuitLifeBar(String username) {
		super(10);
		this.username=username;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		//server.sendDataToAllClients(getData());
		server.sendDataToAllClientsExceptUsername(getData(), username);
	}

	public byte[] getData() {
		return ("10"+this.username).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
}

