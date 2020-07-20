/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.basictypesymbols._symboltable;


import com.google.common.collect.Lists;
import de.monticore.types.basictypesymbols.BasicTypeSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class BasicTypeSymbolsScopeDeSerTest {

  private BasicTypeSymbolsScope scope;

  @Before
  public void setUp(){
    LogStub.init();
    Log.enableFailQuick(false);
    //initialize scope, add some TypeSymbols, TypeVarSymbols, VariableSymbols and FunctionSymbols
    scope = BasicTypeSymbolsMill.basicTypeSymbolsArtifactScopeBuilder().setPackageName("").setImportsList(Lists.newArrayList()).build();
    scope.setName("Test");

    BasicTypeSymbolsScope typeSpannedScope = BasicTypeSymbolsMill.basicTypeSymbolsScopeBuilder().build();

    //put type into main scope
    TypeSymbol type = BasicTypeSymbolsMill.typeSymbolBuilder()
        .setName("Type")
        .setSpannedScope(typeSpannedScope)
        .setEnclosingScope(scope)
        .build();

    type.setSpannedScope(typeSpannedScope);

    //TODO ND: replace null with scope when abstract TypeCheck is implemented
    SymTypeExpression symType1 = SymTypeExpressionFactory.createTypeObject("Type", null);

    //put subtype into main scope, test if supertypes are serialized correctly
    TypeSymbol subtype = BasicTypeSymbolsMill.typeSymbolBuilder()
        .setName("SubType")
        .setSpannedScope(BasicTypeSymbolsMill.basicTypeSymbolsScopeBuilder().build())
        .setEnclosingScope(scope)
        .setSuperTypesList(Lists.newArrayList(symType1))
        .build();

    subtype.setSpannedScope(BasicTypeSymbolsMill.basicTypeSymbolsScopeBuilder().build());

    //put TypeVariable T into spanned scope of type
    TypeVarSymbol t = BasicTypeSymbolsMill.typeVarSymbolBuilder()
        .setName("T")
        .setEnclosingScope(type.getSpannedScope())
        .build();

    t.setSpannedScope(BasicTypeSymbolsMill.basicTypeSymbolsScopeBuilder().build());

    typeSpannedScope.add(t);

    //put Variable variable into spanned scope of type
    VariableSymbol variable = BasicTypeSymbolsMill.variableSymbolBuilder()
        .setName("variable")
        .setEnclosingScope(type.getSpannedScope())
        .setType(SymTypeExpressionFactory.createTypeConstant("double"))
        .build();

    typeSpannedScope.add(variable);

    //put Function function into spanned scope of type
    FunctionSymbol function = BasicTypeSymbolsMill.functionSymbolBuilder()
        .setName("function")
        .setEnclosingScope(type.getSpannedScope())
        .setSpannedScope(BasicTypeSymbolsMill.basicTypeSymbolsScopeBuilder().build())
        .setReturnType(SymTypeExpressionFactory.createTypeConstant("int"))
        .build();

    function.setSpannedScope(BasicTypeSymbolsMill.basicTypeSymbolsScopeBuilder().build());

    typeSpannedScope.add(function);

    scope.add(type);
    scope.add(subtype);
  }

  @Ignore
  @Test
  public void testDeSer(){
    //TODO ND: unignore when SymTypeExpressions use correct TypeSymbols
    performRoundTripSerialization(scope);
  }

  public void performRoundTripSerialization(BasicTypeSymbolsScope scope){
    BasicTypeSymbolsScopeDeSer deser = new BasicTypeSymbolsScopeDeSer();
    //first serialize the scope using the deser
    String serialized = deser.serialize(scope);
    // then deserialize it
    BasicTypeSymbolsScope deserialized = deser.deserialize(serialized);
    assertNotNull(deserialized);
    // and assert that the deserialized scope equals the one before
    //check that both can resolve the type "Type"

    Optional<TypeSymbol> type = scope.resolveType("Type");
    Optional<TypeSymbol> deserializedType = deserialized.resolveType("Type");
    assertTrue(type.isPresent());
    assertTrue(deserializedType.isPresent());

    //check that both can resolve the type "SubType" with the supertype "Type"
    Optional<TypeSymbol> subtype = scope.resolveType("SubType");
    Optional<TypeSymbol> deserializedSubType = deserialized.resolveType("SubType");
    assertTrue(subtype.isPresent());
    assertTrue(deserializedSubType.isPresent());
    assertEquals(1, subtype.get().getSuperTypesList().size());
    assertEquals(1, deserializedSubType.get().getSuperTypesList().size());
    assertEquals("Type", subtype.get().getSuperTypesList().get(0).print());
    assertEquals("Type", deserializedSubType.get().getSuperTypesList().get(0).print());

    IBasicTypeSymbolsScope typeSpanned = type.get().getSpannedScope();
    IBasicTypeSymbolsScope deserTypeSpanned = deserializedType.get().getSpannedScope();

    //check that both can resolve the TypeVariable "T" in their "Type"
    assertTrue(typeSpanned.resolveTypeVar("T").isPresent());
    assertTrue(deserTypeSpanned.resolveTypeVar("T").isPresent());

    //check for Variable variable in Type
    Optional<VariableSymbol> variable = typeSpanned.resolveVariable("variable");
    Optional<VariableSymbol> deserializedVariable = deserTypeSpanned.resolveVariable("variable");
    assertTrue(variable.isPresent());
    assertTrue(deserializedVariable.isPresent());
    assertEquals("double", variable.get().getType().print());
    assertEquals("double", deserializedVariable.get().getType().print());

    //check for Function function in Type
    Optional<FunctionSymbol> function = typeSpanned.resolveFunction("function");
    Optional<FunctionSymbol> deserializedFunction = deserTypeSpanned.resolveFunction("function");
    assertTrue(function.isPresent());
    assertTrue(deserializedFunction.isPresent());
    assertEquals("int", function.get().getReturnType().print());
    assertEquals("int", deserializedFunction.get().getReturnType().print());
  }

  @Test
  public void testInvalidJsonForSerializingReturnsError(){
    String invalidJsonForSerializing = "{\n\t\"Foo\":\"bar\"\n}";
    String invalidJsonForSerializing2 = "{\n\t\"symTypeExpression\": {\n\t\t\"foo\":\"bar\", \n\t\t\"foo2\":\"bar2\"\n\t}\n}";

    BasicTypeSymbolsScopeDeSer deser = new BasicTypeSymbolsScopeDeSer();
    deser.deserialize(invalidJsonForSerializing);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA7224"));

    deser.deserialize(invalidJsonForSerializing2);
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xA7224"));
  }


}
