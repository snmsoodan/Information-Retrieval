import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

// got the termFrequency of all text for a specific doc
public class termTest2 {
	public static void main(String[] args) throws IOException
	{
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","3600s")
				.put("cluster.name", "elastic4").build();
	
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
        BulkRequestBuilder breq=client.prepareBulk();
        
        PrintWriter test = new PrintWriter("C:/Users/Snm/Desktop/HW7/testMatrix.txt");
        PrintWriter train = new PrintWriter("C:/Users/Snm/Desktop/HW7/trainMatrix.txt");
        
       ////////////////////////////////////////////////////////////////////// 
        File doc = new File("C:/Users/Snm/Desktop/HW7/doc_train_mapping.txt");
		Scanner s1=new Scanner(doc);
		
		ArrayList<String> trainId = new ArrayList<String>();
		ArrayList<String> testId = new ArrayList<String>();
//		ArrayList<String> ham = new ArrayList<String>();
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
		
		/////////////////////////////////////////////////////////////////////////////
		//print train and test documents
		PrintWriter te = new PrintWriter("C:/Users/Snm/Desktop/HW7/testDoc.txt");
        PrintWriter tr = new PrintWriter("C:/Users/Snm/Desktop/HW7/trainDoc.txt");
//        for(int i=0;i<trainId.size();i++)
//        {
//        	tr.println(trainId.get(i));
//        }
//        tr.flush();
//        for(int i=0;i<testId.size();i++)
//        {
//        	te.println(testId.get(i));
//        }
//		te.flush();
		
		//////////////////////////////////////////////////////////////////////////
		
		  File words = new File("C:/Users/Snm/Desktop/HW7/spam_words.txt");
//		  File words = new File("C:/Users/Snm/Desktop/HW7/spam3.txt");
//		File words = new File("C:/Users/Snm/Desktop/HW7/a.txt");
			Scanner s2=new Scanner(words);
			
			ArrayList<String> word = new ArrayList<String>();
			
			while(s2.hasNextLine())
			{
				String line1=s2.nextLine();
				String delims[]=line1.split("\\s+");
				word.add(delims[0]);
				
			} //while loop ends
			//////////////////////////////////////////////////////////////////////////
       
		//for test set
			Map<String, Object> map, txt, termVec,text,terms;
		for(int i=0;i<testId.size();i++)
		{
//			System.out.println(testId.get(i));
			try{
			TermVectorsResponse resp = client.prepareTermVectors().setIndex("final_cleaner1")
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
			ArrayList<String> key = new ArrayList<String>();
			ArrayList<Integer> value = new ArrayList<Integer>();

			for(Map.Entry map1:terms.entrySet())
			{	
				key.add((String) map1.getKey());
				value.add((Integer) ((Map) terms.get(map1.getKey())).get("term_freq"));
				
//				System.out.println(map1.getKey()+" "+(Integer)((Map) terms.get(map1.getKey())).get("term_freq"));
			}
//			test.print(testId.get(i)+" ");
			test.print(testHam.get(i)+" ");
			
			for(int i1=0;i1<word.size();i1++)
			{
				if(key.contains(word.get(i1)))
				{
					int index=key.indexOf(word.get(i1));
					if(value.get(index)==0)
						continue;
					test.print((i1+1)+":"+value.get(index)+" ");
				}
//				else
//				{
//					test.print((i1+1)+":"+"0"+" ");
//				}
			}
			test.print("\n");
			
			
		} //end for loop test matrix
		
		
		
		for(int i=0;i<trainId.size();i++)
		{
//			System.out.println(testId.get(i));
			try{
			TermVectorsResponse resp = client.prepareTermVectors().setIndex("final_cleaner1")
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
			ArrayList<String> key = new ArrayList<String>();
			ArrayList<Integer> value = new ArrayList<Integer>();

			for(Map.Entry map1:terms.entrySet())
			{	
				key.add((String) map1.getKey());
				value.add((Integer) ((Map) terms.get(map1.getKey())).get("term_freq"));
				
//				System.out.println(map1.getKey()+" "+(Integer)((Map) terms.get(map1.getKey())).get("term_freq"));
			}
//			train.print(trainId.get(i)+" ");
			train.print(trainHam.get(i)+" ");
			
			for(int i1=0;i1<word.size();i1++)
			{
				if(key.contains(word.get(i1)))
				{
					int index=key.indexOf(word.get(i1));
					if(value.get(index)==0)
						continue;
					train.print((i1+1)+":"+value.get(index)+" ");
				}
//				else
//				{
//					train.print((i1+1)+":"+"0"+" ");
//				}
			}
			train.print("\n");
			
			
		} //end for loop train matrix

        te.flush();
        tr.flush();
		test.flush();
		train.flush();
	}
}
