package de.monticore.generating.templateengine.freemarker.alias;

import de.monticore.generating.templateengine.HookPoint;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class BindHookPointAlias extends Alias {
  public BindHookPointAlias() {
    super("bindHookPoint");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    getGlex().bindHookPoint(arguments.get(0).toString(), (HookPoint) arguments.get(1));
    return TemplateModel.NOTHING;
  }
}
