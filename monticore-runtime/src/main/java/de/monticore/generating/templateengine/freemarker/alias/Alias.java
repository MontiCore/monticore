package de.monticore.generating.templateengine.freemarker.alias;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import freemarker.core.Environment;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public abstract class Alias implements TemplateMethodModelEx {
  private final String name;

  protected Alias(String name) {
    this.name = name;
  }

  public String getName(){
    return name;
  }

  public TemplateController getTc() {
    try {
      TemplateModel tcTM = Environment.getCurrentEnvironment().getVariable("tc");
      return (TemplateController) ((WrapperTemplateModel) tcTM).getWrappedObject();
    } catch (TemplateModelException e) {
      throw new IllegalStateException("Can not find TemplateController tc", e);
    }
  }

  public GlobalExtensionManagement getGlex() {
    try {
      TemplateModel glexTM = Environment.getCurrentEnvironment().getVariable("glex");
      return (GlobalExtensionManagement) ((WrapperTemplateModel)glexTM).getWrappedObject();
    } catch (TemplateModelException e) {
      throw new IllegalStateException("Can not find TemplateController tc", e);
    }
  }
}
