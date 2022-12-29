/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModelException;

import java.util.Collections;
import java.util.List;

public class RequiredGlobalVarsAlias extends GlexAlias {
  public RequiredGlobalVarsAlias() {
    super("requiredGlobalVars", "requiredGlobalVars");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return super.exec(Collections.singletonList(convertVarargsToCollectionModel(arguments, 0)));
  }
}
