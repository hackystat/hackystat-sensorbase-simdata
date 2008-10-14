package org.hackystat.simdata.simpletelemetry;

import java.util.Random;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.simdata.SimData;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Illustrates simple use of Telemetry to understand development. There are two developers, Joe and
 * Bob, who are working on a Project in the "simpletelemetry" directory. Joe always works on the
 * file "Joe.java", and Bob always works on the file "Bob.java". This scenario creates four
 * simulated Scrum "sprints", each lasting 10 days.
 * <ul>
 * <li> Sprint 1: The "Healthy" project: nominal effort by both developers, consistently high
 * coverage, regular builds, commits, and tests, low churn, low average complexity, 
 * gradually increasing coverage.
 * <li> Sprint 2: The "Late Start": low effort, size, builds, tests, commits early in the sprint by
 * both developers, then a sudden burst of activity at the end. As a result, testing, coverage, 
 * and complexity suffers.
 * <li> Sprint 3: "Code Entropy": During this sprint, developers work consistently, but churn is
 * high, test coverage is gradually falling, and complexity is increasing. This is a kind of 
 * "early warning" that something is wrong with the project.
 * <li> Spring 4: "Resource mismanagement": During this sprint, one developer is essentially idle,
 * the other is putting in 12 hour days, a zillion commits, etc. Testing and coverage are low, and
 * average complexity is high. This
 * is an indication that project resources are not being allocated effectively. (I used to call this
 * the "Freeloader" scenario, but it's unfair to assume that the "idle" developer is blowing off
 * work. The idle developer might be working 12 hour days too, but just on non-development work. The
 * real issue is that the second developer is over-committed, as evidenced by decreased testing and
 * quality. )
 * </ul>
 * 
 * @author Philip Johnson
 * 
 */
public class SimpleTelemetry {
  private SimData simData;
  // Package private to support testing.
  static String joe = "joe.simpletelemetry";
  static String bob = "bob.simpletelemetry";
  static String project = "simpletelemetry";
  static String startString = "2007-07-02";
  static String endString = "2007-09-01";
  
  private XMLGregorianCalendar projectStart = Tstamp.makeTimestamp(startString); // Monday.
  private XMLGregorianCalendar projectEnd = Tstamp.makeTimestamp(endString);
  private String projectUriPattern = "*/" + project + "/*";
  private String joeDir = "/users/joe/" + project + "/";
  private String bobDir = "/users/bob/" + project + "/";
  private String joeFile = joeDir + "Joe.java";
  private String bobFile = bobDir + "Bob.java";
  private Random random = new Random(0);
  
  private static final String SUCCESS = "Success";
  private static final String PASS = "pass";
  private static final String LOGPREFIX = "SimpleTelemetry: Making data for day: ";
  
  /**
   * Runs the SimpleTelemetry scenerio.  Creates the two users, the project, and sends the data.
   * @param host The SensorBase host that will receive the data. 
   * @throws Exception If problems occur.
   */
  public SimpleTelemetry(String host) throws Exception {
    this.simData = new SimData(host);
    this.simData.getLogger().info("Initializing SimpleTelemetry scenario");
    this.simData.makeUser(joe);
    this.simData.makeUser(bob);
    this.simData.makeProject(project, joe, projectStart, projectEnd, projectUriPattern);
    this.simData.addMember(project, joe, bob);
    
    makeSprint1();
    makeSprint2();
    makeSprint3();
    makeSprint4();
    // Make sure all remaining data is sent. 
    this.simData.quitShells();
  }
  
