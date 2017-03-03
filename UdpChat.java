import java.net.*;
// main function entry
public class UdpChat {
	public static void main(String[] args) throws Exception{
		// args[5]: mode(-c),nickname,server IP,server port,client port
		// client side
		if(args.length == 5 && args[0].equals("-c")) {
			DatagramSocket s = new DatagramSocket(Integer.parseInt(args[4]));
			String name = args[1];
			int serverport = Integer.parseInt(args[3]);
			InetAddress serverip = InetAddress.getByName(args[2]);
			String reg = "reg,"+args[1];
			DatagramPacket out = new DatagramPacket(reg.getBytes(),reg.getBytes().length,
					serverip,serverport);
			s.send(out);
			MessageReceiver r = new MessageReceiver(s,name,serverport,serverip);
			Thread rt = new Thread(r);
			rt.start();
			while(true) {
				if(!r.mSender.running) break;
				Thread.sleep(1000);
			}
			System.exit(-1);
		}
		// args[2]: mode(-s),server port
		// server side
		if(args.length == 2 && args[0].equals("-s")) {
			Server server = new Server(Integer.parseInt(args[1]));
			server.ReceiveMSG(Integer.parseInt(args[1]));
		}
	}

}
