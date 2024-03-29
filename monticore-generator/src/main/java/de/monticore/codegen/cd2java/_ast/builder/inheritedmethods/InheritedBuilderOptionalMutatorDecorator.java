/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder.inheritedmethods;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderOptionalMutatorDecorator;
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
public class InheritedBuilderOptionalMutatorDecorator extends BuilderOptionalMutatorDecorator {


  public InheritedBuilderOptionalMutatorDecorator(final GlobalExtensionManagement glex,
                                                  final ASTMCType builderType) {
    super(glex, builderType);
  }

  @Override
  protected ASTCDMethod createSetMethod(final ASTCDAttribute attribute) {
    String name = String.format(SET, naiveAttributeName);
    ASTMCType parameterType = getDecorationHelper().getReferenceTypeFromOptional(attribute.getMCType()).getMCTypeOpt().get().deepClone();
    ASTCDParameter parameter = this.getCDParameterFacade().createParameter(parameterType, attribute.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name, parameter);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
    method.setMCReturnType(returnType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.builder.SetInherited", attribute, name));
    return method;
  }

  @Override
  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute attribute) {
    String name = String.format(SET_ABSENT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
    method.setMCReturnType(returnType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.builder.opt.SetAbsentInherited", name));
    return method;
  }
}
