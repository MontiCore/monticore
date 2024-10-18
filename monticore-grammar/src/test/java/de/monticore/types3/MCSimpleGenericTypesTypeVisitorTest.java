/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MCSimpleGenericTypesTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @BeforeEach
  public void initFurtherTypes() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    IBasicSymbolsScope javaScope =
        DefsTypesForTests.inScope(gs, DefsTypesForTests.scope("java"));
    IBasicSymbolsScope utilScope =
        DefsTypesForTests.inScope(javaScope, DefsTypesForTests.scope("util"));
    gs.add(DefsTypesForTests.type(
        "Iterator",
        Collections.emptyList(),
        List.of(DefsTypesForTests.typeVariable("T")))
    );
    utilScope.add(DefsTypesForTests.type(
        "Iterator",
        Collections.emptyList(),
        List.of(DefsTypesForTests.typeVariable("T"))
    ));
    gs.add(DefsTypesForTests.type(
        "Collection",
        Collections.emptyList(),
        List.of(DefsTypesForTests.typeVariable("T")))
    );
  }

  @Test
  public void symTypeFromAST_TestSimpleGeneric() throws IOException {
    checkTypeRoundTrip("Iterator<int>");
  }

  @Test
  public void symTypeFromAST_TestSimpleGeneric2() throws IOException {
    checkTypeRoundTrip("Iterator<Person>");
  }

  @Test
  public void symTypeFromAST_TestSimpleGeneric3() throws IOException {
    checkTypeRoundTrip("java.util.Iterator<java.lang.String>");
  }

  @Test
  public void symTypeFromAST_TestSimpleGeneric4() throws IOException {
    checkTypeRoundTrip("Collection<int>");
  }

  @Test
  public void symTypeFromAST_TestSimpleGeneric5() throws IOException {
    checkTypeRoundTrip("java.util.Iterator<Person>");
  }

  @Test
  public void symTypeFromAST_TestSimpleGeneric6() throws IOException {
    checkTypeRoundTrip("Map<Map<java.util.Iterator<Person>,int>,int>");
  }

  @Test
  public void symTypeFromAST_TestSimpleGeneric7() throws IOException {
    checkTypeRoundTrip(
        "java.util.Iterator<"
            + "java.util.Iterator<"
            + "java.util.Iterator<"
            + "int"
            + ">>>"
    );
  }

  @Test
  public void symTypeFromAST_TestSimpleGeneric8() throws IOException {
    checkTypeRoundTrip(
        "java.util.Iterator<List<java.util.Iterator<int>>>"
    );
  }
}
