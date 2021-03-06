
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.ImageIcon;
import java.awt.*;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code;
	public String UserName;
	public String data;
	public ImageIcon img;
	public MouseEvent mouse_e;
	public int pen_size; // pen size
	public Color c;
	public String Q;
	public String Order = "";
	public int eraser_size;
	public int score = 0;
	public int UserNum = 0;
	public int StartPlayerNum = 0;

	//private 
	
	public int drawNum = 1;
	
	public int[] UserScore = {0};
	public String[] UserList;
	
	public ChatMsg(String UserName, String code, String msg) {
		this.code = code;
		this.UserName = UserName;
		this.data = msg;
	}
	
}