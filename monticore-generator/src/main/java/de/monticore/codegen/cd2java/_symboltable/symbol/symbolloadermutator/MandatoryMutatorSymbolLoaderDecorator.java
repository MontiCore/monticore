package de.monticore.codegen.cd2java._symboltable.symbol.symbolloadermutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class MandatoryMutatorSymbolLoaderDecorator extends MandatoryMutatorDecorator {
  public MandatoryMutatorSymbolLoaderDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    //todo find better util than the DecorationHelper
    String name = String.format(SET, StringUtils.capitalize(DecorationHelper.getNativeAttributeName(ast.getName())));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name, this.getCDParameterFacade().createParameters(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.symbolloader.Set", ast));
    return method;
  }
}
