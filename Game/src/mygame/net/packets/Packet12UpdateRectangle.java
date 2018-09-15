package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet12UpdateRectangle extends Packet {

	int index;
	int x;
	int y;
	int ancho;
	int alto;
	
	public Packet12UpdateRectangle(byte[] data) {
		super(12);
		String[] dataArray=readData(data).split(",");
		this.index=Integer.parseInt(dataArray[0]);
		this.x=Integer.parseInt(dataArray[1]);
		this.y=Integer.parseInt(dataArray[2]);
		this.ancho=Integer.parseInt(dataArray[3]);
		this.alto=Integer.parseInt(dataArray[4]);
	}
	
	public Packet12UpdateRectangle(int index, int x, int y, int ancho, int alto) {
		super(12);
		this.index=index;
		this.x=x;
		this.y=y;
		this.ancho=ancho;
		this.alto=alto;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("12"+this.index+","+this.x+","+this.y+","+this.ancho+","+this.alto).getBytes();
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getAncho() {
		return ancho;
	}
	
	public int getAlto() {
		return alto;
	}
}
