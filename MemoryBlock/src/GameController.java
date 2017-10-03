import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import java.util.*;

public class GameController {
	public JFrame mainFrame;
	public JFrame gameFrame;
	public int blockSize;
	public int blockNum;
	public int[] offSet = new int[]{50,40};
	public int interval = 50;
	public int blockDim = 100;
	public int score = 0;
	public int allRight = 0;
	public boolean rev;
	public View view;
	public MemoryBlock game;
	public List<Rectangle> rectList;
	public List<String> blockList;
	
	public GameController(int initSize, int initNum, boolean rev) {
		this.blockSize = initSize;
		this.blockNum = initNum;
		this.mainFrame = new JFrame();
		this.gameFrame = new JFrame();
		this.rev = rev;
		this.game = new MemoryBlock(initSize,initNum,rev);
		game.generate();
		this.blockList = game.getBlockOrder();
		generateRect();
		this.view = new View(new ArrayList<>(blockList),blockSize,blockNum,mainFrame,gameFrame,rev);
		this.gameFrame.addMouseListener(new MouseHandler());
	}
	
	public void generateRect() {
		blockList = game.getBlockOrder();
		rectList = new ArrayList<>();
		for(int i = 0; i < blockList.size(); i++) {
			String[] tmp = blockList.get(i).split(",");
			int row = Integer.valueOf(tmp[0]);
			int col = Integer.valueOf(tmp[1]);
			int x = offSet[0]+(col-0)*(blockDim+interval);
			int y = offSet[1]+(row-0)*(blockDim+interval);
			Rectangle rect = new Rectangle();
			rect.setBounds(x,y,x+blockDim,y+blockDim);
			rectList.add(rect);
		}
	}
	
	public void check(Point p) {
		// right order
		if(rectList.get(0).contains(p)) {
			rectList.remove(0);
			blockList.remove(0);
			view.setDrawList(new ArrayList<>(blockList));
			// finish it all right
			if(rectList.size() == 0) {
				allRight++;
				score += blockNum;
				if(allRight%3 == 0 && blockNum < blockSize*blockSize) {
					blockNum++;
					game.setNum(blockNum);
					view.setNum(blockNum);
				}
				game.generate();
				generateRect();
				view.setDrawList(new ArrayList<>(blockList));
				
			}
			return;
		}
		else {
			for(int i = 1; i < rectList.size(); i++) {
				// wrong order
				if(rectList.get(i).contains(p)) {
					game.generate();
					blockList = new ArrayList<>(game.getBlockOrder());
					view.setDrawList(new ArrayList<>(blockList));
					generateRect();
					return;
				}
			}
		}
	}
	
	class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			Point p = e.getPoint();
			check(p);
		}
	}
	
}
