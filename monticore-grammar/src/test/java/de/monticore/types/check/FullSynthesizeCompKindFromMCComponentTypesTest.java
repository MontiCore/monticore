/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.componentsymbolswithmccomponenttypestest.ComponentSymbolsWithMCComponentTypesTestMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mccomponenttypes._ast.ASTMCComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCGenericComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCQualifiedComponentType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FullSynthesizeCompKindFromMCComponentTypesTest extends AbstractMCTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();

    ComponentSymbolsWithMCComponentTypesTestMill.reset();
    ComponentSymbolsWithMCComponentTypesTestMill.init();
  }

  @Test
  public void shouldParseAndPrintOnlyComponentTypes() throws IOException {
    Optional<ASTMCComponentType> qualified = parse("pkg.Component");
    Optional<ASTMCComponentType> generic = parse("pkg.Component<String>");
    Optional<ASTMCComponentType> primitive = parse("int");

    assertTrue(qualified.isPresent());
    assertInstanceOf(ASTMCQualifiedComponentType.class, qualified.get());
    assertEquals("pkg.Component", qualified.get().printType());
    assertTrue(generic.isPresent());
    assertInstanceOf(ASTMCGenericComponentType.class, generic.get());
    assertEquals("pkg.Component<String>", generic.get().printType());
    assertTrue(primitive.isEmpty());
    MCAssertions.assertHasFindingStartingWith("mismatched keyword 'int'");
  }

  @Test
  public void shouldSynthesizeQualifiedComponentType() throws IOException {
    ComponentTypeSymbol component = addComponent("A");
    ASTMCComponentType ast = parse("A").orElseThrow();
    ast.setEnclosingScope(ComponentSymbolsWithMCComponentTypesTestMill.globalScope());

    Optional<CompKindExpression> result =
        new FullSynthesizeCompKindFromMCComponentTypes().synthesize(ast);

    assertTrue(result.isPresent());
    assertTrue(result.get().isComponentType());
    assertSame(component, result.get().getTypeInfo());
    assertSame(ast, result.get().getSourceNode().orElseThrow());
  }

  @Test
  public void shouldSynthesizeGenericComponentType() throws IOException {
    ComponentTypeSymbol component = addComponent("Generic");
    ASTMCComponentType ast = parse("Generic<>").orElseThrow();
    ast.setEnclosingScope(ComponentSymbolsWithMCComponentTypesTestMill.globalScope());

    Optional<CompKindExpression> result =
        new FullSynthesizeCompKindFromMCComponentTypes().synthesize(ast);

    assertTrue(result.isPresent());
    assertTrue(result.get().isGenericComponentType());
    assertSame(component, result.get().getTypeInfo());
    assertSame(ast, result.get().getSourceNode().orElseThrow());
    assertTrue(result.get().asGenericComponentType().getTypeBindingsAsList().isEmpty());
  }

  @Test
  public void shouldReturnEmptyForUnresolvableComponent() throws IOException {
    ASTMCComponentType ast = parse("Missing").orElseThrow();
    ast.setEnclosingScope(ComponentSymbolsWithMCComponentTypesTestMill.globalScope());

    Optional<CompKindExpression> result =
        new FullSynthesizeCompKindFromMCComponentTypes().synthesize(ast);

    assertTrue(result.isEmpty());
    MCAssertions.assertHasFindingStartingWith("0xD0104");
  }

  @Test
  public void shouldReportAmbiguousComponentReference() throws IOException {
    ComponentTypeSymbol first = addComponent("Duplicate");
    addComponent("Duplicate");
    ASTMCComponentType ast = parse("Duplicate").orElseThrow();
    ast.setEnclosingScope(ComponentSymbolsWithMCComponentTypesTestMill.globalScope());

    Optional<CompKindExpression> result =
        new FullSynthesizeCompKindFromMCComponentTypes().synthesize(ast);

    assertTrue(result.isPresent());
    assertSame(first, result.get().getTypeInfo());
    MCAssertions.assertHasFindingStartingWith("0xD0105");
  }

  @Test
  public void shouldStoreDefiningSymbolOnBothComponentTypeForms() throws IOException {
    ComponentTypeSymbol component = addComponent("Stored");
    ASTMCComponentType qualified = parse("Stored").orElseThrow();
    ASTMCComponentType generic = parse("Stored<>").orElseThrow();

    qualified.setDefiningSymbol(component);
    generic.setDefiningSymbol(component);

    assertSame(component, qualified.getDefiningSymbol().orElseThrow());
    assertSame(component, generic.getDefiningSymbol().orElseThrow());
  }

  @Test
  public void shouldReportUnsupportedTypeArgument() {
    ASTMCTypeArgument unsupported = MCFullGenericTypesMill
        .mCWildcardTypeArgumentBuilder()
        .build();
    SynthesizeCompKindFromMCComponentTypes synthesizer =
        new SynthesizeCompKindFromMCComponentTypes(new CompKindCheckResult());

    assertTrue(synthesizer.typeArgumentsToTypes(List.of(unsupported)).isEmpty());
    MCAssertions.assertHasFindingStartingWith("0xD0106");
  }

  @Test
  @SuppressWarnings("deprecation")
  public void shouldKeepDeprecatedQualifiedTypeSynthesizerOperational() {
    ComponentTypeSymbol component = addComponent("Legacy");
    ASTMCQualifiedType ast = ComponentSymbolsWithMCComponentTypesTestMill
        .mCQualifiedTypeBuilder()
        .setMCQualifiedName(ComponentSymbolsWithMCComponentTypesTestMill
            .mCQualifiedNameBuilder()
            .addAllParts(List.of("Legacy"))
            .build())
        .build();
    ast.setEnclosingScope(ComponentSymbolsWithMCComponentTypesTestMill.globalScope());

    Optional<CompKindExpression> result =
        new FullSynthesizeCompKindFromMCBasicTypes().synthesize(ast);

    assertTrue(result.isPresent());
    assertSame(component, result.get().getTypeInfo());
    assertSame(ast, result.get().getSourceNode().orElseThrow());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void shouldKeepDeprecatedGenericTypeSynthesizerOperational() {
    ComponentTypeSymbol component = addComponent("LegacyGeneric");
    ASTMCBasicGenericType ast = ComponentSymbolsWithMCComponentTypesTestMill
        .mCBasicGenericTypeBuilder()
        .setNamesList(List.of("LegacyGeneric"))
        .build();
    ast.setEnclosingScope(ComponentSymbolsWithMCComponentTypesTestMill.globalScope());

    Optional<CompKindExpression> result =
        new FullSynthesizeCompKindFromMCSimpleGenericTypes().synthesize(ast);

    assertTrue(result.isPresent());
    assertSame(component, result.get().getTypeInfo());
    assertSame(ast, result.get().getSourceNode().orElseThrow());
  }

  protected Optional<ASTMCComponentType> parse(String input) throws IOException {
    return ComponentSymbolsWithMCComponentTypesTestMill.parser()
        .parse_StringMCComponentType(input);
  }

  protected ComponentTypeSymbol addComponent(String name) {
    ComponentTypeSymbol component = ComponentSymbolsWithMCComponentTypesTestMill
        .componentTypeSymbolBuilder()
        .setName(name)
        .setSpannedScope(ComponentSymbolsWithMCComponentTypesTestMill.scope())
        .build();
    ComponentSymbolsWithMCComponentTypesTestMill.globalScope().add(component);
    ComponentSymbolsWithMCComponentTypesTestMill.globalScope()
        .addSubScope(component.getSpannedScope());
    return component;
  }
}
