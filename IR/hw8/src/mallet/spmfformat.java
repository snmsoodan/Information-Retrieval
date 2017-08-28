package mallet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class spmfformat {
	public static void main(String[] args) throws Exception
	{
		
		PrintWriter writer = new PrintWriter("C:/Users/Snm/Downloads/mallet-2.0.8/prob.txt");
		PrintWriter writer2 = new PrintWriter("C:/Users/Snm/Downloads/mallet-2.0.8/index.txt");
		
		
		File fl=new File("C:\\Users\\Snm\\Desktop\\HW8docs");
		
		File[] filesList=fl.listFiles();
		int k=1;
		for(int i=0;i<filesList.length;i++)
		{
			File newFile= new File(filesList[i].getPath());
			System.out.println(filesList[i].getName());
			writer2.println(k+" "+filesList[i].getName());
			String text="";
			Scanner s1=new Scanner(newFile);
			while(s1.hasNextLine())
			{
				String line1=s1.nextLine();
				line1=line1.replaceAll("\\s+", " ");
				line1=line1.replaceAll("\\'", "");
				line1=line1.replaceAll("\\`", "");
				line1=line1.replaceAll("\\,", "");
				line1=line1.replaceAll("\\.", "");
				line1=line1.replaceAll("\\:", "");
//				if(line1.length()>0)
				line1=line1.substring(1);
				text=text+line1;
			}
			System.out.println(k);
			writer.println(k+"\t"+text);
		
			k++;
//			if(k==2)
//			break;
		}
		

		writer.flush();
		writer2.flush();
	
	}
		
	}


