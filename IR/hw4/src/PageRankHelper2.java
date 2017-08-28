

public class PageRankHelper2 {
	
	public static void main(String[] args) {
		printOutlinks head=new printOutlinks();
		
		head.loadPages(".");
		System.out.println("print top five perplexities");
		System.out.println(head.perpArray.get(0));
		System.out.println(head.perpArray.get(1));
		System.out.println(head.perpArray.get(2));
		System.out.println(head.perpArray.get(3));
		System.out.println(head.perpArray.get(4));
		
	}
}
