package gui.listeners;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import core.communication.ClientSender;
import core.data.Client;
import core.data.Config;
import core.data.ConfigConst;

public class MainFrameWindowListener extends WindowAdapter {

	private List<Client> clients;

	@Override
	public void windowClosed(WindowEvent e) {
		super.windowClosed(e);
		ClientSender.stopClientSenderThread();
		try {
			PrintWriter out = new PrintWriter(new File("config.cfg"),StandardCharsets.UTF_8.name());
			for (ConfigConst c : ConfigConst.values()) {
				out.println(c+"="+Config.getInstance().get(c));
			}
			out.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			Client.saveClients(new File(".lastSession.clf"),clients);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public MainFrameWindowListener(List<Client> clients) {
		this.clients = clients;
	}
	
}
