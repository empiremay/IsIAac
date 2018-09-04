package mygame.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mygame.*;
import mygame.net.packets.*;
import mygame.net.packets.Packet.PacketTypes;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private MainClass game;
	private List<PlayerMP> connectedPlayers=new ArrayList<PlayerMP>();
	static Random rnd=new Random();
	
	Rectangle rectangle;
	
	public GameServer(MainClass game) {
		this.game=game;
		try {
			this.socket=new DatagramSocket(25565);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InitializeRectangles();
	}
	
	private void InitializeRectangles() {
		int ancho=rnd.nextInt(40)+20;
		int alto=rnd.nextInt(40)+20;
		rectangle=new Rectangle(rnd.nextInt(MainClass.WINDOW_WIDTH-ancho), rnd.nextInt(MainClass.WINDOW_HEIGHT-ancho), ancho, alto, 100);
		//Packet05InitializeRectangles packet=new Packet05InitializeRectangles(rnd.nextInt(MainClass.WINDOW_WIDTH-ancho), rnd.nextInt(MainClass.WINDOW_HEIGHT-ancho), ancho, alto, 100);
		//this.handleInitializeRectangles(packet);
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
			/*String message=new String(packet.getData());
			System.out.println("CLIENT ["+packet.getAddress().getHostAddress()+":"+packet.getPort()+"] > "+message);
			if(message.trim().equalsIgnoreCase("ping")) {
				sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
			}*/
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
				System.out.println("["+address.getHostAddress()+":"+port+"] "+((Packet00Login)packet).getUsername()+" has connected");
				PlayerMP player=new PlayerMP(MainClass.WINDOW_WIDTH/2, MainClass.WINDOW_HEIGHT/2, ((Packet00Login)packet).getUsername(), ((Packet00Login)packet).getMissileColor(), address, port);
				this.addConnection(player, (Packet00Login)packet);
				//Initialize rectangles packet
				packet=new Packet05InitializeRectangles(rectangle.getX(), rectangle.getY(), rectangle.getXSize(), rectangle.getYSize(), rectangle.getLife());
				this.handleInitializeRectangles((Packet05InitializeRectangles)packet);
				break;
			case DISCONNECT:
				packet=new Packet01Disconnect(data);
				System.out.println("["+address.getHostAddress()+":"+port+"] "+((Packet01Disconnect)packet).getUsername()+" has disconnected");
				this.removeConnection((Packet01Disconnect)packet);
				break;
			case MOVE:
				packet=new Packet02Move(data);
				this.handleMove((Packet02Move)packet);
				break;
			case SHOOTMISSILE:
				packet=new Packet03ShootMissile(data);
				this.handleShootMissile((Packet03ShootMissile)packet);
				break;
			case UPDATEMISSILE:
				packet=new Packet04UpdateMissile(data);
				this.handleUpdateMissile((Packet04UpdateMissile)packet);
				break;
		}
	}

	public void addConnection(PlayerMP player, Packet00Login packet) {
		boolean alreadyConnected=false;
		for(PlayerMP p:this.connectedPlayers) {
			if(player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if(p.ipAddress==null) {
					p.ipAddress=player.ipAddress;
				}
				if(p.port==-1) {
					p.port=player.port;
				}
				alreadyConnected=true;
			}
			else {
				sendData(packet.getData(), p.ipAddress, p.port);
				
				packet=new Packet00Login(p.getUsername(), p.getMissileColor());
				sendData(packet.getData(), player.ipAddress, player.port);
			}
		}
		if(!alreadyConnected) {
			this.connectedPlayers.add(player);
		}
	}
	
	public void removeConnection(Packet01Disconnect packet) {
		this.connectedPlayers.remove(getPlayerMPindex(packet.getUsername()));
		packet.writeData(this);
	}
	
	public PlayerMP getPlayerMP(String username) {
		for(PlayerMP player: this.connectedPlayers) {
			if(player.getUsername().equals(username)) {
				return player;
			}
		}
		return null;
	}
	
	public int getPlayerMPindex(String username) {
		int index=0;
		for(PlayerMP player: this.connectedPlayers) {
			if(player.getUsername().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet=new DatagramPacket(data, data.length, ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for(PlayerMP p: connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
	}
	
	private void handleMove(Packet02Move packet) {
		if(getPlayerMP(packet.getUsername())!=null) {
			//int index=getPlayerMPindex(packet.getUsername());
			//this.connectedPlayers.get(index).x=packet.getX();
			//this.connectedPlayers.get(index).y=packet.getY();
			packet.writeData(this);
		}
	}
	
	private void handleShootMissile(Packet03ShootMissile packet) {
		if(getPlayerMP(packet.getUsername())!=null) {
			//int index=getPlayerMPindex(packet.getUsername());
			//Missile m=new Missile(packet.getX(), packet.getY(), (int)packet.getSize(), packet.getDirection(), packet.getMissileColor(), packet.getAvance(), packet.getMissileReduction());
			//this.connectedPlayers.get(index).AddMissile(m);
			packet.writeData(this);
		}
	}
	
	private void handleUpdateMissile(Packet04UpdateMissile packet) {
		if(getPlayerMP(packet.getUsername())!=null) {
			packet.writeData(this);
		}
	}
	
	private void handleInitializeRectangles(Packet05InitializeRectangles packet) {
		packet.writeData(this);
	}
}
