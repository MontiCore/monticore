/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.runtime.junit.AbstractMCTest;
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
    BasicSymbolsMill.initializeString();


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

  @Test
  public void shouldHandleMCBasicGenericType() {
    // Given
    // First, we build OOSymbols for String and List<T> and a ComponentTypeSymbol for Comp<K,V>. We put them in a
    // common sub scope of the global scope.
    String compName = "Comp";
    ComponentTypeSymbol compSym = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .setTypeParameters(ImmutableList.of(
        ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("K").build(),
        ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("V").build()
      )).build();

    String nameOfCompScope = "scoop";
    var scopeOfComp = ComponentSymbolsWithMCBasicTypesTestMill.scope();
    scopeOfComp.setName(nameOfCompScope);
    scopeOfComp.add(compSym);
    scopeOfComp.addSubScope(compSym.getSpannedScope());
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(scopeOfComp);

    String stringName = "String";
    OOTypeSymbol stringSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    scopeOfComp.add(stringSym);
    scopeOfComp.addSubScope(stringSym.getSpannedScope());

    String listName = "List";
    OOTypeSymbol listSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    listSym.addTypeVarSymbol(ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build());
    scopeOfComp.add(listSym);
    scopeOfComp.addSubScope(listSym.getSpannedScope());

    // Now we build generic ast types Comp<String, List<String>> and scoop.Comp<scoop.List<scoop.String>, scoop.String>
    // That lay a) in the scope where the symbols lay and b) in the global scope.
    ASTMCQualifiedType astString = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(stringName)
        .build())
      .build();
    ASTMCQualifiedType astQualString = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(nameOfCompScope)
        .addParts(stringName)
        .build())
      .build();
    astString.setEnclosingScope(scopeOfComp);
    astString.getMCQualifiedName().setEnclosingScope(scopeOfComp);
    astQualString.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    astQualString.getMCQualifiedName().setEnclosingScope(scopeOfComp);

    ASTMCType astListOfString = createGenericType(ImmutableList.of(listName), scopeOfComp, astString);
    ASTMCType astQualListOfString = createGenericType(
      ImmutableList.of(nameOfCompScope, listName), ComponentSymbolsWithMCBasicTypesTestMill.globalScope(), astQualString);

    // Now build qualified and unqualified generic types
    ASTMCBasicGenericType astNormalComp = createGenericType(
      ImmutableList.of(compName),
      scopeOfComp,
      astString, astListOfString
    );
    ASTMCBasicGenericType astQualComp = createGenericType(
      ImmutableList.of(nameOfCompScope, compName),
      ComponentSymbolsWithMCBasicTypesTestMill.globalScope(),
      astQualListOfString, astQualString
    );

    astNormalComp.setEnclosingScope(scopeOfComp);
    astQualComp.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    CompKindCheckResult result4normal = new CompKindCheckResult();
    CompKindCheckResult result4qual = new CompKindCheckResult();
    SynthesizeCompKindFromMCSimpleGenericTypes synth4normal = new SynthesizeCompKindFromMCSimpleGenericTypes(result4normal);
    SynthesizeCompKindFromMCSimpleGenericTypes synth4qual = new SynthesizeCompKindFromMCSimpleGenericTypes(result4qual);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);

    // Then
    Assertions.assertTrue(result4normal.getResult().isPresent());
    Assertions.assertTrue(result4qual.getResult().isPresent());
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, result4normal.getResult().get());
    Assertions.assertInstanceOf(CompKindOfGenericComponentType.class, result4qual.getResult().get());


    CompKindOfGenericComponentType result4normalAsGeneric =
      (CompKindOfGenericComponentType) result4normal.getResult().get();
    CompKindOfGenericComponentType result4qualAsGeneric =
      (CompKindOfGenericComponentType) result4qual.getResult().get();

    Assertions.assertEquals(compSym, result4normal.getResult().get().getTypeInfo());
    Assertions.assertEquals(compSym, result4qual.getResult().get().getTypeInfo());
    Assertions.assertInstanceOf(SymTypeOfObject.class, result4normalAsGeneric.getTypeBindingFor("K").get());
    Assertions.assertInstanceOf(SymTypeOfGenerics.class, result4normalAsGeneric.getTypeBindingFor("V").get());
    Assertions.assertEquals(stringSym, result4normalAsGeneric.getTypeBindingFor("K").get().getTypeInfo());
    Assertions.assertEquals(listSym, result4normalAsGeneric.getTypeBindingFor("V").get().getTypeInfo());
    Assertions.assertEquals(stringSym,
      ((SymTypeOfGenerics) result4normalAsGeneric.getTypeBindingFor("V").get()).getArgument(0).getTypeInfo()
    );
    Assertions.assertInstanceOf(SymTypeOfGenerics.class, result4qualAsGeneric.getTypeBindingFor("K").get());
    Assertions.assertInstanceOf(SymTypeOfObject.class, result4qualAsGeneric.getTypeBindingFor("V").get());
    Assertions.assertEquals(stringSym, result4qualAsGeneric.getTypeBindingFor("V").get().getTypeInfo());
    Assertions.assertEquals(listSym, result4qualAsGeneric.getTypeBindingFor("K").get().getTypeInfo());
    Assertions.assertEquals(stringSym,
      ((SymTypeOfGenerics) result4qualAsGeneric.getTypeBindingFor("K").get()).getArgument(0).getTypeInfo()
    );

    Assertions.assertTrue(
      result4normalAsGeneric.getSourceNode().isPresent()
        && result4normalAsGeneric.getSourceNode().get().equals(astNormalComp),
      "Expected source node of normal generic Comp to be astNormalComp"
    );
    Assertions.assertTrue(
      result4qualAsGeneric.getSourceNode().isPresent()
        && result4qualAsGeneric.getSourceNode().get().equals(astQualComp),
      "Expected source node of qualified generic Comp to be astQualComp"
    );

  }

  @Test
  public void shouldNotHandleMCBasicGenericTypeBecauseCompTypeUnresolvable() {
    // Given
    String stringName = "String"; // Opposed to the component type, the type argument is present.
    OOTypeSymbol stringSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(stringSym);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(stringSym.getSpannedScope());

    ASTMCQualifiedType astString = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(stringName)
        .build())
      .build();
    astString.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

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

    ASTMCQualifiedType astString = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts("String")
        .build())
      .build();
    astString.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

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

    String listName = "List";
    OOTypeSymbol listSym = ComponentSymbolsWithMCBasicTypesTestMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    listSym.addTypeVarSymbol(ComponentSymbolsWithMCBasicTypesTestMill.typeVarSymbolBuilder().setName("T").build());
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(listSym);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(listSym.getSpannedScope());

    ASTMCQualifiedType astString = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts("String")
        .build())
      .build();
    astString.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    ASTMCType astListOfString = createGenericType(ImmutableList.of(listName), ComponentSymbolsWithMCBasicTypesTestMill.globalScope(), astString);

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
