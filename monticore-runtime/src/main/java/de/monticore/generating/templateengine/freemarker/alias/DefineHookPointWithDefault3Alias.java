package de.monticore.generating.templateengine.freemarker.alias;

import de.monticore.ast.ASTNode;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class DefineHookPointWithDefault3Alias extends Alias {
  public DefineHookPointWithDefault3Alias() {
    super("defineHookPointWithDefault3");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return getGlex().defineHookPointWithDefault(getTc(), arguments.get(0).toString(), (ASTNode) ((WrapperTemplateModel)arguments.get(1)).getWrappedObject(), arguments.get(2).toString());
  }
}
