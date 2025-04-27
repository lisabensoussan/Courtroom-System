package view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import control.Court;
import model.*;

/**
 * Main admin view for the Court Management System
 */
public class AdminView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JDesktopPane desktopPane;
    private Court court;
    private JMenuBar menuBar;
    
    // Track internal frames to prevent duplicates
    private JInternalFrame employeeFrame = null;
    private JInternalFrame lawyerFrame = null;
    private JInternalFrame judgeFrame = null;
    private JInternalFrame departmentFrame = null;
    private JInternalFrame courtroomFrame = null;
    private JInternalFrame caseFrame = null;
    private JInternalFrame accusedFrame = null;
    private JInternalFrame witnessFrame = null;
    private JInternalFrame queryFrame = null;

    /**
     * Create the frame for Admin view.
     */
    public AdminView() {
        this.court = Main.court;
        
        setTitle("HRS Court Management System - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1024, 768);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Create menu bar
        createMenuBar();
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create desktop pane for internal frames
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(44, 62, 80));
        contentPane.add(desktopPane, BorderLayout.CENTER);
        
        // Add welcome message
        JLabel welcomeLabel = new JLabel("Welcome to the HRS Court Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(50, 50, 600, 30);
        desktopPane.add(welcomeLabel);
        
        JLabel instructionLabel = new JLabel("Please use the menu above to navigate the system.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setBounds(50, 90, 600, 30);
        desktopPane.add(instructionLabel);
        
        // Add quick access panel
        JPanel quickAccessPanel = new JPanel();
        quickAccessPanel.setBackground(new Color(52, 73, 94));
        quickAccessPanel.setBounds(50, 140, 888, 200);
        quickAccessPanel.setLayout(new GridLayout(3, 3, 10, 10));
        quickAccessPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(Color.WHITE),
                "Quick Access",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE));
        
        // Add quick access buttons
        quickAccessPanel.add(createQuickAccessButton("Employees", e -> openEmployeeManagement()));
        quickAccessPanel.add(createQuickAccessButton("Lawyers", e -> openLawyerManagement()));
        quickAccessPanel.add(createQuickAccessButton("Judges", e -> openJudgeManagement()));
        quickAccessPanel.add(createQuickAccessButton("Departments", e -> openDepartmentManagement()));
        quickAccessPanel.add(createQuickAccessButton("Courtrooms", e -> openCourtroomManagement()));
        quickAccessPanel.add(createQuickAccessButton("Cases", e -> openCaseManagement()));
        quickAccessPanel.add(createQuickAccessButton("Accused", e -> openAccusedManagement()));
        quickAccessPanel.add(createQuickAccessButton("Witnesses", e -> openWitnessManagement()));
        quickAccessPanel.add(createQuickAccessButton("Queries", e -> openQueryWindow()));
        
        desktopPane.add(quickAccessPanel);
        
        // Add system stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(new Color(52, 73, 94));
        statsPanel.setBounds(50, 360, 888, 100);
        statsPanel.setLayout(new GridLayout(2, 4, 10, 10));
        statsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(Color.WHITE),
                "System Statistics",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE));
        
        // Add statistics
        addStatItem(statsPanel, "Total Employees", String.valueOf(court.getAllEmployees().size()));
        addStatItem(statsPanel, "Total Lawyers", String.valueOf(court.getAllLawyers().size()));
        addStatItem(statsPanel, "Total Cases", String.valueOf(court.getAllCases().size()));
        addStatItem(statsPanel, "Total Departments", String.valueOf(court.getAllDepartments().size()));
        
        addStatItem(statsPanel, "Total Courtrooms", String.valueOf(court.getAllCourtrooms().size()));
        addStatItem(statsPanel, "Total Meetings", String.valueOf(court.getAllMeetings().size()));
        addStatItem(statsPanel, "Total Verdicts", String.valueOf(court.getAllVerdicts().size()));
        addStatItem(statsPanel, "Total Accused", String.valueOf(court.getAllAccuseds().size()));
        
        desktopPane.add(statsPanel);
    }
    
    /**
     * Creates a quick access button
     */
    private JButton createQuickAccessButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(new Color(49, 67, 130));
        button.setFocusPainted(false);
        button.addActionListener(listener);
        
        // Add hover effect
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
     * Adds a statistic item to the stats panel
     */
    private void addStatItem(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label + ":");
        lblLabel.setForeground(Color.WHITE);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblLabel);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblValue);
    }
    
    /**
     * Creates the menu bar with options for the admin
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setForeground(new Color(30, 62, 116));
        setJMenuBar(menuBar);
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(new Color(30, 62, 116));
        menuBar.add(fileMenu);
        
        JMenuItem saveItem = new JMenuItem("Save Data");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.save();
                JOptionPane.showMessageDialog(AdminView.this, "Data saved successfully", "Save", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        fileMenu.add(saveItem);
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        fileMenu.add(logoutItem);
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });
        fileMenu.add(exitItem);
        
        // Management menu
        JMenu managementMenu = new JMenu("Management");
        menuBar.add(managementMenu);
        
        JMenuItem employeeItem = new JMenuItem("Employees");
        employeeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEmployeeManagement();
            }
        });
        managementMenu.add(employeeItem);
        
        JMenuItem lawyerItem = new JMenuItem("Lawyers");
        lawyerItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLawyerManagement();
            }
        });
        managementMenu.add(lawyerItem);
        
        JMenuItem judgeItem = new JMenuItem("Judges");
        judgeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openJudgeManagement();
            }
        });
        managementMenu.add(judgeItem);
        
        JMenuItem departmentItem = new JMenuItem("Departments");
        departmentItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDepartmentManagement();
            }
        });
        managementMenu.add(departmentItem);
        
        JMenuItem courtroomItem = new JMenuItem("Courtrooms");
        courtroomItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCourtroomManagement();
            }
        });
        managementMenu.add(courtroomItem);
        
        // Data menu
        JMenu dataMenu = new JMenu("Data");
        menuBar.add(dataMenu);
        
        JMenuItem caseItem = new JMenuItem("Cases");
        caseItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCaseManagement();
            }
        });
        dataMenu.add(caseItem);
        
        JMenuItem accusedItem = new JMenuItem("Accused");
        accusedItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAccusedManagement();
            }
        });
        dataMenu.add(accusedItem);
        
        JMenuItem witnessItem = new JMenuItem("Witnesses");
        witnessItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openWitnessManagement();
            }
        });
        dataMenu.add(witnessItem);
        
        // Queries menu
        JMenu queryMenu = new JMenu("Queries");
        menuBar.add(queryMenu);
        
        JMenuItem queryItem = new JMenuItem("Run Queries");
        queryItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openQueryWindow();
            }
        });
        queryMenu.add(queryItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutItem);
    }
    
    /**
     * Opens the employee management panel
     */
    private void openEmployeeManagement() {
        if (employeeFrame == null || employeeFrame.isClosed()) {
            employeeFrame = new EmployeeManagementPanel();
            employeeFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    employeeFrame = null;
                }
            });
            desktopPane.add(employeeFrame);
            employeeFrame.setVisible(true);
        } else {
            employeeFrame.toFront();
            try {
                employeeFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the lawyer management panel
     */
    private void openLawyerManagement() {
        if (lawyerFrame == null || lawyerFrame.isClosed()) {
            lawyerFrame = new LawyerManagementPanel();
            // TODO: Create and implement LawyerManagementPanel
            lawyerFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    lawyerFrame = null;
                }
            });
            desktopPane.add(lawyerFrame);
            lawyerFrame.setVisible(true);
        } else {
            lawyerFrame.toFront();
            try {
                lawyerFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the judge management panel
     */
    private void openJudgeManagement() {
        if (judgeFrame == null || judgeFrame.isClosed()) {
            judgeFrame = new JudgeManagementPanel();
            judgeFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    judgeFrame = null;
                }
            });
            desktopPane.add(judgeFrame);
            judgeFrame.setVisible(true);
        } else {
            judgeFrame.toFront();
            try {
                judgeFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the department management panel
     */
    private void openDepartmentManagement() {
        if (departmentFrame == null || departmentFrame.isClosed()) {
            departmentFrame = new DepartmentManagementPanel();
            departmentFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    departmentFrame = null;
                }
            });
            desktopPane.add(departmentFrame);
            departmentFrame.setVisible(true);
        } else {
            departmentFrame.toFront();
            try {
                departmentFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the courtroom management panel
     */
    private void openCourtroomManagement() {
        if (courtroomFrame == null || courtroomFrame.isClosed()) {
            courtroomFrame = new CourtroomManagementPanel();
            courtroomFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    courtroomFrame = null;
                }
            });
            desktopPane.add(courtroomFrame);
            courtroomFrame.setVisible(true);
        } else {
            courtroomFrame.toFront();
            try {
                courtroomFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the case management panel
     */
    private void openCaseManagement() {
        if (caseFrame == null || caseFrame.isClosed()) {
            caseFrame = new CaseManagementPanel();
            // TODO: Create and implement CaseManagementPanel
            caseFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    caseFrame = null;
                }
            });
            desktopPane.add(caseFrame);
            caseFrame.setVisible(true);
        } else {
            caseFrame.toFront();
            try {
                caseFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the accused management panel
     */
    private void openAccusedManagement() {
        if (accusedFrame == null || accusedFrame.isClosed()) {
            accusedFrame = new AccusedAdmin();
            // TODO: Create and implement AccusedManagementPanel
            accusedFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    accusedFrame = null;
                }
            });
            desktopPane.add(accusedFrame);
            accusedFrame.setVisible(true);
        } else {
            accusedFrame.toFront();
            try {
                accusedFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the witness management panel
     */
    private void openWitnessManagement() {
        if (witnessFrame == null || witnessFrame.isClosed()) {
            witnessFrame = new WitnessManagementPanel();
            // TODO: Create and implement WitnessManagementPanel
            witnessFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    witnessFrame = null;
                }
            });
            desktopPane.add(witnessFrame);
            witnessFrame.setVisible(true);
        } else {
            witnessFrame.toFront();
            try {
                witnessFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the query window
     */
    private void openQueryWindow() {
        if (queryFrame == null || queryFrame.isClosed()) {
            queryFrame = new QueryManagementPanel();
            queryFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    queryFrame = null;
                }
            });
            desktopPane.add(queryFrame);
            queryFrame.setVisible(true);
        } else {
            queryFrame.toFront();
            try {
                queryFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Logs out the current user and returns to login screen
     */
    private void logout() {
        // Ask for confirmation
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // Save data before logout
            Main.save();
            
            // Close this window and open login screen
            dispose();
            new LOGIN().setVisible(true);
        }
    }
    
    /**
     * Exits the application
     */
    private void exitApplication() {
        // Ask for confirmation
        int result = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to exit? Any unsaved data will be lost.", 
                "Exit", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // Ask if user wants to save before exit
            result = JOptionPane.showConfirmDialog(this, 
                    "Do you want to save data before exiting?", 
                    "Save Data", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                Main.save();
            }
            
            System.exit(0);
        }
    }
    
    /**
     * Shows the about dialog
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, 
                "HRS Court Management System\nVersion 1.0\n\n" +
                "Developed for Object-Oriented Programming Course\n" +
                "University of Haifa\n\n" +
                "© 2024 All Rights Reserved", 
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}