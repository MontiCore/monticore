package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class ExistsHookPointAlias extends Alias {
  public ExistsHookPointAlias() {
    super("existsHookPoint");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return getGlex().existsHookPoint(arguments.get(0).toString());
  }
}
