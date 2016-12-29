package gui.listeners;

import gui.models.ClientListModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.SwingUtilities;

import core.data.Client;

public class ClientRemoveListener implements ActionListener {

	private JList<Client> clientList;
	private ClientListModel clm;
	
	public ClientRemoveListener(JList<Client> clientList,
			ClientListModel clm) {
		super();
		this.clientList = clientList;
		this.clm = clm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				int idx = clientList.getSelectedIndex();
				if (idx != -1)
					clm.remove(idx);
			}
		});
	}

}
