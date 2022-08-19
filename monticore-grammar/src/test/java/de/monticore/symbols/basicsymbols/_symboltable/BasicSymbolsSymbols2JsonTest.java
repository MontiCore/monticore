/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;


import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class BasicSymbolsSymbols2JsonTest {

  private IBasicSymbolsArtifactScope scope;
  
  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp(){
    LogStub.getFindings().clear();

    //initialize scope, add some TypeSymbols, TypeVarSymbols, VariableSymbols and FunctionSymbols
    BasicSymbolsMill.reset();
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();

    scope = BasicSymbolsMill.artifactScope();
    scope.setPackageName("");
    scope.setImportsList(Lists.newArrayList());
    scope.setName("Test");

    IBasicSymbolsScope typeSpannedScope = BasicSymbolsMill.scope();

    //put type into main scope
    TypeSymbol type = BasicSymbolsMill.typeSymbolBuilder()
        .setName("Type")
        .setSpannedScope(typeSpannedScope)
        .setEnclosingScope(scope)
        .build();

    SymTypeExpression symType1 = SymTypeExpressionFactory.createTypeObject("Type", scope);

    //put subtype into main scope, test if supertypes are serialized correctly
    TypeSymbol subtype = BasicSymbolsMill.typeSymbolBuilder()
        .setName("SubType")
        .setSpannedScope(BasicSymbolsMill.scope())
        .setEnclosingScope(scope)
        .setSuperTypesList(Lists.newArrayList(symType1))
        .build();

    //put TypeVariable T into spanned scope of type
    TypeVarSymbol t = BasicSymbolsMill.typeVarSymbolBuilder()
        .setName("T")
        .setEnclosingScope(type.getSpannedScope())
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();

    typeSpannedScope.add(t);

    //put Variable variable into spanned scope of type
    VariableSymbol variable = BasicSymbolsMill.variableSymbolBuilder()
        .setName("variable")
        .setEnclosingScope(type.getSpannedScope())
        .setType(SymTypeExpressionFactory.createPrimitive("double"))
        .build();

    typeSpannedScope.add(variable);

    //put Function function into spanned scope of type
    FunctionSymbol function = BasicSymbolsMill.functionSymbolBuilder()
        .setName("function")
        .setEnclosingScope(type.getSpannedScope())
        .setSpannedScope(BasicSymbolsMill.scope())
        .setType(SymTypeExpressionFactory.createPrimitive("int"))
        .build();

    function.setSpannedScope(BasicSymbolsMill.scope());

    typeSpannedScope.add(function);

    scope.add(type);
    scope.add(subtype);
  }

  @Test
  public void testDeSer(){
    performRoundTripSerialization(scope);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  public void performRoundTripSerialization(IBasicSymbolsArtifactScope scope){
    //first serialize the scope using the symbols2json
    BasicSymbolsSymbols2Json symbols2Json = new BasicSymbolsSymbols2Json();
    String serialized = symbols2Json.serialize(scope);
    // then deserialize it
    IBasicSymbolsArtifactScope deserialized = symbols2Json.deserialize(serialized);
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
    assertEquals("int", function.get().getType().print());
    assertEquals("int", deserializedFunction.get().getType().print());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSerializedUnknownKind() {
    BasicSymbolsSymbols2Json symbols2Json = new BasicSymbolsSymbols2Json();
    symbols2Json.deserialize("{\"symbols\": [{\"kind\":\"unknown\", \"name\":\"test\"}]}");
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalidJsonForSerializingReturnsError(){
    String invalidJsonForSerializing = "{\n\t\"symbols\":[{\"noKind\":true}]}\n}";
    String invalidJsonForSerializing2 = "{\"symbols\": [\"SymbolIsNotAnObject\"]}";
    String invalidJsonForSerializing3 = "{\"symbols\": [{\"kind\":\"unknown\"}]}";

    BasicSymbolsSymbols2Json symbols2Json = new BasicSymbolsSymbols2Json();
    symbols2Json.deserialize(invalidJsonForSerializing);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA1238"));

    symbols2Json.deserialize(invalidJsonForSerializing2);
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xA1233"));

    symbols2Json.deserialize(invalidJsonForSerializing3);
    assertTrue(Log.getFindings().get(2).getMsg().startsWith("0xA0572"));
  }


}
