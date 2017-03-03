import java.net.*;

public class MessageReceiver implements Runnable{
	private DatagramSocket clientSocket;
	private byte[] buffer;
	public MessageSender mSender;
	private Thread st;
	
	public MessageReceiver(DatagramSocket s,String name,int serverport,InetAddress IP) {
		clientSocket = s;
		buffer = new byte[1024];
		mSender = new MessageSender(clientSocket,name,serverport,IP);
		st = new Thread(mSender);
		//st.start();
	}
	
	public void run() {
		st.start();
		while(true) {
			if(!mSender.running) break;
			try {
				DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
				clientSocket.receive(incoming);
				String[] recv = new String(incoming.getData(),0,incoming.getLength()).split("//");
				// update local user table
				if(recv[0].equals("userInfo")) {
					int pos = 1;
					while(pos < recv.length) {
						String[] tmp = new String[3];
						for(int i = pos+1; i < pos+4; i++) {
							tmp[(i-2)%4] = recv[i];
						}
						mSender.allUser.put(recv[pos], tmp);
						pos += 4;
					}
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
					System.out.println(recv[1]);
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
					System.out.println(recv[1]);
					int port = incoming.getPort();
					InetAddress ip = incoming.getAddress();
					String send = "client-ack//"+mSender.userName;
					mSender.sendMessage(send,port,ip);
				}
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
	}

}
