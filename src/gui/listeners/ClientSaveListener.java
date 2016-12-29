package gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import core.data.Client;

public class ClientSaveListener implements ActionListener {

	JFileChooser chooser;
	JFrame frame;
	List<Client> clients;
	
	
	
	public ClientSaveListener(JFileChooser chooser, JFrame frame,List<Client> clients) {
		this.chooser = chooser;
		this.frame = frame;
		this.clients = clients;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int result = chooser.showSaveDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			try {
				Client.saveClients(f, clients);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
