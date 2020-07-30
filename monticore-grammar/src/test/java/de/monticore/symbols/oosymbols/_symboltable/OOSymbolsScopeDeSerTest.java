// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class OOSymbolsScopeDeSerTest {

  private IOOSymbolsArtifactScope scope;

  @Before
  public void setUp(){
    LogStub.init();
    Log.enableFailQuick(false);
    //initialize scope, add some TypeSymbols, TypeVarSymbols, VariableSymbols and FunctionSymbols
    scope = OOSymbolsMill.oOSymbolsArtifactScopeBuilder().setPackageName("").setImportsList(Lists.newArrayList()).build();
    scope.setName("Test");

    IOOSymbolsScope typeSpannedScope = OOSymbolsMill.oOSymbolsScopeBuilder().build();

    //put type into main scope
    OOTypeSymbol type = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Type")
        .setSpannedScope(typeSpannedScope)
        .setEnclosingScope(scope)
        .build();

    type.setSpannedScope(typeSpannedScope);

    SymTypeExpression symType1 = SymTypeExpressionFactory.createTypeObject("Type", scope);

    //put subtype into main scope, test if supertypes are serialized correctly
    OOTypeSymbol subtype = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("SubType")
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setEnclosingScope(scope)
        .setSuperTypesList(Lists.newArrayList(symType1))
        .build();

    subtype.setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build());

    //put Variable variable into spanned scope of type
    FieldSymbol variable = OOSymbolsMill.fieldSymbolBuilder()
        .setName("variable")
        .setEnclosingScope(type.getSpannedScope())
        .setType(SymTypeExpressionFactory.createTypeConstant("double"))
        .build();

    typeSpannedScope.add(variable);

    //put Function function into spanned scope of type
    MethodSymbol function = OOSymbolsMill.methodSymbolBuilder()
        .setName("function")
        .setEnclosingScope(type.getSpannedScope())
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setReturnType(SymTypeExpressionFactory.createTypeConstant("int"))
        .build();

    function.setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build());

    typeSpannedScope.add(function);

    scope.add(type);
    scope.add(subtype);
  }

  @Test
  public void testDeSer(){
    performRoundTripSerialization(scope);
  }


  public void performRoundTripSerialization(IOOSymbolsScope scope){
    OOSymbolsScopeDeSer deser = new OOSymbolsScopeDeSer();
    //first serialize the scope using the deser
    String serialized = deser.serialize(scope);
    // then deserialize it
    IOOSymbolsArtifactScope deserialized = deser.deserialize(serialized);
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
    assertEquals(1, subtype.get().getSuperTypesList().size());
    assertEquals(1, deserializedSubType.get().getSuperTypesList().size());
    assertEquals("Type", subtype.get().getSuperTypesList().get(0).print());
    assertEquals("Type", deserializedSubType.get().getSuperTypesList().get(0).print());

    IOOSymbolsScope typeSpanned = type.get().getSpannedScope();
    IOOSymbolsScope deserializedTypeSpanned = deserializedType.get().getSpannedScope();

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

    OOSymbolsScopeDeSer deser = new OOSymbolsScopeDeSer();
    deser.deserialize(invalidJsonForSerializing);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA7224"));

    deser.deserialize(invalidJsonForSerializing2);
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xA7224"));
  }

}
