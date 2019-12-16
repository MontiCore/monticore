package de.monticore.codegen.cd2java._symboltable.symbol.symbolloadermutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class MandatoryMutatorSymbolLoaderDecorator extends MandatoryMutatorDecorator {
  public MandatoryMutatorSymbolLoaderDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name = String.format(SET, StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(ast.getName())));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name, this.getCDParameterFacade().createParameters(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.symbolloader.Set", ast));
    return method;
  }
}
