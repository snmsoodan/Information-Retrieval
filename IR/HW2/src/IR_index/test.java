package IR_index;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class test {
	public static void main(String[] args) throws IOException
	{
	for(int i=1;i<72;i++)
	{
	File mFile = new File("C:/Users/Snm/workspace/HW2/merged/mergedcatalog_"+i+".txt");
	File mFile1 = new File("C:/Users/Snm/workspace/HW2/catalog_"+(i+1)+".txt");
	FileInputStream fis1 = new FileInputStream(mFile1);
	BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(mFile1)));
	FileInputStream fis = new FileInputStream(mFile);
	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mFile)));
	RandomAccessFile raf = new RandomAccessFile("C:/Users/Snm/workspace/HW2/merged/mergedlist_"+i+".txt", "r");
	RandomAccessFile raf1 = new RandomAccessFile("C:/Users/Snm/workspace/HW2/invertedlist_"+(i+1)+".txt", "r");
	PrintWriter writer = new PrintWriter("C:/Users/Snm/workspace/HW2/merged/mergedlist_"+(i+1)+".txt", "UTF-8");
	PrintWriter writer1 = new PrintWriter("C:/Users/Snm/workspace/HW2/merged/mergedcatalog_"+(i+1)+".txt", "UTF-8");
	String line = "";
	String line1 = "";
	boolean check=false;
	boolean checkdoc=false;
	String docno="";
	String text="";
	Map<String, IntTriple> Catalog=new HashMap<String,IntTriple>();
	Map<String, IntTriple> Catalog2=new HashMap<String,IntTriple>();
	Map<String, IntTuple> ResultCatalog=new HashMap<String,IntTuple>();
	
	
	
	//System.out.println("before while");
	while((line = br.readLine()) != null)
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
	while((line1 = br1.readLine()) != null)
	{
	String[] queryparams1= line1.split("\\|");
	
	
	for(int count=1;(count+3)<=queryparams1.length;)
	{
		//System.out.println(queryparams[count]+""+queryparams[count+1]+""+queryparams[count+2]+"");
		IntTriple t1= new IntTriple(Integer.parseInt(queryparams1[count+1]),Integer.parseInt(queryparams1[count+2]),0);
		Catalog2.put(queryparams1[count],t1);
		count=count+3;
	}
	}
	int start=0;
	
	for(String key:Catalog2.keySet())
	{
		int length=Catalog2.get(key).endoffset-(Catalog2.get(key).startoffset);
		 byte[] mybyte= new byte[length];
		 raf1.seek(Catalog2.get(key).startoffset);
		 raf1.readFully(mybyte);
		 
		 String inventry= new String(mybyte,"UTF-8");
		 String[] params1= inventry.split("\\|");
		 HashMap<String,Tuple> wordmap1=new HashMap<String,Tuple>();
		 
		 for(int count=1;(count+3)<=params1.length;count=count+3)
			{
			 String s1=params1[count+2];
			 String replace = s1.replace("[","");
			 String replace1 = replace.replace("]","");
			 List<String> arrayList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
			 ArrayList<Integer> posList = new ArrayList<Integer>();
			 for(String fav:arrayList){
				 posList.add(Integer.parseInt(fav.trim()));
			}
			 Tuple t1= new Tuple(posList,Integer.parseInt(params1[count+1]));
			 wordmap1.put(params1[count], t1);
			}
		
		if(Catalog.containsKey(key))
		{
		 int length1=Catalog.get(key).endoffset-(Catalog.get(key).startoffset);
			 byte[] mybyte1= new byte[length1];
			 raf.seek(Catalog.get(key).startoffset);
			 raf.readFully(mybyte1);
			 String inventry2= new String(mybyte1,"UTF-8");
			 
			 String[] params2= inventry2.split("\\|");
			
			
			 
			
			 for(int count=1;(count+3)<=params2.length;count=count+3)
				{
				 String s1=params2[count+2];
				 String replace = s1.replace("[","");
				 String replace1 = replace.replace("]","");
				 List<String> arrayList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
				 ArrayList<Integer> posList = new ArrayList<Integer>();
				 for(String fav:arrayList){
					 posList.add(Integer.parseInt(fav.trim()));
				 }
				 if(!wordmap1.containsKey(params2[count]))
				 {
					wordmap1.put(params2[count], new Tuple(posList,Integer.parseInt(params2[count+1]))); 
				 }
				 else
				 {
				 int existingtermfrequency= wordmap1.get(params2[count]).termFrequency;
				 ArrayList<Integer> existingList= wordmap1.get(params2[count]).positionList;
				 posList.addAll(existingList);
				 Collections.sort(posList);
				 int resultf= Integer.parseInt(existingtermfrequency+params2[count+1]);
				 wordmap1.put(params2[count], new Tuple(posList,resultf));
				 }
				 
					 
				}
			 
			 Catalog.put(key,new IntTriple(Catalog.get(key).startoffset,Catalog.get(key).endoffset,1));
			
			 
		}
		
		
		int slength=(key+"|").length();
		writer.write(key+"|");
		HashMap<String,Tuple> sortedmap1 =sortMapByValues(wordmap1);
		 for(String key1: sortedmap1.keySet())
		 {
			 
			 String wordinvlist= key1+"|"+sortedmap1.get(key1).termFrequency+"|"+sortedmap1.get(key1).positionList+"|";
				slength=slength+wordinvlist.length();
				writer.print(wordinvlist);
			 //writer.write(key1+"|"+wordmap1.get(key1).termFrequency+"|"+wordmap1.get(key1).positionList+"|");
			 
		}
		 slength=slength+start;
			
			IntTuple position= new IntTuple(start, slength);
			ResultCatalog.put(key,position);
			start=slength;
		 }
	
	for(String key:Catalog.keySet())
	{
		if(Catalog.get(key).flag==0)
		{
			//System.out.println("here");
			int length1=Catalog.get(key).endoffset-(Catalog.get(key).startoffset);
			 HashMap<String,Tuple> wordmap2=new HashMap<String,Tuple>();
			 byte[] mybyte1= new byte[length1];
			 raf.seek(Catalog.get(key).startoffset);
			 raf.readFully(mybyte1);
			 String inventry2= new String(mybyte1,"UTF-8");
			 
			 String[] params2= inventry2.split("\\|");
			
			 for(int count=1;(count+3)<=params2.length;count=count+3)
				{
				 String s1=params2[count+2];
				 String replace = s1.replace("[","");
				 String replace1 = replace.replace("]","");
				 List<String> arrayList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
				 ArrayList<Integer> posList = new ArrayList<Integer>();
				 for(String fav:arrayList){
					 posList.add(Integer.parseInt(fav.trim()));
				 }
				 wordmap2.put(params2[count], new Tuple(posList,Integer.parseInt(params2[count+1])));
		}
			 int slength=(key+"|").length();
				writer.write(key+"|");
				HashMap<String,Tuple> sortedmap2 =sortMapByValues(wordmap2);
				 for(String key1: sortedmap2.keySet())
				 {
					 
					 String wordinvlist= key1+"|"+sortedmap2.get(key1).termFrequency+"|"+sortedmap2.get(key1).positionList+"|";
						slength=slength+wordinvlist.length();
						writer.print(wordinvlist);
					 //writer.write(key1+"|"+wordmap1.get(key1).termFrequency+"|"+wordmap1.get(key1).positionList+"|");
					 
				}
				 slength=slength+start;
					
					IntTuple position= new IntTuple(start, slength);
					ResultCatalog.put(key,position);
					start=slength;
	}
	
		
	}
	
	for(String key:ResultCatalog.keySet())
	{
		writer1.print("|"+key+"|"+ResultCatalog.get(key).startoffset+"|"+ResultCatalog.get(key).endoffset);	
	}
	//System.out.println("size:"+ResultCatalog.size());
	
	writer.flush();
	writer1.flush();
	raf.close();
	raf1.close();
	

	}	
}
private static HashMap<String, Tuple> sortMapByValues(Map<String, Tuple> aMap) {
        
        Set<Entry<String,Tuple>> mapEntries = aMap.entrySet();
        
        // used linked list to sort, because insertion of elements in linked list is faster than an array list. 
        List<Entry<String,Tuple>> aList = new LinkedList<Entry<String,Tuple>>(mapEntries);

        // sorting the List
        Collections.sort(aList, new Comparator<Entry<String,Tuple>>() {

            @Override
            public int compare(Entry<String, Tuple> ele1,
                    Entry<String, Tuple> ele2) {
                
                return ((Integer)ele2.getValue().termFrequency).compareTo(ele1.getValue().termFrequency);
            }
        });
        
        // Storing the list into Linked HashMap to preserve the order of insertion. 
        Map<String,Tuple> aMap2 = new LinkedHashMap<String, Tuple>();
        for(Entry<String,Tuple> entry: aList) {
            aMap2.put(entry.getKey(), entry.getValue());
        }
      
            return (HashMap<String, Tuple>) aMap2;
        }
}
