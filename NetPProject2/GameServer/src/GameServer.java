//JavaObjServer.java ObjectStream 기반 채팅 Server

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

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

	private String[] Q = {"고양이","학교","말","강아지","물고기","아이패드","면봉"};
	private String[] Q_c= {"ㄱㅇㅇ","ㅎㄱ","ㅁ","ㄱㅇㅈ","ㅁㄱㄱ","ㅇㅇㅍㄷ","ㅁㅂ"};
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
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
				
				
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	
	//Server에 메세지 출력
	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
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
			// 매개변수로 넘어온 자료 저장
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
			AppendText("새로운 참가자 " + UserName + " 입장.");
			WriteOne("Welcome to Java chat server\n");
			WriteOne(UserName + "님 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림
			String msg = "[" + UserName + "]님이 입장 하였습니다.\n";
			WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.

			if(UserVec.size()==1) {
				UserOrder = "O";
			}
		}
	

		public void Logout() {
			String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			
			String msg2 = "";
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				msg2 = msg2 + user.UserName + "\t";
			}
			ChatMsg obcm = new ChatMsg(UserName, "101", msg2);
			WriteAllObject(obcm);
		
			WriteAll(msg); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOne(str);
			}
		}
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
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

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this)
					user.WriteOne(str);
			}
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다

			}
		}
		
		//내 차례임을 확인
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
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
		
		//내가 정답을 맞췄을 때 - code = 201
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		//오답자들에게 발송 - code = 202
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

		//랜덤 문제를 만든다.
		public String MakeQuestion() {
			String QMessage = "";
			randomNum = 0;
			
			Random random = new Random();
			randomNum = random.nextInt(7);
			QMessage = Q[randomNum];
			
			return QMessage;
		}
		
		//문제 출제 - code = 300
		public void SendQuestion(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("문제", "300", msg);
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void SendHint() {
			try {
				ChatMsg obcm = new ChatMsg("힌트", "600", "");
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void SendHintWord(String str) {
			try {
				ChatMsg obcm = new ChatMsg("힌트", "601", str);
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void SendHintCon(String str) {
			try {
				ChatMsg obcm = new ChatMsg("힌트", "602", str);
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
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
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
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
						// logout message 처리
						cm.UserNum = UserVec.size();
						Logout();
						break;
					}
					
					else if (cm.code.matches("200")) { // 채팅 메세지
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						AppendText(msg); // server 화면에 출력
						String[] args = msg.split(" "); // 단어들을 분리한다.
						// 일반 채팅 메시지
						//WriteAll(msg + "\n"); // Write All
						WriteAllObject(cm);
						
						//보낸 채팅 메세지가 정답이라면
						if(cm.data.matches(Qm)) {
							msg = cm.UserName + "님이 정답을 맞추셨습니다. 다음 차례는 "+ UserName +"님입니다.\n";
							WriteAll(msg);
							msg = String.format("[%s] 정답", cm.UserName);
							SendCorrect(Qm);
							UserOrder = "O";
							
							SendCorrect_Other(Qm);	//code - 202
							Qm = ""; //중복 정답 방지
							if(UserOrder=="O") {
								startSend_O(); //code -400
							}
						}
					}
					
					// 시작하기 버튼 누르기 -> 서버에서 문제 보냄
					else if(cm.code.matches("400")) {
						Qm = MakeQuestion();
						
						//서버에 출력
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						msg = String.format("문제 - [%s] : %s",cm.UserName, Qm);
						AppendText(msg);
						
						SendQuestion(Qm);
						
						// 다른 사람들에게 알리기
						msg = "게임이 시작합니다.";
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
						msg = cm.UserName+"님 축하드립니다!";
						WriteAll(msg);
					}
					else { // 500, ... 기타 object는 모두 방송한다.
						WriteAllObject(cm);
					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}
