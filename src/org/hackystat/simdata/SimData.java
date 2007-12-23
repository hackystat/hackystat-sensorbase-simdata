package org.hackystat.simdata;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.client.SensorBaseClient.InvitationReply;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.UriPatterns;
import org.hackystat.utilities.logger.HackystatLogger;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Provides utility functions for all SimData scenarios. 
 * Main program takes a host and populates it with all scenarios.
 * @author Philip Johnson
 */
public class SimData {
  
  /** The host to received this simulated data. */
  private String host;
  
  /** The logger for this simdata run. */
  private Logger logger;
  
  /** The test domain for all users in this simulation. */
  private String testdomain = "@hackystat.org";
  
  /** Maps user names to their associated SensorBaseClients. */
  private Map<String, SensorBaseClient> clients = new HashMap<String, SensorBaseClient>();
  
  /**
   * Creates a SimData instance for the given host.
   * @param host The SensorBase host. 
   * @throws Exception If the host cannot be contacted.
   */
  public SimData(String host) throws Exception {
    this.host = host;
    if (!SensorBaseClient.isHost(this.host)) {
      throw new Exception("Could not contact host: " + host);
    }
    this.logger = HackystatLogger.getLogger("org.hackystat.simdata", "simdata");
    
  }
  
  /**
   * Registers the user with the test domain suffix at the host.
   * @param userName The user name, with the domain.
   * @throws Exception If problems occur. 
   */
  public void makeUser(String userName) throws Exception {
    this.logger.info("Creating user: " + userName);
    String email = userName + testdomain;
    SensorBaseClient.registerUser(this.host, email);
    SensorBaseClient client = new SensorBaseClient(host, email, email);
    client.authenticate();
    clients.put(userName, client);
    // I get timeout errors when registering the second user if I call deleteSensorData.
    //client.deleteSensorData(email); 
  }
  
  /**
   * Create a project that will encapsulate this simulation data. 
   * @param projectName The project name.
   * @param user The owner. 
   * @param start The start day for the project.
   * @param end The end day for the project. 
   * @throws Exception If problems occur. 
   */
  public void makeProject(String projectName, String user, String start, String end) 
  throws Exception {
    this.logger.info("Creating project: " + projectName);
    String email = user + testdomain;
    Project project = new Project();
    project.setName(projectName);
    project.setOwner(email);
    project.setDescription("SimData project");
    project.setStartTime(Tstamp.makeTimestamp(Day.getInstance(start)));
    project.setEndTime(Tstamp.makeTimestamp(Day.getInstance(end)));
    UriPatterns uriPatterns = new UriPatterns();
    uriPatterns.getUriPattern().add("*");
    project.setUriPatterns(uriPatterns);
    this.clients.get(user).putProject(project);
  }
  
  /**
   * Adds newMember to projectName owned by owner. 
   * The newMember is invited by the owner, and then newMember accepts. 
   * @param projectName The name of the project.
   * @param owner The owner of the project (without domain name).
   * @param newMember The member to be added (without domain name). 
   * @throws SensorBaseClientException If problems occur during adding. 
   */
  public void addMember(String projectName, String owner, String newMember) 
  throws SensorBaseClientException {
    String ownerEmail = owner + testdomain;
    String newMemberEmail = newMember + testdomain;
    Project project = clients.get(owner).getProject(ownerEmail, projectName);
    project.getInvitations().getInvitation().add(newMemberEmail);
    clients.get(owner).putProject(project);
    clients.get(newMember).reply(ownerEmail, projectName, InvitationReply.ACCEPT);
  }
  
  
  /**
   * Takes one argument, the SensorBase host, such as "http://localhost:9876/sensorbase".
   * @param args One argument, the SensorBase host.
   * @throws Exception if problems occur.
   */
  public static void main(String[] args) throws Exception {
    SimData simData = new SimData(args[0]);
    simData.makeUser("joe.simdata");
    simData.makeUser("bob.simdata");
    simData.makeProject("FooBar", "joe.simdata", "2007-Jun-01", "2007-Aug-01");
    simData.addMember("FooBar", "joe.simdata", "bob.simdata");
  }

}
