import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

public class topSpamWords {
	public static boolean ASC = true;
    public static boolean DESC = false;
    public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
    	
    	Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","3600s")
				.put("cluster.name", "elastic4").build();
	
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
		Map<String, Integer> unsortMap = new HashMap<String, Integer>();
		Map<String, Integer> unsortMap1 = new HashMap<String, Integer>();
		
		PrintWriter spam = new PrintWriter("C:/Users/Snm/Desktop/HW7/topSpam.txt");
		
		File doc1 = new File("C:/Users/Snm/Desktop/HW7/doc_train_mapping.txt");
		Scanner s11=new Scanner(doc1);
		ArrayList<String> id = new ArrayList<String>();
		while(s11.hasNextLine())
		{
			String line1=s11.nextLine();
			String delims[]=line1.split("\\s+");
			if(delims[1].equals("false"))
			{
				
				id.add(delims[0]);
			}
		}
		//////////////////////////////////////////////////////////////////////get all the spam documents list
		
		File doc2 = new File("C:/Users/Snm/Desktop/HW7/stopwords.txt");
		Scanner s12=new Scanner(doc2);
		ArrayList<String> stp = new ArrayList<String>();
		while(s12.hasNextLine())
		{
			String line1=s12.nextLine();
			String delims[]=line1.split("\\s+");
			stp.add(delims[0]);
		}
		///////////////////////////////////////////////////////////////////////////// stoplist
		
		File doc = new File("C:/Users/Snm/Desktop/HW7/doc_train_mapping.txt");
		Scanner s1=new Scanner(doc);
		ArrayList<String> hamid = new ArrayList<String>();
		while(s1.hasNextLine())
		{
			String line1=s1.nextLine();
			String delims[]=line1.split("\\s+");
			if(delims[1].equals("true"))
			{
				
				hamid.add(delims[0]);
			}
		}
		
		////////////////////////////////////////////get all the ham documents list
		
		
		
		Map<String, Object> map, txt, termVec,text,terms;
		for(int i=0;i<id.size();i++)
		{
			System.out.println(i);
			try{
				TermVectorsResponse resp = client.prepareTermVectors().setIndex("final_cleaner")
						.setType("document").setId(id.get(i)).setSelectedFields("text").execute().actionGet();

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
				int val=(Integer) ((Map) terms.get(map1.getKey())).get("term_freq");
				String key=(String) map1.getKey();
				
				if(val==0)
					continue;
				
				if(unsortMap.containsKey(key))
				{
					int tval=unsortMap.get(key);
					
						unsortMap.put(key, (val+tval));
					
					
				}
				else
				{
					unsortMap.put(key, val);
				}
				
			}
			
			
		} // end of main loop
		
		Map<String, Integer> sortedMapAsc = sortByComparator(unsortMap, DESC);
		
		PrintWriter spam1 = new PrintWriter("C:/Users/Snm/Desktop/HW7/allSpam.txt");
		
		for (Entry<String, Integer> entry : sortedMapAsc.entrySet())
        {           
			String key=entry.getKey();
            int val=entry.getValue();
            
            if(stp.contains(key))
            	continue;
            
//            spam.println(key+" : "+val+" ");
            spam1.println(key);
        }
		spam1.flush();
		
		
	//////////////////////////////////////////////////////////////////////////////////////////// allSpam
		
		
//		for(int i=0;i<hamid.size();i++)
//		{
//			System.out.println(i);
//			try{
//				TermVectorsResponse resp = client.prepareTermVectors().setIndex("final_cleaner")
//						.setType("document").setId(hamid.get(i)).setSelectedFields("text").execute().actionGet();
//
//				XContentBuilder builder;
//
//				builder = XContentFactory.jsonBuilder().startObject();
//				resp.toXContent(builder.field("text"), ToXContent.EMPTY_PARAMS);
//				builder.endObject();
//				map = XContentHelper.convertToMap(builder.bytes(), false).v2();
//				txt= (Map) map.get("text");  
//				termVec= (Map) txt.get("term_vectors");
//				
//				text= ((Map) termVec.get("text"));
//				
//				
//				terms=((Map) text.get("terms"));
//				}catch(Exception e){
//					continue;
//				}
//			
//			
//			
//			for(Map.Entry map1:terms.entrySet())
//			{	
//				int val=(Integer) ((Map) terms.get(map1.getKey())).get("term_freq");
//				String key=(String) map1.getKey();
//				
//				if(val==0)
//					continue;
//				
//				if(unsortMap1.containsKey(key))
//				{
//					int tval=unsortMap1.get(key);
//					
//						unsortMap1.put(key, (val+tval));
//					
//					
//				}
//				else
//				{
//					unsortMap1.put(key, val);
//				}
//				
//			}
//			
//			
//		} // end of main loop
//		
//		Map<String, Integer> sortedMapAsc1 = sortByComparator(unsortMap1, DESC);
//		
//		
//		Map<String, Integer> finalSorted = new HashMap<String, Integer>();
//		
//		
//		for (Entry<String, Integer> entry : sortedMapAsc.entrySet())
//	        {           
//				String key=entry.getKey();
//	            int val=entry.getValue();
//	            if(sortedMapAsc1.containsKey(key))
//	            {
//	            	continue;
//	            }
//	            else
//	            {
////	            	System.out.println(key);
////	            	System.out.println(val);;
//	            	finalSorted.put(key, val);
//	            }
//
//	        }
//		
//		
//		Map<String, Integer> sortedMapAsc2 = sortByComparator(finalSorted, DESC);
//		
//		for (Entry<String, Integer> entry : sortedMapAsc2.entrySet())
//	        {           
//				String key=entry.getKey();
//	            int val=entry.getValue();
////	            spam.println(key+" : "+val+" ");
//	            spam.println(key);
//	        }
//			spam.flush();
		
		
		
    	
    }
    
    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
