/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.TemplateModelException;

import java.util.Collections;
import java.util.List;

public class SignatureAlias extends TcAlias{
  public SignatureAlias() {
    super("signature", "signature");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return super.exec(Collections.singletonList(convertVarargsToCollectionModel(arguments, 0)));
  }
}
