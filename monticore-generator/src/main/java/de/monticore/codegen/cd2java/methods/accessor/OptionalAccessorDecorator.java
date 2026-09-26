/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class OptionalAccessorDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected static final String GET = "get%s";

  protected static final String IS_PRESENT = "isPresent%s";

  protected String naiveAttributeName;

  protected final AbstractService service;

  public OptionalAccessorDecorator(final GlobalExtensionManagement glex,
                                   final AbstractService service) {
    super(glex);
    this.service = service;
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    naiveAttributeName = getNaiveAttributeName(ast);
    List<ASTCDMethod> methods = new ArrayList<>();
    methods.add(createGetMethod(ast));
    methods.add(createIsPresentMethod(ast));
    if (getDecorationHelper().isSupplier(ast.getMCType())) {
      methods.add(createSupplierGetMethod(ast));
    }
    return methods;
  }

  protected String getNaiveAttributeName(ASTCDAttribute astcdAttribute) {
    return StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(astcdAttribute.getName()));
  }

  protected ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, naiveAttributeName);

    ASTMCType type = ast.getMCType().deepClone();
    String templateName = "methods.opt.Get4Opt";
    if (getDecorationHelper().isSupplier(type)) {
      type = getDecorationHelper().unwrapSupplier(type);
      templateName = "methods.opt.SupplierGet4Opt";
    }

    type = getDecorationHelper().getReferenceTypeOfOptional(type).getMCTypeOpt().get();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name);
    String generatedErrorCode = service.getGeneratedErrorCode(ast.getName() + ast.printType());
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(templateName, ast, naiveAttributeName, generatedErrorCode));
    return method;
  }

  protected ASTCDMethod createSupplierGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, naiveAttributeName) + "Supplier";
    ASTMCType supplierType = getDecorationHelper().toPublicSupplierType(ast.getMCType());
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), supplierType, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.SupplierGetRaw", ast));
    return method;
  }

  protected ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = String.format(IS_PRESENT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), name);

    String templateName = "methods.opt.IsPresent4Opt";
    if (getDecorationHelper().isSupplier(ast.getMCType())) {
      templateName = "methods.opt.SupplierIsPresent4Opt";
    }

    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(templateName, ast));
    return method;
  }
}
