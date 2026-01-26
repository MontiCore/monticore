// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.SymTypeExpressionGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SymTypeExpressionComparatorTest extends AbstractMCTest {

  @Test
  public void symTypeExpressionComparatorMonkeyTest1() {
    // for Primitives
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();

    SymTypeExpressionGenerator symTypeExpressionGenerator =
        new SymTypeExpressionGenerator();
    List<SymTypeExpression> randomTypes = symTypeExpressionGenerator
        .createSymTypeExpressions(10000, 5, 5, true);
    assertNoFindings();

    // remove all duplicates
    // since we test the comparator, we cannot use sorting here,
    // thus, keep amount/complexity low
    List<SymTypeExpression> uniqueTypes = new ArrayList<>(randomTypes.size());
    for (SymTypeExpression t : randomTypes) {
      if (uniqueTypes.stream().noneMatch(uT -> uT.deepEquals(t))) {
        uniqueTypes.add(t);
      }
    }
    assertNoFindings();

    // sort into set using the comparator
    Set<SymTypeExpression> uniqueTypesSorted = new TreeSet<>(uniqueTypes);
    assertNoFindings();

    // check explicitly with ==
    Set<SymTypeExpression> vanished = uniqueTypesSorted.stream()
        .filter(uT -> uniqueTypes.stream().noneMatch(uT2 -> uT == uT2))
        .collect(Collectors.toSet());

    assertEquals(Collections.emptySet(), vanished);
  }

}
