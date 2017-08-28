import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;



public class merge6 {
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		File qrel = new File("C:/Users/Snm/Desktop/TRECEVAL/qrels.txt");
		Scanner s1=new Scanner(qrel);
		
		ArrayList<String> qidDoc = new ArrayList<String>();
		ArrayList<String> grade = new ArrayList<String>();
		
		
		while(s1.hasNextLine())
		{
			String line1=s1.nextLine();
			String delims[]=line1.split("\\s+");
				qidDoc.add(delims[0]+"-"+delims[2]);
				grade.add(delims[3]);
		}
		///////////////////////////////////////////////////////////////////////////////////////////////
		
		File okapi = new File("C:/Users/Snm/Desktop/TRECEVAL/okapi.txt");
		Scanner s2=new Scanner(okapi);
		
		ArrayList<String> okqidDoc = new ArrayList<String>();
		ArrayList<Double> okgrade = new ArrayList<Double>();
		
		while(s2.hasNextLine())
		{
			String line1=s2.nextLine();
			String delims[]=line1.split("\\s+");
				okqidDoc.add(delims[0]+"-"+delims[2]);
				okgrade.add(Double.parseDouble(delims[4]));
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		
		File bm25 = new File("C:/Users/Snm/Desktop/TRECEVAL/bm25.txt");
		Scanner s3=new Scanner(bm25);
		
		ArrayList<String> bmqidDoc = new ArrayList<String>();
		ArrayList<Double> bmgrade = new ArrayList<Double>();
		
		while(s3.hasNextLine())
		{
			String line1=s3.nextLine();
			String delims[]=line1.split("\\s+");
				bmqidDoc.add(delims[0]+"-"+delims[2]);
				bmgrade.add(Double.parseDouble(delims[4]));
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		File tf = new File("C:/Users/Snm/Desktop/TRECEVAL/tfidf.txt");
		Scanner s4=new Scanner(tf);
		
		ArrayList<String> tfqidDoc = new ArrayList<String>();
		ArrayList<Double> tfgrade = new ArrayList<Double>();
		
		while(s4.hasNextLine())
		{
			String line1=s4.nextLine();
			String delims[]=line1.split("\\s+");
				tfqidDoc.add(delims[0]+"-"+delims[2]);
				tfgrade.add(Double.parseDouble(delims[4]));
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		
		File laplace = new File("C:/Users/Snm/Desktop/TRECEVAL/laplacelm.txt");
		Scanner s5=new Scanner(laplace);
		
		ArrayList<String> lpqidDoc = new ArrayList<String>();
		ArrayList<Double> lpgrade = new ArrayList<Double>();
		
		while(s5.hasNextLine())
		{
			String line1=s5.nextLine();
			String delims[]=line1.split("\\s+");
				lpqidDoc.add(delims[0]+"-"+delims[2]);
				lpgrade.add(Double.parseDouble(delims[4]));
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		
		File jm = new File("C:/Users/Snm/Desktop/TRECEVAL/JM.txt");
		Scanner s6=new Scanner(jm);
		
		ArrayList<String> jmqidDoc = new ArrayList<String>();
		ArrayList<Double> jmgrade = new ArrayList<Double>();
		
		while(s6.hasNextLine())
		{
			String line1=s6.nextLine();
			String delims[]=line1.split("\\s+");
				jmqidDoc.add(delims[0]+"-"+delims[2]);
				jmgrade.add(Double.parseDouble(delims[4]));
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		
		int cnt=0;
		String current="",prev="";
		for(int i=0;i<qidDoc.size();i++)
		{
			if(okqidDoc.contains(qidDoc.get(i))&&
					bmqidDoc.contains(qidDoc.get(i))&&
					tfqidDoc.contains(qidDoc.get(i))&&
					lpqidDoc.contains(qidDoc.get(i))&&
					jmqidDoc.contains(qidDoc.get(i)))
			
			{
				
//				System.out.println(cnt);
				
				current=qidDoc.get(i).substring(0,2);
//				prev=current;	
				
//				if((!current.equals(prev))&&cnt<999)
//				{
//				System.out.println("true");
//				System.out.println(current);
//				System.out.println(prev);
//				System.out.println(cnt);
//				}
			
				
				if(((!current.equals(prev))&&cnt<999))
				{
					
					for(int j=0;j<okqidDoc.size();j++)
					{
						
						if(		(!qidDoc.contains(okqidDoc.get(j)))&&
								(okqidDoc.get(j).substring(0,2).equals(prev))&&
								bmqidDoc.contains(okqidDoc.get(j))&&
								tfqidDoc.contains(okqidDoc.get(j))&&
								lpqidDoc.contains(okqidDoc.get(j))&&
								jmqidDoc.contains(okqidDoc.get(j)))
						{
							if(cnt>999)
							{
								cnt=0;
								prev=current;
								break;
							}
							
							cnt++;
							
//							int okindex1=okqidDoc.indexOf(qidDoc.get(i));

							int bmindex1=bmqidDoc.indexOf(okqidDoc.get(j));
							int tfindex1=tfqidDoc.indexOf(okqidDoc.get(j));
							int lpindex1=lpqidDoc.indexOf(okqidDoc.get(j));
							int jmindex1=jmqidDoc.indexOf(okqidDoc.get(j));
							
							
//							System.out.println("here");
							System.out.println(okqidDoc.get(j)
									+"\t"+okgrade.get(j)
									+"\t"+bmgrade.get(bmindex1)
									+"\t"+tfgrade.get(tfindex1)
									+"\t"+lpgrade.get(lpindex1)
									+"\t"+jmgrade.get(jmindex1)
									+"\t"+"0");
							
						}
//						else
//						{
//							cnt=0;
//							prev=current;
//							break;
//						}
						
					}
					prev=current;
					cnt=0;
					
					
				}
				
				prev=current;
				
				int okindex=okqidDoc.indexOf(qidDoc.get(i));

				int bmindex=bmqidDoc.indexOf(qidDoc.get(i));
				int tfindex=tfqidDoc.indexOf(qidDoc.get(i));
				int lpindex=lpqidDoc.indexOf(qidDoc.get(i));
				int jmindex=jmqidDoc.indexOf(qidDoc.get(i));
				
				
				
				System.out.println(qidDoc.get(i)
						+"\t"+okgrade.get(okindex)
						+"\t"+bmgrade.get(bmindex)
						+"\t"+tfgrade.get(tfindex)
						+"\t"+lpgrade.get(lpindex)
						+"\t"+jmgrade.get(jmindex)
						+"\t"+grade.get(i));
				
			cnt++;
			
			
			if(((current.equals("10"))&&cnt<999))
			{
				
				for(int j=0;j<okqidDoc.size();j++)
				{
					
					if(		(!qidDoc.contains(okqidDoc.get(j)))&&
							(okqidDoc.get(j).substring(0,2).equals(current))&&
							bmqidDoc.contains(okqidDoc.get(j))&&
							tfqidDoc.contains(okqidDoc.get(j))&&
							lpqidDoc.contains(okqidDoc.get(j))&&
							jmqidDoc.contains(okqidDoc.get(j)))
					{
						if(cnt>999)
						{
							cnt=0;
							prev=current;
							break;
						}
						
						cnt++;
						
//						int okindex1=okqidDoc.indexOf(qidDoc.get(i));

						int bmindex1=bmqidDoc.indexOf(okqidDoc.get(j));
						int tfindex1=tfqidDoc.indexOf(okqidDoc.get(j));
						int lpindex1=lpqidDoc.indexOf(okqidDoc.get(j));
						int jmindex1=jmqidDoc.indexOf(okqidDoc.get(j));
						
						
//						System.out.println("here");
						System.out.println(okqidDoc.get(j)
								+"\t"+okgrade.get(j)
								+"\t"+bmgrade.get(bmindex1)
								+"\t"+tfgrade.get(tfindex1)
								+"\t"+lpgrade.get(lpindex1)
								+"\t"+jmgrade.get(jmindex1)
								+"\t"+"0");
						
					}
//					else
//					{
//						cnt=0;
//						prev=current;
//						break;
//					}
					
				}
				prev=current;
				cnt=0;
				
				
			}
			///
			
			
			}
		}
		
	}

}
