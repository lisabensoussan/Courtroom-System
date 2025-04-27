package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import control.Court;
import enums.Status;
import model.*;

/**
 * Panel for lawyers to view their cases and associated documents
 */
public class DocumentsView extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private Court court;
    private Lawyer lawyer;
    
    // UI Components
    private JTree casesTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JTable documentTable;
    private DefaultTableModel tableModel;
    private JTextArea documentContentArea;
    private JButton viewDocumentButton;
    private JButton printDocumentButton;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Colors for styling
    private final Color BUTTON_BG_COLOR = new Color(25, 42, 86); // Dark blue
    private final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    /**
     * Creates the document view panel for a lawyer
     * @param lawyer The lawyer who will use this panel
     */
    public DocumentsView(Lawyer lawyer) {
        this.court = Main.court;
        this.lawyer = lawyer;
        
        setTitle("My Documents");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(50, 50, 950, 600);
        
        // Create main content panel
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);
        
        // Create split pane for tree and documents
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);
        contentPane.add(mainSplitPane, BorderLayout.CENTER);
        
        // Create left panel with cases tree
        JPanel leftPanel = createCasesTreePanel();
        mainSplitPane.setLeftComponent(leftPanel);
        
        // Create right panel with document details
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setDividerLocation(250);
        
        // Top panel with document table
        JPanel documentTablePanel = createDocumentTablePanel();
        rightSplitPane.setTopComponent(documentTablePanel);
        
        // Bottom panel with document content
        JPanel documentContentPanel = createDocumentContentPanel();
        rightSplitPane.setBottomComponent(documentContentPanel);
        
        mainSplitPane.setRightComponent(rightSplitPane);
        
        // Load lawyer's cases and documents
        loadCasesAndDocuments();
    }
    
    /**
     * Creates the panel with cases tree
     */
    private JPanel createCasesTreePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createTitledBorder("My Cases"));
        
        // Create root node for tree
        rootNode = new DefaultMutableTreeNode("My Cases");
        treeModel = new DefaultTreeModel(rootNode);
        
        // Create tree
        casesTree = new JTree(treeModel);
        casesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        casesTree.setShowsRootHandles(true);
        
        // Set renderer for tree nodes
        casesTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, 
                    boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    
                    if (userObject instanceof CaseWrapper) {
                        CaseWrapper caseWrapper = (CaseWrapper) userObject;
                        setIcon(getCaseIcon(caseWrapper.getCase().getCaseStatus()));
                    } else if (userObject instanceof String && userObject.equals("My Cases")) {
                        setIcon(UIManager.getIcon("FileView.directoryIcon"));
                    }
                }
                
                return this;
            }
            
            private Icon getCaseIcon(Status status) {
                switch (status) {
                    case inProcess:
                        return UIManager.getIcon("FileView.fileIcon");
                    case finished:
                        return UIManager.getIcon("FileChooser.upFolderIcon");
                    case canceled:
                        return UIManager.getIcon("OptionPane.errorIcon");
                    default:
                        return UIManager.getIcon("FileView.fileIcon");
                }
            }
        });
        
        // Add selection listener to update document table
        casesTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) casesTree.getLastSelectedPathComponent();
            
            if (node == null) return;
            
            Object nodeInfo = node.getUserObject();
            if (nodeInfo instanceof CaseWrapper) {
                CaseWrapper caseWrapper = (CaseWrapper) nodeInfo;
                updateDocumentTable(caseWrapper.getCase());
            } else {
                clearDocumentTable();
            }
        });
        
        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(casesTree);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> loadCasesAndDocuments());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the panel with document table
     */
    private JPanel createDocumentTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Documents"));
        
        // Create document table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableModel.addColumn("Document Code");
        tableModel.addColumn("Title");
        tableModel.addColumn("Date");
        
        documentTable = new JTable(tableModel);
        documentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentTable.getTableHeader().setReorderingAllowed(false);
        
        // Add selection listener to show document content
        documentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = documentTable.getSelectedRow();
                if (selectedRow != -1) {
                    updateDocumentContent(selectedRow);
                    viewDocumentButton.setEnabled(true);
                    printDocumentButton.setEnabled(true);
                } else {
                    documentContentArea.setText("");
                    viewDocumentButton.setEnabled(false);
                    printDocumentButton.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(documentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the panel with document content
     */
    private JPanel createDocumentContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Document Content"));
        
        // Create document content text area
        documentContentArea = new JTextArea();
        documentContentArea.setEditable(false);
        documentContentArea.setWrapStyleWord(true);
        documentContentArea.setLineWrap(true);
        documentContentArea.setFont(new Font("Serif", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(documentContentArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        viewDocumentButton = createStyledButton("View Document");
        viewDocumentButton.setEnabled(false);
        viewDocumentButton.addActionListener(e -> viewDocument());
        buttonPanel.add(viewDocumentButton);
        
        printDocumentButton = createStyledButton("Print Document");
        printDocumentButton.setEnabled(false);
        printDocumentButton.addActionListener(e -> printDocument());
        buttonPanel.add(printDocumentButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Loads the lawyer's cases and documents into the tree
     */
    private void loadCasesAndDocuments() {
        // Clear existing nodes
        rootNode.removeAllChildren();
        
        // Add lawyer's cases
        for (Case caseObj : lawyer.getCasesHandled()) {
            DefaultMutableTreeNode caseNode = new DefaultMutableTreeNode(new CaseWrapper(caseObj));
            rootNode.add(caseNode);
        }
        
        // Update tree and expand root
        treeModel.reload();
        casesTree.expandRow(0);
        
        // Clear document table
        clearDocumentTable();
    }
    
    /**
     * Updates the document table for the selected case
     */
    private void updateDocumentTable(Case caseObj) {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Add documents for this case
        for (Document doc : caseObj.getDocumentsList()) {
            tableModel.addRow(new Object[] {
                doc.getCode(),
                doc.getTitle(),
                dateFormat.format(doc.getIssusedDate())
            });
        }
        
        // Clear document content
        documentContentArea.setText("");
        viewDocumentButton.setEnabled(false);
        printDocumentButton.setEnabled(false);
    }
    
    /**
     * Clears the document table
     */
    private void clearDocumentTable() {
        tableModel.setRowCount(0);
        documentContentArea.setText("");
        viewDocumentButton.setEnabled(false);
        printDocumentButton.setEnabled(false);
    }
    
    /**
     * Updates the document content area with the selected document
     */
    private void updateDocumentContent(int selectedRow) {
        if (selectedRow == -1) {
            documentContentArea.setText("");
            return;
        }
        
        int documentCode = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Find document with this code
        for (Case caseObj : lawyer.getCasesHandled()) {
            for (Document doc : caseObj.getDocumentsList()) {
                if (doc.getCode() == documentCode) {
                    documentContentArea.setText(doc.getContent());
                    documentContentArea.setCaretPosition(0);
                    return;
                }
            }
        }
    }
    
    /**
     * Opens a document in a separate dialog for viewing
     */
    private void viewDocument() {
        int selectedRow = documentTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int documentCode = (Integer) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Find document
        Document doc = null;
        for (Case caseObj : lawyer.getCasesHandled()) {
            for (Document d : caseObj.getDocumentsList()) {
                if (d.getCode() == documentCode) {
                    doc = d;
                    break;
                }
            }
            if (doc != null) break;
        }
        
        if (doc == null) return;
        
        // Create dialog for viewing document
        JDialog viewDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Document: " + title, true);
        viewDialog.setLayout(new BorderLayout(10, 10));
        viewDialog.setSize(800, 600);
        viewDialog.setLocationRelativeTo(this);
        
        // Document info panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(new JLabel("Title:"));
        infoPanel.add(new JLabel(doc.getTitle()));
        
        infoPanel.add(new JLabel("Date:"));
        infoPanel.add(new JLabel(dateFormat.format(doc.getIssusedDate())));
        
        infoPanel.add(new JLabel("Case:"));
        infoPanel.add(new JLabel(doc.getCasee().getCode()));
        
        viewDialog.add(infoPanel, BorderLayout.NORTH);
        
        // Document content
        JTextArea contentArea = new JTextArea(doc.getContent());
        contentArea.setEditable(false);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setFont(new Font("Serif", Font.PLAIN, 16));
        contentArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(contentArea);
        viewDialog.add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> viewDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        viewDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        viewDialog.setVisible(true);
    }
    
    /**
     * Prints the document (simulated)
     */
    private void printDocument() {
        int selectedRow = documentTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int documentCode = (Integer) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Find document
        Document doc = null;
        for (Case caseObj : lawyer.getCasesHandled()) {
            for (Document d : caseObj.getDocumentsList()) {
                if (d.getCode() == documentCode) {
                    doc = d;
                    break;
                }
            }
            if (doc != null) break;
        }
        
        if (doc == null) return;
        
        // In a real application, this would connect to a printer
        // For now, just show a message
        JOptionPane.showMessageDialog(this, 
                "Printing document: " + title + "\n\n" +
                "In a real application, this would send the document to a printer.", 
                "Print Document", 
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Creates a styled button with dark blue background
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_BG_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(20, 37, 81), 1));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(35, 52, 96)); // Lighter shade when hovering
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON_BG_COLOR);
                }
            }
        });
        
        return button;
    }
    
    /**
     * Wrapper class for Case objects to customize display in the tree
     */
    private class CaseWrapper {
        private Case caseObj;
        
        public CaseWrapper(Case caseObj) {
            this.caseObj = caseObj;
        }
        
        public Case getCase() {
            return caseObj;
        }
        
        @Override
        public String toString() {
            int documentCount = caseObj.getDocumentsList().size();
            return caseObj.getCode() + " - " + caseObj.getAccused().getFirstName() + " " + 
                   caseObj.getAccused().getLastName() + " (" + caseObj.getCaseStatus() + ") - " + 
                   documentCount + " document(s)";
        }
    }
}
