package gui;

import gui.listeners.ClientAddListener;
import gui.listeners.ClientLoadListener;
import gui.listeners.ClientSaveListener;
import gui.listeners.MainFrameWindowListener;
import gui.models.ClientListModel;
import gui.models.ServerTableModel;
import gui.renderers.GameInfoRenderer;
import gui.renderers.GameNameRenderer;
import gui.worker.StartStopWorker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import core.communication.ClientSender;
import core.data.Client;
import core.data.Config;
import core.data.ConfigConst;
import core.data.Server;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -8026416994513756565L;
	private JTextField txtFieldName;
	private JTextField txtFieldAddress;
	private JTextField txtFieldHostAddr;
	private final JPanel panel_2 = new JPanel();
	private JTable serverTable;

	private List<Client> clients;
	private JButton btnStartStop;
	private ClientListModel clm;
	private SortedMap<String, Server> servers;
	private ServerTableModel stm;

	private Config config;
	private JCheckBoxMenuItem chckbxmntmAutoRetrieveAddress;
	private JFileChooser loadSaveClientsChooser;
	private JList<Object> listClients;
	private JTextField txtFieldPort;

	public MainFrame() {
		setTitle("SacredLTI");
		clients = Collections.synchronizedList(new LinkedList<Client>());
		File f = new File(".lastSession.clf");
		if (f.exists()) {
			try {
				clients.addAll(Client.loadClients(f));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		servers = Collections
				.synchronizedSortedMap(new TreeMap<String, Server>());

		// Load the config if available, otherwise create an empty config.
		config = null;
		if (new File("config.cfg").exists()) {
			try {
				config = Config.loadConfig("config.cfg");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		if (config == null) {
			config = Config.getInstance();
			config.put(ConfigConst.AUTO_RETRIEVE_ADDR, "http://checkip.amazonaws.com");
			config.put(ConfigConst.HOST, "");
			config.put(ConfigConst.DO_AUTO_RETRIEVE, "true");
		}

		clm = new ClientListModel(clients);
		stm = new ServerTableModel(servers);

		loadSaveClientsChooser = new JFileChooser();
		loadSaveClientsChooser.setFileFilter(new FileNameExtensionFilter(
				"Client Lists (*.clf)", "clf"));

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new MainFrameWindowListener(clients));
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel_3 = new JPanel();
		getContentPane().add(panel_3, BorderLayout.NORTH);
		panel_3.setLayout(new MigLayout("", "[][grow][]", "[23px]"));

		JLabel lblAddress_1 = new JLabel("Host address:");
		panel_3.add(lblAddress_1, "cell 0 0,alignx left,aligny center");

		txtFieldHostAddr = new JTextField(config.get(ConfigConst.HOST));
		panel_3.add(txtFieldHostAddr, "cell 1 0,grow");
		txtFieldHostAddr.setMaximumSize(new Dimension(2147483647, 20));
		txtFieldHostAddr.setMinimumSize(new Dimension(6, 16));
		txtFieldHostAddr.setPreferredSize(new Dimension(6, 14));
		txtFieldHostAddr.setColumns(10);
		txtFieldHostAddr.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				config.put(ConfigConst.HOST, txtFieldHostAddr.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				config.put(ConfigConst.HOST, txtFieldHostAddr.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				config.put(ConfigConst.HOST, txtFieldHostAddr.getText());
			}
		});
		btnStartStop = new JButton("Start!");
		btnStartStop.setMinimumSize(new Dimension(60, 23));
		panel_3.add(btnStartStop, "cell 2 0,grow");
		btnStartStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnStartStop.setEnabled(false);
				txtFieldHostAddr.setEnabled(false);
				(new StartStopWorker(txtFieldHostAddr, btnStartStop, clients,
						stm)).execute();

			}
		});
		btnStartStop.setMaximumSize(new Dimension(80, 20));
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel_2.add(tabbedPane, "cell 0 0,grow");

		JPanel panel = new JPanel();
		tabbedPane.addTab("Clients", null, panel, null);
		panel.setLayout(new MigLayout("", "[][grow][][grow][][40px][]", "[grow][]"));

		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1, "cell 0 0 7 1,grow");

		listClients = new JList<>();
		listClients.setSelectionBackground(Color.LIGHT_GRAY);
		listClients.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					final int[] indices = listClients.getSelectedIndices();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							for (int i = 0; i < indices.length; i++) {
								clm.remove(indices[i]-i);
							}
						}
					});
				}
			}
		});
		scrollPane_1.setViewportView(listClients);
		listClients.setModel(clm);

		JLabel lblName = new JLabel("Name:");
		panel.add(lblName, "cell 0 1,alignx left,aligny center");

		txtFieldName = new JTextField();
		panel.add(txtFieldName, "cell 1 1,grow");
		txtFieldName.setColumns(10);

		JLabel lblAddress = new JLabel("Address:");
		panel.add(lblAddress, "cell 2 1,growx,aligny center");

		txtFieldAddress = new JTextField();
		panel.add(txtFieldAddress, "flowx,cell 3 1,grow");
		txtFieldAddress.setColumns(10);

		JLabel lblPort = new JLabel("Port:");
		panel.add(lblPort, "cell 4 1");

		txtFieldPort = new JTextField();
		txtFieldPort.setText("2005");
		panel.add(txtFieldPort, "cell 5 1,grow");
		txtFieldPort.setColumns(10);
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ClientAddListener(txtFieldName,
				txtFieldAddress,txtFieldPort, this, clm, btnAdd));
		panel.add(btnAdd, "cell 6 1,grow");
		
		

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Server", null, panel_1, null);
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.WHITE);
		panel_1.add(scrollPane, "cell 0 0,grow");

		serverTable = new JTable();
		serverTable.setSelectionBackground(Color.LIGHT_GRAY);
		serverTable.setShowVerticalLines(false);
		// serverTable.setForeground(Color.WHITE);
		// serverTable.setBackground(Color.BLACK);
		serverTable.setTableHeader(null);
		serverTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		serverTable.setShowHorizontalLines(false);
		serverTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		serverTable.setModel(stm);
		serverTable.setRowHeight(18);
		serverTable.setFillsViewportHeight(true);
		serverTable.getColumn(serverTable.getColumnName(1)).setPreferredWidth(
				70);
		serverTable.getColumn(serverTable.getColumnName(1)).setMaxWidth(70);
		serverTable.getColumn(serverTable.getColumnName(1)).setMinWidth(70);
		serverTable.getColumn(serverTable.getColumnName(0)).setCellRenderer(
				new GameNameRenderer());
		serverTable.getColumn(serverTable.getColumnName(1)).setCellRenderer(
				new GameInfoRenderer());
		scrollPane.setViewportView(serverTable);


		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmLoadClients = new JMenuItem("Load Clients...");
		mntmLoadClients.addActionListener(new ClientLoadListener(
				loadSaveClientsChooser, this, clm));
		mntmLoadClients.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmLoadClients);

		JMenuItem mntmSaveClients = new JMenuItem("Save Clients...");
		mntmSaveClients.addActionListener(new ClientSaveListener(
				loadSaveClientsChooser, this, clients));
		mntmSaveClients.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmSaveClients);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmQuit);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		chckbxmntmAutoRetrieveAddress = new JCheckBoxMenuItem(
				"Auto Retrieve Address");
		chckbxmntmAutoRetrieveAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				config.put(ConfigConst.DO_AUTO_RETRIEVE, String
						.valueOf(chckbxmntmAutoRetrieveAddress.isSelected()));
				if (chckbxmntmAutoRetrieveAddress.isSelected()) {
					txtFieldHostAddr.setEnabled(false);
				} else {
					if (!ClientSender.isRunning()) {
						txtFieldHostAddr.setEnabled(true);
					}
				}
			}
		});
		if (config.get(ConfigConst.DO_AUTO_RETRIEVE).equals("true"))
			chckbxmntmAutoRetrieveAddress.doClick();
		chckbxmntmAutoRetrieveAddress.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_D, InputEvent.CTRL_MASK));
		mnSettings.add(chckbxmntmAutoRetrieveAddress);

		pack();
		setLocationRelativeTo(null);
		
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
	}

}
