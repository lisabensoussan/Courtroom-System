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

public class JudgeView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JDesktopPane desktopPane;
    private Court court;
    private Judge judge;
    private JMenuBar menuBar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final int PROFILE_PHOTO_SIZE = 150;

    

    // Internal frames
    private JInternalFrame casesFrame = null;
    private JInternalFrame issueVerdictFrame = null;
    private JInternalFrame calendarFrame = null;
    private JInternalFrame appealsFrame = null;

    /**
     * Create the frame for Judge view.
     */
    public JudgeView(Judge judge) {
        this.court = Main.court;
        this.judge = judge;
        
        setTitle("HRS Court Management System - Judge Workspace");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800);
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
        
        // Welcome and info section
        createWelcomeSection();
        
        // Quick actions section
        createQuickActionsSection();
        
        // Case statistics section
        createCaseStatisticsSection();
    }
    
    /**
     * Creates the welcome section with judge's basic information
     */
    private void createWelcomeSection() {
        // Welcome message
    	JLabel welcomeLabel = new JLabel("Welcome, Judge " + judge.getFirstName() + " " + judge.getLastName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(PROFILE_PHOTO_SIZE + 80, 50, 600, 30);
        desktopPane.add(welcomeLabel);
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setOpaque(false);
        photoPanel.setBounds(50, 50, PROFILE_PHOTO_SIZE, PROFILE_PHOTO_SIZE);
        // Add profile photo
        JLabel photoLabel = new JLabel();
        photoLabel.setBorder(new LineBorder(new Color(255, 255, 255), 2));
        
        // Judge information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(52, 73, 94));
        infoPanel.setBounds(230, 90, 500, 181);
        infoPanel.setLayout(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "Your Profile", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
     // Check if judge has photo and display it
        if (judge.hasPhoto()) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(judge.getPhoto()));
                ImageIcon icon = new ImageIcon(img);
                photoLabel.setIcon(icon);
            } catch (Exception e) {
                // If error loading photo, show default icon with initials
                String initial = judge.getFirstName().substring(0, 1) + judge.getLastName().substring(0, 1);
                BufferedImage img = createDefaultProfileImage(initial, PROFILE_PHOTO_SIZE);
                photoLabel.setIcon(new ImageIcon(img));
            }
        } else {
            // Create default icon with initial
            String initial = judge.getFirstName().substring(0, 1) + judge.getLastName().substring(0, 1);
            BufferedImage img = createDefaultProfileImage(initial, PROFILE_PHOTO_SIZE);
            photoLabel.setIcon(new ImageIcon(img));
        }
        
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        desktopPane.add(photoPanel);
        
        // Add judge info rows
        addInfoRow(infoPanel, "Department:", 
            judge.getDepartment() != null ? judge.getDepartment().getName() : "Unassigned");
        addInfoRow(infoPanel, "Specialization:", judge.getSpecialization().toString());
        addInfoRow(infoPanel, "Experience:", judge.getExperienceYear() + " years");
        addInfoRow(infoPanel, "License Number:", String.valueOf(judge.getLicenseNumber()));
        addInfoRow(infoPanel, "Current Salary:", String.format("$%.2f", judge.getSalary()));
        
        desktopPane.add(infoPanel);
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
     * Creates the quick actions section
     */
    private void createQuickActionsSection() {
        JPanel actionsPanel = new JPanel();
        actionsPanel.setBackground(new Color(52, 73, 94));
        actionsPanel.setBounds(240, 304, 490, 150);
        actionsPanel.setLayout(new GridLayout(2, 2, 10, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "Quick Actions", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
        // Quick action buttons
        JButton activeCasesButton = createStyledButton("Cases");
        activeCasesButton.addActionListener(e -> openCasesManagement());
        actionsPanel.add(activeCasesButton);
        
        JButton issueVerdictButton = createStyledButton("Issue Verdict");
        issueVerdictButton.addActionListener(e -> openIssueVerdict());
        actionsPanel.add(issueVerdictButton);
        
        JButton casesHistoryButton = createStyledButton("Calendar");
        casesHistoryButton.addActionListener(e -> openCalendar());
        actionsPanel.add(casesHistoryButton);
        
        JButton appealsButton = createStyledButton("Appeals");
        appealsButton.addActionListener(e -> openAppeals());
        actionsPanel.add(appealsButton);
        
        desktopPane.add(actionsPanel);
    }
    
    /**
     * Creates the case statistics section
     */
    private void createCaseStatisticsSection() {
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(new Color(52, 73, 94));
        statsPanel.setBounds(50, 466, 950, 150);
        statsPanel.setLayout(new GridLayout(4, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                "Case Statistics", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                Color.WHITE));
        
        // Calculate case statistics
        int totalCases = judge.getCasesPresided().size();
        int activeCases = 0;
        int completedCases = 0;
        int pendingVerdicts = 0;
        
        for (Case c : judge.getCasesPresided()) {
            if (c.getCaseStatus() == Status.inProcess) {
                activeCases++;
                if (c.getVerdict() == null) {
                    pendingVerdicts++;
                }
            } else if (c.getCaseStatus() == Status.finished) {
                completedCases++;
            }
        }
        
        // Add statistics rows
        addInfoRow(statsPanel, "Total Cases Presided:", String.valueOf(totalCases));
        addInfoRow(statsPanel, "Active Cases:", String.valueOf(activeCases));
        addInfoRow(statsPanel, "Completed Cases:", String.valueOf(completedCases));
        addInfoRow(statsPanel, "Pending Verdicts:", String.valueOf(pendingVerdicts));
        
        // Additional statistics
        long avgCaseLength = calculateAverageCaseLength();
        
        addInfoRow(statsPanel, "Avg Case Duration (days):", String.valueOf(avgCaseLength));
        addInfoRow(statsPanel, "Specialization:", judge.getSpecialization().toString());
        addInfoRow(statsPanel, "Department:", 
            judge.getDepartment() != null ? judge.getDepartment().getName() : "Unassigned");
        addInfoRow(statsPanel, "Total Verdicts Issued:", 
            String.valueOf(judge.getCasesPresided().stream()
                .filter(c -> c.getVerdict() != null)
                .count()));
        
        desktopPane.add(statsPanel);
    }
    
    /**
     * Calculate average case length in days
     */
    private long calculateAverageCaseLength() {
        long totalDays = 0;
        int completedCases = 0;
        
        for (Case c : judge.getCasesPresided()) {
            if (c.getCaseStatus() == Status.finished && c.getVerdict() != null) {
                long caseLength = (c.getVerdict().getIssusedDate().getTime() - c.getOpenedDate().getTime()) 
                                   / (1000 * 60 * 60 * 24);
                totalDays += caseLength;
                completedCases++;
            }
        }
        
        return completedCases > 0 ? totalDays / completedCases : 0;
    }
    
    /**
     * Creates the menu bar
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setForeground(new Color(29, 74, 158));
        setJMenuBar(menuBar);
     // File Menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        JMenuItem saveItem = new JMenuItem("Save Data");
        saveItem.addActionListener(e -> {
            Main.save();
            JOptionPane.showMessageDialog(this, "Data saved successfully", "Save", JOptionPane.INFORMATION_MESSAGE);
        });
        fileMenu.add(saveItem);
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        fileMenu.add(logoutItem);
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);
        
        // Case Menu
        JMenu caseMenu = new JMenu("Cases");
        menuBar.add(caseMenu);
        
        JMenuItem activeCasesItem = new JMenuItem("allCases");
        activeCasesItem.addActionListener(e -> openCasesManagement());
        caseMenu.add(activeCasesItem);
        
        JMenuItem calendarItem = new JMenuItem("Calendar");
        calendarItem.addActionListener(e -> openCalendar());
        caseMenu.add(calendarItem);
        
        // Verdict Menu
        JMenu verdictMenu = new JMenu("Verdicts");
        menuBar.add(verdictMenu);
        
        JMenuItem issueVerdictItem = new JMenuItem("Issue Verdict");
        issueVerdictItem.addActionListener(e -> openIssueVerdict());
        verdictMenu.add(issueVerdictItem);
        
        // Appeals Menu
        JMenu appealsMenu = new JMenu("Appeals");
        menuBar.add(appealsMenu);
        
        JMenuItem manageAppealsItem = new JMenuItem("Manage Appeals");
        manageAppealsItem.addActionListener(e -> openAppeals());
        appealsMenu.add(manageAppealsItem);
        
        
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
    }
    
    /**
     * Opens active cases panel
     */
    private void openCasesManagement() {
        if (casesFrame == null || casesFrame.isClosed()) {
            casesFrame = new CaseJudge(judge);
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
     * Opens issue verdict panel
     */
    private void openIssueVerdict() {
    	 if (issueVerdictFrame == null || issueVerdictFrame.isClosed()) {
    		 issueVerdictFrame = new VerdictJudge(judge);
    		 issueVerdictFrame.addInternalFrameListener(new InternalFrameAdapter() {
                 @Override
                 public void internalFrameClosed(InternalFrameEvent e) {
                	 issueVerdictFrame = null;
                 }
             });
             desktopPane.add(issueVerdictFrame);
             issueVerdictFrame.setVisible(true);
         } else {
        	 issueVerdictFrame.toFront();
             try {
            	 issueVerdictFrame.setSelected(true);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
    }
    
    /**
     * Opens cases history panel
     */
    private void openCalendar() {
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
     * Opens appeals management panel
     */
    private void openAppeals() {
    	 if (appealsFrame == null || appealsFrame.isClosed()) {
    		 appealsFrame = new AppealJudge(judge);
    		 appealsFrame.addInternalFrameListener(new InternalFrameAdapter() {
                 @Override
                 public void internalFrameClosed(InternalFrameEvent e) {
                	 appealsFrame = null;
                 }
             });
             desktopPane.add(appealsFrame);
             appealsFrame.setVisible(true);
         } else {
        	 appealsFrame.toFront();
             try {
            	 appealsFrame.setSelected(true);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
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
     * Logs out the current user and returns to login screen
     */
    private void logout() {
        // Ask for confirmation
        int result = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", 
                JOptionPane.YES_NO_OPTION);
        
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
                "Exit", 
                JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // Ask if user wants to save before exit
            result = JOptionPane.showConfirmDialog(this, 
                    "Do you want to save data before exiting?", 
                    "Save Data", 
                    JOptionPane.YES_NO_OPTION);
            
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
                "Judge Workspace Module\n" +
                "© 2024 All Rights Reserved", 
                "About", 
                JOptionPane.INFORMATION_MESSAGE);
    }
}            