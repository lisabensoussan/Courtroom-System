package view;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import control.Court;
import exceptions.AuthenticationException;
import model.*;

/**
 * Login screen for the Court Management System with animated background
 */
public class LOGIN extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private Court court;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private JLabel backgroundLabel; // For holding the background image or GIF

    /**
     * Create the login frame
     */
    public LOGIN() {
        court = Main.court;
        
        setTitle("HRS Court Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set window size to match GIF dimensions (800x450px)
        setBounds(100, 100, 800, 450);
        setLocationRelativeTo(null); // Center on screen
        
        // Set up the content pane with layered structure
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Create the layered pane to handle background and UI elements
        JLayeredPane layeredPane = new JLayeredPane();
        contentPane.add(layeredPane, BorderLayout.CENTER);
        layeredPane.setLayout(null); // Use null layout for absolute positioning
        
        // Get the content pane dimensions
        int width = 800;
        int height = 450;
        
        // Add animated background (GIF or image)
        backgroundLabel = new JLabel();
        ImageIcon backgroundImage = new ImageIcon("/Users/lisabensoussan/Desktop/Courtroom.gif");
        // If the image doesn't load, try with absolute path or resource
        if (backgroundImage.getIconWidth() <= 0) {
            System.out.println("Warning: Could not load GIF, trying fallback...");
            // Try a fallback image or set a background color
            backgroundLabel.setBackground(new Color(44, 62, 80));
            backgroundLabel.setOpaque(true);
        }
        backgroundLabel.setIcon(backgroundImage);
        backgroundLabel.setBounds(0, 0, width, height);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        
        // Create a semi-transparent panel for all components
        JPanel mainPanel = new JPanel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(new Color(236, 240, 241));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        
        mainPanel.setOpaque(false);
        mainPanel.setBounds(0, 0, width, height);
        mainPanel.setLayout(new BorderLayout());
        layeredPane.add(mainPanel, JLayeredPane.PALETTE_LAYER);
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JLabel titleLabel = new JLabel("HRS Court Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Create login panel - centered in the available space
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setLayout(null);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        // Calculate center positions
        int centerX = width / 2;
        
        // Create and position form elements (centered)
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(centerX - 160, 80, 80, 20);
        loginPanel.add(lblUsername);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(centerX - 160, 120, 80, 20);
        loginPanel.add(lblPassword);
        
        usernameField = new JTextField();
        usernameField.setBounds(centerX - 70, 80, 180, 25);
        loginPanel.add(usernameField);
        usernameField.setColumns(10);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(centerX - 70, 120, 180, 25);
        loginPanel.add(passwordField);
        
        loginButton = new JButton("Login");
        loginButton.setBounds(centerX - 45, 165, 90, 25);
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(new Color(35, 63, 134));
        loginPanel.add(loginButton);
        
        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setBounds(centerX - 150, 200, 300, 20);
        loginPanel.add(statusLabel);
        
        // Add password help information
        JLabel passwordHelpLabel = new JLabel("Password format: lastName.birthDate (e.g., Smith.01/01/1980)");
        passwordHelpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordHelpLabel.setForeground(new Color(30, 45, 75));
        passwordHelpLabel.setFont(new Font("Arial", Font.BOLD, 11));
        passwordHelpLabel.setBounds(centerX - 200, 225, 400, 20);
        loginPanel.add(passwordHelpLabel);
        
        // Add features panel for buttons - centered
        JPanel featuresPanel = new JPanel();
        featuresPanel.setBounds(centerX - 200, 255, 400, 40);
        featuresPanel.setLayout(new GridLayout(1, 3, 20, 5));
        featuresPanel.setOpaque(false);
        loginPanel.add(featuresPanel);
        
        JButton publicVerdictsButton = new JButton("Public Verdicts");
        publicVerdictsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPublicVerdicts();
            }
        });
        publicVerdictsButton.setBackground(new Color(44, 62, 80));
        publicVerdictsButton.setForeground(new Color(35, 63, 134));
        featuresPanel.add(publicVerdictsButton);
        
        JButton witnessButton = new JButton("Submit Testimony");
        witnessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openWitnessTestimony();
            }
        });
        witnessButton.setBackground(new Color(44, 62, 80));
        witnessButton.setForeground(new Color(35, 63, 134));
        featuresPanel.add(witnessButton);
        
        JButton visitorButton = new JButton("Visitor Check-in");
        visitorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openVisitorReporting();
            }
        });
        visitorButton.setBackground(new Color(44, 62, 80));
        visitorButton.setForeground(new Color(35, 63, 134));
        featuresPanel.add(visitorButton);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(44, 62, 80));
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        
        JLabel lblCopyright = new JLabel("© 2024 HRS Court Management System");
        lblCopyright.setForeground(Color.WHITE);
        footerPanel.add(lblCopyright);
        
        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });
        
        // Add key listener to password field
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authenticateUser();
                }
            }
        });
        
        // Add hover effects to buttons
        addHoverEffect(loginButton);
        addHoverEffect(publicVerdictsButton);
        addHoverEffect(witnessButton);
        addHoverEffect(visitorButton);
        
        // Make components resize with the frame
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = getContentPane().getWidth();
                int newHeight = getContentPane().getHeight();
                
                // Update base components
                layeredPane.setBounds(0, 0, newWidth, newHeight);
                backgroundLabel.setBounds(0, 0, newWidth, newHeight);
                mainPanel.setBounds(0, 0, newWidth, newHeight);
                
                // Recalculate center position
                int newCenterX = newWidth / 2;
                
                // Reposition form elements
                lblUsername.setBounds(newCenterX - 160, 80, 80, 20);
                usernameField.setBounds(newCenterX - 70, 80, 180, 25);
                
                lblPassword.setBounds(newCenterX - 160, 120, 80, 20);
                passwordField.setBounds(newCenterX - 70, 120, 180, 25);
                
                loginButton.setBounds(newCenterX - 45, 165, 90, 25);
                statusLabel.setBounds(newCenterX - 150, 200, 300, 20);
                passwordHelpLabel.setBounds(newCenterX - 200, 225, 400, 20);
                featuresPanel.setBounds(newCenterX - 200, 255, 400, 40);
            }
        });
    }
    
    /**
     * Adds hover effect to a button
     */
    private void addHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
    }
    
    /**
     * Authenticates a user and opens the appropriate view
     */
    private void authenticateUser() {
        try {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                throw new AuthenticationException("Username and password cannot be empty");
            }
            
            // Admin login
            if (username.equals("ADMIN") && password.equals("ADMIN")) {
                openAdminView();
                return;
            }
            
            // Judge login
            for (Object obj : court.getAllLawyers().values()) {
                if (obj instanceof Judge) {
                    Judge judge = (Judge) obj;
                    String expectedPassword = judge.getLastName() + "." + 
                                             dateFormat.format(judge.getBirthDate());
                    
                    if (username.equals(String.valueOf(judge.getId())) && 
                        password.equals(expectedPassword)) {
                        openJudgeView(judge);
                        return;
                    }
                }
            }
            
            // Lawyer login
            for (Object obj : court.getAllLawyers().values()) {
                if (obj instanceof Lawyer && !(obj instanceof Judge)) {
                    Lawyer lawyer = (Lawyer) obj;
                    String expectedPassword = lawyer.getLastName() + "." + 
                                             dateFormat.format(lawyer.getBirthDate());
                    
                    if (username.equals(String.valueOf(lawyer.getId())) && 
                        password.equals(expectedPassword)) {
                        openLawyerView(lawyer);
                        return;
                    }
                }
            }
            
            // Employee login
            for (Employee employee : court.getAllEmployees().values()) {
                String expectedPassword = employee.getLastName() + "." + 
                                         dateFormat.format(employee.getBirthDate());
                
                if (username.equals(String.valueOf(employee.getId())) && 
                    password.equals(expectedPassword)) {
                    openEmployeeView(employee);
                    return;
                }
            }
            
            // If we get here, authentication failed
            throw new AuthenticationException("Invalid username or password");
            
        } catch (AuthenticationException e) {
            statusLabel.setText(e.getMessage());
            passwordField.setText("");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Opens the admin view
     */
    private void openAdminView() {
        setVisible(false);
        dispose();
        new AdminView().setVisible(true);
    }
    
    /**
     * Opens the judge view
     */
    private void openJudgeView(Judge judge) {
        setVisible(false);
        dispose();
        new JudgeView(judge).setVisible(true);
    }
    
    /**
     * Opens the lawyer view
     */
    private void openLawyerView(Lawyer lawyer) {
        setVisible(false);
        dispose();
        new LawyerView(lawyer).setVisible(true);
    }
    
    /**
     * Opens the employee view
     */
    private void openEmployeeView(Employee employee) {
        setVisible(false);
        dispose();
        new EmployeeView(employee).setVisible(true);
    }
    
    /**
     * Opens the public verdicts view
     */
    private void openPublicVerdicts() {
        new PublicVerdictView().setVisible(true);
    }
    
    /**
     * Opens the witness testimony panel
     */
    private void openWitnessTestimony() {
        new WitnessTestimonyPanel().setVisible(true);
    }
    
    /**
     * Opens the visitor reporting panel
     */
    private void openVisitorReporting() {
        new VisitorReportingPanel().setVisible(true);
    }
}