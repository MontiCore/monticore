/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.core.Environment;
import freemarker.template.TemplateModelException;

import java.util.ArrayList;
import java.util.List;

public class DefineHookPointWithDefaultAlias extends GlexAlias {
  public DefineHookPointWithDefaultAlias() {
    super("defineHookPointWithDefault", "defineHookPointWithDefault");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    exactArguments(arguments, 2);

    ArrayList args = new ArrayList(arguments);
    args.add(0, Environment.getCurrentEnvironment().getVariable("tc"));
    return super.exec(args);
  }
}
