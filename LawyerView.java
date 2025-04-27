package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import control.Court;
import enums.Specialization;
import model.*;

public class LawyerView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JDesktopPane desktopPane;
    private Court court;
    private Lawyer lawyer;
    private JMenuBar menuBar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Photo constants
    private final int PROFILE_PHOTO_SIZE = 150;
    
    // Track internal frames
    private JInternalFrame casesFrame = null;
    private JInternalFrame documentsFrame = null;
    private JInternalFrame meetingsFrame = null;
    private JInternalFrame calendarFrame = null;

    /**
     * Create the frame for Lawyer view.
     */
    public LawyerView(Lawyer lawyer) {
        this.court = Main.court;
        this.lawyer = lawyer;
        
        setTitle("HRS Court Management System - Lawyer Panel");
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
        
        // Create top panel for profile section
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(null);
        profilePanel.setOpaque(false);
        profilePanel.setBounds(50, 50, 900, 200);
        desktopPane.add(profilePanel);
        
        // Add profile photo
        JLabel photoLabel = new JLabel();
        photoLabel.setBounds(50, 20, PROFILE_PHOTO_SIZE, PROFILE_PHOTO_SIZE);
        photoLabel.setBorder(new LineBorder(new Color(255, 255, 255), 2));
        
        // Check if lawyer has photo and display it
        if (lawyer.hasPhoto()) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(lawyer.getPhoto()));
                ImageIcon icon = new ImageIcon(img);
                photoLabel.setIcon(icon);
            } catch (Exception e) {
                // If error loading photo, show default icon
                photoLabel.setIcon(new ImageIcon(getClass().getResource("/icons/default_user.png")));
                System.err.println("Error loading photo: " + e.getMessage());
            }
        } else {
            // Create default icon with initial
            String initial = lawyer.getFirstName().substring(0, 1) + lawyer.getLastName().substring(0, 1);
            BufferedImage img = createDefaultProfileImage(initial, PROFILE_PHOTO_SIZE);
            photoLabel.setIcon(new ImageIcon(img));
        }
        
        profilePanel.add(photoLabel);
        
        // Add welcome message with lawyer name
        JLabel welcomeLabel = new JLabel("Welcome, " + lawyer.getFirstName() + " " + lawyer.getLastName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(230, 20, 600, 30);
        profilePanel.add(welcomeLabel);
        
        // Display basic lawyer information
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(52, 73, 94));
        infoPanel.setBounds(230, 50, 400, 150);
        infoPanel.setLayout(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "Your Information", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
        // Add lawyer info to panel
        addInfoRow(infoPanel, "Name:", lawyer.getFirstName() + " " + lawyer.getLastName());
        addInfoRow(infoPanel, "ID:", String.valueOf(lawyer.getId()));
        addInfoRow(infoPanel, "Specialization:", lawyer.getSpecialization().toString());
        addInfoRow(infoPanel, "License Number:", String.valueOf(lawyer.getLicenseNumber()));
        addInfoRow(infoPanel, "Department:", 
            lawyer.getDepartment() != null ? lawyer.getDepartment().getName() : "Not Assigned");
        
        profilePanel.add(infoPanel);
        
        // Add quick access buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(52, 73, 94));
        buttonsPanel.setBounds(289, 313, 400, 150);
        buttonsPanel.setLayout(new GridLayout(2, 2, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "Quick Actions", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
        // Add quick action buttons
        JButton casesButton = createStyledButton("My Cases");
        casesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCasesManagement();
            }
        });
        buttonsPanel.add(casesButton);
        
        JButton documentsButton = createStyledButton("Documents");
        documentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDocumentsManagement();
            }
        });
        buttonsPanel.add(documentsButton);
        
        JButton appealsButton = createStyledButton("Future Meetings");
        appealsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMeetingManagement();
            }
        });
        buttonsPanel.add(appealsButton);
        
        JButton calendarButton = createStyledButton("Case Calendar");
        calendarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCaseCalendar();
            }
        });
        buttonsPanel.add(calendarButton);
        
        desktopPane.add(buttonsPanel);
        
        // Add system stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(new Color(52, 73, 94));
        statsPanel.setBounds(50, 490, 850, 150);
        statsPanel.setLayout(new GridLayout(4, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "Case Statistics", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
        // Calculate lawyer-specific case statistics
        int totalCases = lawyer.getCasesHandled().size();
        int activeCases = 0;
        int completedCases = 0;
        
        for (Case c : lawyer.getCasesHandled()) {
            if (c.getCaseStatus() == enums.Status.inProcess) {
                activeCases++;
            } else if (c.getCaseStatus() == enums.Status.finished) {
                completedCases++;
            }
        }
        
        // Add stats to panel
        addInfoRow(statsPanel, "Total Cases:", String.valueOf(totalCases));
        addInfoRow(statsPanel, "Active Cases:", String.valueOf(activeCases));
        addInfoRow(statsPanel, "Completed Cases:", String.valueOf(completedCases));
        addInfoRow(statsPanel, "Specialization:", lawyer.getSpecialization().toString());
        
        long averageCaseLength = calculateAverageCaseLength();
        
        addInfoRow(statsPanel, "Avg Case Time (days):", String.valueOf(averageCaseLength));
        addInfoRow(statsPanel, "Department:", 
            lawyer.getDepartment() != null ? lawyer.getDepartment().getName() : "Not Assigned");
        addInfoRow(statsPanel, "License Number:", String.valueOf(lawyer.getLicenseNumber()));
        addInfoRow(statsPanel, "Salary:", String.format("$%.2f", lawyer.getSalary()));
        
        desktopPane.add(statsPanel);
    }
    
    /**
     * Creates a default profile image with initials
     */
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
     * Calculate average case length in days
     */
    private long calculateAverageCaseLength() {
        long totalDays = 0;
        int completedCases = 0;
        
        for (Case c : lawyer.getCasesHandled()) {
            if (c.getCaseStatus() == enums.Status.finished && c.getVerdict() != null) {
                long caseLength = (c.getVerdict().getIssusedDate().getTime() - c.getOpenedDate().getTime()) 
                                   / (1000 * 60 * 60 * 24);
                totalDays += caseLength;
                completedCases++;
            }
        }
        
        return completedCases > 0 ? totalDays / completedCases : 0;
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
    
    /**
     * Creates the menu bar with options for the lawyer
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setForeground(new Color(35, 63, 134));
        setJMenuBar(menuBar);
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        JMenuItem saveItem = new JMenuItem("Save Data");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.save();
                JOptionPane.showMessageDialog(LawyerView.this, "Data saved successfully", "Save", JOptionPane.INFORMATION_MESSAGE);
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
        
        // Case menu
        JMenu caseMenu = new JMenu("Cases");
        menuBar.add(caseMenu);
        
        JMenuItem myCasesItem = new JMenuItem("My Cases");
        myCasesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCasesManagement();
            }
        });
        caseMenu.add(myCasesItem);
        
        // Documents menu
        JMenu documentMenu = new JMenu("Documents");
        documentMenu.setBackground(new Color(35, 63, 134));
        menuBar.add(documentMenu);
        
        JMenuItem manageDocumentsItem = new JMenuItem("Manage Documents");
        manageDocumentsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDocumentsManagement();
            }
        });
        documentMenu.add(manageDocumentsItem);
        
        // Appeals menu
        JMenu appealsMenu = new JMenu("Meetings");
        appealsMenu.setBackground(new Color(35, 63, 134));
        menuBar.add(appealsMenu);
        
        JMenuItem manageMeetingsItem = new JMenuItem("Future Meetings");
        manageMeetingsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMeetingManagement();
            }
        });
        appealsMenu.add(manageMeetingsItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setBackground(new Color(35, 63, 134));
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
     * Opens the cases management panel
     */
    private void openCasesManagement() {
        if (casesFrame == null || casesFrame.isClosed()) {
            casesFrame = new CaseLawyer(lawyer);
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
     * Opens the documents management panel
     */
    private void openDocumentsManagement() {
        if (documentsFrame == null || documentsFrame.isClosed()) {
            documentsFrame = new DocumentsView(lawyer);  // Create DocumentsView
            documentsFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    documentsFrame = null;
                }
            });
            desktopPane.add(documentsFrame);
            documentsFrame.setVisible(true);
        } else {
            documentsFrame.toFront();
            try {
                documentsFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Opens the appeals management panel
     */
    private void openMeetingManagement() {
    	if (meetingsFrame == null || meetingsFrame.isClosed()) {
    		meetingsFrame = new MeetingsManagement(lawyer);  // Create DocumentsView
    		meetingsFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                	meetingsFrame = null;
                }
            });
            desktopPane.add(meetingsFrame);
            meetingsFrame.setVisible(true);
        } else {
        	meetingsFrame.toFront();
            try {
            	meetingsFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
    }
    
    /**
     * Opens the case calendar
     */
    private void openCaseCalendar() {
        if (calendarFrame == null || calendarFrame.isClosed()) {
            calendarFrame = new CourtCalendarPanel();
            calendarFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    calendarFrame = null;
                }
            });
            desktopPane.add(calendarFrame);
            calendarFrame.setVisible(true);
        } else {
            calendarFrame.toFront();
            try {
                calendarFrame.setSelected(true);
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