package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModelException;

import java.util.List;

public class IncludeArgsAlias extends Alias {
  public IncludeArgsAlias() {
    super("includeArgs");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    assert arguments.size() == 2;
    return getTc().include(arguments.get(0).toString(), arguments.subList(1, arguments.size()));
  }
}
