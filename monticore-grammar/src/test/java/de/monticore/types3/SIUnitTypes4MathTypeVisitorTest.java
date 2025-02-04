/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static de.monticore.types3.util.SIUnitIteratorForTests.get2UnitsGroup;
import static de.monticore.types3.util.SIUnitIteratorForTests.getPrefixedUnits;
import static de.monticore.types3.util.SIUnitIteratorForTests.getUnits;

public class SIUnitTypes4MathTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Test
  public void synTFromSIUnitTypes4MathSimpleAllSingleUnits() throws IOException {
    // converting units to SI
    for (String siUnit : (Iterable<String>) getUnits()::iterator) {
      checkTypeRoundTrip("[" + siUnit + "]");
    }
    for (String siUnit : (Iterable<String>) getPrefixedUnits()::iterator) {
      checkTypeRoundTrip("[" + siUnit + "]");
    }
  }

  @Test
  public void synTFromSIUnitTypes4MathAmbiguousUnitsGroups() throws IOException {
    // converting units to SI
    // note: these are not all unambiguous!
    // Case 1: units can be interpreted as prefix
    //         "ms" -> here, we interpret "m" as 'milli', not as 'meter'
    // Case 2: prefixes can be interpreted as unit
    //         "dms" -> here, "m" is interpreted as 'meter', not as 'milli',
    //         that is because "d" has been interpreted as 'deci', not as 'day'
    // Case 3: units can be "fused into" other units
    //         "lm" -> here, "lm" is 'lumen', not 'liter'*'meter'
    //         this can also happen using prefixes.
    //         "Hzm" -> 'hertz'*'meter', not 'henry'*'zeptometer'
    // Case 4: units and prefix can be "split into" other units and prefix
    //         this is the opposite of Case 3.
    //         This should not happen in our implementation

    Iterator<String> groupsWithoutDelimiter = get2UnitsGroup("").iterator();
    Iterator<String> groupsWithDelimiter = get2UnitsGroup("^1").iterator();
    List<String> ambiguous = new ArrayList<>(609);
    while (groupsWithDelimiter.hasNext()) {
      String typeStr = "[" + groupsWithoutDelimiter.next() + "]";
      String expectedType = "[" + groupsWithDelimiter.next() + "]";
      ASTMCType astType = parseMCType(typeStr);
      generateScopes(astType);
      SymTypeExpression type = TypeCheck3.symTypeFromAST(astType);
      assertNoFindings();
      if (!type.printFullName().equals(expectedType)) {
        ambiguous.add(typeStr);
      }
    }
    // I am not 100% certain, whether there are exactly 609 ambiguous cases.
    // But, if the amount decreases, issues have been fixed
    // and this number can be decreased.
    // This number should never increase, as it would indicate a new bug.
    Assertions.assertEquals(609, ambiguous.size());
  }

}
