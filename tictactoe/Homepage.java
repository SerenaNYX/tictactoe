package tictactoe;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
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
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

public class Homepage extends JFrame {

	private JPanel contentPane;
	private Connection con;
	private PreparedStatement stmt;
	private ResultSet result;
	private String sqlQuery;
	private boolean isMuted;
	private Clip clip;
	private int pausePosition = 0;
	private String mode;

	public void Connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/tictactoe", "root", "");
			System.out.println("Connect!");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Homepage.class.getName()).log(Level.SEVERE, "JDBC Driver not found", ex);
		} catch (SQLException ex) {
            Logger.getLogger(Homepage.class.getName()).log(Level.SEVERE, "SQL Exception occurred", ex);
        }
	}

	private String showMode() {
		mode = null;
		
		try {
			Connect();
			sqlQuery = "SELECT gamemode, board FROM settings";
			stmt = con.prepareStatement(sqlQuery);
			result = stmt.executeQuery();

			if(result.next()) {
				String gamemode = result.getString("gamemode");
				String board = result.getString("board");
				mode = "Current gamemode: " + gamemode + " " + board;
			}
			
		} catch (SQLException ex) {
			Logger.getLogger(Homepage.class.getName()).log(Level.SEVERE, "SQL Exception occurred", ex);
		}

		return mode;
	}

	private void btnPlayActionPerformed(ActionEvent evt) {
		try {
			Connect();
			sqlQuery = "SELECT gamemode, board FROM settings";
			stmt = con.prepareStatement(sqlQuery);
			result = stmt.executeQuery();

			if (result.next()) {
				String gamemode = result.getString("gamemode");
				String board = result.getString("board");

				if ("Singleplayer".equals(gamemode)) {
					if (board != null) {
						switch (board) {
							case "3x3" -> {
								BotBoard3by3 single33 = new BotBoard3by3();
								this.setVisible(false);
								single33.setVisible(true);
							}
	                        case "5x5" -> {
	                            BotBoard5by5 single55 = new BotBoard5by5();
	                            this.setVisible(false);
	                            single55.setVisible(true);
	                        }
							case "6x6" -> {
								BotBoard6by6 single66 = new BotBoard6by6();
								this.setVisible(false);
	                        	single66.setVisible(true);
							}
							default -> {
							}
						}
					}
				} else if ("Multiplayer".equals(gamemode)) {
					if (board != null) {
						switch (board) {
							case "3x3" -> {
								Board3by3 multi33 = new Board3by3();
								this.setVisible(false);
								multi33.setVisible(true);
							}
	                        case "5x5" -> {
	                            Board5by5 multi55 = new Board5by5();
	                            this.setVisible(false);
	                            multi55.setVisible(true);
	                        }
							case "6x6" -> {
								Board6by6 multi66 = new Board6by6();
								this.setVisible(false);
	                            multi66.setVisible(true);
							}
							default -> {
							}
						}
					}
				}
			}
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void btnSettingsActionPerformed(ActionEvent evt) {
		SettingsPage set = new SettingsPage();
		this.setVisible(false);
		set.setVisible(true);
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
						label.setBorder(new LineBorder(newColor, 6, true));
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

	private static void containerColor(Container container) {
        Runnable colorChanger = new Runnable() {
			private boolean isSwitch = true;
			@Override
			public void run() {
				while(true) {
					try {
						Color newColor = isSwitch ? new Color(150, 170, 217) : new Color(250, 227, 104);
						container.setBackground(newColor);						
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

	/**
	 * Launch the application.
     * @param args
	 */
	public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new Homepage(); // Create an instance which initializes and shows the frame
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

	public Homepage() {
        // Create and set up the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 700);
		setUndecorated(true);

        // Create the content pane
        contentPane = new JPanel();
        contentPane.setBackground(new Color(150, 170, 217));
        contentPane.setLayout(null);
		contentPane.setBorder(new LineBorder(new Color(255, 255, 255), 10, true));
		containerColor(contentPane);

        // Set the content pane to the frame
        setContentPane(contentPane);

		// TERMINATE PROGRAM button
		JButton btnClose = new JButton("Exit Game");
		btnClose.setBackground(new Color(217, 85, 181));
		btnClose.setForeground(Color.BLACK);
		btnClose.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnClose.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
		btnClose.setBounds(100, 470, 500, 60);
		btnClose.setFocusPainted(false);
		btnClose.addActionListener((ActionEvent evt) -> {
            System.exit(0);
        });
		contentPane.add(btnClose);
		
        // PLAY button
        JButton btnPlay = new JButton("Play");
        btnPlay.setBackground(new Color(255, 255, 255));
        btnPlay.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
        btnPlay.setBounds(100, 270, 500, 60);
		btnPlay.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnPlay.setFocusPainted(false);
		btnPlay.addActionListener((ActionEvent e) -> {
			btnPlayActionPerformed(e);
			try {
				clip.stop();
			} catch (Exception r) {
				r.printStackTrace();
			}
		});
        contentPane.add(btnPlay);

        // SETTINGS button
        JButton btnSettings = new JButton("Settings");
        btnSettings.setBackground(new Color(255, 255, 255));
        btnSettings.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
        btnSettings.setBounds(100, 370, 500, 60);
		btnSettings.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnSettings.setFocusPainted(false);
		btnSettings.addActionListener((ActionEvent e) -> {
			btnSettingsActionPerformed(e);
			try {
				clip.stop();
			} catch (Exception r) {
				r.printStackTrace();
			}
		});
        contentPane.add(btnSettings);

        // Main Title (TIC TAC TOE)
        JLabel tttLabel = new JLabel("TIC TAC TOE   ");
        tttLabel.setFont(new Font("Pile Of Rock", Font.PLAIN, 60));
		tttLabel.setHorizontalAlignment(JLabel.RIGHT);
        tttLabel.setBounds(100, 100, 500, 130);
        contentPane.add(tttLabel);
		colorChanging(tttLabel);

		JLabel tttBackground = new JLabel("");
		tttBackground.setBackground(new Color(145, 99, 191));
		tttBackground.setBounds(100, 100, 500, 130);
		tttBackground.setOpaque(true);
		contentPane.add(tttBackground);

		// Tic Tac Toe IMAGE
		JLabel lblTTT = new JLabel("");
	    BufferedImage tttImage = null;
        try {
            tttImage = ImageIO.read(new File("tictactoe\\images\\board.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image resizedTTT = tttImage.getScaledInstance(90, 90, Image.SCALE_SMOOTH); // Resize the image to the desired dimensions
        ImageIcon tttIcon = new ImageIcon(resizedTTT); // Create a new ImageIcon with the resized image
        
        lblTTT.setIcon(tttIcon);
        lblTTT.setBounds(160, 110, 100, 100);
		lblTTT.setOpaque(false);
	    contentPane.add(lblTTT);
		contentPane.setComponentZOrder(lblTTT, 0);
		contentPane.setComponentZOrder(tttLabel, 1);
		contentPane.setComponentZOrder(tttBackground, 2);

		// Print CURRENT MODE
		JLabel currentMode = new JLabel(showMode());
		currentMode.setForeground(Color.BLACK);
		currentMode.setFont(new Font("Pile Of Rock", Font.PLAIN, 20));
		currentMode.setHorizontalAlignment(JLabel.CENTER);
		currentMode.setBounds(100, 185, 500, 130);
		contentPane.add(currentMode);

		JLabel authoresult = new JLabel("================= Designed by: Group 14 =================");
		authoresult.setForeground(Color.BLACK);
		authoresult.setFont(new Font("Pile Of Rock", Font.PLAIN, 20));
		authoresult.setHorizontalAlignment(JLabel.CENTER);
		authoresult.setBounds(0, 600, 700, 30);
		contentPane.add(authoresult);
      
		// MUSIC toggle button
		JToggleButton tglMusic = new JToggleButton("");

        BufferedImage soundoffImage = null; // inserting soundoff icon
        BufferedImage soundImage = null; // inserting soundon icon

        try {
            soundoffImage = ImageIO.read(new File("tictactoe\\images\\sounds off.png"));
            soundImage = ImageIO.read(new File("tictactoe\\images\\sounds on.png"));
        } catch (IOException r) {
            r.printStackTrace();
        }

        // Resize the image to the desired dimensions
        Image resizedSoundOff = soundoffImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        Image resizedSound = soundImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);

        // Create a new ImageIcon with the resized image
        ImageIcon soundOffIcon = new ImageIcon(resizedSoundOff);
        ImageIcon soundIcon = new ImageIcon(resizedSound);

        tglMusic.setIcon(soundIcon);
        tglMusic.addMouseListener(new MouseAdapter() { // action when toggle is clicked
            @Override
            public void mouseClicked(MouseEvent e) {
                // Toggle the mute state
                isMuted = !isMuted;
                if (isMuted) {
                    tglMusic.setIcon(soundOffIcon);
                    if (clip != null && clip.isRunning()) {
						pausePosition = clip.getFramePosition(); // Save the current position
						clip.stop();
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

      	loadMusic("tictactoe\\sounds\\bgm 1.wav");
        setVisible(true); // Show the frame
		setLocationRelativeTo(null);
    }
}
