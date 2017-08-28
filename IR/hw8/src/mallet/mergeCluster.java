package mallet;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class mergeCluster {

	public static void main(String[] args) throws Exception
	{
		File clus = new File("C:\\Users\\Snm\\Desktop\\HW8\\output.txt");
		Scanner s1=new Scanner(clus);
		
		ArrayList<String> clusNumber = new ArrayList<String>();
		
		while(s1.hasNextLine())
		{
			String line1=s1.nextLine();
			String delims[]=line1.split("\\s+");
			clusNumber.add(delims[1]);
		}
		
		//////////////////////////////////////////// cluster number 
		
		
		File fileName = new File("C:\\Users\\Snm\\Downloads\\mallet-2.0.8\\index.txt");
		Scanner s2=new Scanner(fileName);
		
		ArrayList<String> fileNm = new ArrayList<String>();
		
		while(s2.hasNextLine())
		{
			String line1=s2.nextLine();
			String delims[]=line1.split("\\s+");
			fileNm.add(delims[1]);
		}
		
		PrintWriter writer = new PrintWriter("C:\\Users\\Snm\\Desktop\\HW8\\partB\\clusterOutput.txt");
		
		for(int i=0;i<fileNm.size();i++)
		{
			writer.println(fileNm.get(i)+" "+clusNumber.get(i));
		}
		
		writer.close();
	}
	
	
}
