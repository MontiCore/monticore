/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

/**
 * Serialization Strategy for Built-In Types
 */
public class BITSerStrategy {

  protected static final String PRINT_LIST_TEMPLATE = "_symboltable.serialization.PrintListAttribute";

  protected static final String PRINT_OPT_TEMPLATE = "_symboltable.serialization.PrintOptionalAttribute";

  protected static final String READ_LIST_TEMPLATE = "_symboltable.serialization.ReadListAttribute";

  protected static final String READ_OPT_TEMPLATE = "_symboltable.serialization.ReadOptionalAttribute";

  protected String type;

  public BITSerStrategy(String type) {
    this.type = type;
  }

  public HookPoint getSerialHook(String attrParam) {
    return new StringHookPoint("return printer.value(" + attrParam + ");");
  }

  public HookPoint getOptSerialHook(String attrParam) {
    return new TemplateHookPoint(PRINT_OPT_TEMPLATE, attrParam);
  }

  public HookPoint getListSerialHook(String attrParam) {
    return new TemplateHookPoint(PRINT_LIST_TEMPLATE, attrParam);
  }

  public HookPoint getDeserialHook(String jsonParam, String attrParam) {
    String typeMap = ".get" + type + "Member(\"" + attrParam + "\");";
    return new StringHookPoint("return " + jsonParam + typeMap);
  }

  public HookPoint getOptDeserialHook(String jsonParam, String attrParam) {
    String typeMap = "get" + type + "Member(" + attrParam + ")";
    return new TemplateHookPoint(READ_OPT_TEMPLATE, jsonParam, attrParam, typeMap);
  }

  public HookPoint getListDeserialHook(String jsonParam, String attrParam) {
    String typeMap = "get" + type + "Member(" + attrParam + ")";
    return new TemplateHookPoint(READ_LIST_TEMPLATE, jsonParam, type, attrParam, typeMap);
  }
}
