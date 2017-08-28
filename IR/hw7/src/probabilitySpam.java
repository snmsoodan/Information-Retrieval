import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class probabilitySpam {
	
	 public static boolean ASC = true;
	    public static boolean DESC = false;

	public static void main(String[] args) throws FileNotFoundException {
		
		File doc = new File("C:/Users/Snm/Desktop/HW7/testDoc.txt");
		Scanner s1=new Scanner(doc);
		
		ArrayList<String> docid = new ArrayList<String>();
		
		while(s1.hasNextLine())
		{
			String line1=s1.nextLine();
			String delims[]=line1.split("\\s+");
			docid.add(delims[0]);
			
		} //while loop ends
		 
		File words = new File("C:/Users/Snm/Downloads/liblinear-2.11/windows/s.txt");
			Scanner s2=new Scanner(words);
			
			ArrayList<String> prob = new ArrayList<String>();
			ArrayList<String> ham = new ArrayList<String>();
			
			while(s2.hasNextLine())
			{
				String line1=s2.nextLine();
				String delims[]=line1.split("\\s+");
				ham.add(delims[0]);
				prob.add(delims[2]);
				
			} //while loop ends
			
			
			PrintWriter tempSpam = new PrintWriter("C:/Users/Snm/Desktop/HW7/tempSpam.txt");
			
			for(int i=0;i<prob.size();i++)
			{
				tempSpam.println(ham.get(i)+" "+docid.get(i)+" "+prob.get(i)+" ");
				
			}
			tempSpam.flush();
			//// make a temp spam file with label docid probability
			Map<String, Double> unsortMap = new HashMap<String, Double>();
			File a = new File("C:/Users/Snm/Desktop/HW7/tempSpam.txt");
			Scanner s3=new Scanner(a);
			
			while(s3.hasNextLine())
			{
				String line1=s3.nextLine();
				String delims[]=line1.split("\\s+");
				if(delims[0].equals("0"))
				{
					unsortMap.put(delims[1], Double.parseDouble(delims[2]));
				}
				
			} //while loop ends
			
			
			
			
			Map<String, Double> sortedMapDesc = sortByComparator(unsortMap, DESC);
			
			PrintWriter spamProb = new PrintWriter("C:/Users/Snm/Desktop/HW7/spamProb.txt");
			
			for (Entry<String, Double> entry : sortedMapDesc.entrySet())
	        {           
				String key=entry.getKey();
	            Double val=entry.getValue();
	           
	            spamProb.println(key+" "+val);
	        }
			spamProb.flush();
			

	}
	 private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
	    {

	        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry<String, Double>>()
	        {
	            public int compare(Entry<String, Double> o1,
	                    Entry<String, Double> o2)
	            {
	                if (order)
	                {
	                    return o1.getValue().compareTo(o2.getValue());
	                }
	                else
	                {
	                    return o2.getValue().compareTo(o1.getValue());

	                }
	            }
	        });

	        // Maintaining insertion order with the help of LinkedList
	        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
	        for (Entry<String, Double> entry : list)
	        {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }

	        return sortedMap;
	    }

}
