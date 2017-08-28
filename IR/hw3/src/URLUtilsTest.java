import  junit.framework.TestCase;
import  org.junit.Assert;

public class URLUtilsTest extends TestCase {

    public static void testCanonicalization() {
        // Remove default port
        String url1 = "http://example.com:80";
        String expectedUrl1 = "http://example.com";

        // Decoding octets for unreserved characters
        String url2 = "http://example.com/%7Ehome";
        String expectedUrl2 = "http://example.com/~home/";

        // Remove . and ..
        String url3 = "http://example.com/a/./b/../c";
        String expectedUrl3 = "http://example.com/a/b/c/";

        // Force trailing slash for directories
        String url4 = "http://example.com/a/b";
        String expectedUrl4 = "http://example.com/a/b/";

        // Remove default index pages
        String url5 = "http://example.com/index.html";
        String expectedUrl5 = "http://example.com";

        // Removing the fragment
        String url6 = "http://example.com/a#b/c";
        String expectedUrl6 = "http://example.com/a/";


        Assert.assertEquals("should remove default port", expectedUrl1, URLUtils.getCanonicalURL(url1));
        Assert.assertEquals("should decode octets for unreserved characters", expectedUrl2, URLUtils.getCanonicalURL(url2));
//        Assert.assertEquals("should remove . and ..", expectedUrl3, URLUtils.getCanonicalURL(url3));
        Assert.assertEquals("should force trailing slash for directories", expectedUrl4, URLUtils.getCanonicalURL(url4));
        Assert.assertEquals("should remove default index pages", expectedUrl5, URLUtils.getCanonicalURL(url5));
        Assert.assertEquals("should remove the fragment", expectedUrl6, URLUtils.getCanonicalURL(url6));
    }

}


