package lu.mullerwegener.etiquettes;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class EtiquettesTableModel extends AbstractTableModel{

	private String[] header = {"Service", "Code", "Compteur"};
	private String[][] data;

	EtiquettesTableModel(String[][] pdata){		
		this.data = pdata;		
	}

	public boolean isCellEditable(int row, int column) {
		// On bloque toutes les colonnes sauf compteur
		if(column != 2){
			return false;		        
		}else{
			return true;
		}
	}

	public String getColumnName(int column) {
		return this.header[column];
	}

	public void setData(String[][] pdata){
		this.data = pdata;
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}

	@Override
	public int getRowCount() {
		if(this.data != null){
			return this.data.length;
		}else{
			return 0;
		}
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return data[arg0][arg1];
	}


}
