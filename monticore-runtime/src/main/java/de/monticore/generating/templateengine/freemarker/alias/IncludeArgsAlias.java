package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModelException;

import java.util.Arrays;
import java.util.List;

public class IncludeArgsAlias extends TcAlias {
  public IncludeArgsAlias() {
    super("includeArgs", "includeArgs");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    atLeastArguments(arguments, 1);

    return super.exec(Arrays.asList(
        arguments.get(0),
        convertVarargsToCollectionModel(arguments, 1)
    ));
  }
}
