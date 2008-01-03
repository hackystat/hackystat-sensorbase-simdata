package org.hackystat.simdata.simpletelemetry;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.build.jaxb.BuildDailyProjectData;
import org.hackystat.dailyprojectdata.resource.commit.jaxb.CommitDailyProjectData;
import org.hackystat.dailyprojectdata.resource.coverage.jaxb.CoverageDailyProjectData;
import org.hackystat.dailyprojectdata.resource.devtime.jaxb.DevTimeDailyProjectData;
import org.hackystat.dailyprojectdata.resource.devtime.jaxb.MemberData;
import org.hackystat.dailyprojectdata.resource.filemetric.jaxb.FileMetricDailyProjectData;
import org.hackystat.dailyprojectdata.resource.unittest.jaxb.UnitTestDailyProjectData;
import org.hackystat.simdata.SimData;
import org.hackystat.simdata.SimDataTestHelper;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartData;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the SimpleTelemetry scenario by retrieving DPDs for the first day and ensuring their
 * values are correct.
 * @author Philip Johnson
 */
public class TestSimpleTelemetry extends SimDataTestHelper {
  
  private boolean invokedSimpleTelemetry = false;
  
  private String dpdHost = this.getDailyProjectDataHostName();
  private String telemetryHost = this.getTelemetryHostName();
  private String joe = SimpleTelemetry.joe + SimData.getTestDomain();
  private String bob = SimpleTelemetry.bob + SimData.getTestDomain();
  private String project = SimpleTelemetry.project;
  private XMLGregorianCalendar day1;
  private DailyProjectDataClient dpdClient;
  private TelemetryClient telemetryClient;

  /**
   * Runs the SimpleTelemetry scenario to set up the data on the SensorBase.
   * You can disable the data sending by setting the System property 
   * "org.hackystat.simdata.TestSimpleTelemetry.sendData" to "false", which
   * speeds up testing when you've already sent the data to your test sensorbase.
   * Can't use @BeforeClass since we can't get the sensorbase hostname.
   * @throws Exception If problems occur. 
   */
  @Before
  public void setupData() throws Exception {
    String sendData = System.getProperty("org.hackystat.simdata.TestSimpleTelemetry.sendData");
    // Only set up data if not disabled and we haven't done it previously.
    if (!"false".equals(sendData) && (!invokedSimpleTelemetry)) { 
      invokedSimpleTelemetry = true;
      new SimpleTelemetry(this.getSensorBaseHostName());
    }
    day1 = Tstamp.makeTimestamp(SimpleTelemetry.startString);
    dpdClient = new DailyProjectDataClient(dpdHost, joe, joe);
    telemetryClient = new TelemetryClient(telemetryHost, joe, joe);
  }
  
  /**
   * Tests the DevTime.
   * @throws Exception If problems occur. 
   */
  @Test public void testDPDDevTime() throws Exception {
    // Check DevTime for Day 1.
    DevTimeDailyProjectData devTime = dpdClient.getDevTime(joe, project, day1);
    BigInteger joeDevTime = null;
    BigInteger bobDevTime = null;
    // Find Joe's devTime.
    for (MemberData data : devTime.getMemberData()) {
      if (data.getMemberUri().contains(joe)) {
        joeDevTime = data.getDevTime();
      }
      if (data.getMemberUri().contains(bob)) {
        bobDevTime = data.getDevTime();
      }
    }
    assertEquals("Checking Joe DevTime", 180, joeDevTime.intValue());
    assertEquals("Checking Bob DevTime", 200, bobDevTime.intValue());
  }
    
  /**
   * Tests the Build data.
   * @throws Exception If problems occur. 
   */
  @Test public void testDPDBuild() throws Exception {
    // Check builds for day 1.
    BuildDailyProjectData builds = dpdClient.getBuild(joe, project, day1);
    int joeBuilds = 0;
    int bobBuilds = 0;
    for (org.hackystat.dailyprojectdata.resource.build.jaxb.MemberData memberData : 
      builds.getMemberData()) {
      if (memberData.getMemberUri().contains(joe)) {
        joeBuilds = memberData.getSuccess() + memberData.getFailure();
      }
      if (memberData.getMemberUri().contains(bob)) {
        bobBuilds = memberData.getSuccess() + memberData.getFailure();
      }
    }
    assertEquals("Checking Joe Builds", 2, joeBuilds);
    assertEquals("Checking Bob Builds", 5, bobBuilds);
  }
  
  
  /**
   * Tests the Build data.
   * @throws Exception If problems occur. 
   */
  @Test public void testDPDFileMetric() throws Exception {
    // Check size for day 1.
    FileMetricDailyProjectData fileMetric = 
      dpdClient.getFileMetric(joe, project, day1, "TotalLines");
    assertEquals("Checking totalSize", 216, fileMetric.getTotal(), 0.1);
  }
  
