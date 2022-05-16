package de.monticore.generating.templateengine.freemarker.alias;

import de.monticore.ast.ASTNode;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class DefineHookPointAlias extends Alias {
  public DefineHookPointAlias() {
    super("defineHookPoint");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    if(arguments.size() == 2){
      return getGlex().defineHookPoint(getTc(), arguments.get(0).toString(), (ASTNode) ((WrapperTemplateModel)arguments.get(1)).getWrappedObject());
    }else if(arguments.size() == 1){
      return getGlex().defineHookPoint(getTc(), arguments.get(0).toString());
    } else {
      throw new TemplateModelException("Expecting 1 or 2 arguments but got " + arguments.size());
    }
    // TODO AHe: Check that all aliases have the correct number of arguments
  }
}
