/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cli.updateChecker;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdateCheckerRunnableTest {
  
  private static final String NEW_VERSION = "100000.0.0";
  private static final String OLD_VERSION = "1.0.0";
  
  @BeforeEach
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testFindLocalPropertiesFile() {
    UpdateCheckerRunnable runnable = new UpdateCheckerRunnable();
    assertNotNull(runnable.getLocalVersion());
    
    assertEquals(0, LogStub.getPrints().size());
  }
  
  @Test
  public void testCheckVersionLogsWhenNewVersionAvailable() {
    UpdateCheckerRunnable checker = createCheckerWithVersions(OLD_VERSION, NEW_VERSION);
    
    checker.checkVersion();
    
    assertEquals(1, LogStub.getPrints().size());
    assertTrue(LogStub.getPrints().getFirst().startsWith(
        "[INFO]   0xA9001 There is a newer Version 100000.0.0 of this tool available at monticore.de/download"));
  }
  
  @Test
  public void testCheckVersionDoesNotLogWhenNoNewVersionAvailable() {
    UpdateCheckerRunnable checker = createCheckerWithVersions(NEW_VERSION, OLD_VERSION);
    
    checker.checkVersion();
    
    assertEquals(0, LogStub.getPrints().size());
  }
  
  @Test
  public void testCheckVersionReturnsOnMissingLocalVersion() {
    UpdateCheckerRunnable checker = createCheckerWithVersions(null, OLD_VERSION);
    
    checker.checkVersion();
    
    assertEquals(0, LogStub.getPrints().size());
  }
  
  @Test
  public void testCheckVersionReturnsOnMissingRemoteVersion() {
    UpdateCheckerRunnable checker = createCheckerWithVersions(NEW_VERSION, null);
    
    checker.checkVersion();
    
    assertEquals(0, LogStub.getPrints().size());
  }
  
  @Test
  public void testRunDelegatesToCheckVersion() {
    UpdateCheckerRunnable checker = spy(new UpdateCheckerRunnable());
    doNothing().when(checker).checkVersion();
    
    checker.run();
    
    verify(checker).checkVersion();
    assertEquals(0, LogStub.getPrints().size());
  }
  
  @Test
  public void testGetRemotePropertiesParsesVersionFrom2xxResponse() throws Exception {
    HttpClient client = mock(HttpClient.class);
    HttpResponse<String> response = mockStringResponse();
    
    when(response.statusCode()).thenReturn(200);
    when(response.body()).thenReturn("version=" + NEW_VERSION);
    when(client.send(any(HttpRequest.class),
        org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>> any())).thenReturn(
        response);
    
    UpdateCheckerRunnable checker = createCheckerWithClient(client);
    Properties remote = checker.getRemoteProperties();
    
    assertEquals(NEW_VERSION, remote.getProperty("version"));
    assertEquals(0, LogStub.getPrints().size());
  }
  
  @Test
  public void testGetRemotePropertiesWarnsOnNon2xxResponse() throws Exception {
    HttpClient client = mock(HttpClient.class);
    HttpResponse<String> response = mockStringResponse();
    
    when(response.statusCode()).thenReturn(500);
    when(client.send(any(HttpRequest.class),
        ArgumentMatchers.<HttpResponse.BodyHandler<String>> any())).thenReturn(response);
    
    UpdateCheckerRunnable checker = createCheckerWithClient(client);
    Properties remote = checker.getRemoteProperties();
    
    assertTrue(remote.isEmpty());
    assertEquals(1, LogStub.getPrints().size());
    assertTrue(Log.getFindings().getFirst().getMsg()
        .startsWith("0xA9008 Could not get remote properties file, http status: 500"));
  }
  
  @Test
  public void testGetRemotePropertiesWarnsOnIOException() throws Exception {
    HttpClient client = mock(HttpClient.class);
    when(client.send(any(HttpRequest.class),
        ArgumentMatchers.<HttpResponse.BodyHandler<String>> any())).thenThrow(
        new IOException("timeout"));
    
    UpdateCheckerRunnable checker = createCheckerWithClient(client);
    Properties remote = checker.getRemoteProperties();
    
    assertTrue(remote.isEmpty());
    assertEquals(1, LogStub.getPrints().size());
    assertTrue(LogStub.getPrints().getFirst()
        .startsWith("[WARN]  0xA9003 Could not retrieve remote properties file"));
  }
  
  @Test
  public void testGetRemotePropertiesWarnsOnInterruptedException() throws Exception {
    HttpClient client = mock(HttpClient.class);
    when(client.send(any(HttpRequest.class),
        ArgumentMatchers.<HttpResponse.BodyHandler<String>> any())).thenThrow(
        new InterruptedException("interrupted"));
    
    UpdateCheckerRunnable checker = createCheckerWithClient(client);
    Properties remote = checker.getRemoteProperties();
    
    assertTrue(remote.isEmpty());
    assertEquals(1, LogStub.getPrints().size());
    assertTrue(Log.getFindings().getFirst().getMsg()
        .startsWith("0xA9003 Could not retrieve remote properties file"));
  }
  
  @Test
  public void testCheckVersionDoesNotLogUpdateOnEmptyRemoteBody() throws Exception {
    HttpClient client = mock(HttpClient.class);
    HttpResponse<String> response = mockStringResponse();
    
    when(response.statusCode()).thenReturn(200);
    when(response.body()).thenReturn("");
    when(client.send(any(HttpRequest.class),
        ArgumentMatchers.<HttpResponse.BodyHandler<String>> any())).thenReturn(response);
    
    UpdateCheckerRunnable checker = createCheckerWithClient(client);
    checker.checkVersion();
    
    assertEquals(0, LogStub.getPrints().size());
  }
  
  @Test
  public void testVersionComparisonSnapshotRule() {
    UpdateCheckerRunnable.Version release = new UpdateCheckerRunnable.Version("1.2.3");
    UpdateCheckerRunnable.Version snapshot = new UpdateCheckerRunnable.Version("1.2.3-SNAPSHOT");
    
    assertTrue(release.isOlderThan(snapshot));
    assertFalse(snapshot.isOlderThan(release));
    
    assertEquals(0, LogStub.getPrints().size());
  }
  
  @Test
  public void testVersionComparisonMajorMinorPatch() {
    UpdateCheckerRunnable.Version v1 = new UpdateCheckerRunnable.Version("1.2.3");
    UpdateCheckerRunnable.Version v2 = new UpdateCheckerRunnable.Version("1.2.4");
    UpdateCheckerRunnable.Version v3 = new UpdateCheckerRunnable.Version("1.3.0");
    UpdateCheckerRunnable.Version v4 = new UpdateCheckerRunnable.Version("2.0.0");
    
    assertTrue(v1.isOlderThan(v2));
    assertTrue(v2.isOlderThan(v3));
    assertTrue(v3.isOlderThan(v4));
    assertFalse(v4.isOlderThan(v3));
    
    assertEquals(0, LogStub.getPrints().size());
  }
  
  protected UpdateCheckerRunnable createCheckerWithVersions(String localVersion,
      String remoteVersion) {
    return new UpdateCheckerRunnable() {
      
      @Override
      protected Properties getLocalProperties() {
        return createProperties(localVersion);
      }
      
      @Override
      protected Properties getRemoteProperties() {
        return createProperties(remoteVersion);
      }
    };
  }
  
  protected UpdateCheckerRunnable createCheckerWithClient(HttpClient client) {
    return new UpdateCheckerRunnable() {
      
      @Override
      protected HttpClient createHttpClient() {
        return client;
      }
      
      @Override
      protected Properties getLocalProperties() {
        return createProperties(OLD_VERSION);
      }
    };
  }
  
  protected Properties createProperties(String version) {
    Properties properties = new Properties();
    if (version != null) {
      properties.setProperty("version", version);
    }
    return properties;
  }
  
  @SuppressWarnings("unchecked")
  protected HttpResponse<String> mockStringResponse() {
    return (HttpResponse<String>) mock(HttpResponse.class);
  }
}


