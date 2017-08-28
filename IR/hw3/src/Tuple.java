import java.util.ArrayList;
import java.util.HashSet;

public class Tuple {

	String url;
	String rawurl;
	String head;
	Integer inlinkcount;
	Boolean visited;
	HashSet<String> incominglinks = new HashSet<String>();
	HashSet<String> outgoinglinks = new HashSet<String>();
	Integer level;
	Integer relevance;
	String httpheader;
	
	public Tuple(String raw,String uri, Integer count, Boolean visit,HashSet<String> in,HashSet<String> out,String head1, Integer lev, Integer relev, String http)
	{
		url = uri;
		inlinkcount = count;
		visited = visit;
		incominglinks= in;
		outgoinglinks = out;
		
		head= head1;
		rawurl= raw;
		relevance = relev;
		level= lev;
		httpheader = http;
	}
}