import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.*;

public class FallBrick implements Runnable {
	public int[] xPos = new int[]{0,125,250,375};
	public int[] brickPos = new int[]{0,0,0,0};
	public int[] brickLevel = new int[]{0,0,0,0};
	public List<List<Boolean>> isVisible = new ArrayList<List<Boolean>>();
	public Brick brick;
	public boolean ready;
	public boolean end;
	public int score = 0;
	private int brickMaxPos = 0;
	private int col = Global.column;
	private int brickWidth = Global.brickWidth;
	private int brickHeight = Global.brickHeight;
	private int windowHeight = Global.windowHeight;
	
	public FallBrick() {
		brick = new Brick();
		Global.frame.getContentPane().add(brick);
		for(int i = 0; i < col; i++) isVisible.add(new ArrayList<>());
		ready = false;
	}
	
	public void run() {
		while(!ready) {
			try {Thread.sleep(10);}
			catch(Exception e) {}
		}
		end = false;
		while(true) {
			while(!end) {
				generate();
				updateMax();
				brick.repaint();
				try {Thread.sleep(Global.timeInt);}
				catch(Exception e) {}
				end = !(brickMaxPos+4*brickHeight <= windowHeight);
			}	
			endFrame();
			while(end) {
				try {Thread.sleep(100);}
				catch(Exception e) {}
			}
		}
	}
	
	public void generate() {
		if(!end) {
			// randomly choose which column to not appear
			int invisibleInd = (int)(Math.random()*col);
			for(int i = 0; i < col; i++) {
				if(i == invisibleInd) {
					isVisible.get(i).add(0,false);
				}
				else {
					isVisible.get(i).add(0,true);
					brickPos[i] = brickLevel[i]*brickHeight + brickHeight;
				}
				brickLevel[i] = brickLevel[i]+1;
			}
		}
	}
	
	public void compensate(int ind) {
		while(brickLevel[ind] > 0) {
			if(isVisible.get(ind).get(brickLevel[ind]-1)) break;
			isVisible.get(ind).remove(brickLevel[ind]-1);
			brickLevel[ind] = brickLevel[ind]-1;
		}
		isVisible.get(ind).add(true);
		brickPos[ind] = brickPos[ind] + brickHeight;
		brickLevel[ind] = brickLevel[ind]+1;
		int pos = isVisible.get(ind).size()-1;
		
		if(allExist(pos)) {
			score++;
			if(score%10 == 0 && Global.timeInt > 300) Global.timeInt -= 100;
			for(int i = 0; i < col; i++) {
				erase(i,pos);
			}
		}
		updateMax();
		brick.repaint();
	}
	
	public void erase(int ind,int pos) {
		brickLevel[ind] = brickLevel[ind]-1;
		brickPos[ind] = brickPos[ind]-brickHeight;
		isVisible.get(ind).remove(pos);
		while(isVisible.get(ind).size() > 0) {
			if(isVisible.get(ind).get(isVisible.get(ind).size()-1)) return;
			isVisible.get(ind).remove(isVisible.get(ind).size()-1);
			brickLevel[ind] = brickLevel[ind]-1;
			brickPos[ind] = brickPos[ind]-brickHeight;
		}
	}
	
	public boolean allExist(int pos) {
		for(int i = 0; i < col; i++) {
			if(pos >= isVisible.get(i).size() || !isVisible.get(i).get(pos)) return false;
		}
		return true;
	}
	
	public void updateMax() {
		int max = -1;
		for(int i = 0; i < col; i++) {
			max = Math.max(max,brickPos[i]);
		}
		brickMaxPos = max;
	}
	
	public void endFrame() {
		String score = Integer.toString(this.score);
		
		Global.scoreFrame = new JFrame("Game Over");
		Global.scoreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Global.scoreFrame.setAlwaysOnTop(true);
		
		JLabel scoreLabel = new JLabel("Your Score: "+score);
		JButton retryButton = new JButton("re-try");
		JButton exitButton = new JButton("Exit");
		retryButton.addActionListener(new retryListener());
		exitButton.addActionListener(new exitListener());
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.add(scoreLabel);
		panel.add(retryButton);
		panel.add(exitButton);
		Global.scoreFrame.getContentPane().add(BorderLayout.CENTER,panel);
		Global.scoreFrame.setSize(200,200);
		Global.scoreFrame.setVisible(true);
	}
	
	class Brick extends JPanel {
		private static final long serialVersionUID = 1L;
		public void paintComponent(Graphics g) {
			g.setColor(Color.blue);
			for(int i = 0; i < col; i++) {
				for(int j = 0; j < brickLevel[i]; j++) {
					if(isVisible.get(i).get(j)) {
						g.fillRect(xPos[i],j*brickHeight,brickWidth,brickHeight);
					}
				}
			}
		}
	}
	
	class retryListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			score = 0;
			end = false;
			brickPos = new int[]{0,0,0,0};
			brickLevel = new int[]{0,0,0,0};
			isVisible = new ArrayList<List<Boolean>>();
			for(int i = 0; i < col; i++) isVisible.add(new ArrayList<>());
			brick.repaint();
			Global.scoreFrame.dispose();
			Global.timeInt = 1000;
		}
	}
	
	class exitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Global.scoreFrame.dispose();
			Global.frame.dispose();
			System.exit(-1);
		}
	}
}
