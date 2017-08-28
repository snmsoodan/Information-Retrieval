
import java.io.*;
import java.util.*;

public class PageRank {
	
	public class Page
	{
		String document;
		HashSet<String> inLinks=new HashSet<String>();
		HashSet<String> outLinks=new HashSet<String>();
		Double rank;
		Double newRank;
		Page(String doc){
			this.document=doc;
			this.rank=0.0;
			this.newRank=0.0;
		}
	}
	
	ArrayList<Double> perpArray=new ArrayList<Double>();
	HashMap<String,Page> pages=new HashMap<String,Page>();
	HashSet<String> sinkPages=new HashSet<String>();
	double initialRank=0;
	double damping=0.85;
	public void loadPages(String path)
	{
		try 
		{
			File f=new File("C:/Users/Snm/Desktop/HW4/wt2g_inlinks.txt");
			Scanner in=new Scanner(f.getCanonicalFile());
			while(in.hasNextLine())
			{
				String[] line=in.nextLine().split(" ");
				String docName=line[0];
				Page newPage;
				
				if(pages.containsKey(docName))
					newPage=pages.get(docName);
				else
					newPage=new Page(docName);
				
				for(int i=1;i<line.length;i++)
				{
					if(!docName.equals(line[i]))
					{
					newPage.inLinks.add(line[i]);
					Page newInPage;
					if(pages.containsKey(line[i]))
						newInPage=pages.get(line[i]);
					else
						newInPage=new Page(line[i]);
					
					newInPage.outLinks.add(docName);
					pages.put(line[i], newInPage);
					}
				}
				pages.put(docName, newPage);
			}
			in.close();	
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void init()
	{
		initialRank=1.0/pages.size();
		for(Page p : pages.values())
			p.rank=initialRank;
	}

	
	public void loadSinkPages()
	{
		for(Page p: pages.values())
		{
			if(p.outLinks.size()==0)
				sinkPages.add(p.document);
		}
	}
	
	public double perplexity()
	{
		double entropy=0.0;
		for(Page p:pages.values())
		{
			entropy+=p.rank*Math.log(p.rank)/Math.log(2.0);
		}
		return Math.pow(2.0, -1*entropy);	
	}
	
	//Function to find out if the preplexity values have converged upto units place or not.
	public boolean isConverged()
	{
		int len=perpArray.size();
		return (len>3 && (Math.abs(perpArray.get(len-2)-perpArray.get(len-1))<1 &&
						  Math.abs(perpArray.get(len-3)-perpArray.get(len-2))<1 &&
						  Math.abs(perpArray.get(len-4)-perpArray.get(len-3))<1));
	}
	
	public void updateRanks()
	{
		int count=0;
		while(!isConverged())
		{
			count++;
			double perp=perplexity();
			
			perpArray.add(perp);
			double sinkPR=0.0;
			double N=pages.size();
			
			for(String p : sinkPages){
				sinkPR+=pages.get(p).rank;
			}
			for(Page p : pages.values()){
				p.newRank=(1.0-damping)/N;
				p.newRank+=damping*sinkPR/N;
				for(String inLink:p.inLinks){
					double L=pages.get(inLink).outLinks.size();
					p.newRank+=damping*pages.get(inLink).rank/L;
				}
			}
			
			for(Page p:pages.values()){
				p.rank=p.newRank;
			}
		}	
//		System.out.println(count);
	}
	
	public void sortAndWrite()
	{
		List<Page> list = new LinkedList<Page>(pages.values());

		Collections.sort(list,new Comparator<Page>(){
			public int compare(Page p1, Page p2) {
				return p2.rank.compareTo(p1.rank);
			}
		});
		logAllPages("Pages sorted according to their PageRanks:",list,"C:/Users/Snm/Desktop/HW4/merged_final.txt");
	}
	
	public static void logAllPages(String header, List<Page> list, String fileName)
	{
		FileWriter fw = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile());
			fw.write(header+System.lineSeparator());
			for(Page page : list){
//				fw.write(page.document+" : "+page.rank+" : "+page.inLinks.size());
				fw.write(page.document+" : "+page.rank);
				fw.write(System.lineSeparator());
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try { fw.close(); } catch (IOException ignore) {}
		}
	}
}