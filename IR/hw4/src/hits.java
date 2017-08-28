import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class hits{
	public static void main(String[] args) throws IOException {
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","6s")
				.put("cluster.name", "elastic4").build();
		
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));


		Double avg_doc_len = avglength.avglength();
		Double k1= 2.0;
		Double b= 0.75;
		// int query_freq=1;
		File file = new File("C:/Users/Snm/Downloads/AP_DATA/Relevant.txt");
		
		PrintWriter hubwriter = new PrintWriter("C:/Users/Snm/Desktop/HW4/hubs.txt", "UTF-8");
		PrintWriter authoritywriter = new PrintWriter("C:/Users/Snm/Desktop/HW4/authority.txt", "UTF-8");
		Scanner scannerline = new Scanner(file);
		ArrayList<String> rootlist = new ArrayList<String>();
		Map<String, Double> querymap = new ConcurrentHashMap<String, Double>();
		Map<String, IntTuple> hitmap = new ConcurrentHashMap<String, IntTuple>();
		Map<String, IntTuple> newhitmap = new ConcurrentHashMap<String, IntTuple>();
		Map<String, Double> sortedmap = new ConcurrentHashMap<String, Double>();
		while (scannerline.hasNext()) {

			String query = scannerline.next();
			System.out.println("q="+query);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("term",query);
			params.put("field", "text");



			SearchResponse response = client.prepareSearch("elastic4_final")
					.setTypes("document")
					.addScriptField("field", (new Script(ScriptType.INLINE,"groovy","_index[field][term].tf()",params)))
					.setQuery(QueryBuilders.matchQuery("text", query))
					.setScroll(new TimeValue(60000))
                    .setSize(1000)
                    .addScriptField("DOC_LENGTH", (new Script("doc['text'].values.size()")))
                    .execute()
                    .actionGet();
			do {
				
			long hits=response.getHits().getTotalHits();
			for ( SearchHit hit : response.getHits()){  
				String docNo= hit.getId();
				Double tf = (double) hit.getScore();
				int doc_len = hit.getFields().get("DOC_LENGTH").getValue();

				Double x= (77540+0.5)/(hits + 0.5);
				Double y= (tf+(k1*tf))/(tf+(k1*(1-b)+(b*(doc_len/avg_doc_len))));
				Double z=(tf+(200*tf))/(tf+200);		

				Double bm_25 =  Math.log(x*y*z);

				querymap.put(docNo, querymap.getOrDefault(docNo,0.0) + bm_25);			
			}
			
			response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
	        } while(response.getHits().getHits().length != 0);


		}

		sortedmap=sortMapByValues(querymap);
		int count=0;
		for(String n:sortedmap.keySet()){ 

			count++;
			if(count<=1000)
			{
				rootlist.add(n);
			}
		}

		ArrayList<String> baselist = new ArrayList<String>();

		for(String n:rootlist)
		{
			baselist.add(n);
		}
		
		for(String n:rootlist)
		{ 
			if(baselist.size()<10000)
			{
				SearchResponse response = client.prepareSearch("elastic4_final")
						.setTypes("document")
						.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
						.setQuery(QueryBuilders.idsQuery("document").addIds(n))
						.execute()
						.actionGet();
				for ( SearchHit hit : response.getHits().hits()){  
					String docNo= hit.getId();
					Map<String, Object> map = hit.getSource();
					String id = (String) map.get("docno");
					List<String> li=   (List<String>) map.get("out_links");
					List<String> ol=  (List<String>) map.get("in_links");;
					if(baselist.size()>10000)
						break;
					if(li.size()>200)
					{
						Collections.shuffle(li);
						baselist.addAll(li.subList(0, 200));
						baselist.addAll(ol);
					}
					else
					{
						baselist.addAll(li);
						baselist.addAll(ol);
					}
				}


			}
		}

		
		List<String> baselist2= baselist.subList(0, 10000);

		for(String s:baselist2)
		{
			hitmap.put(s, new IntTuple(1.0,1.0));
		}

		for(int count1=0;count1<=100;count1++)
		{
			double N=0;
			for(String s:baselist2)
			{
				SearchResponse response = client.prepareSearch("elastic4_final")
						.setTypes("document")
						.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
						.setQuery(QueryBuilders.idsQuery("document").addIds(s))
						.execute()
						.actionGet();
				double authscore = 0;
				for ( SearchHit hit : response.getHits().hits()){  
					
					

					Map<String, Object> map = hit.getSource();
				
					List<String> ol=  (List<String>) map.get("in_links");
					if(ol!=null){
						for(String k:ol)
						{
							try{
							if(hitmap.get(k)!=null)
								authscore = authscore+hitmap.get(k).hub;
							}
							
							catch(Exception e){
								authscore = authscore+1;
							}
						}
					}
					else
						authscore = authscore+1;
					N=N+authscore*authscore;

					
				}
				
				newhitmap.put(s, new IntTuple(authscore,hitmap.get(s).hub));
			}

			N= Math.sqrt(N);

			for(String s:newhitmap.keySet())
			{
				newhitmap.put(s, new IntTuple((newhitmap.get(s).authority/N), newhitmap.get(s).hub));
			}

			N=0;
			for(String s:baselist2)
			{
				SearchResponse response = client.prepareSearch("elastic4_final")
						.setTypes("document")
						.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
						.setQuery(QueryBuilders.idsQuery("document").addIds(s))
						.execute()
						.actionGet();
				double hubscore = 0;
				for ( SearchHit hit : response.getHits().hits()){  
					String docNo= hit.getId();
					

					Map<String, Object> map = hit.getSource();
					String id = (String) map.get("docno");
					List<String> li=   (List<String>) map.get("out_links");
					for(String k:li)
					{
						if(newhitmap.containsKey(k))
							hubscore = hubscore+hitmap.get(k).hub;
					}
					

					
				}
				N=N+hubscore*hubscore;
				newhitmap.put(s, new IntTuple(newhitmap.get(s).authority,hubscore));
			}

			N= Math.sqrt(N);

			for(String s:newhitmap.keySet())
			{
				newhitmap.put(s, new IntTuple(newhitmap.get(s).authority,newhitmap.get(s).hub/N));
				
				
			}
			


			if(hasConverged(hitmap,newhitmap))
			{
				break;
			}
			else
			{
				hitmap.putAll(newhitmap);
				newhitmap.clear();
			}
		}
		
		Map<String, IntTuple> sortedhub = sorthub(hitmap);
		Map<String, IntTuple> sortedauthorrity = sortauthority(hitmap);
		
		int hubcount=0;
		
		for(String k:sortedhub.keySet())
		{
			hubcount++;
			if(hubcount<=500)
			
			{
				SearchResponse response = client.prepareSearch("elastic4_final")
			
			.setTypes("document")
			.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
			.setQuery(QueryBuilders.idsQuery("document").addIds(k))
			.execute()
			.actionGet();
	int outlinkcount = 0;
	String nid="";
	for ( SearchHit hit : response.getHits().hits()){  
		String docNo= hit.getId();
		

		Map<String, Object> map = hit.getSource();
		String id = (String) map.get("docno");
		nid=id;
		List<String> li=   (List<String>) map.get("out_links");
		outlinkcount= li.size();
		
			}
	
//	hubwriter.write("--docid:--"+nid+"--hubscore:--"+sortedhub.get(k).hub+"--outlinkcount:--"+outlinkcount+"\n");
	hubwriter.write(nid+"\t"+sortedhub.get(k).hub+"\n");
	
	
		}
		}
		hubwriter.close();
		
