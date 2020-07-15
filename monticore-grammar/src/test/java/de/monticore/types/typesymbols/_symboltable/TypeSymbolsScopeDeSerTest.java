/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.types.check.*;
import de.monticore.types.typesymbols.TypeSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class TypeSymbolsScopeDeSerTest {

  private TypeSymbolsScope scope;

  @Before
  public void setUp(){
    LogStub.init();
    Log.enableFailQuick(false);
    //initialize scope, add some TypeSymbols, TypeVarSymbols, VariableSymbols and FunctionSymbols
    scope = TypeSymbolsMill.typeSymbolsArtifactScopeBuilder().setPackageName("").setImportList(Lists.newArrayList()).build();
    scope.setName("Test");

    TypeSymbolsScope typeSpannedScope = TypeSymbolsMill.typeSymbolsScopeBuilder().build();

    //put type into main scope
    OOTypeSymbol type = TypeSymbolsMill.oOTypeSymbolBuilder()
        .setName("Type")
        .setSpannedScope(typeSpannedScope)
        .setEnclosingScope(scope)
        .build();

    type.setSpannedScope(typeSpannedScope);

    SymTypeExpression symType1 = SymTypeExpressionFactory.createTypeObject("Type", scope);

    //put subtype into main scope, test if supertypes are serialized correctly
    OOTypeSymbol subtype = TypeSymbolsMill.oOTypeSymbolBuilder()
        .setName("SubType")
        .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
        .setEnclosingScope(scope)
        .setSuperTypeList(Lists.newArrayList(symType1))
        .build();

    subtype.setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build());

    //put Variable variable into spanned scope of type
    FieldSymbol variable = TypeSymbolsMill.fieldSymbolBuilder()
        .setName("variable")
        .setEnclosingScope(type.getSpannedScope())
        .setType(SymTypeExpressionFactory.createTypeConstant("double"))
        .build();

    typeSpannedScope.add(variable);

    //put Function function into spanned scope of type
    MethodSymbol function = TypeSymbolsMill.methodSymbolBuilder()
        .setName("function")
        .setEnclosingScope(type.getSpannedScope())
        .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
        .setReturnType(SymTypeExpressionFactory.createTypeConstant("int"))
        .build();

    function.setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build());

    typeSpannedScope.add(function);

    scope.add(type);
    scope.add(subtype);
  }

  @Test
  public void testDeSer(){
    performRoundTripSerialization(scope);
  }


  public void performRoundTripSerialization(TypeSymbolsScope scope){
    TypeSymbolsScopeDeSer deser = new TypeSymbolsScopeDeSer();
    //first serialize the scope using the deser
    String serialized = deser.serialize(scope);
    // then deserialize it
    TypeSymbolsScope deserialized = deser.deserialize(serialized);
    assertNotNull(deserialized);
    // and assert that the deserialized scope equals the one before

    Optional<OOTypeSymbol> type = scope.resolveOOType("Type");
    Optional<OOTypeSymbol> deserializedType = deserialized.resolveOOType("Type");
    assertTrue(type.isPresent());
    assertTrue(deserializedType.isPresent());

    //check that both can resolve the type "SubType" with the supertype "Type"
    Optional<OOTypeSymbol> subtype = scope.resolveOOType("SubType");
    Optional<OOTypeSymbol> deserializedSubType = deserialized.resolveOOType("SubType");
    assertTrue(subtype.isPresent());
    assertTrue(deserializedSubType.isPresent());
    assertEquals(1, subtype.get().getSuperTypeList().size());
    assertEquals(1, deserializedSubType.get().getSuperTypeList().size());
    assertEquals("Type", subtype.get().getSuperTypeList().get(0).print());
    assertEquals("Type", deserializedSubType.get().getSuperTypeList().get(0).print());

    ITypeSymbolsScope typeSpanned = type.get().getSpannedScope();
    ITypeSymbolsScope deserializedTypeSpanned = deserializedType.get().getSpannedScope();

    //check for Variable variable in Type
    Optional<FieldSymbol> variable = typeSpanned.resolveField("variable");
    Optional<FieldSymbol> deserializedVariable = deserializedTypeSpanned.resolveField("variable");
    assertTrue(variable.isPresent());
    assertTrue(deserializedVariable.isPresent());
    assertEquals("double", variable.get().getType().print());
    assertEquals("double", deserializedVariable.get().getType().print());

    //check for Function function in Type
    Optional<MethodSymbol> function = typeSpanned.resolveMethod("function");
    Optional<MethodSymbol> deserializedFunction = deserializedTypeSpanned.resolveMethod("function");
    assertTrue(function.isPresent());
    assertTrue(deserializedFunction.isPresent());
    assertEquals("int", function.get().getReturnType().print());
    assertEquals("int", deserializedFunction.get().getReturnType().print());

    //TODO: check for equality
  }


  @Test
  public void testInvalidJsonForSerializingReturnsError(){
    String invalidJsonForSerializing = "{\n\t\"Foo\":\"bar\"\n}";
    String invalidJsonForSerializing2 = "{\n\t\"symTypeExpression\": {\n\t\t\"foo\":\"bar\", \n\t\t\"foo2\":\"bar2\"\n\t}\n}";

    TypeSymbolsScopeDeSer deser = new TypeSymbolsScopeDeSer();
    deser.deserialize(invalidJsonForSerializing);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA7224"));

    deser.deserialize(invalidJsonForSerializing2);
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xA7224"));
  }

}
