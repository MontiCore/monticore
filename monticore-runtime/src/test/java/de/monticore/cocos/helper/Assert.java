/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