  /**
   * Tests the UnitTest data.
   * @throws Exception If problems occur. 
   */
  @Test public void testDPDUnitTest() throws Exception {

    // Check Unit Tests for day 1.
    UnitTestDailyProjectData tests = dpdClient.getUnitTest(joe, project, day1);
    int joeTests = 0;
    int bobTests = 0;
    for (org.hackystat.dailyprojectdata.resource.unittest.jaxb.MemberData memberData : 
      tests.getMemberData()) {
      if (memberData.getMemberUri().contains(joe)) {
        joeTests = memberData.getSuccess().intValue() + memberData.getFailure().intValue();
      }
      if (memberData.getMemberUri().contains(bob)) {
        bobTests = memberData.getSuccess().intValue() + memberData.getFailure().intValue();
      }
    }
    assertEquals("Checking Joe Tests", 3, joeTests);
    assertEquals("Checking Bob Tests", 3, bobTests);
  }
  
  /**
   * Tests the Commit data.
   * @throws Exception If problems occur. 
   */
  @Test public void testDPDCommit() throws Exception {
    
    // Check commits for day 1.
    CommitDailyProjectData commits = dpdClient.getCommit(joe, project, day1);
    int joeChurn = 0;
    int bobChurn = 0;
    for (org.hackystat.dailyprojectdata.resource.commit.jaxb.MemberData memberData : 
      commits.getMemberData()) {
      if (memberData.getMemberUri().contains(joe)) {
        joeChurn = 
          memberData.getLinesAdded() +  memberData.getLinesDeleted();
      }
      if (memberData.getMemberUri().contains(bob)) {
        bobChurn = 
          memberData.getLinesAdded() + memberData.getLinesDeleted();
      }
    }
    assertEquals("Checking Joe Churn", 9, joeChurn);
    assertEquals("Checking Bob Churn", 4, bobChurn);
  }
  
  /**
   * Tests the Coverage data.
   * @throws Exception If problems occur. 
   */
  @Test public void testDPDCoverage() throws Exception {
    // Check Coverage for day 1.
    CoverageDailyProjectData coverage = dpdClient.getCoverage(joe, project, day1, "line");
    for (org.hackystat.dailyprojectdata.resource.coverage.jaxb.ConstructData data : 
      coverage.getConstructData()) {
      if (data.getName().contains("Joe.java")) {
        assertEquals("Checking Joe.java coverage", 90, data.getNumCovered().intValue());
      }
      if (data.getName().contains("Bob.java")) {
        assertEquals("Checking Bob.java coverage", 93, data.getNumCovered().intValue());
      }
    }
  }
  
  /**
   * Tests the Telemetry streams associated with SimpleTelemetry.
   * @throws Exception If problems occur. 
   */
  @Test public void testTelemetryProductTrends() throws Exception {
    String chartName = "ProductTrends";
    String params = "";
    Date startTelemetry = new Date();
    System.out.println("Starting ProductTrends telemetry generation at: " + startTelemetry);
    TelemetryChartData chart = telemetryClient.getChart(chartName, joe, project, "Day", 
          day1, Tstamp.incrementDays(day1, 4), params);
    // See if this chart contains 1 stream.
    List<TelemetryStream> streams = chart.getTelemetryStream();
    Date endTelemetry = new Date();
    System.out.println("Finished ProductTrends telemetry in " + 
        (endTelemetry.getTime() - startTelemetry.getTime()) + " milliseconds"); 
    assertEquals("Checking for 7 streams returned", 7, streams.size());
  }
  
  /**
   * Tests the Telemetry streams associated with SimpleTelemetry.
   * @throws Exception If problems occur. 
   */
  @Test public void testTelemetryMemberTrends() throws Exception {
    String chartName = "MemberTrends";
    String params = SimpleTelemetry.joe;
    Date startTelemetry = new Date();
    System.out.println("Starting MemberTrends telemetry generation at: " + startTelemetry);
    TelemetryChartData chart = telemetryClient.getChart(chartName, joe, project, "Day", 
          day1, Tstamp.incrementDays(day1, 4), params);
    // See if this chart contains 1 stream.
    List<TelemetryStream> streams = chart.getTelemetryStream();
    Date endTelemetry = new Date();
    System.out.println("Finished MemberTrends telemetry in " + 
        (endTelemetry.getTime() - startTelemetry.getTime()) + " milliseconds"); 
    assertEquals("Checking for 5 streams returned", 5, streams.size());
  }
  
  
}
