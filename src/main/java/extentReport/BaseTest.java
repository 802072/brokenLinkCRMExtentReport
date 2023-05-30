package extentReport;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {
	public static WebDriver driver;
	public static String screenshotsSubFolderName;
	public static ExtentReports extentReports;
	public static ExtentTest extentTest;

	@BeforeTest
	public void setup(ITestContext context) {

		extentTest = extentReports.createTest(context.getName());
	}

	@AfterTest
	public void tearUp() {
		driver.quit();
	}

	@BeforeSuite
	public void initialiseExtentReports() throws Exception {
		ExtentSparkReporter sparkReporter_all = new ExtentSparkReporter("test-output/brokenLinkExtentTests.html");
		sparkReporter_all.config().setDocumentTitle("VNSHealth_CRM_Broken_Link_Test");
		sparkReporter_all.config().setReportName("VNSHealth-CRM Broken Link Test Report");

		//sparkReporter_all.loadJSONConfig("src/test/resources/extent-reports-config.json");
		// extentReports.attachReporter(sparkReporter_all);

		ExtentSparkReporter sparkReporter_failed = new ExtentSparkReporter("test-output/FailedTests.html");
		sparkReporter_failed.filter().statusFilter().as(new Status[] {Status.FAIL}).apply();
		sparkReporter_all.config().setDocumentTitle("VNSHealth-CRM Broken Link Test Report");
		sparkReporter_failed.config().setReportName("Failure report");

		extentReports = new ExtentReports();
		extentReports.attachReporter(sparkReporter_all, sparkReporter_failed);

		extentReports.setSystemInfo("OS", System.getProperty("os.name"));
		extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));

	}

	@AfterSuite
	public void generateExtentReports() throws Exception {
		extentReports.flush();
		//        Desktop.getDesktop().browse(new File("test-output/AllTests.html").toURI());
		//        Desktop.getDesktop().browse(new File("test-output/FailedTests.html").toURI());
	}

	@AfterMethod
	public void checkStatus(Method m, ITestResult result) {
		if(result.getStatus() == ITestResult.FAILURE) {
			extentTest.fail(m.getName() + " has failed");
			extentTest.fail(result.getThrowable());
		} else if(result.getStatus() == ITestResult.SUCCESS) {
			extentTest.pass(m.getName() + " has passed");
		}

		extentTest.assignCategory(m.getAnnotation(Test.class).groups());
	}



	public void logCapture(String description, String responseBody){
		extentReports.createTest(description)
		//.log(Status.INFO, "<b><i>"+baseApi+"</i></b>")
		//.log(Status.INFO,"Request body is: "+requestBody)
		//.log(Status.INFO,"ResponseBody is: "+responseBody);
		.log(Status.INFO, responseBody);
		//.log(Status.INFO, MarkupHelper.createCodeBlock(requestBody, CodeLanguage.JSON))
		//.log(Status.INFO,MarkupHelper.createCodeBlock(responseBody, CodeLanguage.JSON));

		extentReports.flush();
	}


	public void log(String description){
		extentTest.log(Status.INFO,description);
	}

	public String captureScreenshot(String fileName) {
		if(screenshotsSubFolderName == null) {
			LocalDateTime myDateObj = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
			screenshotsSubFolderName = myDateObj.format(myFormatObj);
		}

		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
		File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
		File destFile = new File("./Screenshots/"+ screenshotsSubFolderName+"/"+fileName);
		try {
			FileUtils.copyFile(sourceFile, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Screenshot saved successfully");
		return destFile.getAbsolutePath();
	}
}