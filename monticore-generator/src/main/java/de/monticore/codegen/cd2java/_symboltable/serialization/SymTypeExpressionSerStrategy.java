/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.check.SymTypeExpression;

/**
 * DeSerialization of {@link SymTypeExpression} symbol attributes.
 */
public class SymTypeExpressionSerStrategy extends BITSerStrategy {
  public SymTypeExpressionSerStrategy() {
    super(SymTypeExpression.class.getName(), "null");
  }

  @Override
  public HookPoint getSerialHook(String attrParam) {
    return new StringHookPoint(String.format("de.monticore.types.check.SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), \"%s\", %s);", attrParam, attrParam));
  }

  @Override
  public HookPoint getOptSerialHook(String attrParam) {
    return new StringHookPoint(String.format("de.monticore.types.check.SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), \"%s\", %s);", attrParam, attrParam));
  }

  @Override
  public HookPoint getListSerialHook(String attrParam) {
    return new StringHookPoint(String.format("de.monticore.types.check.SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), \"%s\", %s);", attrParam, attrParam));
  }

  @Override
  public HookPoint getDeserialHook(String jsonParam, String attrParam, String scopeName) {
    return new StringHookPoint(String.format("return de.monticore.types.check.SymTypeExpressionDeSer.deserializeMember(\"%s\", %s, %s);", attrParam, jsonParam, scopeName));
  }

  @Override
  public HookPoint getOptDeserialHook(String jsonParam, String attrParam, String scopeName) {
    return new StringHookPoint(String.format("return de.monticore.types.check.SymTypeExpressionDeSer.deserializeOptionalMember(\"%s\", %s, %s);", attrParam, jsonParam, scopeName));
  }

  @Override
  public HookPoint getListDeserialHook(String jsonParam, String attrParam, String scopeName) {
    return new StringHookPoint(String.format("return de.monticore.types.check.SymTypeExpressionDeSer.deserializeListMember(\"%s\", %s, %s);", attrParam, jsonParam, scopeName));
  }
}
