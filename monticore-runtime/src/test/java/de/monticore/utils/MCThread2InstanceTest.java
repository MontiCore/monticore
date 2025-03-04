// (c) https://github.com/MontiCore/monticore
package de.monticore.utils;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MCThread2InstanceTest {

  @BeforeEach
  public void setupLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testDefaultConstructor() {
    MCThread2Instance<Box> mcThreadLocal = new MCThread2Instance<>();
    assertNull(mcThreadLocal.get());
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x72100"));
    runInThread(() -> assertNull(mcThreadLocal.get()));
    // deliberately not checking Log again
  }

  @Test
  public void testConstructorWithInitialValue() {
    Box box = new Box(1);
    MCThread2Instance<Box> mcThreadLocal = new MCThread2Instance<>(box);
    assertEquals(1, mcThreadLocal.get().value);
    runInThread(() -> assertEquals(1, mcThreadLocal.get().value));
    mcThreadLocal.get().value = 2;
    assertEquals(2, mcThreadLocal.get().value);
    runInThread(() -> assertEquals(2, mcThreadLocal.get().value));
    assertNoFindings();
  }

  @Test
  public void testConstructorWithSupplier() {
    AtomicInteger i = new AtomicInteger(0);
    MCThread2Instance<Box> mcThreadLocal = new MCThread2Instance<>(
        () -> new Box(i.getAndIncrement())
    );
    assertEquals(0, mcThreadLocal.get().value);
    runInThread(() -> assertEquals(1, mcThreadLocal.get().value));
    runInThread(() -> assertEquals(2, mcThreadLocal.get().value));
  }

  @Test
  public void testSet() {
    MCThread2Instance<Box> mcThreadLocal = new MCThread2Instance<>();
    Box box = new Box(4);
    mcThreadLocal.set(box);
    assertEquals(4, mcThreadLocal.get().value);
    runInThread(() -> {
      mcThreadLocal.set(new Box(5));
    });
    runInThread(() -> {
      assertNull(mcThreadLocal.get());
      assertEquals(1, Log.getErrorCount());
    });
    assertEquals(4, mcThreadLocal.get().value);
  }

  // internals

  protected void runInThread(Runnable task) {
    Thread thread = new Thread(task);
    thread.start();
    try {
      thread.join();
    }
    catch (InterruptedException e) {
      fail(e);
    }
  }

  protected static void assertNoFindings() {
    assertTrue(Log.getFindings().isEmpty(),
        "Expected no Log findings, but got:"
            + System.lineSeparator() + getAllFindingsAsString()
    );
  }

  protected static String getAllFindingsAsString() {
    return Log.getFindings().stream()
        .map(Finding::buildMsg)
        .collect(Collectors.joining(System.lineSeparator()));
  }

  protected static class Box {
    public int value;

    public Box(int value) {
      this.value = value;
    }
  }

}
