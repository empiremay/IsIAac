package mygame;

import java.net.InetAddress;

public class PlayerMP extends Player {

	public InetAddress ipAddress;
	public int port;
	
	public PlayerMP(int x, int y, InputHandler input, String username, String missileColor, InetAddress ipAddress, int port) {
		super(x, y, input, username, missileColor);
		this.ipAddress=ipAddress;
		this.port=port;
	}
	
	public PlayerMP(int x, int y, String username, String missileColor, InetAddress ipAddress, int port) {
		super(x, y, null, username, missileColor);
		this.ipAddress=ipAddress;
		this.port=port;
	}
}
