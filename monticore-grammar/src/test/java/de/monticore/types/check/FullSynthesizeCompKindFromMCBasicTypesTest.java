/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.componentsymbolswithmcbasictypestest.ComponentSymbolsWithMCBasicTypesTestMill;
import de.monticore.types.mccomponenttypes._ast.ASTMCComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCGenericComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCQualifiedComponentType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
  public void synthesizesCompKind_forResolvableComponentTypeSymbol() throws IOException {
    // Given
    ComponentTypeSymbol typeA = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(typeA);
    typeA.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    Optional<ASTMCComponentType> parsed = ComponentSymbolsWithMCBasicTypesTestMill
        .parser()
        .parse_StringMCComponentType("A");
    assertTrue(parsed.isPresent());
    ASTMCComponentType ast = parsed.get();
    ast.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    FullSynthesizeCompKindFromMCComponentTypes synth =
        new FullSynthesizeCompKindFromMCComponentTypes();

    // When
    Optional<CompKindExpression> res = synth.synthesize(ast);

    // Then
    assertTrue(res.isPresent());
    assertTrue(res.get().isComponentType());
    assertEquals(typeA, res.get().getTypeInfo());
  }

  @Test
  public void parsesOnlyQualifiedAndGenericComponentTypes() throws IOException {
    Optional<ASTMCComponentType> qualified = ComponentSymbolsWithMCBasicTypesTestMill
        .parser()
        .parse_StringMCComponentType("pkg.Component");
    Optional<ASTMCComponentType> generic = ComponentSymbolsWithMCBasicTypesTestMill
        .parser()
        .parse_StringMCComponentType("pkg.Component<String>");
    Optional<ASTMCComponentType> primitive = ComponentSymbolsWithMCBasicTypesTestMill
        .parser()
        .parse_StringMCComponentType("int");

    assertTrue(qualified.isPresent());
    assertTrue(qualified.get() instanceof ASTMCQualifiedComponentType);
    assertTrue(generic.isPresent());
    assertTrue(generic.get() instanceof ASTMCGenericComponentType);
    assertTrue(primitive.isEmpty());
    MCAssertions.assertHasFindingStartingWith("mismatched keyword 'int'");
  }
}
