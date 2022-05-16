package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class WarnAlias extends Alias{
  public WarnAlias() {
    super("warn");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getTc().warn(arguments.get(0).toString());
    return TemplateModel.NOTHING;
  }
}
