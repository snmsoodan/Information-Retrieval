
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Trec {
		public static void main(String[] args) throws IOException, InterruptedException
		{
			
//			File bm25 = new File("C:/Users/Snm/Desktop/HW5/model.txt");
//			File qrels = new File("C:/Users/Snm/Desktop/HW5/qrels.txt");
			
			File bm25 = new File("C:/Users/Snm/Desktop/TRECEVAL/ok1.txt");
			File qrels = new File("C:/Users/Snm/Desktop/TRECEVAL/qrels.txt");
			//File queryfile = new File("");
			
			BufferedReader br = new BufferedReader(new FileReader(bm25));
			BufferedReader br1 = new BufferedReader(new FileReader(bm25));
			HashSet<String> querymap= new LinkedHashSet<String>();
			
			String line="";
			
	        while ((line = br.readLine()) != null) {
	        	String line1=br1.readLine();
//	        	System.out.println(line1);
//	        	System.out.println(querymap);
				String delims[]=line1.split("\\s+");
				querymap.add(delims[0]);
			}
//			System.out.println("querymap "+querymap);
//			System.out.println("querymap size"+querymap.size());
			
			double avgpr=0.0;
			int count3=0;
			
			for(String q:querymap)
			{
				count3++;
				Scanner s1=new Scanner(bm25);
				Scanner s2=new Scanner(qrels);
				
				System.out.println("query "+q);
			HashSet<String> bmlist= new LinkedHashSet<String>();
			HashSet<String> qrelslist= new LinkedHashSet<String>(); 
			Map<String,Integer> qrelsmap = new HashMap<String,Integer>();
			
			while(s1.hasNextLine())
			{
				String line1=s1.nextLine();
				String delims[]=line1.split("\\s+");
				if(delims[0].equals(q)) {
				for(int i=2; i<delims.length; i=i+5)
				{
					bmlist.add(delims[i]);
				}
			}
			}
//			System.out.println("bmlist "+bmlist);
			//System.out.println("bmlist size "+bmlist.size());
			while(s2.hasNextLine())
			{
				String line2=s2.nextLine();
				String delims2[]=line2.split(" ");
				
				if(delims2[0].equals(q))
				{
				for(int i=2; i<delims2.length; i=i+3)
				{
					qrelslist.add(delims2[i]);
					//System.out.println(delims2[i]);
					//System.out.println(delims2[i+1]);
					qrelsmap.put(delims2[i], Integer.parseInt(delims2[i+1]));
				}
			}
			}
//			System.out.println("qrelsmap "+qrelslist);
			
			double sum_prec = 0.0;
			double size_rel = 0.0;
			
			for(String s:qrelsmap.keySet())
			{
				if(qrelsmap.get(s)==1)
					size_rel++;	
			}
			
			int i=0;
			int k=1;
			double ndcg=0.0;
			for(String r:bmlist) 
			{
				i++;
				//System.out.println("i here "+i);
				Map<String,Integer> confusion_i = getcutoff(i,qrelsmap,bmlist,qrelslist);
				
				Map<String,Integer> confusion_prev = getcutoff(i-1,qrelsmap,bmlist,qrelslist);
				
				int tp_i = cal_tp(confusion_i);
				
				
				int tp_prev = cal_tp(confusion_prev);
			
				double prec_i = (double) tp_i/i;
				
				
				double recall_i=(double) tp_i/size_rel;
				
				
			   double f_measure_i = cal_f_measure(prec_i,recall_i);
				
				
				if((tp_i-tp_prev)!=0)
				sum_prec+= prec_i;
				
				//System.out.println("sum "+sum_prec);
				
				if(i==5||i==10||i==15||i==20||i==30||i==100||i==200||i==500||i==1000){
					System.out.println("query "+q);
					System.out.println("precision@ "+i+" "+prec_i);
					System.out.println("recall@ "+i+" "+recall_i);
					System.out.println("fmeasure@ "+i+" "+f_measure_i);
					System.out.println("tp@ "+i+" "+tp_i);
				}
				
				if(i==size_rel)
				{
					System.out.println("r-precision "+i+" "+prec_i);
					//System.out.println("recall_for_r@ "+i+" "+recall_i);
				}
				
				if(qrelsmap.containsKey(r))
				{
					int rel = qrelsmap.get(r);
					if(i==1)
						ndcg+=(double)rel;
					else
					{
					
					ndcg+= (double) (Math.pow(2, rel)-1)/(Math.log(i));
					}
					
				}
				
		}
			List<Integer> ranklist = new ArrayList<Integer>();
			for(String key: qrelsmap.keySet())
			{
				ranklist.add(qrelsmap.get(key));
			}
			//}
			Collections.sort(ranklist);
			Collections.reverse(ranklist);
//			System.out.println("ranklist"+ranklist);
			double idealndcg=0;
			int k1=0;
			
			for(int j=0;j<ranklist.size();j++)
			{
				k1++;
				if(k1==1)
					idealndcg+=(double)ranklist.get(j);	
				else
				{
				
				idealndcg+= (double) ranklist.get(j)/(Math.log(k1));
				}
				
			}
			
			
			System.out.println("ndcg value "+ndcg/idealndcg);
		    System.out.println("sumndcg:"+ndcg);
		    System.out.println("idealndcg:"+idealndcg);
			double avg_prec = (double) sum_prec/size_rel;
			avgpr+=avg_prec;
			System.out.println("avg precision "+avg_prec);
			System.out.println("total_relavnce count "+size_rel);
			}	
			
			System.out.println("avgprecision "+avgpr/count3);
			
	}

		private static double cal_f_measure(double prec, double recall) {

			return (double) (2*prec*recall)/(prec+recall);
		}

		

		private static int cal_tp(Map<String, Integer> confusion) {
			int tp=0;
			for(String s:confusion.keySet())
			{
				if(confusion.get(s)==11)
					tp++;
			}
			return tp;
		}

		private static Map<String, Integer> getcutoff(int i, Map<String, Integer> qrelsmap, HashSet<String> bmlist, HashSet<String> qrelslist) {

			Integer tp=0;
			Integer fp=0;
			Integer tn=0;
			Integer fn=0;

			Map<String,Integer> confusionmap = new LinkedHashMap<String,Integer>();

			HashSet<String> cutoffbmlist = new LinkedHashSet<String>();

			int b = 0;

			for(String bm:bmlist)
			{
				b++;
				if(b>i)
					break;
				else cutoffbmlist.add(bm);
			}

			HashSet<String> intersection = new LinkedHashSet<String>(cutoffbmlist);
			intersection.retainAll(qrelslist);

			for(String in:intersection){
				if(qrelsmap.get(in)==1) 
				{
					tp++;
					confusionmap.put(in, 11);
				}

				if(qrelsmap.get(in)==0)
				{
					fp++;
					confusionmap.put(in, 1); 
				}
			}

			HashSet<String> remainingqrels = new HashSet<String>(qrelslist);
			remainingqrels.removeAll(cutoffbmlist);
			
			for(String rem:remainingqrels)
			{
				if(qrelsmap.get(rem)==0)
				{
					tn++;
					confusionmap.put(rem, 10); 
				}

				if(qrelsmap.get(rem)==1)
				{
					fn++;
					confusionmap.put(rem, 0); 
				}
			}


			return confusionmap;
		}
		    
	 
		
	}

