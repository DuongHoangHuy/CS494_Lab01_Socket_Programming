package UI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONObject;

import Client.TCPClient;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.ImageIcon;

public class Register extends JFrame {
	private JPanel contentPane;
	private JTextField tfUsername;
	private JLabel lbRegisterStatus;
	public TCPClient tcpClient = null;
	public boolean isRegistered = false;
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Register frame = new Register();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public Register(TCPClient tcpClient) throws IOException {
		this.tcpClient = tcpClient; 
		setTitle("Registration");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 690, 277);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lbRacingArena = new JLabel("Racing Arena");
		lbRacingArena.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lbRacingArena.setHorizontalAlignment(SwingConstants.CENTER);
		lbRacingArena.setBounds(252, 11, 414, 33);
		contentPane.add(lbRacingArena);
		
		tfUsername = new JTextField();
		tfUsername.setFont(new Font("Tahoma", Font.PLAIN, 18));
		tfUsername.setBounds(252, 97, 248, 33);
		contentPane.add(tfUsername);
		tfUsername.setColumns(10);
		
		JLabel lbUsername = new JLabel("Username");
		lbUsername.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lbUsername.setBounds(252, 67, 120, 33);
		contentPane.add(lbUsername);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = tfUsername.getText();
				String regex = "[a-zaA-Z0-9_]+";
				String statusMsg = "";
				if (username.length() > 10) {
					statusMsg = "Your username is at most 10 characters!";
				}
				else if (!username.matches(regex)) {
					statusMsg = "Your username should contain only letters, digits and underscore!";
				} else {
					JSONObject req = new JSONObject();
			    	req.put("event", "SERVER_REGISTER");
			    	req.put("name", username);
					try {
						JSONObject res = tcpClient.sendRequest(req);
					    boolean resStatus = res.getBoolean("status");
					    boolean resIsFull = res.getBoolean("isFull");
					    if(!resStatus) {
					    	if(resIsFull) {
								statusMsg = "Room is full!";
					    	}else {
					    		statusMsg = "Duplicated name!";
					    	}
					    } else {
					    	isRegistered = true;
							statusMsg = "Registration Completed Successfully!";
					    }
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
				lbRegisterStatus.setText(statusMsg);
				if(isRegistered) {
					try {
						setVisible(false);
						WaitingRoom waitingRoom = new WaitingRoom(tcpClient);
						waitingRoom.setVisible(true);
						waitingRoom.run();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnRegister.setBounds(527, 97, 139, 32);
		contentPane.add(btnRegister);
		
//		JButton btnStartGame = new JButton("Start Game");
//		btnStartGame.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// switch to waiting screen, neu chua du nguoi
////				if (true) {
////					setVisible(false);
////					WaitingRoom waitScreen = new WaitingRoom();
////					waitScreen.setVisible(true);
////				}
//				// switch directly to game screen, neu da du nguoi roi
//				if (true) {
//					setVisible(false);
//					GamePlay gameScreen = new GamePlay();
//					gameScreen.setVisible(true);
//				}
//			}
//		});
//		btnStartGame.setFont(new Font("Tahoma", Font.PLAIN, 18));
//		btnStartGame.setBounds(391, 190, 146, 36);
//		contentPane.add(btnStartGame);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(Color.WHITE);
		sidePanel.setBounds(0, 0, 240, 240);
		contentPane.add(sidePanel);
		sidePanel.setLayout(null);
		
		JLabel lbRacingBg = new JLabel("");
		lbRacingBg.setIcon(new ImageIcon(Register.class.getResource("/images/racing-arena-bg.png")));
		lbRacingBg.setBounds(0, 0, 240, 240);
		lbRacingBg.setHorizontalAlignment(SwingConstants.LEFT);
		lbRacingBg.setVerticalAlignment(SwingConstants.TOP);
		sidePanel.add(lbRacingBg);
		
		lbRegisterStatus = new JLabel("");
		lbRegisterStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbRegisterStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lbRegisterStatus.setBounds(252, 147, 414, 33);
		contentPane.add(lbRegisterStatus);
		
	}
}
