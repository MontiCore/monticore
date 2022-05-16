package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class DefineHookPointWithDefaultAlias extends Alias {
  public DefineHookPointWithDefaultAlias() {
    super("defineHookPointWithDefault");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return getGlex().defineHookPointWithDefault(getTc(), arguments.get(0).toString(), arguments.get(1).toString());
  }
}
