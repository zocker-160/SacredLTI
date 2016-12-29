package gui.models;

import java.util.List;

import javax.swing.AbstractListModel;

import core.data.Client;

public class ClientListModel extends AbstractListModel<Object> {

	private static final long serialVersionUID = 7200309461382840624L;
	private List<Client> clients; 
	
	
	
	public ClientListModel(List<Client> clients2) {
		this.clients = clients2;
	}

	public void addElement(Client client) {
		int index = clients.size();
		clients.add(client);
		fireIntervalAdded(this, index, index);
	}

	@Override
	public Object getElementAt(int index) {
		return clients.get(index);
	}

	@Override
	public int getSize() {
		return clients.size();
	}

	public void remove(int index) {
		clients.remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	public void clear() {
		int size = clients.size();
		clients.clear();
		fireIntervalRemoved(this, 0, size);
	}

}
