package mygame.net;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import mygame.MainClass;
import mygame.PlayerMP;
import mygame.net.packets.Packet;
import mygame.net.packets.Packet00Login;
import mygame.net.packets.Packet01Disconnect;
import mygame.net.packets.Packet02Move;
import mygame.net.packets.Packet03ShootMissile;
import mygame.net.packets.Packet04UpdateMissile;
import mygame.net.packets.Packet05InitializeRectangles;
import mygame.net.packets.Packet.PacketTypes;

public class GameClient extends Thread {

	private InetAddress ipAddress;
	private DatagramSocket socket;
	private MainClass game;
	boolean isServer;	//True if this client is also server
	
	public GameClient(MainClass game, String ipAddress, boolean isServer) {
		this.game=game;
		this.isServer=isServer;
		try {
			this.socket=new DatagramSocket();
			this.ipAddress=InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		while(true) {
			byte[] data=new byte[1024];		//Data array that will be sent to the server
			DatagramPacket packet=new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			//String message=new String(packet.getData());
			//System.out.println("SERVER > "+message);
		}
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message=new String(data).trim();
		PacketTypes type=Packet.lookupPacket(message.substring(0, 2));
		Packet packet=null;
		switch(type) {
			default:
			case INVALID:
				break;
			case LOGIN:
				packet=new Packet00Login(data);
				System.out.println("["+address.getHostAddress()+":"+port+"] "+((Packet00Login)packet).getUsername()+" has joined the game");
				PlayerMP player=new PlayerMP(MainClass.WINDOW_WIDTH/2, MainClass.WINDOW_HEIGHT/2, ((Packet00Login)packet).getUsername(), ((Packet00Login)packet).getMissileColor(), address, port);
				game.AddPlayerMP(player);
				break;
			case DISCONNECT:
				packet=new Packet01Disconnect(data);
				System.out.println("["+address.getHostAddress()+":"+port+"] "+((Packet01Disconnect)packet).getUsername()+" has left the game");
				game.RemovePlayerMP(((Packet01Disconnect)packet).getUsername());
				break;
			case MOVE:
				packet=new Packet02Move(data);
				handleMove((Packet02Move)packet);
				break;
			case SHOOTMISSILE:
				packet=new Packet03ShootMissile(data);
				handleShootMissile((Packet03ShootMissile)packet);
				break;
			case UPDATEMISSILE:
				packet=new Packet04UpdateMissile(data);
				handleUpdateMissile((Packet04UpdateMissile)packet);
				break;
			case INITIALIZERECTANGLES:
				packet=new Packet05InitializeRectangles(data);
				handleInitializeRectangles((Packet05InitializeRectangles)packet);
				break;
		}
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet=new DatagramPacket(data, data.length, ipAddress, 25565);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleMove(Packet02Move packet) {
		this.game.updatePlayerPosition(packet.getUsername(), packet.getX(), packet.getY());
	}
	
	private void handleShootMissile(Packet03ShootMissile packet) {
		this.game.shootMissile(packet.getUsername(), packet.getX(), packet.getY(), packet.getSize(), packet.getDirection(), packet.getMissileColor(), packet.getAvance(), packet.getMissileReduction());
	}
	
	private void handleUpdateMissile(Packet04UpdateMissile packet) {
		this.game.updateMissile(packet.getUsername(), packet.getMissile());
	}
	
	private void handleInitializeRectangles(Packet05InitializeRectangles packet) {
		this.game.initializeRectangles(packet.getX(), packet.getY(), packet.getXSize(), packet.getYSize(), packet.getLife());
	}
}
