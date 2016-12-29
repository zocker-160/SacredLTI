package gui.renderers;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class GameNameRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		JLabel nameLabel = new JLabel((String) value);
		if (isSelected) {
			cellPanel.setBackground(table.getSelectionBackground());
		} else {
			cellPanel.setBackground(table.getBackground());
		}
		cellPanel.add(nameLabel);
		return cellPanel;
	}

}
