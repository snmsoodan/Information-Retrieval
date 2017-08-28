import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Queue;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRules.RobotRulesMode;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.robots.SimpleRobotRules.RobotRulesMode;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.mapper.ObjectMapper;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.pattern.Util;
public class Frontier7 {
	static HashMap<String,ArrayList<RobotRule>> RobotRules = new HashMap<String,ArrayList<RobotRule>>();
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException,NumberFormatException, InterruptedException{
		
		String url1 ="http://en.wikipedia.org/wiki/Lists_of_nuclear_disasters_and_radioactive_incidents";
		String url2 ="https://en.wikipedia.org/wiki/Nuclear_and_radiation_accidents_and_incidents";
		String url3 ="https://en.wikipedia.org/wiki/Kyshtym_disaster";
		
		PrintWriter writer = new PrintWriter("C:/Users/Snm/Desktop/HW3/docs");
		PrintWriter writer2 = new PrintWriter("C:/Users/Snm/Desktop/HW3/links");
		

		
		ArrayList<XContentBuilder> builderList = new ArrayList<XContentBuilder>();

		HashMap<String, Long> elapsedtime = new HashMap<String, Long>();
		
		String[] Relevant ={"Fukushima","Nuclear","Power","Plant","Fukushima","Nuclear","reactor","zirconium",
				"radiation-induced cancer","cancer","Zircaloy","core meltdown","contamination","death",
				"fission","reaction","control rods","SCRAM","decay heat","decay","radiation","fatilities","Chernobyl",
				"disaster","Kyshtym"," nuclear fuel","Kyzyltash","Ozyorsk"};
		
		String[] Relevant2={"meltdown","nuclear","Three Mile","accident","nuclear accident","reactor","nuclear meltdown","SL-1","disaster",
				"Chernobyl","radioactive","Kyshtym"};
		


		
//		PrintWriter writer = new PrintWriter("C:/Users/Snm/HW3/mergedlist_1.txt", "UTF-8");
		HashMap<String,String> textmap = new HashMap<String,String>();
		HashMap<String,String> htmlsourcemap = new HashMap<String,String>();
		LinkedList<Tuple> data = new LinkedList<Tuple>();
		
		//list for in and out links
		HashSet<String> a1= new HashSet<String>();
		
		
		
		String canurl1 = test.canUrl(url1);
		
	
		String canurl2 =test.canUrl(url2);
		
	
		String canurl3 =test.canUrl(url3);
		
		LinkedList<Tuple> myqueue = new LinkedList<Tuple>();
		org.codehaus.jackson.map.ObjectMapper ob=new org.codehaus.jackson.map.ObjectMapper();
		
		Connection.Response seedres = Jsoup.connect(url1).timeout(6000).execute();
		Document seeddoc = seedres.parse();
		String seedhttpheaders = seedres.headers().toString();
		String seedtext = seeddoc.body().text();
		String seedhtmlsource = seeddoc.html();
		
		Connection.Response seedres2 = Jsoup.connect(url2).timeout(6000).execute();
		Document doc2 = seedres2.parse();
		String seedhttpheaders2 = seedres2.headers().toString();
		String seedtext2 = doc2.body().text();
		String seedhtmlsource2 = doc2.html();
		
		Connection.Response seedres3 = Jsoup.connect(url3).timeout(6000).execute();
		Document doc3 = seedres3.parse();
		String seedhttpheaders3 = seedres3.headers().toString();
		String seedtext3 = doc3.body().text();
		String seedhtmlsource3 = doc3.html();
		
		
		String seedhead3 = doc3.select("title").toString();
		String seedhead2 = doc2.select("title").toString();
		String seedhead1 = seeddoc.select("title").toString();
		
		enqueue(new Tuple(url1,canurl1,1,false,a1,a1,seedhead1, 0, 1000,seedhttpheaders), myqueue);
		enqueue(new Tuple(url2,canurl2,1,false,a1,a1,seedhead2, 0, 1000,seedhttpheaders2), myqueue);
		enqueue(new Tuple(url3,canurl3,1,false,a1,a1,seedhead3, 0, 1000,seedhttpheaders3), myqueue);
		
		
		textmap.put(canurl1, seedtext);
		htmlsourcemap.put(canurl1, seedhtmlsource);
		
		textmap.put(canurl2, seedtext2);
		htmlsourcemap.put(canurl2, seedhtmlsource2);
		
		textmap.put(canurl3, seedtext3);
		htmlsourcemap.put(canurl3, seedhtmlsource3);
		
		
		int requests = 0;
		int present=0;
		Iterator<Tuple> iter = myqueue.iterator();
		
		int previouslevel=0;
		
		while(iter.hasNext())
		{
			int current1=peek(myqueue);
			
			
			try
			{
				int current=peek(myqueue);
				
				
				if(myqueue.get(current).level!=previouslevel)
				{
					myqueue=sortMapByValues(myqueue);
				}
				current=peek(myqueue);
				
				
				
				while(myqueue.get(current).visited==true)
				{
					current=current+1;
				}
				current1=current;
				
				int currentlevel = myqueue.get(current).level;
				String url = myqueue.get(current).rawurl;
				
				
				previouslevel=currentlevel;
				URL myurl = new URL(url);
				int port = myurl.getPort();
				if((myurl.getProtocol().equals("http")  && port == 80) ||
						(myurl.getProtocol().equals("https") && port == 443))
				{
					port = -1;
				}
				
				if(url.contains("#")) {
					url = url.substring(0,url.indexOf("#"));
				}

				if(url.contains("?")) {
					url = url.substring(0,url.indexOf("?"));
				}
				
				String canurl =myqueue.get(current).url;
				
				String domain = myurl.getProtocol() + "://" +
						myurl.getHost() +
						(port > -1 ? ":" + myurl.getPort() : "");
				
				elapsedtime.put(domain, System.nanoTime());
				if(isRobotAllowed(myurl))
				{
					
					long difference = 0;
					
					if(elapsedtime.containsKey(domain))
					{
						long starttime = elapsedtime.get(domain);

						difference = (starttime-System.nanoTime())/1000000000;
					}
					
					if((difference!=0)&&(difference<1))
					{
						Thread.sleep(1000);

					}
					
					Connection.Response res = Jsoup.connect(url).timeout(6000).execute();
					
					if (res.statusCode() != 200) {
						myqueue.get(current).visited=true;
		                continue;
		            }
					
					
					
					Document doc = res.parse();
					if(res.contentType().contains("html")&&doc.select("html").attr("lang").equalsIgnoreCase("en"))
					{
						if(requests==2000)
						{
							break;
						}
						requests++;
						System.out.println(requests);
					
						String httpheaders = res.headers().toString();
						String text = doc.body().text();
						String htmlsource = doc.html();
						String head2 = doc.select("title").toString();
						
						textmap.put(canurl,text);
						htmlsourcemap.put(canurl, htmlsource);
						
						set(new Tuple(myqueue.get(current).rawurl,myqueue.get(current).url,myqueue.get(current).inlinkcount,true,myqueue.get(current).incominglinks,myqueue.get(current).outgoinglinks,head2,myqueue.get(current).level, myqueue.get(current).relevance,httpheaders), current,myqueue);
							
						Elements links = doc.select("a[href]");
						for (Element link : links) {
							
							
							
							String urls= link.attr("abs:href").toString();
							
							String linkTitle=link.text().toLowerCase();
							
							
							int relvFactor=0;
							
							for(int c=0;c<Relevant2.length;c++)
							{
								if(linkTitle.contains(Relevant2[c].toLowerCase())){
//									System.out.println(Relevant[c]);
									relvFactor++;
								}
							}
							
							
							
							if(urls.contains("#")) {
								urls = urls.substring(0,urls.indexOf("#"));
							}


							if(urls.contains("?")) {
								urls = urls.substring(0,urls.indexOf("?"));
							}
							
							String canurls=test.canUrl(urls);
//							System.out.println("href can link "+canurls);
							
					
							
							
							HashSet<String> Outlinks = new HashSet<String>();
							if(myqueue.get(current).outgoinglinks.isEmpty())
							{
								Outlinks.add(canurls);
							}
							else
							{
								Outlinks= myqueue.get(current).outgoinglinks;
								Outlinks.add(canurls);
							}
							
							
							set(new Tuple(myqueue.get(current).rawurl,myqueue.get(current).url,myqueue.get(current).inlinkcount,true,myqueue.get(current).incominglinks,Outlinks,myqueue.get(current).head, myqueue.get(current).level, myqueue.get(current).relevance, myqueue.get(current).httpheader), current,myqueue);
							
							if(relvFactor==0)
							{
								continue;
							}
							
							if(urls.contains("https://en.wikipedia.org/w/index.php")|
									urls.contains("Category:")|
									urls.contains("Portal:")|
									urls.contains("index.php")|
									urls.contains(":Talk")|
									urls.contains("/w/")|
									urls.contains("facebook.com")|
									urls.contains("twitter.com")|
									urls.contains("linkedin.com")|
									urls.contains("facebook.com"))
									{
										continue;
									}
							
							if(urls.length()==0){
								continue;
							}
							
							if(urls.substring(urls.length()-3,urls.length()).equals("png")
									|urls.substring(urls.length()-3,urls.length()).equals("jpg")
									|urls.substring(urls.length()-3,urls.length()).equals("pdf")
									)
							{
								continue;
							}
							
							
							
							URL myurls = new URL(urls);
							if(contains(canurls, myqueue))
							{
								int index = index(canurls,myqueue);
								HashSet<String> inlinks1 = myqueue.get(index).incominglinks;
								inlinks1.add(canurl);
								set(new Tuple(myqueue.get(index).rawurl,myqueue.get(index).url,(myqueue.get(index).inlinkcount)+1,true,inlinks1,myqueue.get(index).outgoinglinks,myqueue.get(index).head, myqueue.get(index).level,myqueue.get(index).relevance,myqueue.get(index).httpheader), index,myqueue);							
								continue;
							}
							
							else
							{
								HashSet<String> inlinks = new HashSet<String>();
								HashSet<String> Outlinks2 = new HashSet<String>();
								inlinks.add(canurl);
								enqueue(new Tuple(urls,canurls,1,false,inlinks,Outlinks2,"", myqueue.get(current).level+1,relvFactor,""),myqueue);
							}
							
							
						}
						
						
					
						
					
					
					
					
				}
				else{
					myqueue.get(current).visited=true;
					continue;
				}
						
						
					
						
				
				}
				else{
					myqueue.get(current).visited=true;
					continue;
				}
				

				if(requests%5==0&&requests!=0&&present!=requests)
				{
					present=requests;
					int pointer2=requests-5;
					for(int i=0;i<5;i++)
					{

						HashSet<String> li = new HashSet<String>(); 
						HashSet<String> ol = new HashSet<String>(); 

				
						
						
						writer.print("<doc>"+"\n");
						
						writer.print("<docno>"+myqueue.get(pointer2).url+"</docno>"+"\n");
						writer.print("<HTTPheader>"+myqueue.get(pointer2).httpheader+"</HTTPheader>"+"\n");
						writer.print("<title>"+myqueue.get(pointer2).head+"</title>"+"\n");
						writer.print("<text>"+textmap.get(myqueue.get(pointer2).url)+"</text>"+"\n");
						writer.print("<in_links>"+ob.writeValueAsString(myqueue.get(pointer2).incominglinks)+"</in_links>"+"\n");
						writer.print("<out_links>"+ob.writeValueAsString(myqueue.get(pointer2).outgoinglinks)+"</out_links>"+"\n");
						writer.print("<depth>"+myqueue.get(pointer2).level+"</depth>"+"\n");
						writer.print("<url>"+myqueue.get(pointer2).rawurl+"</url>"+"\n");
						writer.print("<html_Source>"+htmlsourcemap.get(myqueue.get(pointer2).url)+"</html_Source>"+"\n");
						writer.print("<author>"+"sanamdeep"+"</author>"+"\n");
						
						writer.print("</doc>"+"\n");
						
						writer2.print(myqueue.get(pointer2).url+" "+ob.writeValueAsString(myqueue.get(pointer2).outgoinglinks)+"\n");
						
						pointer2++;
					}
//					htmlsourcemap.clear();
//					textmap.clear();
				}
				
				
				
				
			}
			catch(Exception ex)
			{
				myqueue.get(current1).visited=true;
				continue;
			}
			
			
		}
		
	
		
		writer.flush();
		writer2.flush();
		
	}
	
