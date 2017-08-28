import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
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
import org.json.simple.parser.ParseException;



public class makeInlinks {
	
	  public static void main(String args[]) throws Exception {
	    	
	    	Settings settings = Settings.builder()
  				.put("client.transport.sniff", true)
  				.put("client.transport.ping_timeout","6s")
  				.put("cluster.name", "elastic4").build();
      	
      	
      	TransportClient client = new PreBuiltTransportClient(settings)
  				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
      	
      	HashSet<String> ol = new HashSet<String>();
      	HashSet<String> ill = new HashSet<String>();
      	org.codehaus.jackson.map.ObjectMapper ob=new org.codehaus.jackson.map.ObjectMapper();
      	JSONArray lia=null;
      	

      	
     
      	HashMap<String, ArrayList<String>> li = new HashMap<String, ArrayList<String>>();
      	int cnt=0,col=0;
	        	
	            SearchResponse resp = client.prepareSearch("elastic4_final")
	                    .setQuery(QueryBuilders.matchAllQuery())
	                    .setScroll(new TimeValue(60000))
	                    .setSize(1)
	                    .execute()
	                    .actionGet();

	            do {
	            for (SearchHit hit : resp.getHits()) {
	                Map<String, Object> map = hit.getSource();
	                String canUrl = (String) map.get("docno");
	                String author = (String) map.get("author");
	               
	               
	                if(map.get("out_links")!=null){
						ol = new HashSet<String>((ArrayList<String>)map.get("out_links"));
						}
	                col=col+ol.size();

	                for(int i=0;i<ol.size();i++)
	                {

	                	
	                	if(li.containsKey(ol.toArray()[i].toString()))
	                	{
	                		ArrayList<String> a=new ArrayList<String>();
	                		a=li.get(ol.toArray()[i].toString());
	                		a.add(canUrl);
	                		li.put(ol.toArray()[i].toString(), a);
	                		
	                	}
	                	else{
	                		ArrayList<String> a=new ArrayList<String>();
	                		a.add(canUrl);
	                		li.put(ol.toArray()[i].toString(), a);
	                		
	                	}

	                }
	                
 
	                
	              
	               
	               map.clear();

	               
	            }
	         
	            resp = client.prepareSearchScroll(resp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();

	            cnt++;
	            System.out.println(cnt);
	        } while(resp.getHits().getHits().length != 0);
	            
	            

	            
	           int cnt1=0;

	            SearchResponse resp1 = client.prepareSearch("elastic4_final")
	                    .setQuery(QueryBuilders.matchAllQuery())
	                    .setScroll(new TimeValue(60000))
	                    .setSize(1)
	                    .execute()
	                    .actionGet();

	            do {
	            for (SearchHit hit : resp1.getHits()) {
	            	Map<String, Object> map1 = hit.getSource();
	                String key = (String) map1.get("docno");
	                String curl = (String) hit.getId();
	       
	                       
	                GetResponse respMerge = client.prepareGet("elastic4_final", "document",curl)
							.execute()
							.actionGet();
	                if (respMerge.isExists()) {

		            	if(li.get(key)!=null)
		            	{
		            		ill=new HashSet<String>(li.get(key));
		            	}
		            	else
		            	{
		            		ill=new HashSet<String>();
		            	}
		            	
		            	JSONParser parser = new JSONParser();
		            	lia=(JSONArray)parser.parse(ob.writeValueAsString(ill));
		            	client.prepareUpdate("elastic4_final", "document",curl)
		            	.setDoc(jsonBuilder()               
		            			.startObject()
		            			.field("in_links", lia)
		            			.endObject())
		            	.get();

		            }

	               
	            }
	         
	            resp1 = client.prepareSearchScroll(resp1.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
	            cnt1++;
	            System.out.println(cnt1);
	        } while(resp1.getHits().getHits().length != 0);
	     
	            
	            
	        
	        
	    }

}
