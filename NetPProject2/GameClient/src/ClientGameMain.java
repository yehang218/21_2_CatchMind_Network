import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.Color;

public class ClientGameMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUserName;
	private JTextField txtIpAddress;
	private JTextField txtPortNumber;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGameMain frame = new ClientGameMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	
	public ClientGameMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100,100,254,400);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//UserName
		JLabel lblNewLabel = new JLabel("User Name");
		lblNewLabel.setBounds(12, 158, 82, 33);
		contentPane.add(lblNewLabel);
		
		txtUserName = new JTextField();
		txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
		txtUserName.setBounds(110, 158, 116, 33);
		contentPane.add(txtUserName);
		txtUserName.setColumns(10);
		
		//Ip Address
		JLabel lblIpAddress = new JLabel("IP Address");
		lblIpAddress.setBounds(12, 201, 82, 33);
		contentPane.add(lblIpAddress);
		
		txtIpAddress = new JTextField();
		txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
		txtIpAddress.setText("127.0.0.1");
		txtIpAddress.setColumns(10);
		txtIpAddress.setBounds(110, 201, 116, 33);
		contentPane.add(txtIpAddress);
		
		//Port Number
		JLabel lblPortNumber = new JLabel("Port Number");
		lblPortNumber.setBounds(12, 244, 82, 33);
		contentPane.add(lblPortNumber);
		
		txtPortNumber = new JTextField();
		txtPortNumber.setText("30000");
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setColumns(10);
		txtPortNumber.setBounds(110, 244, 116, 33);
		contentPane.add(txtPortNumber);
		
		//Connect Button
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBounds(12, 300, 214, 38);
		contentPane.add(btnConnect);
		

		ImageIcon imgLogo = new ImageIcon(".\\Logo.png");
		imgLogo = imageSetSize(imgLogo,214,121);
		JLabel lblLogo = new JLabel("",imgLogo,JLabel.CENTER);
		lblLogo.setBounds(12, 10, 214, 121);
		contentPane.add(lblLogo);
		Myaction action = new Myaction();
		btnConnect.addActionListener(action);
		txtUserName.addActionListener(action);
		txtIpAddress.addActionListener(action);
		txtPortNumber.addActionListener(action);
	}
	
	class Myaction implements ActionListener // ???????????? ???? ?????? ???? ??????
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = txtUserName.getText().trim();
			String ip_addr = txtIpAddress.getText().trim();
			String port_no = txtPortNumber.getText().trim();
			ClientGameView view = new ClientGameView(username, ip_addr, port_no);
			setVisible(false);
		}
	}
	
	ImageIcon imageSetSize(ImageIcon icon, int i, int j) {
		Image ximg = icon.getImage();
		Image yimg = ximg.getScaledInstance(i, j, java.awt.Image.SCALE_SMOOTH);
		ImageIcon xyimg = new ImageIcon(yimg);
		return xyimg;
	}
}
