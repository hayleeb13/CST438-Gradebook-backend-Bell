package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *  In SpringBootTest environment, the test program may use Spring repositories to 
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestAddAssignment {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/Users/bell0/Downloads/chromedriver_win32/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_STUDENT_NAME = "Test";

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addAssignmentTest() throws Exception {

//		Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle(TEST_COURSE_TITLE);

//	    add an assignment that needs grading for course 99999
		Assignment a = new Assignment();
		a.setCourse(c);
		// set assignment due date to 24 hours ago
		a.setDueDate(new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		a.setName(TEST_ASSIGNMENT_NAME);
		a.setNeedsGrading(1);

//	    add a student TEST into course 99999
		Enrollment e = new Enrollment();
		e.setCourse(c);
		e.setStudentEmail(TEST_USER_EMAIL);
		e.setStudentName(TEST_STUDENT_NAME);

		courseRepository.save(c);
		a = assignmentRepository.save(a);
		e = enrollmentRepository.save(e);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
			/*
			* locate input element for test assignment by assignment name
			* 
			* To select a radio button in a Datagrid display
			* 1.  find the elements in the assignmentName column of the data grid table.
			* 2.  locate the element with test assignment name and click the input tag.
			*/
			
			driver.findElement(By.xpath("//a[2]")).click();
			Thread.sleep(SLEEP_DURATION);
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys("TestAssignment");
			driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys("10-10-2022");
			driver.findElement(By.xpath("//input[@name='course_id']")).sendKeys("99999");
			Thread.sleep(SLEEP_DURATION);

			driver.findElement(By.xpath("//button[@id='AddAssignment']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.xpath("//a")).click();

		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  clean up database so the test is repeatable.
			 */
			enrollmentRepository.delete(e);
			assignmentRepository.delete(a);

			driver.quit();
		}

	}
}
