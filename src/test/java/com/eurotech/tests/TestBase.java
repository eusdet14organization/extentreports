package com.eurotech.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.eurotech.context.TestContext;
import com.eurotech.utils.ConfigurationReader;
import com.eurotech.utils.DriverFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;

public class TestBase {
    TestContext context;

    ExtentTest test;
    static ExtentReports report;
    static ExtentSparkReporter sparkReporter;

    @BeforeAll
    public static void beforeAll(){
        String projectPath = System.getProperty("user.dir");
        String path = projectPath + "/test-output/report.html";

        sparkReporter = new ExtentSparkReporter(path);

        sparkReporter.config().setDocumentTitle("Автотесты для сайта suecelab");
        sparkReporter.config().setReportName("Отчёт по тестам");
        sparkReporter.config().setTheme(Theme.STANDARD);

        report = new ExtentReports();
        report.attachReporter(sparkReporter);

        report.setSystemInfo("Browser", ConfigurationReader.get("browser"));
        report.setSystemInfo("OS", System.getProperty("os name"));
        report.setSystemInfo("Environment", System.getProperty("Test"));
    }


    @BeforeEach
    public void beforeEach(TestInfo testInfo){

        test = report.createTest(testInfo.getDisplayName());

        context = new TestContext();
        context.driver = DriverFactory.get();
        context.wait = new WebDriverWait(context.driver, Duration.ofSeconds(Long.parseLong(ConfigurationReader.get("timeout"))));
        context.actions = new Actions(context.driver);
        context.js =  (JavascriptExecutor) context.driver;
    }

    @AfterEach
    public void afterEach(){
        String screenshot = ((TakesScreenshot)context.driver).getScreenshotAs(OutputType.BASE64);
        test.addScreenCaptureFromBase64String(screenshot);
        if(context.driver !=null){
            context.driver.quit();
        }
    }

    @AfterAll
    public static void afterAll(){
        report.flush();
    }

    @RegisterExtension
    private final AfterTestExecutionCallback afterTest = context -> {
        final Optional<Throwable> exception = context.getExecutionException();
        exception.ifPresentOrElse(this::onError, () -> onSuccess(context));
    };

    private void onSuccess(ExtensionContext context) {
        test.pass(context.getDisplayName() + "passed!");
    }

    private void onError(Throwable throwable1) {
        test.fail(throwable1.getMessage());
    }

}
