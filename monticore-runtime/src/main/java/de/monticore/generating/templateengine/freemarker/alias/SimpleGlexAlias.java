/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModelException;

import java.util.List;

public class SimpleGlexAlias extends GlexAlias{
  protected final int params;

  public SimpleGlexAlias(String name, String method, int params) {
    super(name, method);
    this.params = params;
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    exactArguments(arguments, params);
    return super.exec(arguments);
  }
}
