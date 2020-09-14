package UcBuscaMServer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import shared.Multicast;
import shared.Url;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Crawler extends Thread {

    private static final int MAX_DEPTH = 2;
    private long SLEEP_TIME = 5000;
    private HashMap<String, HashSet<String>> index = new HashMap<String, HashSet<String>>();
    private HashMap<String, HashSet<String>> connections = new HashMap<String, HashSet<String>>();
    private HashMap<String,String> titles = new HashMap<String,String>();
    private HashMap<String,String> citations = new HashMap<String,String>();
    private ArrayList<Url> urlsToVisit = new ArrayList<Url>();
    private HashSet<String> urlsVisited = new HashSet<String>();
    private Boolean lock = true;
    private String multicastAddress;
    private int port;
    private String serverName;
    private ArrayList<Url> listUrlsToShare = null;

    public Crawler(String multicastAddress, int port, String serverName){
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.serverName = serverName;
    } 

    public void run() {
        Multicast multicast = new Multicast(multicastAddress, port);
        long elapsedTime = 0;
        int i = 1;
        Boolean done = false;
        while (true) {
            long startTime = System.currentTimeMillis();
            i=1;
            while (!urlsToVisit.isEmpty()) {
                 try{
                    Thread.sleep(100);
                }catch(Exception e){
                    System.out.print(e);
                }
                elapsedTime = System.currentTimeMillis() - startTime;
                if(elapsedTime >= 7000*i){ 
                    int size = numberUrlsToVisit();                           
                    if(size >= 100){
                        listUrlsToShare = getUrlsToShare(size); //shares urlstoVisit with other multicast servers 5 seconds interval 
                        Message message = new ShareUrls("shareurls", serverName, Integer.toString(0), listUrlsToShare);
                        String msg = message.toString();
                        multicast.sendMessage(msg);
                    }                  
                    i++;
                } 
            
                done = true;
                System.out.println("Urls to visit:" + numberUrlsToVisit());
                crawlPage(nextUrl());
            }
            if (done) {
                done = false;
                System.out.println("No new urls to crawl.");
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                System.out.print(e);
            }
        }

    }

    synchronized void getSemaphore() {
        while (!lock)
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("interruptedException caught");
            }
        lock = false;
        notify();
    }

    synchronized void releaseSemaphore() {
        while (lock)
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("interruptedException caught");
            }
        lock = true;
        notify();
    }

    public int numberUrlsToVisit() {
        getSemaphore();
        int size = urlsToVisit.size();
        releaseSemaphore();
        return size;
    }

    private ArrayList<Url> getUrlsToShare(int size){
        ArrayList<Url> urlsToShare = new ArrayList<Url>();
        int middle = size/2;
        getSemaphore();
        for(int i = middle; i < size; i++){
            urlsToShare.add(urlsToVisit.get(i));
        }
        releaseSemaphore();
        return urlsToShare;
    }

    public void removeUrlsToVisit(){
        getSemaphore();
        urlsToVisit.removeAll(listUrlsToShare);
        releaseSemaphore();
    }

    public int addUrlToVisit(String ws, int depth) {
        Url url = new Url(ws, depth);
        urlsToVisit.add(url);
        return 0; // url indexed with success
    }

    public Boolean urlAlreadyVisited(String ws) {
        if (urlsVisited.contains(ws)) {
            return true; // already indexed
        } else
            return false;
    }

    private void crawlPage(Url url) {
        String ws = url.url;
        try {

            if (!ws.startsWith("http://") && !ws.startsWith("https://"))
                ws = "http://".concat(ws);

            // Attempt to connect and get the document
            Document doc = Jsoup.connect(ws).get(); // Documentation: https://jsoup.org/

            if (url.depth < MAX_DEPTH) {
                getLinks(doc, ws, url.depth);
            }

            indexUrl(doc, ws);
        } catch (IOException e) {
            System.out.println("Url could not be indexed.");
        }
    }

    private void getLinks(Document doc, String ws, int currentDepth) {
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            // Ignore bookmarks within the page
            if (link.attr("href").startsWith("#")) {
                continue;
            }
            // Shall we ignore local links? Otherwise we have to rebuild them for future
            // parsing
            if (!link.attr("href").startsWith("http")) {
                continue;
            }
            addToConnections(link.attr("href"), ws);
            Url url = new Url(link.attr("href"), currentDepth + 1);
            urlsToVisit.add(url);
        }
    }

    // create hashmap of list of pages linked to a specific page
    private void addToConnections(String link, String ws) {
        if (!this.connections.containsKey(link)) {
            HashSet<String> connects = new HashSet<String>();
            connects.add(ws);
            this.connections.put(link, connects);

        } else {
            connections.get(link).add(ws);
        }
        System.out.println("Added to connections " + link + " " + connections.get(link));
    }

    public HashSet<String> getConnections(String ws){
        String[] patterns = {"http://", "https://", "http://www.", "https://www."};
        if(connections.containsKey(ws)){
            return connections.get(ws);
        }
        else if(!ws.startsWith("http://") && !ws.startsWith("https://")){
            int size = patterns.length;
            for(int i = 0; i < size; i++){
                String url = patterns[i].concat(ws);
                if(connections.containsKey(url))
                    return connections.get(url);
            }
        }
        else{
            String ws1 = ws.substring(0, 7) +  "www." + ws.substring(7, ws.length());
            System.out.println("ws1 " + ws1);
            String ws2 = ws.substring(0, 8) +  "www." + ws.substring(8, ws.length());
            System.out.println("ws2 " + ws2);
            if(connections.containsKey(ws1))
                return connections.get(ws1);
            else
                return connections.get(ws2);
        }
        return null;
    }

    private void indexUrl(Document doc, String website) {
        // Get website text
        String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
        String line;

        // Get words
        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;
                String[] words = line.split("[ ,;:.?!\"(){}\\[\\]<>']+");
                for (String word : words) {
                    word = word.toLowerCase();
                    if ("".equals(word)) {
                        continue;
                    }
                    getSemaphore();
                    if (!this.index.containsKey(word)) {
                        HashSet<String> webList = new HashSet<String>();
                        webList.add(website);
                        this.index.put(word, webList);

                    } else {
                        index.get(word).add(website);
                    }
                    releaseSemaphore();
                }
                String citation = getCitation(doc);
                String title = getTitle(doc);
                getSemaphore();
                citations.put(website,citation);
                titles.put(website,title);
                releaseSemaphore();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close reader
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // for (String word : index.keySet()) {
        //     if (word.length() >= 3) { // Shall we ignore small words?
        //         // System.out.println(word + "\t" + index.get(word));
        //     }
        // }
    }

    private Url nextUrl() {
        Url nextUrl = null;
        do {
            nextUrl = this.urlsToVisit.remove(0);

        } while (this.urlsVisited.contains(nextUrl.url) && !this.urlsToVisit.isEmpty());
        this.urlsVisited.add(nextUrl.url);
        return nextUrl;
    }

    private String getTitle(Document doc) {
        String title  = "";
        try {
            title = doc.title();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }

    public String getUrlTitle(String url) {
        getSemaphore();
        String title = titles.get(url);
        releaseSemaphore();
        if(title==null){
            title = "";
        }
        return title;
    }

    private String getCitation(Document doc){
        try {
            Element body = doc.body();
            Elements paragraphs = body.getElementsByTag("p");
            String citation = "";
            for (Element paragraph : paragraphs) {
                citation += paragraph.text();
                if (citation.length() <= 50) {
                    citation = "";
                    continue;
                }
                if (citation.length() > 200) {
                    citation = citation.substring(0, 150) + "...";
                }
                break;
            }
            return citation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getUrlCitation(String url) {
        getSemaphore();
        String citation = citations.get(url);
        releaseSemaphore();
        if(citation==null){
            citation = "";
        }
        return citation;
    }

    public int getNumberConnectionsToUrl(String url){
        int nConnections = 0;
        getSemaphore();
        if(connections.containsKey(url)){
            nConnections = connections.get(url).size();
        }
        releaseSemaphore();
        return nConnections;
    }

    public HashSet<String> getUrls(ArrayList<String> words) {
            HashSet<String> result = new HashSet<String>();
            int number_words = words.size();
            if (number_words > 1) {
                return getUrlsIntersection(words);
            } else {
                String search_word = words.get(0);
                getSemaphore();
                if (index.containsKey(search_word)) {
                    result.addAll(index.get(search_word));
                }
                releaseSemaphore();
                return result;
            }
    }

    public HashSet<String> getUrlsIntersection(ArrayList<String> words) {

        HashSet<String> urls_intersection = new HashSet<String>();
        int number_words = words.size();
        int i = 0;
        getSemaphore();
        do {
            String search_word = words.get(i++);
            if (index.containsKey(search_word)) {
                urls_intersection.addAll(index.get(search_word));
            }
        } while (urls_intersection == null && i < number_words); // get hashset of first word from index

        if (!urls_intersection.isEmpty() && i < number_words) {
            for (int j = i; j < number_words; j++) {
                if (index.containsKey(words.get(j))) {
                    urls_intersection.retainAll(index.get(words.get(j))); // intersect hashsets of remaining words
                }
            }
        }
        releaseSemaphore();
        return urls_intersection;

    }
}
