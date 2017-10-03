import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AppearBlock implements Runnable {
	public DrawPanel gamePanel;
	public JFrame gameFrame;
	public List<String> drawList;
	public int[] blockPos = new int[2];
	public int[] offSet = new int[]{50,40};
	public int len;
	public int interval = 50;
	public int blockDim = 100;
	public int blockNum = 5;
	public int matDim;
	public boolean next = false;
	public boolean end = false;
	public boolean rev;
	public Color blockColor;
	
	public AppearBlock(List<String> blockOrder,JFrame gameFrame,boolean rev) {
		this.drawList = new ArrayList<>(blockOrder);
		this.gamePanel = new DrawPanel();
		this.gameFrame = gameFrame;
		this.rev = rev;
		this.blockColor = rev? Color.red : Color.blue;
		this.len = 0;
	}
	
	public void setDrawList(ArrayList<String> list) {
		// next
		if(list.size() == blockNum) {
			len = 0;
			drawList = new ArrayList<>(list);
			next = true;
		}
		// right, decrease by 1
		else {
			drawList = new ArrayList<>(list);
			len = drawList.size();
			gamePanel.repaint();
		}
	}
	
	public boolean isNext() {
		return this.next;
	}
	
	public void run() {
		gameFrame.getContentPane().add(BorderLayout.CENTER,gamePanel);
		while(true) {
			// a new round, display block
			while(len <= blockNum) {
				gamePanel.repaint();
				try {Thread.sleep(Global.appearInt);}
				catch(Exception e) {}
				len++;
			}
			// wait for solve
			while(true) {
				try {Thread.sleep(5);}
				catch(Exception e) {}
				boolean next = isNext(); 
				if(next) {
					this.next = false;
					break;
				}
			}
		}
	}
	
	class DrawPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		public void paint(Graphics g) {
			g.setColor(blockColor);
			if(rev) {
				for(int i = drawList.size()-1; i >= drawList.size()-len; i--) {
					String[] tmp = drawList.get(i).split(",");
					int row = Integer.valueOf(tmp[0]);
					int col = Integer.valueOf(tmp[1]);
					blockPos[0] = offSet[0] + (col-0)*(blockDim+interval);
					blockPos[1] = offSet[1] + (row-0)*(blockDim+interval);
					g.fillRect(blockPos[0],blockPos[1],blockDim,blockDim);
				}
			}
			else {
				for(int i = 0; i < len; i++) {
					String[] tmp = drawList.get(i).split(",");
					int row = Integer.valueOf(tmp[0]);
					int col = Integer.valueOf(tmp[1]);
					blockPos[0] = offSet[0] + (col-0)*(blockDim+interval);
					blockPos[1] = offSet[1] + (row-0)*(blockDim+interval);
					g.fillRect(blockPos[0],blockPos[1],blockDim,blockDim);
				}
			}
		}
	}
}
