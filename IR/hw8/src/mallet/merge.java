package mallet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class merge {
	public static void main(String[] args) throws Exception
	{
		File bm25 = new File("C:/Users/Snm/Desktop/trec/bm25.txt");
		Scanner s3=new Scanner(bm25);
		
		ArrayList<String> bmqidDoc = new ArrayList<String>();
		
		while(s3.hasNextLine())
		{
			String line1=s3.nextLine();
			String delims[]=line1.split("\\s+");
				bmqidDoc.add(delims[0]+"|"+delims[2]);
		}
		
		
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		File qrel = new File("C:/Users/Snm/Desktop/trec/qrels.txt");
		Scanner s1=new Scanner(qrel);
		
		ArrayList<String> qidDoc = new ArrayList<String>();
		
		while(s1.hasNextLine())
		{
			String line1=s1.nextLine();
			String delims[]=line1.split("\\s+");
				if((delims[0].equals("100")|
						delims[0].equals("54")|
						delims[0].equals("56")|
						delims[0].equals("57")|
						delims[0].equals("58")|
						delims[0].equals("59")|
						delims[0].equals("60")|
						delims[0].equals("61")|
						delims[0].equals("62")|
						delims[0].equals("63")|
						delims[0].equals("64")|
						delims[0].equals("68")|
						delims[0].equals("71")|
						delims[0].equals("77")|
						delims[0].equals("80")|
						delims[0].equals("85")|
						delims[0].equals("87")|
						delims[0].equals("89")|
						delims[0].equals("91")|
						delims[0].equals("93")|
						delims[0].equals("94")|
						delims[0].equals("95")|
						delims[0].equals("97")|
						delims[0].equals("98")|
						delims[0].equals("99")
						)&&(delims[3].equals("1")))
				{
					qidDoc.add(delims[0]+"|"+delims[2]);
				}
				
		}
		
		
		HashSet<String> doc = new HashSet<String>();
		
		for(int i=0;i<bmqidDoc.size();i++)
		{
			doc.add(bmqidDoc.get(i));
		}
		
		
		for(int i=0;i<qidDoc.size();i++)
		{
			doc.add(qidDoc.get(i));
		}
		
		
		ArrayList<String> fdoc = new ArrayList<String>();
		
		for (String s : doc ) {
			fdoc.add(s);
		}
		
		
		
		File q = new File("C:/Users/Snm/Desktop/HW8/queries.txt");
		Scanner s21=new Scanner(q);
		ArrayList<String> q1 = new ArrayList<String>();
		while(s21.hasNextLine())
		{
			String line1=s21.nextLine();
			String delims[]=line1.split("\\s+");
			q1.add(delims[0]);
		}
		
		for(int k=0;k<q1.size();k++)
		{
			String qr=q1.get(k);
			System.out.println(qr);

			ArrayList<String> docno = new ArrayList<String>();
			for(int i=0;i<fdoc.size();i++)
			{
				String t=fdoc.get(i);
				String d[]=t.split("\\|");
				String querry=d[0];
				//			String docno=d[1];
				if(d[0].equals(qr))
				{
					docno.add(d[1]);
					//				TopicModel.topicModel(querry, docno);
				}
			}


			//		TopicModel.topicModel("54", docno);

			//		System.out.println(docno.size());

			for(int i=0;i<docno.size();i++)
			{
				PrintWriter writer = new PrintWriter("C:/Users/Snm/Desktop/HW8/docs/"+qr+"/"+docno.get(i));

				File doc1 = new File("C:/Users/Snm/Desktop/HW8docs/"+docno.get(i));
				Scanner s11=new Scanner(doc1);
				String text="";
				while(s11.hasNextLine())
				{
					String line1=s11.nextLine();
					text=text+line1;
				}
				writer.println(text);

				writer.close();
			}

		}//main querry for loop ends here
		
		
		
		
	}

}
