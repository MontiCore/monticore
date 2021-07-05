/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;


/**
 * Checks if the source astrules were extending or implementing other rules
 * and sets the super classes / extended interfaces of the target nodes
 * accordingly. This Translation differs from {@link ExtendsTranslation} and
 * {@link ImplementsTranslation} since it applies to ASTRules rather than
 * ordinary rules. E.g: 'ast A astextends B' instead of 'A astextends B'.
 *
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
    if (!rule.getASTSuperClassList().isEmpty() && !cdInterface.isPresentCDExtendUsage()) {
      cdInterface.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().build());
    }
    for (ASTMCType superInterface : rule.getASTSuperClassList()) {
      String qualifiedSuperInterface = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdInterface);

      cdInterface.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(qualifiedSuperInterface));
    }
  }

  private void translateClassProd(ASTASTRule rule, ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    // translates "astextends"
    if (!rule.getASTSuperClassList().isEmpty() && !cdClass.isPresentCDExtendUsage()) {
      cdClass.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().build());
    }
    for (ASTMCType superClass : rule.getASTSuperClassList()) {
      String qualifiedSuperClass = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superClass, astGrammar, cdClass);
      cdClass.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(qualifiedSuperClass));
    }

    // translates "astimplements"
    if (!rule.getASTSuperInterfaceList().isEmpty() && ! cdClass.isPresentCDInterfaceUsage()) {
      cdClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
    }
    for (ASTMCType superInterface : rule.getASTSuperInterfaceList()) {
      String qualifiedSuperInterface = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdClass);
      cdClass.getCDInterfaceUsage().addInterface(TransformationHelper.createObjectType(qualifiedSuperInterface));
    }
  }
}
