package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class InfoAlias extends Alias{
  public InfoAlias() {
    super("info");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getTc().info(arguments.get(0).toString(), arguments.get(1).toString());
    return TemplateModel.NOTHING;
  }
}
