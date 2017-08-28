import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;


public class test {
	public static void main(String[] args) throws IOException
	{
		File input = new File("C:/Users/Snm/Desktop/inmail2.txt");
		
		HashSet<String> dict=new HashSet<String>();
		File f=new File("C:/Users/Snm/Downloads/enable1.txt");
		Scanner in=new Scanner(f);
		while(in.hasNextLine())
		{
			String[] line=in.nextLine().split(" ");
			String word=line[0];
			dict.add(word);
		}
		
		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
		
	String text="";
		Scanner in2=new Scanner(input);
		while(in2.hasNextLine())
		{
			String line=in2.nextLine();
//			String[] line=in2.nextLine().split("\\s+|\\-|\\=|\\.|\\,|\\'|\\/|\\@|\\:|\\?|\\\"|\\(|\\)|\\<|\\>|\\[|\\]|\\_|\\;");
//			String[] line=in2.nextLine().split("\\s|\\s+|\\\"|\\/|\\,|\\.");
//			System.out.println(line.length);
//			for(int i=0;i<line.length;i++)
//			{
//				System.out.println(line[i]);
//				if(dict.contains(line[i].toLowerCase()))
//				{
//					text=text+" "+line[i];
//				}
				
				
				
//			}
			StandardTokenizer tokenizer = new StandardTokenizer(factory);
			tokenizer.setReader(new StringReader(line));
			tokenizer.reset();
			CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);
			while(tokenizer.incrementToken()) {
			    // Grab the term
			    text = text+attr.toString()+" ";
//			    System.out.println(term);
			    // Do something crazy...
			}
			
		}
		
		System.out.println(text);
		
		
	}

}
