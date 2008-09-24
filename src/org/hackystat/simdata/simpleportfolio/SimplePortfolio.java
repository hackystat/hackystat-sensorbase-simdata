package org.hackystat.simdata.simpleportfolio;

import java.util.Random;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.simdata.SimData;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Illustrates simple use of Telemetry to understand development. 
 * 
 * @author Shaoxuan Zhang
 */
public class SimplePortfolio {
  /** The SimData instance for generate and send data. */
  private final SimData simData;
  // Package private to support testing.
  /** The user name. */
  static final String joe = "joe.simpleportfolio";
  /** The project name for good portfolio. */
  static final String project1 = "GoodProject";
  /** The project name for bad portfolio. */
  static final String project2 = "TroubledProject";
  /** The project name for unstable portfolio. */
  static final String project3 = "UnstableProject";
  
  /** The period these data will cover. */
  static final int dataPeriod = 35;
  
  /** Start date of the projects. */
  private final XMLGregorianCalendar projectStart = Tstamp.makeTimestamp("2008-06-01");
  /** Start date of the software development progress data. */
  private final XMLGregorianCalendar dataStart = Tstamp.makeTimestamp("2008-07-01");
  /** End date of the projects. */
  private final XMLGregorianCalendar projectEnd = Tstamp.makeTimestamp("2008-08-08");
  /** A random number generator. */
  private final Random random = new Random(0);
  
  /** String of "Success". */
  private static final String SUCCESS = "Success";
  /** String of "pass". */
  private static final String PASS = "pass";
  /** Prefix for logging. */
  private static final String LOGPREFIX = "SimplePortfolio: Making data for day: ";

  /**
   * Runs the SimplePortfolio scenerio.  Creates the two users, the project, and sends the data.
   * @param host The SensorBase host that will receive the data. 
   * @throws Exception If problems occur.
   */
  public SimplePortfolio(String host) throws Exception {
    simData = new SimData(host);
    simData.getLogger().info("Clearing up data");
    simData.clearData(host, joe);
    simData.getLogger().info("Initializing SimplePortfolio scenario");
    simData.makeUser(joe);
    simData.makeProject(project1, joe, projectStart, projectEnd, getProjectUriPattern(project1));
    simData.makeProject(project2, joe, projectStart, projectEnd, getProjectUriPattern(project2));
    simData.makeProject(project3, joe, projectStart, projectEnd, getProjectUriPattern(project3));
    
    this.makeSprint1();
    this.makeSprint2();
    this.makeSprint3();
    
    // Make sure all remaining data is sent. 
    simData.quitShells();
  }
  
  /**
   * Return the URI pattern of the given project.
   * @param project the project
   * @return the URI pattern
   */
  private String getProjectUriPattern(String project) {
    return "*/" + project + "/*";
  }

  /**
   * Return the directory of the given user in the given project.
   * @param project the project
   * @param user the user
   * @return the directory
   */
  private String getDir(String project, String user) {
    return "/users/" + user + "/" + project + "/";
  }

  /**
   * Return the file path of the given user in the given project.
   * @param project the project
   * @param user the user
   * @return the file path
   */
  private String getFilePath(String project, String user) {
    return getDir(project, user) + user + ".java";
  }
  
  /**
   * Sprint 1: Illustrates "healthy" process and product metrics. 
   * <ul>
   * <li> Effort is constant, between three and four hours a day.
   * <li> Size increases steadily, starting at about 300 and increasing by 30 or so lines per day.
   * <li> Builds and unit tests between 2-6 times a day.
   * <li> Coverage is always at least 80%.
   * <li> Average complexity is low and stable.
   * <li> CodeIssues is stable and low, 2 per file.
   * <li> They each commit once a day, with relatively low churn (less than 20%).
   * </ul>
   * @throws Exception If problems occur.
   */
  private void makeSprint1() throws Exception {
    int joeFileSize = 300; 
    int joeCoverage = 80;
    for (int i = 0; i < dataPeriod; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(dataStart, i);
      simData.getLogger().info(LOGPREFIX + day);

      // Effort is constant, between three and four hours a day.
      simData.addDevEvents(joe, day, (12 * 3) + random.nextInt(12), getFilePath(project1, joe));
      
      // Size increases steadily, starting at 300 and increasing by 25~40 LOC per day.
      joeFileSize += 25 + random.nextInt(15);
      simData.addFileMetric(joe, day, getFilePath(project1, joe), joeFileSize, day);
      
      // Complexity is low and stable.
      simData.addComplexity(joe, day, getFilePath(project1, joe), joeFileSize, day, 4 - i / 15);

      // Coupling is low and stable.
      simData.addCoupling(joe, day, getFilePath(project1, joe), 6 - i / 20);
      
      // Builds and unit tests between 3-5 times a day.
      simData.addBuilds(joe, day, getDir(project1, joe), SUCCESS, 3 + random.nextInt(2));
      simData.addUnitTests(joe, day, getFilePath(project1, joe), PASS, 2 + random.nextInt(3));
      
      // Coverage is always at least 80%, increasing slowly.
      if (i % 4 == 0) {
        joeCoverage += random.nextInt(5);
      }
      if (joeCoverage > 95) {
        joeCoverage = 95;
      }
      simData.addCoverage(joe, day, getFilePath(project1, joe), joeCoverage, joeFileSize,  day);
      
      // Joe commits twice a day, and Bob commits once, with relatively low churn (50-60 LOC).
      simData.addCommit(joe, day, getFilePath(project1, joe), 23 + random.nextInt(5));
      simData.addCommit(joe, day, getFilePath(project1, joe), 20 + random.nextInt(10));
      
      // Code issues are low and stable, 2 per file.
      simData.addCodeIssues(joe, day, getFilePath(project1, joe), 3 - i / 20);
    }
  }

