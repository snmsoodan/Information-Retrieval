import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ExtractDocs {
	public static void main(String[] args) throws IOException, InterruptedException
	{
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","6s")
				.put("cluster.name", "elastic4").build();
		
		
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        BulkRequestBuilder breq=client.prepareBulk(); 
        

        List<Object> url = new ArrayList<Object>();
        
        PrintWriter writer = new PrintWriter("C:/Users/Snm/Desktop/HW5/4.txt");

        	
        SearchResponse response = client.prepareSearch("elastic4_final")
				.setTypes("document") 
				.setQuery(QueryBuilders.multiMatchQuery("Kyshtym disaster","title", "url","text"))
				 .setScroll(new TimeValue(60000))
				 .setSize(150).execute().actionGet();
        
		 long hits = response.getHits().getTotalHits();

		
		    for (SearchHit hit : response.getHits().getHits()) {
		    	
		    	Map<String, Object> map = hit.getSource();
		    	String author = (String) map.get("author");
		    	
		    	writer.print(152704+" "+author+" "+hit.getId()+" "+hit.getScore()+" "+"\n");
		    	
		    		    	
		    	System.out.print(152704+" ");
		    	System.out.print(author+" ");
		        System.out.print(hit.getId()+" ");
		        System.out.println(hit.getScore()+" ");
		    }
		    writer.flush();
	
	}
}
