package vnsHealthCRM;

import static org.testng.Assert.assertEquals;


import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import dataDriven.dataDriven;
import extentReport.BaseTest;
import io.github.bonigarcia.wdm.HttpClient;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
public class brokenLinkExtent extends BaseTest{
	public ExtentTest extentTest;

	public static WebDriver driver;

	String myhomePage = "https://vnshealth-crm--fullsbx.sandbox.my.site.com/provider/login";

	String myurl = "";
	HttpURLConnection myhuc = null;
	int responseCode = 200;

	String excelPath = "C:\\\\Users\\\\802072\\\\git\\\\brokenLinkTestCRM\\\\src\\\\test\\\\resources\\\\testData\\\\testData.xlsx";
	String sheetName = "loginInfo";

	@BeforeTest (alwaysRun= true)
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(myhomePage);
	}

	@BeforeMethod
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

		Assert.assertEquals(driver.findElement(By.xpath("//h1[text()='Recent Authorizations']")).getText(), "Recent Authorizations");
		//log("The Login Page url is: "+ myhomePage);
		//log("Login is successful with user name : "+ data.get(1));
	}


	@Test (testName= "Broken Link Test", priority = 1)
	public void testBrokenLinks() {
		ArrayList el= new ArrayList();
		ArrayList adl= new ArrayList();
		ArrayList bl = new ArrayList();
		ArrayList mdl= new ArrayList();

		List<WebElement> mylinks = driver.findElements(By.xpath("//a"));
		log("There are "+mylinks.size()+ " urls in the page under test");
		log("*******************************************************");

		Iterator<WebElement> myit = mylinks.iterator();
		while (myit.hasNext()) {

			myurl = myit.next().getAttribute("href");
			System.out.println("The link is :"+myurl);

			if (myurl == null || myurl.isEmpty()) {
				System.out.println("Empty URL or an Unconfigured URL");
				el.add(myurl);
				continue;
			}
			if (myurl.contains("https://vnshealth")) {
				//System.out.println("This URL is from another domain");
				mdl.add(myurl);
				continue;
			}

			if (!myurl.contains("https://vnshealth")) {
				//System.out.println("This URL is from another domain");
				adl.add(myurl);
				continue;
			}
		}
		
		try {
			myhuc = (HttpURLConnection) (new URL(myurl).openConnection());
			myhuc.setRequestMethod("HEAD");
			myhuc.connect();
			responseCode = myhuc.getResponseCode();

			if (responseCode >= 400) {
				System.out.println(myurl + " This link is broken");
				bl.add(responseCode);

			} else {
				//System.out.println(myurl + " This link is valid");
			}
			//System.out.println("The response code is:"+responseCode);
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		log("A total of "+mdl.size()+ " urls in the page are from the same domain");
		log("The urls from same domain are listed below:");
		mdl.forEach(t -> log((String) t));

		log("*******************************************************");
		log("A total of "+adl.size()+ " urls in the page are from other domains");
		log("The urls from other domains are listed below:");
		adl.forEach(t -> log((String) t));

		log("*******************************************************");
		log("A total of "+el.size()+ " urls are empty or unconfigured");
		log("The empty urls or unconfigured urls are listed below:");
		el.forEach(t -> log((String) t));

		log("*******************************************************");
		log("A total of "+bl.size()+ " links are broken");
		log("The broken links are listed below:");
		bl.forEach(t -> log((String) t));
	}

	@Test (testName="Broken Image Test", priority=2)
	public void testBrokenImages() {
		Integer iBrokenImageCount = 0;

		String status= "passed";
		try
		{
			iBrokenImageCount = 0;
			List<WebElement> image_list = driver.findElements(By.xpath("//img"));

			//System.out.println("The page under test has " + image_list.size() + " image/s");
			log("The page under test has " + image_list.size() + " image/s");
			for (WebElement img : image_list)
			{
				if (img != null)
				{
					CloseableHttpClient client = HttpClientBuilder.create().build();
					HttpGet request = new HttpGet(img.getAttribute("src"));
					HttpResponse response = client.execute(request);

					if (response.getStatusLine().getStatusCode() != 200)
					{
						System.out.println(img.getAttribute("outerHTML") + " is broken.");
						log("The broken image is :"+img.getAttribute("outerHTML"));
						iBrokenImageCount++;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			status = "failed";
			System.out.println(e.getMessage());
		}
		status = "passed";
		//System.out.println("The page " + "has " + iBrokenImageCount + " broken image/s");
		log("The page " + "has " + iBrokenImageCount + " broken image/s");
	}

	@AfterTest
	public void tearUp() {
		driver.close();
	}
}