package IR_index;

import java.util.ArrayList;

public class Tuple {
	ArrayList<Integer> positionList;
	int termFrequency;
	
	public Tuple(ArrayList<Integer> list, int termCount)
	{
		positionList = list;
		termFrequency = termCount;
	}
}
