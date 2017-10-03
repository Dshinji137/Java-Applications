import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

public class View {
	public JFrame mainFrame;
	public JFrame gameFrame;
	public JPanel mainPanel;
	public int windowWidth = Global.windowWidth;
	public int windowHeight = Global.windowHeight;
	public int blockSize;
	public int blockNum;
	public boolean rev;
	public ArrayList<String> drawList;
	public AppearBlock ap;
	public Thread at;
	
	public View(ArrayList<String> list,int blockSize,int blockNum,JFrame mainFrame,JFrame gameFrame,boolean rev) {
		this.drawList = new ArrayList<>(list);
		this.blockSize = blockSize;
		this.blockNum = blockNum;
		this.mainFrame = mainFrame;
		this.gameFrame = gameFrame;
		this.rev = rev;
		main();
	}
	
	public void main() {
		if(gameFrame != null && gameFrame.isVisible()) gameFrame.dispose();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		Font font1 = new Font("Serif",Font.BOLD+Font.ITALIC,36);
		Font font2 = new Font("Serif",Font.ITALIC,15);
		JLabel title = new JLabel("Memory Block");
		title.setFont(font1);
		JButton startButton = new JButton("start");
		startButton.setFont(font2);
		startButton.addActionListener(new startListener());
		addComponent(mainPanel,title,new int[]{0,1,1,1});
		addComponent(mainPanel,startButton,new int[]{0,2,1,1});
		
		mainFrame.getContentPane().add(mainPanel);
		mainFrame.setSize(windowWidth,windowHeight);
		mainFrame.setLocation(400,100);
		mainFrame.setVisible(true);
	}
	
	public void go() {
		mainFrame.dispose();
		gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		gameFrame.setSize(windowWidth,windowHeight);
		gameFrame.setLocation(400,100);
		gameFrame.setVisible(true);
		
		ap = new AppearBlock(drawList,gameFrame,rev);
		ap.blockNum = blockNum;
		ap.setDrawList(this.drawList);
		at = new Thread(ap);
		at.start();
	}
	
	public void setDrawList(ArrayList<String> list) {
		this.drawList = new ArrayList<>(list);
		ap.setDrawList(this.drawList);
	}
	
	public void setNum(int blockNum) {
		this.blockNum = blockNum;
		ap.blockNum = blockNum;
	}
	
	public void addComponent(JPanel panel, JComponent component, int[] pos) {
		GridBagConstraints cons = new GridBagConstraints();
		cons.weightx = 100; cons.weighty = 100;
		cons.gridx = pos[0];
		cons.gridy = pos[1];
		cons.gridwidth = pos[2];
		cons.gridheight = pos[3];
		panel.add(component,cons);
	}
	
	class startListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			go();
		}
	}

}
