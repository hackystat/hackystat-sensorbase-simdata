package org.hackystat.simdata;

import static org.hackystat.telemetry.service.server.ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY;
import static org.hackystat.telemetry.service.server.ServerProperties.SENSORBASE_FULLHOST_KEY;

import org.junit.BeforeClass;

public class SimDataTestHelper {

  /** The Sensorbase server used in these tests. */
  @SuppressWarnings("unused")
  private static org.hackystat.sensorbase.server.Server sensorbaseServer;
  /** The DailyProjectData server used in these tests. */
  @SuppressWarnings("unused")
  private static org.hackystat.dailyprojectdata.server.Server dpdServer;  
  /** The Telemetry server used in these tests. */
  private static org.hackystat.telemetry.service.server.Server telemetryServer;  
  

  /**
   * Constructor.
   */
  public SimDataTestHelper () {
    // Does nothing.
  }
  
  /** 
   * Starts the server going for these tests. 
   * @throws Exception If problems occur setting up the server. 
   */
  @BeforeClass public static void setupServer() throws Exception {
    // Create testing versions of the Sensorbase, DPD, and Telemetry servers.
    SimDataTestHelper.sensorbaseServer = org.hackystat.sensorbase.server.Server.newTestInstance();
    SimDataTestHelper.dpdServer = org.hackystat.dailyprojectdata.server.Server.newTestInstance(); 
    SimDataTestHelper.telemetryServer = 
      org.hackystat.telemetry.service.server.Server.newTestInstance();
  }

  /**
   * Returns the hostname associated with the Telemetry test server. 
   * @return The host name, including the context root. 
   */
  protected String getTelemetryHostName() {
    return SimDataTestHelper.telemetryServer.getHostName();
  }
  
  /**
   * Returns the sensorbase hostname that the Telemetry server communicates with.
   * @return The host name, including the context root. 
   */
  protected String getSensorBaseHostName() {
    return SimDataTestHelper.telemetryServer.getServerProperties().get(SENSORBASE_FULLHOST_KEY);
  }
  
  /**
   * Returns the DPD hostname that the Telemetry server communicates with.
   * @return The host name, including the context root. 
   */
  protected String getDailyProjectDataHostName() {
    return 
    SimDataTestHelper.telemetryServer.getServerProperties().get(DAILYPROJECTDATA_FULLHOST_KEY);
  }
}

