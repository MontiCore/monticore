package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class ErrorAlias extends Alias{
  public ErrorAlias() {
    super("error");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getTc().error(arguments.get(0).toString());
    return TemplateModel.NOTHING;
  }
}
