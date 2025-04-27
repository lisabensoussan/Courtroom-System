package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;

import control.Court;
import enums.Status;
import model.*;

public class EmployeeView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JDesktopPane desktopPane;
    private Court court;
    private Employee employee;
    private JMenuBar menuBar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Track internal frames
    private JInternalFrame courtroomsFrame = null;
    private JInternalFrame departmentsFrame = null;
    private JInternalFrame casesFrame = null;
    private JInternalFrame accusedFrame = null;
    private JInternalFrame assignLawyersFrame = null;
    private JInternalFrame assignJudgesFrame = null;
    private JInternalFrame bookCourtroomFrame = null;
    private final int PROFILE_PHOTO_SIZE = 150;


    /**
     * Create the frame for Court Employee view.
     */
    public EmployeeView(Employee employee) {
        this.court = Main.court;
        this.employee = employee;
        
        setTitle("HRS Court Management System - Court Employee Panel");
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
        
        // Add welcome message with employee name
        JLabel welcomeLabel = new JLabel("Welcome, " + employee.getFirstName() + " " + employee.getLastName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(PROFILE_PHOTO_SIZE + 80, 30, 600, 30);
        desktopPane.add(welcomeLabel);

        
        // Display basic employee information
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(52, 73, 94));
        infoPanel.setBounds(PROFILE_PHOTO_SIZE + 80, 70, 438, 150);
        infoPanel.setLayout(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "Your Information", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
        // Add employee info to panel
        addInfoRow(infoPanel, "Name:", employee.getFirstName() + " " + employee.getLastName());
        addInfoRow(infoPanel, "ID:", String.valueOf(employee.getId()));
        addInfoRow(infoPanel, "Position:", employee.getPosition().toString());
        addInfoRow(infoPanel, "Start Date:", dateFormat.format(employee.getWorkStartDate()));
        
        // Display department(s)
        StringBuilder deptNames = new StringBuilder();
        for (Department dept : employee.getDepartments()) {
            if (deptNames.length() > 0) {
                deptNames.append(", ");
            }
            deptNames.append(dept.getName());
        }
        addInfoRow(infoPanel, "Department(s):", deptNames.length() > 0 ? deptNames.toString() : "None");
        
        desktopPane.add(infoPanel);
        
        // Add quick access buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(52, 73, 94));
        buttonsPanel.setBounds(60, 244, 643, 200); // Increased height
        buttonsPanel.setLayout(new GridLayout(4, 2, 10, 10)); // Changed from 3x2 to 4x2 grid
        buttonsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "Quick Actions", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
        
     // Create a panel for the profile section
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(null);
        profilePanel.setOpaque(false);
        profilePanel.setBounds(50, 50, 900, 200);
        desktopPane.add(profilePanel);

        // Add profile photo
        JLabel photoLabel = new JLabel();
        photoLabel.setBounds(50, 20, PROFILE_PHOTO_SIZE, PROFILE_PHOTO_SIZE);
        photoLabel.setBorder(new LineBorder(new Color(255, 255, 255), 2));
        
     // Check if employee has photo and display it
        if (employee.hasPhoto()) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(employee.getPhoto()));
                ImageIcon icon = new ImageIcon(img);
                photoLabel.setIcon(icon);
            } catch (Exception e) {
                // If error loading photo, show default icon with initials
                String initial = employee.getFirstName().substring(0, 1) + employee.getLastName().substring(0, 1);
                BufferedImage img = createDefaultProfileImage(initial, PROFILE_PHOTO_SIZE);
                photoLabel.setIcon(new ImageIcon(img));
            }
        } else {
            // Create default icon with initial
            String initial = employee.getFirstName().substring(0, 1) + employee.getLastName().substring(0, 1);
            BufferedImage img = createDefaultProfileImage(initial, PROFILE_PHOTO_SIZE);
            photoLabel.setIcon(new ImageIcon(img));
        }
        
        profilePanel.add(photoLabel);

        
        // Add quick action buttons
        JButton courtroomsButton = createStyledButton("Manage Courtrooms");
        courtroomsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCourtroomsManagement();
            }
        });
        buttonsPanel.add(courtroomsButton);
        
     // Add new Courtroom Booking button
        JButton bookCourtroomButton = createStyledButton("Courtroom Booking");
        bookCourtroomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openBookCourtroom();
            }
        });
        buttonsPanel.add(bookCourtroomButton);
        
        JButton departmentsButton = createStyledButton("Manage Departments");
        departmentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDepartmentsManagement();
            }
        });
        buttonsPanel.add(departmentsButton);
        
        JButton casesButton = createStyledButton("Manage Cases");
        casesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCasesManagement();
            }
        });
        buttonsPanel.add(casesButton);
        
        JButton accusedButton = createStyledButton("Manage Accused");
        accusedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAccusedManagement();
            }
        });
        buttonsPanel.add(accusedButton);
        
        JButton assignLawyersButton = createStyledButton("Assign Departments");
        assignLawyersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAssignDepartment();
            }
        });
        buttonsPanel.add(assignLawyersButton);
        
        JButton assignJudgesButton = createStyledButton("Assign Judges");
        assignJudgesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAssignJudges();
            }
        });
        buttonsPanel.add(assignJudgesButton);
        
        desktopPane.add(buttonsPanel);
        
        // Add system stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(new Color(52, 73, 94));
        statsPanel.setBounds(50, 456, 850, 150);
        statsPanel.setLayout(new GridLayout(4, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "System Statistics", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
        // Calculate some basic statistics
        int totalCases = court.getAllCases().size();
        int activeCases = 0;
        int closedCases = 0;
        
        for (Case c : court.getAllCases().values()) {
            if (c.getCaseStatus() == Status.inProcess) {
                activeCases++;
            } else if (c.getCaseStatus() == Status.finished) {
                closedCases++;
            }
        }
        
        // Add stats to panel
        addInfoRow(statsPanel, "Total Cases:", String.valueOf(totalCases));
        addInfoRow(statsPanel, "Active Cases:", String.valueOf(activeCases));
        addInfoRow(statsPanel, "Closed Cases:", String.valueOf(closedCases));
        addInfoRow(statsPanel, "Total Departments:", String.valueOf(court.getAllDepartments().size()));
        
        addInfoRow(statsPanel, "Total Courtrooms:", String.valueOf(court.getAllCourtrooms().size()));
        addInfoRow(statsPanel, "Total Lawyers:", String.valueOf(court.getAllLawyers().size()));
        addInfoRow(statsPanel, "Total Employees:", String.valueOf(court.getAllEmployees().size()));
        addInfoRow(statsPanel, "Total Accused:", String.valueOf(court.getAllAccuseds().size()));
        
        desktopPane.add(statsPanel);
        
        
    }
    
    /**
     * Helper method to add a labeled row to info panels
     */
    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setForeground(Color.WHITE);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblLabel);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblValue);
    }
    
    /**
     * Creates a styled button for the quick access panel
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
    
    private BufferedImage createDefaultProfileImage(String initials, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        
        // Fill background with a color based on the initials (for personalization)
        int hash = initials.hashCode();
        Color backgroundColor = new Color(
                Math.abs(hash) % 200 + 55,   // Red component (55-255)
                Math.abs(hash / 2) % 200 + 55, // Green component (55-255)
                Math.abs(hash / 3) % 200 + 55  // Blue component (55-255)
        );
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, size, size);
        
        // Draw the initials
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, size / 3));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(initials);
        int textHeight = fm.getHeight();
        g2d.drawString(initials, (size - textWidth) / 2, (size - textHeight) / 2 + fm.getAscent());
        
        g2d.dispose();
        return img;
    }
    /**
     * Creates the menu bar with options for the court employee
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setForeground(new Color(20, 52, 165));
        setJMenuBar(menuBar);
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        JMenuItem saveItem = new JMenuItem("Save Data");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.save();
                JOptionPane.showMessageDialog(EmployeeView.this, "Data saved successfully", "Save", JOptionPane.INFORMATION_MESSAGE);
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
        
        JMenuItem courtroomsItem = new JMenuItem("Courtrooms");
        courtroomsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCourtroomsManagement();
            }
        });
        managementMenu.add(courtroomsItem);
        
        JMenuItem courtroomsBookItem = new JMenuItem("Courtrooms Booking");
        courtroomsBookItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openBookCourtroom();
            }
        });
        managementMenu.add(courtroomsBookItem);
        
        JMenuItem departmentsItem = new JMenuItem("Departments");
        departmentsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDepartmentsManagement();
            }
        });
        managementMenu.add(departmentsItem);
        
        // Data menu
        JMenu dataMenu = new JMenu("Data");
        menuBar.add(dataMenu);
        
        JMenuItem casesItem = new JMenuItem("Cases");
        casesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCasesManagement();
            }
        });
        dataMenu.add(casesItem);
        
        JMenuItem accusedItem = new JMenuItem("Accused");
        accusedItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAccusedManagement();
            }
        });
        dataMenu.add(accusedItem);
        
        // Assignment menu
        JMenu assignmentMenu = new JMenu("Assignment");
        menuBar.add(assignmentMenu);
        
        JMenuItem assignLawyersItem = new JMenuItem("Assign Department");
        assignLawyersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAssignDepartment();
            }
        });
        assignmentMenu.add(assignLawyersItem);
        
        JMenuItem assignJudgesItem = new JMenuItem("Assign Judges");
        assignJudgesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAssignJudges();
            }
        });
        assignmentMenu.add(assignJudgesItem);
        
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
     * Opens the courtrooms management panel
     */
    private void openCourtroomsManagement() {
        if (courtroomsFrame == null || courtroomsFrame.isClosed()) {
            courtroomsFrame = new CourtroomManagementEmployee();
            courtroomsFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    courtroomsFrame = null;
                }
            });
            desktopPane.add(courtroomsFrame);
            courtroomsFrame.setVisible(true);
        } else {
            courtroomsFrame.toFront();
            try {
                courtroomsFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the courtroom booking panel
     */
    private void openBookCourtroom() {
        if (bookCourtroomFrame == null || bookCourtroomFrame.isClosed()) {
            bookCourtroomFrame = new BookCourtroom();
            bookCourtroomFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    bookCourtroomFrame = null;
                }
            });
            desktopPane.add(bookCourtroomFrame);
            bookCourtroomFrame.setVisible(true);
        } else {
            bookCourtroomFrame.toFront();
            try {
                bookCourtroomFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Opens the departments management panel
     */
    private void openDepartmentsManagement() {
        if (departmentsFrame == null || departmentsFrame.isClosed()) {
            departmentsFrame = new DepartmentManagementEmployee();
            departmentsFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    departmentsFrame = null;
                }
            });
            desktopPane.add(departmentsFrame);
            departmentsFrame.setVisible(true);
        } else {
            departmentsFrame.toFront();
            try {
                departmentsFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the cases management panel
     */
    private void openCasesManagement() {
        if (casesFrame == null || casesFrame.isClosed()) {
            casesFrame = new CaseManagementPanel();
            casesFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    casesFrame = null;
                }
            });
            desktopPane.add(casesFrame);
            casesFrame.setVisible(true);
        } else {
            casesFrame.toFront();
            try {
                casesFrame.setSelected(true);
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
            accusedFrame = new AccusedManagementPanel();
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
     * Opens the assign lawyers panel
     */
    private void openAssignDepartment() {
        if (assignLawyersFrame == null || assignLawyersFrame.isClosed()) {
            assignLawyersFrame = new AssignDepartment();
            assignLawyersFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    assignLawyersFrame = null;
                }
            });
            desktopPane.add(assignLawyersFrame);
            assignLawyersFrame.setVisible(true);
        } else {
            assignLawyersFrame.toFront();
            try {
                assignLawyersFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the assign judges panel
     */
    private void openAssignJudges() {
        if (assignJudgesFrame == null || assignJudgesFrame.isClosed()) {
            assignJudgesFrame = new AssignJudgesPanel();
            assignJudgesFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    assignJudgesFrame = null;
                }
            });
            desktopPane.add(assignJudgesFrame);
            assignJudgesFrame.setVisible(true);
        } else {
            assignJudgesFrame.toFront();
            try {
                assignJudgesFrame.setSelected(true);
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
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit? Any unsaved data will be lost.", "Exit", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // Ask if user wants to save before exit
            result = JOptionPane.showConfirmDialog(this, "Do you want to save data before exiting?", "Save Data", JOptionPane.YES_NO_OPTION);
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
                "HRS Court Management System\nVersion 1.0\n\nDeveloped for Object-Oriented Programming Course\nUniversity of Haifa", 
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}