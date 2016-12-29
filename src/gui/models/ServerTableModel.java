package gui.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.swing.table.AbstractTableModel;

import core.data.Server;

public class ServerTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 2943131084496008563L;
	
//	private List<Server> servers;
	private SortedMap<String, Server> servers;
	
	public ServerTableModel(SortedMap<String, Server> servers) {
		this.servers = servers;
	}
	
    public void addServer(Server s) {
        int row = servers.size();
    	servers.put(s.name, s);
    	if (row == servers.size()) {
    	} else {
    	}
    	fireTableRowsUpdated(0, servers.size());
    	fireTableRowsInserted(row, row);    		
    }

    private void removeServer(String name) {
    	Entry<String, Server>[] entries = servers.entrySet().toArray(new Entry[0]);
    	int row = 0;
        while (!entries[row].getValue().name.equals(name)) {
        	row++;
        }
        if (row == servers.size()) {
        	return;
        }
    	servers.remove(entries[row].getValue().name);
        fireTableRowsDeleted(row, row);
    }
	
    public void validate() {
    	List<Server> delList = new LinkedList<>();
    	for (Entry<String, Server> e : servers.entrySet()) {
			if (!e.getValue().isAlive()) {
				delList.add(e.getValue());
			}
		}
    	for (Server s : delList) {
			removeServer(s.name);
		}
    }
    
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return servers.size();
		
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		Entry<String, Server>[] entries = servers.entrySet().toArray(new Entry[0]);
		String result = "";
		Server s = entries[arg0].getValue();
		switch (arg1) {
		case 0:
			result = s.name + " - " + s.curNumber + "/" + s.maxNumber;
			break;
		case 1:
			result += s.difficulty + " " + s.gameMode + " " + s.locked + " " + s.pass;
			break;
		default:
			break;
		}
		return result;
	}

	public void clear() {
		int size = servers.size();
		fireTableRowsDeleted(0, size);
		servers.clear();
	}

}
