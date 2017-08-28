package mallet;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.queryparser.ext.Extensions.Pair;

public class finalPart {
	
	public static void main(String[] args) throws Exception
	{
		File clus = new File("C:\\Users\\Snm\\Desktop\\trec\\qrels.txt");
		Scanner s1=new Scanner(clus);
		
//		ArrayList<String> documentName = new ArrayList<String>();
//		ArrayList<ArrayList<String>> querry = new ArrayList<ArrayList<String>>();
		ArrayList<String> qr = new ArrayList<String>();
		
		List<CustomObject> myList = new ArrayList<CustomObject>();
		
		HashSet documentName = new HashSet();   
		while(s1.hasNextLine())
		{
			
			String line1=s1.nextLine();
			String delims[]=line1.split("\\s+");
			
			if(     (delims[0].equals("54")|delims[0].equals("56")|delims[0].equals("57")|delims[0].equals("58")|delims[0].equals("59")|
					delims[0].equals("60")|delims[0].equals("61")|delims[0].equals("62")|delims[0].equals("63")|delims[0].equals("64")|
					delims[0].equals("68")|delims[0].equals("71")|delims[0].equals("77")|delims[0].equals("80")|delims[0].equals("85")|
					delims[0].equals("87")|delims[0].equals("89")|delims[0].equals("91")|delims[0].equals("93")|delims[0].equals("94")|
					delims[0].equals("95")|delims[0].equals("97")|delims[0].equals("98")|delims[0].equals("99")|delims[0].equals("100")
					)&&(delims[3].equals("1")))

			{
				documentName.add(delims[2]);

//				if(!documentName.contains(delims[2]))
//				{
//					documentName.add(delims[2]);
//					qr.add(delims[0]);
//					querry.add(qr);
//
//				}
//				else
//				{
//					int index=documentName.indexOf(delims[2]);
//					querry.get(index).add(delims[0]);
//				}
				
				CustomObject o1 = new CustomObject(delims[2], delims[0]);
				myList.add(o1);

				

			}
				
		}
		
		System.out.println("document  size "+myList.size());
		System.out.println("document name "+documentName.size());
	
		
		//////////////////////////////////////////// add document number and queries 
		
		
		File clus2 = new File("C:\\Users\\Snm\\Desktop\\HW8\\partB\\clusterOutput.txt");
		Scanner s2=new Scanner(clus2);
		
//		List<CustomObject> myList2 = new ArrayList<CustomObject>();
		
//		Map<String, String> cluster = new HashMap<String, String>();
		
		while(s2.hasNextLine())
		{
			String line1=s2.nextLine();
			String delims[]=line1.split("\\s+");
			if(documentName.contains(delims[0]))
			{
//			cluster.put(delims[0], delims[1]);
//				CustomObject o1 = new CustomObject(delims[0], delims[1]);
				for(int i=0;i<myList.size();i++)
				{
					if(myList.get(i).value1.equals(delims[0]))
						myList.get(i).value3=delims[1];
				}
				
			}
			
		}
		
		System.out.println("doc size "+myList.size());
		
		int sqsc=0,sqdc=0,dqsc=0,dqdc=0;
		
		for(int i=0;i<myList.size();i++)
		{
			String qname=myList.get(i).value2;
			String cname=myList.get(i).value3;
			for(int k=0;k<myList.size();k++)
			{
				String qname1=myList.get(k).value2;
				String cname1=myList.get(k).value3;
				
				if((qname.equals(qname1))&&(cname.equals(cname1)))
					{
						sqsc++;
					}
					if((qname.equals(qname1))&&(!cname.equals(cname1)))
					{
						sqdc++;
					}
					if((!qname.equals(qname1))&&(!cname.equals(cname1)))
					{
						dqdc++;
					}

					if((!qname.equals(qname1))&&(cname.equals(cname1)))
					{
						dqsc++;
					}
				
			}
		}
		
		
		
		
//		ArrayList<String> clname = new ArrayList<String>();
//		
//		for(int i=0;i<documentName.size();i++)
//		{
//			String dname=documentName.get(i);
//			String cname=cluster.get(dname);
//			clname.add(cname);
//
//
//		}
		
		
		
//		for(int i=0;i<documentName.size();i++)
//		{
//			System.out.println(i);
//			String dname=documentName.get(i);
//			String cname=cluster.get(dname);
//			for(int k=0;k<querry.get(i).size();k++)
//			{
//				String qname=querry.get(i).get(k);
//
//
//				for(int i1=0;i1<documentName.size();i1++)
//				{
//
//					String dname1=documentName.get(i1);
//					String cname1=cluster.get(dname1);
//
//					for(int k1=0;k1<querry.get(i1).size();k1++)
//					{
//
//						String qname1=querry.get(i1).get(k1);
//
//						if((qname.equals(qname1))&&(cname.equals(cname1)))
//						{
//							sqsc++;
//						}
//						if((qname.equals(qname1))&&(!cname.equals(cname1)))
//						{
//							sqdc++;
//						}
//						if((!qname.equals(qname1))&&(!cname.equals(cname1)))
//						{
//							dqdc++;
//						}
//
//						if((!qname.equals(qname1))&&(cname.equals(cname1)))
//						{
//							dqsc++;
//						}
//
//
//					}
//
//				}
//
//
//			}
//
//		}// end of for loop

		System.out.println("same query same cluster : "+sqsc);
		System.out.println("same query different cluster : "+sqdc);
		System.out.println("different query different cluster : "+dqdc);
		System.out.println("different query same cluster : "+dqsc);
		
		PrintWriter writer = new PrintWriter("C:\\Users\\Snm\\Desktop\\HW8\\partB\\confusionTable.txt");
		writer.println("same query same cluster : "+sqsc);
		writer.println("same query different cluster : "+sqdc);
		writer.println("different query different cluster : "+dqdc);
		writer.println("different query same cluster : "+dqsc);
		writer.close();
		
		
	}

}
