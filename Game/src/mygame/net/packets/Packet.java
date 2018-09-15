package mygame.net.packets;

import mygame.net.GameClient;
import mygame.net.GameServer;

public abstract class Packet {

	public static enum PacketTypes {
		INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02), SHOOTMISSILE(03), UPDATEMISSILE(04), INITIALIZERECTANGLES(05), PLAYERCOLLISION(06), SENDLIFEBARS(07), QUITLIFEBAR(10), RECEIVERECTANGLEINDEX(11), UPDATERECTANGLE(12), UPDATEHP(13);
		
		private int packetId;
		private PacketTypes(int packetId) {
			this.packetId=packetId;
		}
		
		public int getId() {
			return packetId;
		}
	}
	
	public byte packetId;
	
	public Packet(int packetId) {
		this.packetId=(byte)packetId;
	}
	
	public abstract void writeData(GameClient client);
	
	public abstract void writeData(GameServer client);
	
	public String readData(byte[] data) {
		String message=new String(data).trim();
		return message.substring(2);
	}
	
	public abstract byte[] getData();
	
	public static PacketTypes lookupPacket(String packetId) {
		try {
			return lookupPacket(Integer.parseInt(packetId));
		} catch(NumberFormatException e) {
			return PacketTypes.INVALID;
		}
	}
	
	public static PacketTypes lookupPacket(int id) {
		for(PacketTypes p: PacketTypes.values()) {
			if(p.getId()==id) {
				return p;
			}
		}
		return PacketTypes.INVALID;
	}
}
