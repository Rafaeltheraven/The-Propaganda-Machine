package net.yura.swing;

import java.util.Vector;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class JTable extends javax.swing.JTable {

    public JTable() {
        setSensibleDefaultHeight();
    }

    public JTable(TableModel dm) {
        super(dm);
        setSensibleDefaultHeight();
    }

    public JTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        setSensibleDefaultHeight();
    }

    public JTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        setSensibleDefaultHeight();
    }

    public JTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        setSensibleDefaultHeight();
    }

    public JTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        setSensibleDefaultHeight();
    }

    public JTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        setSensibleDefaultHeight();
    }
    
    private void setSensibleDefaultHeight() {
        setRowHeight(getFontMetrics(getFont()).getHeight());
    }
}
