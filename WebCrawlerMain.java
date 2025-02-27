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

// Task to crawl a web page (simulated using Callable)
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
            // Simulate fetching web page content (replace with actual HTTP request)
            page.content = fetchWebPage(url);
            page.isCrawled = true;
            // Process the content (e.g., extract links, text, etc.)
            processContent(page);
            // Add new URLs found in the content to the crawler queue
            crawler.addUrls(extractUrls(page.content));
        } catch (Exception e) {
            System.err.println("Error crawling " + url + ": " + e.getMessage());
        }
        return page;
    }

    // Simulate fetching web page (replace with real HTTP client)
    private String fetchWebPage(String url) throws IOException, InterruptedException {
        // Simulate network delay and content retrieval
        Thread.sleep(100); // Simulate network latency
        return "Content of " + url; // Simulated content
    }

    // Process the web page content (e.g., extract text, links)
    private void processContent(WebPage page) {
        // Simulate processing (e.g., indexing or parsing)
        System.out.println("Processed: " + page.url + " - Content: " + page.content);
    }

    // Simulate extracting URLs from content (could use regex or HTML parser)
    private List<String> extractUrls(String content) {
        List<String> urls = new ArrayList<>();
        // Simulate finding new URLs (e.g., "http://example.com/page1",
        // "http://example.com/page2")
        if (content.contains("example.com")) {
            urls.add("http://example.com/page" + (new Random().nextInt(3) + 1));
        }
        return urls;
    }
}

// WebCrawler class to manage the crawling process
class WebCrawler {
    private final BlockingQueue<String> urlQueue; // Queue for URLs to crawl
    private final Set<String> visitedUrls; // Track visited URLs to avoid cycles
    private final ExecutorService executorService; // Thread pool
    private final Map<String, WebPage> crawledData; // Store crawled data

    public WebCrawler(int maxThreads, int initialCapacity) {
        this.urlQueue = new LinkedBlockingQueue<>(initialCapacity);
        this.visitedUrls = new HashSet<>();
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.crawledData = new ConcurrentHashMap<>();
    }

    // Add URLs to the queue (with deduplication)
    public void addUrls(List<String> urls) {
        for (String url : urls) {
            if (!visitedUrls.contains(url)) {
                urlQueue.offer(url);
                visitedUrls.add(url);
            }
        }
    }

    // Start crawling with initial URLs
    public void startCrawling(List<String> initialUrls) {
        addUrls(initialUrls);

        // Submit tasks for all URLs in the queue
        while (!urlQueue.isEmpty()) {
            try {
                String url = urlQueue.take(); // Block if queue is empty
                Future<WebPage> future = executorService.submit(new CrawlTask(url, this));
                // Store the result when available
                WebPage page = future.get(5, TimeUnit.SECONDS); // Timeout for safety
                if (page != null && page.isCrawled) {
                    crawledData.put(page.url, page);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.err.println("Error processing URL: " + e.getMessage());
            }
        }

        // Shutdown the executor service
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Error shutting down executor: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    // Get crawled data
    public Map<String, WebPage> getCrawledData() {
        return crawledData;
    }

    // Shutdown the crawler
    public void shutdown() {
        executorService.shutdownNow();
    }
}

public class WebCrawlerMain {
    public static void main(String[] args) {
        // Initialize the web crawler with 4 threads and capacity for 100 URLs
        WebCrawler crawler = new WebCrawler(4, 100);

        // Initial URLs to crawl
        List<String> initialUrls = new ArrayList<>();
        initialUrls.add("http://example.com");
        initialUrls.add("http://example.com/page1");

        // Start crawling
        System.out.println("Starting web crawl...");
        crawler.startCrawling(initialUrls);

        // Print crawled data
        System.out.println("\nCrawled Data:");
        for (Map.Entry<String, WebPage> entry : crawler.getCrawledData().entrySet()) {
            System.out.println("URL: " + entry.getKey() + ", Content: " + entry.getValue().content);
        }

        // Shutdown the crawler
        crawler.shutdown();
    }
}