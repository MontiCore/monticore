package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class ChangeGlobalVarAlias extends Alias {
  public ChangeGlobalVarAlias() {
    super("changeGlobalVar");
  }


  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getGlex().changeGlobalVar(arguments.get(0).toString(), arguments.get(1));
    return TemplateModel.NOTHING;
  }
}
