import sun.net.URLCanonicalizer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by Abhishek Mulay on 6/16/17.
 */

public class URLUtils {

    private static final URLCanonicalizer canonicalizer = new URLCanonicalizer();
    public static String getCanonicalURL(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Can not canonicalize empty or null url.");
        }

        final String[] defaultIndexPages = {"/index.html", "/index.htm"};

        String validUrl = canonicalizer.canonicalize(url);
        URL urlObj = null;
        URI uriObj = null;
        URL finalUrl = null;
        try {
            validUrl = java.net.URLDecoder.decode(validUrl, "UTF-8");
            urlObj = new URL(validUrl);
            uriObj = new URI(urlObj.toString());

            String path = uriObj.getPath();
            int port = uriObj.getPort();
            // remove port number
            validUrl = validUrl.replace(":" + port, "");

            // remove default index pages
            if (!path.isEmpty()) {
                for (String fileName : defaultIndexPages) {
                    if (path.contains(fileName)) {
                        path = path.replace(fileName, "");
                    }
                }
            }

            // remove .
//            path = path.replaceAll("\\.", "");
            // remove double forward slashes
            path = path.replaceAll("\\/\\/", "/");

            // remove fragment after #
            if (!path.isEmpty() && path.contains("#")) {
                path = path.substring(0, path.indexOf("#"));
            }
            
            //remove fragment after ?
            //added by sanamdeep
            if(path.contains("?")) {
            	path = path.substring(0,path.indexOf("?"));
			}
          //added by sanamdeep
            //covers both the port
            URL myurl = new URL(url);
            int port1 = myurl.getPort();
            if((myurl.getProtocol().equals("http")  && port == 80) ||
					(myurl.getProtocol().equals("https") && port == 443))
			{
				port1 = -1;
			}
            
            //get protocol can be directly set to http we can discuss on this
          //added by sanamdeep
            path = myurl.getProtocol() + "://" +
					myurl.getHost().toLowerCase() +
					(myurl.getPort() > -1 ? ":" + myurl.getPort() : "")+myurl.getPath();

            // add trailing /
            if (!path.isEmpty() && path.charAt(path.length() - 1) != '/') {
                path += '/';
            }
            
           

            finalUrl = new URL(urlObj.getProtocol(), urlObj.getHost(), path);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalUrl.toString();
    }
}
