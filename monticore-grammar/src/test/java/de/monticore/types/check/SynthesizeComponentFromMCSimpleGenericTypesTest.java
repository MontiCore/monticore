/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.componentsymbolswithmcbasictypestest.ComponentSymbolsWithMCBasicTypesTestMill;
import de.monticore.types.componentsymbolswithmcbasictypestest._symboltable.IComponentSymbolsWithMCBasicTypesTestScope;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericTypeBuilder;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.misc.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SynthesizeComponentFromMCSimpleGenericTypesTest extends AbstractMCTest {

  @BeforeAll
  public static void beforeAll() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();


    BasicSymbolsMill.initializePrimitives();
    BasicSymbolsMill.initializeString();
  }

  @BeforeEach
  public void setup() {
    Log.clearFindings();

    ComponentSymbolsWithMCBasicTypesTestMill.reset();
    ComponentSymbolsWithMCBasicTypesTestMill.init();

    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();

    var traverser = ComponentSymbolsWithMCBasicTypesTestMill.traverser();

    MCBasicTypesTypeVisitor basicTypesVisitor = new MCBasicTypesTypeVisitor();
    basicTypesVisitor.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(basicTypesVisitor);

    MCCollectionTypesTypeVisitor collVisitor = new MCCollectionTypesTypeVisitor();
    collVisitor.setType4Ast(type4Ast);
    traverser.add4MCCollectionTypes(collVisitor);

    MCSimpleGenericTypesTypeVisitor simpleGenVisitor = new MCSimpleGenericTypesTypeVisitor();
    simpleGenVisitor.setType4Ast(type4Ast);
    traverser.add4MCSimpleGenericTypes(simpleGenVisitor);

    new MapBasedTypeCheck3(traverser, type4Ast, ctx4Ast).setThisAsDelegate();
  }

  static Stream<Arguments> compRefs() {
    return Stream.of(
      Arguments.of(ImmutableList.of("Comp"), false),
      Arguments.of(ImmutableList.of("scoop", "Comp"), true)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"Comp", "scoop.Comp"})
  public void shouldHandleMCBasicGenericType(String qualifiedCompName) {
    // Given
    var globalScope = ComponentSymbolsWithMCBasicTypesTestMill.globalScope();

    // local scope "scoop" sous le global
    IComponentSymbolsWithMCBasicTypesTestScope localScope = ComponentSymbolsWithMCBasicTypesTestMill.scope();
    localScope.setName("scoop");
    globalScope.addSubScope(localScope);

    ComponentTypeSymbol compSym = createComponentType("Comp", "K", "V");
    addWithSpannedScope(localScope, compSym);

    OOTypeSymbol stringSym = createOOType("String");
    addWithSpannedScope(localScope, stringSym);

    OOTypeSymbol listSym = createOOType("List");
    listSym.addTypeVarSymbol(ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build());
    addWithSpannedScope(localScope, listSym);

    ASTMCQualifiedType astStringLocal = createQualifiedType(
      ImmutableList.of("String"),
      localScope,
      localScope
    );

    ASTMCType astListOfStringLocal = createGenericType(
      ImmutableList.of("List"),
      localScope,
      astStringLocal
    );

    ASTMCQualifiedType compNameAst = MCTypeFacade.getInstance().createQualifiedType(qualifiedCompName);
    List<String> compNameParts = compNameAst.getMCQualifiedName().getPartsList();
    boolean qualified = compNameParts.size() > 1;

    IComponentSymbolsWithMCBasicTypesTestScope enclScope = qualified ? globalScope : localScope;

    ASTMCBasicGenericType astComp = createGenericType(
      compNameParts,
      enclScope,
      astStringLocal,
      astListOfStringLocal
    );

    // When
    CompKindCheckResult wrapper = new CompKindCheckResult();
    new SynthesizeCompKindFromMCSimpleGenericTypes(wrapper).handle(astComp);

    // Then
    Assertions.assertTrue(wrapper.getResult().isPresent(), "Expected synthesis result to be present");
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, wrapper.getResult().get());
    CompKindOfGenericComponentType result = (CompKindOfGenericComponentType) wrapper.getResult().get();

    Assertions.assertEquals(compSym, result.getTypeInfo());
    Assertions.assertEquals(astComp, result.getSourceNode().orElseThrow());

    Assertions.assertEquals(stringSym, result.getTypeBindingFor("K").orElseThrow().getTypeInfo());

    SymTypeExpression v = result.getTypeBindingFor("V").orElseThrow();
    Assertions.assertInstanceOf(SymTypeOfGenerics.class, v);
    SymTypeOfGenerics vGen = (SymTypeOfGenerics) v;
    Assertions.assertEquals(listSym, vGen.getTypeInfo());
    Assertions.assertEquals(stringSym, vGen.getArgument(0).getTypeInfo());

    MCAssertions.assertNoFindings();
  }

  @Test
  public void shouldNotHandleMCBasicGenericTypeBecauseCompTypeUnresolvable() {
    // Given
    OOTypeSymbol stringSym = createOOType("String");
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(stringSym);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(stringSym.getSpannedScope());

    ASTMCQualifiedType astString = createQualifiedType(
      ImmutableList.of("String"),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope(),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope()
    );

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope(),
      astString
    );

    // When
    CompKindCheckResult wrapper = new CompKindCheckResult();
    new SynthesizeCompKindFromMCSimpleGenericTypes(wrapper).handle(astComp);

    // Then
    Assertions.assertTrue(wrapper.getResult().isEmpty());
  }

  @Test
  public void shouldLogErrorForDuplicateSymbols() {
    // Given
    IComponentSymbolsWithMCBasicTypesTestScope localScope = ComponentSymbolsWithMCBasicTypesTestMill.scope();
    localScope.setName("scoop");
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(localScope);

    ComponentTypeSymbol comp1 = createComponentType("Comp", "T");
    ComponentTypeSymbol comp2 = createComponentType("Comp", "T");
    addWithSpannedScope(localScope, comp1);
    addWithSpannedScope(localScope, comp2);

    OOTypeSymbol stringSym = createOOType("String");
    addWithSpannedScope(localScope, stringSym);

    ASTMCQualifiedType astString = createQualifiedType(
      ImmutableList.of("String"),
      localScope,
      localScope
    );

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Comp"),
      localScope,
      astString
    );

    // When
    CompKindCheckResult wrapper = new CompKindCheckResult();
    new SynthesizeCompKindFromMCSimpleGenericTypes(wrapper).handle(astComp);

    // Then
    MCAssertions.assertHasFindingStartingWith("0xD0105");
    Assertions.assertTrue(wrapper.getResult().isPresent());
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, wrapper.getResult().get());
    Assertions.assertEquals(astComp, wrapper.getResult().get().getSourceNode().orElseThrow());
  }

  private static void addWithSpannedScope(IComponentSymbolsWithMCBasicTypesTestScope scope, ComponentTypeSymbol sym) {
    scope.add(sym);
    scope.addSubScope(sym.getSpannedScope());
  }

  private static void addWithSpannedScope(IComponentSymbolsWithMCBasicTypesTestScope scope, OOTypeSymbol sym) {
    scope.add(sym);
    scope.addSubScope(sym.getSpannedScope());
  }

  private static ComponentTypeSymbol createComponentType(String name, String... typeParams) {
    var builder = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(name)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope());

    builder.setTypeParameters(
      Arrays.stream(typeParams)
        .map(tp -> ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName(tp).build())
        .collect(ImmutableList.toImmutableList())
    );

    return builder.build();
  }

  private static OOTypeSymbol createOOType(String name) {
    return ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(name)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
  }

  private static ASTMCQualifiedType createQualifiedType(
    List<String> nameParts,
    IComponentSymbolsWithMCBasicTypesTestScope typeEnclosingScope,
    IComponentSymbolsWithMCBasicTypesTestScope nameEnclosingScope
  ) {
    ASTMCQualifiedType type = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addAllParts(nameParts)
        .build())
      .build();

    type.setEnclosingScope(typeEnclosingScope);
    type.getMCQualifiedName().setEnclosingScope(nameEnclosingScope);
    return type;
  }

  /**
   * Returns a {@link ASTMCBasicGenericType} whose format is {@code name.parts<typeArg[0], typeArg[1], ...>}.
   * All newly created AST objects are enclosed by {@code enclScope}.
   */
  protected static ASTMCBasicGenericType createGenericType(@NotNull List<String> nameParts,
                                                           @NotNull IComponentSymbolsWithMCBasicTypesTestScope enclScope,
                                                           @NotNull ASTMCType... typeArgs) {
    Preconditions.checkNotNull(nameParts);
    Preconditions.checkNotNull(enclScope);
    Preconditions.checkNotNull(typeArgs);
    Preconditions.checkArgument(Arrays.stream(typeArgs).allMatch(Objects::nonNull));

    ASTMCBasicGenericTypeBuilder builder = ComponentSymbolsWithMCBasicTypesTestMill.mCBasicGenericTypeBuilder()
      .setNamesList(nameParts);

    for (ASTMCType typeArg : typeArgs) {
      if (typeArg instanceof ASTMCPrimitiveType) {
        ASTMCPrimitiveType asPrimitiveType = (ASTMCPrimitiveType) typeArg;
        ASTMCPrimitiveTypeArgument asArg = ComponentSymbolsWithMCBasicTypesTestMill.mCPrimitiveTypeArgumentBuilder()
          .setMCPrimitiveType(asPrimitiveType).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);

      } else if (typeArg instanceof ASTMCQualifiedType) {
        ASTMCQualifiedType asQualType = (ASTMCQualifiedType) typeArg;
        ASTMCBasicTypeArgument asArg = ComponentSymbolsWithMCBasicTypesTestMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(asQualType).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);

      } else {
        ASTMCCustomTypeArgument asArg = ComponentSymbolsWithMCBasicTypesTestMill.mCCustomTypeArgumentBuilder().setMCType(typeArg).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);
      }
    }

    ASTMCBasicGenericType type = builder.build();
    type.setEnclosingScope(enclScope);
    return type;
  }
}