  /**
   * Sprint 2: Illustrates "bad" process and product metrics. 
   * <ul>
   * <li> Effort, size, builds, and unit tests are quite variable.
   * <li> Coverage shows a falling trend.
   * <li> High churn on commits.
   * <li> Code issues steadily rising.
   * <li> Complexity steadily rising.
   * </ul>
   * @throws Exception If problems occur.
   */
  private void makeSprint2() throws Exception {
    int joeCoverage = 95;
    int codeIssue = 2;
    int coupling = 5;
    for (int i = 0; i < dataPeriod; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(dataStart, i);
      simData.getLogger().info(LOGPREFIX + day);

      // Effort varies between 2 and 9 hours 
      simData.addDevEvents(joe, day, (12 * 2) + random.nextInt(12 * 7), getFilePath(project2, joe));
      
      // Size is quite variable
      int joeFileSize = 300 + i * 34 + random.nextInt((i + 1) * 26);
      simData.addFileMetric(joe, day, getFilePath(project2, joe), joeFileSize, day);
      
      // Complexity steadily rising.
      simData.addComplexity(joe, day, getFilePath(project2, joe), joeFileSize, day, 3 + i / 4);
      
      // Builds and unit tests between 1-4 times a day.
      simData.addBuilds(joe, day, getDir(project2, joe), SUCCESS, 1 + random.nextInt(3));
      simData.addUnitTests(joe, day, getFilePath(project2, joe), PASS, 1 + random.nextInt(2));
      
      // Coverage shows a falling trend.
      if (i % 7 == 0) {
        joeCoverage -= 3;
      }
      simData.addCoverage(joe, day, getFilePath(project2, joe), joeCoverage, joeFileSize,  day);

      // Coupling increasing. 
      if (i % (random.nextInt(4) + 2) == 0) {
        coupling += random.nextInt(5);
      }
      simData.addCoupling(joe, day, getFilePath(project2, joe), coupling);
      
      // Commits are irregular and have high churn.
      simData.addCommits(joe, day, getFilePath(project2, joe), 200 + random.nextInt(100), 
          1 + random.nextInt(5)); 

      // Code issues steadily rising.
      codeIssue += random.nextInt(3);
      simData.addCodeIssues(joe, day, getFilePath(project2, joe), codeIssue);
    }
  }

  /**
   * Sprint 3: Illustrates "unstable" process and product metrics. 
   * <ul>
   * <li> Effort is constant, between three and four hours a day.
   * <li> Size increases unsteadily.
   * <li> Churn, builds, and unit tests are quite variable.
   * <li> Coverage varies between 60% to 80%.
   * <li> Average complexity is low but unstable.
   * <li> CodeIssues is low but unstable.
   * </ul>
   * @throws Exception If problems occur.
   */
  private void makeSprint3() throws Exception {
    int joeFileSize = 300;
    int joeCoverage = 55;
    for (int i = 0; i < dataPeriod; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(dataStart, i);
      simData.getLogger().info(LOGPREFIX + day);

      // Effort is constant, between one and five hours a day.
      simData.addDevEvents(joe, day, 12 + random.nextInt(12 * 5), getFilePath(project3, joe));
      
      // Size increases unsteadily, starting at 300. Increase varies over days.
      joeFileSize += (i % 15) * (i % 15);
      simData.addFileMetric(joe, day, getFilePath(project3, joe), joeFileSize, day);
      
      // Complexity is low but unstable, variable between 3-10
      simData.addComplexity(joe, day, getFilePath(project3, joe), joeFileSize, day, 
                            3 + i / 10 + i % (random.nextInt(9) + 1));
      
      // Builds and unit tests between 1-10 times a day.
      simData.addBuilds(joe, day, getDir(project3, joe), SUCCESS, 1 + random.nextInt(9));
      simData.addUnitTests(joe, day, getFilePath(project3, joe), PASS, 1 + random.nextInt(9));
      
      // Coverage varies between 60% to 80%.
      /*
      joeCoverage += random.nextInt(13);
      if (joeCoverage > 88) {
        joeCoverage -= 20 + random.nextInt(10);
      }
      */
      // Coverage shows a increasing trend.
      if (i % 7 == 0) {
        joeCoverage += 5;
      }
      simData.addCoverage(joe, day, getFilePath(project3, joe), joeCoverage, joeFileSize,  day);
      
      // Coupling varies between 5-15. 
      simData.addCoupling(joe, day, getFilePath(project3, joe), 5 + random.nextInt(i % 9 + 2));
      
      // Commits 2-5 a day, with variable churn (20-200 LOC).
      simData.addCommits(joe, day, getFilePath(project3, joe), 20 + random.nextInt(200), 
          2 + random.nextInt(3)); 
      
      // Code issues is low but unstable. between 2 to 7
      simData.addCodeIssues(joe, day, getFilePath(project3, joe), 2 + random.nextInt(5));
    }
  }
  
}
