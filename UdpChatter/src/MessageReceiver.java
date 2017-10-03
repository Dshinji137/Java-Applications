import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MessageReceiver implements Runnable{
	public List<String> contacts;
	public List<String> newMsg;
	public MessageSender mSender;
	public boolean updated;
	public String currChatObj;
	private DatagramSocket clientSocket;
	private byte[] buffer;
	
	public MessageReceiver(DatagramSocket s,String name,int serverport,InetAddress IP) {
		clientSocket = s;
		buffer = new byte[1024];
		mSender = new MessageSender(clientSocket,name,serverport,IP);
		updated = false;
		newMsg = new ArrayList<>();
	}
	
	public void initial() {
		if(Global.frame2 != null && Global.frame2.isVisible()) Global.frame2.dispose();
		Global.frame2 = new JFrame();
		Global.frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);			
		Global.contactPanel = new JPanel();
		Global.contactPanel.setLayout(new GridBagLayout());
		Global.userNameLabel = new JLabel("User:"+Global.userName.getText());
	}
	
	public void updateContacts() {
		initial();
		int size = this.contacts.size();
		Global.contacts = new String[size];
		for(int i = 0; i < size; i++) {
			Global.contacts[i] = this.contacts.get(i);
		}
		Global.contactList = new JList(Global.contacts);
		Global.contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Global.contactScroller = new JScrollPane(Global.contactList);
		Global.contactScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.updated = true;
		
		Global.contactPanel.remove(Global.userNameLabel);
		Global.contactPanel.remove(Global.msgIndicator);
		Global.contactPanel.remove(Global.contactScroller);
		Global.contactPanel.remove(Global.talkButton);
		Global.contactPanel.remove(Global.logoutButton);
		
		addComponent(Global.userNameLabel,new int[]{0,1,1,1});
		addComponent(Global.msgIndicator,new int[]{0,2,1,1});
		addComponent(Global.talkButton,new int[]{0,3,1,1});
		addComponent(Global.logoutButton,new int[]{0,4,1,1});
		addComponent(new JLabel("All contacts"),new int[]{1,1,1,1});
		addComponent(Global.contactScroller,new int[]{1,2,1,3});

		Global.frame2.getContentPane().add(BorderLayout.CENTER,Global.contactPanel);
		
		Global.contactList.addListSelectionListener(new chatListener());
		
		Global.frame2.setSize(500,500);
		Global.frame1.setVisible(false);
		Global.frame2.setVisible(true);
		changeIndicator();
	}
	
	public void changeIndicator() {
		if(newMsg.size() == 0) {
			Global.msgIndicator.setText("You have no messages.");
		}
		else {
			String msgSrc = "";
			for(int i = 0; i < newMsg.size(); i++) {
				if(i < newMsg.size()-1) msgSrc += newMsg.get(i)+", ";
				else msgSrc += newMsg.get(i)+".";
			}
			Global.msgIndicator.setText("You have messages from "+msgSrc);
		}
	}
	
	public void run() {
		while(true) {
			if(!mSender.running) break;
			try {
				DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
				clientSocket.receive(incoming);
				String[] recv = new String(incoming.getData(),0,incoming.getLength()).split("//");
				// update local user table
								
				if(recv[0].equals("userInfo")) {
					contacts = new ArrayList<String>();
					int pos = 1;
					while(pos < recv.length) {
						String[] tmp = new String[3];
						for(int i = pos+1; i < pos+4; i++) {
							tmp[(i-2)%4] = recv[i];
						}
						mSender.allUser.put(recv[pos],tmp);
						contacts.add(recv[pos]);
						pos += 4;
					}
					updateContacts();
					System.out.println("Client tables updated");
				}
				// register ACK
				if(recv[0].equals("reg-ack")) {
					mSender.regConfirm = true;
					System.out.println("Welcome,you are registered");
				}
				if(recv[0].equals("al-reg-ack")) {
					mSender.regConfirm = true;
					System.out.println(mSender.userName+",welcome back");
				}
				// Offline chat
				if(recv[0].equals("HaveMessage")) {
					System.out.println("You have messages");
				}
				if(recv[0].equals("OffLine")) {
					String sender = recv[2];
					String msg = recv[1]+"\n"+recv[2]+" "+recv[3];
					List<String> list = Global.history.getOrDefault(sender,new ArrayList<String>());
					list.add(msg);
					Global.history.put(sender,list);
					if(!newMsg.contains(sender)) newMsg.add(sender);
					changeIndicator();
				}
				// server check if the client is still on
				if(recv[0].equals("StillOn")) {
					String send = "yes";
					mSender.sendMessage(send,mSender.serverPort,mSender.serverIP);
				}
				// de-register ACK
				if(recv[0].equals("dreg-ack")) {
					mSender.deregConfirm = true;
					System.out.println("You are Offline,Bye");
					break;
				}
				// de-registered a user that does not exist
				if(recv[0].equals("dreg-nack")) {
					mSender.deregConfirm = true;
					System.out.println("No such users!");
				}
				// receiver exists
				if(recv[0].equals("RecvEx")) {
					System.out.println("User "+recv[1]+" exists!");
				}
				// message saved by server
				if(recv[0].equals("messageSaved")) {
					mSender.saveConfirm = true;
					System.out.println("Message saved by the server");
				}
				// client received
				if(recv[0].equals("client-ack")) {
					mSender.sendConfirm = true;
					System.out.println("Message received by "+recv[1]);
				}
				// client message, send ACK to sender.
				if(recv[0].equals("client-chat")) {
					int port = incoming.getPort();
					InetAddress ip = incoming.getAddress();
					String send = "client-ack//"+mSender.userName;
					mSender.sendMessage(send,port,ip);
					String sender = recv[1];
					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date now = new Date();
					String time = sdfDate.format(now);
					// if chatframe not opened
					if(Global.chatFrame.get(sender)==null || !Global.chatFrame.get(sender).chatFrame.isVisible()) {
						List<String> list = Global.history.getOrDefault(sender,new ArrayList<String>());
						list.add(time+"\n"+recv[2]);
						Global.history.put(sender,list);
						if(!newMsg.contains(sender)) newMsg.add(sender);
						changeIndicator();
					}
					// chatframe already opened
					else {
						ChatFrame chat = Global.chatFrame.get(sender);
						String msgToDisplay = time+"\n"+recv[2]+"\n";
						chat.msgArea.append(msgToDisplay);
					}
				}
			}
			catch(Exception e) {System.out.println(e);}
		}
	}
	
	public void addComponent(JComponent component, int[] pos) {
		GridBagConstraints cons = new GridBagConstraints();
		cons.weightx = 100; cons.weighty = 100;
		cons.gridx = pos[0];
		cons.gridy = pos[1];
		cons.gridwidth = pos[2];
		cons.gridheight = pos[3];
		Global.contactPanel.add(component,cons);
	}
	
	class chatListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			int ind = Global.contactList.getMinSelectionIndex();
			if(ind != -1) {
				currChatObj = Global.contacts[ind];
			}
		}
	}
	
}
