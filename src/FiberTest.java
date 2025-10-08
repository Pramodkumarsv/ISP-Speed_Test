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

public class FiberTest {

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
            driver.get("https://fibertest.net/");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));
            WebElement startButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("_inner_img")));
            startButton.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[contains(@src, 'asset/img/home/startagain.png')]")));

            WebElement downloadSpeed = driver.findElement(By.id("dspeed"));
            WebElement uploadSpeed = driver.findElement(By.id("uspeed"));
            WebElement ISP = driver.findElement(By.id("appendISP"));
            String download = downloadSpeed.getText();
            String upload = uploadSpeed.getText();
            String input = ISP.getText();
            String[] lines = input.split("\\r?\\n");
            String ispLine = lines[0];   
            String ipLine = lines[1];    
            String tata = ispLine.replace("ISP - ", "").split(" ")[0];
            String ip = ipLine;
            String isp = tata + " " + ip;
            System.out.println("Download Speed: " + download);
            System.out.println("Upload Speed: " + upload);
            System.out.println("ISP: " + isp);

            appendDataToCSV(download, upload, isp);

        } catch (Exception e) {
            System.out.println("Website not loading or error occurred, logging as slow network.");
            appendDataToCSV("Error", "Error", "Slow Network");
        } finally {
            driver.quit();
        }
    }

    public static void appendDataToCSV(String download, String upload, String isp) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = timestamp + "," + download + "," + upload + "," + isp;

        try {
            File file = new File("speedtest_data_TATA.csv");
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
