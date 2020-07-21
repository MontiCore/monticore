/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class MandatoryMutatorDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected static final String SET = "set%s";

  public MandatoryMutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    return new ArrayList<>(Arrays.asList(createSetter(ast)));
  }

  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name = String.format(SET, StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(ast.getName())));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name, this.getCDParameterFacade().createParameters(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Set", ast));
    return method;
  }
}
