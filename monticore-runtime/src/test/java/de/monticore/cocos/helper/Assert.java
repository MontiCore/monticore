/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cocos.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import com.google.common.base.Joiner;

import de.se_rwth.commons.logging.Finding;

/**
 * Helper for testing CoCos.
 *
 * @author Robert Heim
 */
public class Assert {
  
  /**
   * Asserts that each of the expectedErrors is found at least once in the
   * actualErrors.
   * 
   * @param expectedErrors
   * @param actualErrors
   */
  public static void assertErrors(Collection<Finding> expectedErrors,
      Collection<Finding> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    for (Finding expectedError : expectedErrors) {
      boolean found = actualErrors.stream()
          .filter(s -> s.buildMsg().contains(expectedError.buildMsg())).count() >= 1;
      assertTrue("The following expected error was not found: " + expectedError
          + actualErrorsJoined, found);
    }
  }
  
  /**
   * Asserts that each of the messages of expectedErrors is found at least once
   * in any of the actualErrors. The check omits other fields of the errors.
   * 
   * @param expectedErrors
   * @param actualErrors
   */
  public static void assertErrorMsg(Collection<Finding> expectedErrors,
      Collection<Finding> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    for (Finding expectedError : expectedErrors) {
      
      boolean found = actualErrors.stream().filter(
          f -> f.getMsg().equals(expectedError.getMsg())
          ).count() >= 1;
      assertTrue("The following expected error was not found: " + expectedError
          + actualErrorsJoined, found);
    }
  }
  
  /**
   * Asserts that there are exactly as many actual errors as expected.
   * 
   * @param expectedErrors
   * @param actualErrors
   */
  public static void assertEqualErrorCounts(Collection<Finding> expectedErrors,
      Collection<Finding> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    String expectedErrorsJoined = "\nexpected Errors: \n\t"
        + Joiner.on("\n\t").join(expectedErrors);
    assertEquals(
        "Expected " + expectedErrors.size() + " errors, but found " + actualErrors.size()
            + "." + expectedErrorsJoined + actualErrorsJoined, expectedErrors.size(),
        actualErrors.size());
    
  }
}
