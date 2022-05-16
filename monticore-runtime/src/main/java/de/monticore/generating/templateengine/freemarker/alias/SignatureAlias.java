package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;
import java.util.stream.Collectors;

public class SignatureAlias extends Alias{
  public SignatureAlias() {
    super("signature");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    List<String> paramNames = (List<String>) arguments.stream().map(Object::toString).collect(Collectors.<String>toList());
    getTc().signature(paramNames);
    return TemplateModel.NOTHING;
  }
}