	public static void enqueue(Tuple item,LinkedList<Tuple> data)
	{
		data.addLast(item);
	}
	public static int peek(LinkedList<Tuple> data) 
	{
		int count = 0;
		for(Tuple i: data)
		{
			if(i.visited==true)
			{
				++count;
				continue;
			}

		}
		return count;
	}
	
	public static LinkedList<Tuple> sortMapByValues(LinkedList<Tuple> aMap) {

		LinkedList<Tuple> mapEntries = aMap;


		// sorting the List
		Collections.sort(aMap, new Comparator<Tuple>() {

			@Override
			public int compare(Tuple ele1, Tuple ele2) {

				Integer a1value= (10000*(100-ele1.level))+(100*ele1.relevance)+(10*ele1.inlinkcount);
				Integer a2value= (10000*(100-ele2.level))+(100*ele2.relevance)+(10*ele2.inlinkcount);
				return a2value.compareTo(a1value);


			}
		});

		return aMap;
		// Storing the list into Linked HashMap to preserve the order of insertion. 


	}
	
	public static int index(String item, LinkedList<Tuple> data)
	{
		int count = 0;

		for(Tuple i: data)
		{

			if(i.url.equals(item))
			{
				return count;
			}

			count++;
		}

		return (0);
	}
	
	public static void set(Tuple item, int i,LinkedList<Tuple> data)
	{
		data.set(i, item);	
	}
	
