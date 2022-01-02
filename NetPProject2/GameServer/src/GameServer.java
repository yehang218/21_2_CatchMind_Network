//JavaObjServer.java ObjectStream ��� ä�� Server

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class GameServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // ��������
	private Socket client_socket; // accept() ���� ������ client ����
	private Vector UserVec = new Vector(); // ����� ����ڸ� ������ ����
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����

	private String[] Q = {"������","�б�","��","������","������","�����е�","���"};
	private String[] Q_c= {"������","����","��","������","������","��������","����"};
	private String Qm = "";
	private int randomNum;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameServer frame = new GameServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GameServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
				txtPortNumber.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�
				
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
				
				
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// ���ο� ������ accept() �ϰ� user thread�� ���� �����Ѵ�.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
					AppendText("���ο� ������ from " + client_socket);
					// User �� �ϳ��� Thread ����
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // ���ο� ������ �迭�� �߰�
					new_user.start(); // ���� ��ü�� ������ ����
					AppendText("���� ������ �� " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	
	//Server�� �޼��� ���
	public void AppendText(String str) {
		// textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User �� �����Ǵ� Thread
	// Read One ���� ��� -> Write All
	class UserService extends Thread {
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		private String UserOrder = "";
		private int UserScore = 0;
		
		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// �Ű������� �Ѿ�� �ڷ� ����
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				AppendText("userService error");
			}
		}
		
		public void Login2() {
			String msg = "";
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				msg = msg + user.UserName + "\t";
			}
			ChatMsg obcm = new ChatMsg(UserName, "100", msg);
			WriteAllObject(obcm);
			
			}

		public void Login() {
			AppendText("���ο� ������ " + UserName + " ����.");
			WriteOne("Welcome to Java chat server\n");
			WriteOne(UserName + "�� ȯ���մϴ�.\n"); // ����� ����ڿ��� ���������� �˸�
			String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
			WriteOthers(msg); // ���� user_vc�� ���� ������ user�� ���Ե��� �ʾҴ�.

			if(UserVec.size()==1) {
				UserOrder = "O";
			}
		}
	

		public void Logout() {
			String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
			UserVec.removeElement(this); // Logout�� ���� ��ü�� ���Ϳ��� �����
			
			String msg2 = "";
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				msg2 = msg2 + user.UserName + "\t";
			}
			ChatMsg obcm = new ChatMsg(UserName, "101", msg2);
			WriteAllObject(obcm);
		
			WriteAll(msg); // ���� ������ �ٸ� User�鿡�� ����
			AppendText("����� " + "[" + UserName + "] ����. ���� ������ �� " + UserVec.size());
		}

		// ��� User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOne(str);
			}
		}
		// ��� User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
					user.WriteOneObject(ob);
			}
		}
		
		public void WriteOthersObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this)
					user.WriteOneObject(ob);
			}
		}

		// ���� ������ User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this)
					user.WriteOne(str);
			}
		}

		// UserService Thread�� ����ϴ� Client ���� 1:1 ����
		public void WriteOne(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����

			}
		}
		
		//�� �������� Ȯ��
		public void startSend_O() {
			try {
			ChatMsg obcm = new ChatMsg(UserName, "400", "Your Turn");
			oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		
		public void startSend() {
			ChatMsg obcm = new ChatMsg(UserName, "401", "start game");
			WriteOthersObject(obcm);
		}
		
		public void EndSend(String str) {
			ChatMsg obcm = new ChatMsg(UserName,"700",str);
			WriteAllObject(obcm);
		}
		
		//���� ������ ������ �� - code = 201
		public void SendCorrect(String str) {
			try {
			UserScore = UserScore+50;
			str = str + "\t";
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				str = str + user.UserScore + "\t";
			}
			
			ChatMsg obcm = new ChatMsg(UserName, "201", str);
			obcm.score = obcm.score+50;
			
			if(Qm.matches(Q[0])) obcm.img = new ImageIcon(".\\cat.jpg");
			else if(Qm.matches(Q[1])) obcm.img = new ImageIcon(".\\school.png");
			else if(Qm.matches(Q[2])) obcm.img = new ImageIcon(".\\horse.jpg");
			else if(Qm.matches(Q[3])) obcm.img = new ImageIcon(".\\puppy.jpg");
			else if(Qm.matches(Q[4])) obcm.img = new ImageIcon(".\\fish.jpg");
			else if(Qm.matches(Q[5])) obcm.img = new ImageIcon(".\\ipad.jpg");
			else if(Qm.matches(Q[6])) obcm.img = new ImageIcon(".\\cottonswab.jpg");
			
			oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		
		//�����ڵ鿡�� �߼� - code = 202
		public void SendCorrect_Other(String str) {
			str = str + "\t";
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				str = str + user.UserScore + "\t";
			}
			ChatMsg obcm = new ChatMsg(UserName, "202", str);

			if(Qm.matches(Q[0])) obcm.img = new ImageIcon(".\\cat.jpg");
			else if(Qm.matches(Q[1])) obcm.img = new ImageIcon(".\\school.png");
			else if(Qm.matches(Q[2])) obcm.img = new ImageIcon(".\\horse.jpg");
			else if(Qm.matches(Q[3])) obcm.img = new ImageIcon(".\\puppy.jpg");
			else if(Qm.matches(Q[4])) obcm.img = new ImageIcon(".\\fish.jpg");
			else if(Qm.matches(Q[5])) obcm.img = new ImageIcon(".\\ipad.jpg");
			else if(Qm.matches(Q[6])) obcm.img = new ImageIcon(".\\cottonswab.jpg");
			WriteOthersObject(obcm);
		}

		//���� ������ �����.
		public String MakeQuestion() {
			String QMessage = "";
			randomNum = 0;
			
			Random random = new Random();
			randomNum = random.nextInt(7);
			QMessage = Q[randomNum];
			
			return QMessage;
		}
		
		//���� ���� - code = 300
		public void SendQuestion(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("����", "300", msg);
				if(Qm.matches(Q[0])) obcm.img = new ImageIcon(".\\cat.jpg");
				else if(Qm.matches(Q[1])) obcm.img = new ImageIcon(".\\school.png");
				else if(Qm.matches(Q[2])) obcm.img = new ImageIcon(".\\horse.jpg");
				else if(Qm.matches(Q[3])) obcm.img = new ImageIcon(".\\puppy.jpg");
				else if(Qm.matches(Q[4])) obcm.img = new ImageIcon(".\\fish.jpg");
				else if(Qm.matches(Q[5])) obcm.img = new ImageIcon(".\\ipad.jpg");
				else if(Qm.matches(Q[6])) obcm.img = new ImageIcon(".\\cottonswab.jpg");
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		
		public void SendHint() {
			try {
				ChatMsg obcm = new ChatMsg("��Ʈ", "600", "");
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		
		public void SendHintWord(String str) {
			try {
				ChatMsg obcm = new ChatMsg("��Ʈ", "601", str);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		
		public void SendHintCon(String str) {
			try {
				ChatMsg obcm = new ChatMsg("��Ʈ", "602", str);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		
		public void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();

			}
		}
		
		
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;

					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						AppendObject(cm);
					} else
						continue;
					if (cm.code.matches("100")) { //Login
						UserName = cm.UserName;
						UserScore = cm.score;
						Login2();
						//ul.enterUser(cm);
						cm.UserNum = UserVec.size();
						Login();
						if(UserOrder == "O")
							startSend_O();
					} else if(cm.code.matches("101")) {
						// logout message ó��
						cm.UserNum = UserVec.size();
						Logout();
						break;
					}
					
					else if (cm.code.matches("200")) { // ä�� �޼���
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						AppendText(msg); // server ȭ�鿡 ���
						String[] args = msg.split(" "); // �ܾ���� �и��Ѵ�.
						// �Ϲ� ä�� �޽���
						//WriteAll(msg + "\n"); // Write All
						WriteAllObject(cm);
						
						//���� ä�� �޼����� �����̶��
						if(cm.data.matches(Qm)) {
							msg = cm.UserName + "���� ������ ���߼̽��ϴ�. ���� ���ʴ� "+ UserName +"���Դϴ�.\n";
							WriteAll(msg);
							msg = String.format("[%s] ����", cm.UserName);
							SendCorrect(Qm);
							UserOrder = "O";
							
							SendCorrect_Other(Qm);	//code - 202
							Qm = ""; //�ߺ� ���� ����
							if(UserOrder=="O") {
								startSend_O(); //code -400
							}
						}
					}
					
					// �����ϱ� ��ư ������ -> �������� ���� ����
					else if(cm.code.matches("400")) {
						Qm = MakeQuestion();
						
						//������ ���
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						msg = String.format("���� - [%s] : %s",cm.UserName, Qm);
						AppendText(msg);
						
						SendQuestion(Qm);
						
						// �ٸ� ����鿡�� �˸���
						msg = "������ �����մϴ�.";
						startSend();
						WriteOthers(msg);
						
						UserOrder = "X";
					} 
					else if(cm.code.matches("601")) {
						msg = "" + Qm.length();
						SendHintWord(msg);
					}
					else if(cm.code.matches("602")) {
						msg = "" + Q_c[randomNum];
						SendHintCon(msg);
					}
					
					else if(cm.code.matches("700")) {
						EndSend(cm.UserName);
						msg = cm.UserName+"�� ���ϵ帳�ϴ�!";
						WriteAll(msg);
					}
					else { // 500, ... ��Ÿ object�� ��� ����Ѵ�.
						WriteAllObject(cm);
					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����
			} // while
		} // run
	}

}