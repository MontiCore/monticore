/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class MandatoryMutatorDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected static final String SET = "set%s";

  public MandatoryMutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    List<ASTCDMethod> methods = new ArrayList<>(Collections.singletonList(createSetter(ast)));
    if (getDecorationHelper().isSupplier(ast.getMCType())) {
      methods.add(createSupplierSetter(ast));
    }
    return methods;
  }

  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    ASTCDAttribute attribute = ast;
    String templateName = "methods.Set";
    if (getDecorationHelper().isSupplier(ast.getMCType())) {
      // expose the unwrapped type (Supplier<X> -> X) in the setter parameter; the Supplier stays hidden
      attribute = ast.deepClone();
      attribute.setMCType(getDecorationHelper().getReferenceTypeOfSupplier(attribute.getMCType()).getMCTypeOpt().get());
      templateName = "methods.SupplierSet";
    }

    String name = String.format(SET, StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(attribute.getName())));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name, this.getCDParameterFacade().createParameters(attribute));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(templateName, ast));
    return method;
  }


  protected ASTCDMethod createSupplierSetter(final ASTCDAttribute ast) {
    ASTCDAttribute attribute = ast.deepClone();
    ASTMCType supplierType = getMCTypeFacade().createBasicGenericTypeOf(
        "java.util.function.Supplier", getDecorationHelper().getReferenceTypeOfSupplier(ast.getMCType()));
    attribute.setMCType(supplierType);

    String name = String.format(SET, StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(attribute.getName()))) + "Supplier";
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name, this.getCDParameterFacade().createParameters(attribute));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.SupplierSetRaw", ast));
    return method;
  }
}
