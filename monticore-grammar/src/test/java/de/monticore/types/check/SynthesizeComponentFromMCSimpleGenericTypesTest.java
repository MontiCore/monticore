/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.ImmutableList;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.componentsymbolswithmcbasictypestest.ComponentSymbolsWithMCBasicTypesTestMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SynthesizeComponentFromMCSimpleGenericTypesTest {

  @BeforeAll
  public static void beforeAll() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {
    Log.clearFindings();

    ComponentSymbolsWithMCBasicTypesTestMill.reset();
    ComponentSymbolsWithMCBasicTypesTestMill.init();


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
  /*  assertThat(Log.getFindings()).isEmpty();
    assertThat(result4normalAsGeneric.getSourceNode()).contains(astNormalComp);
    assertThat(result4qualAsGeneric.getSourceNode()).contains(astQualComp);*/
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
   /* assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(MCError.MISSING_COMPONENT));*/
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
  /*  assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(MCError.MISSING_COMPONENT));*/
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
    /*assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(MCError.MISSING_COMPONENT));*/
  }

}
