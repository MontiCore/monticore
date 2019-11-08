/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.check.*;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;

import java.util.*;

/**
 * TODO
 *
 * @author (last commit)
 * @version , 06.11.2019
 * @since TODO
 */
public class DeSerMap {

  protected static final String LIST_TEMPLATE = "_symboltable.serialization.PrintListAttribute";

  protected static final String OPTIONAL_TEMPLATE = "_symboltable.serialization.PrintOptionalAttribute";

  protected static final String COMPLEX_TEMPLATE = "_symboltable.serialization.PrintComplexAttribute";

  protected static TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMCSimpleGenericTypes(),
      null);

  protected static final Map<SymTypeExpression, String> primitiveDataTypes = new HashMap<>();

  static {
    primitiveDataTypes
        .put(SymTypeExpressionFactory.createTypeConstant("boolean"), "getBooleanMember(\"%s\")");
    primitiveDataTypes
        .put(SymTypeExpressionFactory.createTypeObject("Boolean"), "getBooleanMember(\"%s\")");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("java.lang.Boolean"),
        "getBooleanMember(\"%s\")");

    primitiveDataTypes
        .put(SymTypeExpressionFactory.createTypeObject("String"), "getStringMember(\"%s\")");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("java.lang.String"),
        "getStringMember(\"%s\")");

    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeConstant("double"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsDouble()");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("Double"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsDouble()");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("java.lang.Double"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsDouble()");

    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeConstant("float"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsFloat()");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("Float"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsFloat()");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("java.lang.Float"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsFloat()");

    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeConstant("int"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsInt()");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("Integer"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsInt()");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("java.lang.Integer"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsInt()");

    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeConstant("long"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsLong()");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("Long"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsLong()");
    primitiveDataTypes.put(SymTypeExpressionFactory.createTypeObject("java.lang.Long"),
        "getMember(\"%s\").getAsJsonNumber().getNumberAsLong()");
  }

  public static HookPoint getDeserializationImplementation(ASTCDAttribute a, String methodName,
      String jsonName, TypeSymbolsScope enclosingScope) {
    SymTypeExpression actualType = tc.symTypeFromAST(a.getMCType());
    Optional<HookPoint> primitiveTypeOpt = getHookPointForPrimitiveDataType(actualType, a.getName(),
        jsonName, enclosingScope);
    if (primitiveTypeOpt.isPresent()) {
      return primitiveTypeOpt.get();
    }
    //TODO implement check for language-specific DeSers here
    return new TemplateHookPoint(COMPLEX_TEMPLATE, actualType.print(), methodName, "deserialize",
        "null");
  }

  protected static Optional<HookPoint> getHookPointForPrimitiveDataType(
      SymTypeExpression actualType,
      String varName, String jsonName, TypeSymbolsScope enclosingScope) {
    for (SymTypeExpression e : primitiveDataTypes.keySet()) {
      if (isTypeOf(e, actualType, enclosingScope)) {
        String s = String.format(primitiveDataTypes.get(e), varName);
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
      TypeSymbolsScope enclosingScope) {
//    if (TypeCheck.compatible(expectedType, actualType)) {
    if (expectedType.print().equals(actualType.print())) {
      return true;
    }
    return false;
  }

  protected static boolean isListTypeOf(SymTypeExpression expectedType, SymTypeExpression actualType,
      TypeSymbolsScope enclosingScope) {
    SymTypeExpression list1 = SymTypeExpressionFactory
        .createGenerics("List", Lists.newArrayList(expectedType), enclosingScope);
    SymTypeExpression list2 = SymTypeExpressionFactory
        .createGenerics("java.util.List", Lists.newArrayList(expectedType), enclosingScope);
//    if (TypeCheck.compatible(list1, actualType) || TypeCheck.compatible(list2, actualType)) {
    if (list1.print().equals(actualType.print()) || list2.print().equals(actualType.print())) {
      return true;
    }
    return false;
  }

  protected static boolean isOptionalTypeOf(SymTypeExpression expectedType,
      SymTypeExpression actualType,
      TypeSymbolsScope enclosingScope) {
    SymTypeExpression optional1 = SymTypeExpressionFactory
        .createGenerics("Optional", Lists.newArrayList(expectedType), enclosingScope);
    SymTypeExpression optional2 = SymTypeExpressionFactory
        .createGenerics("java.util.Optional", Lists.newArrayList(expectedType), enclosingScope);
//    if (TypeCheck.compatible(optional1, actualType) || TypeCheck
//        .compatible(optional2, actualType)) {
    if (optional1.print().equals(actualType.print()) || optional2.print().equals(actualType.print())) {
      return true;
    }
    return false;
  }

}
