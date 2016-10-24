
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumExample {
    private WebDriver driver;

    public SeleniumExample(boolean mobile) {
    	
        System.setProperty("webdriver.chrome.driver",
                           "/home/neha/Downloads/chromedriver");
        if (mobile) {
            final DesiredCapabilities dc = DesiredCapabilities.chrome();
            dc.setCapability(ChromeOptions.CAPABILITY, new ChromeOptions() {
                {
                    setExperimentalOption("mobileEmulation", new HashMap<String, Object>() {
                        private static final long serialVersionUID =
                            6294953054374614483L;
                        {
                            put("deviceName", "Google Nexus 5");
                        }
                    });
                }
            });
            this.driver = new ChromeDriver(dc);
        } else {
            this.driver = new ChromeDriver();
        }
    }

    private void scrape() {
        driver.get("http://confluence.arc42.org/display/templateEN/"
         + "arc42+Template+%28English%29+-+Home");
        List<String> searchResults = new ArrayList<>();
        try {
            WebElement form = driver.findElement(By.name("pagetreesearchform"));
            WebElement searchElement = form.findElement(By.name("queryString"));
            searchElement.sendKeys("Requirements");
            form.submit();
            int currentPageIndex = 1;
            do {
                searchResults.addAll(driver.findElements(
                        By.xpath("//a[contains(@class,'search-result-link')]"))
                        .stream().map(a -> a.getText() + "->"
                        + a.getAttribute("href"))
                        .collect(Collectors.toList()));

            } while (jumpToNextPage(++currentPageIndex));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        searchResults.stream().sorted().forEach(System.out::println);
        driver.close();

    }

    private boolean jumpToNextPage(int nextPageIndex) {
        boolean found = false;
        // per default 3s wait
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
        List<WebElement> nextLink = driver.findElements(
            By.className("pagination-next"));
        if (nextLink.size() > 0) {
            nextLink.get(0).click();
            final WebDriverWait wait = new WebDriverWait(driver, 1);
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                      By.className("pagination-curr"),
                      String.valueOf(nextPageIndex)));
            found = true;
        }
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        return found;
    }

    public static void main(String[] args) {
        new SeleniumExample(false).scrape();
    }
}