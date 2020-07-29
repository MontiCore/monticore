// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;


import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class BasicSymbolsScopeDeSerTest {

  private IBasicSymbolsArtifactScope scope;

  @Before
  public void setUp(){
    LogStub.init();
    Log.enableFailQuick(false);
    //initialize scope, add some TypeSymbols, TypeVarSymbols, VariableSymbols and FunctionSymbols
    scope = BasicSymbolsMill.basicSymbolsArtifactScopeBuilder().setPackageName("").setImportsList(Lists.newArrayList()).build();
    scope.setName("Test");

    IBasicSymbolsScope typeSpannedScope = BasicSymbolsMill.basicSymbolsScopeBuilder().build();

    //put type into main scope
    TypeSymbol type = BasicSymbolsMill.typeSymbolBuilder()
        .setName("Type")
        .setSpannedScope(typeSpannedScope)
        .setEnclosingScope(scope)
        .build();

    type.setSpannedScope(typeSpannedScope);

    //TODO ND: replace null with scope when abstract TypeCheck is implemented
    SymTypeExpression symType1 = SymTypeExpressionFactory.createTypeObject("Type", null);

    //put subtype into main scope, test if supertypes are serialized correctly
    TypeSymbol subtype = BasicSymbolsMill.typeSymbolBuilder()
        .setName("SubType")
        .setSpannedScope(BasicSymbolsMill.basicSymbolsScopeBuilder().build())
        .setEnclosingScope(scope)
        .setSuperTypesList(Lists.newArrayList(symType1))
        .build();

    subtype.setSpannedScope(BasicSymbolsMill.basicSymbolsScopeBuilder().build());

    //put TypeVariable T into spanned scope of type
    TypeVarSymbol t = BasicSymbolsMill.typeVarSymbolBuilder()
        .setName("T")
        .setEnclosingScope(type.getSpannedScope())
        .build();

    t.setSpannedScope(BasicSymbolsMill.basicSymbolsScopeBuilder().build());

    typeSpannedScope.add(t);

    //put Variable variable into spanned scope of type
    VariableSymbol variable = BasicSymbolsMill.variableSymbolBuilder()
        .setName("variable")
        .setEnclosingScope(type.getSpannedScope())
        .setType(SymTypeExpressionFactory.createTypeConstant("double"))
        .build();

    typeSpannedScope.add(variable);

    //put Function function into spanned scope of type
    FunctionSymbol function = BasicSymbolsMill.functionSymbolBuilder()
        .setName("function")
        .setEnclosingScope(type.getSpannedScope())
        .setSpannedScope(BasicSymbolsMill.basicSymbolsScopeBuilder().build())
        .setReturnType(SymTypeExpressionFactory.createTypeConstant("int"))
        .build();

    function.setSpannedScope(BasicSymbolsMill.basicSymbolsScopeBuilder().build());

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

  public void performRoundTripSerialization(IBasicSymbolsScope scope){
    BasicSymbolsScopeDeSer deser = new BasicSymbolsScopeDeSer();
    //first serialize the scope using the deser
    String serialized = deser.serialize(scope);
    // then deserialize it
    IBasicSymbolsArtifactScope deserialized = deser.deserialize(serialized);
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

    IBasicSymbolsScope typeSpanned = type.get().getSpannedScope();
    IBasicSymbolsScope deserTypeSpanned = deserializedType.get().getSpannedScope();

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

    BasicSymbolsScopeDeSer deser = new BasicSymbolsScopeDeSer();
    deser.deserialize(invalidJsonForSerializing);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA7224"));

    deser.deserialize(invalidJsonForSerializing2);
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xA7224"));
  }


}
