package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import control.Court;
import enums.Gender;
import model.*;

public class QueryManagementPanel extends JInternalFrame {
    private static final long serialVersionUID = 1L;
    private Court court;
    private JComboBox<String> querySelection;
    private JTextField inputField;
    private JComboBox<Case> caseComboBox;
    private JComboBox<Department> departmentComboBox;
    private JComboBox<Lawyer> lawyerComboBox;
    private JComboBox<String> genderComboBox;
    private JButton searchButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public QueryManagementPanel() {
        super("Query Management", true, true, true, true);
        this.court = Main.court;
        initializeComponents();
    }

    private void initializeComponents() {
        setSize(1000, 600);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        querySelection = new JComboBox<>(new String[]{
            "HowManyCasesBefore (Date)",
            "findInactiveCasesCountByDepartment ()",
            "findTheSuitableLawyer (Case)",
            "AppointManager (Department)",
            "differenceBetweenLongestAndShortestCase (Lawyer)",
            "findFamilyCasesWithWitnessesFromBothSides ()",
            "findCasesWithMoreThanThreeTestimoniesFromSameGender (Gender)",
            "findUniqueCrimeToolsByCrimeScene (Crime Scene)",
            "findFamilyCasesWithWitnessesFromBothSides ()"
        });

        inputField = new JTextField(15);
        caseComboBox = new JComboBox<>(court.getAllCases().values().toArray(new Case[0]));
        departmentComboBox = new JComboBox<>(court.getAllDepartments().values().toArray(new Department[0]));
        lawyerComboBox = new JComboBox<>(court.getAllLawyers().values().toArray(new Lawyer[0]));
        genderComboBox = new JComboBox<>(new String[]{"M", "F"});
        
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> executeQuery());

        topPanel.add(new JLabel("Select Query:"));
        topPanel.add(querySelection);
        topPanel.add(new JLabel("Input (if required):"));
        topPanel.add(inputField);
        topPanel.add(caseComboBox);
        topPanel.add(departmentComboBox);
        topPanel.add(lawyerComboBox);
        topPanel.add(genderComboBox);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        querySelection.addActionListener(e -> updateInputField());
        updateInputField();
    }

    private void updateInputField() {
        inputField.setVisible(false);
        caseComboBox.setVisible(false);
        departmentComboBox.setVisible(false);
        lawyerComboBox.setVisible(false);
        genderComboBox.setVisible(false);

        String selectedQuery = (String) querySelection.getSelectedItem();

        if (selectedQuery.contains("Date")) {
            inputField.setVisible(true);
            inputField.setText("Enter date (dd/MM/yyyy)");
        } else if (selectedQuery.contains("Case)")) {
            caseComboBox.setVisible(true);
        } else if (selectedQuery.contains("Department)")) {
            departmentComboBox.setVisible(true);
        } else if (selectedQuery.contains("Lawyer)")) {
            lawyerComboBox.setVisible(true);
        } else if (selectedQuery.contains("Gender")) {
            genderComboBox.setVisible(true);
        }
    }

    private void executeQuery() {
        String selectedQuery = (String) querySelection.getSelectedItem();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (selectedQuery.equals("HowManyCasesBefore (Date)")) {
            try {
                Date date = dateFormat.parse(inputField.getText());
                int result = court.howManyCasesBefore(date);
                tableModel.addColumn("Number of Cases");
                tableModel.addRow(new Object[]{result});
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (selectedQuery.equals("findInactiveCasesCountByDepartment ()")) {
            tableModel.addColumn("Department");
            tableModel.addColumn("Inactive Cases");
            
            HashMap<Department, Integer> results = court.findInActiveCasesCountByDepartment();
            for (Map.Entry<Department, Integer> entry : results.entrySet()) {
                tableModel.addRow(new Object[]{entry.getKey().getName(), entry.getValue()});
            }
        } else if (selectedQuery.equals("findTheSuitableLawyer (Case)")) {
            Case selectedCase = (Case) caseComboBox.getSelectedItem();
            Lawyer lawyer = court.findTheSuitableLawyer(selectedCase);
            tableModel.addColumn("Lawyer");
            tableModel.addRow(new Object[]{lawyer.getFirstName() + " " + lawyer.getLastName()});
        } else if (selectedQuery.equals("AppointManager (Department)")) {
            Department department = (Department) departmentComboBox.getSelectedItem();
            Employee newManager = court.AppointANewManager(department);
            tableModel.addColumn("New Manager");
            tableModel.addRow(new Object[]{newManager.getFirstName() + " " + newManager.getLastName()});
        } else if (selectedQuery.equals("differenceBetweenLongestAndShortestCase (Lawyer)")) {
            Lawyer lawyer = (Lawyer) lawyerComboBox.getSelectedItem();
            int difference = court.differenceBetweenTheLongestAndShortestCase(lawyer);
            tableModel.addColumn("Time Difference (Days)");
            tableModel.addRow(new Object[]{difference});
        } else if (selectedQuery.equals("findCasesWithMoreThanThreeTestimoniesFromSameGender (Gender)")) {
            Gender gender = Gender.valueOf((String) genderComboBox.getSelectedItem());
            ArrayList<Case> cases = court.findCasesWithMoreThanThreeTestimoniesFromSameGender(gender);
            tableModel.addColumn("Case Code");
            for (Case c : cases) {
                tableModel.addRow(new Object[]{c.getCode()});
            }
        }
    }
}
