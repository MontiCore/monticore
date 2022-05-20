package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.CollectionModel;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.ArrayList;
import java.util.List;

public abstract class Alias implements TemplateMethodModelEx {
  private final String name;
  private final String methodName;

  protected Alias(String name, String method) {
    this.name = name;
    this.methodName = method;
  }

  public String getName(){
    return name;
  }

  public String getMethodName() {
    return methodName;
  }

  public abstract TemplateMethodModelEx getMethod() throws TemplateModelException;

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return getMethod().exec(arguments);
  }

  private List convertVarargsToList(List arguments, int startIndex) throws TemplateModelException {
    // Conversion of ... syntax
    List l = new ArrayList();
    if(!arguments.isEmpty()) {
      ObjectWrapperAndUnwrapper wrapper = (ObjectWrapperAndUnwrapper) Environment.getCurrentEnvironment().getObjectWrapper();
      for (Object o : arguments.subList(startIndex, arguments.size())) {
        l.add(wrapper.unwrap((TemplateModel) o));
      }
    }
    return l;
  }

  protected CollectionModel convertVarargsToCollectionModel(List arguments, int startIndex) throws TemplateModelException{
    return new CollectionModel(
        convertVarargsToList(arguments, startIndex),
        (BeansWrapper) Environment.getCurrentEnvironment().getObjectWrapper()
    );
  }

  protected void exactArguments(List arguments, int count) throws TemplateModelException {
    if(arguments.size() != count){
      throw new TemplateModelException("Expected " + count + " arguments but got " + arguments.size());
    }
  }

  protected void atLeastArguments(List arguments, int count) throws TemplateModelException {
    if(arguments.size() < count){
      throw new TemplateModelException("Expected at least " + count + " arguments but got " + arguments.size());
    }
  }
}
