package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.core.Environment;
import freemarker.template.TemplateModelException;

import java.util.ArrayList;
import java.util.List;

public class DefineHookPointAlias extends GlexAlias {
  public DefineHookPointAlias() {
    super("defineHookPoint", "defineHookPoint");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    // 1 or 2 arguments: name, ast?
    atLeastArguments(arguments, 1);
    if(arguments.size() > 1){
      exactArguments(arguments, 2);
    }
    // First argument of defineHookPoint is always tc
    ArrayList args = new ArrayList(arguments);
    args.add(0, Environment.getCurrentEnvironment().getVariable("tc"));
    return super.exec(args);
  }
}
