/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class GlexAlias extends Alias{
  protected GlexAlias(String name, String method) {
    super(name, method);
  }

  @Override
  public TemplateMethodModelEx getMethod() throws TemplateModelException {
    BeanModel tc = (BeanModel) Environment.getCurrentEnvironment().getVariable("glex");
    TemplateMethodModelEx res = (TemplateMethodModelEx) ((BeanModel) tc.getAPI()).get(getMethodName());
    if(res == null){
      throw new TemplateModelException("Can not find method " + getMethodName() + " of GlobalExtensionManagement");
    }
    return res;
  }
}
