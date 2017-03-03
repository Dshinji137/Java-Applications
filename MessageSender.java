import java.io.*;
import java.net.*;
import java.util.*;

public class MessageSender implements Runnable{
	// local user table
	public HashMap<String,String[]> allUser = new HashMap<String,String[]>();
	public DatagramSocket clientSocket;
	public int serverPort;
	public InetAddress serverIP;
	public boolean regConfirm;
	public boolean deregConfirm;
	public boolean sendConfirm;
	public boolean saveConfirm;
	public boolean running;
	public String userName;
	
	MessageSender(DatagramSocket s,String name,int serverport,InetAddress IP) {
		clientSocket = s;
		userName = name;
		serverPort = serverport;
		serverIP = IP;
		regConfirm = false;
		deregConfirm = false;
		sendConfirm = false;
		saveConfirm = false;
		running = true;
	}
	
	public void sendMessage(String send,int destPort,InetAddress destIP) throws Exception {
		DatagramPacket out = new DatagramPacket(send.getBytes(),send.getBytes().length,
				destIP,destPort);
		this.clientSocket.send(out);
	}
	
	public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			try {
				while(!in.ready()) {
					Thread.sleep(100);
				}
				String[] message = in.readLine().split(" ");
				// reg
				if(message[0].equals("reg")) {
					sendMessage(message[0]+","+message[1],this.serverPort,this.serverIP);
					// wait for 500ms
					Thread.sleep(500);
					if(this.regConfirm) {
						this.regConfirm = false;
					}
					else {
						// try for 5 times
						int Time = 5;
						int cnt = 0;
						while(cnt < Time) {
							sendMessage(message[0]+","+message[1],this.serverPort,this.serverIP);
							Thread.sleep(500);
							if(this.regConfirm) {
								break;
							}
							cnt++;
						}
						if(this.regConfirm) this.regConfirm = false;
						else {
							System.out.println("Server not responding");
							System.out.println("Exiting");
							this.running = false;
							break;
						}
					}
				}
				// de-register
				if(message[0].equals("dereg")) {
					sendMessage(message[0]+","+message[1],this.serverPort,this.serverIP);
					// wait for 500ms
					Thread.sleep(500);
					if(this.deregConfirm) {
						this.deregConfirm = false;
					}
					else {
						// try for 5 times
						int Time = 5;
						int cnt = 0;
						while(cnt < Time) {
							System.out.println("sending to server attempt "+(cnt+1));
							sendMessage(message[0]+","+message[1],this.serverPort,this.serverIP);
							Thread.sleep(500);
							if(this.deregConfirm) break;
							cnt++;
						}
						if(this.deregConfirm) this.deregConfirm = false;
						else {
							System.out.println("Server not responding");
							System.out.println("Exiting");
							this.running = false;
							break;
						}
					}
				}
				// send message
				if(message[0].equals("send")) {
					String name = message[1];
					if(!allUser.containsKey(name)) {
						System.out.println("no such users!");
						continue;
					}
					else {
						// find it "on" from local table
						if(allUser.get(name)[2].equals("on")) {
							InetAddress ip = InetAddress.getByName(allUser.get(name)[0]);
							int port = Integer.parseInt(allUser.get(name)[1]);
							String toSend = "client-chat//"+this.userName+":"+message[2];
							sendMessage(toSend,port,ip);
							// wait for 500ms
							Thread.sleep(500);
							if(this.sendConfirm) {
								this.sendConfirm = false;
							}
							// no response from users, send to server
							else {
								System.out.println("No ACK from "+ name + " ,message send to server");
								toSend = "Offline,"+this.userName+","+message[1]+","+message[2];
								sendMessage(toSend,this.serverPort,this.serverIP);
								Thread.sleep(500);
								if(this.saveConfirm) {
									this.saveConfirm = false;
								}
								// no response from server, re-try 5 times
								else {
									int Time = 5;
									int cnt = 0;
									while(cnt < Time) {
										System.out.println("sending to server attempt "+(cnt+1));
										sendMessage(toSend,this.serverPort,this.serverIP);
										Thread.sleep(500);
										if(this.saveConfirm) break;
										cnt++;
									}
									if(this.saveConfirm) this.saveConfirm = false;
									else {
										System.out.println("Server not responding");
										this.running = false;
										break;
									}
								}
							}
						}
						// find it "off" from local table
						else {
							String toSend = "Offline,"+this.userName+","+message[1]+","+message[2];
							sendMessage(toSend,this.serverPort,this.serverIP);
							Thread.sleep(500);
							if(this.saveConfirm) this.saveConfirm = false;
							// no response from server, re-try 5 times.
							else {
								int Time = 5;
								int cnt = 0;
								while(cnt < Time) {
									System.out.println("sending to server attempt "+(cnt+1));
									sendMessage(toSend,this.serverPort,this.serverIP);
									Thread.sleep(500);
									if(this.saveConfirm) {
										break;
									}
									cnt++;
								}
								if(this.saveConfirm) this.saveConfirm = false;
								else {
									System.out.println("Server not responding");
									System.out.println("Exiting");
									this.running = false;
									break;
								}
							}
						}
					}
				}
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
	}

}
