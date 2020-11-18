/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.oosymbols._symboltable.BuiltInJavaSymbolResolver;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SynthesizeSymTypeFromMCSimpleGenericTypes;
import de.monticore.types.check.TypeCheck;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeConstant;

/**
 * This class manages a static map with known DeSers and captures special serializations for (iterations of) primitive data types.
 */
public class DeSerMap {

  protected static final String LIST_TEMPLATE = "_symboltable.serialization.PrintListAttribute";

  protected static final String OPTIONAL_TEMPLATE = "_symboltable.serialization.PrintOptionalAttribute";

  protected static final String COMPLEX_TEMPLATE = "_symboltable.serialization.PrintComplexAttribute";

  protected static TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMCSimpleGenericTypes(),
      null);

  protected static final Map<SymTypeExpression, String> primitiveDataTypes = new HashMap<>();

  protected static IOOSymbolsScope gs = BuiltInJavaSymbolResolver.getScope();

  static {
    primitiveDataTypes.put(createTypeConstant("boolean"), "getBooleanMember(\"%s\")");
    primitiveDataTypes.put(createObjectType("java.lang.Boolean"), "getBooleanMember(\"%s\")");

    primitiveDataTypes.put(createObjectType("String"), "getStringMember(\"%s\")");
    primitiveDataTypes.put(createObjectType("java.lang.String"), "getStringMember(\"%s\")");

    primitiveDataTypes.put(createTypeConstant("double"), getNumberMember("Double"));
    primitiveDataTypes.put(createObjectType("java.lang.Double"), getNumberMember("Double"));

    primitiveDataTypes.put(createTypeConstant("float"), getNumberMember("Float"));
    primitiveDataTypes.put(createObjectType("java.lang.Float"), getNumberMember("Float"));

    primitiveDataTypes.put(createTypeConstant("int"), getNumberMember("Int"));
    primitiveDataTypes.put(createObjectType("java.lang.Integer"), getNumberMember("Int"));

    primitiveDataTypes.put(createTypeConstant("long"), getNumberMember("Long"));
    primitiveDataTypes.put(createObjectType("java.lang.Long"), getNumberMember("Long"));
  }

  protected static SymTypeExpression createObjectType(String name) {
    OOTypeSymbolSurrogate t = new OOTypeSymbolSurrogate(name);
    t.setEnclosingScope(gs);
    return SymTypeExpressionFactory.createTypeObject(t);
  }

  protected static String getNumberMember(String type) {
    return "getMember(\"%s\").getAsJsonNumber().getNumberAs" + type + "()";
  }

  public static HookPoint getDeserializationImplementation(ASTCDAttribute a, String methodName,
                                                           String jsonName, OOSymbolsScope enclosingScope, String generatedErrorCode) {
    SymTypeExpression actualType = tc.symTypeFromAST(a.getMCType());
    Optional<HookPoint> primitiveTypeOpt = getHookPointForPrimitiveDataType(actualType, a.getName(),
        jsonName, enclosingScope);
    if (primitiveTypeOpt.isPresent()) {
      return primitiveTypeOpt.get();
    }
    //TODO implement check for language-specific DeSers here

    return new TemplateHookPoint(COMPLEX_TEMPLATE, actualType.print(), methodName, "deserialize",
        "return null;", generatedErrorCode);
  }

  protected static Optional<HookPoint> getHookPointForPrimitiveDataType(
      SymTypeExpression actualType,
      String varName, String jsonName, OOSymbolsScope enclosingScope) {
    for (SymTypeExpression e : primitiveDataTypes.keySet()) {
      if (isTypeOf(e, actualType, enclosingScope)) {
        String s =
            "return " + jsonName + "." + String.format(primitiveDataTypes.get(e), varName) + ";";
        return Optional.of(new StringHookPoint(s));
      }
      else if (isOptionalTypeOf(e, actualType, enclosingScope)) {
        String s = String.format(primitiveDataTypes.get(e), varName);
        return Optional.of(new TemplateHookPoint(OPTIONAL_TEMPLATE, varName, jsonName, s));
      }
      else if (isListTypeOf(e, actualType, enclosingScope)) {
        String s = String.format(primitiveDataTypes.get(e), varName);
        return Optional
            .of(new TemplateHookPoint(LIST_TEMPLATE, actualType.print(), varName, jsonName, s));
      }
    }
    return Optional.empty();
  }

  protected static boolean isTypeOf(SymTypeExpression expectedType, SymTypeExpression actualType,
      OOSymbolsScope enclosingScope) {
    //    if (TypeCheck.compatible(expectedType, actualType)) {
    if (expectedType.print().equals(actualType.print())) {
      return true;
    }
    return false;
  }

  protected static boolean isListTypeOf(SymTypeExpression expectedType,
      SymTypeExpression actualType,
      OOSymbolsScope enclosingScope) {
    OOTypeSymbolSurrogate t = new OOTypeSymbolSurrogate("java.util.List");
    t.setEnclosingScope(enclosingScope);
    SymTypeExpression list = SymTypeExpressionFactory
        .createGenerics(t, Lists.newArrayList(expectedType));
    //    if (TypeCheck.compatible(list1, actualType) || TypeCheck.compatible(list2, actualType)) {
    if (list.print().equals(actualType.print())) {
      return true;
    }
    return false;
  }

  protected static boolean isOptionalTypeOf(SymTypeExpression expectedType,
      SymTypeExpression actualType, OOSymbolsScope enclosingScope) {
    OOTypeSymbolSurrogate t = new OOTypeSymbolSurrogate("java.util.Optional");
    t.setEnclosingScope(enclosingScope);
    SymTypeExpression optional = SymTypeExpressionFactory
        .createGenerics(t, Lists.newArrayList(expectedType));
    //    if (TypeCheck.compatible(optional, actualType)) {
    if (optional.print().equals(actualType.print())) {
      return true;
    }
    return false;
  }

}
