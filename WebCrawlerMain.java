import java.util.*;
import java.util.concurrent.*;
import java.io.IOException;
import java.net.URL;

//done
class WebPage {
    String url;
    String content;
    boolean isCrawled;

    public WebPage(String url) {
        this.url = url;
        this.content = "";
        this.isCrawled = false;
    }
}

class CrawlTask implements Callable<WebPage> {
    private final String url;
    private final WebCrawler crawler;

    public CrawlTask(String url, WebCrawler crawler) {
        this.url = url;
        this.crawler = crawler;
    }

    @Override
    public WebPage call() {
        WebPage page = new WebPage(url);
        try {
            page.content = fetchWebPage(url);
            page.isCrawled = true;
            processContent(page);
            crawler.addUrls(extractUrls(page.content));
        } catch (Exception e) {
            System.err.println("Error crawling " + url + ": " + e.getMessage());
        }
        return page;
    }

    private String fetchWebPage(String url) throws IOException, InterruptedException {
        Thread.sleep(100);
        return "Content of " + url;
    }

    private void processContent(WebPage page) {
        System.out.println("Processed: " + page.url + " - Content: " + page.content);
    }

    private List<String> extractUrls(String content) {
        List<String> urls = new ArrayList<>();
        if (content.contains("example.com")) {
            urls.add("http://example.com/page" + (new Random().nextInt(3) + 1));
        }
        return urls;
    }
}

class WebCrawler {
    private final BlockingQueue<String> urlQueue;
    private final Set<String> visitedUrls;
    private final ExecutorService executorService;
    private final Map<String, WebPage> crawledData;

    public WebCrawler(int maxThreads, int initialCapacity) {
        this.urlQueue = new LinkedBlockingQueue<>(initialCapacity);
        this.visitedUrls = new HashSet<>();
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.crawledData = new ConcurrentHashMap<>();
    }

    public void addUrls(List<String> urls) {
        for (String url : urls) {
            if (!visitedUrls.contains(url)) {
                urlQueue.offer(url);
                visitedUrls.add(url);
            }
        }
    }

    public void startCrawling(List<String> initialUrls) {
        addUrls(initialUrls);

        while (!urlQueue.isEmpty()) {
            try {
                String url = urlQueue.take();
                Future<WebPage> future = executorService.submit(new CrawlTask(url, this));

                WebPage page = future.get(5, TimeUnit.SECONDS);
                if (page != null && page.isCrawled) {
                    crawledData.put(page.url, page);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.err.println("Error processing URL: " + e.getMessage());
            }
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Error shutting down executor: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, WebPage> getCrawledData() {
        return crawledData;
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}

public class WebCrawlerMain {
    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler(4, 100);
        List<String> initialUrls = new ArrayList<>();
        initialUrls.add("http://example.com");
        initialUrls.add("http://example.com/page1");
        System.out.println("Starting web crawl...");
        crawler.startCrawling(initialUrls);
        System.out.println("\nCrawled Data:");
        for (Map.Entry<String, WebPage> entry : crawler.getCrawledData().entrySet()) {
            System.out.println("URL: " + entry.getKey() + ", Content: " + entry.getValue().content);
        }
        crawler.shutdown();
    }
}