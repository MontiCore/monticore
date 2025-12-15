/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SynthesizeComponentFromMCSimpleGenericTypesTest extends AbstractMCTest {

  private static final String COMP = "Comp";
  private static final String SCOPE = "scoop";
  private static final String STRING = "String";
  private static final String LIST = "List";

  @BeforeEach
  public void setup() {
    Log.clearFindings();
    LogStub.init();
    Log.enableFailQuick(false);

    ComponentSymbolsWithMCBasicTypesTestMill.reset();
    ComponentSymbolsWithMCBasicTypesTestMill.init();
    BasicSymbolsMill.initializePrimitives();

    // Setup TypeCheck
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

  private static final class PositiveCase {
    final String name;
    final boolean qualified;

    PositiveCase(String name, boolean qualified) {
      this.name = name;
      this.qualified = qualified;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  static Stream<Arguments> positiveCases() {
    return Stream.of(
      Arguments.of(new PositiveCase("normal Comp<String, List<String>>", false)),
      Arguments.of(new PositiveCase("qualified scoop.Comp<scoop.List<scoop.String>, scoop.String>", true))
    );
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("positiveCases")
  public void shouldHandleMCBasicGenericType_parametrized(PositiveCase pc) {

    var global = ComponentSymbolsWithMCBasicTypesTestMill.globalScope();

    ComponentTypeSymbol compSym = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(COMP)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .setTypeParameters(ImmutableList.of(
        ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("K").build(),
        ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("V").build()
      )).build();

    var scopeOfComp = ComponentSymbolsWithMCBasicTypesTestMill.scope();
    scopeOfComp.setName(SCOPE);
    scopeOfComp.add(compSym);
    scopeOfComp.addSubScope(compSym.getSpannedScope());
    global.addSubScope(scopeOfComp);

    OOTypeSymbol stringSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(STRING)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    scopeOfComp.add(stringSym);
    scopeOfComp.addSubScope(stringSym.getSpannedScope());

    OOTypeSymbol listSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(LIST)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    listSym.addTypeVarSymbol(ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build());
    scopeOfComp.add(listSym);
    scopeOfComp.addSubScope(listSym.getSpannedScope());

    ASTMCQualifiedType astString = mkQualifiedType(STRING, scopeOfComp);
    ASTMCQualifiedType astQualString = mkQualifiedType(SCOPE, STRING, global); // QUALIFIED => name scope = global

    ASTMCType astListOfString = createGenericType(ImmutableList.of(LIST), scopeOfComp, astString);
    ASTMCType astQualListOfString = createGenericType(
      ImmutableList.of(SCOPE, LIST),
      global,
      astQualString
    );

    ASTMCBasicGenericType astNormalComp = createGenericType(
      ImmutableList.of(COMP),
      scopeOfComp,
      astString, astListOfString
    );
    ASTMCBasicGenericType astQualComp = createGenericType(
      ImmutableList.of(SCOPE, COMP),
      global,
      astQualListOfString, astQualString
    );

    CompKindCheckResult result4normal = new CompKindCheckResult();
    CompKindCheckResult result4qual = new CompKindCheckResult();
    new SynthesizeCompKindFromMCSimpleGenericTypes(result4normal).handle(astNormalComp);
    new SynthesizeCompKindFromMCSimpleGenericTypes(result4qual).handle(astQualComp);

    // --- assertions communes ---
    Assertions.assertTrue(result4normal.getResult().isPresent());
    Assertions.assertTrue(result4qual.getResult().isPresent());
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, result4normal.getResult().get());
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, result4qual.getResult().get());

    CompKindOfGenericComponentType normal = (CompKindOfGenericComponentType) result4normal.getResult().get();
    CompKindOfGenericComponentType qual = (CompKindOfGenericComponentType) result4qual.getResult().get();

    if (!pc.qualified) {
      Assertions.assertEquals(compSym, normal.getTypeInfo());
      Assertions.assertInstanceOf(SymTypeOfObject.class, normal.getTypeBindingFor("K").get());
      Assertions.assertInstanceOf(SymTypeOfGenerics.class, normal.getTypeBindingFor("V").get());
      Assertions.assertEquals(stringSym, normal.getTypeBindingFor("K").get().getTypeInfo());
      Assertions.assertEquals(listSym, normal.getTypeBindingFor("V").get().getTypeInfo());
      Assertions.assertEquals(stringSym,
        ((SymTypeOfGenerics) normal.getTypeBindingFor("V").get()).getArgument(0).getTypeInfo()
      );
      Assertions.assertTrue(normal.getSourceNode().isPresent());
      Assertions.assertEquals(astNormalComp, normal.getSourceNode().get());
    }
    else {
      Assertions.assertEquals(compSym, qual.getTypeInfo());
      Assertions.assertInstanceOf(SymTypeOfGenerics.class, qual.getTypeBindingFor("K").get());
      Assertions.assertInstanceOf(SymTypeOfObject.class, qual.getTypeBindingFor("V").get());
      Assertions.assertEquals(stringSym, qual.getTypeBindingFor("V").get().getTypeInfo());
      Assertions.assertEquals(listSym, qual.getTypeBindingFor("K").get().getTypeInfo());
      Assertions.assertEquals(stringSym,
        ((SymTypeOfGenerics) qual.getTypeBindingFor("K").get()).getArgument(0).getTypeInfo()
      );
      Assertions.assertTrue(qual.getSourceNode().isPresent());
      Assertions.assertEquals(astQualComp, qual.getSourceNode().get());
    }
  }

  private static ASTMCQualifiedType mkQualifiedType(String simpleName, IComponentSymbolsWithMCBasicTypesTestScope encl) {
    ASTMCQualifiedType t = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(simpleName)
        .build())
      .build();
    t.setEnclosingScope(encl);
    t.getMCQualifiedName().setEnclosingScope(encl);
    return t;
  }

  private static ASTMCQualifiedType mkQualifiedType(String p1, String p2, IComponentSymbolsWithMCBasicTypesTestScope encl) {
    ASTMCQualifiedType t = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(p1)
        .addParts(p2)
        .build())
      .build();
    t.setEnclosingScope(encl);
    t.getMCQualifiedName().setEnclosingScope(encl);
    return t;
  }

  private enum NegativeKind {
    COMP_TYPE_UNRESOLVABLE,
    TYPE_ARGUMENT_UNRESOLVABLE,
    NESTED_TYPE_ARGUMENT_UNRESOLVABLE
  }

  private static final class NegativeScenario {
    final String name;
    final NegativeKind kind;

    NegativeScenario(String name, NegativeKind kind) {
      this.name = name;
      this.kind = kind;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  static Stream<Arguments> negativeScenarios() {
    return Stream.of(
      Arguments.of(new NegativeScenario("Comp type unresolvable", NegativeKind.COMP_TYPE_UNRESOLVABLE)),
      Arguments.of(new NegativeScenario("Type argument unresolvable", NegativeKind.TYPE_ARGUMENT_UNRESOLVABLE)),
      Arguments.of(new NegativeScenario("Nested type argument unresolvable", NegativeKind.NESTED_TYPE_ARGUMENT_UNRESOLVABLE))
    );
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("negativeScenarios")
  public void shouldNotHandleMCBasicGenericType_parametrized(NegativeScenario scenario) {

    ASTMCBasicGenericType astComp = buildNegativeAst(scenario.kind);

    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    new SynthesizeCompKindFromMCSimpleGenericTypes(resultWrapper).handle(astComp);

    Assertions.assertFalse(resultWrapper.getResult().isPresent());
  }

  private ASTMCBasicGenericType buildNegativeAst(NegativeKind kind) {
    var global = ComponentSymbolsWithMCBasicTypesTestMill.globalScope();

    switch (kind) {
      case COMP_TYPE_UNRESOLVABLE: {
        OOTypeSymbol stringSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
          .setName(STRING)
          .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
          .build();
        global.add(stringSym);
        global.addSubScope(stringSym.getSpannedScope());

        ASTMCQualifiedType astString = mkQualifiedType(STRING, global);

        return createGenericType(ImmutableList.of("Unresolvable"), global, astString);
      }

      case TYPE_ARGUMENT_UNRESOLVABLE: {
        ComponentTypeSymbol compSym = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
          .setName(COMP)
          .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
          .setTypeParameters(ImmutableList.of(
            ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build()
          )).build();
        global.add(compSym);
        global.addSubScope(compSym.getSpannedScope());

        ASTMCQualifiedType astString = mkQualifiedType(STRING, global);

        return createGenericType(ImmutableList.of("Unresolvable"), global, astString);
      }

      case NESTED_TYPE_ARGUMENT_UNRESOLVABLE: {
        ComponentTypeSymbol compSym = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
          .setName(COMP)
          .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
          .setTypeParameters(ImmutableList.of(
            ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build()
          )).build();
        global.add(compSym);
        global.addSubScope(compSym.getSpannedScope());

        OOTypeSymbol listSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
          .setName(LIST)
          .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
          .build();
        listSym.addTypeVarSymbol(ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build());
        global.add(listSym);
        global.addSubScope(listSym.getSpannedScope());

        ASTMCQualifiedType astString = mkQualifiedType(STRING, global);

        ASTMCType astListOfString = createGenericType(ImmutableList.of(LIST), global, astString);
        return createGenericType(ImmutableList.of("Unresolvable"), global, astListOfString);
      }

      default:
        throw new IllegalStateException("Unhandled NegativeKind: " + kind);
    }
  }

  @Test
  public void shouldLogErrorWhenMultipleComponentTypesMatch() {
    String compName = "Comp";
    String stringName = "MyString";

    var globalScope = ComponentSymbolsWithMCBasicTypesTestMill.globalScope();

    // Two component symbols with the same name "Comp"
    ComponentTypeSymbol comp1 = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentTypeSymbol comp2 = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();

    globalScope.add(comp1);
    globalScope.addSubScope(comp1.getSpannedScope());
    globalScope.add(comp2);
    globalScope.addSubScope(comp2.getSpannedScope());

    // MyString type used as type argument
    OOTypeSymbol stringSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    globalScope.add(stringSym);
    globalScope.addSubScope(stringSym.getSpannedScope());

    ASTMCQualifiedType astString = mkQualifiedType(stringName, globalScope);

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of(compName),
      globalScope,
      astString
    );

    CompKindCheckResult result = new CompKindCheckResult();
    new SynthesizeCompKindFromMCSimpleGenericTypes(result).handle(astComp);

    Assertions.assertTrue(result.getResult().isPresent());
    Assertions.assertEquals(comp1, result.getResult().get().getTypeInfo());

    MCAssertions.assertHasFindingStartingWith("0xD0105");
    Log.clearFindings();
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
