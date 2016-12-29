package gui.renderers;

import gui.Images;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class GameInfoRenderer implements TableCellRenderer {


		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			
			JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 1));
			if (isSelected) {
				cellPanel.setBackground(table.getSelectionBackground());
			} else {				
				cellPanel.setBackground(Color.WHITE);
			}
			if (value instanceof String) {
				String data[] = ((String) value).split(" ");

				// Password
				if (Boolean.parseBoolean(data[3])) {
					cellPanel.add(new JLabel(Images.ICO_KEY));
				}
				
				// Difficulty
				JLabel diffLabel = null;
				switch (Integer.parseInt(data[0])) {
				case 0:
					diffLabel = new JLabel(Images.ICO_BRONZE);
					break;
				case 1:
					diffLabel = new JLabel(Images.ICO_SILVER);
					break;
				case 2:
					diffLabel = new JLabel(Images.ICO_GOLD);
					break;
				case 4:
					diffLabel = new JLabel(Images.ICO_PLATIN);
					break;
				case 8:
					diffLabel = new JLabel(Images.ICO_NIOB);	
					break;
				default:
					break;
				}
				if (diffLabel != null) {
					cellPanel.add(diffLabel);
				}
				
				// Game mode
				JLabel modeLabel = null;
				switch (Integer.parseInt(data[1])) {
				case 0:
					modeLabel = new JLabel(Images.ICO_UCAMP);
					break;
				case 1:
					modeLabel = new JLabel(Images.ICO_CAMP);
					break;
				case 2:
					modeLabel = new JLabel(Images.ICO_FREE);
					break;
				case 4:
					modeLabel = new JLabel(Images.ICO_PK);
					break;
				default:
					break;
				}
				if (modeLabel != null) {
					cellPanel.add(modeLabel);
				}
				
				// Locked?
				JLabel lockedLabel = null;
				if (Boolean.parseBoolean(data[2])) {
					lockedLabel = new JLabel(Images.ICO_LOCKED);
				} else {
					lockedLabel = new JLabel(Images.ICO_UNLOCKED);						
				}
				cellPanel.add(lockedLabel);
				
				
			}
			return cellPanel;
		}
	
	
}
