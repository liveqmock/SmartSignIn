package com.ssi.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import com.ssi.i18n.Messages;
import com.wicky.tdl.SimpleTableModel;
import com.wicky.tdl.SimpleTodoTable;
import com.wicky.util.DrawableUtils;
import com.wicky.util.JTableHelper;

public class RecordView extends JPanel implements ActionListener {
    private static final long serialVersionUID = 4479482587513212049L;
    
    private JButton btnHome;
    private JButton btnAdd;
    private JButton btnClear;
	
    private SimpleTodoTable todoTable;
    
    private JTextField tfSearch;
    
    public RecordView() {
        Dimension frameSize = MainView.getFrame().getSize();
        int frameWidth = (int)frameSize.getWidth();
        int frameHeight = (int)frameSize.getHeight();
        
        this.setOpaque(false);
        this.setLayout(null);
        
        todoTable = new SimpleTodoTable();
        
        ImageIcon imgHome = new ImageIcon("res/home.png"); //$NON-NLS-1$
        btnHome = DrawableUtils.createImageButton("", imgHome, null); //$NON-NLS-1$
        btnHome.setBounds(20, 20, imgHome.getIconWidth(),
                imgHome.getIconHeight());
        DrawableUtils.setMouseListener(btnHome, "res/home"); //$NON-NLS-1$
        btnHome.addActionListener(this);
        this.add(btnHome);
        
        JButton btnAdd2 = getBtnAdd();
        btnAdd2.setBounds(100, 15, 150, 25);
        this.add(btnAdd2);
        
        JButton btnClear2 = getBtnClear();
        btnClear2.setBounds(100, 40, 150, 25);
        this.add(btnClear2);
        
        JScrollPane panelTable = getPanelTable();
        panelTable.setBounds(0, 70, frameWidth - 3, frameHeight - 200);
        this.add(panelTable);
        
        JLabel labelSearch = getLabelSearch();
        labelSearch.setBounds(0, frameHeight - 120, 170, 30);
        this.add(labelSearch);
        
        JTextField tfSearch2 = getTfSearch();
        tfSearch2.setBounds(170, frameHeight - 120, frameWidth - 170 - 10, 30);
        this.add(tfSearch2);
        
    }
    
    private JButton getBtnAdd() {
        Dimension btnSize = new Dimension(150, 25);
        
        btnAdd = new JButton(Messages.getString("RecordView.btn_add")); //$NON-NLS-1$
        btnAdd.setPreferredSize(btnSize);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = todoTable.dataModel.addRow();
                todoTable.stopCellEditing();
                tfSearch.setText(null);
                todoTable.refreshTable();
                ListSelectionModel model = todoTable.getSelectionModel();
                model.clearSelection();
                model.setSelectionInterval(--row, row);
                JTableHelper.scrollCellToCenter(todoTable, row, 1);
            }
        });
        return btnAdd;
    }
    
    private JButton getBtnClear() {
        Dimension btnSize = new Dimension(150, 25);
        
        btnClear = new JButton(Messages.getString("RecordView.btn_clear")); //$NON-NLS-1$
        btnClear.setPreferredSize(btnSize);
        btnClear.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                todoTable.stopCellEditing();
                tfSearch.setText(null);
                int result = JOptionPane.showConfirmDialog(todoTable, Messages.getString("RecordView.btn_clear_confirm"), 
                		Messages.getString("RecordView.btn_clear_confirm_title"), 
                		JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
                if(result == JOptionPane.YES_OPTION){
                    int rowCount = todoTable.dataModel.getRowCount();
                    for (int rowId = 0;rowId < rowCount;rowId++) {
                        Boolean value = (Boolean) todoTable.dataModel.getValueAt(rowId, 3);
                        if(value){
                            todoTable.dataModel.removeRow(rowId);
                            rowId--;rowCount--;
                        }
                    }
                    todoTable.refreshTable();
                }
            }
        });
        return btnClear;
    }
    
    private JScrollPane getPanelTable() {
        JScrollPane scrollpane = new JScrollPane(todoTable);
        scrollpane.setOpaque(false);
        scrollpane.setBackground(null);
        scrollpane.setBorder(null);
        
        return scrollpane;
    }

    private JTextField getTfSearch() {
        tfSearch = new JTextField();
        tfSearch.setForeground(Color.RED);
        tfSearch.setFont(tfSearch.getFont().deriveFont(Font.BOLD));
        tfSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                todoTable.stopCellEditing();
            }
        });
        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    tfSearch.setText(null);
                }
            }
        });
        tfSearch.getDocument().addDocumentListener(todoTable);
        return tfSearch;
    }

    private JLabel getLabelSearch() {
        JLabel label = new JLabel(Messages.getString("RecordView.label_search")); //$NON-NLS-1$
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                tfSearch.requestFocus();
                tfSearch.selectAll();
            }
        });
        label.setToolTipText(Messages.getString("RecordView.label_search_tip")); //$NON-NLS-1$
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHome) {
            todoTable.stopCellEditing();
            todoTable.refreshTable();
            
            JFrame frame = MainView.getFrame();
            frame.getContentPane().remove(this);
            JPanel panel = ((MainView) frame).getMainJpanel();
            frame.getContentPane().add(panel);
            frame.getContentPane().validate();
            frame.getContentPane().repaint();
        }
    }

	public SimpleTableModel getTableModel() {
		return todoTable.dataModel;
	}

}