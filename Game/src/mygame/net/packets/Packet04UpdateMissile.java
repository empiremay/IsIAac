package mygame.net.packets;

import mygame.Missile;
import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet04UpdateMissile extends Packet {

	private String username;		//Propietary of the missile
	Missile m;
	int index;
	
	public Packet04UpdateMissile(byte[] data) {
		super(04);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.index=Integer.parseInt(dataArray[1]);
	}
	
	public Packet04UpdateMissile(String username, Missile m, int index) {
		super(04);
		this.username=username;
		this.m=m;
		this.index=index;
	}
	
	public void writeData(GameClient client) {
		client.sendData(getData());
	}
	
	public void writeData(GameServer server) {
		//server.sendDataToAllClients(getData());
		server.sendDataToAllClientsExceptUsername(getData(), username);
	}
	
	public byte[] getData() {
		return ("04"+this.username+","+this.index).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public Missile getMissile() {
		return m;
	}
	
	public int getIndex() {
		return index;
	}
}
