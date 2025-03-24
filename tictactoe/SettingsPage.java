package tictactoe;

import java.awt.Color;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;


public class SettingsPage extends JFrame {

	private JPanel contentPane;
	private Connection con;
	private PreparedStatement stmt;
	private ResultSet result;
	private String sqlQuery;
	private Clip clip;
	private boolean isMuted;
	private int pausePosition = 0;
	private DefaultListCellRenderer listRenderer;
	@SuppressWarnings("rawtypes")
	private JComboBox cBoxGameMode, cBoxSetBoard;
	private JCheckBox checkBoxMatchTimer, checkBoxBoardInfo, checkBoxScoreInfo;
	
	public void Connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/tictactoe", "root", "");
			System.out.println("Connect!");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(SettingsPage.class.getName()).log(Level.SEVERE, "JDBC Driver not found", ex);
		} catch (SQLException ex) {
            Logger.getLogger(SettingsPage.class.getName()).log(Level.SEVERE, "SQL Exception occurred", ex);
        }
	}

	private void currentSetting(){
        try {
            sqlQuery = "SELECT gamemode, board, match_timer, board_info, score FROM settings"; // SQL query to retrieve data from the db table
            stmt = con.prepareStatement(sqlQuery); // Prepare SQL statement
            result = stmt.executeQuery(); // Execute the query
			
            // Retrieve value from db
            if (result.next()) {
                String gamemode = result.getString("gamemode");
                String board = result.getString("board");
                boolean match_timer = result.getBoolean("match_timer");
                boolean board_info = result.getBoolean("board_info");
                boolean score = result.getBoolean("score");
                // Set the selected item of the combo box to match the retrieved value
                cBoxGameMode.setSelectedItem(gamemode);
                cBoxSetBoard.setSelectedItem(board);
                checkBoxMatchTimer.setSelected(match_timer);
                checkBoxBoardInfo.setSelected(board_info);
                checkBoxScoreInfo.setSelected(score);
            }
            // Close the resultset, statement
            result.close();
            stmt.close();
        } catch (SQLException e) {
            Logger.getLogger(SettingsPage.class.getName()).log(Level.SEVERE, null, e); // Handle any potential SQL error result here
        }
    }

	private void updateSettings() {
		try {
			String Gamemode = (String) cBoxGameMode.getSelectedItem();
            String setBoard = (String) cBoxSetBoard.getSelectedItem();
			boolean matchTimerOn = checkBoxMatchTimer.isSelected();
			boolean boardInfoOn = checkBoxBoardInfo.isSelected();
			boolean scoreInfoOn = checkBoxScoreInfo.isSelected();

            String query = "UPDATE settings SET gamemode=?, board=?, match_timer=?, board_info=?, score=?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, Gamemode);
			stmt.setString(2, setBoard);
			stmt.setBoolean(3, matchTimerOn);
			stmt.setBoolean(4, boardInfoOn);
			stmt.setBoolean(5, scoreInfoOn);
            stmt.executeUpdate();
		} catch (SQLException ex) {
			Logger.getLogger(SettingsPage.class.getName()).log(Level.SEVERE, null, ex);
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

	private static void colorChanging2(JLabel label) {
		Runnable colorChanger = new Runnable() {
			private boolean isSwitch = true;
			@Override
			public void run() {
				while(true) {
					try {
						Color newColor = isSwitch ? new Color(0,0,0) : new Color(80, 35, 102);
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

	/**
	 * Launch the application.
     * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
            try {
                new SettingsPage(); // Create an instance which initializes and shows the frame
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SettingsPage() {

		contentPane = new JPanel();
		contentPane.setBackground(new Color(154, 170, 217));
		contentPane.setLayout(null);
		contentPane.setBorder(new LineBorder(new Color(255, 255, 255), 10, true));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		setContentPane(contentPane);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setVisible(true);

		JButton btnMainMenu = new JButton("RETURN");
		btnMainMenu.addActionListener((ActionEvent e) -> {
			updateSettings();
			mainMenu();
		});
		btnMainMenu.setFont(new Font("Pile Of Rock", Font.PLAIN, 20));
		btnMainMenu.setBackground(new Color(217, 85, 181));
		btnMainMenu.setForeground(Color.BLACK);
		btnMainMenu.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnMainMenu.setBounds(25, 30, 100, 35);
		btnMainMenu.setFocusable(false);
		contentPane.add(btnMainMenu);

		BufferedImage originalImage = null;
	    try {
            originalImage = ImageIO.read(new File("tictactoe\\images\\settings.png"));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	   
        Image resizedImage = originalImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Resize image
	    ImageIcon settingsIcon = new ImageIcon(resizedImage); // Create ImageIcon with resized image

		JLabel lblSettings = new JLabel("SETTINGS");
		lblSettings.setFont(new Font("Pile Of Rock", Font.PLAIN, 55));
		lblSettings.setBounds(150, 30, 400, 65);
		lblSettings.setForeground(Color.BLACK);
		lblSettings.setBackground(new Color(145, 99, 191));
		lblSettings.setOpaque(true);
		lblSettings.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lblSettings.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(lblSettings);
		colorChanging(lblSettings);
		
		// GENERAL
		JLabel lblGeneral = new JLabel(" GENERAL");
		lblGeneral.setFont(new Font("Pile Of Rock", Font.PLAIN, 32));
		lblGeneral.setForeground(Color.BLACK);
		lblGeneral.setBounds(60, 115, 219, 35);
		lblGeneral.setIcon(settingsIcon);
		contentPane.add(lblGeneral);

		// Game mode panel
		JPanel panelGameMode = new JPanel();
		panelGameMode.setBackground(new Color(155, 218, 242));
		panelGameMode.setBounds(9, 149, 682, 68);
		panelGameMode.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelGameMode.setLayout(null);
		contentPane.add(panelGameMode);

		JLabel lblGameMode = new JLabel("GAME MODE");
		lblGameMode.setBounds(130, 23, 200, 30);
		lblGameMode.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
		lblGameMode.setForeground(Color.BLACK);
		panelGameMode.add(lblGameMode);
		
		cBoxGameMode = new JComboBox();
		cBoxGameMode.addItem("Singleplayer");
		cBoxGameMode.addItem("Multiplayer");
		cBoxGameMode.setBounds(424, 18, 209, 35);
		cBoxGameMode.setFont(new Font("Pile Of Rock", Font.PLAIN, 25));
		cBoxGameMode.setBackground(new Color(255, 222, 223));
		cBoxGameMode.setForeground(Color.BLACK);
		cBoxGameMode.setBorder(new LineBorder(Color.WHITE, 2, true));
		listRenderer = new DefaultListCellRenderer();
      	listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
      	cBoxGameMode.setRenderer(listRenderer);
		cBoxGameMode.setFocusable(false);
		panelGameMode.add(cBoxGameMode);
		
		// Set board panel
		JPanel panelSetBoard = new JPanel();
		panelSetBoard.setBackground(new Color(155, 218, 242));
		panelSetBoard.setBounds(9, 227, 682, 68);
		panelSetBoard.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(panelSetBoard);
		panelSetBoard.setLayout(null);
		
		JLabel lblSetBoard = new JLabel("SET BOARD");
		lblSetBoard.setBounds(130, 23, 200, 30);
		lblSetBoard.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
		lblSetBoard.setForeground(Color.BLACK);
		panelSetBoard.add(lblSetBoard);
		
		cBoxSetBoard = new JComboBox();
		cBoxSetBoard.addItem("3x3");
		cBoxSetBoard.addItem("5x5");
		cBoxSetBoard.addItem("6x6");
		cBoxSetBoard.setBounds(424, 18, 209, 35);
		cBoxSetBoard.setFont(new Font("Pile Of Rock", Font.PLAIN, 25));
		cBoxSetBoard.setBackground(new Color(255, 222, 223));
		cBoxSetBoard.setForeground(Color.BLACK);
		cBoxSetBoard.setBorder(new LineBorder(Color.WHITE, 2, true));
      	listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
      	cBoxSetBoard.setRenderer(listRenderer);
		cBoxSetBoard.setFocusable(false);
		panelSetBoard.add(cBoxSetBoard);
		
		// MATCH INFO
		JLabel lblMatchInfo = new JLabel(" MATCH INFO");
		lblMatchInfo.setFont(new Font("Pile Of Rock", Font.PLAIN, 32));
		lblMatchInfo.setForeground(Color.BLACK);
		lblMatchInfo.setBounds(60, 315, 219, 35);
		lblMatchInfo.setIcon(settingsIcon);
		contentPane.add(lblMatchInfo);
		
		// Match timer panel
		JPanel panelMatchTimer = new JPanel();
		panelMatchTimer.setBackground(new Color(155, 218, 242));
		panelMatchTimer.setBounds(9, 350, 682, 68);
		panelMatchTimer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(panelMatchTimer);
		panelMatchTimer.setLayout(null);
		
		JLabel lblMatchTimer = new JLabel("MATCH TIMER");
		lblMatchTimer.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
		lblMatchTimer.setForeground(Color.BLACK);
		lblMatchTimer.setBounds(130, 23, 200, 30);
		panelMatchTimer.add(lblMatchTimer);
		
		checkBoxMatchTimer = new JCheckBox("ON");
		checkBoxMatchTimer.setFont(new Font("Pile Of Rock", Font.PLAIN, 25));
		checkBoxMatchTimer.setForeground(Color.BLACK);
		checkBoxMatchTimer.setBounds(490, 27, 173, 21);
		checkBoxMatchTimer.setOpaque(false);
		checkBoxMatchTimer.setFocusable(false);
		panelMatchTimer.add(checkBoxMatchTimer);
		
		// Board info panel
		JPanel panelBoardInfo = new JPanel();
		panelBoardInfo.setBackground(new Color(155, 218, 242));
		panelBoardInfo.setBounds(9, 428, 682, 68);
		panelBoardInfo.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(panelBoardInfo);
		panelBoardInfo.setLayout(null);
		
		JLabel lblBoardInfo = new JLabel("BOARD INFO");
		lblBoardInfo.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
		lblBoardInfo.setForeground(Color.BLACK);
		lblBoardInfo.setBounds(130, 23, 200, 30);
		panelBoardInfo.add(lblBoardInfo);
		
		checkBoxBoardInfo = new JCheckBox("ON");
		checkBoxBoardInfo.setFont(new Font("Pile Of Rock", Font.PLAIN, 25));
		checkBoxBoardInfo.setForeground(Color.BLACK);
		checkBoxBoardInfo.setBounds(490, 28, 173, 21);
		checkBoxBoardInfo.setOpaque(false);
		checkBoxBoardInfo.setFocusable(false);
		panelBoardInfo.add(checkBoxBoardInfo);
		
		// Score info panel
		JPanel panelScoreInfo = new JPanel();
		panelScoreInfo.setBackground(new Color(155, 218, 242));
		panelScoreInfo.setBounds(9, 506, 682, 68);
		panelScoreInfo.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(panelScoreInfo);
		panelScoreInfo.setLayout(null);
		
		JLabel lblScoreInfo = new JLabel("SCORE INFO");
		lblScoreInfo.setFont(new Font("Pile Of Rock", Font.PLAIN, 30));
		lblScoreInfo.setForeground(Color.BLACK);
		lblScoreInfo.setBounds(130, 23, 200, 30);
		panelScoreInfo.add(lblScoreInfo);
		
		checkBoxScoreInfo = new JCheckBox("ON");
		checkBoxScoreInfo.setFont(new Font("Pile Of Rock", Font.PLAIN, 25));
		checkBoxScoreInfo.setForeground(Color.BLACK);
		checkBoxScoreInfo.setBounds(490, 28, 173, 21);
		checkBoxScoreInfo.setOpaque(false);
		checkBoxScoreInfo.setFocusable(false);
		panelScoreInfo.add(checkBoxScoreInfo);

		colorChanging2(lblGameMode);
		colorChanging2(lblSetBoard);
		colorChanging2(lblMatchTimer);
		colorChanging2(lblBoardInfo);
		colorChanging2(lblScoreInfo);
	
		JButton btnReset = new JButton("RESET TO DEFAULT");
		btnReset.setBackground(new Color(217, 85, 181));
		btnReset.setFont(new Font("Pile Of Rock", Font.PLAIN, 28));
		btnReset.setForeground(Color.BLACK);
		btnReset.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btnReset.setBounds(225, 600, 250, 51);
		btnReset.setFocusable(false);
		btnReset.addActionListener((ActionEvent e) -> {
            cBoxGameMode.setSelectedItem("Singleplayer");
			cBoxSetBoard.setSelectedItem("3x3");
			checkBoxMatchTimer.setSelected(true);
			checkBoxBoardInfo.setSelected(true);
			checkBoxScoreInfo.setSelected(true);
        });
		contentPane.add(btnReset);

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

		Connect();
        currentSetting();
		loadMusic("tictactoe\\sounds\\bgm 3.wav");	
	}
}

