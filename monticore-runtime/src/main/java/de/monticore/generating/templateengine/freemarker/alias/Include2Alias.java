package de.monticore.generating.templateengine.freemarker.alias;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.TemplateController;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class Include2Alias extends Alias{
  public Include2Alias() {
    super("include2");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    assert arguments.size() == 2;
    return getTc().include(arguments.get(0).toString(), (ASTNode) ((WrapperTemplateModel)arguments.get(1)).getWrappedObject());
  }
}
