package com.hotel.utils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class ButtonColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener {

    private JTable table;
    private Action action;
    private JButton renderButton;
    private JButton editorButton;
    private String text;

    public ButtonColumn(JTable table, Action action, int column) {
        this.table = table;
        this.action = action;

        renderButton = new JButton();
        editorButton = new JButton();
        editorButton.setFocusPainted(false);
        editorButton.addActionListener(this);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        renderButton.setText((value == null) ? "" : value.toString());
        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        text = (value == null) ? "" : value.toString();
        editorButton.setText(text);
        return editorButton;
    }

    @Override
    public Object getCellEditorValue() {
        return text;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();

        int viewRow = table.getEditingRow();
        if (viewRow < 0) {
            System.out.println("No editing row selected.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        System.out.println("✅ Button clicked at view row: " + viewRow + ", model row: " + modelRow);

        ActionEvent event = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, String.valueOf(modelRow));
        action.actionPerformed(event);
    }
}
