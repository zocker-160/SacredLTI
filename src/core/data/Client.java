package core.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

public class Client {
	public String addr;
	public String name;
	public int port;

	public Client(String name, String addr, int port) {
		this.name = name;
		this.addr = addr;
		this.port = port;
	}

	@Override
	public String toString() {
		return name + "@" + addr+((port != 2005) ? ":"+port : "");
	}

	public static void saveClients(File f, List<Client> clients) throws IOException {
		if (!f.getName().endsWith(".clf")) {
			f = new File(f.getPath() + ".clf");
		}

		FileWriter writer = new FileWriter(f);
		for (int i = 0; i < clients.size(); i++) {
			writer.write(clients.get(i).toString() + "\n");
		}
		writer.close();

	}

	public static LinkedList<Client> loadClients(File f) throws IOException {
		FileReader reader = new FileReader(f);
		String clientStr = "";
		char c;
		LinkedList<Client> result = new LinkedList<>();
		while (reader.ready()) {
			c = (char) reader.read();
			if (c == '\n') {
				String[] strArr = clientStr.split("@");
				try {
					String[] addrPort = strArr[1].split(":");
					InetAddress.getByName(addrPort[0]);
					if (addrPort.length < 2) {
						result.add(new Client(strArr[0], addrPort[0],2005));
					} else {
						result.add(new Client(strArr[0], addrPort[0],Integer.parseInt(addrPort[1])));
					}
				} catch (UnknownHostException e) {
					JOptionPane.showMessageDialog(null,"Couldn't resovle client address! ("+strArr[0]+")",
							"Error!", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				clientStr = "";
			} else {
				clientStr += c;
			}
		}
		reader.close();
		return result;
	}

}
