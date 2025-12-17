/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.componentsymbolswithmcbasictypestest.ComponentSymbolsWithMCBasicTypesTestMill;
import de.monticore.types.componentsymbolswithmcbasictypestest._visitor.ComponentSymbolsWithMCBasicTypesTestTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SynthesizeComponentFromMCBasicTypesTest extends AbstractMCTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();

    ComponentSymbolsWithMCBasicTypesTestMill.reset();
    ComponentSymbolsWithMCBasicTypesTestMill.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {"Foo", "qual.Foo"})
  public void shouldHandleMCBasicGenericType(String qualifiedCompName) {
    var globalScope = ComponentSymbolsWithMCBasicTypesTestMill.globalScope();

    ASTMCQualifiedType ast = MCTypeFacade.getInstance().createQualifiedType(qualifiedCompName);
    ast.setEnclosingScope(globalScope);

    ComponentTypeSymbol symbol = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(ast.getMCQualifiedName().getBaseName())
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();

    if (qualifiedCompName.equals("qual.Foo")) {
      var qualScope = ComponentSymbolsWithMCBasicTypesTestMill.scope();
      qualScope.setName("qual");
      qualScope.add(symbol);
      qualScope.addSubScope(symbol.getSpannedScope());
      globalScope.addSubScope(qualScope);
    } else {
      globalScope.add(symbol);
      globalScope.addSubScope(symbol.getSpannedScope());
    }

    CompKindCheckResult result = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth =
      new SynthesizeCompKindFromMCBasicTypes(result);

    // When
    synth.handle(ast);

    // Then
    Assertions.assertTrue(result.getResult().isPresent());
    Assertions.assertTrue(result.getResult().get().isComponentType());
    Assertions.assertEquals(symbol, result.getResult().get().getTypeInfo());
  }

  @Test
  public void shouldLogErrorForDuplicateSymbols() {
    // Given
    ASTMCQualifiedType ast = MCTypeFacade.getInstance().createQualifiedType("Foo");
    ast.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());


    ComponentTypeSymbol compSymbol1 = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(ast.getMCQualifiedName().getBaseName())
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(compSymbol1);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(compSymbol1.getSpannedScope());

    ComponentTypeSymbol compSymbol2 = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(ast.getMCQualifiedName().getBaseName())
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(compSymbol2);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(compSymbol2.getSpannedScope());

    CompKindCheckResult result = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth = new SynthesizeCompKindFromMCBasicTypes(result);

    // When
    synth.handle(ast);

    // Then
    Assertions.assertTrue(result.getResult().isPresent());
    Assertions.assertTrue(result.getResult().get().isComponentType());
    Assertions.assertEquals(compSymbol1, result.getResult().get().getTypeInfo());
    MCAssertions.assertHasFindingStartingWith("0xD0105");
  }

  @ParameterizedTest
  @ValueSource(strings = {"Foo", "qual.Foo"})
  public void shouldNotLogErrorForMissingSymbol(String qualifiedName) {
    // Given
    ASTMCQualifiedType astNormalComp = MCTypeFacade.getInstance().createQualifiedType(qualifiedName);
    astNormalComp.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    CompKindCheckResult result = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth4normal = new SynthesizeCompKindFromMCBasicTypes(result);

    // When
    synth4normal.handle(astNormalComp);

    // Then
    Assertions.assertFalse(result.getResult().isPresent());
  }

  @Test
  public void shouldNotHandleVoidType() {
    // Given
    ASTMCVoidType voidType = ComponentSymbolsWithMCBasicTypesTestMill.mCVoidTypeBuilder().build();
    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth = new SynthesizeCompKindFromMCBasicTypes(resultWrapper);

    // Attach a traverser to the synth, as we do not override the handle method and thus the synth tries to traverse the
    // AST. In the end this should result in an empty synth result, however, if we do not attach a traverser, this will
    // Result in an error instead.
    ComponentSymbolsWithMCBasicTypesTestTraverser traverser = ComponentSymbolsWithMCBasicTypesTestMill.traverser();
    traverser.setMCBasicTypesHandler(synth);

    // When
    synth.handle(voidType);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
  }
}
