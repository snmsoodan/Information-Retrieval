import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

public class JelinekMercer {
	
	public static void main(String[] args) throws UnknownHostException{
		try
		{

			Settings settings = Settings.builder()
					.put("client.transport.sniff", true)
					.put("client.transport.ping_timeout","3600s")
					.put("cluster.name", "elasticsearch").build();

			TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
			
			
//			Double Vocabulary=(double) totalLength.vocabolary();
//			Double totallen=totalLength.lengthAvg();

			long sumttf=0;
			int queryno = -1;
			Map<String,String> hm=new HashMap<String,String>();
			Map<String,Float> TFMap=new HashMap<String,Float>();
			for (String line : Files.readAllLines(Paths.get("C:/Users/Snm/Downloads/AP_DATA/modified_queries.txt")))  
			{
				String[] queryparams= line.split("\\s+");
				for(String i: queryparams)
				{
					if(!TFMap.containsKey(i))
					{
					/*String Pattern = "(\\d+\\.)";
					if(i.matches(Pattern))
					{
					break;	

					}*/
					final Map<String, Object> params = new HashMap<String, Object>();
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
					
					
					if((response.getHits().totalHits()==0))
					{
						TFMap.put(i, 0.00001F);
					}
					else
					{
						TFMap.put(i, response.getHits().maxScore());
					}
					
					
					response=client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
					} while(response.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
					//scroll response till  here
					}  //if loop
				}
			}

			File folder = new File("C:\\Users\\Snm\\Downloads\\AP_DATA\\ap89_collection");
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++)
			{
				//for (int i = 0; i < 1; i++) {
				//System.out.println(listOfFiles[i].getPath());

				File mFile = new File(listOfFiles[i].getPath());
				FileInputStream fis = new FileInputStream(mFile);

				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mFile)));
				String line = "",docno="",text="";
				boolean check=false,checkdoc=false;
				
				//System.out.println("before while");
				while( (line = br.readLine()) != null){
					if(line.startsWith("<DOC>"))
					{
						checkdoc=true;
						docno="";
						text="";
					}
					if(line.endsWith("</DOC>"))
					{
						checkdoc=false;
						hm.put(docno,text);

					}
					int line_length=line.length();
					if(line.startsWith("<DOCNO>"))
					{

						//System.out.println(line.substring(8,(line_length - 9)));
						docno=(line.substring(8,(line_length - 9)));
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
							//	System.out.println(line);
							text=text.concat(" "+line);
						}
					}



				}


				br.close();
				fis.close();


			}

			for (String line : Files.readAllLines(Paths.get("C:/Users/Snm/Downloads/AP_DATA/modified_queries.txt")))  
			{
				HashMap<String,Double> Rankedlist = new HashMap<String,Double>();

				String[] queryparams= line.split("\\s+");
				String Pattern = "(\\d+\\.)";

				int flagtotalfrequency=0;
				for(Map.Entry m:hm.entrySet())
				{
					Double JM=0.0;
					XContentBuilder builder;

//					TermVectorsResponse resp = client.prepareTermVectors()
//							.setIndex("ap_dataset2")
//							.setType("document")
//							.setId((String)m.getKey())
//							.execute().actionGet();
					
					TermVectorsResponse resp = client.prepareTermVectors().setIndex("ap_dataset")
   						 .setType("document").setId((String)m.getKey()).setSelectedFields("text").execute().actionGet();
					
					builder = XContentFactory.jsonBuilder().startObject();
					resp.toXContent(builder.field("text"), ToXContent.EMPTY_PARAMS);
					builder.endObject();
					String documentid=(String)m.getKey();
					if(m.getValue().toString().length()==1||m.getValue().toString().length()==0)
					{
						continue;
					}
					/*
					if(flagtotalfrequency==0)
					{
						sumttf=resp.getFields().terms("text").getSumTotalTermFreq();

						flagtotalfrequency++;
					}*/
					sumttf=20984205;
					Map<String, Object> map = XContentHelper.convertToMap(builder.bytes(), false).v2();
					Map<String, Object> txt= (Map) map.get("text");

					Map<String, Object> termVec= ((Map) txt.get("term_vectors"));
					Map<String, Object> text= ((Map) termVec.get("text"));
					Map<String, Object> terms=((Map) text.get("terms"));
					Double  doclength = 0.00;

					for(Map.Entry am:terms.entrySet())
					{
						doclength+=(Integer)((Map) terms.get(am.getKey())).get("term_freq");

					}

					for(String i: queryparams)
					{
						Integer tfscore=0;


						if(i.matches(Pattern))
						{
							queryno = Integer.parseInt(i.replace(".", ""));

						}
						else
						{

							for(Map.Entry am:terms.entrySet())
							{
								if(terms.containsKey(i))
								{
									//System.out.println(((Map) terms.get(splitted[s])).get("term_freq"));
									tfscore=(Integer) ((Map) terms.get(i)).get("term_freq");
									
								}	
								}
							
						
							Double term1 = (0.6*tfscore/doclength)+(0.4*(TFMap.get(i)-tfscore)/(sumttf-doclength));
							JM=JM+(Math.log(term1));
							
							/*
							  System.out.println(term1);
							System.out.println("tfscore is :" + tfscore);
							System.out.println("doclegth is :" + doclength);
							System.out.println("summttf is :" + sumttf);
							System.out.println("term--"+i+"--value"+TFMap.get(i));
							*/

						}

					}
					Rankedlist.put(documentid, 
							Rankedlist.getOrDefault(documentid, 0.0) + JM);	

					
				}


				HashMap<String,Double> SortedMap= (HashMap<String, Double>) sortMapByValues(Rankedlist);
				int count=0;
				//System.out.println("printing sorted values map");
				for(String key:SortedMap.keySet()){
					count++;
					if(count<=1000)
					{

							System.out.println(queryno+"\t Q0"+"\t"+key+"\t"+count+"\t"+ SortedMap.get(key)+"\tExp");
					}
					


				}


			}
		}

		catch(Exception ex){
			String message = getStackTrace(ex);
			ex.printStackTrace();
			System.out.println("In Exception Block");
			System.out.println(ex.getMessage());
		}


}
static String getStackTrace(final Throwable throwable) {
	final StringWriter sw = new StringWriter();
	final PrintWriter pw = new PrintWriter(sw, true);
	throwable.printStackTrace(pw);
	return sw.getBuffer().toString();
}
private static HashMap<String, Double> sortMapByValues(Map<String, Double> aMap) {

	Set<Entry<String,Double>> mapEntries = aMap.entrySet();

	// used linked list to sort, because insertion of elements in linked list is faster than an array list. 
	List<Entry<String,Double>> aList = new LinkedList<Entry<String,Double>>(mapEntries);

	// sorting the List
	Collections.sort(aList, new Comparator<Entry<String,Double>>() {

		public int compare(Entry<String, Double> ele1,Entry<String, Double> ele2) {

			return ele2.getValue().compareTo(ele1.getValue());
		}
	});

	// Storing the list into Linked HashMap to preserve the order of insertion. 
	Map<String,Double> aMap2 = new LinkedHashMap<String, Double>();
	for(Entry<String,Double> entry: aList) {
		aMap2.put(entry.getKey(), entry.getValue());
	}

	return (HashMap<String, Double>) aMap2;
}
}
