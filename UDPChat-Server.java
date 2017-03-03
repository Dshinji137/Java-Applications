import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
	private HashMap<String,String[]> users = new HashMap<String,String[]>();
	private HashMap<String,ArrayList<String>> offLine = new HashMap<String,ArrayList<String>>();
	private ArrayList<String> userID = new ArrayList<String>();
	// listening socket
	private DatagramSocket serverSocket;
	// send socket
	private DatagramSocket sendSocket;
	
	public Server(int serverPort) {
		try {
			serverSocket = new DatagramSocket(serverPort);
			sendSocket = new DatagramSocket(1500);
			System.out.println("Server socket created, waiting for incoming data");
		}
		catch(IOException e) {
			System.err.println("IOException"+e);
		}
	}
	
	public String broadcast() {
		String userInfo = "userInfo//";
		for(int i = 0; i < userID.size(); i++) {
			String id = userID.get(i);
			userInfo = userInfo + id + "//";
			for(int j = 0; j < 3; j++) {
				userInfo = userInfo + users.get(id)[j];
				if(i != userID.size()-1 || j != 2) userInfo = userInfo + "//";
			}
		}
		
		return userInfo;
	}
	
	public void saveMessage(String receiver,String message,InetAddress senderIP,int senderPort) {
		if(!offLine.containsKey(receiver)) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(message);
			offLine.put(receiver, list);
		}
		else {
			ArrayList<String> list = offLine.get(receiver);
			list.add(message);
			offLine.put(receiver, list);
		}
		// message save confirm
		String send = "messageSaved";
		DatagramPacket out = new DatagramPacket(send.getBytes(),
				send.getBytes().length,senderIP,senderPort);
		try {
			sendSocket.send(out);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
	
	public void ReceiveMSG(int serverPort) {
		byte[] buffer = new byte[65536];
		DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
		
		while(true) {
			try {
				serverSocket.receive(incoming);
				byte[] received = incoming.getData();
				String[] msg = new String(received,0,incoming.getLength()).split(",");
				String whatToDo = msg[0];
				// reg
				if(whatToDo.equals("reg")) {
					String userName = msg[1];
					InetAddress senderIP = incoming.getAddress();
					int senderPort = incoming.getPort();
					String status = "on";
					// Not registered yet
					if(!users.containsKey(userName)) {
						// store data in table
						users.put(userName, new String[]{senderIP.getHostAddress(),
								Integer.toString(senderPort),status});
						userID.add(userName);
						// send ACK to register user
						String ACK = "reg-ack";
						DatagramPacket out = new DatagramPacket(ACK.getBytes(),ACK.getBytes().length,
								senderIP,senderPort);
						sendSocket.send(out);
					}
					// already registered
					else {
						// store data in table
						users.put(userName, new String[]{senderIP.getHostAddress(),
								Integer.toString(senderPort),status});
						// send ACK to register user
						String ACK = "al-reg-ack";
						DatagramPacket out = new DatagramPacket(ACK.getBytes(),ACK.getBytes().length,
								senderIP,senderPort);
						sendSocket.send(out);
						// if there is offline chat for the user, send them all and clear
						if(offLine.containsKey(userName)) {
							String notify = "HaveMessage";
							out = new DatagramPacket(notify.getBytes(),notify.getBytes().length,
									senderIP,senderPort);
							sendSocket.send(out);
							ArrayList<String> list = offLine.get(userName);
							for(int i = 0; i < list.size(); i++) {
								String send = "OffLine//"+list.get(i);
								out = new DatagramPacket(send.getBytes(),send.getBytes().length,
									senderIP,senderPort);
								sendSocket.send(out);
							}
							offLine.remove(userName);
						}
					}
					// broadcast user tables to all users
					String userInfo = broadcast();
					for(int i = 0; i < userID.size(); i++) {
						String id = userID.get(i);
						InetAddress IP = InetAddress.getByName(users.get(id)[0]);
						int port = Integer.parseInt(users.get(id)[1]);
						DatagramPacket bc = new DatagramPacket(userInfo.getBytes(),
								userInfo.getBytes().length,IP,port);
						sendSocket.send(bc);
					}
				}
				// de-register
				if(whatToDo.equals("dereg")) {
					String userName = msg[1];
					InetAddress senderIP = incoming.getAddress();
					int senderPort = Integer.parseInt(users.get(userName)[1]);
					if(!users.containsKey(userName)) {
						String Nack = "dreg-nack";
						DatagramPacket out = new DatagramPacket(Nack.getBytes(),Nack.getBytes().length,
								senderIP,senderPort);
						sendSocket.send(out);
					}
					else {
						// update in table
						String status = "off";
						users.put(userName, new String[]{senderIP.getHostAddress(),
								Integer.toString(senderPort),status});
						// send ACK to register user
						String ACK = "dreg-ack";
						DatagramPacket out = new DatagramPacket(ACK.getBytes(),ACK.getBytes().length,
								senderIP,senderPort);
						sendSocket.send(out);
						// broadcast user tables to all users
						String userInfo = broadcast();
						for(int i = 0; i < userID.size(); i++) {
							String id = userID.get(i);
							InetAddress IP = InetAddress.getByName(users.get(id)[0]);
							int port = Integer.parseInt(users.get(id)[1]);
							DatagramPacket bc = new DatagramPacket(userInfo.getBytes(),
									userInfo.getBytes().length,IP,port);
							sendSocket.send(bc);
						}
					}
				}
				if(whatToDo.equals("Offline")) {
					// offline, senderName, receiverName, message
					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date now = new Date();
					String time = sdfDate.format(now);
					String receiver = msg[2];
					String message = msg[1]+": "+time + " " + msg[3];
					int senderPort = incoming.getPort();
					InetAddress senderIP = incoming.getAddress();
					// server finds the receiver on
					if(users.get(receiver)[2].equals("on")) {
						// check if the receiver is on
						String check = "StillOn";
						InetAddress receiverIP = InetAddress.getByName(users.get(receiver)[0]); 
						int receiverPort = Integer.parseInt(users.get(receiver)[1]);
						DatagramPacket out = new DatagramPacket(check.getBytes(),
								check.getBytes().length,receiverIP,receiverPort);
						sendSocket.send(out);
						DatagramPacket feed = new DatagramPacket(buffer,buffer.length);
						serverSocket.receive(feed);
						byte[] back = feed.getData();
						String resp = new String(back,0,feed.getLength());
						// receiver does respond, it indeed exists
						if(resp.equals("yes")) {
							String send = "RecvEx//"+receiver;
							out = new DatagramPacket(send.getBytes(),
									send.getBytes().length,senderIP,senderPort);
							sendSocket.send(out);
						}
						// receiver does not respond, update table, broadcast and save message.
						else {
							users.put(receiver, new String[]{users.get(receiver)[0],
									users.get(receiver)[1],"off"});
							String userInfo = broadcast();
							for(int i = 0; i < userID.size(); i++) {
								String id = userID.get(i);
								InetAddress IP = InetAddress.getByName(users.get(id)[0]);
								int port = Integer.parseInt(users.get(id)[1]);
								DatagramPacket bc = new DatagramPacket(userInfo.getBytes(),
										userInfo.getBytes().length,IP,port);
								sendSocket.send(bc);
							}
							saveMessage(receiver,message,senderIP,senderPort);
						}
					}
					// server finds the receiver offline
					else {
						saveMessage(receiver,message,senderIP,senderPort);
					}
				}
			}
			catch(IOException e) {
				System.err.println("IOException"+e);
			}
		}
	}
}
