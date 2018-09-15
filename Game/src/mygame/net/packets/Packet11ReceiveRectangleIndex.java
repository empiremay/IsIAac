package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet11ReceiveRectangleIndex extends Packet {

	int index;
	
	public Packet11ReceiveRectangleIndex(byte[] data) {
		super(11);
		this.index=Integer.parseInt(readData(data));
	}
	
	public Packet11ReceiveRectangleIndex(int index) {
		super(11);
		this.index=index;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("11"+this.index).getBytes();
	}
	
	public int getIndex() {
		return index;
	}
}
