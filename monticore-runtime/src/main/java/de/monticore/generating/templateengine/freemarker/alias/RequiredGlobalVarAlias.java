package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class RequiredGlobalVarAlias extends Alias {
  public RequiredGlobalVarAlias() {
    super("requiredGlobalVar");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getGlex().requiredGlobalVar(arguments.get(0).toString());
    return TemplateModel.NOTHING;
  }
}
