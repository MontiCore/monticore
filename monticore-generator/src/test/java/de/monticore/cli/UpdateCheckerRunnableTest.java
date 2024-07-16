/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cli;

import de.monticore.cli.updateChecker.HttpGetter;
import de.monticore.cli.updateChecker.UpdateCheckerRunnable;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateCheckerRunnableTest {

  private UpdateCheckerRunnable updateCheckerRunnable;
  private HttpGetter httpGetter;

  private static final String NEW_VERSION_AVAILABLE = "version=100000.0.0";
  private static final String NO_NEW_VERSION_AVAILABLE = "version=0.0.0";
  private static final String NO_RESPONSE = "";

  private static final String NEW_VERSION = "100000.0.0";

  private static final String NEW_VERSION_LOG_MESSAGE =
      "[INFO]   There is a newer Version 100000.0.0 of this tool available at monticore.de/download";
  
  @BeforeEach
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void init() {
    updateCheckerRunnable = new UpdateCheckerRunnable();
    httpGetter = mock(HttpGetter.class);

    updateCheckerRunnable.setHttpGetter(httpGetter);
  }

  @Test
  public void testFindLocalPropertiesFile() {
    Assertions.assertNotNull(updateCheckerRunnable.getLocalVersion());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testNewVersionAvailable() {
    when(httpGetter.getResponse()).thenReturn(NEW_VERSION_AVAILABLE);

    Assertions.assertTrue(updateCheckerRunnable.newVersionAvailable());
    Assertions.assertEquals(NEW_VERSION, updateCheckerRunnable.getNewVersion());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoNewVersionAvailable() {
    when(httpGetter.getResponse()).thenReturn(NO_NEW_VERSION_AVAILABLE);

    Assertions.assertFalse(updateCheckerRunnable.newVersionAvailable());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoResponse() {
    when(httpGetter.getResponse()).thenReturn(NO_RESPONSE);

    Assertions.assertFalse(updateCheckerRunnable.newVersionAvailable());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRun() {
    when(httpGetter.getResponse()).thenReturn(NEW_VERSION_AVAILABLE);

    updateCheckerRunnable.run();

    Assertions.assertEquals(NEW_VERSION, updateCheckerRunnable.getNewVersion());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