  /**
   * Sprint 1: Illustrates "healthy" process and product metrics. 
   * <ul>
   * <li> Effort is constant, between three and hour hours a day.
   * <li> Size increases steadily, starting at about 100 and increasing by 10 or so lines per day.
   * <li> Builds and unit tests between 2-6 times a day.
   * <li> Coverage is always at least 80%.
   * <li> Average complexity is low and stable (between 3 and 6).
   * <li> CodeIssues is stable and low, between 5 and 10 per file.
   * <li> They each commit once a day, with relatively low churn (less than 20%).
   * </ul>
   * @throws Exception If problems occur.
   */
  private void makeSprint1() throws Exception {
    for (int i = 0; i < 10; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(projectStart, i);
      this.simData.getLogger().info(LOGPREFIX + day);
      
      // Effort is constant, between three and four hours a day.
      this.simData.addDevEvents(joe, day, (12 * 3) + random.nextInt(12), joeFile);
      this.simData.addDevEvents(bob, day, (12 * 3) + random.nextInt(12), bobFile);
      
      // Size increases steadily, starting at about 100 and increasing by 50-60 LOC per day.
      int joeFileSize = 100 + (i * 50) + random.nextInt(10); 
      int bobFileSize = 100 + (i * 50) + random.nextInt(10); 
      simData.addFileMetric(joe, day, joeFile, joeFileSize, day);
      simData.addFileMetric(joe, day, bobFile, bobFileSize, day);
      
      // Complexity is low and stable (between 3 and 6).
      simData.addComplexity(joe, day, bobFile, bobFileSize, day, 3 + random.nextInt(3));
      simData.addComplexity(joe, day, joeFile, joeFileSize, day, 3 + random.nextInt(3));
      
      // Builds and unit tests between 2-6 times a day.
      simData.addBuilds(joe, day, joeDir, SUCCESS, 2 + random.nextInt(5));
      simData.addBuilds(bob, day, bobDir, SUCCESS, 2 + random.nextInt(5));
      simData.addUnitTests(joe, day, joeFile, PASS, 2 + random.nextInt(5));
      simData.addUnitTests(bob, day, bobFile, PASS, 2 + random.nextInt(5));
      
      // Coverage is always at least 80%.
      int joeCoverage = 80 + random.nextInt(20);
      int bobCoverage = 80 + random.nextInt(20);
      simData.addCoverage(joe, day, joeFile, joeCoverage, joeFileSize,  day);
      simData.addCoverage(joe, day, bobFile, bobCoverage, bobFileSize, day);
      
      // Joe commits twice a day, and Bob commits once, with relatively low churn (50-60 LOC).
      simData.addCommit(joe, day, joeFile, 20 + random.nextInt(10));
      simData.addCommit(joe, day, joeFile, 20 + random.nextInt(10));
      simData.addCommit(bob, day, bobFile, 50 + random.nextInt(10));
      
      // Code issues are low and stable, between 0 and 4 per file.
      simData.addCodeIssues(joe, day, joeFile, random.nextInt(4));
      simData.addCodeIssues(bob, day, bobFile, random.nextInt(4));
    }
  }
  
