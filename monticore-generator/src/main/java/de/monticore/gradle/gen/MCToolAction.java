/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.gen;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.internal.isolation.CachedIsolation;
import org.gradle.api.Action;

import java.util.concurrent.Semaphore;

/**
 * A unit of work, representing a single invocation of the MontiCore Tool,
 * which is
 */
public abstract class MCToolAction extends AToolAction {

  /**
   * A cached isolation which enables us to classloader-isolate individual
   * runs of the MontiCore tool against each other.
   * Similar to {@link org.gradle.workers.WorkerExecutor#classLoaderIsolation(Action)},
   * but may re-use loaded classes for sequential work.
   * Shared between all MCToolActions.
   */
  protected static final CachedIsolation.WithClassPath isolator = new CachedIsolation.WithClassPath();

  protected static int oldMaxParallelMC = guessInitialMaxParallelMC();
  /**
   * Unfortunately, Gradle does not allow us to limit the maximum work-actions of a WorkQueue being performed
   * (without limit the max-worker for the entire project)
   */
  protected static final Semaphore semaphore = new Semaphore(oldMaxParallelMC);

  public static synchronized void setMaxConcurrentMC(int maxParallelMC) {
    if (oldMaxParallelMC < maxParallelMC) {
      // The limit has been increased -> release/add some permits
      semaphore.release(maxParallelMC - oldMaxParallelMC);
      oldMaxParallelMC = maxParallelMC;
    } else if (oldMaxParallelMC > maxParallelMC) {
      // The max amount has been lowered -> acquire/remove some permits
      semaphore.acquireUninterruptibly(oldMaxParallelMC - maxParallelMC);
      oldMaxParallelMC = maxParallelMC;
    }
  }

  public static int getMaxConcurrentMC() {
    return oldMaxParallelMC;
  }

  protected static int guessInitialMaxParallelMC() {
    // We generously estimate 500MB of memory usage per grammar
    long leftOverMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory();
    int maxParallel = (int) Math.max(4, leftOverMemory / 500000000d);
    System.err.println("Guessing an initial of " + maxParallel);
    System.err.println("  " + Runtime.getRuntime().freeMemory());
    System.err.println("  " + Runtime.getRuntime().maxMemory());
    System.err.println("  " + Runtime.getRuntime().totalMemory());
    System.err.println("  " + leftOverMemory / 500000000d);
    return maxParallel;
  }

  @Override
  protected void doRun(final String[] args) {
    try {
      // In case we run into the limit of maximum concurrent MontiCore Generation actions,
      // we wait until another generation has concluded
      semaphore.acquire();
    } catch (InterruptedException e) {
      // Unable to acquire slot to run -> abort
      throw new RuntimeException(e);
    }
    try {
      String logPrefix = "[" + getParameters().getProgressName().get() + "] ";
      isolator.executeInClassloader(MCToolInvoker.class.getName(), "run",
                                    args, logPrefix, getParameters().getExtraClasspathElements());
    } finally {
      semaphore.release();
    }

  }

}
