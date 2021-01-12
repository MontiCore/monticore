/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

/**
 * Serialization Strategy for Built-In Types
 */
public class BITSerStrategy {

  protected static final String PRINT_PLAIN_TEMPLATE = "_symboltable.serialization.PrintPlainAttribute";

  protected static final String PRINT_LIST_TEMPLATE = "_symboltable.serialization.PrintListAttribute";

  protected static final String PRINT_OPT_TEMPLATE = "_symboltable.serialization.PrintOptionalAttribute";

  protected static final String READ_LIST_TEMPLATE = "_symboltable.serialization.ReadListAttribute";

  protected static final String READ_OPT_TEMPLATE = "_symboltable.serialization.ReadOptionalAttribute";

  protected String type;

  protected String defaultValue;

  protected boolean useEquals;

  public BITSerStrategy(String type, String defaultValue) {
    this.type = type;
    this.defaultValue = defaultValue;
    this.useEquals = false;
  }

  public BITSerStrategy(String type, String defaultValue, boolean useEquals) {
    this.type = type;
    this.defaultValue = defaultValue;
    this.useEquals = useEquals;
  }

  public HookPoint getSerialHook(String attrParam) {
    return new TemplateHookPoint(PRINT_PLAIN_TEMPLATE, attrParam, defaultValue, useEquals);
  }

  public HookPoint getOptSerialHook(String attrParam) {
    return new TemplateHookPoint(PRINT_OPT_TEMPLATE, attrParam);
  }

  public HookPoint getListSerialHook(String attrParam) {
    return new TemplateHookPoint(PRINT_LIST_TEMPLATE, attrParam);
  }

  public HookPoint getDeserialHook(String jsonParam, String attrParam) {
    String typeMap = ".get" + type + "MemberOpt(\"" + attrParam + "\").orElse("+defaultValue+");";
    return new StringHookPoint("return " + jsonParam + typeMap);
  }

  public HookPoint getOptDeserialHook(String jsonParam, String attrParam) {
    String typeMap = "get" + type + "Member(\"" + attrParam + "\")";
    return new TemplateHookPoint(READ_OPT_TEMPLATE, jsonParam, attrParam, typeMap);
  }

  public HookPoint getListDeserialHook(String jsonParam, String attrParam) {
    String typeMap;
    if(type.equals("String") || type.equals("Boolean")){
      typeMap = "getAsJson" + type + "().getValue()";
    }else{
      typeMap = "getAsJsonNumber().getNumberAs" + type + "()";
    }
    return new TemplateHookPoint(READ_LIST_TEMPLATE, jsonParam, type, attrParam, typeMap);
  }
}
