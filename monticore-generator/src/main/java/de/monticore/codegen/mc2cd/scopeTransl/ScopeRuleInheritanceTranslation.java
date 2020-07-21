/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class ScopeRuleInheritanceTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTScopeRule, ASTCDClass> link : rootLink.getLinks(ASTScopeRule.class, ASTCDClass.class)) {
      translateClassProd(link.source(), link.target(), rootLink.source());
    }
    return rootLink;
  }

  private void translateClassProd(ASTScopeRule rule, ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    for (ASTMCType superClass : rule.getSuperClassList()) {
      String qualifiedSuperClass = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superClass, astGrammar, cdClass);
      cdClass.setSuperclass(TransformationHelper.createObjectType(qualifiedSuperClass));
    }

    for (ASTMCType superInterface : rule.getSuperInterfaceList()) {
      String qualifiedSuperInterface = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdClass);
      cdClass.getInterfaceList()
          .add(TransformationHelper.createObjectType(qualifiedSuperInterface));
    }
  }
}
