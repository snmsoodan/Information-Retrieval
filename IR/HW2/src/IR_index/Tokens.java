package IR_index;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokens {

	public static void main(String[] args) throws IOException{
		Scanner s1 = new Scanner(new File("C:/Users/Snm/Downloads/AP_DATA/stoplist.txt"));
		List<String> stopwords = new ArrayList<String>();
		while (s1.hasNext()){
		    stopwords.add(s1.next());
		}
		s1.close();
//		System.out.println(stopwords);
		int i= 0;
		PrintWriter writer2 = new PrintWriter("doclengthmap.txt", "UTF-8");
		PrintWriter docwriter = new PrintWriter("documentidmap.txt", "UTF-8");
		Map<String,Integer> docmap=new HashMap<String,Integer>();
		Map<String, Integer> doclengthmap=new HashMap<String,Integer>();
		
		int counter=1;
		for(int f=0;f<73;f++) {
			File folder = new File("C:/Users/Snm/Downloads/AP_DATA/ap89_collection");
			File[] listOfFiles = folder.listFiles();
			//Map<String,String> hm=new HashMap<String,String>(); 
			Map<String, HashMap<String,Tuple>> hm=new HashMap<String,HashMap<String,Tuple>>();
			
			
			PrintWriter writer = new PrintWriter("invertedlist_"+f+".txt", "UTF-8");
			PrintWriter writer1 = new PrintWriter("catalog_"+f+".txt", "UTF-8");
			int doccounter=0;
			
			
	 		//for (int i = 0; i < listOfFiles.length; i++) {
			int j=0;
			int d=i+5;
			for ( ; i < d; i++) {  
				//System.out.println(listOfFiles[i].getPath());
				
				File mFile = new File(listOfFiles[i].getPath());
				FileInputStream fis = new FileInputStream(mFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mFile)));
				String line = "";
				boolean check=false;
				boolean checkdoc=false;
				String docno="";
				String text="";
				
				
				//int doclength=0;
				//System.out.println("before while");
				while( (line = br.readLine()) != null){
					String currentdoc="";
					if(line.startsWith("<DOC>"))
					{
						checkdoc=true;
					    docno="";
						text="";
						doccounter=0;
						
					}
					if(line.endsWith("</DOC>"))
					{
						checkdoc=false;
						//hm.put(docno,text);
						currentdoc=docno;
						
						docmap.put(docno,counter);
						counter++;
						
						
						
					}
					int line_length=line.length();
					if(line.startsWith("<DOCNO>"))
					{
						
						//System.out.println(line.substring(8,(line_length - 9)));
						docno=(line.substring(8,(line_length - 9)));
						
					}
					else if(line.startsWith("<TEXT>"))
					{
						check=true;
					}
					else if(line.endsWith("</TEXT>"))
					{
						check=false;
					}
					if(check)
					{
					
					 currentdoc="";
						//String negate = "[^\\w.]";
						//line=line.replaceAll(negate," ");
					 Pattern p1 =Pattern.compile("[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*");
				       // get a matcher object
				       Matcher m = p1.matcher(line);
				       
				       List<String> matches = new ArrayList<String>();
				       while(m.find()){
				           matches.add(m.group());
				       }
					       	
							for(String s: matches)
							{
								
								//System.out.println("-"+s+"--");
								//doclength= doclength+s.length();
								if(stopwords.contains(s))
							    continue;
					
								
								
								  String word= s.toLowerCase().trim();
								
								//word=word.replaceAll("\\.(?!\\w)", " ");
								//word=word.replaceAll("[^a-zA-Z0-9.']+", " ");
								doccounter++;
								
								
									if(!hm.containsKey(word))
									{
										ArrayList<Integer> a1= new ArrayList<Integer>();
										a1.add(doccounter);
										Tuple t= new Tuple(a1,1);
										HashMap<String,Tuple> wordmap=new HashMap<String,Tuple>();
										wordmap.put(docno, t);
										hm.put(word,wordmap);
									}
									else{
										if(hm.get(word).containsKey(docno))
										{
											Tuple t = hm.get(word).get(docno);
											t.positionList.add(doccounter);
											t.termFrequency= t.termFrequency+1;
											
										}
										else{
											ArrayList<Integer> a2= new ArrayList<Integer>();
											a2.add(doccounter);
											Tuple t= new Tuple(a2,1);
											hm.get(word).put(docno,t);
										}
									}
									
								
							}
					}
					
					doclengthmap.put(docno, doccounter);
					
				
					
				}
				br.close();
				fis.close();
			//	System.out.println(doclengthmap.size());
				
				
			      
				}
			
//			System.out.println(hm);
			
			for(String key:hm.keySet()){
				HashMap<String,Tuple> sortedmap =sortMapByValues(hm.get(key));
				hm.put(key, sortedmap);
				//System.out.println("--word--"+key);
				/*for(String key2: hm.get(key).keySet())
				{
					System.out.println("--docid--"+key2+"--positions--"+hm.get(key).get(key2).positionList+"--termfrequency--"+hm.get(key).get(key2).termFrequency);
				}*/
				}
			
			Map<String, IntTuple> Catalog=new HashMap<String,IntTuple>();
			int start=0;
				for(String key:hm.keySet()){
					writer.print(key+"|");
					int length=(key+"|").length();
					for(String key2: hm.get(key).keySet())
					{
						String wordinvlist= docmap.get(key2)+"|"+hm.get(key).get(key2).termFrequency+"|"+hm.get(key).get(key2).positionList+"|";
						length=length+wordinvlist.length();
						writer.print(wordinvlist);
						
//						writer.println("The second line");
//						System.out.println("--docid--"+key2+"--positions--"+hm.get(key).get(key2).positionList+"--termfrequency--"+hm.get(key).get(key2).termFrequency);
					}
					length=length+start;
					
					IntTuple position= new IntTuple(start, length);
					Catalog.put(key,position);
					start=length;
					}
				
				for(String key:Catalog.keySet())
				{
					writer1.print("|"+key+"|"+Catalog.get(key).startoffset+"|"+Catalog.get(key).endoffset);	
				}
				
				
				writer1.close();	
			writer.close();
			}
		
		for(String key:doclengthmap.keySet())
		{
			writer2.print("|"+key+"|"+doclengthmap.get(key));	
		}
		writer2.close();
		for(String key:docmap.keySet())
		{
			docwriter.print("|"+key+"|"+docmap.get(key));	
		}
		
		docwriter.close();
		
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
