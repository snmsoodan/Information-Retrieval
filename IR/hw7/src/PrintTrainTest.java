import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PrintTrainTest {
	
	public static void main(String[] args) throws IOException
	{
		File doc = new File("C:/Users/Snm/Desktop/HW7/doc_train_mapping.txt");
		Scanner s1=new Scanner(doc);
		
		ArrayList<String> trainId = new ArrayList<String>();
		ArrayList<String> testId = new ArrayList<String>();
		
		while(s1.hasNextLine())
		{
			String line1=s1.nextLine();
			String delims[]=line1.split("\\s+");
			
			if(delims[2].equals("true"))
			{
				trainId.add(delims[0]);
			}
			else
			{
				testId.add(delims[0]);
			}

		} //while loop ends
		
		
	
	}

}
