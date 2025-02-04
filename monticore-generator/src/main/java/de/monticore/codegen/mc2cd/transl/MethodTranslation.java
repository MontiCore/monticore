/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTGrammarMethod;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethodParameter;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * Translates Methods belonging to ASTRules into CDMethods and attaches them to
 * the corresponding CDClasses.
 */
public class MethodTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  protected GlobalExtensionManagement glex;

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
      for (ASTGrammarMethod method : link.source().getGrammarMethodList()) {
        link.target().addCDMember(translateASTMethodToASTCDMethod(method));
      }
    }

    for (Link<ASTASTRule, ASTCDInterface> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDInterface.class)) {
      for (ASTGrammarMethod method : link.source().getGrammarMethodList()) {
        link.target().addCDMember(translateASTMethodToASTCDMethodInterface(method));
      }
    }

    return rootLink;
  }

  protected ASTCDMethod createSimpleCDMethod(ASTGrammarMethod method) {
    String dotSeparatedName = Grammar_WithConceptsMill.prettyPrint(method.getMCReturnType(), true);
    ASTCDMethod cdMethod = CD4CodeBasisMill.cDMethodBuilder().
            setModifier(TransformationHelper.createPublicModifier()).
            setName(method.getName()).
            setMCReturnType(TransformationHelper.createReturnType(dotSeparatedName)).uncheckedBuild();
    for (ASTMethodParameter param : method.getMethodParameterList()) {
      String typeName = Grammar_WithConceptsMill.prettyPrint(param.getType(), true);
      cdMethod.getCDParameterList().add(TransformationHelper.createParameter(typeName, param.getName()));
    }
    return cdMethod;
  }

  protected void addMethodBodyStereotype(ASTModifier modifier, StringBuilder code) {
    // to save the body in the cd
    TransformationHelper.addStereotypeValue(modifier,
        MC2CDStereotypes.METHOD_BODY.toString(),
        code.toString());
  }

  protected ASTCDMethod translateASTMethodToASTCDMethodInterface(ASTGrammarMethod method) {
    ASTCDMethod cdMethod = createSimpleCDMethod(method);
    if (method.getBody() instanceof ASTAction) {
      StringBuilder code = new StringBuilder();
      for (ASTMCBlockStatement action : ((ASTAction) method.getBody()).getMCBlockStatementList()) {
        code.append(Grammar_WithConceptsMill.prettyPrint(action, true));
      }
      if (!code.toString().isEmpty()) {
        addMethodBodyStereotype(cdMethod.getModifier(), code);
      } else {
        cdMethod.getModifier().setAbstract(true);
      }
    }
    return cdMethod;
  }

  protected ASTCDMethod translateASTMethodToASTCDMethod(ASTGrammarMethod method) {
    ASTCDMethod cdMethod = createSimpleCDMethod(method);
    if (method.getBody() instanceof ASTAction) {
      StringBuilder code = new StringBuilder();
      for (ASTMCBlockStatement action : ((ASTAction) method.getBody()).getMCBlockStatementList()) {
        code.append(Grammar_WithConceptsMill.prettyPrint(action, true));
      }
      addMethodBodyStereotype(cdMethod.getModifier(), code);
    }
    return cdMethod;
  }
}
