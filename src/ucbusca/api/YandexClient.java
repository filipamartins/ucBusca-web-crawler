package ucbusca.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;



public class YandexClient {
    public static final String API_KEY = ""; // Insert Yandex API key here

    
    private static String getTextResponse(HttpURLConnection connection) {
	    StringBuilder responseText = new StringBuilder();
		try {
			InputStream inputStream = connection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				responseText.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseText.toString();
    }
	
    public static String translate(String text, String lang, String toLang) {
    	String translation ="";
    	try {
	    	String keyq = "key=" + API_KEY; 							//? key=<API key>
			String textq = "text=" + URLEncoder.encode(text, "UTF-8"); 	//& text=<text to translate> (source text must be URL-encoded) 
			String langq = "lang=" + lang +"-"+ toLang; 				//& lang=<translation direction> (en-pt)
			String urlString = "https://translate.yandex.net/api/v1.5/tr.json/translate?"+keyq+"&"+textq+"&"+langq;
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json; charset=utf-8");
			
	        int statusCode = connection.getResponseCode();
	        String responseText;
	        if (statusCode >= 200 && statusCode < 400) {
	        	responseText = getTextResponse(connection);
	        	System.out.println(responseText);
	        	JSONObject responseJSON = new JSONObject(responseText);
	        	JSONArray arr = (JSONArray) responseJSON.get("text");
	            translation = arr.get(0).toString();
	        }
	        else {
	        	responseText = getTextResponse(connection);
	        	System.out.println(responseText);
	        	translation ="";
	        }     
    	}catch(Exception e){
			System.out.println("YANDEX ERROR");
			e.printStackTrace();
		}	
    	return translation;
    }
    
   
    
    public static String getLanguage(String text) {
    	String lang ="";
		try {
			String keyq = "key=" + API_KEY;
			String textq = "text=" + URLEncoder.encode(text, "UTF-8");
			String urlString = "https://translate.yandex.net/api/v1.5/tr.json/detect?"+keyq+"&"+textq;
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
	        int statusCode = connection.getResponseCode();
	        String responseText;
	        if (statusCode >= 200 && statusCode < 400) {
	        	responseText = getTextResponse(connection);
	        	JSONObject responseJSON = new JSONObject(responseText);
	        	lang = (String) responseJSON.get("lang");
	        }
	        else {
	        	responseText = getTextResponse(connection);
	        	System.out.println(responseText);
	        	lang ="";
	        }     
		} catch(Exception e){
			System.out.println("YANDEX ERROR");
			e.printStackTrace();
		}
		return lang;
	}

    public static void main(String[] args) {
    	String text3 = getLanguage("Olá Mundo\r\n");
    	String text4 =  translate("Hello World", "en", "pt");
    	System.out.println(text4);
      	System.out.println(text3);
    }
}	
