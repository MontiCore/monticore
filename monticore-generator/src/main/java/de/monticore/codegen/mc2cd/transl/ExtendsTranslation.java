/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.TransformationHelper.getPackageName;

/**
 * Checks if the source rules were extending other rules and sets the super
 * classes / extended interfaces of the target nodes accordingly.
 *
 */
public class ExtendsTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(
        ASTClassProd.class, ASTCDClass.class)) {
      translateClassProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(
        ASTAbstractProd.class, ASTCDClass.class)) {
      translateAbstractProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(
        ASTInterfaceProd.class, ASTCDInterface.class)) {
      translateInterfaceProd(link.source(), link.target(), rootLink.source());
    }

    return rootLink;
  }

  private void translateClassProd(ASTClassProd classProd, ASTCDClass cdClass,
      ASTMCGrammar astGrammar) {
    // translates "extends"
    if (!(classProd.getSuperRuleList().isEmpty() && classProd.getASTSuperClassList().isEmpty()) && !cdClass.isPresentCDExtendUsage()) {
      cdClass.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().build());
    }
    for (ASTRuleReference ruleReference : classProd.getSuperRuleList()) {
      ProdSymbol ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(astGrammar, ruleReference.getName()).get();
      String packageName = getPackageName(ruleSymbol);
      cdClass.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(
          packageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    for (ASTMCType typeReference : classProd.getASTSuperClassList()) {
      String qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdClass);
      cdClass.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(qualifiedRuleName));
    }
  }

  private void translateAbstractProd(ASTAbstractProd abstractProd,
      ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    // translates "extends"
    if (!(abstractProd.getSuperRuleList().isEmpty() && abstractProd.getASTSuperClassList().isEmpty()) && !cdClass.isPresentCDExtendUsage()) {
      cdClass.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().build());
    }
    for (ASTRuleReference ruleReference : abstractProd.getSuperRuleList()) {
      ProdSymbol ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(astGrammar, ruleReference.getName()).get();
      String packageName = getPackageName(ruleSymbol);
      cdClass.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(
          packageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    String qualifiedRuleName;
    for (ASTMCType typeReference : abstractProd.getASTSuperClassList()) {
      qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdClass);
      cdClass.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(qualifiedRuleName));
    }
  }

  private void translateInterfaceProd(ASTInterfaceProd interfaceProd,
      ASTCDInterface cdInterface, ASTMCGrammar astGrammar) {
    // translates "extends"
    if (!(interfaceProd.getSuperInterfaceRuleList().isEmpty() && interfaceProd.getASTSuperInterfaceList().isEmpty()) && !cdInterface.isPresentCDExtendUsage()) {
      cdInterface.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().build());
    }
    for (ASTRuleReference ruleReference : interfaceProd.getSuperInterfaceRuleList()) {
      ProdSymbol ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(astGrammar, ruleReference.getName()).get();
      String packageName = getPackageName(ruleSymbol);
      cdInterface.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(
          packageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    String qualifiedRuleName;
    for (ASTMCType typeReference : interfaceProd.getASTSuperInterfaceList()) {
      qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdInterface);
      cdInterface.getCDExtendUsage().addSuperclass(TransformationHelper.createObjectType(qualifiedRuleName));
    }
  }
}
