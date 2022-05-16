package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class DebugAlias extends Alias{
  public DebugAlias() {
    super("debug");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getTc().debug(arguments.get(0).toString(), arguments.get(1).toString());
    return TemplateModel.NOTHING;
  }
}
