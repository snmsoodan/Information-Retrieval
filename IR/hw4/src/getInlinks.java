import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class getInlinks {
	

	    /**
	     * @param args
	     * @throws Exception
	     */
	    public static void main(String args[]) throws Exception {
	    	
	    	Settings settings = Settings.builder()
    				.put("client.transport.sniff", true)
    				.put("client.transport.ping_timeout","6s")
    				.put("cluster.name", "elastic4").build();
        	
        	
        	TransportClient client = new PreBuiltTransportClient(settings)
    				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        	
        	HashSet<String> li = new HashSet<String>(); 
        	PrintWriter writer = new PrintWriter("C:/Users/Snm/Desktop/HW4/inlinks");
	    	
	        	int i=1;
	        	
	        	
	            SearchResponse resp = client.prepareSearch("elastic4_final")
	                    .setQuery(QueryBuilders.matchAllQuery())
	                    .setScroll(new TimeValue(60000))
	                    .setSize(1)
	                    .execute()
	                    .actionGet();

	            do {
	            for (SearchHit hit : resp.getHits()) {
	            	System.out.println(i);
	            	i++;
	                Map<String, Object> map = hit.getSource();
	                String canUrl = (String) map.get("docno");
//	                System.out.println(canUrl);
	                
	                
//	                String author = (String) map.get("author");
//	                System.out.println(author);
	                
	                
	                writer.print(canUrl);
	                
	                if(map.get("in_links")!=null)
	                {
	                	 li=  new HashSet<String>((ArrayList<String>)map.get("in_links"));
//	                	 System.out.println(li.size());
	                	
//	                	if(li.size()<500){
	                		writer.print(" "+Arrays.toString(li.toArray()));
//	                	}
//	                	else
//	                	{
//	                		Iterator<String> iter = li.iterator();
//	                		int index=0;
//
//	                		while(iter.hasNext()){
//	                			writer.print(" "+li.toArray()[index]);
//	                			index++;
//	                			if(index==500)
//	                				break;
//	                		}
//	                	}
	               
	                
	                
	             
	                }
	                writer.print("\n");
	                
	                
	                writer.flush();
	                li.clear();
	               map.clear();
	               
	            }
	            
	            resp = client.prepareSearchScroll(resp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
	        } while(resp.getHits().getHits().length != 0);
	            
	        
	        
	    }
	    
	   
	}

