package de.monticore.codegen.cd2java._ast_emf.enums;

import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

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
    astcdEnum.addInterface(getCDTypeFacade().createQualifiedType(ENUMERATOR));
    astcdEnum.addCDMethod(createGetNameMethod());
    astcdEnum.addCDMethod(createGetLiteralMethod());
    astcdEnum.addCDMethod(createGetValueMethod());
    return astcdEnum;
  }

  protected ASTCDMethod createGetNameMethod(){
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createStringType()).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, returnType, "getName");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint(TO_STRING_CALL));
    return method;
  }

  protected ASTCDMethod createGetLiteralMethod(){
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createStringType()).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, returnType, "getLiteral");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint(TO_STRING_CALL));
    return method;
  }


  protected ASTCDMethod createGetValueMethod(){
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createIntType()).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, returnType, "getValue");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return intValue;"));
    return method;
  }
}
