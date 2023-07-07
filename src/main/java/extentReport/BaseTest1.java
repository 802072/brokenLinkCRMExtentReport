package extentReport;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
//import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BaseTest1 implements ITestListener {
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

	@Parameters({ "browser" })
	@BeforeSuite
	public void initialiseExtentReports() throws IOException, DocumentException, com.itextpdf.text.DocumentException {

		ExtentSparkReporter sparkReporter_all = new ExtentSparkReporter("ProviderPortalBrokenLinkAndImageTests.html");
		sparkReporter_all.config().setReportName("Provider Portal Broken Link and Image Test");

		extentReports = new ExtentReports();
		extentReports.attachReporter(sparkReporter_all);

		extentReports.setSystemInfo("OS", System.getProperty("os.name"));
		extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
		
	}

	@AfterSuite
	public void generateExtentReports() throws Exception {
		extentReports.flush();
	}

	@AfterMethod
	public void checkStatus(Method m, ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			extentTest.fail(m.getName() + " has failed");
			extentTest.fail(result.getThrowable());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			extentTest.pass(m.getName() + " has passed");
		}

		extentTest.assignCategory(m.getAnnotation(Test.class).groups());
	}

	public void logCapture(String description, String responseBody) {
		extentReports.createTest(description).log(Status.PASS, responseBody);
		// .log(Status.INFO, "<b><i>"+baseApi+"</i></b>")
		// .log(Status.INFO,"Request body is: "+requestBody)
		// .log(Status.INFO,"ResponseBody is: "+responseBody);
		// .log(Status.INFO, MarkupHelper.createCodeBlock(requestBody,
		// CodeLanguage.JSON))
		// .log(Status.INFO,MarkupHelper.createCodeBlock(responseBody,
		// CodeLanguage.JSON));

		extentReports.flush();
	}

	public void log(String description) {
		// extentTest.log(Status.INFO,description);
		extentTest.log(Status.PASS, description);
	}



}
