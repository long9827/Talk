package client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MyRenderer extends DefaultListCellRenderer {
    private Color rowcolor;
    private ArrayList<Integer> rows;

    public MyRenderer() {
        rows = new ArrayList<>();
    }

    public MyRenderer(Color rowcolor) {
        this();
        this.rowcolor = rowcolor;
    }

    public MyRenderer(ArrayList rows, Color color) {
        this.rowcolor = color;
        this.rows = rows;
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (rows!=null){
            for (int i = 0; i < rows.size(); i++) {
                if (index == rows.get(i)) {
                    setBackground(this.rowcolor);
                    setFont(getFont().deriveFont((float) (getFont().getSize() + 2)));
                }
            }
        }
        return this;
    }
    public void setColor(int row) {
        for (int i=0; i< rows.size(); ++i) {
            if (rows.get(i)<row) {
                rows.set(i, rows.get(i) + 1);
            }
        }
        if (rows.indexOf(0) == -1) {
            rows.add(0);
        }
    }

    public void removeRow(int row) {
        int index = rows.indexOf(row);
        if (index != -1)
            rows.remove(index);
    }

}
