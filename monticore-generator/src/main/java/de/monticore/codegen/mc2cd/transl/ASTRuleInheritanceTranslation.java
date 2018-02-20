/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.utils.Link;


/**
 * Checks if the source astrules were extending or implementing other rules
 * and sets the super classes / extended interfaces of the target nodes
 * accordingly. This Translation differs from {@link ExtendsTranslation} and
 * {@link ImplementsTranslation} since it applies to ASTRules rather than
 * ordinary rules. E.g: 'ast A astextends B' instead of 'A astextends B'.
 *
 * @author Sebastian Oberhoff
 */
public class ASTRuleInheritanceTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTASTRule, ASTCDClass> link : rootLink.getLinks(
        ASTASTRule.class, ASTCDClass.class)) {
      translateClassProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTASTRule, ASTCDInterface> link : rootLink.getLinks(
        ASTASTRule.class, ASTCDInterface.class)) {
      translateInterfaceProd(link.source(), link.target(), rootLink.source());
    }

    return rootLink;
  }

  private void translateInterfaceProd(ASTASTRule rule, ASTCDInterface cdInterface,
      ASTMCGrammar astGrammar) {
    // translates "astextends"
    for (ASTGenericType superInterface : rule.getASTSuperClassList()) {
      String qualifiedSuperInterface = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdInterface);

      cdInterface.getInterfaceList().add(
          TransformationHelper.createSimpleReference(qualifiedSuperInterface));
    }
  }

  private void translateClassProd(ASTASTRule rule, ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    // translates "astextends"
    for (ASTGenericType superClass : rule.getASTSuperClassList()) {
      String qualifiedSuperClass = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superClass, astGrammar, cdClass);
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(qualifiedSuperClass));
    }

    // translates "astimplements"
    for (ASTGenericType superInterface : rule.getASTSuperInterfaceList()) {
      String qualifiedSuperInterface = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdClass);
      cdClass.getInterfaceList()
          .add(TransformationHelper.createSimpleReference(qualifiedSuperInterface));
    }
  }
}