  /**
   * Sprint 2: Work suffers from not getting started until late in the sprint.
   * <ul>
   * <li> Effort is very low for first five days, then very high.
   * <li> Size is very low for first five days, then jumps.
   * <li> Builds and unit tests very low for first five days, then high.
   * <li> Average complexity low for first five days, then jumps. 
   * <li> Coverage is very low for first five days, never gets high.
   * <li> 1 commit only for first five days, then many in last five days. High churn each time.
   * <li>> 1 code issue for first five days, then increasing in last five days.
   * </ul>
   * @throws Exception If problems occur.
   */
  private void makeSprint2() throws Exception {
    // Move forward 10 days to start the second sprint.
    int dayOffset = 10;
    // Do first five days of sprint in one loop.
    for (int i = dayOffset + 0; i < dayOffset + 5; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(projectStart, i);
      this.simData.getLogger().info(LOGPREFIX + day);
      
      // Effort is low, between zero minutes and 20 minutes.
      this.simData.addDevEvents(joe, day, 0 + random.nextInt(4), joeFile);
      this.simData.addDevEvents(bob, day, 0 + random.nextInt(4), bobFile);
      
      // Size is very low, between 20 and 30 LOC.
      int joeFileSize = 20 + random.nextInt(10); 
      int bobFileSize = 20 + random.nextInt(10); 
      simData.addFileMetric(joe, day, joeFile, joeFileSize, day);
      simData.addFileMetric(joe, day, bobFile, bobFileSize, day);
      
      // Complexity is low (between 3 and 6).
      simData.addComplexity(joe, day, bobFile, bobFileSize, day, 3 + random.nextInt(3));
      simData.addComplexity(joe, day, joeFile, joeFileSize, day, 3 + random.nextInt(3));
      
      // Builds, unit tests, commits, churn, code issues are all low.
      simData.addBuilds(joe, day, joeDir, SUCCESS, 1 + random.nextInt(1));
      simData.addBuilds(bob, day, bobDir, SUCCESS, 1 + random.nextInt(1));
      simData.addUnitTests(joe, day, joeFile, PASS, 1 + random.nextInt(1));
      simData.addUnitTests(bob, day, bobFile, PASS, 1 + random.nextInt(1));
      simData.addCommits(joe, day, joeFile, 10 + random.nextInt(10), 1 + random.nextInt(1));  
      simData.addCommits(bob, day, bobFile, 10 + random.nextInt(10), 1 + random.nextInt(1));  
      simData.addCodeIssues(joe, day, joeFile, 1 + random.nextInt(5));
      simData.addCodeIssues(bob, day, bobFile, 1 + random.nextInt(5));
      
      // Coverage is low
      int joeCoverage = 10 + random.nextInt(5);
      int bobCoverage = 10 + random.nextInt(5);
      simData.addCoverage(joe, day, joeFile, joeCoverage, joeFileSize, day);
      simData.addCoverage(joe, day, bobFile, bobCoverage, bobFileSize, day);
      
    }
    
    // Now do last five days.
    for (int i = dayOffset + 5; i < dayOffset + 10; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(projectStart, i);
      this.simData.getLogger().info("SimpleTelemetry: Making data for day: " + day);
      
      // Effort is high, between 8 and 10 hours per day.
      this.simData.addDevEvents(joe, day, (12 * 8) + random.nextInt(12 * 2), joeFile);
      this.simData.addDevEvents(bob, day, (12 * 8) + random.nextInt(12 * 2), bobFile);
      
      // Size increases dramatically, by a total of around 300 or so lines per day.
      int joeFileSize = ((i - dayOffset) * 150) +  random.nextInt(10); 
      int bobFileSize = ((i - dayOffset) * 150) + random.nextInt(10); 
      simData.addFileMetric(joe, day, joeFile, joeFileSize, day);
      simData.addFileMetric(joe, day, bobFile, bobFileSize, day);
      
      // Complexity is high and increasing (10 + day + rand(2))
      simData.addComplexity(joe, day, bobFile, bobFileSize, day, 10 + i + random.nextInt(2));
      simData.addComplexity(joe, day, joeFile, joeFileSize, day, 10 + i + random.nextInt(2));
      
      // Builds and unit tests between 20 and 40 per day
      simData.addBuilds(joe, day, joeDir, SUCCESS, 20 + random.nextInt(20));
      simData.addBuilds(bob, day, bobDir, SUCCESS, 20 + random.nextInt(20));
      simData.addUnitTests(joe, day, joeFile, PASS, 20 + random.nextInt(20));
      simData.addUnitTests(bob, day, bobFile, PASS, 20 + random.nextInt(20));
      
      // Coverage doesn't rise much
      int joeCoverage = 20 + random.nextInt(10);
      int bobCoverage = 20 + random.nextInt(10);
      simData.addCoverage(joe, day, joeFile, joeCoverage, joeFileSize, day);
      simData.addCoverage(joe, day, bobFile, bobCoverage, bobFileSize, day);
      
      // Lots of commits for last five days, with high churn.
      simData.addCommits(joe, day, joeFile, 200 + random.nextInt(10), 5 + random.nextInt(5));  
      simData.addCommits(bob, day, bobFile, 200 + random.nextInt(10), 5 + random.nextInt(5));  
      
      // Code issues increase steadily for last five days.
      simData.addCodeIssues(joe, day, joeFile, ((i - dayOffset) * 10) + random.nextInt(5));
      simData.addCodeIssues(bob, day, bobFile, ((i - dayOffset) * 10) + random.nextInt(5));
    }
  }
  
