
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;




public class RemoveDuplicates {
		
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
		
		HashMap<String,Page> pages=new HashMap<String,Page>();
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
						
						}
					}
					pages.put(docName, newPage);
				}
				
				FileWriter fw = null;
				File file = new File("C:/Users/Snm/Desktop/HW4/wt2g_inlinksWduplicates.txt");
				
				if (!file.exists()) {
					file.createNewFile();
				}
				fw = new FileWriter(file.getAbsoluteFile());
//				Iterator<Entry<String, Page>> iter = pages.entrySet().iterator();
//				
//				while(iter.hasNext()){
//					Map.Entry pair = (Map.Entry)iter.next();
//					System.out.println(pair.getKey() + " = " + pair.getValue().);
//					break;
//				}	
				for (String key : pages.keySet()) {
//				    System.out.println("Key = " + key);
				    Page value = pages.get(key);
				    fw.write(key+" "+value.inLinks.toString()+"\n");
				   
				}

				// Iterating over values only
//				for (Page value : pages.values()) {
//				    System.out.println("Value = " + value.outLinks.toString());
//				   
//				}
				
				fw.close();
				in.close();	
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
}
