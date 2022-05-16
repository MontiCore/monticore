package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class AddToGlobalVarAlias extends Alias {
  public AddToGlobalVarAlias() {
    super("addToGlobalVar");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getGlex().addToGlobalVar(arguments.get(0).toString(), arguments.get(1));
    return TemplateModel.NOTHING;
  }
}
