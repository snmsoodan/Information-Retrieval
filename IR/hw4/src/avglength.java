import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class avglength {

	public static double  avglength() throws IOException {
		double totalDocumentLength=length.a();
		Map<String,String> hmap=new HashMap<String,String>();
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","6s")
				.put("cluster.name", "elastic4").build();
		
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
		XContentBuilder xbuilder;	
		int totalDocmentLength=0,termCount=0;
		
		for(Map.Entry m:hmap.entrySet())
		{
			if(m.getValue().toString().length()==1||m.getValue().toString().length()==0)
			{
				continue;
			}
			
			
			TermVectorsResponse resp = client.prepareTermVectors().setIndex("elastic4_final")
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
		    	totalDocmentLength= totalDocmentLength+(Integer)((Map) terms.get(map2.getKey())).get("term_freq");
		    	}
			}
		}
		
		System.out.println(totalDocumentLength);
		return totalDocumentLength;

	}

}
