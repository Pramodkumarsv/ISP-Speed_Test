import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class OctaTest {

    public static void main(String[] args) {

        while (true) { // Run every 10 minutes
            runSpeedTestAndSave();
            try {
                Thread.sleep(10 * 60 * 1000); // 10 minutes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void runSpeedTestAndSave() {
        System.setProperty("webdriver.chrome.driver", "D:\\speedTest\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.speedtest.net/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
            Thread.sleep(5000);
            WebElement privacy = wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
            privacy.click();
            WebElement startButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Go']")));
            startButton.click();
            Thread.sleep(60000);
            //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='Result ID']")));

            WebElement downloadSpeed = driver.findElement(By.xpath("//span[@class='result-data-large number result-data-value download-speed']"));
            WebElement uploadSpeed = driver.findElement(By.xpath("//span[@class='result-data-large number result-data-value upload-speed']"));
            WebElement ISP = driver.findElement(By.xpath("//div[@class='result-label js-data-isp']"));
            WebElement IP = driver.findElement(By.xpath("//div[@class='result-data js-data-ip']"));

            String download = downloadSpeed.getText();
            String upload = uploadSpeed.getText();
            String isp = ISP.getText();
            String ip = IP.getText();
            String Wan = isp + " " + ip;

            System.out.println("Download Speed: " + download);
            System.out.println("Upload Speed: " + upload);
            System.out.println("ISP: " + isp);

            appendDataToCSV(download, upload, Wan);

        } catch (Exception e) {
            System.out.println("Website not loading or error occurred, logging as slow network.");
            appendDataToCSV("Error", "Error", "Slow Network");
        } finally {
            driver.quit();
        }
    }

    public static void appendDataToCSV(String download, String upload, String Wan) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = timestamp + "," + download + "," + upload + "," + Wan ;

        try {
            File file = new File("speedtest_data_JIO.csv");
            boolean fileExists = file.exists();

            FileWriter fw = new FileWriter(file, true); // append mode
            PrintWriter pw = new PrintWriter(fw);

            if (!fileExists) {
                pw.println("Time,Download,Upload,ISP");
            }

            pw.println(line);
            pw.close();
            fw.close();

            System.out.println("Data saved to CSV: " + line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
