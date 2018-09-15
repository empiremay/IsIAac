package mygame.net.packets;

import java.util.HashMap;
import java.util.Map;

import mygame.net.GameClient;
import mygame.net.GameServer;

public class Packet07SendLifeBars extends Packet {

	Map<String, Integer> playersHP=new HashMap<String, Integer>();
	
	public Packet07SendLifeBars(byte[] data) {
		super(07);
		String[] dataArray=readData(data).split(",");
		int i=0;
		while(i<dataArray.length) {
			playersHP.put(dataArray[i], Integer.parseInt(dataArray[i+1]));
			i+=2;
		}
	}
	
	public Packet07SendLifeBars(Map<String, Integer> playersHP) {
		super(07);
		this.playersHP=playersHP;
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
		//server.sendDataToAllClientsExceptUsername(getData(), username);
	}

	public byte[] getData() {
		String data="07";
		for(Map.Entry<String, Integer> entry: playersHP.entrySet()) {
			data+=entry.getKey()+",";		//Username
			data+=entry.getValue()+",";		//Life
		}
		data=data.substring(0, data.length()-1);	//Quitar la última ","
		return data.getBytes();
	}
	
	public Map<String, Integer> getPlayersHP() {
		return playersHP;
	}
}
