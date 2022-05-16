package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class TraceAlias extends Alias{
  public TraceAlias() {
    super("trace");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getTc().trace(arguments.get(0).toString(), arguments.get(1).toString());
    return TemplateModel.NOTHING;
  }
}
