package gui.listeners;

import gui.models.ClientListModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import core.data.Client;

public class ClientLoadListener implements ActionListener {

	JFileChooser chooser;
	JFrame frame;
	ClientListModel clm;

	
	
	public ClientLoadListener(JFileChooser chooser, JFrame frame,
			ClientListModel clm) {
		super();
		this.chooser = chooser;
		this.frame = frame;
		this.clm = clm;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int result = chooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			if (f.exists()) {
				try {
					clm.clear();
					for (Client client : Client.loadClients(f)) {
						clm.addElement(client);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(frame, "File not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
