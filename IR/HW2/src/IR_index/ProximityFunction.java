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

public class ProximityFunction {
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
PrintWriter okapiwriter = new PrintWriter("C:/Users/Snm/Desktop/HW2/Proximity.txt", "UTF-8");

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




for (String line3 : Files.readAllLines(Paths.get("C:/Users/Snm/Downloads/AP_DATA/qp.txt"))) 
{
    HashMap<String,Double> Rankedlist = new HashMap<String,Double>();
    HashMap<String,HashMap<String,Tuple>> proximitymap = new HashMap<String,HashMap<String,Tuple>>();
    HashMap<String,Double> scores= new HashMap<String,Double>();
  
    
	String[] params1= line3.split("\\s+");
	String Pattern = "(\\d+\\.)";
	
	for(String i: params1)
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
			 String[] params2= inventry.split("\\|");
			 HashMap<String,Tuple> wordmap1=new HashMap<String,Tuple>();
			 int docfreqency=0;
			 for(int count=1;(count+3)<=params2.length;count=count+3)
			 {
			 docfreqency++;
			 }
			 for(int count=1;(count+3)<=params2.length;count=count+3)
			 { 
				  //System.out.println(params1[count]+"---"+params1[count+1]+"----"+docidmap.get(Integer.parseInt(params1[count])));
				 
				  int tfscore=Integer.parseInt(params2[count+1]);
				  String docid= docidmap.get(Integer.parseInt(params2[count]));
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
							  //System.out.println(params1[count]+"---"+params1[count+1]+"----"+docidmap.get(Integer.parseInt(params1[count])));
							  String s1=params2[count+2];
								 String replace = s1.replace("[","");
								 String replace1 = replace.replace("]","");
								 List<String> arrayList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
								 ArrayList<Integer> posList = new ArrayList<Integer>();
								 for(String fav:arrayList){
									 posList.add(Integer.parseInt(fav.trim()));
							 
							  HashMap<String,Tuple> hm= new HashMap<String,Tuple>();
							  
							 
							  if(proximitymap.containsKey(docid))
							    {
								  proximitymap.get(docid).put(i, new Tuple(posList, 0));
							    }
							  else
							  {
								  hm.put(i, new Tuple(posList,0));
							  proximitymap.put(docid,hm);
							  }
							  
							
							 
							 //wordmap1.put(params1[count], t1);
					     }
						 		
						
					
				}
					
		}

			    
			    
			    
					
	
		
				}
				
				
		}

			    
			    
			    for(String key: proximitymap.keySet())
			    {
			   
			    int score=minspan(proximitymap.get(key));
			    double proxi = (1500-(score*1.0))/180000;
			   // System.out.println(score);
			    scores.put(key,proxi);
			    }
			    for(String key: scores.keySet())
			    {
			    	scores.put(key,(Rankedlist.get(key)+scores.get(key)));
			    }
			    
				HashMap<String,Double> SortedMap= (HashMap<String, Double>) sortMapByValues(scores);
				int count=0;
				
				for(String key:SortedMap.keySet()){
					count++;
					if(count<=1000)
					{
					okapiwriter.println(queryno+"\t Q0"+"\t"+key+"\t"+count+"\t"+ SortedMap.get(key)+"\t Proxi");
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
	 public static int minspan(HashMap<String,Tuple> amap)
	    {
	        for(String key: amap.keySet())
	        {
	            amap.put(key,new Tuple(amap.get(key).positionList,0));
	         
	        }
	         
	        ArrayList<Integer> minspanlist= new ArrayList<Integer>();
	        HashMap<String,Integer> Marked = new HashMap<String,Integer>();
	        int markedmax = 0;
	        int markedmin = Integer.MAX_VALUE;
	        while(checkEnd(amap))
	        {
	        int minspan=0;
	        HashMap<String,Integer> Window = CreateWindow(amap);
	         
	         int smallest = Integer.MAX_VALUE;
	         int secondSmallest = Integer.MAX_VALUE;
	         String ref="";
	         String secondref="";
	         int max=0;
	         
	        for(String i: Window.keySet()) {
	        	if(!Marked.containsKey(i))
	        	{
	            
	        		 if(Window.get(i) > max ) 
	        		 {
	        			 if(markedmax>Window.get(i))
	        		     max= markedmax;
	        			 else
	                     max = Window.get(i);
	        		 }
	        		 
	        	 if (Window.get(i) < smallest) {
	                secondSmallest = smallest;
	                if(markedmin<Window.get(i))
	                {
	                smallest = markedmin;
	                }
	                else
	                {
	                smallest = Window.get(i);
	                }
	                secondref=ref;
	                ref=i;
	                
	        	}
	            
	                else if (Window.get(i) < secondSmallest) {
	                	secondSmallest = Window.get(i);
	                    secondref=i;
	           
	                }
	 
	           
	        	}
	            
	        }
	        if((amap.get(ref).termFrequency<amap.get(ref).positionList.size()))
	        {   
	        amap.put(ref, new Tuple(amap.get(ref).positionList,(amap.get(ref).termFrequency+1)));
	        }
	        else
	        {
	        	
	            if(secondref.equals(""))
	                    continue;
	            else
	                amap.put(secondref, new Tuple(amap.get(secondref).positionList,(amap.get(secondref).termFrequency+1)));
	        }
	        
	        for(String key:amap.keySet())
	        {
	        	if(amap.get(key).termFrequency>=amap.get(key).positionList.size())
	        	{
	        		Marked.put(key,amap.get(key).positionList.get(amap.get(key).positionList.size()-1));
	        	}
	        }
	        	
	        markedmin = minHash(Marked);
	        markedmax= maxHash(Marked);
	        minspan= max-smallest;
	        //System.out.println("min"+minspan);
	        minspanlist.add(minspan);
	        //int xsh=min(minspanlist);
	     
	        }
	         
	        return min(minspanlist);
	         
	    }
	     
	    public static HashMap<String,Integer> CreateWindow(HashMap<String,Tuple> amap)
	    {
	        HashMap<String,Integer> Window= new HashMap<String,Integer>();
	        for(String key:amap.keySet())
	        {
	            if(amap.get(key).termFrequency>=amap.get(key).positionList.size())
	            Window.put(key, amap.get(key).positionList.get(amap.get(key).positionList.size() - 1));
	            else
	            Window.put(key, amap.get(key).positionList.get(amap.get(key).termFrequency));
	             
	        }
	        return Window;
	    }
	    public static Boolean checkEnd(HashMap<String,Tuple> amap)
	    {
	        int count=amap.size();
	        for(String key:amap.keySet())
	        {
	        if(amap.get(key).termFrequency >=amap.get(key).positionList.size())
	        {
	            count--;    
	        }
	        }
	        if(count==0)
	        {
	        return false;
	        }
	        else
	        {
	            return true;
	        }
	         
	    }
	     
	    public static int min(ArrayList<Integer> list)
	    {
	        int min=Integer.MAX_VALUE;
	        for(int i: list)
	        {
	         
	           if(i<min)
	        	   min=i;
	        }
	        
	        return min;
	         
	    }
	    
	    public static int minHash(HashMap<String,Integer> list)
	    {
	        int min=Integer.MAX_VALUE;
	        for(String i: list.keySet())
	        {
	         
	           if(list.get(i)<min)
	        	   min=list.get(i);
	        }
	        
	        return min;
	         
	    }
	    public static int maxHash(HashMap<String,Integer> list)
	    {
	        int max=0;
	        for(String i: list.keySet())
	        {
	         
	           if(list.get(i)<max)
	        	   max=list.get(i);
	        }
	        
	        return max;
	         
	    }
	 
	}
	        
	    
	
	

	
	
	


	





