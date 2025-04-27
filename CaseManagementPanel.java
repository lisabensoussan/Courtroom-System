package view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import control.Court;
import enums.*;
import exceptions.FutureDateException;
import exceptions.NegativeNumberOfLossesAmountException;
import exceptions.ObjectDoesNotExistException;
import model.*;
import utils.MyFileLogWriter;

public class CaseManagementPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private Court court;
    private JTable caseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private JComboBox<String> caseTypeComboBox;
    private JComboBox<Accused> accusedComboBox;
    private JComboBox<Lawyer> lawyerComboBox;
    private JFormattedTextField openDateField;
    private JComboBox<Status> statusComboBox;
    private JTextField searchCaseCodeField;
    private JTextField searchAccusedField;
    private JTextField searchCaseTypeField;
    private JTextField searchLawyerField;
    private JFormattedTextField searchOpenDateField;
    private JComboBox<Status> searchStatusComboBox;

    
    // Additional fields for different case types
    private JPanel criminalPanel;
    private JPanel familyPanel;
    private JPanel financialPanel;
    
    // Criminal case fields
    private JTextField crimeSceneField;
    private JTextField crimeToolField;
    private JComboBox<Person> victimComboBox;
    
    // Family case fields
    private JTextField relationTypeField;
    
    // Financial case fields
    private JTextField lossesAmountField;
    private JTextField damagedItemField;

    public CaseManagementPanel() {
        super("Case Management", true, true, true, true); 
        this.court = Main.court;
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE); 
        initializeComponents();
        loadCaseData();
    }


    private void initializeComponents() {
        setSize(1200, 800);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create main split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(400);

        // Top panel for form
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(createFormPanel(), BorderLayout.CENTER);
        topPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        mainSplitPane.setTopComponent(topPanel);

        // Bottom panel for table
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(createSearchPanel(), BorderLayout.NORTH);
        bottomPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainSplitPane.setBottomComponent(bottomPanel);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        
        // Common fields panel
        JPanel commonFieldsPanel = new JPanel(new GridBagLayout());
        commonFieldsPanel.setBorder(BorderFactory.createTitledBorder("Case Information"));
        
        // Initialize common components
        caseTypeComboBox = new JComboBox<>(new String[]{"Criminal Case", "Family Case", "Financial Case"});
        accusedComboBox = new JComboBox<>();
        lawyerComboBox = new JComboBox<>();
        openDateField = new JFormattedTextField(dateFormat);
        statusComboBox = new JComboBox<>(Status.values());

        // Populate comboboxes
        updateAccusedComboBox();
        updateLawyerComboBox();

        // Add common fields
        addFormField(commonFieldsPanel, "Case Type:", caseTypeComboBox, 0, 0);
        addFormField(commonFieldsPanel, "Accused:", accusedComboBox, 0, 1);
        addFormField(commonFieldsPanel, "Lawyer:", lawyerComboBox, 0, 2);
        addFormField(commonFieldsPanel, "Open Date:", openDateField, 0, 3);
        addFormField(commonFieldsPanel, "Status:", statusComboBox, 0, 4);

        formPanel.add(commonFieldsPanel, BorderLayout.NORTH);

        // Create type-specific panels
        createCriminalPanel();
        createFamilyPanel();
        createFinancialPanel();

        // Add card layout for type-specific fields
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.add(criminalPanel, "Criminal Case");
        cardPanel.add(familyPanel, "Family Case");
        cardPanel.add(financialPanel, "Financial Case");

        // Add listener to switch panels
        caseTypeComboBox.addActionListener(e -> {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, (String) caseTypeComboBox.getSelectedItem());
        });

        formPanel.add(cardPanel, BorderLayout.CENTER);
        return formPanel;
    }

    private void createCriminalPanel() {
        criminalPanel = new JPanel(new GridBagLayout());
        criminalPanel.setBorder(BorderFactory.createTitledBorder("Criminal Case Details"));
        
        crimeSceneField = new JTextField();
        crimeToolField = new JTextField();
        victimComboBox = new JComboBox<>();
        updateVictimComboBox();

        addFormField(criminalPanel, "Crime Scene:", crimeSceneField, 0, 0);
        addFormField(criminalPanel, "Crime Tool:", crimeToolField, 0, 1);
        addFormField(criminalPanel, "Victim:", victimComboBox, 0, 2);
    }

    private void createFamilyPanel() {
        familyPanel = new JPanel(new GridBagLayout());
        familyPanel.setBorder(BorderFactory.createTitledBorder("Family Case Details"));
        
        relationTypeField = new JTextField();
        addFormField(familyPanel, "Relation Type:", relationTypeField, 0, 0);
    }

    private void createFinancialPanel() {
        financialPanel = new JPanel(new GridBagLayout());
        financialPanel.setBorder(BorderFactory.createTitledBorder("Financial Case Details"));
        
        lossesAmountField = new JTextField();
        damagedItemField = new JTextField();

        addFormField(financialPanel, "Losses Amount:", lossesAmountField, 0, 0);
        addFormField(financialPanel, "Damaged Item:", damagedItemField, 0, 1);
    }

    private void addFormField(JPanel panel, String label, JComponent field, int gridx, int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = gridx + 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        buttonPanel.add(createStyledButton("Add Case", e -> addCase()));
        buttonPanel.add(createStyledButton("Update Case", e -> updateCase()));
        buttonPanel.add(createStyledButton("Delete Case", e -> deleteCase()));
        buttonPanel.add(createStyledButton("Clear Form", e -> clearForm()));
        
        return buttonPanel;
    }

    private JButton createStyledButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(listener);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
        });

        return button;
    }

    
    /**
     * Adds a document listener to the search field to trigger filtering.
     */
    private void addSearchListener(JTextField searchField) {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterCases();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterCases();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterCases();
            }
        });
    }

    
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new GridLayout(2, 6, 5, 5)); // 2 rows, 6 columns layout for better structure
        
        // Initialize search fields
        searchCaseCodeField = new JTextField(10);
        searchAccusedField = new JTextField(10);
        searchCaseTypeField = new JTextField(10);
        searchLawyerField = new JTextField(10);
        searchOpenDateField = new JFormattedTextField(dateFormat);
        searchOpenDateField.setColumns(10);
        searchStatusComboBox = new JComboBox<>(Status.values()); // Dropdown for case status

        // Add event listeners to trigger search dynamically
        addSearchListener(searchCaseCodeField);
        addSearchListener(searchAccusedField);
        addSearchListener(searchCaseTypeField);
        addSearchListener(searchLawyerField);
        addSearchListener(searchOpenDateField);
        searchStatusComboBox.addActionListener(e -> filterCases());

        // Row 1: Labels
        searchPanel.add(new JLabel("Case Code:"));
        searchPanel.add(new JLabel("Accused Name:"));
        searchPanel.add(new JLabel("Case Type:"));
        searchPanel.add(new JLabel("Lawyer Name:"));
        searchPanel.add(new JLabel("Open Date:"));
        searchPanel.add(new JLabel("Case Status:"));

        // Row 2: Input Fields
        searchPanel.add(searchCaseCodeField);
        searchPanel.add(searchAccusedField);
        searchPanel.add(searchCaseTypeField);
        searchPanel.add(searchLawyerField);
        searchPanel.add(searchOpenDateField);
        searchPanel.add(searchStatusComboBox);

        return searchPanel;
    }


    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] columns = {
            "Case Code", "Type", "Accused", "Lawyer", "Open Date", 
            "Status", "Details"
        };
        for (String column : columns) {
            tableModel.addColumn(column);
        }

        caseTable = new JTable(tableModel);
        caseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelection();
            }
        });

        return new JScrollPane(caseTable);
    }

    // Helper methods for updating ComboBoxes
    private void updateAccusedComboBox() {
        accusedComboBox.removeAllItems();
        for (Accused accused : court.getAllAccuseds().values()) {
            accusedComboBox.addItem(accused);
        }
    }

    private void updateLawyerComboBox() {
        lawyerComboBox.removeAllItems();
        for (Lawyer lawyer : court.getAllLawyers().values()) {
            if (!(lawyer instanceof Judge)) {
                lawyerComboBox.addItem(lawyer);
            }
        }
    }

    private void updateVictimComboBox() {
        victimComboBox.removeAllItems();
        // Add potential victims (could be witnesses or other persons)
        for (Witness witness : court.getAllWitnesses().values()) {
            victimComboBox.addItem(witness);
        }
    }
    

    private void loadCaseData() {
        tableModel.setRowCount(0);
        for (Case caseObj : court.getAllCases().values()) {
            String details = "";
            if (caseObj instanceof CriminalCase) {
                CriminalCase cc = (CriminalCase) caseObj;
                details = "Crime Scene: " + cc.getCrimeScene();
            } else if (caseObj instanceof FamilyCase) {
                FamilyCase fc = (FamilyCase) caseObj;
                details = "Relation: " + fc.getRelationType();
            } else if (caseObj instanceof FinancialCase) {
                FinancialCase fc = (FinancialCase) caseObj;
                details = "Losses: $" + fc.getLossesAmount();
            }

            tableModel.addRow(new Object[]{
                caseObj.getCode(),
                caseObj.getCaseType(),
                caseObj.getAccused().getFirstName() + " " + caseObj.getAccused().getLastName(),
                caseObj.getLawyer() != null ? caseObj.getLawyer().getFirstName() + " " + caseObj.getLawyer().getLastName() : "Not Assigned",
                dateFormat.format(caseObj.getOpenedDate()),
                caseObj.getCaseStatus(),
                details
            });
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        caseTable.setRowSorter(sorter);

        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void addCase() {
        try {
            if (!validateForm()) {
                return;
            }

            String caseType = (String) caseTypeComboBox.getSelectedItem();
            Accused accused = (Accused) accusedComboBox.getSelectedItem();
            Lawyer lawyer = (Lawyer) lawyerComboBox.getSelectedItem();
            Date openDate = dateFormat.parse(openDateField.getText());
            Status status = (Status) statusComboBox.getSelectedItem();

            Case newCase = null;
            switch (caseType) {
                case "Criminal Case":
                    Person victim = (Person) victimComboBox.getSelectedItem();
                    newCase = new CriminalCase(
                        accused,                    // accused
                        openDate,                   // openedDate 
                        status,                     // caseStatus 
                        lawyer.getSpecialization(), // caseType - get from lawyer's specialization
                        lawyer,                     // lawyer
                        null,                       // verdict
                        victim,                     // victim
                        crimeSceneField.getText(),  // crimeScene
                        crimeToolField.getText()    // crimeTool
                    );
                    break;

                case "Family Case":
                    newCase = new FamilyCase(
                        accused,                                        // accused
                        openDate,                                      // openedDate
                        status,                                        // caseStatus
                        lawyer.getSpecialization(),                    // caseType - get from lawyer's specialization
                        lawyer,                                        // lawyer
                        null,                                          // verdict
                        (Person) victimComboBox.getSelectedItem(),     // victim  
                        relationTypeField.getText()                    // relationType
                    );
                    break;

                case "Financial Case":
                    double lossesAmount = Double.parseDouble(lossesAmountField.getText());
                    if (lossesAmount < 0) {
                        throw new NegativeNumberOfLossesAmountException("Losses amount cannot be negative");
                    }
                    newCase = new FinancialCase(
                        accused,                        // accused
                        openDate,                       // openedDate
                        status,                         // caseStatus
                        lawyer.getSpecialization(),     // caseType - get from lawyer's specialization
                        lawyer,                         // lawyer
                        null,                           // verdict
                        lossesAmount,                   // lossesAmount
                        damagedItemField.getText()      // damagedItem
                    );
                    break;
            }

            if (newCase != null && court.addCase(newCase)) {
                JOptionPane.showMessageDialog(this,
                    "Case added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadCaseData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to add case.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (NegativeNumberOfLossesAmountException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format. Please use dd/MM/yyyy.", "Date Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }           
                
                
                private void updateCase() {
                    try {
                        int selectedRow = caseTable.getSelectedRow();
                        if (selectedRow == -1) {
                            JOptionPane.showMessageDialog(this,
                                "Please select a case to update.", "No Selection",
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        if (!validateForm()) {
                            return;
                        }

                        int modelRow = caseTable.convertRowIndexToModel(selectedRow);
                        String caseCode = (String) tableModel.getValueAt(modelRow, 0);
                        Case existingCase = court.getRealCase(caseCode);

                        if (existingCase == null) {
                            throw new ObjectDoesNotExistException("Case not found");
                        }

                        // Update common fields
                        existingCase.setAccused((Accused) accusedComboBox.getSelectedItem());
                        existingCase.setLawyer((Lawyer) lawyerComboBox.getSelectedItem());
                        existingCase.setOpenedDate(dateFormat.parse(openDateField.getText()));
                        existingCase.setCaseStatus((Status) statusComboBox.getSelectedItem());

                        // Update type-specific fields
                        if (existingCase instanceof CriminalCase) {
                            CriminalCase cc = (CriminalCase) existingCase;
                            cc.setVictim((Person) victimComboBox.getSelectedItem());
                            cc.setCrimeScene(crimeSceneField.getText());
                            cc.setCrimeTool(crimeToolField.getText());
                        } else if (existingCase instanceof FamilyCase) {
                            FamilyCase fc = (FamilyCase) existingCase;
                            fc.setVictim((Person) victimComboBox.getSelectedItem());
                            fc.setRelationType(relationTypeField.getText());
                        } else if (existingCase instanceof FinancialCase) {
                            FinancialCase fc = (FinancialCase) existingCase;
                            double lossesAmount = Double.parseDouble(lossesAmountField.getText());
                            if (lossesAmount < 0) {
                                throw new NegativeNumberOfLossesAmountException("Losses amount cannot be negative");
                            }
                            fc.setLossesAmount(lossesAmount);
                            fc.setDamagedItem(damagedItemField.getText());
                        }

                        JOptionPane.showMessageDialog(this,
                            "Case updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadCaseData();
                        clearForm();

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                            "Error: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }

                private void deleteCase() {
                    int selectedRow = caseTable.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(this,
                            "Please select a case to delete.", "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this case?\n" +
                        "This will also delete all associated records.",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            int modelRow = caseTable.convertRowIndexToModel(selectedRow);
                            String caseCode = (String) tableModel.getValueAt(modelRow, 0);
                            Case caseToDelete = court.getRealCase(caseCode);

                            if (court.removeCase(caseToDelete)) {
                                JOptionPane.showMessageDialog(this,
                                    "Case deleted successfully!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                                loadCaseData();
                                clearForm();
                            } else {
                                JOptionPane.showMessageDialog(this,
                                    "Failed to delete case.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(this,
                                "Error: " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

                private void clearForm() {
                    caseTypeComboBox.setSelectedIndex(0);
                    accusedComboBox.setSelectedIndex(0);
                    lawyerComboBox.setSelectedIndex(0);
                    openDateField.setText("");
                    statusComboBox.setSelectedIndex(0);

                    // Clear criminal case fields
                    crimeSceneField.setText("");
                    crimeToolField.setText("");
                    if (victimComboBox.getItemCount() > 0) {
                        victimComboBox.setSelectedIndex(0);
                    }

                    // Clear family case fields
                    relationTypeField.setText("");

                    // Clear financial case fields
                    lossesAmountField.setText("");
                    damagedItemField.setText("");

                    caseTable.clearSelection();
                }

                private boolean validateForm() {
                    String caseType = (String) caseTypeComboBox.getSelectedItem();
                    
                    // Validate common fields
                    if (accusedComboBox.getSelectedItem() == null ||
                        lawyerComboBox.getSelectedItem() == null ||
                        openDateField.getText().trim().isEmpty()) {
                        
                        JOptionPane.showMessageDialog(this,
                            "Please fill all required fields.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    // Validate date
                    try {
                        Date openDate = dateFormat.parse(openDateField.getText());
                        if (openDate.after(new Date())) {
                            throw new FutureDateException("Open date cannot be in the future");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                            "Invalid date format. Use dd/MM/yyyy", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    // Validate type-specific fields
                    switch (caseType) {
                        case "Criminal Case":
                            if (crimeSceneField.getText().trim().isEmpty() ||
                                crimeToolField.getText().trim().isEmpty() ||
                                victimComboBox.getSelectedItem() == null) {
                                
                                JOptionPane.showMessageDialog(this,
                                    "Please fill all criminal case fields.", "Validation Error",
                                    JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                            break;

                        case "Family Case":
                            if (relationTypeField.getText().trim().isEmpty()) {
                                JOptionPane.showMessageDialog(this,
                                    "Please enter relation type.", "Validation Error",
                                    JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                            break;

                        case "Financial Case":
                            try {
                                double lossesAmount = Double.parseDouble(lossesAmountField.getText());
                                if (lossesAmount < 0) {
                                    throw new NegativeNumberOfLossesAmountException("Losses amount cannot be negative");
                                }
                                if (damagedItemField.getText().trim().isEmpty()) {
                                    throw new Exception("Please enter damaged item");
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(this,
                                    "Invalid losses amount.", "Validation Error",
                                    JOptionPane.ERROR_MESSAGE);
                                return false;
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(this,
                                    e.getMessage(), "Validation Error",
                                    JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                            break;
                    }

                    return true;
                }

                private void populateFormFromSelection() {
                    int selectedRow = caseTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = caseTable.convertRowIndexToModel(selectedRow);
                        String caseCode = (String) tableModel.getValueAt(modelRow, 0);
                        Case selectedCase = court.getRealCase(caseCode);

                        if (selectedCase != null) {
                            // Set common fields
                            if (selectedCase instanceof CriminalCase) {
                                caseTypeComboBox.setSelectedItem("Criminal Case");
                            } else if (selectedCase instanceof FamilyCase) {
                                caseTypeComboBox.setSelectedItem("Family Case");
                            } else if (selectedCase instanceof FinancialCase) {
                                caseTypeComboBox.setSelectedItem("Financial Case");
                            }

                            accusedComboBox.setSelectedItem(selectedCase.getAccused());
                            lawyerComboBox.setSelectedItem(selectedCase.getLawyer());
                            openDateField.setText(dateFormat.format(selectedCase.getOpenedDate()));
                            statusComboBox.setSelectedItem(selectedCase.getCaseStatus());

                            // Set type-specific fields
                            if (selectedCase instanceof CriminalCase) {
                                CriminalCase cc = (CriminalCase) selectedCase;
                                crimeSceneField.setText(cc.getCrimeScene());
                                crimeToolField.setText(cc.getCrimeTool());
                                victimComboBox.setSelectedItem(cc.getVictim());
                            } else if (selectedCase instanceof FamilyCase) {
                                FamilyCase fc = (FamilyCase) selectedCase;
                                relationTypeField.setText(fc.getRelationType());
                                victimComboBox.setSelectedItem(fc.getVictim());
                            } else if (selectedCase instanceof FinancialCase) {
                                FinancialCase fc = (FinancialCase) selectedCase;
                                lossesAmountField.setText(String.valueOf(fc.getLossesAmount()));
                                damagedItemField.setText(fc.getDamagedItem());
                            }
                        }
                    }
                }
                
                private void filterCases() {
                    String caseCode = searchCaseCodeField.getText().trim().toLowerCase();
                    String accusedName = searchAccusedField.getText().trim().toLowerCase();
                    String caseType = searchCaseTypeField.getText().trim().toLowerCase();
                    String lawyerName = searchLawyerField.getText().trim().toLowerCase();
                    String openDateText = searchOpenDateField.getText().trim();
                    String caseStatus = searchStatusComboBox.getSelectedItem() != null ?
                                        searchStatusComboBox.getSelectedItem().toString().toLowerCase() : "";

                    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                    caseTable.setRowSorter(sorter);

                    RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
                        @Override
                        public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                            String rowCaseCode = entry.getStringValue(0).toLowerCase(); // Case Code column
                            String rowAccused = entry.getStringValue(2).toLowerCase();  // Accused column
                            String rowCaseType = entry.getStringValue(1).toLowerCase(); // Case Type column
                            String rowLawyer = entry.getStringValue(3).toLowerCase();   // Lawyer column
                            String rowOpenDate = entry.getStringValue(4);  // Open Date column
                            String rowStatus = entry.getStringValue(5).toLowerCase();  // Status column

                            boolean matchesCaseCode = caseCode.isEmpty() || rowCaseCode.contains(caseCode);
                            boolean matchesAccused = accusedName.isEmpty() || rowAccused.contains(accusedName);
                            boolean matchesCaseType = caseType.isEmpty() || rowCaseType.contains(caseType);
                            boolean matchesLawyer = lawyerName.isEmpty() || rowLawyer.contains(lawyerName);
                            boolean matchesOpenDate = openDateText.isEmpty() || rowOpenDate.equals(openDateText);
                            boolean matchesStatus = caseStatus.isEmpty() || rowStatus.contains(caseStatus);

                            return matchesCaseCode && matchesAccused && matchesCaseType && matchesLawyer && matchesOpenDate && matchesStatus;
                        }
                    };

                    sorter.setRowFilter(filter);
                }

                
}