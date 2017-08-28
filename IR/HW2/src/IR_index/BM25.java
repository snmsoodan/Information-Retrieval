package IR_index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.lang.Object;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class BM25 {
	
	public static void main(String[] args) throws NumberFormatException, IOException{
		
		double totallen=0.0;
	    int queryno = -1;
	    String line1="";
	    String line="";
	    File mFile1 = new File("C:/Users/Snm/workspace/HW2/doclengthmap.txt");
		FileInputStream fis1 = new FileInputStream(mFile1);
		File docfile = new File("C:/Users/Snm/workspace/HW2/documentidmap.txt");
		File cFile2 = new File("C:/Users/Snm/workspace/HW2/merged/mergedcatalog_72.txt");
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(mFile1)));
		HashMap<String,Integer> DocLengthMap = new HashMap<String,Integer>();
		RandomAccessFile raf1 = new RandomAccessFile("C:/Users/Snm/workspace/HW2/merged/mergedlist_72.txt", "r");
		FileInputStream docfis = new FileInputStream(docfile);
		BufferedReader docbr2 = new BufferedReader(new InputStreamReader(new FileInputStream(docfile)));
		FileInputStream fis2 = new FileInputStream(cFile2);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(cFile2)));
		Map<String, IntTriple> Catalog=new HashMap<String,IntTriple>();
		HashMap<String,IntTuple> wordmap2=new HashMap<String,IntTuple>();
		HashMap<Integer,String> docidmap=new HashMap<Integer,String>();
		PrintWriter okapiwriter = new PrintWriter("C:/Users/Snm/Desktop/HW2/BM25.txt", "UTF-8");
		
		while((line1 = br1.readLine()) != null)
		{
		String[] params= line1.split("\\|");
		
		
		for(int count=3;(count+2)<=params.length;)
		{
			//System.out.println(queryparams[count]+""+queryparams[count+1]+""+queryparams[count+2]+"");
			DocLengthMap.put(params[count],Integer.parseInt(params[count+1]));
			count=count+2;
		}
		
		}
		for (String key: DocLengthMap.keySet())
		{
		totallen= totallen+DocLengthMap.get(key);
		}
		
		double avglen= totallen/DocLengthMap.size();
		while((line1 = docbr2.readLine()) != null)
		{
		String[] params= line1.split("\\|");
		for(int count=1;(count+2)<=params.length;)
		{
			//System.out.println(params[count]+""+params[count+1]);
			docidmap.put(Integer.parseInt(params[count+1]),params[count]);
			count=count+2;
		}
		}
		
		
		
		while((line = br2.readLine()) != null)
		{
		String[] queryparams= line.split("\\|");
		
		
		for(int count=1;(count+3)<=queryparams.length;)
		{
			//System.out.println(queryparams[count]+""+queryparams[count+1]+""+queryparams[count+2]+"");
			IntTriple t1= new IntTriple(Integer.parseInt(queryparams[count+1]),Integer.parseInt(queryparams[count+2]),0);
			Catalog.put(queryparams[count],t1);
			count=count+3;
		}
		}
	

		

		for (String line3 : Files.readAllLines(Paths.get("C:/Users/Snm/Downloads/AP_DATA/modified queries.txt"))) 
		{
		    HashMap<String,Double> Rankedlist = new HashMap<String,Double>();		
		  
		    
			String[] queryparams= line3.split("\\s+");
			String Pattern = "(\\d+\\.)";
			
			for(String i: queryparams)
			{
				
				if(i.matches(Pattern))
				{
					queryno = Integer.parseInt(i.replace(".", ""));
					Rankedlist.clear();
					
			}
				else
				{
					//System.out.print("__"+i);
					if(Catalog.containsKey(i))
					{
					int length=Catalog.get(i).endoffset-(Catalog.get(i).startoffset);
					 byte[] mybyte= new byte[length];
					 raf1.seek(Catalog.get(i).startoffset);
					 raf1.readFully(mybyte);
					// System.out.print(Catalog.get(i).startoffset);
					 String inventry= new String(mybyte,"UTF-8");
					 String[] params1= inventry.split("\\|");
					 HashMap<String,Tuple> wordmap1=new HashMap<String,Tuple>();
					 int docfreqency=0;
					 for(int count=1;(count+3)<=params1.length;count=count+3)
					 {
					 docfreqency++;
					 }
//					 System.out.println(docfreqency);
					 for(int count=1;(count+3)<=params1.length;count=count+3)
					 {
						  Double okapitf=0.00;  
						  //System.out.println(params1[count]+"---"+params1[count+1]+"----"+docidmap.get(Integer.parseInt(params1[count])));
						 
						  int tfscore=Integer.parseInt(params1[count+1]);
						  String docid= docidmap.get(Integer.parseInt(params1[count]));
						  int doclen= DocLengthMap.get(docid);
						
						  //okapitf= tfscore/(tfscore+1.5+(0.5*doclen/totallen));
						  Double BM25=0.00;
						    Double term1 = (docidmap.size()+0.5)/(docfreqency+0.5);
						    Double term2 = (tfscore+(1.2*tfscore))/(tfscore+(1.2*(0.25+(0.75*(doclen/247.0)))));
						    Double term3 = ((tfscore*1.0)+(500*tfscore))/(tfscore+500);
						    BM25= Math.log(term1*term2*term3);
						  if(Rankedlist.containsKey(docid))
							{
								Rankedlist.put(docid, Rankedlist.get(docid)+BM25);
							}
							else
							{ 
								
								Rankedlist.put(docid, BM25);
							}
						 //wordmap1.put(params1[count], t1);
				     }
					 		
					
				
			}
				
			
	}
			HashMap<String,Double> SortedMap= (HashMap<String, Double>) sortMapByValues(Rankedlist);
			int count=0;
			
			for(String key:SortedMap.keySet()){
				count++;
				if(count<=1000)
				{
				okapiwriter.println(queryno+"\t Q0"+"\t"+key+"\t"+count+"\t"+ SortedMap.get(key)+"\t BM25");
				}
				
				

	
			}
			
			

	}
			
			
			
	}
		okapiwriter.close();

}
 private static HashMap<String, Double> sortMapByValues(Map<String, Double> aMap) {
        
        Set<Entry<String,Double>> mapEntries = aMap.entrySet();
        
        // used linked list to sort, because insertion of elements in linked list is faster than an array list. 
        List<Entry<String,Double>> aList = new LinkedList<Entry<String,Double>>(mapEntries);

        // sorting the List
        Collections.sort(aList, new Comparator<Entry<String,Double>>() {

            @Override
            public int compare(Entry<String, Double> ele1,
                    Entry<String, Double> ele2) {
                
                return ele2.getValue().compareTo(ele1.getValue());
            }
        });
        
        // Storing the list into Linked HashMap to preserve the order of insertion. 
        Map<String,Double> aMap2 = new LinkedHashMap<String, Double>();
        for(Entry<String,Double> entry: aList) {
            aMap2.put(entry.getKey(), entry.getValue());
        }
      
            return (HashMap<String, Double>) aMap2;
        }

}
