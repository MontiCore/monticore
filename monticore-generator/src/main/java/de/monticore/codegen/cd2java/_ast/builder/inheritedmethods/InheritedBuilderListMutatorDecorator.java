/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder.inheritedmethods;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderListMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * changes return type of builder setters for list attributes
 */
public class InheritedBuilderListMutatorDecorator extends BuilderListMutatorDecorator {

  public InheritedBuilderListMutatorDecorator(GlobalExtensionManagement glex, final ASTMCType builderType) {
    super(glex, builderType);
  }

  @Override
  protected ASTCDMethod createSetListMethod(ASTCDAttribute ast) {
    String signature = String.format(SET_LIST, capitalizedAttributeNameWithS, attributeType, ast.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethodByDefinition(signature);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
    method.setMCReturnType(returnType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.builder.SetInherited", ast, "set" + capitalizedAttributeNameWithS + "List"));
    return method;
  }
}
