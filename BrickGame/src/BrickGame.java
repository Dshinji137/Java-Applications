import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.*;

public class BrickGame {
	JButton bt1; JButton bt2; JButton bt3; JButton bt4;
	public int windowWidth = Global.windowWidth;
	public int windowHeight = Global.windowHeight;
	public int brickWidth = Global.brickWidth;
	public int brickHeight = Global.brickHeight;
	public int col = Global.column;
	public FallBrick fallBrick;
	public JPanel startPanel;
	private Thread ft;
	
	public static void main(String[] args) {
		BrickGame gui = new BrickGame();
		gui.begin();
	}
	
	public void begin() {
		Global.frame = new JFrame();
		Global.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		startPanel = new JPanel();
		startPanel.setLayout(new GridBagLayout());
		JLabel title = new JLabel("Brick Game");
		Font font1 = new Font("Serif",Font.BOLD+Font.ITALIC,36);
		Font font2 = new Font("Serif",Font.ITALIC,15);
		title.setFont(font1);
		JButton startButton = new JButton("start");
		startButton.setFont(font2);
		startButton.addActionListener(new startListener());
		addComponent(title,new int[]{0,1,1,1});
		addComponent(startButton,new int[]{0,2,1,1});
		
		Global.frame.getContentPane().add(BorderLayout.CENTER,startPanel);
		
		Global.frame.setSize(windowWidth,windowHeight);
		Global.frame.setVisible(true);
	}
	
	public void go() {
		if(Global.frame != null && Global.frame.isVisible()) Global.frame.dispose();
		if(Global.scoreFrame != null && Global.scoreFrame.isVisible()) Global.scoreFrame.dispose();
		
		Global.frame = new JFrame();
		Global.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel cPanel = new JPanel();
		cPanel.setLayout(new GridLayout(1,4));
		bt1 = new JButton("chanel1");
		bt2 = new JButton("chanel2");
		bt3 = new JButton("chanel3");
		bt4 = new JButton("chanel4");
		bt1.addActionListener(new compListener());
		bt2.addActionListener(new compListener());
		bt3.addActionListener(new compListener());
		bt4.addActionListener(new compListener());
		cPanel.add(bt1); 
		cPanel.add(bt2);
		cPanel.add(bt3);
		cPanel.add(bt4);
		
		fallBrick = new FallBrick();
		ft = new Thread(fallBrick);
		ft.start();
		
		Global.frame.getContentPane().add(BorderLayout.SOUTH,cPanel);
		
		Global.frame.setSize(windowWidth,windowHeight);
		Global.frame.setVisible(true);
		fallBrick.ready = true;
	}
	
	public void addComponent(JComponent component, int[] pos) {
		GridBagConstraints cons = new GridBagConstraints();
		cons.weightx = 100; cons.weighty = 100;
		cons.gridx = pos[0];
		cons.gridy = pos[1];
		cons.gridwidth = pos[2];
		cons.gridheight = pos[3];
		startPanel.add(component,cons);
	}
	
	class startListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			go();
		}
	}
	
	class quitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Global.scoreFrame.dispose();
			Global.frame.dispose();
			System.exit(-1);
		}
	}
	
	class compListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(!fallBrick.end) {
				JButton buttonClicked = (JButton) e.getSource();
				String content = buttonClicked.getText();
				int ind = Integer.parseInt(content.substring(content.length()-1)) - 1;
				fallBrick.compensate(ind);
			}
		}
	}
	
}
