package IR_index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class to1 {

	public static void main(String[] args) throws NumberFormatException, IOException{
		
		RandomAccessFile raf1 = new RandomAccessFile("C:/Users/Snm/workspace/HW2/merged/mergedlist_72.txt", "r");
		File mFile1 = new File("C:/Users/Snm/workspace/HW2/merged/mergedcatalog_72.txt");
		File mFile2 = new File("C:/Users/Snm/workspace/HW2/merged/in.0.50.txt");
		PrintWriter writer = new PrintWriter("docfrequency.txt", "UTF-8");
		FileInputStream fis1 = new FileInputStream(mFile1);
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(mFile1)));
		FileInputStream fis2 = new FileInputStream(mFile2);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(mFile2)));
		String line="";
		String line2="";
		Map<String, IntTriple> Catalog=new HashMap<String,IntTriple>();
		HashMap<String,IntTuple> wordmap2=new HashMap<String,IntTuple>();
		
		while((line = br1.readLine()) != null)
		{
		String[] queryparams= line.split("\\|");
		
		
		for(int count=1;(count+3)<=queryparams.length;)
		{
			//System.out.println(queryparams[count]+""+queryparams[count+1]+""+queryparams[count+2]+"");
			IntTriple t1= new IntTriple(Integer.parseInt(queryparams[count+1]),Integer.parseInt(queryparams[count+2]),0);
			Catalog.put(queryparams[count],t1);
			count=count+3;
		}
		}
		
		
		for(String key:Catalog.keySet())
		{
			int length=Catalog.get(key).endoffset-(Catalog.get(key).startoffset);
			 byte[] mybyte= new byte[length];
			 raf1.seek(Catalog.get(key).startoffset);
			 raf1.readFully(mybyte);
			 
			 String inventry= new String(mybyte,"UTF-8");
			 String[] params1= inventry.split("\\|");
			 HashMap<String,Tuple> wordmap1=new HashMap<String,Tuple>();
			 
			 
			 for(int count=1;(count+3)<=params1.length;count=count+3)
				{
				 String s1=params1[count+2];
				 String replace = s1.replace("[","");
				 String replace1 = replace.replace("]","");
				 List<String> arrayList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
				 ArrayList<Integer> posList = new ArrayList<Integer>();
				 for(String fav:arrayList){
					 posList.add(Integer.parseInt(fav.trim()));
				}
				 Tuple t1= new Tuple(posList,Integer.parseInt(params1[count+1]));
				 wordmap1.put(params1[count], t1);
				}
			 int docfrequency=0;
			 int totaltermfrequency=0;
			 for(String key1: wordmap1.keySet())
			 {
				totaltermfrequency=totaltermfrequency+wordmap1.get(key1).termFrequency;
				 
			 }
			 docfrequency=wordmap1.size();
			 wordmap2.put(key, new IntTuple(docfrequency,totaltermfrequency));

	}
		while((line2 = br2.readLine()) != null)
		{
		
			if(wordmap2.containsKey(line2))
			{
			writer.println(line2+" "+wordmap2.get(line2).startoffset+" "+wordmap2.get(line2).endoffset);
			}
		 
		}
		writer.close();

		}
}
