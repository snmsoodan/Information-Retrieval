package IR_index;

import java.util.ArrayList;
import java.util.HashMap;
 
public class ProximitySearch {
 
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        HashMap<String,Tuple> Rlist = new HashMap<String,Tuple>();
        ArrayList<Integer> abc = new ArrayList();
        
        abc.add(6);
        abc.add(7);
        abc.add(9);
       
         
        Rlist.put("abc",new Tuple(abc, 0));
         
        ArrayList<Integer> bcd = new ArrayList();
        bcd.add(1);
        bcd.add(3);
        bcd.add(2);
       
         
        Rlist.put("bcd",new Tuple(bcd, 0));
        Boolean i = checkEnd(Rlist);
        //System.out.println(i);
        int mins= minspan(Rlist);
//        System.out.println(mins);
         
         
         
 
    }
    public static int minspan(HashMap<String,Tuple> amap)
    {
        for(String key: amap.keySet())
        {
            amap.put(key,new Tuple(amap.get(key).positionList,0));            
        }
       
         
        ArrayList<Integer> minspanlist= new ArrayList<Integer>();
        HashMap<String,Integer> Marked = new HashMap<String,Integer>();
        int markedmax = 0;
        int markedmin = Integer.MAX_VALUE;
        while(checkEnd(amap))
        {
        int minspan=0;
        HashMap<String,Integer> Window = CreateWindow(amap);
         int smallest = Integer.MAX_VALUE;
         int secondSmallest = Integer.MAX_VALUE;
         String ref="";
         String secondref="";
         int max=0;
         
        for(String i: Window.keySet()) {	
        	if(!Marked.containsKey(i))
        	{
            
        		 if(Window.get(i) > max ) 
        		 {
        			 if(markedmax>Window.get(i))
        		     max= markedmax;
        			 else
                     max = Window.get(i);
        		 }
        		 
        	 if (Window.get(i) < smallest) {
                secondSmallest = smallest;
                if(markedmin<Window.get(i))
                {
                smallest = markedmin;
                }
                else
                {
                smallest = Window.get(i);
                }
                secondref=ref;
                ref=i;
                
        	}
            
                else if (Window.get(i) < secondSmallest) {
                	secondSmallest = Window.get(i);
                    secondref=i;
           
                }
 
           
        	}
            
        }
        if((amap.get(ref).termFrequency<amap.get(ref).positionList.size()))
        {   
        amap.put(ref, new Tuple(amap.get(ref).positionList,(amap.get(ref).termFrequency+1)));
        }
        else
        {
        	
            if(secondref.equals(""))
                    continue;
            else
                amap.put(secondref, new Tuple(amap.get(secondref).positionList,(amap.get(secondref).termFrequency+1)));
        }
        
        for(String key:amap.keySet())
        {
        	if(amap.get(key).termFrequency>=amap.get(key).positionList.size())
        	{
        		Marked.put(key,amap.get(key).positionList.get(amap.get(key).positionList.size()-1)); 
        	}
        }
        	
        markedmin = minHash(Marked);
        markedmax= maxHash(Marked);
        minspan= max-smallest;
        System.out.println("min"+minspan);
        minspanlist.add(minspan);
        //int xsh=min(minspanlist);
     
        }
         
        return min(minspanlist);
         
    }
     
    public static HashMap<String,Integer> CreateWindow(HashMap<String,Tuple> amap)
    {
        HashMap<String,Integer> Window= new HashMap<String,Integer>();
        for(String key:amap.keySet())
        {
            if(amap.get(key).termFrequency>=amap.get(key).positionList.size())
            Window.put(key, amap.get(key).positionList.get(amap.get(key).positionList.size() - 1));
            else
            Window.put(key, amap.get(key).positionList.get(amap.get(key).termFrequency));
             
        }
        return Window;
    }
    public static Boolean checkEnd(HashMap<String,Tuple> amap)
    {
        int count=amap.size();
        for(String key:amap.keySet())
        {
        if(amap.get(key).termFrequency >=amap.get(key).positionList.size())
        {
            count--;    
        }
        }
        if(count==0)
        {
        return false;
        }
        else
        {
            return true;
        }
         
    }
     
    public static int min(ArrayList<Integer> list)
    {
        int min=Integer.MAX_VALUE;
        for(int i: list)
        {
         
           if(i<min)
        	   min=i;
        }
        
        return min;
         
    }
    
    public static int minHash(HashMap<String,Integer> list)
    {
        int min=Integer.MAX_VALUE;
        for(String i: list.keySet())
        {
         
           if(list.get(i)<min)
        	   min=list.get(i);
        }
        
        return min;
         
    }
    public static int maxHash(HashMap<String,Integer> list)
    {
        int max=0;
        for(String i: list.keySet())
        {
         
           if(list.get(i)<max)
        	   max=list.get(i);
        }
        
        return max;
         
    }
 
}