  /**
   * Sprint 3: High churn, falling coverage, and increasing code issues and complexity 
   * indicate a trouble project.
   * <ul>
   * <li> Effort, size, builds, and unit tests are quite variable.
   * <li> Coverage shows a falling trend.
   * <li> High churn on commits.
   * <li> Code issues steadily rising.
   * <li> Complexity steadily rising.
   * </ul>
   * @throws Exception If problems occur.
   */
  private void makeSprint3() throws Exception {
    // Move forward 20 days to start Sprint 3.
    int dayOffset = 20;
    for (int i = dayOffset + 0; i < dayOffset + 10; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(projectStart, i);
      this.simData.getLogger().info(LOGPREFIX + day);
      
      // Effort varies between 3 and 5 hours (12 * 8) 
      this.simData.addDevEvents(joe, day, 36 + random.nextInt(12 * 2), joeFile);
      this.simData.addDevEvents(bob, day, 36 + random.nextInt(12 * 2), bobFile);
      
      // Size is variable but has slight upward trend.
      int joeFileSize = 200 + ((i - dayOffset) * 50) + random.nextInt(20); 
      int bobFileSize = 200 + ((i - dayOffset) * 50) + random.nextInt(20); 
      simData.addFileMetric(joe, day, joeFile, joeFileSize, day);
      simData.addFileMetric(joe, day, bobFile, bobFileSize, day);
      
      // Complexity is steadily increasing.
      simData.addComplexity(joe, day, bobFile, bobFileSize, day, 5 + i + random.nextInt(2));
      simData.addComplexity(joe, day, joeFile, joeFileSize, day, 5 + i + random.nextInt(2));
      
      // Builds and unit tests are low; 1-3 times a day
      simData.addBuilds(joe, day, joeDir, SUCCESS, 1 + random.nextInt(2));
      simData.addBuilds(bob, day, bobDir, SUCCESS, 1 + random.nextInt(2));
      simData.addUnitTests(joe, day, joeFile, PASS, 1 + random.nextInt(2));
      simData.addUnitTests(bob, day, bobFile, PASS, 1 + random.nextInt(2));
      
      // Coverage starts out about 90%, but falls 10% per day with a little random jiggle.
      int joeCoverage = 90 - ((i - dayOffset) * 10) + random.nextInt(3);
      int bobCoverage = 90 - ((i - dayOffset) * 10) + random.nextInt(3);
      simData.addCoverage(joe, day, joeFile, joeCoverage, joeFileSize, day);
      simData.addCoverage(joe, day, bobFile, bobCoverage, bobFileSize, day);
      
      // Commits are regular and have high churn
      simData.addCommits(joe, day, joeFile, 200 + random.nextInt(10), 2 + random.nextInt(2)); 
      simData.addCommits(bob, day, bobFile, 200 + random.nextInt(10), 2 + random.nextInt(2)); 
      
      // Code Issues steadily rising by about 10 per day per file.
      simData.addCodeIssues(joe, day, joeFile, ((i - dayOffset) * 8)  + random.nextInt(5));
      simData.addCodeIssues(bob, day, bobFile, ((i - dayOffset) * 8) + random.nextInt(5));
      
    }
  }
  
  /**
   * Sprint 4: Resource mismanagement. Bob is idle, Joe is overworked.
   * <ul>
   * <li> Effort, size, builds, and unit tests are quite variable.
   * <li> Coverage shows a falling trend.
   * <li> High churn on commits.
   * </ul>
   * @throws Exception If problems occur.
   */
  private void makeSprint4() throws Exception {
    // Move forward 30 days to start Sprint 4.
    int dayOffset = 30;
    for (int i = dayOffset + 0; i < dayOffset + 10; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(projectStart, i);
      this.simData.getLogger().info(LOGPREFIX + day);
      
      // Joe: Effort varies between 8 and 13 hours 
      this.simData.addDevEvents(joe, day, (12 * 8)  + random.nextInt(12 * 5), joeFile);
      // Bob: Effort varies between 0 and 1 hour.
      this.simData.addDevEvents(bob, day, 1 + random.nextInt(11), bobFile);
      
      // Joe: size is variable, moving upward fast. Bob: not much size increase.
      int joeFileSize = 10 + ((i - dayOffset) * 100) + random.nextInt(20); 
      int bobFileSize = 10 + ((i - dayOffset) * 2) + random.nextInt(2); 
      simData.addFileMetric(joe, day, joeFile, joeFileSize, day);
      simData.addFileMetric(joe, day, bobFile, bobFileSize, day);
      
      // Joe: Complexity is steadily increasing, Bob: complexity is low. 
      simData.addComplexity(joe, day, bobFile, bobFileSize, day, 2 + random.nextInt(2));
      simData.addComplexity(joe, day, joeFile, joeFileSize, day, 5 + i + random.nextInt(2));
        
      // Builds and unit tests between 0-10 times a day.
      simData.addBuilds(joe, day, joeDir, SUCCESS, 5 + random.nextInt(2));
      simData.addBuilds(bob, day, bobDir, SUCCESS, 1 + random.nextInt(1));
      simData.addUnitTests(joe, day, joeFile, PASS, 10 + random.nextInt(2));
      simData.addUnitTests(bob, day, bobFile, PASS, 1 + random.nextInt(1));
      
      // Coverage stays low.
      int joeCoverage = 30 + random.nextInt(10);
      int bobCoverage = 10 + random.nextInt(3);
      simData.addCoverage(joe, day, joeFile, joeCoverage, joeFileSize, day);
      simData.addCoverage(joe, day, bobFile, bobCoverage, bobFileSize, day);
      
      // Bob doesn't even commit, joe has a commit with lots of churn.
      simData.addCommits(joe, day, joeFile, joeFileSize + random.nextInt(20), 
          5 + random.nextInt(5));  
      
      // Code Issues are high and rising.
      simData.addCodeIssues(joe, day, joeFile, 30 + ((i - dayOffset) * 2)  + random.nextInt(2));
    }
  }
}
