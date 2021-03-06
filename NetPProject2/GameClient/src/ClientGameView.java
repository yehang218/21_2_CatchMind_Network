
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Spring;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.JOptionPane;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.SpringLayout;
import javax.swing.JSlider;
import javax.swing.UIManager;

public class ClientGameView extends JFrame {
	/**
	 * 
	 */

	private int Panelx = 418;
	private int Panely = 59;

	int ox = 0, oy = 0, nx = 0, ny = 0;
	int x1 = 0, y1 = 0, x2 = 0, y2 = 0;

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	// private JTextArea textArea;
	private JTextPane textArea;
	private JButton btnStartButton;
	private JSlider SizeControlerSlider;
	private JButton BtnColorPicker;
	private JTextPane UserNameText;
	JButton BtnFree;
	JButton BtnRect;
	JButton BtnCircle;

	private JTextField[] UserNameTF = new JTextField[4];
	private JTextField[] UserScoreTF = new JTextField[4];

	private Frame frame;
	private FileDialog fd;
	private JButton imgHintBtn;
	private JPanel hint_panel;

	JPanel panel;
	JLabel label;
	private Graphics2D gc;
	private int pen_size = 2; // minimum 2, 펜 사이즈
	private int eraser_size = 2; // minimum 2, 지우개 사이즈
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImage = null;
	private Image tmpImage = null;
	private Graphics2D gc3 = null;
	private Graphics2D gc2 = null;
	private ImageIcon imgPen;
	private ImageIcon imgEraser;
	private ImageIcon imgHint;
	private ImageIcon imgPanel;
	private ImageIcon imgcolor;

	private int score;
	private String Qm = "  ";
	private Color selectedColor = Color.BLACK;
	private Color sc = Color.BLACK;
	private String Qm_c = " ";

	public String Order = "X";

	private int imgNum = 0;
	private int UserNum = 0;
	private int drawNum = 1; // eraser = 0, free = 1, rect = 2, circle = 3, line =4

