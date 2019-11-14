/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.enums;

import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.facade.CDModifier;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.ENUMERATOR;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.TO_STRING_CALL;

public class EmfEnumDecorator extends EnumDecorator {

  public EmfEnumDecorator(GlobalExtensionManagement glex, AccessorDecorator accessorDecorator, ASTService astService) {
    super(glex, accessorDecorator, astService);
  }

  @Override
  public ASTCDEnum decorate(final ASTCDEnum input) {
    ASTCDEnum astcdEnum = super.decorate(input);
    //add emf interface
    astcdEnum.addInterface(getMCTypeFacade().createQualifiedType(ENUMERATOR));
    astcdEnum.addCDMethod(createGetNameMethod());
    astcdEnum.addCDMethod(createGetLiteralMethod());
    astcdEnum.addCDMethod(createGetValueMethod());
    return astcdEnum;
  }

  protected ASTCDMethod createGetNameMethod(){
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, getMCTypeFacade().createStringType(), "getName");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint(TO_STRING_CALL));
    return method;
  }

  protected ASTCDMethod createGetLiteralMethod(){
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, getMCTypeFacade().createStringType(), "getLiteral");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint(TO_STRING_CALL));
    return method;
  }

  protected ASTCDMethod createGetValueMethod(){
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, getMCTypeFacade().createIntType(), "getValue");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return intValue;"));
    return method;
  }
}
