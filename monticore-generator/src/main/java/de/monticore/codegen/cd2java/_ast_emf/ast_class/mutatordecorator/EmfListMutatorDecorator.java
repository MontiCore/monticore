/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.ast_class.mutatordecorator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.PACKAGE_SUFFIX;


public class EmfListMutatorDecorator extends ListMutatorDecorator {

  protected final ASTService astService;

  protected String className;

  public EmfListMutatorDecorator(GlobalExtensionManagement glex, ASTService astService) {
    super(glex);
    this.astService = astService;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  @Override
  protected ASTCDMethod createSetListMethod(ASTCDAttribute attribute) {
    String packageName = astService.getCDName() + PACKAGE_SUFFIX;
    String signature = String.format(SET_LIST, capitalizedAttributeNameWithOutS, attributeType, attribute.getName());
    ASTCDMethod getList = this.getCDMethodFacade().createMethodByDefinition(signature);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("_ast_emf.ast_class.Set4EMFASTClass",
        packageName, className, attribute));
    return getList;
  }
}
