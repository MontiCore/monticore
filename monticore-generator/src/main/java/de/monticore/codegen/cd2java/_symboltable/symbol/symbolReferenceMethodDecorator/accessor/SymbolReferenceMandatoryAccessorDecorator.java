package de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.accessor;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolReferenceMandatoryAccessorDecorator extends MandatoryAccessorDecorator {

  public SymbolReferenceMandatoryAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDMethod createGetter(final ASTCDAttribute ast) {
    String getterPrefix;
    if (getMCTypeFacade().isBooleanType(ast.getMCType())) {
      getterPrefix = IS;
    } else {
      getterPrefix = GET;
    }
    //todo find better util than the DecorationHelper
    String methodName = String.format(getterPrefix, StringUtils.capitalize(DecorationHelper.getNativeAttributeName(ast.getName())));
    ASTMCType type = ast.getMCType().deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, type, methodName);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        "_symboltable.symbolreferece.methods.Get", methodName));
    return method;
  }
}