	public static Boolean contains(String item,LinkedList<Tuple> data)
	{
		int count = 0;

		for(Tuple i: data)
		{

			if(i.url.equals(item))
			{
				return true;
			}


		}

		return false;
	}
	
	
	
	public static boolean isRobotAllowed(URL url) throws IOException 
	{
		String strHost = url.getHost();

		String strRobot = "http://" + strHost + "/robots.txt";
		URL urlRobot;
		try { urlRobot = new URL(strRobot);
		} catch (MalformedURLException e) {
			// something weird is happening, so don't trust it
			return false;
		}


		String strCommands ;
		try 
		{
			InputStream urlRobotStream = urlRobot.openStream();
			byte b[] = new byte[1000];
			int numRead = urlRobotStream.read(b);
			if(numRead==-1)
				return true;
			strCommands = new String(b, 0, numRead);
			while (numRead != -1) {
				numRead = urlRobotStream.read(b);
				if (numRead != -1) 
				{
					String newCommands = new String(b, 0, numRead);
					strCommands += newCommands;
				}
			}
			urlRobotStream.close();
		} 
		catch (IOException e) 
		{
			return true; // if there is no robots.txt file, it is OK to search
		}
		if (strCommands.contains("Disallow")) // if there are no "disallow" values, then they are not blocking anything.
		{
			String[] split = strCommands.split("\n");
			ArrayList<RobotRule> robotRules = new ArrayList<>();
			String mostRecentUserAgent = null;
			if(!RobotRules.containsKey(strHost))
			{
				for (int i = 0; i < split.length; i++) 
				{
					String line = split[i].trim();
					if (line.toLowerCase().startsWith("user-agent")) 
					{
						int start = line.indexOf(":") + 1;
						int end   = line.length();
						mostRecentUserAgent = line.substring(start, end).trim();
					}
					else if (line.startsWith("Disallow")) {
						if (mostRecentUserAgent != null) {
							RobotRule r = new RobotRule();
							r.userAgent = mostRecentUserAgent;
							int start = line.indexOf(":") + 1;
							int end   = line.length();
							r.rule = line.substring(start, end).trim();
							robotRules.add(r);
						}
					}
				}

				RobotRules.put(strHost, robotRules);
			}

			for (RobotRule robotRule : RobotRules.get(strHost)	)
			{
				String path = url.getPath();
				if (robotRule.rule.length() == 0) return true; // allows everything if BLANK
				if (robotRule.rule == "/") return false;       // allows nothing if /

				if (robotRule.rule.length() <= path.length())
				{ 
					String pathCompare = path.substring(0, robotRule.rule.length());
					if (pathCompare.equals(robotRule.rule)) return false;
				}
			}
		}
		return true;
	}

}
