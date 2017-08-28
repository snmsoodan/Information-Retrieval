package mallet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Map;
import java.util.Scanner;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class termVectors {
	public static void main(String[] args) throws Exception
	{
		Settings settings = Settings.builder()
  				.put("client.transport.sniff", true)
  				.put("client.transport.ping_timeout","6s")
  				.put("cluster.name", "elastic4").build();
		
		TransportClient client = new PreBuiltTransportClient(settings)
  				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
		PrintWriter writer = new PrintWriter("C:\\Users\\Snm\\Downloads\\AP_DATA\\cache\\b.txt");
		int a=0;
		
		SearchResponse resp1 = client.prepareSearch("ap_dataset")
                .setQuery(QueryBuilders.matchAllQuery())
                .setScroll(new TimeValue(60000))
                .setSize(1)
                .execute()
                .actionGet();

        do {
        	for (SearchHit hit : resp1.getHits()) {
                Map<String, Object> map = hit.getSource();
                String id = (String) hit.getId();
            
            	XContentBuilder build;
        		TermVectorsResponse resp = client.prepareTermVectors().setIndex("ap_dataset")
        				 .setType("document").setId(id).setSelectedFields("text").execute().actionGet();
//        		if(a==2)
//        			break;
        		a++;
        		System.out.println(a);

        		try {
        			build = XContentFactory.jsonBuilder().startObject();
        			resp.toXContent(build.field("text"), ToXContent.EMPTY_PARAMS);
        		    build.endObject();
        		    Map<String, Object> map1 = XContentHelper.convertToMap(build.bytes(), false).v2();
				    Map<String, Object> txt= (Map) map1.get("text");
				    Map<String, Object> termVec= (Map) txt.get("term_vectors");
				   	Map<String, Object> text= ((Map) termVec.get("text"));
					Map<String, Object> terms=((Map) text.get("terms"));

					for(Map.Entry map2:terms.entrySet())
					{	
//						System.out.print(map2.getKey()+" ");
						writer.print(map2.getKey()+" ");
				    }
        		} catch (Exception e)
        		{
        			continue;
        		}
            }
         
            resp1 = client.prepareSearchScroll(resp1.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();

          
//      break;
        } while(resp1.getHits().getHits().length != 0);
		
		
		
	
		writer.flush();
		
	}
}
