package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import control.Court;
import model.*;

public class AssignJudgesPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private JTable casesTable;
    private JTable judgesTable;
    private DefaultTableModel casesTableModel;
    private DefaultTableModel judgesTableModel;
    private Court court;
    private JButton assignButton;

    /**
     * Create the panel for assigning judges to cases
     */
    public AssignJudgesPanel() {
        court = Main.court;
        
        setTitle("Assign Judges to Cases");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setBounds(10, 10, 900, 600);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Cases Panel
        JPanel casesPanel = new JPanel(new BorderLayout());
        casesPanel.setBorder(new TitledBorder("Cases Awaiting Judge"));
        
        // Cases Table Model
        casesTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        casesTableModel.addColumn("Case Code");
        casesTableModel.addColumn("Case Type");
        casesTableModel.addColumn("Accused");
        casesTableModel.addColumn("Open Date");
        casesTableModel.addColumn("Current Status");
        
        // Cases Table
        casesTable = new JTable(casesTableModel);
        JScrollPane casesScrollPane = new JScrollPane(casesTable);
        casesPanel.add(casesScrollPane, BorderLayout.CENTER);
        
        // Judges Panel
        JPanel judgesPanel = new JPanel(new BorderLayout());
        judgesPanel.setBorder(new TitledBorder("Available Judges"));
        
        // Judges Table Model
        judgesTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        judgesTableModel.addColumn("ID");
        judgesTableModel.addColumn("Name");
        judgesTableModel.addColumn("Specialization");
        judgesTableModel.addColumn("Experience");
        judgesTableModel.addColumn("Active Cases");
        
        // Judges Table
        judgesTable = new JTable(judgesTableModel);
        JScrollPane judgesScrollPane = new JScrollPane(judgesTable);
        judgesPanel.add(judgesScrollPane, BorderLayout.CENTER);
        
        // Assign Button
        assignButton = new JButton("Assign Judge");
        assignButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignJudge();
            }
        });
        
        // Main Panel with Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, casesPanel, judgesPanel);
        splitPane.setDividerLocation(300);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(assignButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        loadCasesNeedingJudge();
        loadAvailableJudges();
    }
    
    /**
     * Loads cases that do not have a verdict
     */
    private void loadCasesNeedingJudge() {
        casesTableModel.setRowCount(0);
        
        for (Case caseObj : court.getAllCases().values()) {
            // Load cases without a verdict
            if (caseObj.getVerdict() == null) {
                casesTableModel.addRow(new Object[]{
                    caseObj.getCode(),
                    caseObj.getCaseType(),
                    caseObj.getAccused().getFirstName() + " " + caseObj.getAccused().getLastName(),
                    caseObj.getOpenedDate(),
                    caseObj.getCaseStatus()
                });
            }
        }
    }
    
    /**
     * Loads available judges who can take on more cases
     */
    private void loadAvailableJudges() {
        judgesTableModel.setRowCount(0);
        
        for (Object obj : court.getAllLawyers().values()) {
            if (obj instanceof Judge) {
                Judge judge = (Judge) obj;
                
                // Load judges with fewer than 5 cases
                int activeCases = (int) judge.getCasesPresided().stream()
                    .filter(c -> c.getCaseStatus() == enums.Status.inProcess)
                    .count();
                
                if (activeCases < 5) {
                    judgesTableModel.addRow(new Object[]{
                        judge.getId(),
                        judge.getFirstName() + " " + judge.getLastName(),
                        judge.getSpecialization(),
                        judge.getExperienceYear() + " years",
                        activeCases
                    });
                }
            }
        }
    }
    
    /**
     * Assigns selected judge to selected case
     */
    private void assignJudge() {
        int caseRow = casesTable.getSelectedRow();
        int judgeRow = judgesTable.getSelectedRow();
        
        if (caseRow == -1 || judgeRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a case and a judge to assign.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String caseCode = (String) casesTableModel.getValueAt(caseRow, 0);
        int judgeId = (int) judgesTableModel.getValueAt(judgeRow, 0);
        
        Case caseObj = court.getRealCase(caseCode);
        Judge judge = court.getRealJudge(judgeId);
        
        if (caseObj != null && judge != null) {
            // Create a new verdict with the selected judge
            Verdict verdict = new Verdict(
                "Pending Final Review", 
                new Date(), 
                judge, 
                caseObj, 
                null
            );
            
            // Add verdict to the case
            caseObj.setVerdict(verdict);
            
            // Add case to judge's presided cases
            judge.getCasesPresided().add(caseObj);
            
            // Refresh tables
            loadCasesNeedingJudge();
            loadAvailableJudges();
            
            JOptionPane.showMessageDialog(this, 
                "Judge successfully assigned to case.", 
                "Assignment Successful", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error assigning judge to case.", 
                "Assignment Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}