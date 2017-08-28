import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class indexCreation {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File fl=new File("C:\\Users\\Snm\\Downloads\\AP_DATA\\ap89_collection");
//		File fl=new File("C:\\Users\\Snm\\Downloads\\AP_DATA\\test");

		File[] filesList=fl.listFiles();
		Map<String,String> hmap= new HashMap<String,String>();
		int x=0,y=0;
		
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","3600s")
				.put("cluster.name", "elasticsearch").build();
		
		
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
				
		BulkRequestBuilder brb=client.prepareBulk();
		
		for(int i=0;i<filesList.length;i++){
			File newFile= new File(filesList[i].getPath());
			String line = "",docno="",text="";
			FileInputStream fis = new FileInputStream(newFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
			boolean check=false,checkdoc=false;
			
			while( (line = br.readLine()) != null){
				int lineLength=line.length();
				if(line.startsWith("<DOC>"))
				{
					checkdoc=true;
					text="";
				    docno="";
				}
				if(line.endsWith("</DOC>"))
				{
					hmap.put(docno,text);
					checkdoc=false;
				}
				if(line.startsWith("<DOCNO>"))
				{
					x+=1;
					docno=(line.substring(8,(lineLength - 9)));
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
						text=text.concat(" "+line);
					}
				}
			}
			
			br.close();
			fis.close();
		}
		
		for(Map.Entry m:hmap.entrySet())
		{
			y++;
			brb.add(client.prepareIndex("ap_dataset", "document",(String) m.getKey()).setSource("text",m.getValue()));
//			IndexResponse response =client.prepareIndex("ap_dataset", "document",(String) m.getKey()).setSource("text",m.getValue()).get();
			
		}
		
		System.out.println(y+" "+x);
		BulkResponse bres=brb.get();
		
	}

}
