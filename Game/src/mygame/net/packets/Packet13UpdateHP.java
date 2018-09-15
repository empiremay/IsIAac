package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet13UpdateHP extends Packet {

	String username;
	double damage;
	
	public Packet13UpdateHP(byte[] data) {
		super(13);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.damage=Double.parseDouble(dataArray[1]);
	}
	
	public Packet13UpdateHP(String username, double damage) {
		super(13);
		this.username=username;
		this.damage=damage;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("13"+this.username+","+this.damage).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public double getDamage() {
		return damage;
	}
}
