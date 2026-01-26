/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.componentsymbolswithmcbasictypestest.ComponentSymbolsWithMCBasicTypesTestMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FullSynthesizeCompKindFromMCBasicTypesTest extends AbstractMCTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();

    ComponentSymbolsWithMCBasicTypesTestMill.reset();
    ComponentSymbolsWithMCBasicTypesTestMill.init();
  }

  @Test
  public void synthesizesCompKind_forResolvableComponentTypeSymbol() {
    // Given
    ComponentTypeSymbol typeA = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(typeA);
    typeA.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    ASTMCType ast = MCTypeFacade.getInstance().createQualifiedType("A");
    ast.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    FullSynthesizeCompKindFromMCBasicTypes synth = new FullSynthesizeCompKindFromMCBasicTypes();

    // When
    Optional<CompKindExpression> res = synth.synthesize(ast);

    // Then
    assertTrue(res.isPresent());
    assertTrue(res.get().isComponentType());
    assertEquals(typeA, res.get().getTypeInfo());
  }

  @Test
  public void shouldLogErrorOnPrimitive() {
    // Given
    ASTMCType ast = MCTypeFacade.getInstance().createIntType();
    FullSynthesizeCompKindFromMCBasicTypes synth = new FullSynthesizeCompKindFromMCBasicTypes();

    // When
    Optional<CompKindExpression> result = synth.synthesize(ast);

    // Then
    assertTrue(result.isEmpty(), "Expected no CompKindExpression for primitive 'int'");
    MCAssertions.assertHasFindingStartingWith("0xD0104 Cannot resolve component 'int'");
  }
}
