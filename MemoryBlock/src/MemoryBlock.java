import java.util.*;

public class MemoryBlock {
	public int blockSize;
	public int blockNum;
	public List<String> blockOrder;
	public List<String> blockOrder2;
	public boolean rev;
	
	public MemoryBlock(int blockSize, int blockNum, boolean rev) {
		this.blockSize = blockSize;
		this.blockNum = blockNum;
		this.rev = rev;
	}
	
	public ArrayList<String> getBlockOrder() {
		return new ArrayList<>(this.blockOrder);
	}
	
	public void setNum(int blockNum) {
		this.blockNum = blockNum;
	}
	
	public void generate() {
		this.blockOrder = new ArrayList<>();
		List<Integer> tmp = new ArrayList<>();
		for(int i = 0; i < blockSize*blockSize; i++) {tmp.add(i);}
		int cnt = 0;
		while(tmp.size() > 0 && cnt < blockNum) {
			int rand = (int)(tmp.size()*Math.random());
			int x = tmp.get(rand)/3;
			int y = tmp.get(rand)%3;
			if(rev) {
				blockOrder.add(0,Integer.toString(x)+","+Integer.toString(y));
			}
			else {
				blockOrder.add(Integer.toString(x)+","+Integer.toString(y));
			}
			tmp.remove(rand);
			cnt++;
		}
	}

}
