import java.net.MalformedURLException;
import java.net.URL;

public class test {

	public static String canUrl(String url1) throws MalformedURLException {
		// TODO Auto-generated method stub
//		String url="HTTP://Example.com:80//a.html//b#anything";
		String url= url1;
		String path="";
		
		
		if (url == null || url.isEmpty()) {
            return "empty";
        }
		
		if(url.contains("#")) {
			url = url.substring(0,url.indexOf("#"));
		}

		if(url.contains("?")) {
			url = url.substring(0,url.indexOf("?"));
		}
		
		URL myurl = new URL(url);
		int port = myurl.getPort();
		
		
		path=myurl.getPath();
		if(path.contains("//"))
		{
			path = path.replaceAll("//", "/");
			
		}

		if((myurl.getProtocol().equals("http")  && port == 80) ||
				(myurl.getProtocol().equals("https") && port == 443))
		{
			port = -1;
		}


		
		
		String canUrl = myurl.getProtocol().toLowerCase() + "://" +
				myurl.getHost().toLowerCase() +
				(port > -1 ? ":" + myurl.getPort() : "")+ path;
		return canUrl;
		
//		System.out.println(canUrl);

	}

	

}
