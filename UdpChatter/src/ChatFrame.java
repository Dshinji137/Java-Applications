import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;

public class ChatFrame {
	public String chatObj;
	public String userName;
	public JFrame chatFrame;
	public JPanel chatPanel;
	public JTextArea msgArea;
	public JScrollPane scroller;
	public JTextField msgToSend;
	public JButton sendButton;
	public MessageReceiver receiver;
	
	public ChatFrame(String chatObj,String userName,MessageReceiver r) {
		this.receiver = r;
		this.chatObj = chatObj;
		this.userName = userName;
		chatFrame = new JFrame(chatObj);
		chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		chatPanel = new JPanel();
		chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
		
		msgArea = new JTextArea(20,20);
		scroller = new JScrollPane(msgArea);
		msgArea.setLineWrap(true);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		msgToSend = new JTextField(10);
		sendButton = new JButton("send");
		sendButton.addActionListener(new sendListener());
		
		chatPanel.add(scroller);
		chatPanel.add(msgToSend);
		chatPanel.add(sendButton);
		
		chatFrame.getContentPane().add(BorderLayout.CENTER,chatPanel);
		chatFrame.setSize(500,500);
		chatFrame.setVisible(true);
	}
	
	class sendListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String msg = msgToSend.getText();
			if(msg.length() > 0) {
				try {
					receiver.mSender.send(new String[]{"send",chatObj,msg});
					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date now = new Date();
					String time = sdfDate.format(now);
					String msgToDisplay = time+"\n"+userName+": "+msg+"\n";
					msgArea.append(msgToDisplay);
					msgToSend.setText("");
				} 
				catch (Exception e) {e.printStackTrace();}
			}
		}
	}

}
