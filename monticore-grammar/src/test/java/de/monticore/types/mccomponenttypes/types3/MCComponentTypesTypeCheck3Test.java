/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccomponenttypes.types3;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullSynthesizeCompKindFromMCBasicTypes;
import de.monticore.types.check.FullSynthesizeCompKindFromMCSimpleGenericTypes;
import de.monticore.types.componentsymbolswithmccomponenttypestest.ComponentSymbolsWithMCComponentTypesTestMill;
import de.monticore.types.componentsymbolswithmccomponenttypestest.types3.ComponentSymbolsWithMCComponentTypesTestTypeCheck3;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccomponenttypes._ast.ASTMCComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCGenericComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCQualifiedComponentType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types3.Type4Ast;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MCComponentTypesTypeCheck3Test extends AbstractMCTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();

    ComponentSymbolsWithMCComponentTypesTestMill.reset();
    ComponentSymbolsWithMCComponentTypesTestMill.init();
    ComponentSymbolsWithMCComponentTypesTestTypeCheck3.init();
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
  public void shouldCalculateQualifiedComponentTypeAndCacheIt() throws IOException {
    ComponentTypeSymbol component = addComponent("A");
    ASTMCComponentType ast = scoped(parse("A").orElseThrow());

    Optional<CompKindExpression> result =
        MCComponentTypesTypeCheck3.compTypeFromAST(ast);

    assertTrue(result.isPresent());
    assertTrue(result.get().isComponentType());
    assertSame(component, result.get().getTypeInfo());
    assertSame(component, ast.getDefiningSymbol().orElseThrow());
    assertSame(ast, result.get().getSourceNode().orElseThrow());
    assertSame(result.get(),
        MCComponentTypesTypeCheck3.compTypeFromAST(ast).orElseThrow());
  }

  @Test
  public void shouldCalculateGenericComponentType() throws IOException {
    TypeSymbol string = addType("String");
    TypeVarSymbol typeVar = ComponentSymbolsWithMCComponentTypesTestMill
        .typeVarSymbolBuilder().setName("T").build();
    ComponentTypeSymbol component = addComponent("Generic", List.of(typeVar));
    ASTMCComponentType ast = scoped(parse("Generic<String>").orElseThrow());

    Optional<CompKindExpression> result =
        MCComponentTypesTypeCheck3.compTypeFromAST(ast);

    assertTrue(result.isPresent());
    assertTrue(result.get().isGenericComponentType());
    assertSame(component, result.get().getTypeInfo());
    assertSame(string, result.get().asGenericComponentType()
        .getTypeBindingsAsList().getFirst().getTypeInfo());
    assertSame(ast, result.get().getSourceNode().orElseThrow());
  }

  @Test
  public void shouldCacheUnresolvableComponentAndReportItOnce() throws IOException {
    ASTMCComponentType ast = scoped(parse("Missing").orElseThrow());

    assertTrue(MCComponentTypesTypeCheck3.compTypeFromAST(ast).isEmpty());
    assertTrue(MCComponentTypesTypeCheck3.compTypeFromAST(ast).isEmpty());

    assertEquals(1, Log.getFindings().stream()
        .filter(finding -> finding.getMsg().startsWith("0xD0104"))
        .count());
    MCAssertions.assertHasFindingStartingWith("0xD0104");
  }

  @Test
  public void shouldReportAmbiguousComponentReference() throws IOException {
    ComponentTypeSymbol first = addComponent("Duplicate");
    addComponent("Duplicate");
    ASTMCComponentType ast = scoped(parse("Duplicate").orElseThrow());

    Optional<CompKindExpression> result =
        MCComponentTypesTypeCheck3.compTypeFromAST(ast);

    assertTrue(result.isPresent());
    assertSame(first, result.get().getTypeInfo());
    MCAssertions.assertHasFindingStartingWith("0xD0105");
  }

  @Test
  public void shouldReportUnsupportedTypeArgument() {
    ASTMCTypeArgument unsupported = MCFullGenericTypesMill
        .mCWildcardTypeArgumentBuilder()
        .build();
    MCComponentTypesTypeVisitor visitor = new MCComponentTypesTypeVisitor();
    visitor.setType4Ast(new Type4Ast());
    visitor.setCompType4Ast(new CompType4Ast());

    assertTrue(visitor.calculateTypeArguments(List.of(unsupported)).isEmpty());
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

  protected ASTMCComponentType scoped(ASTMCComponentType ast) {
    ast.setEnclosingScope(ComponentSymbolsWithMCComponentTypesTestMill.globalScope());
    if (ast instanceof ASTMCGenericComponentType genericType) {
      genericType.getMCTypeArgumentList().stream()
          .map(ASTMCTypeArgument::getMCTypeOpt)
          .flatMap(Optional::stream)
          .filter(ASTMCQualifiedType.class::isInstance)
          .map(ASTMCQualifiedType.class::cast)
          .forEach(type -> {
            type.setEnclosingScope(
                ComponentSymbolsWithMCComponentTypesTestMill.globalScope());
            type.getMCQualifiedName().setEnclosingScope(
                ComponentSymbolsWithMCComponentTypesTestMill.globalScope());
          });
    }
    return ast;
  }

  protected ComponentTypeSymbol addComponent(String name) {
    return addComponent(name, List.of());
  }

  protected ComponentTypeSymbol addComponent(
      String name,
      List<TypeVarSymbol> typeParameters) {
    ComponentTypeSymbol component = ComponentSymbolsWithMCComponentTypesTestMill
        .componentTypeSymbolBuilder()
        .setName(name)
        .setSpannedScope(ComponentSymbolsWithMCComponentTypesTestMill.scope())
        .setTypeParameters(new ArrayList<>(typeParameters))
        .build();
    ComponentSymbolsWithMCComponentTypesTestMill.globalScope().add(component);
    ComponentSymbolsWithMCComponentTypesTestMill.globalScope()
        .addSubScope(component.getSpannedScope());
    return component;
  }

  protected TypeSymbol addType(String name) {
    TypeSymbol type = ComponentSymbolsWithMCComponentTypesTestMill
        .typeSymbolBuilder()
        .setName(name)
        .setSpannedScope(ComponentSymbolsWithMCComponentTypesTestMill.scope())
        .build();
    ComponentSymbolsWithMCComponentTypesTestMill.globalScope().add(type);
    ComponentSymbolsWithMCComponentTypesTestMill.globalScope()
        .addSubScope(type.getSpannedScope());
    return type;
  }
}
