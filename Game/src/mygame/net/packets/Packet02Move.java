package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet02Move extends Packet {

	private String username;
	int x;
	int y;
	
	public Packet02Move(byte[] data) {
		super(02);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.x=Integer.parseInt(dataArray[1]);
		this.y=Integer.parseInt(dataArray[2]);
	}
	
	public Packet02Move(String username, int x, int y) {
		super(02);
		this.username=username;
		this.x=x;
		this.y=y;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("02"+this.username+","+this.x+","+this.y).getBytes();
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
}

