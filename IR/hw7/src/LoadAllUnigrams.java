import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class LoadAllUnigrams {
	
	public static boolean ASC = true;
    public static boolean DESC = false;

	public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
		
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","3600s")
				.put("cluster.name", "elastic4").build();
	
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
		 PrintWriter test = new PrintWriter("C:/Users/Snm/Desktop/HW7/testMatrix.txt");
	        PrintWriter train = new PrintWriter("C:/Users/Snm/Desktop/HW7/trainMatrix.txt");
		
		
		/////////////////////////////////////////////////////////////////////////////////////////
		
//		PrintWriter writer = new PrintWriter("C:/Users/Snm/Desktop/HW7/allUnigrams.txt");
		
		File doc1 = new File("C:/Users/Snm/Desktop/HW7/doc_train_mapping.txt");
		Scanner s11=new Scanner(doc1);
		ArrayList<String> ham = new ArrayList<String>();
		ArrayList<String> id = new ArrayList<String>();
		while(s11.hasNextLine())
		{
			String line1=s11.nextLine();
			String delims[]=line1.split("\\s+");
			id.add(delims[0]);
			if(delims[1].equals("true"))
			{
				
				ham.add("1");
			}
			else
			{
				ham.add("0");
			}
		}
		
///////////////////////////////////////////////////////////////////////////////////////////////////////		
		
		 File doc = new File("C:/Users/Snm/Desktop/HW7/doc_train_mapping.txt");
			Scanner s1=new Scanner(doc);
			
			ArrayList<String> trainId = new ArrayList<String>();
			ArrayList<String> testId = new ArrayList<String>();
//			ArrayList<String> ham = new ArrayList<String>();
			ArrayList<String> trainHam = new ArrayList<String>();
			ArrayList<String> testHam = new ArrayList<String>();
			
			
			while(s1.hasNextLine())
			{
				String line1=s1.nextLine();
				String delims[]=line1.split("\\s+");
					
					if(delims[2].equals("true"))
					{
						trainId.add(delims[0]);
						if(delims[1].equals("true"))
						{
							
							trainHam.add("1");
						}
						else
						{
							trainHam.add("0");
						}
					}
					else
					{
						testId.add(delims[0]);
						if(delims[1].equals("true"))
						{
							testHam.add("1");
						}
						else
						{
							testHam.add("0");
						}
					}
					
				

			} //while loop ends
		
		
		///////////////////////////////////////////////////////////////////////////////////////////////
			//test doc train doc
			PrintWriter te = new PrintWriter("C:/Users/Snm/Desktop/HW7/testDoc.txt");
	        PrintWriter tr = new PrintWriter("C:/Users/Snm/Desktop/HW7/trainDoc.txt");
//	        for(int i=0;i<trainId.size();i++)
//	        {
//	        	tr.println(trainId.get(i));
//	        }
//	        tr.flush();
//	        for(int i=0;i<testId.size();i++)
//	        {
//	        	te.println(testId.get(i));
//	        }
//			te.flush();
		
		
		
		
		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		
		HashSet<String> termVectors = new HashSet<String>();
		Map<String, Object> map, txt, termVec,text,terms;
		int k=0;
		for(int i=1;i<75420;i++)
		{
		System.out.println(i);	
			try{
				TermVectorsResponse resp = client.prepareTermVectors().setIndex("final_cleaner")
						.setType("document").setId("inmail."+i).setSelectedFields("text").execute().actionGet();

				XContentBuilder builder;

				builder = XContentFactory.jsonBuilder().startObject();
				resp.toXContent(builder.field("text"), ToXContent.EMPTY_PARAMS);
				builder.endObject();
				map = XContentHelper.convertToMap(builder.bytes(), false).v2();
				txt= (Map) map.get("text");  
				termVec= (Map) txt.get("term_vectors");
				
				text= ((Map) termVec.get("text"));
				
				
				terms=((Map) text.get("terms"));
				
				
				}catch(Exception e){
					continue;
				}
			
			for(Map.Entry map1:terms.entrySet())
			{	
				termVectors.add((String) map1.getKey());
				k++;
			}
			
		} //loaded all the unigrams in hashSet
		
		ArrayList<String> termVector = new ArrayList<String>();
		
		for (String s : termVectors ) {
			termVector.add(s);
		}

		
		//////////////////////////////////////////////////////////////////
		//add termVector
		PrintWriter iu = new PrintWriter("C:/Users/Snm/Desktop/HW7/indexUnigram.txt");
		for(int i=0;i<termVector.size();i++)
		{
			iu.println(i+":"+termVector.get(i));
		}
		
		//////////////////////////////////////////////////////////////////

		
		for(int i=0;i<testId.size();i++)
		{
			System.out.println(i);
			try{
			TermVectorsResponse resp = client.prepareTermVectors().setIndex("final_cleaner")
					.setType("document").setId(testId.get(i)).setSelectedFields("text").execute().actionGet();

			XContentBuilder builder;

			builder = XContentFactory.jsonBuilder().startObject();
			resp.toXContent(builder.field("text"), ToXContent.EMPTY_PARAMS);
			builder.endObject();
			map = XContentHelper.convertToMap(builder.bytes(), false).v2();
			txt= (Map) map.get("text");  
			termVec= (Map) txt.get("term_vectors");
			
			text= ((Map) termVec.get("text"));
			
			
			terms=((Map) text.get("terms"));
			}catch(Exception e){
				continue;
			}
			te.println(testId.get(i));
			Map<Integer, Integer> unsortMap = new HashMap<Integer, Integer>();
			test.print(testHam.get(i)+" ");
			for(Map.Entry map1:terms.entrySet())
			{	
				int val=(Integer) ((Map) terms.get(map1.getKey())).get("term_freq");
				String ky=(String) map1.getKey();
				int key=termVector.indexOf(ky);
				if(val==0)
					continue;
				
				unsortMap.put(key, val);
			
			}
			
			Map<Integer, Integer> sortedMapAsc = sortByComparator(unsortMap, ASC);
			
			for (Entry<Integer, Integer> entry : sortedMapAsc.entrySet())
	        {           
	            int key=entry.getKey();
	            int val=entry.getValue();
	            test.print((key+1)+":"+val+" ");
	        }
			
			test.print("\n");
			
			
		} //end for loop test matrix
		
		test.flush();
		
		for(int i=0;i<trainId.size();i++)
		{
			System.out.println(i);
			try{
			TermVectorsResponse resp = client.prepareTermVectors().setIndex("final_cleaner")
					.setType("document").setId(trainId.get(i)).setSelectedFields("text").execute().actionGet();

			XContentBuilder builder;

			builder = XContentFactory.jsonBuilder().startObject();
			resp.toXContent(builder.field("text"), ToXContent.EMPTY_PARAMS);
			builder.endObject();
			map = XContentHelper.convertToMap(builder.bytes(), false).v2();
			txt= (Map) map.get("text");  
			termVec= (Map) txt.get("term_vectors");
			
			text= ((Map) termVec.get("text"));
			
			
			terms=((Map) text.get("terms"));
			}catch(Exception e){
				continue;
			}
			tr.println(trainId.get(i));
			Map<Integer, Integer> unsortMap = new HashMap<Integer, Integer>();
			train.print(trainHam.get(i)+" ");
			for(Map.Entry map1:terms.entrySet())
			{	
				int val=(Integer) ((Map) terms.get(map1.getKey())).get("term_freq");
				String ky=(String) map1.getKey();
				int key=termVector.indexOf(ky);
				if(val==0)
					continue;
				
				unsortMap.put(key, val);
			}
			
			Map<Integer, Integer> sortedMapAsc = sortByComparator(unsortMap, ASC);
			
			for (Entry<Integer, Integer> entry : sortedMapAsc.entrySet())
	        {           
				int key=entry.getKey();
	            int val=entry.getValue();
	            train.print((key+1)+":"+val+" ");
	        }
			train.print("\n");
			
			
		} //end for loop train matrix
		
		
		
		te.flush();
		tr.flush();
		train.flush();
//		writer.flush();
	}
	
	 private static Map<Integer, Integer> sortByComparator(Map<Integer, Integer> unsortMap, final boolean order)
	    {

	        List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(unsortMap.entrySet());

	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry<Integer, Integer>>()
	        {
	            public int compare(Entry<Integer, Integer> o1,
	                    Entry<Integer, Integer> o2)
	            {
	                if (order)
	                {
	                    return o1.getKey().compareTo(o2.getKey());
	                }
	                else
	                {
	                    return o2.getKey().compareTo(o1.getKey());

	                }
	            }
	        });

	        // Maintaining insertion order with the help of LinkedList
	        Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
	        for (Entry<Integer, Integer> entry : list)
	        {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }

	        return sortedMap;
	    }

}
