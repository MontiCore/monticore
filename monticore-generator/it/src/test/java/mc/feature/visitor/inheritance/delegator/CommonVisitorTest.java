/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.GeneratorIntegrationsTest;

/**
 * Common infrastructure for visitor tests.
 *
 * @author Robert Heim
 */
public class CommonVisitorTest extends GeneratorIntegrationsTest {
  // run traces the actual visiting which later is assert to match the
  // expectations.
  protected StringBuilder run = new StringBuilder();
  
  protected StringBuilder expectedRun = new StringBuilder();
  
}