	/**
	 * Create the frame.
	 * 
	 * @throws BadLocationException
	 */
	public ClientGameView(String username, String ip_addr, String port_no) {

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 683, 550);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(24,148,198));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(418, 270, 237, 188);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 12));

		panel_1 = new JPanel();
		panel_1.setBackground(new Color(8,99,165));
		panel_1.setBounds(418, 59, 237, 154);
		panel_1.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));
		contentPane.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		
		
		lblname = new JLabel("");
		lblname.setBackground(Color.YELLOW);
		lblname.setFont(new Font("함초롬돋움", Font.BOLD, 12));
		ImageIcon imgname = new ImageIcon(".\\nickname.png");
		imgname = imageSetSize(imgname,106,25);
		lblname.setIcon(imgname);
		panel_1.add(lblname);
		
	
		
		lblscore = new JLabel("");
		lblscore.setBackground(Color.YELLOW);
		lblscore.setFont(new Font("함초롬돋움", Font.BOLD, 12));
		ImageIcon imgscore = new ImageIcon(".\\score.png");
		imgscore = imageSetSize(imgscore,106,25);
		lblscore.setIcon(imgscore);
		panel_1.add(lblscore);

		for (int i = 0; i < 4; i++) {
			
			UserNameTF[i] = new JTextField(10);
			UserScoreTF[i] = new JTextField(10);
			panel_1.add(UserNameTF[i]);
			panel_1.add(UserScoreTF[i]);
			UserNameTF[i].setColumns(10);
			UserScoreTF[i].setColumns(10);
		}

		txtInput = new JTextField();
		txtInput.setBounds(417, 468, 170, 33);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("");
		//btnSend.setFont(new Font("함초롬돋움", Font.PLAIN, 10));
		btnSend.setBounds(599, 470, 57, 30);
		ImageIcon imgSend = new ImageIcon(".\\SendBtn.png");
		imgSend = imageSetSize(imgSend,57,30);
		btnSend.setIcon(imgSend);
		contentPane.add(btnSend);
		setVisible(true);

		imgHintBtn = new JButton("");
		//imgHintBtn.setFont(new Font("함초롬돋움", Font.PLAIN, 16));
		imgHintBtn.setBounds(296, 11, 110, 36);
		ImageIcon imgHint1 = new ImageIcon(".\\Hint1.png");
		imgHint1 = imageSetSize(imgHint1,110,36);
		imgHintBtn.setIcon(imgHint1);
		imgHintBtn.setEnabled(false);
		contentPane.add(imgHintBtn);

		// 스케치북

		panel = new JPanel();
		// imgPanel = new ImageIcon(".\\820495.png");
		// imgPanel = imageSetSize(imgPanel, 394, 377);
		// label =new JLabel("",imgPanel,JLabel.CENTER);
		// panel.add(label);
		panel.setBackground(Color.WHITE);
		panel.setBounds(12, 59, 394, 377);
		contentPane.add(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);

		lblHint = new JLabel("");
		sl_panel.putConstraint(SpringLayout.NORTH, lblHint, 0, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, lblHint, 0, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, lblHint, 377, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, lblHint, 394, SpringLayout.WEST, panel);
		panel.add(lblHint);
		gc = (Graphics2D) panel.getGraphics();

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;

		// Image 영역 보관용. paint() 에서 이용한다.
		panelImage = createImage(panel.getWidth(), panel.getHeight());
		gc2 = (Graphics2D) panelImage.getGraphics();

		tmpImage = createImage(panel.getWidth(), panel.getHeight());
		gc3 = (Graphics2D) tmpImage.getGraphics();
		gc3.setColor(panel.getBackground());
		gc3.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		gc3.setColor(Color.BLACK);
		gc3.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);

		gc2.setColor(panel.getBackground());
		gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		gc2.setColor(Color.BLACK);
		gc2.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);

		BtnColorPicker = new JButton("");
		imgcolor = new ImageIcon(".\\color.png");
		imgcolor = imageSetSize(imgcolor, 60, 40);
		BtnColorPicker.setIcon(imgcolor);

		BtnColorPicker.addActionListener(new ActionListener() {
			JColorChooser chooser = new JColorChooser();

			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				selectedColor = chooser.showDialog(null, "Color", Color.YELLOW);
				sc = selectedColor;
				BtnColorPicker.setBackground(selectedColor);
				if (selectedColor != null) {
					gc2.setColor(selectedColor);
					BtnEraser.setEnabled(true);
				}
			}
		});
		BtnColorPicker.setBackground(selectedColor);
		BtnColorPicker.setBounds(22, 446, 70, 55);
		contentPane.add(BtnColorPicker);

		// 지우개 버튼
		BtnEraser = new JButton("");
		BtnEraser.setBackground(Color.LIGHT_GRAY);
		imgEraser = new ImageIcon(".\\eraserBtnIcon.png");
		imgEraser = imageSetSize(imgEraser, 45, 40);

		imgPen = new ImageIcon(".\\pen.png");
		imgPen = imageSetSize(imgPen,80, 23);

		// 상단의 문제 텍스트 박스
		txtQuestion = new JLabel("?",JLabel.CENTER);
		txtQuestion.setHorizontalAlignment(SwingConstants.CENTER);
		txtQuestion.setFont(new Font("함초롬돋움", Font.BOLD, 14));
		txtQuestion.setBackground(new Color(255, 255, 255));
		txtQuestion.setBounds(47, 11, 237, 36);
		contentPane.add(txtQuestion);

		// 펜 사이즈 조절 슬라이더
		SizeControlerSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, 10);
		SizeControlerSlider.setBackground(SystemColor.activeCaption);
		if (drawNum > 0) {
			SizeControlerSlider.setValue(pen_size);
		} else {
			SizeControlerSlider.setValue(eraser_size);
		}
		SizeControlerSlider.setBounds(297, 446, 109, 16);
		contentPane.add(SizeControlerSlider);
		SizeControlerSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				JSlider js = (JSlider) e.getSource();
				if (drawNum > 0) {
					pen_size = js.getValue();
				} else {
					eraser_size = js.getValue();
				}
			}

		});

		// 네모 그리기 버튼
		BtnRect = new JButton("□");
		BtnRect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BtnRect.setEnabled(false);
				drawNum = 2;
				selectedColor = sc;
				BtnEraser.setEnabled(true);
				BtnFree.setEnabled(true);
				BtnCircle.setEnabled(true);
				BtnLine.setEnabled(true);
				BtnColorPicker.setEnabled(true);
				BtnColorPicker.setBackground(selectedColor);
			}
		});
		BtnRect.setBounds(107, 473, 80, 23);
		contentPane.add(BtnRect);

		// 동그라미 그리기 버튼
		BtnCircle = new JButton("○");
		BtnCircle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BtnCircle.setEnabled(false);
				drawNum = 3;
				selectedColor = sc;
				BtnEraser.setEnabled(true);
				BtnFree.setEnabled(true);
				BtnRect.setEnabled(true);
				BtnLine.setEnabled(true);
				BtnColorPicker.setEnabled(true);
				BtnColorPicker.setBackground(selectedColor);
			}
		});
		BtnCircle.setBounds(199, 473, 80, 23);
		contentPane.add(BtnCircle);

		// 선 그리기 버튼
		BtnLine = new JButton("/");
		BtnLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawNum = 4;
				selectedColor = sc;
				BtnCircle.setEnabled(true);
				BtnEraser.setEnabled(true);
				BtnFree.setEnabled(true);
				BtnRect.setEnabled(true);
				BtnLine.setEnabled(false);
				BtnColorPicker.setEnabled(true);
				BtnColorPicker.setBackground(selectedColor);
			}
		});
		BtnLine.setBounds(107, 446, 80, 23);
		contentPane.add(BtnLine);

		// 자유 그리기
		BtnFree = new JButton("");
		BtnFree.setIcon(imgPen);
		BtnFree.setEnabled(false);
		BtnFree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawNum = 1;
				selectedColor = sc;
				BtnFree.setEnabled(false);
				BtnEraser.setEnabled(true);
				BtnCircle.setEnabled(true);
				BtnLine.setEnabled(true);
				BtnRect.setEnabled(true);
				BtnColorPicker.setEnabled(true);
				BtnColorPicker.setBackground(selectedColor);
				SizeControlerSlider.setValue(pen_size);

			}
		});

		BtnFree.setBounds(199, 446, 80, 23);
		contentPane.add(BtnFree);

		BtnEraser.setIcon(imgEraser);
		BtnEraser.setEnabled(false);
		BtnEraser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawNum = 0;
				// 지우개일때
				BtnFree.setEnabled(true);
				BtnCircle.setEnabled(true);
				BtnLine.setEnabled(true);
				BtnRect.setEnabled(true);
				sc = selectedColor;
				selectedColor = Color.WHITE;
				BtnEraser.setEnabled(false);
				SizeControlerSlider.setValue(eraser_size);
				BtnColorPicker.setEnabled(false);
				BtnColorPicker.setBackground(selectedColor);
			}
		});
		BtnEraser.setBounds(328, 468, 40, 33);
		contentPane.add(BtnEraser);

		
		wordHintBtn = new JButton("");
		wordHintBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "601", "Hint_1");
				SendObject(msg);
				btnStartButton.setEnabled(false);
				imgHintBtn.setEnabled(true);
			}
		});
		//wordHintBtn.setFont(new Font("함초롬돋움", Font.PLAIN, 16));
		wordHintBtn.setEnabled(false);
		ImageIcon imgHint2 = new ImageIcon(".\\Hint2.png");
		imgHint2 = imageSetSize(imgHint2,110,36);
		wordHintBtn.setIcon(imgHint2);
		wordHintBtn.setBounds(418, 11, 110, 36);
		contentPane.add(wordHintBtn);

		conHintBtn = new JButton("");
		conHintBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "602", "Hint_2");
				SendObject(msg);
				btnStartButton.setEnabled(false);
				imgHintBtn.setEnabled(true);
			}
		});
		//conHintBtn.setFont(new Font("함초롬돋움", Font.PLAIN, 16));
		ImageIcon imgHint3 = new ImageIcon(".\\Hint3.png");
		imgHint3 = imageSetSize(imgHint3,110,36);
		conHintBtn.setIcon(imgHint3);
		conHintBtn.setEnabled(false);
		conHintBtn.setBounds(540, 11, 110, 36);
		contentPane.add(conHintBtn);

		
		
		// 시작하기 버튼
		btnStartButton = new JButton("");
		btnStartButton.setBounds(418, 223, 110, 37);
		ImageIcon imgStart = new ImageIcon(".\\startBtn.png");
		imgStart = imageSetSize(imgStart,110,37);
		btnStartButton.setIcon(imgStart);
		contentPane.add(btnStartButton);
		btnStartButton.setEnabled(false);
		btnStartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Start");
				SendObject(msg);
				btnStartButton.setEnabled(false);
				imgHintBtn.setEnabled(true);
			}
		});
		btnStartButton.setFont(new Font("함초롬돋움", Font.PLAIN, 14));

		// Logout(나가기) 버튼
		JButton btnExitButton = new JButton("");
		btnExitButton.setBackground(Color.WHITE);
		btnExitButton.setBounds(550, 223, 100, 37);
		
		ImageIcon imgEnd = new ImageIcon(".\\exitBtnIcon.png");
		imgEnd = imageSetSize(imgEnd,110,37);
		btnExitButton.setIcon(imgEnd);
		contentPane.add(btnExitButton);
		btnExitButton.setFont(new Font("함초롬돋움", Font.PLAIN, 14));
		
		panel_2 = new JPanel() {
			public void paintComponent(Graphics g) {
				ImageIcon imgQuestion = new ImageIcon(".\\Question.png");
				imgQuestion = imageSetSize(imgQuestion,274,36);
				g.drawImage(imgQuestion.getImage(), 0, 0, null);
			}	

		};
		panel_2.setBounds(12, 11, 274, 36);
		contentPane.add(panel_2);
		btnExitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "101", "Bye");
				SendObject(msg);
				System.exit(0);
			}
		});

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");

			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();

			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();

			ImageSendAction action2 = new ImageSendAction();
			imgHintBtn.addActionListener(action2);

			MyMouseEvent mouse_f = new MyMouseEvent();
			panel.addMouseMotionListener(mouse_f);
			panel.addMouseListener(mouse_f);

			if (Panelx < 547)
				Panelx = Panelx + 128;
			if (Panely < 142)
				Panely = Panely + 82;

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}

	}

	public void paint(Graphics g) {
		super.paint(g);
		// Image 영역이 가려졌다 다시 나타날 때 그려준다.
		if (Order.equals("O")) {
			gc.drawImage(panelImage, 0, 0, this);
		}
	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		String[] username;
		String[] UserScore;

		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s] %s", cm.UserName, cm.data);

					} else
						continue;
					switch (cm.code) {
					case "100":
						username = cm.data.split("\t");
						for (int i = 0; i < username.length; i++) {
							UserNameTF[i].setText(username[i]);
							UserScoreTF[i].setText(cm.score + "");
							UserNameTF[i].setFont(new Font("함초롬돋움",Font.BOLD,10));
							UserScoreTF[i].setFont(new Font("함초롬돋움",Font.BOLD,10));
							if (cm.Order.equals("O"))
								UserNum = i;
						}
						UserNameTF[UserNum].setBorder(new LineBorder(new Color(0, 0, 255), 3));
						break;
					case "101":
						for (int i = 0; i < 4; i++) {
							UserNameTF[i].setText(" ");
							UserScoreTF[i].setText(" ");
						}
						String[] username2 = cm.data.split("\t"); // 단어들을 분리한다.
						for (int i = 0; i < username2.length; i++) {
							UserNameTF[i].setText(username2[i]);
							UserScoreTF[i].setText("0");
						}
						break;

					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextR(msg); // 내 메세지는 우측에
						else
							AppendText(msg);
						break;
					case "201": { // 내가 정답 맞추면
						String[] userScore = cm.data.split("\t");
						cm.drawNum = 1;
						drawNum = 1;
						;
						btnStartButton.setEnabled(true);
						Order = "O";
						cm.Order = Order;
						// imgHintBtn.setEnabled(true);
						for (int i = 0; i < username.length; i++) {
							if (username[i].equals(cm.UserName)) {
								UserNum = i;
							}
							UserScoreTF[i].setText(userScore[i + 1]);
						}
						for (int i = 0; i < username.length; i++) {
							UserNameTF[i].setBorder(new LineBorder(new Color(128, 128, 128), 1));
						}
						UserNameTF[UserNum].setBorder(new LineBorder(new Color(0, 0, 255), 3));
						JOptionPane.showMessageDialog(contentPane, cm.UserName + "님 정답입니다! " + userScore[0]);
						txtQuestion.setText("" + userScore[0]);
						AppendImage(cm.img);

						for (int i = 0; i < username.length; i++) {
							if (username[i].equals(cm.UserName)) {
								UserNum = i;
							}
							if (Integer.parseInt(userScore[i + 1]) >= 100) {
								EndMessage();
								break;
							}
						}

						break;
					}
					case "202": { // 내가 아닌 다른 사람이 정답을 맞췄을때
						String[] userScore = cm.data.split("\t");
						// score 업데이트
						for (int i = 0; i < username.length; i++) {
							if (username[i].equals(cm.UserName)) {
								UserNum = i;
							}
							UserScoreTF[i].setText(userScore[i + 1]);

						}
						imgHintBtn.setText("");
						cm.drawNum = 1;
						drawNum = 1;
						JOptionPane.showMessageDialog(contentPane, cm.UserName + "님 정답입니다! " + userScore[0]);
						txtQuestion.setText("" + userScore[0]);

						for (int i = 0; i < username.length; i++) {
							UserNameTF[i].setBorder(new LineBorder(new Color(128, 128, 128), 1));
						}
						UserNameTF[UserNum].setBorder(new LineBorder(new Color(0, 0, 255), 3));
						// PanelUser[PlayerNum].setBorder(new LineBorder(new Color(0, 0, 0), 1));
						imgHintBtn.setEnabled(false);
						txtInput.setEnabled(true);
						btnSend.setEnabled(true);
						BtnEraser.setEnabled(false); // 지우개 비활성화
						AppendImage(cm.img);
						BtnFree.setEnabled(false);
						BtnCircle.setEnabled(false);
						BtnRect.setEnabled(false);
						BtnLine.setEnabled(false);
						Order = "X";
						cm.Order = Order;
						break;
					}
					case "300": { // start 버튼 누르면 서버로부터 문제받아서 그림 그리기
						gc2.setColor(Color.white);
						gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
						gc.drawImage(panelImage, 0, 0, panel);
						if (cm.drawNum > 0) {
							selectedColor = Color.BLACK;
						}
						cm.drawNum = 1;
						drawNum = 1;
						pen_size = 2;
						SizeControlerSlider.setValue(pen_size);
						eraser_size = 2;
						String[] args = msg.split(" "); // 단어들을 분리한다.
						txtQuestion.setText("" + args[1]);
						Qm = args[1];
						JOptionPane.showMessageDialog(contentPane, "" + args[1]);
						// LabelName[PlayerNum].setBorder(new LineBorder(new Color(0, 0, 255), 3));
						BtnEraser.setIcon(imgEraser);
						BtnColorPicker.setEnabled(true);
						imgHintBtn.setEnabled(true);
						txtInput.setEnabled(false); // 채팅창 잠그기
						btnSend.setEnabled(false);
						BtnEraser.setEnabled(true);
						BtnFree.setEnabled(false);
						BtnRect.setEnabled(true);
						BtnCircle.setEnabled(true);
						BtnLine.setEnabled(true);
						imgHint = cm.img;
					}
						break;
					case "310": // Image 첨부
						// AppendImage(cm.img);
						break;
					case "400": // 내 차례이면 start버튼 활성화
						cm.drawNum = 1;
						drawNum = 1;
						Order = "O";
						cm.Order = Order;
						btnStartButton.setEnabled(true);
						imgHintBtn.setEnabled(false);
						wordHintBtn.setEnabled(false);
						conHintBtn.setEnabled(false);
						break;
					case "401": // 다른사람이 Start 버튼을 눌렀을 때
						wordHintBtn.setEnabled(true);
						conHintBtn.setEnabled(true);
						gc2.setColor(Color.white);
						gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
						gc.drawImage(panelImage, 0, 0, panel);
						txtQuestion.setText("?");
						BtnFree.setEnabled(false);
						BtnCircle.setEnabled(false);
						BtnRect.setEnabled(false);
						BtnLine.setEnabled(false);
						BtnColorPicker.setEnabled(false);
						imgHintBtn.setEnabled(false);
						break;
					case "500": { // Mouse Event 수신
						DoMouseEvent(cm);
						break;
					}
					case "600": {
						String[] args = msg.split("\n"); // 단어들을 분리한다.
						imgHint = cm.img;
						break;
					}
					case "601": {
						imgHintBtn.setEnabled(false);
						JOptionPane.showMessageDialog(contentPane, cm.data);
						break;
					}
					case "602": {
						imgHintBtn.setEnabled(false);
						JOptionPane.showMessageDialog(contentPane, cm.data);
						break;
					}
					case "700": {
						lblHint.setIcon(new ImageIcon(".\\End.gif"));
						txtQuestion.setText(cm.data + "님 우승입니다!");
						btnStartButton.setEnabled(false);
						BtnFree.setEnabled(false);
						BtnCircle.setEnabled(false);
						BtnRect.setEnabled(false);
						BtnLine.setEnabled(false);
						BtnColorPicker.setEnabled(false);
						break;
					}

					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}

	}

	// Mouse Event 수신 처리
	public void DoMouseEvent(ChatMsg cm) {
		boolean mousePressed = false;
		int xArray[] = { 0, 0 };
		int yArray[] = { 0, 0 };

		if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
			return;

		gc2.setColor(cm.c);
		if (cm.drawNum > 0) {
			gc2.setStroke(new BasicStroke(cm.pen_size));
			// panelImnage는 paint()에서 이용한다.
		} else {
			gc2.setStroke(new BasicStroke(cm.eraser_size));
		}

		if (cm.mouse_e.getID() == MouseEvent.MOUSE_PRESSED) {
			if (!mousePressed) {
				ox = cm.mouse_e.getX();
				oy = cm.mouse_e.getY();
				mousePressed = true;
			}

			if (drawNum == 4) {
				gc3.drawImage(panelImage, 0, 0, panel);
				xArray[0] = ox;
				yArray[0] = oy;
				mousePressed = true;
			}

		}

		if (cm.mouse_e.getID() == MouseEvent.MOUSE_DRAGGED) {
			nx = cm.mouse_e.getX();
			ny = cm.mouse_e.getY();

			if (cm.drawNum == 1 || cm.drawNum == 0) {
				// free draw
				gc2.drawLine(ox, oy, nx, ny);
				gc.drawImage(panelImage, 0, 0, panel);
				ox = nx;
				oy = ny;
			}

		}

		if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
			mousePressed = false;

			if (cm.drawNum == 2) {
				int x, y;
				x = ox - cm.mouse_e.getX();
				y = oy - cm.mouse_e.getY();
				if (x < 0)
					x = -x;
				if (y < 0)
					y = -y;

				gc2.drawRect(ox, oy, x, y);
				gc.drawImage(panelImage, 0, 0, panel);
			}
			// 원
			else if (cm.drawNum == 3) {
				int x, y;
				x = ox - cm.mouse_e.getX();
				y = oy - cm.mouse_e.getY();
				if (x < 0)
					x = -x;
				if (y < 0)
					y = -y;

				gc2.drawOval(ox, oy, x, y);
				gc.drawImage(panelImage, 0, 0, panel);
			}
			// 직선
			else if (cm.drawNum == 4) {
				xArray[0] = ox;
				yArray[0] = oy;
				xArray[1] = cm.mouse_e.getX();
				yArray[1] = cm.mouse_e.getY();

				gc2.drawPolyline(xArray, yArray, 2);
				gc.drawImage(panelImage, 0, 0, panel);

			}

		}

	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		cm.eraser_size = eraser_size;
		cm.c = selectedColor;
		// shape 모양 정해주는 숫자
		cm.drawNum = drawNum;
		SendObject(cm);
	}

	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		Point startP = null;
		Point endP = null;
		int x = 0, y = 0;
		int xArray[] = { 0, 0 };
		int yArray[] = { 0, 0 };

		boolean mouseClicked;

		@Override
		public void mouseDragged(MouseEvent e) {
			endP = e.getPoint();
			x2 = e.getX();
			y2 = e.getY();
			gc2.setColor(selectedColor);

			if (drawNum > 0) {
				gc2.setStroke(new BasicStroke(pen_size));
			} else {
				gc2.setStroke(new BasicStroke(eraser_size));
			}

			if (Order.equals("O")) {
				if (drawNum == 1 || drawNum == 0) {
					gc2.drawLine(startP.x, startP.y, endP.x, endP.y);
					gc.drawImage(panelImage, 0, 0, panel);
					SendMouseEvent(e);
				}

				// 사각형 그리기
				else if (drawNum == 2) {
					gc3.setColor(selectedColor);
					x = x1 - x2;
					y = y1 - y2;
					if (x < 0)
						x = -x;
					if (y < 0)
						y = -y;
					// gc.drawImage(tmpImage, 0, 0, panel);
					// gc2.drawImage(tmpImage,0,0,panel);
					// gc3.drawRect(x1, y1, x , y);

					gc2.drawImage(tmpImage, 0, 0, panel);
					gc2.drawRect(x1, y1, x, y);
					gc.drawImage(panelImage, 0, 0, panel);

					SendMouseEvent(e);

				}

				else if (drawNum == 3) {
					gc3.setColor(selectedColor);
					x = x2 - x1;
					y = y2 - y1;
					if (x < 0)
						x = -x;
					if (y < 0)
						y = -y;

					gc2.drawImage(tmpImage, 0, 0, panel);
					gc2.drawOval(x1, y1, x, y);
					gc.drawImage(panelImage, 0, 0, panel);

					SendMouseEvent(e);
				}

				// 직선 그리기
				else if (drawNum == 4) {
					gc3.setColor(selectedColor);
					xArray[1] = e.getX();
					yArray[1] = e.getY();

					gc2.drawImage(tmpImage, 0, 0, panel);
					gc2.drawPolyline(xArray, yArray, 2);

					gc.drawImage(panelImage, 0, 0, panel);
					SendMouseEvent(e);
				}
			}
			startP = endP;

		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// panel.setBackground(Color.YELLOW);

		}

		@Override
		public void mouseExited(MouseEvent e) {

			// panel.setBackground(Color.CYAN);

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!mouseClicked) {
				if (drawNum == 1 || drawNum == 0) {
					startP = e.getPoint();
					mouseClicked = true;
				} else if (drawNum == 2) {
					gc3.drawImage(panelImage, 0, 0, panel);
					x1 = e.getX();
					y1 = e.getY();
					mouseClicked = true;
				}

				else if (drawNum == 3) {
					gc3.drawImage(panelImage, 0, 0, panel);
					x1 = e.getX();
					y1 = e.getY();
					mouseClicked = true;
				}

				// Line
				else if (drawNum == 4) {
					gc3.drawImage(panelImage, 0, 0, panel);
					xArray[0] = e.getX();
					yArray[0] = e.getY();
					mouseClicked = true;
				}

			}
			if (Order.equals("O"))
				SendMouseEvent(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			x2 = e.getX();
			y2 = e.getY();

			mouseClicked = false;
			if (Order.equals("O"))
				SendMouseEvent(e);
		}
	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	// 힌트버튼
	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == imgHintBtn) {
				// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
				if (imgNum == 0) {
					imgHintBtn.setFont(new Font("굴림", Font.PLAIN, 12));
					//imgHintBtn.setText("다시 불러오기");
					imgHint = imageSetSize(imgHint, panel.getWidth(), panel.getHeight());
					// lblHint.setBorder(new LineBorder(new Color(0, 0, 0), 1));
					lblHint.setIcon(imgHint);
					gc2.drawImage(imgHint.getImage(), 0, 0, lblHint);
					// imgNum++;
				} else {
					// imgHintBtn.setText("힌트 닫기");
					// lblHint.setBorder(new LineBorder(new Color(0, 0, 0), 0));
					// lblHint.setIcon(new ImageIcon());
					// imgNum++;
				}
			}

		}
	}

	ImageIcon icon1 = new ImageIcon("src/icon1.jpg");
	private JButton BtnEraser;
	private JLabel txtQuestion;

	private JLabel lblHint;

	private JButton BtnLine;
	private JPanel panel_1;
	private JButton wordHintBtn;
	private JButton conHintBtn;
	private JPanel panel_2;
	private JLabel lblname;
	private JLabel lblscore;

	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// 화면에 출력
	public void AppendText(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.

		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		// textArea.replaceSelection("\n");

	}

	// 내 채팅 회색 표시
	public void AppendTextR(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.GRAY);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		// textArea.replaceSelection("\n");

	}

	// 이미지 출력
	public void AppendImage(ImageIcon ori_icon) {
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 400 || height > 400) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 400;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 400;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
		} else {
			new_img = ori_img;
			new_icon = new ImageIcon(new_img);
		}

		new HintFrame(new_icon);
		// gc2.drawImage(ori_img, 0, 0, hint_panel.getWidth(), hint_panel.getHeight(),
		// this);
		// gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);

	}

	public class HintFrame extends JFrame {
		ImageIcon imgicon;
		JButton closeBtn = new JButton("닫기");

		public HintFrame(ImageIcon img) {
			setTitle("힌트 이미지");
			Container c = getContentPane();
			c.setLayout(new BorderLayout());
			JLabel imageLabel = new JLabel(img);
			c.add(imageLabel, BorderLayout.CENTER);
			closeBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			c.add(closeBtn, BorderLayout.SOUTH);

			setSize(400, 400);
			setLocationRelativeTo(contentPane);
			setVisible(true);

		}
	}

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	public void StartMessage() {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "300", "Start");
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void EndMessage() {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "700", "END");
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	// Server에게 network으로 전송
	public void SendMessage(String msg) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			AppendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error");
		}
	}

	ImageIcon imageSetSize(ImageIcon icon, int i, int j) {
		Image ximg = icon.getImage();
		Image yimg = ximg.getScaledInstance(i, j, java.awt.Image.SCALE_SMOOTH);
		ImageIcon xyimg = new ImageIcon(yimg);
		return xyimg;
	}
}
