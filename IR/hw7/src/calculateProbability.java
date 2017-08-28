import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class calculateProbability {

	public static void main(String[] args) throws FileNotFoundException {
		
		File doc = new File("C:/Users/Snm/Desktop/HW7/doc_train_mapping.txt");
		Scanner s1=new Scanner(doc);
		
		ArrayList<String> docid1 = new ArrayList<String>();
		ArrayList<String> ham1 = new ArrayList<String>();
		
		while(s1.hasNextLine())
		{
			String line1=s1.nextLine();
			String delims[]=line1.split("\\s+");
			docid1.add(delims[0]);
			
			if(delims[1].equals("true"))
			{
				ham1.add("1");
			}
			else{
				ham1.add("0");
			}
			
		} //while loop ends
		
//		System.out.println(ham1.size()+" "+docid1.size());
//		System.out.println(ham1.get(docid1.indexOf("inmail.45847")));
		
		int c=0;
		
		File doc2 = new File("C:/Users/Snm/Desktop/HW7/2/spamProb.txt");
		Scanner s2=new Scanner(doc2);
		
		ArrayList<String> docid2 = new ArrayList<String>();
		ArrayList<String> ham2 = new ArrayList<String>();
		
		while(s2.hasNextLine())
		{
			if(c==50)
				break;
			String line1=s2.nextLine();
			String delims[]=line1.split("\\s+");
			docid2.add(delims[0]);
			c++;
			
		} //while loop ends
		
		int count=0;
		for(int i=0;i<docid2.size();i++)
		{
			if(ham1.get(docid1.indexOf(docid2.get(i))).equals("0"))
			{
				count++;
			}
		}
		
		System.out.println("percentage accuracy for first 50 docs is: "+(count*100)/50+"%");
		
		
		
	}

}
