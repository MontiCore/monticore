package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModelException;

import java.util.List;

public class GetGlobalVarAlias extends Alias {
  public GetGlobalVarAlias() {
    super("getGlobalVar");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return getGlex().getGlobalVar(arguments.get(0).toString());
  }
}
