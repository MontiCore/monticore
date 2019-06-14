/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.CdDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.grammar.grammar._ast.ASTMethodParameter;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.javastatements._ast.ASTBlockStatement;
import de.monticore.types.FullGenericTypesPrinter;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * Translates Methods belonging to ASTRules into CDMethods and attaches them to
 * the corresponding CDClasses.
 */
public class MethodTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  private GlobalExtensionManagement glex;

  /**
   * Constructor for
   * de.monticore.codegen.mc2cd.transl.MethodTranslation
   *
   * @param glex
   */
  public MethodTranslation(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTASTRule, ASTCDClass> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDClass.class)) {
      for (ASTMethod method : link.source().getMethodList()) {
        link.target().getCDMethodList().add(translateASTMethodToASTCDMethod(method));
      }
    }

    for (Link<ASTASTRule, ASTCDInterface> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDInterface.class)) {
      for (ASTMethod method : link.source().getMethodList()) {
        link.target().getCDMethodList().add(translateASTMethodToASTCDMethodInterface(method));
      }
    }

    return rootLink;
  }

  private ASTCDMethod createSimpleCDMethod(ASTMethod method) {
    ASTCDMethod cdMethod = CD4AnalysisNodeFactory.createASTCDMethod();
    cdMethod.setModifier(TransformationHelper.createPublicModifier());
    cdMethod.setName(method.getName());
    String dotSeparatedName = FullGenericTypesPrinter.printReturnType(method.getMCReturnType());
    cdMethod.setMCReturnType(MCBasicTypesMill.mCReturnTypeBuilder().setMCType(TransformationHelper.createSimpleReference(dotSeparatedName)).build());
    for (ASTMethodParameter param : method.getMethodParameterList()) {
      String typeName = FullGenericTypesPrinter.printType(param.getType());
      cdMethod.getCDParameterList().add(TransformationHelper.createParameter(typeName, param.getName()));
    }
    return cdMethod;
  }

  private void addMethodBodyStereotype(ASTModifier modifier, StringBuilder code){
    // to save the body in the cd
    // todo think of better version
    TransformationHelper.addStereotypeValue(modifier,
        MC2CDStereotypes.METHOD_BODY.toString(),
        code.toString());
  }

  private ASTCDMethod translateASTMethodToASTCDMethodInterface(ASTMethod method) {
    ASTCDMethod cdMethod = createSimpleCDMethod(method);
    if (method.getBody() instanceof ASTAction) {
      StringBuilder code = new StringBuilder();
      for (ASTBlockStatement action : ((ASTAction) method.getBody()).getBlockStatementList()) {
        code.append(GeneratorHelper.getMcPrettyPrinter().prettyprint(action));
      }
      if (!code.toString().isEmpty()) {
        HookPoint methodBody = new StringHookPoint(code.toString());
        glex.replaceTemplate(CdDecorator.EMPTY_BODY_TEMPLATE, cdMethod, methodBody);

        addMethodBodyStereotype(cdMethod.getModifier(), code);
      } else {
        cdMethod.getModifier().setAbstract(true);
      }
    }
    return cdMethod;
  }

  private ASTCDMethod translateASTMethodToASTCDMethod(ASTMethod method) {
    ASTCDMethod cdMethod = createSimpleCDMethod(method);
    if (method.getBody() instanceof ASTAction) {
      StringBuilder code = new StringBuilder();
      for (ASTBlockStatement action : ((ASTAction) method.getBody()).getBlockStatementList()) {
        code.append(GeneratorHelper.getMcPrettyPrinter().prettyprint(action));
      }
      HookPoint methodBody = new StringHookPoint(code.toString());
      glex.replaceTemplate(CdDecorator.EMPTY_BODY_TEMPLATE, cdMethod, methodBody);

      addMethodBodyStereotype(cdMethod.getModifier(), code);
    }
    return cdMethod;
  }
}
