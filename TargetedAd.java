import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TargetedAd {

  static ArrayList<Review> reviewList = new ArrayList<Review>();
  static ArrayList<Review> targetReviews = new ArrayList<Review>();
  static int numberOfPages = 6;

  public static void main(String[] args) throws IOException {

    System.out.println("Starting server...");

    Process pr = null;

    try {

      Runtime rt = Runtime.getRuntime();

      pr = rt.exec(new String[] { "jwebserver" });

      pr.waitFor(3, TimeUnit.SECONDS);

    } catch (Exception e) {

      e.printStackTrace();

    }

    System.out.println("Beginning JSOUP Extraction...");

    extractWithJsoup();

    System.out.println("Getting Target Words...");

    getTargetWords();

    System.out.println("Beginning Advertisement Generation...");

    generateAds();

    System.out.println("Advertisements generated and saved to advertisements.txt");

    if (pr != null) {
      pr.destroy();
    }

    System.out.println("Server stopped.");

  }

  public static void extractWithJsoup() throws IOException {

    FileWriter fw = new FileWriter("reviews.txt", false);

    fw.write("stars, name, review\n");

    for (int i = 1; i <= numberOfPages; i++) {

      Document doc = Jsoup.connect("http://127.0.0.1:8000/amazon" + i + ".html").get();

      Elements reviews = doc.getElementsByClass("a-section review aok-relative");

      for (Element element : reviews) {

        String stars = element.select(".a-icon-alt").text();
        String name = element.select(".a-profile-name").text();
        String review = element.select(".a-size-base.review-text.review-text-content").text();

        reviewList.add(new Review(name, review, stars));

        fw.append(stars + ", " + name + ", " + review + "\n");

      }

    }

    fw.close();

  }

  public static void getTargetWords() {

    DataCollector dataCollector = new DataCollector();
    dataCollector.setData("reviews.txt", "targetWords.txt");

    String targetWord = dataCollector.getNextTargetWord();

    while (!targetWord.equals("NONE")) {

      for (Review review : reviewList) {

        if (review.getContent().contains(targetWord)) {

          targetReviews.add(review);

        }
      }

      targetWord = dataCollector.getNextTargetWord();

    }
  }

  public static void generateAds() throws IOException {

    FileWriter fw3 = new FileWriter("advertisements.txt", false);

    Set<String> users = new HashSet<>();

    for (Review review : targetReviews) {

      try {

        String reviewer = review.getReviewer();

        if (!users.contains(reviewer)) {

          String advertisement = ChatGPT
              .prompt("give me a targeted advertisement (very specific), addressed to " + reviewer
                  + " for shoes based on the review: " + review.getContent());

          fw3.append("To: " + reviewer + ", " + advertisement + "\n");

          users.add(reviewer);

        }
      } catch (Exception e) {

        System.out.println("API call failed, skipping...");

      }
    }

    fw3.close();

  }
}
