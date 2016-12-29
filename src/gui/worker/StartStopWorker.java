package gui.worker;

import gui.models.ServerTableModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import core.communication.ClientSender;
import core.data.Client;
import core.data.Config;
import core.data.ConfigConst;

public class StartStopWorker extends SwingWorker<Object, Object> {

	private JTextField txtFieldHostAddr;
	private JButton btnStartStop;
	private List<Client> clients;
	private ServerTableModel stm;

	public StartStopWorker(JTextField txtFieldHostAddr, JButton btnStartStop,
			List<Client> clients, ServerTableModel stm) {
		this.txtFieldHostAddr = txtFieldHostAddr;
		this.btnStartStop = btnStartStop;
		this.clients = clients;
		this.stm = stm;
	}

	@Override
	protected Object doInBackground() throws Exception {
		if (new Boolean(Config.getInstance().get(ConfigConst.DO_AUTO_RETRIEVE))) {
			txtFieldHostAddr.setText(retrieveHostAddress());
		}
		String host = txtFieldHostAddr.getText();

		try {
			if (btnStartStop.getText().equals("Start!")) {
				InetAddress hostAddr = InetAddress.getByName(host);
				ClientSender.startClientSenderThread(clients, hostAddr, stm);
				btnStartStop.setText("Stop!");
				btnStartStop.setEnabled(true);
			} else {
				ClientSender.stopClientSenderThread();
				SwingUtilities.invokeAndWait(new Runnable() {
					
					@Override
					public void run() {
						stm.clear();
					}
				});
				if (!new Boolean(Config.getInstance().get(ConfigConst.DO_AUTO_RETRIEVE))) txtFieldHostAddr.setEnabled(true);
				btnStartStop.setText("Start!");
				btnStartStop.setEnabled(true);
			}
		} catch (UnknownHostException e) {
			txtFieldHostAddr.setEnabled(true);
			btnStartStop.setEnabled(true);
			e.printStackTrace();
		}

		return null;
	}

	private String retrieveHostAddress() throws IOException {
		URL whatismyip = new URL(Config.getInstance().get(ConfigConst.AUTO_RETRIEVE_ADDR));
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));

		return in.readLine(); //you get the IP as a String
	}

}