int authcount=0;
		String nid="";
		for(String k:sortedauthorrity.keySet())
		{
			authcount++;
			if(authcount<=500)
			
			{
				SearchResponse response = client.prepareSearch("elastic4_final")
						.setTypes("document")
						.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
						.setQuery(QueryBuilders.idsQuery("document").addIds(k))
						.execute()
						.actionGet();
	int inlinkcount = 0;
	for ( SearchHit hit : response.getHits().hits()){  
		String docNo= hit.getId();
		Map<String, Object> map = hit.getSource();
		String id = (String) map.get("docno");
		nid=id;
		List<String> li=   (List<String>) map.get("in_links");
		if(li!=null)
		inlinkcount= li.size();
		
			}
	
//	authoritywriter.write("--docid:--"+nid+"--authorityscore:--"+sortedauthorrity.get(k).hub+"--inlinkcount:--"+inlinkcount+"\n");
	authoritywriter.write(nid+"\t"+sortedauthorrity.get(k).hub+"\n");
	
	
		}
		}
		
		authoritywriter.close();

	}

	private static boolean hasConverged(Map<String, IntTuple> hitmap,Map<String, IntTuple> newhitmap)
	{
		boolean status = true;
		for(String docNo : hitmap.keySet())
		{

			if((Math.floor(newhitmap.get(docNo).authority * 1000) != Math.floor(hitmap.get(docNo).authority * 1000)) ||
					(Math.floor(newhitmap.get(docNo).hub * 1000) != Math.floor(hitmap.get(docNo).hub * 1000)))
			{
//				System.out.println(newhitmap.get(docNo).authority +"--"+hitmap.get(docNo).authority+"----"+newhitmap.get(docNo).hub+"---"+hitmap.get(docNo).hub+"---"+docNo);
				
				
				
				return false;
			}
		}

		return true;
	}
	private static HashMap<String, Double> sortMapByValues(Map<String, Double> querymap) {

		Set<Entry<String,Double>> mapEntries = querymap.entrySet();

		List<Entry<String,Double>> aList = new LinkedList<Entry<String,Double>>(mapEntries);


		Collections.sort(aList, new Comparator<Entry<String,Double>>() {


			public int compare(Entry<String, Double> ele1,
					Entry<String, Double> ele2) {

				return ele2.getValue().compareTo(ele1.getValue());
			}
		});

		Map<String,Double> aMap2 = new LinkedHashMap<String, Double>();
		for(Entry<String,Double> entry: aList) {
			aMap2.put(entry.getKey(), entry.getValue());
		}

		return (HashMap<String, Double>) aMap2;
	}
	
	private static HashMap<String, IntTuple> sorthub(Map<String, IntTuple> querymap) {

		Set<Entry<String,IntTuple>> mapEntries = querymap.entrySet();

		List<Entry<String,IntTuple>> aList = new LinkedList<Entry<String,IntTuple>>(mapEntries);


		Collections.sort(aList, new Comparator<Entry<String,IntTuple>>() {


			public int compare(Entry<String, IntTuple> ele1,
					Entry<String, IntTuple> ele2) {
				Double ele2double= ele2.getValue().hub;
				Double ele1double= ele1.getValue().hub;

				return ele2double.compareTo(ele1double);
			}
		});

		Map<String,IntTuple> aMap2 = new LinkedHashMap<String, IntTuple>();
		for(Entry<String,IntTuple> entry: aList) {
			aMap2.put(entry.getKey(), entry.getValue());
		}

		return (HashMap<String, IntTuple>) aMap2;
	}
	private static HashMap<String, IntTuple> sortauthority(Map<String, IntTuple> querymap) {

		Set<Entry<String,IntTuple>> mapEntries = querymap.entrySet();

		List<Entry<String,IntTuple>> aList = new LinkedList<Entry<String,IntTuple>>(mapEntries);


		Collections.sort(aList, new Comparator<Entry<String,IntTuple>>() {


			public int compare(Entry<String, IntTuple> ele1,
					Entry<String, IntTuple> ele2) {
				Double ele2double= ele2.getValue().authority;
				Double ele1double= ele1.getValue().authority;

				return ele2double.compareTo(ele1double);
			}
		});

		Map<String,IntTuple> aMap2 = new LinkedHashMap<String, IntTuple>();
		for(Entry<String,IntTuple> entry: aList) {
			aMap2.put(entry.getKey(), entry.getValue());
		}

		return (HashMap<String, IntTuple>) aMap2;
	}
}
