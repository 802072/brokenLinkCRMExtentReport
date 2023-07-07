package providerPortalTests;

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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import dataDriven.dataDriven;
import extentReport.BaseTest1;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class BrokenImageTestPP extends BaseTest1 {
	public ExtentTest extentTest;

	String myhomePage = "https://providerportal.vnshealthplans.org/login";

	String myurl = "";
	HttpURLConnection myhuc = null;
	int responseCode = 200;

	String excelPath = "C:\\Users\\802072\\git\\brokenImageTestCRM\\src\\test\\resources\\testData\\testData.xlsx";
	String sheetName = "loginInfo";
	WebDriver driver;

	ArrayList allList = new ArrayList();
	ArrayList emptyLst = new ArrayList();
	ArrayList anotherDomainLst = new ArrayList();
	ArrayList brokenLst = new ArrayList();
	ArrayList myDomainLst = new ArrayList();

	// @Parameters({ "browserName" })
	@BeforeTest
	public void loginProvider() throws IOException, InterruptedException {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(myhomePage);
		driver.get(myhomePage);

		dataDriven d = new dataDriven();
		ArrayList data = d.getData("User2", "loginInfo");
		String username = (String) data.get(1);
		String password = (String) data.get(2);

		WebElement enter = driver.findElement(By.xpath("//button[contains(text(), 'Enter')]"));
		enter.click();

		// enter username
		WebElement uname = driver.findElement(By.xpath("//input[@id='username']"));
		uname.sendKeys(username);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// enter password
		WebElement pwd = driver.findElement(By.xpath("//input[@id='password']"));
		pwd.sendKeys(password);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// login
		WebElement signOn = driver.findElement(By.xpath("//button[contains(text(), 'Sign On')]"));
		signOn.click();
		Thread.sleep(10000);
		// Assert.assertEquals(driver.findElement(By.xpath("//a[text()='Home']")).getText(),
		// "Home");
		log("The Login Page url is: " + myhomePage);
		log("Login is successful with user name : " + data.get(1));
		Thread.sleep(5000);
	}

	@AfterMethod
	public void testBrokenImages() {
		Integer iBrokenImageCount = 0;

		String status = "passed";
		try {
			iBrokenImageCount = 0;
			List<WebElement> Image_list = driver.findElements(By.xpath("//img"));

			// System.out.println("The page under test has " + Image_list.size() + "
			// Image/s");
			log("The page under test has " + Image_list.size() + " Image/s");
			for (WebElement img : Image_list) {
				if (img != null) {
					CloseableHttpClient client = HttpClientBuilder.create().build();
					HttpGet request = new HttpGet(img.getAttribute("src"));
					CloseableHttpResponse response = client.execute(request);

					if (response.getCode() != 200) {
						// System.out.println(img.getAttribute("outerHTML") + " is broken.");
						log("The broken Image is :" + img.getAttribute("outerHTML"));
						iBrokenImageCount++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = "failed";
			// System.out.println(e.getMessage());
		}
		status = "passed";
		// System.out.println("The page " + "has " + iBrokenImageCount + " broken
		// Image/s");
		log("The page " + "has " + iBrokenImageCount + " broken Image/s");

	}

	// Homepage
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesHomePage() throws InterruptedException {

	}

	// Rosters
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesRosterPage() throws InterruptedException {
		WebElement patients = driver.findElement(By.xpath("(//a[@class='menu'])[1]"));
		Actions action = new Actions(driver);
		action.moveToElement(patients).perform();
		Thread.sleep(5000);
		WebElement rosters = driver.findElement(By.xpath("//a[normalize-space()='Rosters']"));
		rosters.click();
	}

	// EligSearch
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesEligibilitySearch() throws InterruptedException {
		WebElement patients = driver.findElement(By.xpath("(//a[@class='menu'])[1]"));
		Actions action = new Actions(driver);
		action.moveToElement(patients).perform();
		Thread.sleep(5000);
		WebElement eligSearch = driver.findElement(By.xpath("//a[normalize-space()='Eligibility Search']"));
		eligSearch.click();
	}

	// Enrollment Ref
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesEnrollmentRef() throws InterruptedException {
		WebElement patients = driver.findElement(By.xpath("(//a[@class='menu'])[1]"));
		Actions action = new Actions(driver);
		action.moveToElement(patients).perform();
		Thread.sleep(5000);
		WebElement eligSearch = driver.findElement(By.xpath("//a[normalize-space()='Enrollment Referrals']"));
		eligSearch.click();
	}

	// Claims
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesClaimsAndPayments() throws InterruptedException {
		WebElement claims = driver.findElement(By.xpath("//a[normalize-space()='Claims & Payments']"));
		claims.click();
	}

	// Authorizations
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesAuthPage() throws InterruptedException {
		WebElement auth = driver.findElement(By.xpath("//a[normalize-space()='Authorizations']"));
		auth.click();
	}

	// Provider Directory
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesProviderDir() throws InterruptedException {
		WebElement provDirectory = driver.findElement(By.xpath("//a[normalize-space()='Provider Directory']"));
		provDirectory.click();
	}

	// Formulary Search

	@Test(groups = "Broken Image Test")
	public void testBrokenImagesFormularySearch() throws InterruptedException {
		WebElement formularySearch = driver.findElement(By.xpath("//a[normalize-space()='Formulary Search']"));
		formularySearch.click();
	}

	// Provider Toolkit
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesProviderToolkit() throws InterruptedException {
		WebElement providerToolkit = driver.findElement(By.xpath("//a[normalize-space()='Provider Toolkit']"));
		providerToolkit.click();
	}

	// Appeals and Disputes
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesAppealsAndDisputes() throws InterruptedException {
		WebElement appealsAndDisputes = driver.findElement(By.xpath("//a[normalize-space()='Appeals & Disputes']"));
		appealsAndDisputes.click();
	}

	// Communication Center
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesCommunicationCenter() throws InterruptedException {
		WebElement comCenter = driver.findElement(By.xpath("(//a[contains(text(),'Communication Center')])[1]"));
		comCenter.click();
	}

	// Resources
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesResources() throws InterruptedException {
		WebElement comCenter = driver.findElement(By.xpath("//a[normalize-space()='Resources']"));
		comCenter.click();
	}

	// Resources
	@Test(groups = "Broken Image Test")
	public void testBrokenImagesMyAcc() throws InterruptedException {
		WebElement myAcc = driver.findElement(By.xpath("//a[normalize-space()='My Account']"));
		myAcc.click();
	}

	// TechSupport

	@Test(groups = "Broken Image Test")
	public void testBrokenImagesTechSupp() throws InterruptedException {
		WebElement techSupp = driver.findElement(By.xpath("(//a[normalize-space()='Technical Support'])[1]"));
		techSupp.click();
	}

	// Contact Us

	@Test(groups = "Broken Image Test")
	public void testBrokenImagesContactUs() throws InterruptedException {
		WebElement contactUs = driver.findElement(By.xpath("(//a[normalize-space()='Contact Us'])[1]"));
		contactUs.click();
	}

	@AfterTest
	public void tearUp() {
		driver.close();
	}
}
