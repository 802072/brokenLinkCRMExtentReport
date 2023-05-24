package brokenLink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import testBrokenLinkCRM.dataDriven;

public class brokenLinkCRM {

	public static WebDriver driver;

	String myhomePage = "https://vnshealth-crm--fullsbx.sandbox.my.site.com/provider/login";

	String myurl = "";
	HttpURLConnection myhuc = null;
	int responseCode = 200;

	String excelPath = "C:\\Users\\802072\\git\\brokenLinkTestCRM\\src\\test\\resources\\testData\\testData.xlsx";
	String sheetName = "loginInfo";


	@BeforeTest
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(myhomePage);

	}

	@Test (description= "log in to homepage")
	public void logIn() throws IOException {
		dataDriven d=new dataDriven();
		ArrayList data=d.getData("User1","loginInfo");
		String username= (String) data.get(1);
		String password= (String) data.get(2);

		//enter username
		WebElement uname = driver.findElement(By.xpath("//input[@id='input-25']"));
		uname.sendKeys(username);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//enter password
		WebElement pwd = driver.findElement(By.xpath("//input[@id='input-26']"));
		pwd.sendKeys(password);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		//login
		WebElement signIn = driver.findElement(By.xpath("//button[contains(text(),'Log In')]"));
		signIn.click();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Reporter.log("Login is successful with user name : "+ data.get(1));
	}

	@Test (description= "Check for broken links in homepage")
	public void testBrokenLinks() {
		ArrayList ls = new ArrayList();

		List<WebElement> mylinks = driver.findElements(By.xpath("//a"));
		Reporter.log("The total hyperlinks count in page is :"+mylinks.size());
		Iterator<WebElement> myit = mylinks.iterator();
		while (myit.hasNext()) {

			myurl = myit.next().getAttribute("href");
			//System.out.println("The link is :"+myurl);


			if (myurl == null || myurl.isEmpty()) {
				//System.out.println("Empty URL or an Unconfigured URL");
				Reporter.log("Empty URL or an Unconfigured URL");
				continue;
			}

			if (!myurl.contains("https://vnshealth")) {
				//System.out.println("This URL is from another domain");
				Reporter.log(myurl+ "This URL is from another domain");
				continue;
			}

			try {
				myhuc = (HttpURLConnection) (new URL(myurl).openConnection());
				myhuc.setRequestMethod("HEAD");
				myhuc.connect();
				responseCode = myhuc.getResponseCode();

				if (responseCode >= 400) {
					//System.out.println(myurl + " This link is broken");
					ls.add(responseCode);

					Reporter.log(myurl + " This link is broken");
				} else {
					//System.out.println(myurl + " This link is valid");
					//Reporter.log(myurl + " This link is valid");
				}
				//System.out.println("The response code is:"+responseCode);
				//Reporter.log("The response code is:"+responseCode);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		Reporter.log("The broken links count is: "+ls.size());
	}


	@AfterTest
	public void tearUp() {
		driver.close();

	}

}
