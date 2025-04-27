package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.util.List;
import java.util.stream.Collectors;

import control.Court;
import model.Witness;
import model.Testimony;

/**
 * Panel for viewing witnesses and their details
 */
public class WitnessManagementPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable witnessTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private Court court;

    /**
     * Create the panel for witness viewing
     */
    public WitnessManagementPanel() {
        court = Main.court;
        
        setTitle("Witness Management");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 900, 500);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Search Panel
        contentPane.add(createSearchPanel(), BorderLayout.NORTH);

        // Table Panel
        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        // Define table columns
        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Birth Date");
        tableModel.addColumn("Cases Related");
        tableModel.addColumn("Address");
        tableModel.addColumn("Phone Number");
        tableModel.addColumn("Email");
        tableModel.addColumn("Gender");
        
        witnessTable = new JTable(tableModel);
        witnessTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        witnessTable.getTableHeader().setReorderingAllowed(false);
        witnessTable.getTableHeader().setResizingAllowed(true);
        
        // Enable sorting & searching
        rowSorter = new TableRowSorter<>(tableModel);
        witnessTable.setRowSorter(rowSorter);

        scrollPane.setViewportView(witnessTable);
        
        // Load witnesses into table
        refreshWitnessTable();
    }
    
    /**
     * Creates the search panel
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        return searchPanel;
    }

    /**
     * Filters the table based on search input
     */
    private void filterTable() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    /**
     * Refreshes the witness table with current data
     */
    private void refreshWitnessTable() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Add all witnesses to the table
        for (Witness witness : court.getAllWitnesses().values()) {
            // Get related cases from testimonies
            List<String> relatedCases = witness.getTestimoniesList().stream()
                .map(testimony -> testimony.getCasee().getCode())
                .distinct()
                .collect(Collectors.toList());

            tableModel.addRow(new Object[]{
                witness.getId(),
                witness.getFirstName(),
                witness.getLastName(),
                witness.getBirthDate(),
                relatedCases.isEmpty() ? "None" : String.join(", ", relatedCases),
                witness.getAddress(),
                witness.getPhoneNumber(),
                witness.getEmail(),
                witness.getGender()
            });
        }
    }
}
