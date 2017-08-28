

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java_cup.runtime.Symbol;

public class cleaner {
	public static void main(String[] args) throws IOException
	{
		
		String fileNew = "C:/Users/Snm/Desktop/HW7/doc_train_mapping.txt";
		File my_file = new File(fileNew); 
		PrintWriter printwriter = new PrintWriter(my_file);
		
		Settings settings = Settings.builder()
				.put("client.transport.sniff", true)
				.put("client.transport.ping_timeout","3600s")
				.put("cluster.name", "elastic4").build();
	
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        BulkRequestBuilder breq=client.prepareBulk();
        
        List<new_type> docs = new ArrayList<new_type>(); 
        
        Scanner label = new Scanner(new File("C:/Users/Snm/Downloads/trec07p/full/index"));
        List<Boolean> label_list = new ArrayList<Boolean>();
        
        AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
        
        
        //HashSet<String> text = new HashSet<String>();
        
        while(label.hasNextLine())
        {
        	String[] line = label.nextLine().split(" ");
        	if(line[0].equals("ham"))
        	label_list.add(true);
        	if(line[0].equals("spam"))
            	label_list.add(false);
        }
        
        
        HashSet<String> dict=new HashSet<String>();
		File f=new File("C:/Users/Snm/Downloads/enable1.txt");
		Scanner in=new Scanner(f);
		while(in.hasNextLine())
		{
			String[] line=in.nextLine().split(" ");
			String word=line[0];
			dict.add(word);
		}
        
        

		for(int i=1;i<75420;i++)
		{
			String text="";
		File input = new File("C:/Users/Snm/Downloads/trec07p/data/inmail."+i);
		try{
		
			System.out.println(i);
			

			
			Scanner in2=new Scanner(input);
			while(in2.hasNextLine())
			{
				String line=in2.nextLine();
				StandardTokenizer tokenizer = new StandardTokenizer(factory);
				tokenizer.setReader(new StringReader(line));
				tokenizer.reset();
				CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);
				while(tokenizer.incrementToken()) {
				    text = text+attr.toString()+" ";
				}
			}
			
//			while(in2.hasNextLine())
//			{
//				String[] line=in2.nextLine().split("\\s+|\\-|\\=|\\.|\\,|\\'|\\/|\\@|\\:|\\?|\\\"|\\(|\\)|\\<|\\>|\\[|\\]|\\_|\\;|\\$");
//				for(int i1=0;i1<line.length;i1++)
//				{
//					if(dict.contains(line[i1].toLowerCase()))
//					{
//						text=text+" "+line[i1].toLowerCase();
//					}	
//				}
//			}
//			
//			while(in2.hasNextLine())
//			{
//				text+=in2.nextLine();
//				
//			}
			
		String current_id = "inmail."+i;
		
		new_type temp = new new_type();
		temp.id = current_id;
		temp.text = text;
		temp.ham = label_list.get(i-1);
		
		docs.add(temp);
		
		//printwriter.println("<id>"+current_id+"</id>"+"<text>"+plaintext+"</text>"+"<label>"+label_list.get(i-1)+"</label>");
		
	}
		catch (Exception e) {
			System.out.println("Exception occured for "+i);
			continue;
		}
		
	}
//printwriter.flush();
		
		List<new_type> training = new ArrayList<new_type>();
		List<new_type> testing = new ArrayList<new_type>();
		//List<new_type> final_docs = new ArrayList<new_type>();
		
		Collections.shuffle(docs);
		
		
		//training = docs.subList(0, 80);
		//testing = docs.subList(80, 100);
		training = docs.subList(0, 60335);
		testing = docs.subList(60335, 75419);
		int c=0;
		int k=0;
		int t=0;
		for(new_type n :docs)
		{ c++;
			new_type temp = new new_type();
			
			temp.id=n.id;
			temp.text=n.text;
			temp.ham=n.ham;
			
			
			
			if(training.contains(n))
			{
				System.out.println("i am here");
			temp.training=true;	
			}
			
			else if(testing.contains(n))
			{
				System.out.println("i am here");
				temp.training=false;
			}
			
			else System.out.println("errorrrr");//final_docs.add(temp);
			
			
			//k++;
			breq.add(client.prepareIndex("final_cleaner1", "document",temp.id)
			.setSource(jsonBuilder()
					.startObject()
					.field("text",temp.text)
					.field("label",temp.ham)
					.field("split",temp.training)
					.endObject()
					)
			);
	
	
		
		printwriter.println(temp.id+" "+temp.ham+" "+temp.training);
			
		
		
		
		}
		
		
		BulkResponse bulkResponse = breq.execute().actionGet();
		
		if(bulkResponse.hasFailures())
			System.out.println(bulkResponse.buildFailureMessage());
		else
			System.out.println("successfully added: "+c);
		
	printwriter.flush();	
}
}
