package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class DefineGlobalVarAlias extends Alias {
  public DefineGlobalVarAlias() {
    super("defineGlobalVar");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getGlex().defineGlobalVar(arguments.get(0).toString(), arguments.get(1));
    return TemplateModel.NOTHING;
  }
}
