import java.io.BufferedReader;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.google.gson.Gson;
import com.json.parsers.JsonParserFactory;


public class indexCreation3 {

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
//		File fl=new File("C:\\Users\\Snm\\Desktop\\HW3\\docs");
		File fl=new File("C:\\Users\\Snm\\Desktop\\HW3");

		File[] filesList=fl.listFiles();
		Map<String,String> hmap= new HashMap<String,String>();
		int x=0,y=0;
		
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","6s")
				.put("cluster.name", "elastic4").build();
		
		
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.20.10.4"), 9300))
//				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.20.10.6"), 9300));
		
		System.out.println("here");
				
//		BulkRequestBuilder brb=client.prepareBulk();
		int cnt=0;
		for(int i=0;i<filesList.length;i++){
			File newFile= new File(filesList[i].getPath());
//			File newFile= new File("C:\\Users\\Snm\\Desktop\\HW3\\docs1");
			String line = "",docno="",text="",HTTPheader="",title="",in_links="",out_links="",depth="",url="",author="",html_Source="";
			FileInputStream fis = new FileInputStream(newFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
			boolean check=false,checkdoc=false;
			org.codehaus.jackson.map.ObjectMapper ob=new org.codehaus.jackson.map.ObjectMapper();
			JSONArray inl=null,oll=null,inl2 = null,oll2=null;
			
			while( (line = br.readLine()) != null){
				int lineLength=line.length();
				if(line.startsWith("<doc>"))
				{
					checkdoc=true;
					text="";
				    docno="";
				    HTTPheader="";
				    title="";
				    in_links="";
				    out_links="";
				    depth="";
				    url="";
				    author="";
				    html_Source="";
				}
			
				if(line.startsWith("<docno>"))
				{
					x+=1;
					docno=(line.substring(7,(lineLength - 8)));
				}
				

				
				
				//text
				
				if(line.startsWith("<text>"))
				{
					if(lineLength<13){
						continue;
					}
					text=(line.substring(6,(lineLength - 7)));
				}
				
				//HTTPheader
				
				if(line.startsWith("<HTTPheader>"))
				{
					if(lineLength<25){
						continue;
					}
					HTTPheader=(line.substring(12,(lineLength - 13)));
				}
				
				
				//title
				
				if(line.startsWith("<title><title>"))
				{
					if(lineLength<16)
					{
						continue;
					}
					if(lineLength<30)
					{
						continue;
					}
					title=(line.substring(14,(lineLength - 16)));
				}
				
				if(!line.startsWith("<title><title>")&&line.endsWith("</title></title>"))
				{
					title=(line.substring(0,(lineLength - 16)));
				}
				
				
				
				
				//inlinks
				if(line.startsWith("<in_links>"))
				{
					if(lineLength<21){
						continue;
					}
					
					in_links=(line.substring(10,(lineLength - 11)));
//					System.out.println("object "+actualObj);
				}
				
				//outlinks
				if(line.startsWith("<out_links>"))
				{
					if(lineLength<23){
						continue;
					}
					out_links=(line.substring(11,(lineLength - 12)));
				}
				
				//depth
				if(line.startsWith("<depth>"))
				{
					if(lineLength<15){
						continue;
					}
					depth=(line.substring(7,(lineLength - 8)));
				}
				
				//depth
				if(line.startsWith("<url>"))
				{
					if(lineLength<11){
						continue;
					}
					url=(line.substring(5,(lineLength - 6)));
				}
				
				//author
				if(line.startsWith("<author>"))
				{
					if(lineLength<17){
						continue;
					}
					author=(line.substring(8,(lineLength - 9)));
//					System.out.println("author "+author);
				}
				
				
				//html_Source
				
				else if(line.startsWith("<html_Source><!doctype html>"))
				{
					check=true;
					html_Source=html_Source.concat("<!doctype html>");
				}
				else if(line.endsWith("</html></html_Source>"))
				{
					html_Source=html_Source.concat("</html>");
					check=false;
				}
				if(check)
				{
					if(!(line.equals("<html_Source><!doctype html>")))
					{
						html_Source=html_Source.concat(" "+line);
					}
				}
				
				
				if(line.endsWith("</doc>"))
				{
					
					HashSet<String> li = new HashSet<String>(); 
					HashSet<String> ol = new HashSet<String>();
					
					HashSet<String> li2 = new HashSet<String>(); 
					HashSet<String> ol2 = new HashSet<String>();
//					HashSet<String> li="";
//					HashSet<String> ol="";
//					String li="";
//					String ol="";
//					JSONArray li,ol;
//					ArrayList<String> li=new ArrayList<String>();
//					ArrayList<String> ol=new ArrayList<String>();
					String Auth="";
					boolean chk=false;
			
					GetResponse respMerge = client.prepareGet("es", "document",URLEncoder.encode(docno,"UTF-8"))
							.execute()
							.actionGet();
					
					
					
					if (respMerge.isExists()) {
						
						JSONParser parser = new JSONParser();
						 inl=(JSONArray)parser.parse(in_links);
						 oll = (JSONArray)parser.parse(out_links);
						 
						 if (inl != null) { 
							   int len = inl.size();
							   for (int i1=0;i1<len;i1++){ 
							    li2.add(inl.get(i1).toString());
							   } 
							} 
						 
						 
						 if (oll != null) { 
							   int len = oll.size();
							   for (int i1=0;i1<len;i1++){ 
							    ol2.add(oll.get(i1).toString());
							   } 
							} 
						 
						 
						
						Map<String, Object> mp = respMerge.getSourceAsMap();
						if(mp.get("in_links")!=null){
							li=  new HashSet<String>((ArrayList<String>)mp.get("in_links"));
						}
						
						if(mp.get("out_links")!=null){
						ol = new HashSet<String>((ArrayList<String>)mp.get("out_links"));
						}
						
						
						li.addAll(li2);
						ol.addAll(ol2);
						
						
						
						 inl2=(JSONArray)parser.parse(ob.writeValueAsString(li));
						 oll2 = (JSONArray)parser.parse(ob.writeValueAsString(ol));
						
//						inl2.add(inl);
//						oll2.add(oll);
						
						Auth=(String)(mp.get("author"));
						chk=true;
					}
					
					if(!Auth.contains(author)){
						Auth=Auth.concat(" and "+author);
					}
					
				if(cnt==29271)
					{
//				if(cnt==1)
//				{
					break;
				}
				
				if(chk)
				{
					
					
					IndexResponse response =client.prepareIndex("es", "document",URLEncoder.encode(docno,"UTF-8"))
							 .setSource(jsonBuilder()
					                    .startObject()
					                        .field("docno", docno)
					                        .field("HTTPheader", HTTPheader)
					                        .field("title", title)
					                        .field("text", text)
					                        .field("html_Source", html_Source)
					                        .field("in_links", inl2)
					                        .field("out_links", oll2)
					                        .field("author", Auth)
					                        .field("depth", depth)
					                        .field("url", url)
					                        
					                    .endObject()
					                  )
							.get();
				}
				else{				
					JSONParser parser = new JSONParser();
					JSONArray array=(JSONArray)parser.parse(in_links);
					JSONArray array1 = (JSONArray)parser.parse(out_links);
					

					
					IndexResponse response =client.prepareIndex("es", "document",URLEncoder.encode(docno,"UTF-8"))
							 .setSource(jsonBuilder()
					                    .startObject()
					                        .field("docno", docno)
					                        .field("HTTPheader", HTTPheader)
					                        .field("title", title)
					                        .field("text", text)
					                        .field("html_Source", html_Source)
					                        .field("in_links",array)
					                        .field("out_links",array1)
					                        .field("author", author)
					                        .field("depth", depth)
					                        .field("url", url)
					                        
					                    .endObject()
					                  )
							.get();
				}
					
					
					
					checkdoc=false;
					cnt++;
					hmap.clear();
					System.out.println(cnt);
				}
				
				
				
			}
			
			br.close();
			fis.close();
		}
			System.out.println("out of loop");
		

	}

}
