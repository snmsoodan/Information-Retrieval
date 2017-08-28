package mallet;

import java.io.File;
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

public class probability {

	public static boolean ASC = true;
    public static boolean DESC = false;
    
	public static void main(String[] args) throws Exception
	{
		PrintWriter writer = new PrintWriter("C:/Users/Snm/Downloads/mallet-2.0.8/probabilty.txt");
		File doc = new File("C:/Users/Snm/Downloads/mallet-2.0.8/tutorial_compostion.txt");
		Scanner s3=new Scanner(doc);
		
//		ArrayList<String> filename = new ArrayList<String>();
		String filename="";
		Map<String, Double> unsortMap = new HashMap<String, Double>();
		
		while(s3.hasNextLine())
		{
			String line1=s3.nextLine();
			String delims[]=line1.split("\\s+");
			
			String line2=delims[1];
			String n[]=line2.split("\\/");
			System.out.println(n[(n.length-1)]);
			filename=n[(n.length-1)];
			writer.print(filename);
			
			for(int i=0;i<20;i++)
			{
				unsortMap.put((i+""),Double.parseDouble(delims[(i+2)]));
			}
			
			// sorting in descending order
			Map<String, Double> sortedMapDesc = sortByComparator(unsortMap, DESC);
			
			for (Entry<String, Double> entry : sortedMapDesc.entrySet())
	        {           
	            String key=entry.getKey();
	            double val=entry.getValue();
	            writer.print(" "+key+":"+val);
	        }
			writer.print("\n");
//			break;
		}
		writer.close();
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
