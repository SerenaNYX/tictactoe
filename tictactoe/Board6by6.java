package tictactoe;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

public class Board6by6 extends JFrame {

    private Connection con;
	private JTextField p1Scoretf;
	private JTextField p2Scoretf;
	private JTextField txtClick;
	private JTextField textField_Timer;
	private JButton b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,b21,b22,b23,b24,b25,b26,b27,b28,b29,b30,b31,b32,b33,b34,b35,b36;
	private JButton btnStartGame;
	private JLabel status, lblTimerIcon, lblSpotsLeft;
	private boolean isMuted, isPlayed;
	private Timer timer;
	private final JPanel contentPane;
	private PreparedStatement stmt;
	private ResultSet result;
	private String sqlQuery;
	private String Turn="X";
	private int p1Score=1;
	private int p2Score=1;
	private int clickTime=1;
	private int pausePosition = 0;
	private boolean score;
	private boolean setTimer;
	private boolean counter;
	private Clip clip;
	private int secondsPassed = 0;

    public void Connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/tictactoe", "root", "");
			System.out.println("Connect!");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Board6by6.class.getName()).log(Level.SEVERE, "JDBC Driver not found", ex);
		} catch (SQLException ex) {
            Logger.getLogger(Board6by6.class.getName()).log(Level.SEVERE, "SQL Exception occurred", ex);
        }
	}

	private void loadMusic(String filePath) {
		// Loads the music
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
		// Plays the music
		if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Play the music in a loop
            clip.start();
        }
    }
 
	private void currentSetting() {
		try {
            sqlQuery = "SELECT match_timer, board_info, score FROM settings"; // Define the SQL query to retrieve data from the table
            stmt = con.prepareStatement(sqlQuery); // Prepare the SQL statement
            result = stmt.executeQuery(); // Execute the query

            // Retrieve the first value from the result set
            if (result.next()) {
                boolean match_timer = result.getBoolean("match_timer");
                boolean board_info = result.getBoolean("board_info");
                boolean score_info = result.getBoolean("score");
                
                // Show match timer
                if (match_timer){
                    setTimer=true;
                    textField_Timer.setEnabled(true);
                } else {
                	setTimer=false;
            		textField_Timer.setText("--:--");
                }
                
                // Show board info (spots left)
                if (board_info){
                    counter=true;
                } else {
                    counter=false;
                    txtClick.setText("-/-");
                }
                
                // Show score
                if (score_info){
                    score = true;
                } else {
                    score = false;
                    p1Scoretf.setText("-");
                    p2Scoretf.setText("-");
                }
            }
            // Close the resultset, statement
            result.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle any potential SQL errors here
        }
	}
	
	private void resetScore() {
		if(score) {
			p1Scoretf.setText("");
			p2Scoretf.setText("");
			p1Score = 1;
			p2Score = 1;
		}
	}
	
	private void checkClick(){
	    if(counter){
	    	String click = String.format("%d/36", clickTime++);
        	txtClick.setText(click);
	    }
    }

	private void startGame() {
        if (!isPlayed) {
            isPlayed = true;
			if(setTimer) {
				timer.start();
			}

			JButton[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25,
					b26,b27,b28,b29,b30,b31,b32,b33,b34,b35,b36};
			for (JButton button : buttons) {
				button.setEnabled(true);
			}
        }
    }
	
	private void resetGame() {
		btnStartGame.setEnabled(true);
	    isPlayed = false;
		status.setText("Start game!");

		JButton[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25,
				b26,b27,b28,b29,b30,b31,b32,b33,b34,b35,b36};
		for (JButton button : buttons) {
   		 	button.setText("");
			button.setEnabled(false);
			button.setBackground(Color.WHITE);
		}

	    if(counter) {
	    	clickTime=1;
	    	txtClick.setText("0/36");
	    }
	    if(setTimer){
			timer.stop();
	    	textField_Timer.setText("00:00");
			secondsPassed = 0;
	    	initializeTimer();
	    }
    }
	
	private void detTurn() {
		if(Turn.equalsIgnoreCase("X")) {
			Turn="O";
		} else{
			Turn="X";
		}
	}
	
	private void winEffect(JButton win1,JButton win2,JButton win3, JButton win4){
        win1.setBackground(Color.green);
        win2.setBackground(Color.green);
        win3.setBackground(Color.green);
        win4.setBackground(Color.green);
        
        win1.setEnabled(true);
        win2.setEnabled(true);
        win3.setEnabled(true);
        win4.setEnabled(true);
    }
	
	private void xWin() {
		Turn = "X";
		status.setText("Player 1 Wins!");

		JButton[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25,
				b26,b27,b28,b29,b30,b31,b32,b33,b34,b35,b36};
		for (JButton button : buttons) {
			button.setEnabled(false);
		}

		if(score) {
			p1Scoretf.setText(""+p1Score+++"");
		}
		if(setTimer) {
			timer.stop();
		}
		
		int choice = JOptionPane.showConfirmDialog(null, "Continue playing?", "Player 1 Wins!", JOptionPane.YES_NO_OPTION);
		switch(choice) {
			case JOptionPane.YES_OPTION->{
				resetGame();
				status.setText("Start game!");
			}	
			case JOptionPane.NO_OPTION->{
				mainMenu();
			}
			case JOptionPane.CLOSED_OPTION->{
				btnStartGame.setEnabled(false);
				status.setText("Reset game!");
			}
			default ->{
			}
		}
	}
	
	private void oWin() {
		Turn = "O";
		status.setText("Player 2 Wins!");

		JButton[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25,
				b26,b27,b28,b29,b30,b31,b32,b33,b34,b35,b36};
		for (JButton button : buttons) {
			button.setEnabled(false);
		}

		if(score) {
			p2Scoretf.setText(""+p2Score+++"");
		}
		if(setTimer) {
			timer.stop();
		}
		
		int choice = JOptionPane.showConfirmDialog(null, "Continue playing?", "Player 2 Wins!", JOptionPane.YES_NO_OPTION);
		switch(choice) {
			case JOptionPane.YES_OPTION->{ // Continue
				resetGame();
				status.setText("Start game!");
			}
			case JOptionPane.NO_OPTION->{
				mainMenu();
			}
			case JOptionPane.CLOSED_OPTION->{
				btnStartGame.setEnabled(false);
				status.setText("Reset game!");
			}
			default ->{
			}
		}
	}

	private void detWin() {
		String n1 = b1.getText();
		String n2 = b2.getText();
		String n3 = b3.getText();
		String n4 = b4.getText();
		String n5 = b5.getText();
		String n6 = b6.getText();
		String n7 = b7.getText();
		String n8 = b8.getText();
		String n9 = b9.getText();
		String n10 = b10.getText();
		String n11 = b11.getText();
		String n12 = b12.getText();
		String n13 = b13.getText();
		String n14 = b14.getText();
		String n15 = b15.getText();
		String n16=  b16.getText();
		String n17 = b17.getText();
		String n18 = b18.getText();
		String n19 = b19.getText();
		String n20 = b20.getText();
		String n21 = b21.getText();
		String n22 = b22.getText();
		String n23 = b23.getText();
		String n24 = b24.getText();
		String n25 = b25.getText();
		String n26=  b26.getText();
		String n27 = b27.getText();
		String n28 = b28.getText();
		String n29 = b29.getText();
		String n30 = b30.getText();
		String n31 = b31.getText();
		String n32 = b32.getText();
		String n33 = b33.getText();
		String n34 = b34.getText();
		String n35 = b35.getText();
		String n36 = b36.getText();
		
		//for all 4 rows occupied
		if ("X".equals(n1) && "X".equals(n2) && "X".equals(n3) && "X".equals(n4)) { 
			winEffect(b1, b2, b3, b4); 
			xWin(); 
		} else if ("X".equals(n2) && "X".equals(n3) && "X".equals(n4) && "X".equals(n5)) { 
			winEffect(b2, b3, b4, b5); 
			xWin(); 
		} else if ("X".equals(n3) && "X".equals(n4) && "X".equals(n5) && "X".equals(n6)) { 
			winEffect(b3, b4, b5, b6); 
			xWin(); 
		} else if ("X".equals(n7) && "X".equals(n8) && "X".equals(n9) && "X".equals(n10)) { 
			winEffect(b7, b8, b9, b10); 
			xWin(); 
		} else if ("X".equals(n8) && "X".equals(n9) && "X".equals(n10) && "X".equals(n11)) { 
			winEffect(b8, b9, b10, b11); 
			xWin(); 
		} else if ("X".equals(n9) && "X".equals(n10) && "X".equals(n11) && "X".equals(n12)) { 
			winEffect(b9, b10, b11, b12); 
			xWin(); 
		} else if ("X".equals(n13) && "X".equals(n14) && "X".equals(n15) && "X".equals(n16)) { 
			winEffect(b13, b14, b15, b16); 
			xWin(); 
		} else if ("X".equals(n14) && "X".equals(n15) && "X".equals(n16) && "X".equals(n17)) { 
			winEffect(b14, b15, b16, b17); 
			xWin(); 
		} else if ("X".equals(n15) && "X".equals(n16) && "X".equals(n17) && "X".equals(n18)) { 
		 	winEffect(b15, b16, b17, b18); 
			xWin(); 
		} else if ("X".equals(n19) && "X".equals(n20) && "X".equals(n21) && "X".equals(n22)) { 
			winEffect(b19, b20, b21, b22);
			xWin();
		} else if ("X".equals(n20) && "X".equals(n21) && "X".equals(n22) && "X".equals(n23)) { 
			winEffect(b20, b21, b22, b23); 
			xWin();
		} else if ("X".equals(n21) && "X".equals(n22) && "X".equals(n23) && "X".equals(n24)) { 
			winEffect(b21, b22, b23, b24);
			xWin(); 
		} else if ("X".equals(n25) && "X".equals(n26) && "X".equals(n27) && "X".equals(n28)) { 
			winEffect(b25, b26, b27, b28); 
			xWin(); 
		} else if ("X".equals(n26) && "X".equals(n27) && "X".equals(n28) && "X".equals(n29)) { 
			winEffect(b26, b27, b28, b29); 
			xWin(); 
		} else if ("X".equals(n27) && "X".equals(n28) && "X".equals(n29) && "X".equals(n30)) { 
			winEffect(b27, b28, b29, b30); 
			xWin(); 
		} else if ("X".equals(n31) && "X".equals(n32) && "X".equals(n33) && "X".equals(n34)) { 
			winEffect(b31, b32, b33, b34); 
			xWin(); 
		} else if ("X".equals(n32) && "X".equals(n33) && "X".equals(n34) && "X".equals(n35)) { 
			winEffect(b32, b33, b34, b35);
			xWin(); 
		} else if ("X".equals(n33) && "X".equals(n34) && "X".equals(n35) && "X".equals(n36)) { 
			winEffect(b33, b34, b35, b36); 
			xWin(); 
		} else if ("X".equals(n1) && "X".equals(n7) && "X".equals(n13) && "X".equals(n19)) {
		    winEffect(b1, b7, b13, b19);
		    xWin();
		} else if ("X".equals(n7) && "X".equals(n13) && "X".equals(n19) && "X".equals(n25)) {
		    winEffect(b7, b13, b19, b25);
		    xWin();
		} else if ("X".equals(n13) && "X".equals(n19) && "X".equals(n25) && "X".equals(n31)) {
		    winEffect(b13, b19, b25, b31);
		    xWin();
		} else if ("X".equals(n2) && "X".equals(n8) && "X".equals(n14) && "X".equals(n20)) {
		    winEffect(b2, b8, b14, b20);
		    xWin();
		} else if ("X".equals(n8) && "X".equals(n14) && "X".equals(n20) && "X".equals(n26)) {
		    winEffect(b8, b14, b20, b26);
		    xWin();
		} else if ("X".equals(n14) && "X".equals(n20) && "X".equals(n26) && "X".equals(n32)) {
		    winEffect(b14, b20, b26, b32);
		    xWin();
		} else if ("X".equals(n3) && "X".equals(n9) && "X".equals(n15) && "X".equals(n21)) {
		    winEffect(b3, b9, b15, b21);
		    xWin();
		} else if ("X".equals(n9) && "X".equals(n15) && "X".equals(n21) && "X".equals(n27)) {
		    winEffect(b9, b15, b21, b27);
		    xWin();
		} else if ("X".equals(n15) && "X".equals(n21) && "X".equals(n27) && "X".equals(n33)) {
		    winEffect(b15, b21, b27, b33);
		    xWin();
		} else if ("X".equals(n4) && "X".equals(n10) && "X".equals(n16) && "X".equals(n22)) {
		    winEffect(b4, b10, b16, b22);
		    xWin();
		} else if ("X".equals(n10) && "X".equals(n16) && "X".equals(n22) && "X".equals(n28)) {
		    winEffect(b10, b16, b22, b28);
		    xWin();
		} else if ("X".equals(n16) && "X".equals(n22) && "X".equals(n28) && "X".equals(n34)) {
		    winEffect(b16, b22, b28, b34);
		    xWin();
		} else if ("X".equals(n5) && "X".equals(n11) && "X".equals(n17) && "X".equals(n23)) {
		    winEffect(b5, b11, b17, b23);
		    xWin();
		} else if ("X".equals(n11) && "X".equals(n17) && "X".equals(n23) && "X".equals(n29)) {
		    winEffect(b11, b17, b23, b29);
		    xWin();
		} else if ("X".equals(n17) && "X".equals(n23) && "X".equals(n29) && "X".equals(n35)) {
		    winEffect(b17, b23, b29, b35);
		    xWin();
		} else if ("X".equals(n6) && "X".equals(n12) && "X".equals(n18) && "X".equals(n24)) {
		    winEffect(b6, b12, b18, b24);
		    xWin();
		} else if ("X".equals(n12) && "X".equals(n18) && "X".equals(n24) && "X".equals(n30)) {
		    winEffect(b12, b18, b24, b30);
		    xWin();
		} else if ("X".equals(n18) && "X".equals(n24) && "X".equals(n30) && "X".equals(n36)) {
		    winEffect(b18, b24, b30, b36);
		    xWin();
		} else if ("X".equals(n1) && "X".equals(n8) && "X".equals(n15) && "X".equals(n22)) {
		    winEffect(b1, b8, b15, b22);
		    xWin();
		} else if ("X".equals(n8) && "X".equals(n15) && "X".equals(n22) && "X".equals(n29)) {
		    winEffect(b8, b15, b22, b29);
		    xWin();
		} else if ("X".equals(n15) && "X".equals(n22) && "X".equals(n29) && "X".equals(n36)) {
		    winEffect(b15, b22, b29, b36);
		    xWin();
		} else if ("X".equals(n2) && "X".equals(n9) && "X".equals(n16) && "X".equals(n23)) {
		    winEffect(b2, b9, b16, b23);
		    xWin();
		} else if ("X".equals(n9) && "X".equals(n16) && "X".equals(n23) && "X".equals(n30)) {
		    winEffect(b9, b16, b23, b30);
		    xWin();
		} else if ("X".equals(n3) && "X".equals(n10) && "X".equals(n17) && "X".equals(n24)) {
		    winEffect(b3, b10, b17, b24);
		    xWin();
		}else if ("X".equals(n13) && "X".equals(n20) && "X".equals(n27) && "X".equals(n34)) {
		    winEffect(b13, b20, b27, b34);
		    xWin();
		} else if ("X".equals(n7) && "X".equals(n14) && "X".equals(n21) && "X".equals(n28)) {
		    winEffect(b7, b14, b21, b28);
		    xWin();
		} else if ("X".equals(n14) && "X".equals(n21) && "X".equals(n28) && "X".equals(n35)) {
		    winEffect(b14, b21, b28, b35);
		    xWin();
		} else if ("X".equals(n4) && "X".equals(n9) && "X".equals(n14) && "X".equals(n19)) {
		    winEffect(b4, b9, b14, b19);
		    xWin();
		} else if ("X".equals(n6) && "X".equals(n11) && "X".equals(n16) && "X".equals(n21)) {
		    winEffect(b6, b11, b16, b21);
		    xWin();
		} else if ("X".equals(n5) && "X".equals(n10) && "X".equals(n15) && "X".equals(n20)) {
		    winEffect(b5, b10, b15, b20);
		    xWin();
		} else if ("X".equals(n10) && "X".equals(n15) && "X".equals(n20) && "X".equals(n25)) {
		    winEffect(b10, b15, b20, b25);
		    xWin();
		}else if ("X".equals(n18) && "X".equals(n23) && "X".equals(n28) && "X".equals(n33)) {
		    winEffect(b18, b23, b28, b33);
		    xWin();
		}else if ("X".equals(n12) && "X".equals(n17) && "X".equals(n22) && "X".equals(n27)) {
		    winEffect(b12, b17, b22, b27);
		    xWin();
		}else if ("X".equals(n17) && "X".equals(n22) && "X".equals(n27) && "X".equals(n32)) {
		    winEffect(b17, b22, b27, b32);
		    xWin();
		}else if ("X".equals(n6) && "X".equals(n11) && "X".equals(n16) && "X".equals(n21)) {
		    winEffect(b6, b11, b16, b21);
		    xWin();
		}else if ("X".equals(n11) && "X".equals(n16) && "X".equals(n21) && "X".equals(n26)) {
		    winEffect(b11, b16, b21, b26);
		    xWin();
		}else if ("X".equals(n16) && "X".equals(n21) && "X".equals(n26) && "X".equals(n31)) {
		    winEffect(b16, b21, b26, b31);
		    xWin();
		}else if ("O".equals(n1) && "O".equals(n2) && "O".equals(n3) && "O".equals(n4)) { //for O
			winEffect(b1, b2, b3, b4); 
			oWin(); 
		} else if ("O".equals(n2) && "O".equals(n3) && "O".equals(n4) && "O".equals(n5)) { 
			winEffect(b2, b3, b4, b5); 
			oWin(); 
		} else if ("O".equals(n3) && "O".equals(n4) && "O".equals(n5) && "O".equals(n6)) { 
			winEffect(b3, b4, b5, b6); 
			oWin(); 
		} else if ("O".equals(n7) && "O".equals(n8) && "O".equals(n9) && "O".equals(n10)) { 
			winEffect(b7, b8, b9, b10); 
			oWin(); 
		} else if ("O".equals(n8) && "O".equals(n9) && "O".equals(n10) && "O".equals(n11)) { 
			winEffect(b8, b9, b10, b11); 
			oWin(); 
		} else if ("O".equals(n9) && "O".equals(n10) && "O".equals(n11) && "O".equals(n12)) { 
			winEffect(b9, b10, b11, b12); 
			oWin(); 
		} else if ("O".equals(n13) && "O".equals(n14) && "O".equals(n15) && "O".equals(n16)) { 
			winEffect(b13, b14, b15, b16); 
			oWin(); 
		} else if ("O".equals(n14) && "O".equals(n15) && "O".equals(n16) && "O".equals(n17)) { 
			winEffect(b14, b15, b16, b17); 
			oWin(); 
		} else if ("O".equals(n15) && "O".equals(n16) && "O".equals(n17) && "O".equals(n18)) { 
		 	winEffect(b15, b16, b17, b18); 
			oWin(); 
		} else if ("O".equals(n19) && "O".equals(n20) && "O".equals(n21) && "O".equals(n22)) { 
			winEffect(b19, b20, b21, b22);
			oWin();
		} else if ("O".equals(n20) && "O".equals(n21) && "O".equals(n22) && "O".equals(n23)) { 
			winEffect(b20, b21, b22, b23); 
			oWin();
		} else if ("O".equals(n21) && "O".equals(n22) && "O".equals(n23) && "O".equals(n24)) { 
			winEffect(b21, b22, b23, b24);
			oWin(); 
		} else if ("O".equals(n25) && "O".equals(n26) && "O".equals(n27) && "O".equals(n28)) { 
			winEffect(b25, b26, b27, b28); 
			oWin(); 
		} else if ("O".equals(n26) && "O".equals(n27) && "O".equals(n28) && "O".equals(n29)) { 
			winEffect(b26, b27, b28, b29); 
			oWin(); 
		} else if ("O".equals(n27) && "O".equals(n28) && "O".equals(n29) && "O".equals(n30)) { 
			winEffect(b27, b28, b29, b30); 
			oWin(); 
		} else if ("O".equals(n31) && "O".equals(n32) && "O".equals(n33) && "O".equals(n34)) { 
			winEffect(b31, b32, b33, b34); 
			oWin(); 
		} else if ("O".equals(n32) && "O".equals(n33) && "O".equals(n34) && "O".equals(n35)) { 
			winEffect(b32, b33, b34, b35);
			oWin(); 
		} else if ("O".equals(n33) && "O".equals(n34) && "O".equals(n35) && "O".equals(n36)) { 
			winEffect(b33, b34, b35, b36); 
			oWin(); 
		} else if ("O".equals(n1) && "O".equals(n7) && "O".equals(n13) && "O".equals(n19)) {
		    winEffect(b1, b7, b13, b19);
		    oWin();
		} else if ("O".equals(n7) && "O".equals(n13) && "O".equals(n19) && "O".equals(n25)) {
		    winEffect(b7, b13, b19, b25);
		    oWin();
		} else if ("O".equals(n13) && "O".equals(n19) && "O".equals(n25) && "O".equals(n31)) {
		    winEffect(b13, b19, b25, b31);
		    oWin();
		} else if ("O".equals(n2) && "O".equals(n8) && "O".equals(n14) && "O".equals(n20)) {
		    winEffect(b2, b8, b14, b20);
		    oWin();
		} else if ("O".equals(n8) && "O".equals(n14) && "O".equals(n20) && "O".equals(n26)) {
		    winEffect(b8, b14, b20, b26);
		    oWin();
		} else if ("O".equals(n14) && "O".equals(n20) && "O".equals(n26) && "O".equals(n32)) {
		    winEffect(b14, b20, b26, b32);
		    oWin();
		} else if ("O".equals(n3) && "O".equals(n9) && "O".equals(n15) && "O".equals(n21)) {
		    winEffect(b3, b9, b15, b21);
		    oWin();
		} else if ("O".equals(n9) && "O".equals(n15) && "O".equals(n21) && "O".equals(n27)) {
		    winEffect(b9, b15, b21, b27);
		    oWin();
		} else if ("O".equals(n15) && "O".equals(n21) && "O".equals(n27) && "O".equals(n33)) {
		    winEffect(b15, b21, b27, b33);
		    oWin();
		} else if ("O".equals(n4) && "O".equals(n10) && "O".equals(n16) && "O".equals(n22)) {
		    winEffect(b4, b10, b16, b22);
		    oWin();
		} else if ("O".equals(n10) && "O".equals(n16) && "O".equals(n22) && "O".equals(n28)) {
		    winEffect(b10, b16, b22, b28);
		    oWin();
		} else if ("O".equals(n16) && "O".equals(n22) && "O".equals(n28) && "O".equals(n34)) {
		    winEffect(b16, b22, b28, b34);
		    oWin();
		} else if ("O".equals(n5) && "O".equals(n11) && "O".equals(n17) && "O".equals(n23)) {
		    winEffect(b5, b11, b17, b23);
		    oWin();
		} else if ("O".equals(n11) && "O".equals(n17) && "O".equals(n23) && "O".equals(n29)) {
		    winEffect(b11, b17, b23, b29);
		    oWin();
		} else if ("O".equals(n17) && "O".equals(n23) && "O".equals(n29) && "O".equals(n35)) {
		    winEffect(b17, b23, b29, b35);
		    oWin();
		} else if ("O".equals(n6) && "O".equals(n12) && "O".equals(n18) && "O".equals(n24)) {
		    winEffect(b6, b12, b18, b24);
		    oWin();
		} else if ("O".equals(n12) && "O".equals(n18) && "O".equals(n24) && "O".equals(n30)) {
		    winEffect(b12, b18, b24, b30);
		    oWin();
		} else if ("O".equals(n18) && "O".equals(n24) && "O".equals(n30) && "O".equals(n36)) {
		    winEffect(b18, b24, b30, b36);
		    oWin();
		} else if ("O".equals(n1) && "O".equals(n8) && "O".equals(n15) && "O".equals(n22)) {
		    winEffect(b1, b8, b15, b22);
		    oWin();
		} else if ("O".equals(n8) && "O".equals(n15) && "O".equals(n22) && "O".equals(n29)) {
		    winEffect(b8, b15, b22, b29);
		    oWin();
		} else if ("O".equals(n15) && "O".equals(n22) && "O".equals(n29) && "O".equals(n36)) {
		    winEffect(b15, b22, b29, b36);
		    oWin();
		} else if ("O".equals(n2) && "O".equals(n9) && "O".equals(n16) && "O".equals(n23)) {
		    winEffect(b2, b9, b16, b23);
		    oWin();
		} else if ("O".equals(n9) && "O".equals(n16) && "O".equals(n23) && "O".equals(n30)) {
		    winEffect(b9, b16, b23, b30);
		    oWin();
		} else if ("O".equals(n3) && "O".equals(n10) && "O".equals(n17) && "O".equals(n24)) {
		    winEffect(b3, b10, b17, b24);
		    oWin();
		}else if ("O".equals(n13) && "O".equals(n20) && "O".equals(n27) && "O".equals(n34)) {
		    winEffect(b13, b20, b27, b34);
		    oWin();
		} else if ("O".equals(n7) && "O".equals(n14) && "O".equals(n21) && "O".equals(n28)) {
		    winEffect(b7, b14, b21, b28);
		    oWin();
		} else if ("O".equals(n14) && "O".equals(n21) && "O".equals(n28) && "O".equals(n35)) {
		    winEffect(b14, b21, b28, b35);
		    oWin();
		} else if ("O".equals(n4) && "O".equals(n9) && "O".equals(n14) && "O".equals(n19)) {
		    winEffect(b4, b9, b14, b19);
		    oWin();
		} else if ("O".equals(n6) && "O".equals(n11) && "O".equals(n16) && "O".equals(n21)) {
		    winEffect(b6, b11, b16, b21);
		    oWin();
		} else if ("O".equals(n5) && "O".equals(n10) && "O".equals(n15) && "O".equals(n20)) {
		    winEffect(b5, b10, b15, b20);
		    oWin();
		} else if ("O".equals(n10) && "O".equals(n15) && "O".equals(n20) && "O".equals(n25)) {
		    winEffect(b10, b15, b20, b25);
		    oWin();
		}else if ("O".equals(n18) && "O".equals(n23) && "O".equals(n28) && "O".equals(n33)) {
		    winEffect(b18, b23, b28, b33);
		    oWin();
		}else if ("O".equals(n12) && "O".equals(n17) && "O".equals(n22) && "O".equals(n27)) {
		    winEffect(b12, b17, b22, b27);
		    oWin();
		}else if ("O".equals(n17) && "O".equals(n22) && "O".equals(n27) && "O".equals(n32)) {
		    winEffect(b17, b22, b27, b32);
		    oWin();
		}else if ("O".equals(n6) && "O".equals(n11) && "O".equals(n16) && "O".equals(n21)) {
		    winEffect(b6, b11, b16, b21);
		    oWin();
		}else if ("O".equals(n11) && "O".equals(n16) && "O".equals(n21) && "O".equals(n26)) {
		    winEffect(b11, b16, b21, b26);
		    oWin();
		}else if ("O".equals(n16) && "O".equals(n21) && "O".equals(n26) && "O".equals(n31)) {
		    winEffect(b16, b21, b26, b31);
		    oWin();
		}		
	}
	
	private void detDraw() {
		String n1 = b1.getText();
		String n2 = b2.getText();
		String n3 = b3.getText();
		String n4 = b4.getText();
		String n5 = b5.getText();
		String n6 = b6.getText();
		String n7 = b7.getText();
		String n8 = b8.getText();
		String n9 = b9.getText();
		String n10 = b10.getText();
		String n11 = b11.getText();
		String n12 = b12.getText();
		String n13 = b13.getText();
		String n14 = b14.getText();
		String n15 = b15.getText();
		String n16=  b16.getText();
		String n17 = b17.getText();
		String n18 = b18.getText();
		String n19 = b19.getText();
		String n20 = b20.getText();
		String n21 = b21.getText();
		String n22 = b22.getText();
		String n23 = b23.getText();
		String n24 = b24.getText();
		String n25 = b25.getText();
		String n26=  b26.getText();
		String n27 = b27.getText();
		String n28 = b28.getText();
		String n29 = b29.getText();
		String n30 = b30.getText();
		String n31 = b31.getText();
		String n32 = b32.getText();
		String n33 = b33.getText();
		String n34 = b34.getText();
		String n35 = b35.getText();
		String n36 = b36.getText();
		
		if(!n1.isEmpty() && !n2.isEmpty() && !n3.isEmpty() && !n4.isEmpty() && !n5.isEmpty() && !n6.isEmpty() && !n7.isEmpty() && !n8.isEmpty() && !n9.isEmpty()
				&& !n10.isEmpty() && !n11.isEmpty() && !n12.isEmpty() && !n13.isEmpty() && !n14.isEmpty() && !n15.isEmpty() && !n16.isEmpty() && !n17.isEmpty()&& !n18.isEmpty() 
				&& !n19.isEmpty() && !n20.isEmpty() && !n21.isEmpty() && !n22.isEmpty() && !n23.isEmpty() && !n24.isEmpty() && !n25.isEmpty()
				&& !n26.isEmpty() && !n27.isEmpty() && !n28.isEmpty() && !n29.isEmpty() && !n30.isEmpty() && !n31.isEmpty() && !n32.isEmpty()
				&& !n33.isEmpty() && !n34.isEmpty() && !n35.isEmpty() && !n36.isEmpty()) {
			status.setText("Draw");
			if(setTimer) {
				timer.stop();
			}

			int choice = JOptionPane.showConfirmDialog(null, "Continue playing?", "It's a Draw!", JOptionPane.YES_NO_OPTION);
				switch(choice) {
				case JOptionPane.YES_OPTION->{ // Continue
					resetGame();
					status.setText("Start game!");
				}
				case JOptionPane.NO_OPTION->{
					mainMenu();
				}
				case JOptionPane.CLOSED_OPTION->{
					btnStartGame.setEnabled(false);
					status.setText("Reset game!");
				}
				default ->{
				}
			}
		}
	}
	
	private void mainMenu() {
		try {
			clip.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Homepage menu = new Homepage();
		setVisible(false);
		menu.setVisible(true);
	}

	private static void buttonColor(JButton button) {
        Runnable colorChanger = new Runnable() {
			private boolean isSwitch = true;
			@Override
			public void run() {
				while(true) {
					try {
						Color newColor = isSwitch ? new Color(255, 150, 200) : new Color(250, 168, 135);
						button.setBorder(new LineBorder(newColor, 3, true));				
						isSwitch = !isSwitch;
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(colorChanger).start();
	}
	
	private static void colorChanging(JLabel label) {
		Runnable colorChanger = new Runnable() {
			private boolean isSwitch = true;
			@Override
			public void run() {
				while(true) {
					try {
						Color newColor = isSwitch ? new Color(250, 227, 104) : new Color(255, 255, 255);
						label.setForeground(newColor);
						isSwitch = !isSwitch;
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(colorChanger).start();
	}
	
	private void initializeTimer() {
		timer = new Timer(1000, (ActionEvent e) -> {
            secondsPassed++;
        	int minutes = secondsPassed / 60;
            int seconds = secondsPassed % 60;
            String timeString = String.format("%02d:%02d", minutes, seconds);
            textField_Timer.setText(timeString);
            textField_Timer.setHorizontalAlignment(SwingConstants.CENTER);
        });
	}

	private void btnActionPerformed(ActionEvent evt, JButton button) {
		if (button.getText().isEmpty()) {
            button.setEnabled(false);
            button.setText(Turn);
            if(Turn.equalsIgnoreCase("X")){
                status.setText("Player 2's turn");
            }
            else{
                status.setText("Player 1's turn");
            }
            checkClick();
            detTurn();
            detWin();
            detDraw();
        }
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Board6by6(); // Create an instance which initializes and shows the frame
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}

	/**
	 * Initialize the contents of the 
	 */
	public Board6by6() {

		contentPane = new JPanel();
		contentPane.setBackground(new Color(150, 170, 217));
		contentPane.setLayout(null);
		contentPane.setBorder(new LineBorder(new Color(255, 255, 255), 10, true));

		JPanel panel = new JPanel();
		panel.setBackground(new Color(155, 218, 242));
		panel.setBounds(9, 130, 682, 118);
		panel.setLayout(null);
		panel.setBorder(new LineBorder(new Color(255, 255, 255), 2, true));
		contentPane.add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        setBounds(100, 100, 700, 700);
		setUndecorated(true);
		setLocationRelativeTo(null);
        setVisible(true);
		
		// PLAYER 1 AND PLAYER 2 SCORE
		BufferedImage originalImage = null;
	    try {
            originalImage = ImageIO.read(new File("tictactoe\\images\\player.png"));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
        Image resizedPlayer = originalImage.getScaledInstance(30, 35, Image.SCALE_SMOOTH); // Resize image
	    ImageIcon playerIcon = new ImageIcon(resizedPlayer); // Create ImageIcon with resized image
	    
		JLabel lblPlayer1 = new JLabel(" Player 1");
		lblPlayer1.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 18));
	    lblPlayer1.setBounds(108, 63, 130, 45);
		lblPlayer1.setForeground(Color.BLACK);
		lblPlayer1.setIcon(playerIcon); // Set the icon to the JLabel
	    panel.add(lblPlayer1);

	    JLabel lblPlayer2 = new JLabel(" Player 2");
	    lblPlayer2.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 18));
	    lblPlayer2.setBounds(456, 63, 130, 45);
		lblPlayer2.setForeground(Color.BLACK);
	    lblPlayer2.setIcon(playerIcon); // Set the icon to the JLabel
	    panel.add(lblPlayer2);

	    p1Scoretf = new JTextField("");
	    p1Scoretf.setEditable(false);
		p1Scoretf.setFocusable(false);
	    p1Scoretf.setBorder(new LineBorder(new Color(217, 85, 181), 3, true));
	    p1Scoretf.setBounds(246, 71, 38, 34);
		p1Scoretf.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 20));
		p1Scoretf.setHorizontalAlignment(SwingConstants.CENTER);
	    panel.add(p1Scoretf);
	    p1Scoretf.setColumns(10);
	    
	    p2Scoretf = new JTextField("");
	    p2Scoretf.setEditable(false);
		p2Scoretf.setFocusable(false);
	    p2Scoretf.setColumns(10);
	    p2Scoretf.setBorder(new LineBorder(new Color(217, 85, 181), 3, true));
	    p2Scoretf.setBounds(393, 71, 38, 34);
		p2Scoretf.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 20));
		p2Scoretf.setHorizontalAlignment(SwingConstants.CENTER);
	    panel.add(p2Scoretf);
	    
		// VS LABEL
	    JLabel lblVersus = new JLabel("VS");
	    lblVersus.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
		lblVersus.setForeground(Color.BLACK);
	    lblVersus.setBounds(317, 80, 45, 23);
	    lblVersus.setHorizontalAlignment(JLabel.CENTER);
	    panel.add(lblVersus);

		// TIMER ICON
	    lblTimerIcon = new JLabel("");
	    lblTimerIcon.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 15));
	    BufferedImage timerImage = null;// To insert timer icon
        try {
            timerImage = ImageIO.read(new File("tictactoe\\images\\timer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image resizedTimer = timerImage.getScaledInstance(35, 35, Image.SCALE_SMOOTH); // Resize the image
        ImageIcon timerIcon = new ImageIcon(resizedTimer); // Create new ImageIcon with the resized image

        // Set the icon to the JLabel
        lblTimerIcon.setIcon(timerIcon);
        lblTimerIcon.setBounds(108, 10, 38, 45);
	    panel.add(lblTimerIcon);
	    
		// Spots taken icon
	    lblSpotsLeft = new JLabel("");
	    lblSpotsLeft.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 15));
	    
	    BufferedImage spotImage = null; // Inserting timer icon
        try {
            spotImage = ImageIO.read(new File("tictactoe\\images\\board info.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image resizedSpot = spotImage.getScaledInstance(30, 35, Image.SCALE_SMOOTH); // Resize the image
        ImageIcon spotIcon = new ImageIcon(resizedSpot); // Create new ImageIcon with the resized image

        lblSpotsLeft.setIcon(spotIcon); // Set the icon to the JLabel
        lblSpotsLeft.setBounds(536, 10, 38, 45);
	    panel.add(lblSpotsLeft);
	    
		// NUMBER OF SPOTS TAKEN
	    txtClick= new JTextField("0/36");
	    txtClick.setEditable(false);
		txtClick.setFocusable(false);
	    txtClick.setColumns(10);
		txtClick.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 20));
		txtClick.setForeground(Color.BLACK);
	    txtClick.setBorder(new LineBorder(new Color(217, 85, 181), 3, true));
	    txtClick.setBounds(393, 19, 133, 34);
		txtClick.setHorizontalAlignment(SwingConstants.CENTER);
	    panel.add(txtClick);
	    
		// TIMER
	    textField_Timer = new JTextField("00:00");
	    textField_Timer.setEditable(false);
		textField_Timer.setFocusable(false);
	    textField_Timer.setColumns(10);
		textField_Timer.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 20));
		textField_Timer.setForeground(Color.BLACK);
	    textField_Timer.setBorder(new LineBorder(new Color(217, 85, 181), 3, true));
	    textField_Timer.setBounds(151, 19, 133, 34);
		textField_Timer.setHorizontalAlignment(SwingConstants.CENTER);
	    panel.add(textField_Timer);
		
		// TIC TAC TOE title
		JPanel panel_mainTitle = new JPanel();
		panel_mainTitle.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_mainTitle.setBackground(new Color(145, 99, 191));
		panel_mainTitle.setBounds(39, 36, 604, 78);
		contentPane.add(panel_mainTitle);
		panel_mainTitle.setLayout(null);
	
		JLabel lblTTT = new JLabel("TIC TAC TOE");
		lblTTT.setFont(new Font("Pile Of Rock", Font.PLAIN, 48));
		lblTTT.setBounds(170, 10, 255, 63);
		lblTTT.setHorizontalAlignment(JLabel.CENTER);
		lblTTT.setForeground(Color.BLACK);
		panel_mainTitle.add(lblTTT);
		colorChanging(lblTTT);
		
		// GAME STATUS TEXT (Start/Reset/Player Turn/...)
		status = new JLabel("Start game!");
		status.setFont(new Font("Pile Of Rock", Font.PLAIN, 25));
		status.setForeground(Color.BLACK);
		status.setBackground(new Color(145, 99, 191));
		status.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		status.setOpaque(true);
		status.setBounds(497, 303, 170, 50);
		status.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(status);
		colorChanging(status);

		// START GAME
		btnStartGame = new JButton("START GAME");
		btnStartGame.addActionListener((ActionEvent e) -> {
            startGame();
			if(Turn.equalsIgnoreCase("X")) {
				status.setText("Player 1's turn");
			} else {
				status.setText("Player 2's turn");
			}
        });
		btnStartGame.setFont(new Font("Pile Of Rock", Font.PLAIN, 17));
		btnStartGame.setBackground(new Color(217, 85, 181));
		btnStartGame.setForeground(Color.BLACK);
		btnStartGame.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnStartGame.setBounds(507, 381, 150, 45);
		btnStartGame.setFocusable(false);
		contentPane.add(btnStartGame);

		// RESET GAME
		JButton btnResetGame = new JButton("RESET GAME");
		btnResetGame.addActionListener((ActionEvent e) -> {
            resetGame();
			resetScore();
        });
		btnResetGame.setFont(new Font("Pile Of Rock", Font.PLAIN, 17));
		btnResetGame.setBackground(new Color(217, 85, 181));
		btnResetGame.setForeground(Color.BLACK);
		btnResetGame.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnResetGame.setBounds(507, 442, 150, 45);
		btnResetGame.setFocusable(false);
		contentPane.add(btnResetGame);
		
		// RETURN TO MENU
		JButton btnMainMenu = new JButton("BACK TO MAIN MENU");
		btnMainMenu.addActionListener((ActionEvent e) -> {
			mainMenu();
		});
		btnMainMenu.setFont(new Font("Pile Of Rock", Font.PLAIN, 17));
		btnMainMenu.setBackground(new Color(217, 85, 181));
		btnMainMenu.setForeground(Color.BLACK);
		btnMainMenu.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnMainMenu.setBounds(507, 507, 150, 45);
		btnMainMenu.setFocusable(false);
		contentPane.add(btnMainMenu);
		
		// BACKGROUND MUSIC
		JToggleButton tglMusic = new JToggleButton("");
		BufferedImage soundoffImage = null; // To insert soundoff icon
		BufferedImage soundImage = null; // To insert soundon icon  
	    try {
	        soundoffImage = ImageIO.read(new File("tictactoe\\images\\sounds off.png"));
            soundImage = ImageIO.read(new File("tictactoe\\images\\sounds on.png"));
	    } catch (IOException r) {
	        r.printStackTrace();
        }
	    Image resizedSoundOff = soundoffImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Resize the image
        Image resizedSound = soundImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);

	    // Create new ImageIcon with the resized image
	    ImageIcon soundOffIcon = new ImageIcon(resizedSoundOff);
        ImageIcon soundIcon = new ImageIcon(resizedSound);
		
		tglMusic.setIcon(soundIcon);
		tglMusic.addMouseListener(new MouseAdapter() { // Action when toggle is clicked
			@Override
			public void mouseClicked(MouseEvent e) {
				// Toggle the mute state
				isMuted = !isMuted;
				if (isMuted) {
					tglMusic.setIcon(soundOffIcon);
					if (clip != null && clip.isRunning()) {
						pausePosition = clip.getFramePosition(); // Save the current position
						try {
							clip.stop();
						} catch (Exception r) {
							r.printStackTrace();
						}
					}
				} else {
					tglMusic.setIcon(soundIcon);
					if (clip != null) {
						clip.setFramePosition(pausePosition); // Resume from the saved position
						clip.start();
					}
				}
				
			}
		});
		tglMusic.setBackground(new Color(107, 242, 242));
		tglMusic.setBounds(645, 15, 35, 35);
		tglMusic.setOpaque(false);
		tglMusic.setFocusable(false);
		tglMusic.setBorderPainted(false);
		contentPane.add(tglMusic);
		
		// GAME BOARD (Buttons 1 - 36)
		JPanel panel_gameBoard = new JPanel();
		panel_gameBoard.setBounds(35, 280, 442, 367);
		contentPane.add(panel_gameBoard);
		panel_gameBoard.setLayout(new GridLayout(6, 6, 0, 0));

		b1 = new JButton("");
		b2 = new JButton("");
		b3 = new JButton("");
		b4 = new JButton("");
		b5 = new JButton("");
		b6 = new JButton("");
		b7 = new JButton("");
		b8 = new JButton("");
		b9 = new JButton("");
		b10 = new JButton("");
		b11 = new JButton("");
		b12= new JButton("");
		b13 = new JButton("");
		b14= new JButton("");
		b15 = new JButton("");
		b16 = new JButton("");
		b17 = new JButton("");
		b18 = new JButton("");
		b19 = new JButton("");
		b20 = new JButton("");
		b21 = new JButton("");
		b22 = new JButton("");
		b23= new JButton("");
		b24 = new JButton("");
		b25 = new JButton("");
		b26 = new JButton("");
		b27 = new JButton("");
		b28 = new JButton("");
		b29 = new JButton("");
		b30 = new JButton("");
		b31 = new JButton("");
		b32= new JButton("");
		b33 = new JButton("");
		b34= new JButton("");
		b35 = new JButton("");
		b36 = new JButton("");
		
		JButton[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25,
				b26,b27,b28,b29,b30,b31,b32,b33,b34,b35,b36};
		for (JButton button : buttons) {
			button.setEnabled(false);
			button.setBackground(new Color(255, 255, 255));
			button.setFont(new Font("Pile Of Rock", Font.PLAIN, 50));
			button.setBorder(new LineBorder(new Color(217, 85, 181), 3, true));
			buttonColor(button);
			button.addActionListener(e -> btnActionPerformed(e, button));
			panel_gameBoard.add(button);
		}	
			
		Connect();
        currentSetting();
		initializeTimer();
	    loadMusic("tictactoe\\sounds\\bgm 2.wav");
	}
}
