/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class OOSymbolsSymbols2JsonTest {

  private IOOSymbolsArtifactScope scope;

  @Before
  public void setUp(){
    LogStub.init();
    Log.enableFailQuick(false);

    //initialize scope, add some TypeSymbols, TypeVarSymbols, VariableSymbols and FunctionSymbols
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
    scope = OOSymbolsMill.artifactScope();
    scope.setPackageName("");
    scope.setImportsList(Lists.newArrayList());
    scope.setName("Test");

    IOOSymbolsScope typeSpannedScope = OOSymbolsMill.scope();

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
        .setSpannedScope(OOSymbolsMill.scope())
        .setEnclosingScope(scope)
        .setSuperTypesList(Lists.newArrayList(symType1))
        .build();

    subtype.setSpannedScope(OOSymbolsMill.scope());

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
        .setSpannedScope(OOSymbolsMill.scope())
        .setType(SymTypeExpressionFactory.createTypeConstant("int"))
        .build();

    function.setSpannedScope(OOSymbolsMill.scope());

    typeSpannedScope.add(function);

    scope.add(type);
    scope.add(subtype);
  }

  @Test
  public void testDeSer(){
    performRoundTripSerialization(scope);
  }


  public void performRoundTripSerialization(IOOSymbolsScope scope){
    //first serialize the scope using the deser
    OOSymbolsSymbols2Json s2j = ((OOSymbolsGlobalScope) OOSymbolsMill.globalScope()).getSymbols2Json();
    String serialized = s2j.serialize(scope);
    // then deserialize it
    OOSymbolsSymbols2Json symbols2Json = new OOSymbolsSymbols2Json();
    IOOSymbolsArtifactScope deserialized = symbols2Json.deserialize(serialized);
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
    assertEquals("int", function.get().getType().print());
    assertEquals("int", deserializedFunction.get().getType().print());
  }

  @Test
  public void testSerializedUnknownKind() {
    OOSymbolsSymbols2Json symbols2Json = new OOSymbolsSymbols2Json();
    symbols2Json.deserialize("{\"symbols\": [{\"kind\":\"unknown\", \"name\":\"test\"}]}");
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalidJsonForSerializingReturnsError(){
    String invalidJsonForSerializing = "{\n\t\"symbols\":[{\"noKind\":true}]}\n}";
    String invalidJsonForSerializing2 = "{\"symbols\": [\"SymbolIsNotAnObject\"]}";
    String invalidJsonForSerializing3 = "{\"symbols\": [{\"kind\":\"unknown\"}]}";

    OOSymbolsSymbols2Json symbols2Json = new OOSymbolsSymbols2Json();
    symbols2Json.deserialize(invalidJsonForSerializing);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA1238"));

    symbols2Json.deserialize(invalidJsonForSerializing2);
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xA1233"));

    symbols2Json.deserialize(invalidJsonForSerializing3);
    assertTrue(Log.getFindings().get(2).getMsg().startsWith("0xA0572"));
  }

}
