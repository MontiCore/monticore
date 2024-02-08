/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.gen;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.internal.isolation.CachedIsolation;
import org.gradle.api.Action;

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

  @Override
  protected void doRun(final String[] args) {
    String logPrefix = "[" + getParameters().getProgressName().get() + "] ";
    isolator.executeInClassloader(MCToolInvoker.class.getName(), "run",
            args, logPrefix, getParameters().getExtraClasspathElements());
  }

}
