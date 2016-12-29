package core.communication;

import gui.models.ServerTableModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.swing.SwingUtilities;

import core.data.Client;
import core.data.Server;

public class ClientSender {


	private static ClientSender CSInstance = null;

	private ClientSenderThread t;
	
	private ClientSender() {
		t = null;
	}

	public static void startClientSenderThread(List<Client> clients, InetAddress hostAddr, ServerTableModel stm) {
		if (CSInstance == null) {
			CSInstance = new ClientSender();
		}
		CSInstance.t = new ClientSenderThread(clients,hostAddr,stm);
		CSInstance.t.start();
	}

	public static void stopClientSenderThread() {
		if (CSInstance == null || CSInstance.t == null) return;
		CSInstance.t.term();
		try {
			CSInstance.t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static class ClientSenderThread extends Thread {
		
		private List<Client> clients;
		private boolean terminated;
		private InetAddress hostAddr;
		private ServerTableModel stm;
		private byte[] data;
		
		public ClientSenderThread(List<Client> clients2, InetAddress hostAddr2, ServerTableModel stm) {
			this.clients = clients2;
			this.hostAddr = hostAddr2;
			this.stm = stm;
			this.terminated = false;
		}
		
		public synchronized void term() {
			terminated =  true;
		}
		
		@Override
		public void run() {
			boolean term = false;
			byte[] buf = new byte[1024];
			byte[] buf2 = new byte[1024];
			data = new byte[1024];
			byte[] ip = null;
			int len = 0;
			
			Inflater decomp = null;
			Deflater comp = null;
			
			DatagramSocket socket = null;
			DatagramPacket packet = null;
			try {
				socket = new DatagramSocket(2006);
				socket.setBroadcast(true);
				socket.setSoTimeout(5000);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			while (!term) {
				try {
					
					// Check old servers
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							stm.validate();
						}
					});
					
					packet = new DatagramPacket(buf, buf.length);
					
					// Recieve packet
					socket.receive(packet);
								
					// Broadcast in local network
					InetAddress localHost = Inet4Address.getLocalHost();
					NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
					int prefixLen = (networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength()) / 8;
					
					byte[] address = localHost.getAddress();
					for (int i = prefixLen; i < 4; i++) {
						address[i] = -1;
					}
					packet.setAddress(InetAddress.getByAddress(address));//getByName("192.168.13.255"));
					packet.setPort(2005);
					socket.send(packet);
					
					// Get host ip
					ip = hostAddr.getAddress();
					
					// Decompress data
					decomp = new Inflater();
					decomp.setInput(buf, 4, packet.getLength() - 4);
					len = decomp.inflate(data);
					decomp.end();

					// Save server
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							stm.addServer(new Server(data));
						}
					});
					
					
					// Change IP
					for (int i = 0; i < 4; i++) {
						data[i + 4] = ip[3 - i];
					}
					
					// Compress data
					comp = new Deflater();
					comp.setInput(data, 0, len);
					comp.finish();
					len = comp.deflate(buf2);
					
					// Put data back into the packet
					for (int i = 0; i < len; i++) {
						buf[4 + i] = buf2[i];
					}
				
					packet.setData(buf, 0, len + 4);
					
					// Send packet to all Clients
					for (Object o : clients) {
						Client c = (Client) o; 
						try {
							packet.setPort(c.port);
							packet.setAddress(Inet6Address.getByName(c.addr));
							socket.send(packet);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DataFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// Check whether the thread should terminate!
				synchronized (this) {
					term = terminated;
				}
				
			}
			socket.close();
		}

		public synchronized boolean isRunning() {
			return !terminated;
		}
	}

	public static boolean isRunning() {
		return CSInstance != null && CSInstance.t != null && CSInstance.t.isRunning();
	}

}
