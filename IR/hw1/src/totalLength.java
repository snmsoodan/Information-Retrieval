import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class totalLength {
public static double lengthAvg() throws IOException
{
	File fl = new File("C:\\Users\\Snm\\Downloads\\AP_DATA\\ap89_collection");
	File[] filesList = fl.listFiles();
	Map<String,String> hmap=new HashMap<String,String>();
	
	
	for (int i = 0; i < filesList.length; i++)
	{
		File newFile = new File(filesList[i].getPath());
		FileInputStream fis = new FileInputStream(newFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
		String line="",docno="",text="";
		boolean check=false,checkdoc=false;
		while( (line = br.readLine()) != null){
			if(line.startsWith("<DOC>"))
			{
				docno="";
				checkdoc=true;
				text="";
			}
			if(line.endsWith("</DOC>"))
			{
				hmap.put(docno,text);
				checkdoc=false;
				
				
			}
			int lineLength=line.length();
			if(line.startsWith("<DOCNO>"))
			{
				
				docno=(line.substring(8,(lineLength - 9)));
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
				if(!(line.equals("<TEXT>")))
				{
					text=text.concat(" "+line);
				}
			}
			
		}
		
		br.close();
		fis.close();
		
		
	}
	
	Settings settings = Settings.builder()
			.put("client.transport.sniff", true)
			.put("client.transport.ping_timeout","3600s")
			.put("cluster.name", "elasticsearch").build();

	TransportClient client = new PreBuiltTransportClient(settings)
		.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	
	XContentBuilder xbuilder;	
	int totalDocumentLength=0,termCount=0;
	for(Map.Entry m:hmap.entrySet())
	{
		if(m.getValue().toString().length()==1||m.getValue().toString().length()==0)
		{
			continue;
		}
		
		
		TermVectorsResponse resp = client.prepareTermVectors().setIndex("ap_dataset")
                .setType("document").setId((String) m.getKey()).setSelectedFields("text").execute().actionGet();
	
		xbuilder = XContentFactory.jsonBuilder().startObject();
	    resp.toXContent(xbuilder.field("text"), ToXContent.EMPTY_PARAMS);
	    xbuilder.endObject();
	    
	    Map<String, Object> map = XContentHelper.convertToMap(xbuilder.bytes(), false).v2();

	    Map<String, Object> txt= (Map) map.get("text");

	    Map<String, Object> termVec= (Map) txt.get("term_vectors");
		   

	    Map<String, Object> text= ((Map) termVec.get("text"));
	    Map<String, Object> terms=((Map) text.get("terms"));
	    
	    termCount=termCount+terms.size();
	    for(Map.Entry map2:terms.entrySet())
	    {
	    	if(terms.get(map2.getKey())!=null)
	    	{
	    	totalDocumentLength= totalDocumentLength+(Integer)((Map) terms.get(map2.getKey())).get("term_freq");
	    	}
		}
	}
//	System.out.println("uniqueTerms "+termCount);
//	System.out.println(totalDocumentLength);
	Double avgDocumentLength= (double) (totalDocumentLength/(hmap.size()));
//	System.out.println(avgDocumentLength);
	return(avgDocumentLength);
	
}

public static int vocabolary() throws IOException{
//	File fl = new File("C:\\Users\\Snm\\Downloads\\AP_DATA\\ap89_collection");
//	File[] filesList = fl.listFiles();
//	Map<String,String> hmap=new HashMap<String,String>();
//	
//	
//	for (int i = 0; i < filesList.length; i++)
//	{
//		File newFile = new File(filesList[i].getPath());
//		FileInputStream fis = new FileInputStream(newFile);
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
//		String line="",docno="",text="";
//		boolean check=false,checkdoc=false;
//		while( (line = br.readLine()) != null){
//			if(line.startsWith("<DOC>"))
//			{
//				docno="";
//				checkdoc=true;
//				text="";
//			}
//			if(line.endsWith("</DOC>"))
//			{
//				hmap.put(docno,text);
//				checkdoc=false;
//				
//				
//			}
//			int lineLength=line.length();
//			if(line.startsWith("<DOCNO>"))
//			{
//				
//				docno=(line.substring(8,(lineLength - 9)));
//			}
//			else if(line.startsWith("<TEXT>"))
//			{
//				check=true;
//			}
//			else if(line.endsWith("</TEXT>"))
//			{
//				check=false;
//			}
//			if(check)
//			{
//				if(!(line.equals("<TEXT>")))
//				{
//					text=text.concat(" "+line);
//				}
//			}
//			
//		}
//		
//		br.close();
//		fis.close();
//		
//		
//	}
	
	Settings settings = Settings.builder()
			.put("client.transport.sniff", true)
			.put("client.transport.ping_timeout","3600s")
			.put("cluster.name", "elasticsearch").build();

	TransportClient client = new PreBuiltTransportClient(settings)
		.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	
	CardinalityAggregationBuilder aggregation = AggregationBuilders
	                									.cardinality("agg")
	                									.field("text");
	
	SearchResponse  ucount=client.prepareSearch("ap_dataset")
								.setTypes("document")
								.setSize(0)
								.addAggregation(aggregation).execute().actionGet();
	XContentBuilder xbuilder1 = XContentFactory.jsonBuilder().startObject();
    ucount.toXContent(xbuilder1.field("value"), ToXContent.EMPTY_PARAMS);
    xbuilder1.endObject();
    
    Map<String, Object> map1 = XContentHelper.convertToMap(xbuilder1.bytes(), false).v2();

    Map<String, Object> value= (Map) map1.get("value");

    Map<String, Object> aggregations= (Map) value.get("aggregations");


    Map<String, Object> agg= ((Map) aggregations.get("agg"));
   
    
    int uniqueVal=(Integer) agg.get("value");
//    System.out.println(uniqueVal);
    return(uniqueVal);
	
}


}
