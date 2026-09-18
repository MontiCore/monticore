/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder.buildermethods;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * changes return type of builder setters for optional attributes
 */
public class BuilderOptionalMutatorDecorator extends OptionalMutatorDecorator {

  protected final ASTMCType builderType;

  public BuilderOptionalMutatorDecorator(final GlobalExtensionManagement glex,
                                         final ASTMCType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected ASTCDMethod createSetMethod(final ASTCDAttribute attribute) {
    String name = String.format(SET, naiveAttributeName);

    ASTMCType type = attribute.getMCType().deepClone();
    String templateName;
    if (getDecorationHelper().isSupplier(type)) {
      templateName = "_ast.builder.opt.SupplierSet4ASTBuilderOpt";
      type = getDecorationHelper().getReferenceTypeOfSupplier(type).getMCTypeOpt().get();
    } else {
      templateName = "_ast.builder.opt.Set4ASTBuilderOpt";
    }

    ASTMCType parameterType = getDecorationHelper().getReferenceTypeOfOptional(type).getMCTypeOpt().get().deepClone();
    ASTCDParameter parameter = this.getCDParameterFacade().createParameter(parameterType, attribute.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name, parameter);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
    method.setMCReturnType(returnType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(templateName, attribute));
    return method;
  }

  @Override
  protected ASTCDMethod createSupplierSetMethod(final ASTCDAttribute ast) {
    String name = String.format(SET, naiveAttributeName) + "Supplier";
    ASTMCType supplierType = getMCTypeFacade().createBasicGenericTypeOf(
        "java.util.function.Supplier", getDecorationHelper().getReferenceTypeOfSupplier(ast.getMCType()));
    ASTCDParameter parameter = this.getCDParameterFacade().createParameter(supplierType, ast.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name, parameter);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
    method.setMCReturnType(returnType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.builder.SupplierSetRaw4ASTBuilder", ast));
    return method;
  }

  @Override
  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute attribute) {
    String name = String.format(SET_ABSENT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
    method.setMCReturnType(returnType);

    String templateName = getDecorationHelper().isSupplier(attribute.getMCType())
            ? "_ast.builder.opt.SupplierSetAbsent4ASTBuilderOpt"
            : "_ast.builder.opt.SetAbsent4ASTBuilderOpt";

    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(templateName, attribute));
    return method;
  }
}
