/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * This class manages a static map with known DeSers and captures special serializations for (iterations of) primitive data types.
 */
public class DeSerMap {

  protected static final String LIST_TEMPLATE = "_symboltable.serialization.PrintListAttribute";

  protected static final String OPTIONAL_TEMPLATE = "_symboltable.serialization.PrintOptionalAttribute";

  protected static final String COMPLEX_TEMPLATE = "_symboltable.serialization.PrintComplexAttribute";

  protected static final Map<String, String> primitiveDataTypes = new HashMap<>();

  static {
    primitiveDataTypes.put("boolean", "getBooleanMember(\"%s\")");
    primitiveDataTypes.put("java.lang.Boolean", "getBooleanMember(\"%s\")");
    primitiveDataTypes.put("Boolean", "getBooleanMember(\"%s\")");

    primitiveDataTypes.put("String", "getStringMember(\"%s\")");
    primitiveDataTypes.put("java.lang.String", "getStringMember(\"%s\")");

    primitiveDataTypes.put("double", getNumberMember("Double"));
    primitiveDataTypes.put("java.lang.Double", getNumberMember("Double"));
    primitiveDataTypes.put("Double", getNumberMember("Double"));

    primitiveDataTypes.put("float", getNumberMember("Float"));
    primitiveDataTypes.put("java.lang.Float", getNumberMember("Float"));
    primitiveDataTypes.put("Float", getNumberMember("Float"));

    primitiveDataTypes.put("int", getNumberMember("Int"));
    primitiveDataTypes.put("java.lang.Integer", getNumberMember("Int"));
    primitiveDataTypes.put("Integer", getNumberMember("Int"));

    primitiveDataTypes.put("long", getNumberMember("Long"));
    primitiveDataTypes.put("java.lang.Long", getNumberMember("Long"));
    primitiveDataTypes.put("Long", getNumberMember("Long"));
  }

  protected static String getNumberMember(String type) {
    return "getMember(\"%s\").getAsJsonNumber().getNumberAs" + type + "()";
  }

  public static HookPoint getDeserializationImplementation(ASTCDAttribute a, String methodName,
                                                           String jsonName, String generatedErrorCode) {
    String type = a.getMCType().printType(MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter());
    Optional<HookPoint> primitiveTypeOpt = getHookPointForPrimitiveDataType(type, a.getName(),
        jsonName);
    if (primitiveTypeOpt.isPresent()) {
      return primitiveTypeOpt.get();
    }
    //TODO implement check for language-specific DeSers here

    return new TemplateHookPoint(COMPLEX_TEMPLATE, type, methodName, "deserialize",
        "return null;", generatedErrorCode);
  }

  protected static Optional<HookPoint> getHookPointForPrimitiveDataType(
      String actualType,
      String varName, String jsonName) {
    for (String e : primitiveDataTypes.keySet()) {
      if (isTypeOf(e, actualType)) {
        String s =
            "return " + jsonName + "." + String.format(primitiveDataTypes.get(e), varName) + ";";
        return Optional.of(new StringHookPoint(s));
      }
      else if (isOptionalTypeOf(e, actualType)) {
        String s = String.format(primitiveDataTypes.get(e), varName);
        return Optional.of(new TemplateHookPoint(OPTIONAL_TEMPLATE, varName, jsonName, s));
      }
      else if (isListTypeOf(e, actualType)) {
        String s = String.format(primitiveDataTypes.get(e), varName);
        return Optional
            .of(new TemplateHookPoint(LIST_TEMPLATE, actualType, varName, jsonName, s));
      }
    }
    return Optional.empty();
  }

  protected static boolean isTypeOf(String expectedType, String actualType) {
    if (expectedType.equals(actualType)) {
      return true;
    }
    return false;
  }

  protected static boolean isListTypeOf(String expectedType,
      String actualType) {
    String t = "java.util.List<"+expectedType+">";
    String t2 = "List<"+expectedType+">";
    if (t.equals(actualType) || t2.equals(actualType)) {
      return true;
    }
    return false;
  }

  protected static boolean isOptionalTypeOf(String expectedType,
      String actualType) {
    String t = "java.util.Optional<"+expectedType+">";
    String t2 = "Optional<"+expectedType+">";
    if (t.equals(actualType) || t2.equals(actualType)) {
      return true;
    }
    return false;
  }

}
