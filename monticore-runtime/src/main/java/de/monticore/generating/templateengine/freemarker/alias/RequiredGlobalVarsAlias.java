package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;
import java.util.stream.Collectors;

public class RequiredGlobalVarsAlias extends Alias {
  public RequiredGlobalVarsAlias() {
    super("requiredGlobalVars");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    List<String> names = (List<String>) arguments.stream().map(Object::toString).collect(Collectors.toList());
    getGlex().requiredGlobalVars(names.toArray(new String[0]));
    return TemplateModel.NOTHING;
  }
}
