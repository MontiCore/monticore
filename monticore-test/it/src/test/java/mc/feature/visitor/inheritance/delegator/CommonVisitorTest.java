/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import de.monticore.runtime.junit.AbstractMCTest;

/**
 * Common infrastructure for visitor tests.
 *
 */
public class CommonVisitorTest extends AbstractMCTest {
  // run traces the actual visiting which later is assert to match the
  // expectations.
  protected StringBuilder run = new StringBuilder();
  
  protected StringBuilder expectedRun = new StringBuilder();
  
}
