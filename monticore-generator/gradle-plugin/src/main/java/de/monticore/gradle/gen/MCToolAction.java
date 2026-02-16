/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.gen;

import de.monticore.gradle.common.AToolAction;

/**
 * A unit of work, representing a single invocation of the MontiCore Tool.
 */
public abstract class MCToolAction extends AToolAction {

  // Due to using the new ICachedQueueTask API, this class is basically empty

  @Override
  protected void doRun(final String[] args) {
    MCToolInvoker.run(args);
  }

}
