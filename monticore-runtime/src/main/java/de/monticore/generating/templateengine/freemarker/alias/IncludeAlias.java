package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.core.Environment;
import freemarker.template.TemplateModelException;

import java.util.List;

public class IncludeAlias extends Alias {

  public IncludeAlias() {
    super("include");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return getTc().include(arguments.get(0).toString());
  }
}
