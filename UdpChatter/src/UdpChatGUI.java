import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.IOException;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class UdpChatGUI {
	public MessageReceiver receiver;
	public String userName;
	public String currChatObj;
	public int sePort = 2000;
	public JPanel panel;
	private Thread rt;
	private DatagramSocket s;
	
	public static void main(String[] args) {
		// client side
		if(args.length == 0) {
			UdpChatGUI gui = new UdpChatGUI();
			gui.go();
		}
		// server side
		else if(args.length == 2 && args[0].equals("-s")){
			Server server = new Server(Integer.parseInt(args[1]));
			server.ReceiveMSG(Integer.parseInt(args[1]));
		}
	}
	
	private int find(String s) {
		for(int i = 0; i < receiver.newMsg.size(); i++) {
			if(s.equals(receiver.newMsg.get(i))) return i;
		}
		return -1;
	}
	
	public void go() {
		/*
		Global.frame1 = new JFrame();
		Global.frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		
		JLabel userLabel = new JLabel("Username");
		JLabel serverIPLabel = new JLabel("Server IP");
		JLabel clientPortLabel = new JLabel("Client port");
		Global.userName = new JTextField(15);
		Global.serverIP = new JTextField(15);
		Global.serverIP.setText("127.0.0.1");
		Global.clientPort = new JTextField(15);
		
		JButton logOrRegButton = new JButton("login or register");
		logOrRegButton.addActionListener(new loginListener());
		
		panel.add(userLabel);
		panel.add(Global.userName);
		panel.add(serverIPLabel);
		panel.add(Global.serverIP);
		panel.add(clientPortLabel);
		panel.add(Global.clientPort);
		panel.add(logOrRegButton);
		
		Global.frame1.getContentPane().add(BorderLayout.CENTER,panel);
		Global.frame1.setSize(500,300);
		Global.frame1.setVisible(true);
		*/
		Global.frame1 = new JFrame("UdpChatter");
		Global.frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		
		JLabel nameLabel = new JLabel("User Name");
		addComponent(nameLabel,new int[]{0,1,1,1});
		Global.userName = new JTextField(20);
		addComponent(Global.userName,new int[]{1,1,1,1});
		JLabel cpLabel = new JLabel("Client Port");
		addComponent(cpLabel,new int[]{0,2,1,1});
		Global.clientPort = new JTextField(20);
		addComponent(Global.clientPort,new int[]{1,2,1,1});
		JLabel seipLabel = new JLabel("Server IP");
		addComponent(seipLabel,new int[]{0,3,1,1});
		Global.serverIP = new JTextField(20);
		addComponent(Global.serverIP,new int[]{1,3,1,1});
		JButton logOrRegButton = new JButton("login or register");
		addComponent(logOrRegButton,new int[]{0,4,2,1});
		
		logOrRegButton.addActionListener(new loginListener());
		
		Global.frame1.getContentPane().add(panel);
		Global.frame1.setSize(500,500);
		Global.frame1.setVisible(true);
		
	}
	
	public void addComponent(JComponent component, int[] pos) {
		GridBagConstraints cons = new GridBagConstraints();
		cons.weightx = 100; cons.weighty = 100;
		cons.gridx = pos[0];
		cons.gridy = pos[1];
		cons.gridwidth = pos[2];
		cons.gridheight = pos[3];
		panel.add(component,cons);
	}
	
	class loginListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				userName = Global.userName.getText();
				InetAddress seIP = InetAddress.getByName(Global.serverIP.getText());
				int clPort = Integer.parseInt(Global.clientPort.getText());
				s = new DatagramSocket(clPort);
				receiver = new MessageReceiver(s,userName,sePort,seIP);
				rt = new Thread(receiver);
				rt.start();
				receiver.mSender.send(new String[]{"reg",userName});
			} 
			catch (UnknownHostException e) {e.printStackTrace();} 
			catch (SocketException e) {e.printStackTrace();} 
			catch (IOException e) {e.printStackTrace();} 
			catch (Exception e) {e.printStackTrace();} 
			
			while(!receiver.updated) {
				try {
					Thread.sleep(200);
				} 
				catch (InterruptedException e) {e.printStackTrace();}
			}

			Global.logoutButton.addActionListener(new logoutListener());
			Global.talkButton.addActionListener(new talkListener());
		}
	}
	
	class logoutListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Global.frame2.dispose();
			try {
				receiver.mSender.send(new String[]{"dereg",userName});
				rt.join();
				s.close();
			} 
			catch (Exception e) {e.printStackTrace();}
			go();
		}
	}
	
	class talkListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Global.contactList.clearSelection();
			String chatObj = receiver.currChatObj;
			if(Global.chatFrame.get(chatObj)==null || !Global.chatFrame.get(chatObj).chatFrame.isVisible()) {
				ChatFrame chat = new ChatFrame(chatObj,userName,receiver);
				Global.chatFrame.put(chatObj,chat);
				// there are some messages
				if(Global.history.get(chatObj) != null && Global.history.get(chatObj).size() > 0) {
					List<String> list = Global.history.get(chatObj);
					for(int i = 0; i < list.size(); i++) {
						String msgToDisplay = list.get(i)+"\n";
						chat.msgArea.append(msgToDisplay);
					}
					list = new ArrayList<>();
					Global.history.put(chatObj,list);
					int indToDel = find(chatObj);
					if(indToDel != -1) receiver.newMsg.remove(indToDel);
					receiver.changeIndicator();
				}
			}
		}
	}
	
}
