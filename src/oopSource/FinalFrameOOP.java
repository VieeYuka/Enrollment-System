package oopSource;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class FinalFrameOOP extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FinalFrameOOP frame = new FinalFrameOOP();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public FinalFrameOOP() {
        setTitle("Rey University - Enrollment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1280, 720);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);
        contentPane.setLayout(new GridBagLayout());

        // Left Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(11, 55, 49));
        leftPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc_leftPanel = new GridBagConstraints();
        gbc_leftPanel.fill = GridBagConstraints.BOTH;
        gbc_leftPanel.gridx = 0;
        gbc_leftPanel.gridy = 0;
        gbc_leftPanel.weightx = 0.35;
        gbc_leftPanel.weighty = 1.0;
        contentPane.add(leftPanel, gbc_leftPanel);
        
        JLabel lblLogoCircle = new JLabel("", SwingConstants.CENTER);
        URL imgUrl = this.getClass().getResource("/RUlogo (1).png");
        if (imgUrl != null) {
            Image img = new ImageIcon(imgUrl).getImage();
            lblLogoCircle.setIcon(new ImageIcon(img));
        } else {
            lblLogoCircle.setText("LOGO");
        }

        lblLogoCircle.setOpaque(true);
        lblLogoCircle.setBackground(new Color(11, 55, 49));
        lblLogoCircle.setFont(new Font("Arial", Font.BOLD, 16));
        lblLogoCircle.setPreferredSize(new Dimension(150, 150));

        GridBagConstraints gbc_lblLogoCircle = new GridBagConstraints();
        gbc_lblLogoCircle.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblLogoCircle.anchor = GridBagConstraints.CENTER;
        gbc_lblLogoCircle.insets = new Insets(10, 20, 10, 20);
        gbc_lblLogoCircle.gridx = 0;
        gbc_lblLogoCircle.gridy = 0;
        leftPanel.add(lblLogoCircle, gbc_lblLogoCircle);
        
        JLabel lblTitle = new JLabel("REY UNIVERSITY", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTitle.insets = new Insets(30, 20, 5, 20);
        gbc_lblTitle.gridx = 0;
        gbc_lblTitle.gridy = 1;
        leftPanel.add(lblTitle, gbc_lblTitle);
        
        JLabel lblSubtitle = new JLabel("ENROLLMENT SYSTEM", SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(200, 220, 215));
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        
        GridBagConstraints gbc_lblSubtitle = new GridBagConstraints();
        gbc_lblSubtitle.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSubtitle.insets = new Insets(0, 20, 50, 20);
        gbc_lblSubtitle.gridx = 0;
        gbc_lblSubtitle.gridy = 2;
        leftPanel.add(lblSubtitle, gbc_lblSubtitle);
        
        JLabel lblTagline = new JLabel("", SwingConstants.CENTER);
        lblTagline.setForeground(Color.WHITE);
        lblTagline.setFont(new Font("Arial", Font.ITALIC, 14));
        
        GridBagConstraints gbc_lblTagline = new GridBagConstraints();
        gbc_lblTagline.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTagline.insets = new Insets(10, 20, 10, 20);
        gbc_lblTagline.gridx = 0;
        gbc_lblTagline.gridy = 3;
        leftPanel.add(lblTagline, gbc_lblTagline);

        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(235, 235, 235));
        rightPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc_rightPanel = new GridBagConstraints();
        gbc_rightPanel.fill = GridBagConstraints.BOTH;
        gbc_rightPanel.gridx = 1;
        gbc_rightPanel.gridy = 0;
        gbc_rightPanel.weightx = 0.65;
        gbc_rightPanel.weighty = 1.0;
        contentPane.add(rightPanel, gbc_rightPanel);
        
        JLabel lblWelcome = new JLabel("Welcome Back!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 32));
        lblWelcome.setForeground(Color.BLACK);
        
        GridBagConstraints gbc_lblWelcome = new GridBagConstraints();
        gbc_lblWelcome.anchor = GridBagConstraints.WEST;
        gbc_lblWelcome.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblWelcome.insets = new Insets(0, 100, 5, 100);
        gbc_lblWelcome.gridx = 0;
        gbc_lblWelcome.gridy = 0;
        rightPanel.add(lblWelcome, gbc_lblWelcome);
        
        JLabel lblInstruction = new JLabel("Please login to your account");
        lblInstruction.setFont(new Font("Arial", Font.PLAIN, 16));
        lblInstruction.setForeground(Color.GRAY);
        
        GridBagConstraints gbc_lblInstruction = new GridBagConstraints();
        gbc_lblInstruction.anchor = GridBagConstraints.WEST;
        gbc_lblInstruction.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblInstruction.insets = new Insets(0, 100, 30, 100);
        gbc_lblInstruction.gridx = 0;
        gbc_lblInstruction.gridy = 1;
        rightPanel.add(lblInstruction, gbc_lblInstruction);
        
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 14));
        
        GridBagConstraints gbc_lblUsername = new GridBagConstraints();
        gbc_lblUsername.anchor = GridBagConstraints.WEST;
        gbc_lblUsername.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblUsername.insets = new Insets(10, 100, 5, 100);
        gbc_lblUsername.gridx = 0;
        gbc_lblUsername.gridy = 2;
        rightPanel.add(lblUsername, gbc_lblUsername);
        
        txtUsername = new JTextField("");
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(0, 45));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 15, 0, 15)));
        
        GridBagConstraints gbc_txtUsername = new GridBagConstraints();
        gbc_txtUsername.anchor = GridBagConstraints.WEST;
        gbc_txtUsername.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtUsername.insets = new Insets(5, 100, 5, 100);
        gbc_txtUsername.gridx = 0;
        gbc_txtUsername.gridy = 3;
        rightPanel.add(txtUsername, gbc_txtUsername);
        
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 14));
        
        GridBagConstraints gbc_lblPassword = new GridBagConstraints();
        gbc_lblPassword.anchor = GridBagConstraints.WEST;
        gbc_lblPassword.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblPassword.insets = new Insets(20, 100, 5, 100);
        gbc_lblPassword.gridx = 0;
        gbc_lblPassword.gridy = 4;
        rightPanel.add(lblPassword, gbc_lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(0, 45));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 15, 0, 45)));
        
        GridBagConstraints gbc_txtPassword = new GridBagConstraints();
        gbc_txtPassword.anchor = GridBagConstraints.WEST;
        gbc_txtPassword.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtPassword.insets = new Insets(5, 100, 5, 100);
        gbc_txtPassword.gridx = 0;
        gbc_txtPassword.gridy = 5;
        rightPanel.add(txtPassword, gbc_txtPassword);
        
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(11, 55, 49));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(0, 45));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        GridBagConstraints gbc_btnLogin = new GridBagConstraints();
        gbc_btnLogin.anchor = GridBagConstraints.WEST;
        gbc_btnLogin.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnLogin.insets = new Insets(30, 100, 15, 100);
        gbc_btnLogin.gridx = 0;
        gbc_btnLogin.gridy = 6;
        rightPanel.add(btnLogin, gbc_btnLogin);
        
        JLabel lblEnroll = new JLabel("<html>No account? <font color='#0b3731'><u>Enroll now</u></font></html>");
        lblEnroll.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEnroll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        GridBagConstraints gbc_lblEnroll = new GridBagConstraints();
        gbc_lblEnroll.anchor = GridBagConstraints.WEST;
        gbc_lblEnroll.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblEnroll.insets = new Insets(10, 100, 0, 100);
        gbc_lblEnroll.gridx = 0;
        gbc_lblEnroll.gridy = 7;
        rightPanel.add(lblEnroll, gbc_lblEnroll);

        // Actions
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword());
                String sql = "select userName, password, role from usercreds where userName = ?";
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(FinalFrameOOP.this, 
                            "Please enter both username and password.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                } else {
                	
                	try(Connection conn = DBConnection.getConnection();
                		PreparedStatement pstate = conn.prepareStatement(sql);){
                		
                		SecurePass sp = new SecurePass(); 
                		
                		
                		
                		
                		
                		pstate.setString(1, username);
                		
                		ResultSet rs = pstate.executeQuery();
                		
                		if(rs.next()) {
                			String storedHash = rs.getString("password");
                			String role = rs.getString("role");
                			String usern = rs.getString("userName");
                			
                			boolean valid = sp.passChecker(password, storedHash);
                			
                			if(valid) {
                				
                				if(role.equalsIgnoreCase("admin")) {
                					 DashboardFrame dashboard = new DashboardFrame(usern,"admin");
                                     dashboard.setVisible(true);
                                     dispose();
                				}
                				else if(role.equalsIgnoreCase("cashier")) {
                					
                					DashboardFrame dashboard = new DashboardFrame(usern,"cashier");
                                    dashboard.setVisible(true);
                                    dispose();
                					JOptionPane.showMessageDialog(null,"cashier");
                				}
                				else if(role.equalsIgnoreCase("registrar")) {
                					JOptionPane.showMessageDialog(null, "registrar");
                				}
                				
                			}
                			else {
                				JOptionPane.showMessageDialog(null,"wrong");
                			}
                		}
                		
                		
                	}catch(SQLException e1) {
                		
                	}
                	
                	
                   
                }
            }
        });

        lblEnroll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	
            	
            	
            	
            	
                EnrollFrame enrollWindow = new EnrollFrame();
                enrollWindow.setVisible(true);
                dispose();
            }
        });
    }
}