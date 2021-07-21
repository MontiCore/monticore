/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
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

  protected void translateClassProd(ASTScopeRule rule, ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    if (!rule.getSuperClassList().isEmpty() && !cdClass.isPresentCDExtendUsage()) {
      cdClass.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().build());
    }
    for (ASTMCType superClass : rule.getSuperClassList()) {
      String qualifiedSuperClass = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superClass, astGrammar, cdClass);
      cdClass.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(qualifiedSuperClass));
    }

    if (!rule.getSuperInterfaceList().isEmpty() && !cdClass.isPresentCDInterfaceUsage()) {
      cdClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
    }
    for (ASTMCType superInterface : rule.getSuperInterfaceList()) {
      String qualifiedSuperInterface = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdClass);
      cdClass.getCDInterfaceUsage().addInterface(TransformationHelper.createObjectType(qualifiedSuperInterface));
    }
  }
}
