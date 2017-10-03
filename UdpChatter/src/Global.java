import javax.swing.*;
import java.util.*;

public class Global {
	public static JFrame frame1;
	public static JFrame frame2;
	public static JTextField userName;
	public static JTextField serverIP;
	public static JTextField clientPort;
	public static String[] contacts;
	
	public static JPanel contactPanel;
	public static JLabel userNameLabel;
	public static JLabel msgIndicator = new JLabel("You have no message.");
	public static JList contactList;
	public static JScrollPane contactScroller;
	public static JButton talkButton = new JButton("talk");
	public static JButton logoutButton = new JButton("logout");
	
	public static Map<String,ChatFrame> chatFrame = new HashMap<>();
	public static Map<String,List<String>> history = new HashMap<>();
	
	public static void go() {
		
	}
}
