package core.data;

import java.nio.charset.StandardCharsets;

public class Server {
	private static long TIMEOUT = 5000l;
	
	public String name;
	
	public byte[] ip;
	public int port;
	
	public boolean locked;
	public boolean started;
	
	public int gameMode;
	public int difficulty;
	public int maxNumber;
	public int curNumber;
	
	public boolean pass;
	
	private long lastUpdate;
	
	public Server(byte[] data) {

		name = new String(data, 14, 48, StandardCharsets.UTF_16LE);
		name = name.trim();
		
		ip = new byte[4];
		for (int i = 0; i < 4; i++) {
			ip[i] = data[7-i];
		}
		
		int b0 = 0;
		int b1 = 0;
		b0 |= data[2];
		b1 |= data[3];
		port = b1 * 256 + b0;
		
		locked = (data[8] & 4) != 0; 
		pass = (data[8] & 2) != 0;
		started = data[8] >> 7 != 0;
		
		gameMode = (data[8] >> 4) & 7;
		difficulty = data[9];
	
		curNumber = data[12];
		maxNumber = data[13];
		update();
	};
	
	public void update() {
		lastUpdate = System.currentTimeMillis();
	}
	
	public boolean isAlive() {
		return lastUpdate + TIMEOUT >= System.currentTimeMillis(); 
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Server) && ((Server) obj).name.equals(name);
	}
	
}
