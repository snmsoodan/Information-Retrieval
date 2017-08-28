

public class PageRankHelper {
	
	public static void main(String[] args) {
		PageRank head=new PageRank();
		
		head.loadPages(".");
		head.loadSinkPages();
		head.init();
		
		head.updateRanks();
	
		head.sortAndWrite();
		
		
		
	}
}
