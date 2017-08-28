package mallet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class GetAllDocs {
	
	public static void main(String[] args) throws IOException {
		
		Settings settings = Settings.builder()
  				.put("client.transport.sniff", true)
  				.put("client.transport.ping_timeout","6s")
  				.put("cluster.name", "elastic4").build();
		
		TransportClient client = new PreBuiltTransportClient(settings)
  				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
		
		
		
		
		SearchResponse resp = client.prepareSearch("ap_dataset")
                .setQuery(QueryBuilders.matchAllQuery())
                .setScroll(new TimeValue(60000))
                .setSize(1)
                .execute()
                .actionGet();

        do {
        	for (SearchHit hit : resp.getHits()) {
                Map<String, Object> map = hit.getSource();
                String id = (String) hit.getId();
                String text = (String) map.get("text");
                
               PrintWriter writer = new PrintWriter("C:/Users/Snm/Desktop/HW8docs/"+id);
               writer.println(text);
               writer.flush();
              
            }
         
            resp = client.prepareSearchScroll(resp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();

          
      
        } while(resp.getHits().getHits().length != 0);
        }
		
		
		
	}


