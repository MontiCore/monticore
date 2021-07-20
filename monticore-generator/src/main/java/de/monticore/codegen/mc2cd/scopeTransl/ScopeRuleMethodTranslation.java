/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTGrammarMethod;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethodParameter;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class ScopeRuleMethodTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTScopeRule, ASTCDClass> link : rootLink.getLinks(ASTScopeRule.class,
        ASTCDClass.class)) {
      for (ASTGrammarMethod method : link.source().getGrammarMethodList()) {
        link.target().addCDMember(translateASTMethodToASTCDMethod(method));
      }
    }

    return rootLink;
  }

  protected ASTCDMethod createSimpleCDMethod(ASTGrammarMethod method) {
    String dotSeparatedName = MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(method.getMCReturnType());
    ASTCDMethod cdMethod = CD4CodeBasisMill.cDMethodBuilder().
            setModifier(TransformationHelper.createPublicModifier()).
            setName(method.getName()).
            setMCReturnType(TransformationHelper.createReturnType(dotSeparatedName)).uncheckedBuild();
    for (ASTMethodParameter param : method.getMethodParameterList()) {
      String typeName = MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(param.getType());
      cdMethod.getCDParameterList().add(TransformationHelper.createParameter(typeName, param.getName()));
    }
    return cdMethod;
  }

  protected void addMethodBodyStereotype(ASTModifier modifier, StringBuilder code){
    // to save the body in the cd
    // todo think of better version
    TransformationHelper.addStereotypeValue(modifier,
        MC2CDStereotypes.METHOD_BODY.toString(),
        code.toString());
  }

  protected ASTCDMethod translateASTMethodToASTCDMethod(ASTGrammarMethod method) {
    ASTCDMethod cdMethod = createSimpleCDMethod(method);
    if (method.getBody() instanceof ASTAction) {
      StringBuilder code = new StringBuilder();
      for (ASTMCBlockStatement action : ((ASTAction) method.getBody()).getMCBlockStatementList()) {
        code.append(new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter()).prettyprint(action));
      }
      addMethodBodyStereotype(cdMethod.getModifier(), code);
    }
    return cdMethod;
  }
}
