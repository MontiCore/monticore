/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;


import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import de.monticore.types.check.KindOfComponent;
import de.monticore.types.check.KindOfComponentDeSer;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ComponentSymbolDeSerTest {
  private static final String SIMPLE_JSON =
    "{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"" +
      "}";

  private static final String JSON_WITH_PARENT =
    "{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"super\":[{\"kind\":\"de.monticore.types.check.KindOfComponent\",\"componentName\":\"Parent\"}]" +
      "}";

  private static final String JSON_WITH_TYPE_PARAMS =
    "{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"spannedScope\":{\"symbols\":[{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol\"," +
      "\"name\":\"A\"," +
      "\"fullName\":\"Comp.A\"" +
      "},{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol\"," +
      "\"name\":\"B\"," +
      "\"fullName\":\"Comp.B\"" +
      "}]" +
      "}}";

  private static final String JSON_WITH_PARAMS =
    "{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"parameters\":[{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\"," +
      "\"name\":\"a\"," +
      "\"fullName\":\"Comp.a\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}" +
      "},{" +
      "\"kind\":\"de.monticore.symbols.basicsymbols._symboltable.VariableSymbol\"," +
      "\"name\":\"b\"," +
      "\"fullName\":\"Comp.b\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}" +
      "}]" +
      "}";

  private static final String JSON_WITH_PORTS =
    "{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"fullName\":\"Comp\"," +
      "\"spannedScope\":{\"symbols\":[{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.PortSymbol\"," +
      "\"name\":\"inc\"," +
      "\"fullName\":\"Comp.inc\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}," +
      "\"incoming\":true," +
      "\"timing\":\"timed\"" +
      "},{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.PortSymbol\"," +
      "\"name\":\"outg\"," +
      "\"fullName\":\"Comp.outg\"," +
      "\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"}," +
      "\"outgoing\":true," +
      "\"timing\":\"timed\"" +
      "}]" +
      "}}";

  private static final String JSON_WITH_SUB =
    "{" +
      "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\"," +
      "\"name\":\"Parent\"," +
      "\"fullName\":\"Parent\"," +
      "\"spannedScope\":{\"symbols\":[{\"kind\":\"de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol\",\"name\":\"inst\",\"fullName\":\"Parent.inst\",\"type\":{\"kind\":\"de.monticore.types.check.KindOfComponent\",\"componentName\":\"Comp\"}}]" +
      "}}";

  protected static final String COMP_TYPE_WITH_SUPER1 = "{" +
    "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\"," +
    "\"name\":\"CompTypeWithSuper1\"," +
    "\"fullName\":\"CompTypeWithSuper1\"," +
    "\"super\":[" +
    "{" +
    "\"kind\":\"de.monticore.types.check.KindOfComponent\"," +
    "\"componentName\":\"SuperCType\"" +
    "}]" +
    "}";

  protected static final String COMP_TYPE_WITH_SUPER2 = "{" +
    "\"kind\":\"de.monticore.symbols.compsymbols._symboltable.ComponentSymbol\"," +
    "\"name\":\"CompTypeWithSuper2\"," +
    "\"fullName\":\"CompTypeWithSuper2\"," +
    "\"super\":[" +
    "{" +
    "\"kind\":\"de.monticore.types.check.KindOfComponent\"," +
    "\"componentName\":\"SuperCType1\"" +
    "}," +
    "{" +
    "\"kind\":\"de.monticore.types.check.KindOfComponent\"," +
    "\"componentName\":\"SuperCType2\"" +
    "}]" +
    "}";

  protected ComponentSymbolDeSer deSer;
  protected CompSymbolsSymbols2Json arc2json;

  @BeforeEach
  void setup() {
    CompSymbolsMill.reset();
    CompSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();

    deSer = new ComponentSymbolDeSer(new FullTestCompKindDeser());
    CompSymbolsMill.globalScope().putSymbolDeSer(deSer.getSerializedKind(), deSer);
    SubcomponentSymbolDeSer subDeSer = new SubcomponentSymbolDeSer(new FullTestCompKindDeser());
    CompSymbolsMill.globalScope().putSymbolDeSer(subDeSer.getSerializedKind(), subDeSer);
    arc2json = new CompSymbolsSymbols2Json();
  }

  @Test
  void shouldSerializeSuperComponentType() throws IOException {
    // Given
    // create a symbol for the super component type
    ComponentSymbol superCType = CompSymbolsMill.componentSymbolBuilder()
      .setName("SuperCType")
      .setSpannedScope(CompSymbolsMill.scope())
      .build();

    // create a reference to the super component type
    CompKindExpression parentType = new KindOfComponent(superCType) {};

    // create a symbol for a component type, reference its super type
    ComponentSymbol cType = CompSymbolsMill.componentSymbolBuilder()
      .setName("CompTypeWithSuper1")
      .setSpannedScope(CompSymbolsMill.scope())
      .addSuperComponents(parentType)
      .build();

    // When
    String actual = deSer.serialize(cType, arc2json);

    // Then
    Assertions.assertEquals(COMP_TYPE_WITH_SUPER1, actual);
  }

  @Test
  void shouldSerializeSuperComponentType2() throws IOException {
    // Given
    // create symbols for the two super component types
    ComponentSymbol superCType1 = CompSymbolsMill.componentSymbolBuilder()
      .setName("SuperCType1")
      .setSpannedScope(CompSymbolsMill.scope())
      .build();
    ComponentSymbol superCType2 = CompSymbolsMill.componentSymbolBuilder()
      .setName("SuperCType2")
      .setSpannedScope(CompSymbolsMill.scope())
      .build();

    // create a reference for to each of the two super component types
    CompKindExpression parentType1 = new KindOfComponent(superCType1);
    CompKindExpression parentType2 = new KindOfComponent(superCType2);

    // symbol for a component type, reference its super types
    ComponentSymbol cType = CompSymbolsMill.componentSymbolBuilder()
      .setName("CompTypeWithSuper2")
      .setSpannedScope(CompSymbolsMill.scope())
      .addSuperComponents(parentType1)
      .addSuperComponents(parentType2)
      .build();

    // When
    String actual = deSer.serialize(cType, arc2json);

    // Then
    Assertions.assertEquals(COMP_TYPE_WITH_SUPER2, actual);
  }

  @Test
  void shouldNotSerializeAbsent() {
    // Given
    ComponentSymbol comp = createSimpleComp();

    // When
    String createdJson = deSer.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(SIMPLE_JSON, createdJson);
  }

  @Test
  void shouldSerializeTypeParameters() {
    // Given
    ComponentSymbol comp = createSimpleComp();
    comp.getSpannedScope().add(
      CompSymbolsMill.typeVarSymbolBuilder()
        .setName("A")
        .setSpannedScope(CompSymbolsMill.scope())
        .build()
    );
    comp.getSpannedScope().add(
      CompSymbolsMill.typeVarSymbolBuilder()
        .setName("B")
        .setSpannedScope(CompSymbolsMill.scope())
        .build()
    );

    // When
    String createdJson = deSer.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_TYPE_PARAMS, createdJson);
  }

  @Test
  void shouldSerializeParameters() {
    // Given
    ComponentSymbol comp = createSimpleComp();
    VariableSymbol paramA = CompSymbolsMill.variableSymbolBuilder()
      .setName("a")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();
    VariableSymbol paramB = CompSymbolsMill.variableSymbolBuilder()
      .setName("b")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();

    comp.getSpannedScope().add(paramA);
    comp.getSpannedScope().add(paramB);
    comp.addParameter(paramA);
    comp.addParameter(paramB);

    // When
    String createdJson = deSer.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_PARAMS, createdJson);
  }

  @Test
  void shouldSerializePorts() {
    // Given
    ComponentSymbol comp = createSimpleComp();
    PortSymbol portIncoming = CompSymbolsMill.portSymbolBuilder()
      .setName("inc")
      .setIncoming(true)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .setTiming(Timing.TIMED)
      .setStronglyCausal(false)
      .build();
    PortSymbol portOutgoing = CompSymbolsMill.portSymbolBuilder()
      .setName("outg")
      .setOutgoing(true)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .setTiming(Timing.TIMED)
      .setStronglyCausal(false)
      .build();

    comp.getSpannedScope().add(portIncoming);
    comp.getSpannedScope().add(portOutgoing);


    // When
    String createdJson = deSer.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_PORTS, createdJson);
  }

  @Test
  void shouldDeserializeParent() {
    // When
    ComponentSymbol comp = deSer.deserialize(JSON_WITH_PARENT);

    // Then
    Assertions.assertFalse(comp.isEmptySuperComponents(), "Parent not present");
    Assertions.assertEquals("Parent", comp.getSuperComponents(0).printName());
  }

  @Test
  void shouldNotDeserializeAbsentParent() {
    // When
    ComponentSymbol comp = deSer.deserialize(SIMPLE_JSON);

    // Then
    Assertions.assertTrue(comp.isEmptySuperComponents(), "Parent is present");
  }

  @Test
  void shouldDeserializeTypeParameters() {
    // When
    ComponentSymbol comp = deSer.deserialize(JSON_WITH_TYPE_PARAMS);

    // Then
    Assertions.assertEquals(2, comp.getTypeParameters().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("A", comp.getTypeParameters().get(0).getName()),
      () -> Assertions.assertEquals("B", comp.getTypeParameters().get(1).getName())
    );
  }

  @Test
  void shouldDeserializeParameters() {
    // When
    ComponentSymbol comp = deSer.deserialize(JSON_WITH_PARAMS);

    // Then
    Assertions.assertEquals(2, comp.getParameterList().size());
    Assertions.assertEquals(2, comp.getSpannedScope().getLocalVariableSymbols().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("a", comp.getParameterList().get(0).getName()),
      () -> Assertions.assertEquals("b", comp.getParameterList().get(1).getName()),
      () -> Assertions.assertTrue(comp.getSpannedScope().resolveVariable("a").isPresent()),
      () -> Assertions.assertTrue(comp.getSpannedScope().resolveVariable("b").isPresent()),
      () -> Assertions.assertEquals(comp.getSpannedScope().resolveVariable("a").get(), comp.getParameterList().get(0)),
      () -> Assertions.assertEquals(comp.getSpannedScope().resolveVariable("b").get(), comp.getParameterList().get(1))
    );
  }

  @Test
  void shouldDeserializePorts() {
    // When
    ComponentSymbol comp = deSer.deserialize(JSON_WITH_PORTS);

    // Then
    Assertions.assertEquals(2, comp.getPorts().size());
    Assertions.assertEquals(2, comp.getSpannedScope().getLocalPortSymbols().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inc", comp.getPorts().get(0).getName()),
      () -> Assertions.assertEquals("outg", comp.getPorts().get(1).getName()),
      () -> Assertions.assertTrue(comp.getSpannedScope().resolvePort("inc").isPresent()),
      () -> Assertions.assertTrue(comp.getSpannedScope().resolvePort("outg").isPresent()),
      () -> Assertions.assertEquals(comp.getSpannedScope().resolvePort("inc").get(), comp.getPorts().get(0)),
      () -> Assertions.assertEquals(comp.getSpannedScope().resolvePort("outg").get(), comp.getPorts().get(1))
    );
  }

  @Test
  void shouldSerializeSubComponents() {
    // Given
    ComponentSymbol comp = createParentComp();
    comp.getSpannedScope().add(
      CompSymbolsMill.subcomponentSymbolBuilder()
        .setName("inst")
        .setType(new KindOfComponent(createSimpleComp()))
        .build()
    );

    // When
    String createdJson = deSer.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_SUB, createdJson);
  }

  @Test
  void shouldDeserializeSubComponents() {
    // When
    ComponentSymbol comp = deSer.deserialize(JSON_WITH_SUB);

    // Then
    Assertions.assertEquals(1, comp.getSubcomponents().size());
    Assertions.assertEquals(1, comp.getSpannedScope().getLocalSubcomponentSymbols().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("inst", comp.getSubcomponents().get(0).getName()),
      () -> Assertions.assertTrue(comp.getSpannedScope().resolveSubcomponent("inst").isPresent()),
      () -> Assertions.assertEquals(comp.getSpannedScope().resolveSubcomponent("inst").get(), comp.getSubcomponents().get(0))
    );
  }

  protected static ComponentSymbol createSimpleComp() {
    return CompSymbolsMill.componentSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(CompSymbolsMill.scope())
      .build();
  }

  protected static ComponentSymbol createParentComp() {
    return CompSymbolsMill.componentSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(CompSymbolsMill.scope())
      .build();
  }

  static class FullTestCompKindDeser implements FullCompKindExprDeSer {
    KindOfComponentDeSer kindOfComponentDeSer = new KindOfComponentDeSer();

    @Override
    public String serializeAsJson(@NonNull CompKindExpression toSerialize) {
      return kindOfComponentDeSer.serializeAsJson((KindOfComponent) toSerialize);
    }

    @Override
    public CompKindExpression deserialize(@NonNull JsonElement serialized) {
      return kindOfComponentDeSer.deserialize(serialized.getAsJsonObject());
    }
  }
}
