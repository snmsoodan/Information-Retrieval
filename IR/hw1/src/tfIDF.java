import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.index.query.functionscore.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
//import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import static org.elasticsearch.index.query.QueryBuilders.scriptQuery;



public class tfIDF {

	public static void main(String[] args) throws UnknownHostException{
		try
		{
		Double totallen=totalLength.lengthAvg();

		Settings settings = Settings.builder()
			.put("client.transport.sniff", true)
			.put("client.transport.ping_timeout","3600s")
			.put("cluster.name", "elasticsearch").build();

	TransportClient client = new PreBuiltTransportClient(settings)
		.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

		int querryNo=-1;

//		for (String line : Files.readAllLines(Paths.get("C:\\Users\\Snm\\Downloads\\AP_DATA\\query_desc.51-100.short.txt")))
		for (String line : Files.readAllLines(Paths.get("C:/Users/Snm/Downloads/AP_DATA/queryModified.txt")))
		{
//		System.out.println("in the file");
		HashMap<String,Double> RankedList = new HashMap<String,Double>();	
		String[] qParams= line.split("\\s+");
		String pattern = "(\\d+\\.)";
		for(String i: qParams)
				{
					
					if(i.matches(pattern))
					{
						querryNo = Integer.parseInt(i.replace(".", ""));
						RankedList.clear();
						
					}

					else
					{
						Long count1=Long.valueOf(0);
						
						final Map<String, Object> params = new HashMap<String, Object>();
//						System.out.println(i);
						params.put("term", i);
						params.put("field", "text");
						
						SearchResponse response = client.prepareSearch("ap_dataset")
								.setTypes("document")
								.addScriptField("field", (new Script(ScriptType.INLINE,"groovy","_index[field][term].tf()",params)))
								.setQuery(QueryBuilders.matchQuery("text", i))
								.setScroll(new TimeValue(60000))
								.setFrom(0)
								.setSize(100).get();
																

						
						do{
							


						JSONParser parser = new JSONParser();
						JSONObject root = (JSONObject) parser.parse(response.toString());
						JSONObject hitsObject= (JSONObject)root.get("hits");
						JSONArray hitsArray = (JSONArray)hitsObject.get("hits");
						
						count1= (Long)hitsObject.get("total");


						for(int j=0;j<hitsArray.size();j++)
						{
							XContentBuilder build;
							Double  documentLength = 0.00;
							JSONObject objects = (JSONObject)hitsArray.get(j);
							String documentid= (String)((JSONObject)objects).get("_id");
							Double tfScore=(Double)((JSONObject)objects).get("_score");
										
//							System.out.println(documentid);


							if(tfScore>0.0)
								{
									Double tfIDf=0.00;
									TermVectorsResponse resp = client.prepareTermVectors().setIndex("ap_dataset")
						                    						 .setType("document").setId(documentid).setSelectedFields("text").execute().actionGet();
									build = XContentFactory.jsonBuilder().startObject();
								    resp.toXContent(build.field("text"), ToXContent.EMPTY_PARAMS);
								    build.endObject();
								    Map<String, Object> map = XContentHelper.convertToMap(build.bytes(), false).v2();
								    Map<String, Object> txt= (Map) map.get("text");
								    Map<String, Object> termVec= (Map) txt.get("term_vectors");
								   	Map<String, Object> text= ((Map) termVec.get("text"));
									Map<String, Object> terms=((Map) text.get("terms"));

									for(Map.Entry map1:terms.entrySet())
									{
								       	documentLength+=(Integer)((Map) terms.get(map1.getKey())).get("term_freq");
								    	
								    }
									tfIDf= (tfScore/(tfScore+1.5+(0.5*(documentLength/totallen))))*Math.log(84678/count1);
//								    okapitf= tfScore/(tfScore+1.5+(0.5*(documentLength/totallen)));
								    if(RankedList.containsKey(documentid))
													{
														RankedList.put(documentid, RankedList.get(documentid)+tfIDf);
													}
													else
													{ 
														
														RankedList.put((String)((JSONObject)objects).get("_id"), tfIDf);
													}
								}

//						}  //hits array
						
							}
							response=client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
						} while(response.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
						//scroll response till  here
					}
				}

				HashMap<String,Double> mapSorted= (HashMap<String, Double>) sortingMapByValue(RankedList);
				int count=0;
				for(String key:mapSorted.keySet()){
					count++;
					if(count<=1000)
					{
					System.out.println(querryNo+"\t Q0"+"\t"+key+"\t"+count+"\t"+ mapSorted.get(key)+"\t Exp");
					}
	
		
				}
			}

		}
		

		catch(Exception ex){
		    String message = getStackTrace(ex);
		}
	}


	static String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}


	private static HashMap<String, Double> sortingMapByValue(Map<String, Double> map) {
	        
	        Set<Entry<String,Double>> mapEntries = map.entrySet();
	        
	        // used linked list to sort, because insertion of elements in linked list is faster than an array list. 
	        List<Entry<String,Double>> lst = new LinkedList<Entry<String,Double>>(mapEntries);

	        // sorting the List
	        Collections.sort(lst, new Comparator<Entry<String,Double>>() {

	            public int compare(Entry<String, Double> element,
	                    Entry<String, Double> element2) {
	                	return element2.getValue().compareTo(element.getValue());
	            }
	        });
	        
	        // Storing the list into Linked HashMap to preserve the order of insertion. 
	        Map<String,Double> map2 = new LinkedHashMap<String, Double>();
	        for(Entry<String,Double> entry: lst) {
	            map2.put(entry.getKey(), entry.getValue());
	        }
	      
	            return (HashMap<String, Double>) map2;
	}
	
	
}
