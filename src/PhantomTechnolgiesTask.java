import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhantomTechnolgiesTask {
    private static HttpURLConnection mainPageConnection;
    private static HttpURLConnection addToCartConnection;

    private static HttpURLConnection productIdConnection;

    public static void main(String[] args) throws IOException {
        BufferedReader mainPageReader;
        BufferedReader productIdReader;


        String csrfRegex = "(?<=\"csrf_token\" value=\")[^\"]*";
        String productIdRegex = "(?<=data-variant-id=\")[0-9]*";
        Pattern csrfPattern = Pattern.compile(csrfRegex);
        Pattern productIdPattern= Pattern.compile(productIdRegex);
        String csrfToken="";
        String bufferLineReader;
        String productID = "";
        StringBuffer mainPageResponseContent=new StringBuffer();
        StringBuffer productIdResponseContent= new StringBuffer();
        try {
            CookieHandler.setDefault(new CookieManager());
            URL mainPageUrl= new URL("https://www.shopdisney.co.uk/disney-store-disney-princess-costume-collection-for-kids-2841047080168M.html");
            mainPageConnection = (HttpURLConnection) mainPageUrl.openConnection();
            mainPageConnection.setRequestMethod("GET");
            mainPageConnection.setConnectTimeout(5000);
            mainPageConnection.setReadTimeout(5000);

            int status =mainPageConnection.getResponseCode();

            if(status >299)
            {
                mainPageReader=new BufferedReader(new InputStreamReader(mainPageConnection.getErrorStream()));
                while ((bufferLineReader= mainPageReader.readLine())!=null)
                {
                    mainPageResponseContent.append(bufferLineReader);
                }
                mainPageReader.close();

            }
            else{
                mainPageReader =new BufferedReader(new InputStreamReader(mainPageConnection.getInputStream()));
                while ((bufferLineReader= mainPageReader.readLine())!=null)
                {
                    mainPageResponseContent.append(bufferLineReader);
                }
                mainPageReader.close();
            }
            csrfToken = mainPageResponseContent.toString();
            Matcher csrfMatcher = csrfPattern.matcher(csrfToken);
            if (csrfMatcher.find()) {
                csrfToken=csrfMatcher.group(0);
                System.out.println("csrf Token : "+csrfToken);
            }
            URL getProductIdUrl= new URL("https://www.shopdisney.co.uk/on/demandware.store/Sites-disneyuk-Site/en_GB/Product-Variation?format=ajax&csrf_token="+csrfToken+"&cartAction=add&pid=2841047080168M&dwvar_2841047080168M_size=9-10%20YEARS&_=1624478443919");
            productIdConnection = (HttpURLConnection) getProductIdUrl.openConnection();
            productIdConnection.setRequestMethod("GET");
            productIdConnection.setConnectTimeout(5000);
            productIdConnection.setReadTimeout(5000);
            int status2 =productIdConnection.getResponseCode();

            if(status2 >299)
            {
                productIdReader=new BufferedReader(new InputStreamReader(productIdConnection.getErrorStream()));
                while ((bufferLineReader= productIdReader.readLine())!=null)
                {
                    productIdResponseContent.append(bufferLineReader);
                }
                productIdReader.close();

            }
            else{
                productIdReader =new BufferedReader(new InputStreamReader(productIdConnection.getInputStream()));
                while ((bufferLineReader= productIdReader.readLine())!=null)
                {
                    productIdResponseContent.append(bufferLineReader);
                }
                productIdReader.close();
            }
            productID=productIdResponseContent.toString();
            Matcher productIdMatcher = productIdPattern.matcher(productID);
            if (productIdMatcher.find()) {

                productID=productIdMatcher.group(0);
                System.out.println("Product ID :"+productID);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        URL addToCartUrl = new URL("https://www.shopdisney.co.uk/on/demandware.store/Sites-disneyuk-Site/en_GB/Cart-AddProduct");
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("Quantity", 1);
        params.put("pid", productID);
        params.put("csrf_token", csrfToken);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        addToCartConnection = (HttpURLConnection)addToCartUrl.openConnection();
        addToCartConnection.setRequestMethod("POST");
        addToCartConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        addToCartConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        addToCartConnection.setDoOutput(true);
        addToCartConnection.getOutputStream().write(postDataBytes);
        int statusCode=addToCartConnection.getResponseCode();
        System.out.println("Status Code :"+statusCode + " Response: Added to Cart");
    }
}
