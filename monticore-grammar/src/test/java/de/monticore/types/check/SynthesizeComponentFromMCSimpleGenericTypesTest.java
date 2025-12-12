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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SynthesizeComponentFromMCSimpleGenericTypesTest extends AbstractMCTest {

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

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void shouldHandleMCBasicGenericType(boolean useQualifiedVariant) {
    // Given
    String compName = "Comp";
    String stringName = "MyString";
    String listName = "MyList";
    String scopeName = "scoop";

    // Component Comp<K, V>
    ComponentTypeSymbol compSym = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .setTypeParameters(ImmutableList.of(
        ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("K").build(),
        ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("V").build()
      )).build();

    var compScope = ComponentSymbolsWithMCBasicTypesTestMill.scope();
    compScope.setName(scopeName);
    compScope.add(compSym);
    compScope.addSubScope(compSym.getSpannedScope());
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(compScope);

    // MyString type in comp scope
    OOTypeSymbol stringSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    compScope.add(stringSym);
    compScope.addSubScope(stringSym.getSpannedScope());

    // MyList<T> type in comp scope
    OOTypeSymbol listSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    listSym.addTypeVarSymbol(ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build());
    compScope.add(listSym);
    compScope.addSubScope(listSym.getSpannedScope());

    // Now we build generic ast types Comp<String, List<String>> and scoop.Comp<scoop.List<scoop.String>, scoop.String>
    // That lay a) in the scope where the symbols lay and b) in the global scope.
    ASTMCQualifiedType astString = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(stringName)
        .build())
      .build();
    astString.setEnclosingScope(compScope);
    astString.getMCQualifiedName().setEnclosingScope(compScope);

    ASTMCQualifiedType astQualString = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(scopeName)
        .addParts(stringName)
        .build())
      .build();
    astQualString.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    astQualString.getMCQualifiedName().setEnclosingScope(compScope);

    // MyList<MyString> in comp scope
    ASTMCType astListOfString = createGenericType(ImmutableList.of(listName), compScope, astString);

    // scoop.MyList<scoop.MyString> from global scope
    ASTMCType astQualListOfString = createGenericType(
      ImmutableList.of(scopeName, listName), ComponentSymbolsWithMCBasicTypesTestMill.globalScope(), astQualString);

    // Now build qualified and unqualified generic types
    ASTMCBasicGenericType astNormalComp = createGenericType(
      ImmutableList.of(compName),
      compScope,
      astString, astListOfString
    );
    astNormalComp.setEnclosingScope(compScope);

    // scoop.Comp<scoop.MyList<scoop.MyString>, scoop.MyString> in global scope
    ASTMCBasicGenericType astQualComp = createGenericType(
      ImmutableList.of(scopeName, compName),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope(),
      astQualListOfString, astQualString
    );
    astQualComp.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    ASTMCBasicGenericType ast = useQualifiedVariant ? astQualComp : astNormalComp;

    CompKindCheckResult result = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth = new SynthesizeCompKindFromMCSimpleGenericTypes(result);

    // When
    synth.handle(ast);

    // Then
    Assertions.assertTrue(result.getResult().isPresent());
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, result.getResult().get());

    CompKindOfGenericComponentType generic =
      (CompKindOfGenericComponentType) result.getResult().get();

    Assertions.assertEquals(compSym, generic.getTypeInfo());
    Assertions.assertTrue(generic.getSourceNode().isPresent());
    Assertions.assertEquals(ast, generic.getSourceNode().get());

    SymTypeExpression kBinding = generic.getTypeBindingFor("K").get();
    SymTypeExpression vBinding = generic.getTypeBindingFor("V").get();

    if (!useQualifiedVariant) {
      // Comp<MyString, MyList<MyString>>: K = MyString, V = MyList<MyString>
      Assertions.assertInstanceOf(SymTypeOfObject.class, kBinding);
      Assertions.assertInstanceOf(SymTypeOfGenerics.class, vBinding);
      Assertions.assertEquals(stringSym, kBinding.getTypeInfo());
      Assertions.assertEquals(listSym, vBinding.getTypeInfo());
      Assertions.assertEquals(stringSym,
        ((SymTypeOfGenerics) vBinding).getArgument(0).getTypeInfo()
      );
    } else {
      // scoop.Comp<scoop.MyList<scoop.MyString>, scoop.MyString>:
      // K = MyList<MyString>, V = MyString
      Assertions.assertInstanceOf(SymTypeOfGenerics.class, kBinding);
      Assertions.assertInstanceOf(SymTypeOfObject.class, vBinding);
      Assertions.assertEquals(listSym, kBinding.getTypeInfo());
      Assertions.assertEquals(stringSym, vBinding.getTypeInfo());
      Assertions.assertEquals(stringSym, ((SymTypeOfGenerics) kBinding).getArgument(0).getTypeInfo());
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

    ASTMCType astString = MCTypeFacade.getInstance().createQualifiedType(stringName);
    astString.setEnclosingScope(globalScope);
    if (astString instanceof ASTMCQualifiedType) {
      ((ASTMCQualifiedType) astString).getMCQualifiedName().setEnclosingScope(globalScope);
    }

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of(compName),
      globalScope,
      astString
    );

    CompKindCheckResult result = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth =
      new SynthesizeCompKindFromMCSimpleGenericTypes(result);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertTrue(result.getResult().isPresent());
    Assertions.assertEquals(comp1, result.getResult().get().getTypeInfo());
    MCAssertions.assertHasFindingStartingWith("0xD0105");
  }

  @Test
  public void shouldNotHandleMCBasicGenericTypeBecauseCompTypeUnresolvable() {
    // Given
    String stringName = "MyString";
    OOTypeSymbol stringSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(stringSym);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(stringSym.getSpannedScope());

    ASTMCType astString = MCTypeFacade.getInstance().createQualifiedType(stringName);
    astString.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    if (astString instanceof ASTMCQualifiedType) {
      ((ASTMCQualifiedType) astString).getMCQualifiedName()
        .setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    }

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope(),
      astString
    );

    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth = new SynthesizeCompKindFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
  }

  @Test
  public void shouldHandleMCBasicGenericTypeBecauseTypeArgumentUnresolvable() {
    // Given
    String compName = "Comp";
    ComponentTypeSymbol compSym = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .setTypeParameters(ImmutableList.of(
        ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build()
      )).build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(compSym);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(compSym.getSpannedScope());

    ASTMCType astString = MCTypeFacade.getInstance().createQualifiedType("MyString");
    astString.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    if (astString instanceof ASTMCQualifiedType) {
      ((ASTMCQualifiedType) astString).getMCQualifiedName()
        .setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    }

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope(),
      astString
    );

    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth = new SynthesizeCompKindFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
  }

  @Test
  public void shouldHandleMCBasicGenericTypeBecauseNestedTypeArgumentUnresolvable() {
    // Given
    String compName = "Comp";
    ComponentTypeSymbol compSym = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .setTypeParameters(ImmutableList.of(
        ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build()
      )).build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(compSym);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(compSym.getSpannedScope());

    String listName = "MyList";
    OOTypeSymbol listSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    listSym.addTypeVarSymbol(ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build());
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(listSym);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(listSym.getSpannedScope());

    ASTMCType astString = MCTypeFacade.getInstance().createQualifiedType("MyString");
    astString.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    if (astString instanceof ASTMCQualifiedType) {
      ((ASTMCQualifiedType) astString).getMCQualifiedName()
        .setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    }

    ASTMCType astListOfString = createGenericType(
      ImmutableList.of(listName),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope(),
      astString
    );

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope(),
      astListOfString
    );

    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth = new SynthesizeCompKindFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
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
