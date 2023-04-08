package UI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;

import Client.TCPClient;

import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class GamePlay extends JFrame {
	private JPanel contentPane;
    private JTable leaderboardTable;
    private JPanel leaderboardPanel = null;
    private DefaultTableModel leaderboardModel = null;
    private JLabel leaderboardLabel = null;
    
    private JTextField tfAnswer;
    private JPanel panel = null;
    private JPanel pTimer = null;
    private JLabel lbTimer = null;
    private JButton btnSubmit = null;

    private JLabel lblNewLabel_1 = null;
    private JTextArea txtrQuestionProveThat = null;
    
    private Timer timer = null;
    private Integer timeLeft = null;
    private TCPClient tcpClient = null;
	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public GamePlay(TCPClient tcpClient) {
		this.tcpClient = tcpClient;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Create leaderboard panel
		leaderboardPanel = new JPanel(new BorderLayout());
		leaderboardPanel.setBounds(542, 0, 244, 443);
		leaderboardPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 2), new EmptyBorder(5, 5, 5, 5)));
		leaderboardPanel.setBackground(new Color(255, 255, 200));

		// Create leaderboard label
		leaderboardLabel = new JLabel("Leaderboard");
		leaderboardLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		leaderboardLabel.setHorizontalAlignment(JLabel.CENTER);
		leaderboardPanel.add(leaderboardLabel, BorderLayout.NORTH);

		// Create leaderboard table with three columns
		leaderboardModel = new DefaultTableModel(new Object[][]{}, new String[]{"Rank", "Username", "Score"});
		leaderboardTable = new JTable(leaderboardModel);
		leaderboardTable.setGridColor(Color.WHITE);
		leaderboardTable.setShowGrid(false);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		leaderboardTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		leaderboardTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		leaderboardTable.setBackground(new Color(255, 255, 200));
		leaderboardTable.getTableHeader().setBackground(new Color(200, 200, 255));
		leaderboardTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		leaderboardTable.getTableHeader().setOpaque(false);
		leaderboardPanel.add(leaderboardTable, BorderLayout.CENTER);

		contentPane.setLayout(null);
		contentPane.add(leaderboardPanel);
		setContentPane(contentPane);
		
		// game panel
		panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBounds(0, 0, 542, 443);
		contentPane.add(panel);
		panel.setLayout(null);
		
		pTimer = new JPanel();
		pTimer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pTimer.setBounds(391, 10, 141, 53);
		panel.add(pTimer);
		pTimer.setLayout(null);
		
		lbTimer = new JLabel("Time left: ");
		lbTimer.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbTimer.setHorizontalAlignment(SwingConstants.CENTER);
		lbTimer.setBounds(13, 10, 118, 38);
		pTimer.add(lbTimer);
		
		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(161, 88, 196, 53);
		panel.add(lblNewLabel_1);
		
		tfAnswer = new JTextField();
		tfAnswer.setFont(new Font("Tahoma", Font.PLAIN, 18));
		tfAnswer.setBounds(86, 339, 351, 34);
		panel.add(tfAnswer);
		tfAnswer.setColumns(10);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				if (true) {
//					tfAnswer.setText("You are disqualified!");
//					tfAnswer.setEnabled(false);
//					btnSubmit.setEnabled(false);
//				}
				String answer = tfAnswer.getText();
				JSONObject resJson = new JSONObject();
				resJson.put("event", "SERVER_ANSWER");
				resJson.put("answer", answer);

				try {
					tcpClient.sendRequest(resJson);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		btnSubmit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnSubmit.setBounds(341, 383, 96, 34);
		panel.add(btnSubmit);
		
		txtrQuestionProveThat = new JTextArea();
		txtrQuestionProveThat.setEditable(false);
		txtrQuestionProveThat.setFont(new Font("Monospaced", Font.PLAIN, 18));
		txtrQuestionProveThat.setWrapStyleWord(true);
		txtrQuestionProveThat.setLineWrap(true);
		txtrQuestionProveThat.setBounds(86, 151, 351, 157);
		panel.add(txtrQuestionProveThat);
	}
	
	public void run() {
		newTask.start();
	}
	
	Thread newTask = new Thread() {
		public void run() {
			int round = 3;
			while(round > 0) {
				--round;
				timeLeft = 5;
				
				try {
					String resRoundStart = tcpClient.receive().toString();
					System.out.println("game play" + resRoundStart);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				Object[][] data = {
						{"#1", "Player1", 100},
						{"#2", "Player2", 75},
						{"#3", "Player3", 50},
						{"#4", "Player4", 25},
						{"#5", "Player5", 10}
				};
				for (Object[] row : data) {
					leaderboardModel.addRow(row);
				}
				
		        lbTimer.setText("Time left: " + timeLeft);
				timer = new Timer(1000, e -> {
			        timeLeft--;
			        lbTimer.setText("Time left: " + timeLeft);
			        if (timeLeft <= 0) {
			            timer.stop();
			        } else if (timeLeft <= 10) {
			        	lbTimer.setForeground(Color.RED);
			        }
			    });
			    timer.start();
			    
			    try {
					Thread.sleep(timeLeft*1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    
			    System.out.println("End timer");
			    
		//	    lblNewLabel_1.setText();
			    
				txtrQuestionProveThat.setText("QUESTION: ");
				
				try {
					String resRoundEnd = tcpClient.receive().toString();
					System.out.println("game play" + resRoundEnd);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
	};
}