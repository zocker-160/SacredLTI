package gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import core.data.Client;
import gui.models.ClientListModel;

public class ClientAddListener implements ActionListener {

	private JTextField clientName, clientAddr;
	private JFrame frame;
	private ClientListModel clm;
	private JTextField clientPort;
	private JButton btnAdd;
	
	
	public ClientAddListener(JTextField clientName, JTextField clientAddr, JTextField clientPort,
			JFrame frame, ClientListModel clients, JButton btnAdd) {
		super();
		this.clientName = clientName;
		this.clientAddr = clientAddr;
		this.frame = frame;
		this.clm = clients;
		this.clientPort = clientPort;
		this.btnAdd = btnAdd;
		
	}



	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (clientName.getText().equals("") || clientAddr.getText().equals("") || clientPort.equals("")) {
			JOptionPane.showMessageDialog(frame,"Empty name, address or port!","Error",JOptionPane.ERROR_MESSAGE);
		} else {
			btnAdd.setEnabled(false);
			clientName.setEnabled(false);
			clientAddr.setEnabled(false);
			clientPort.setEnabled(false);
			(new SwingWorker<Object, Object>() {

				@Override
				protected Object doInBackground() throws Exception {					
					
					try {
						
						InetAddress.getByName(clientAddr.getText());
						Client c = new Client(clientName.getText(),clientAddr.getText(),Integer.parseInt(clientPort.getText()));
						clm.addElement(c);
						clientName.setText("");
						clientAddr.setText("");
						clientPort.setText("2005");
					} catch (UnknownHostException e1) {
						JOptionPane.showMessageDialog(frame,"Can't resolve address!","Error",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					} finally {
						btnAdd.setEnabled(true);
						clientName.setEnabled(true);
						clientAddr.setEnabled(true);
						clientPort.setEnabled(true);
					}
					return null;
				}
			
			}).execute();
		}
		
	}

}
