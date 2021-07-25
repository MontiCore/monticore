package de.monticore.cli;

import de.monticore.cli.updateChecker.HttpGetter;
import de.monticore.cli.updateChecker.UpdateCheckerRunnable;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

  @Before
  public void init() {
    Log.init();

    updateCheckerRunnable = new UpdateCheckerRunnable();
    httpGetter = mock(HttpGetter.class);

    updateCheckerRunnable.setHttpGetter(httpGetter);
  }


  @Test
  public void testNewVersionAvailable() {
    when(httpGetter.getResponse()).thenReturn(NEW_VERSION_AVAILABLE);

    Assert.assertTrue(updateCheckerRunnable.newVersionAvailable());
    Assert.assertEquals(NEW_VERSION, updateCheckerRunnable.getNewVersion());
  }

  @Test
  public void testNoNewVersionAvailable() {
    when(httpGetter.getResponse()).thenReturn(NO_NEW_VERSION_AVAILABLE);

    Assert.assertFalse(updateCheckerRunnable.newVersionAvailable());
  }

  @Test
  public void testNoResponse() {
    when(httpGetter.getResponse()).thenReturn(NO_RESPONSE);

    Assert.assertFalse(updateCheckerRunnable.newVersionAvailable());
  }

  @Test
  public void testRun() {
    when(httpGetter.getResponse()).thenReturn(NEW_VERSION_AVAILABLE);

    updateCheckerRunnable.run();

    Assert.assertEquals(NEW_VERSION, updateCheckerRunnable.getNewVersion());
  }
}
