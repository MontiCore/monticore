/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import static de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper.resolveRule;
import static de.monticore.codegen.mc2cd.TransformationHelper.getPackageName;

import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.utils.Link;

/**
 * Checks if the source rules were extending other rules and sets the super
 * classes / extended interfaces of the target nodes accordingly.
 *
 * @author Sebastian Oberhoff
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
    for (ASTRuleReference ruleReference : classProd.getSuperRuleList()) {
      MCProdSymbol ruleSymbol = resolveRule(astGrammar, ruleReference.getName()).get();
      String packageName = getPackageName(ruleSymbol);
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(
          packageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    for (ASTGenericType typeReference : classProd.getASTSuperClassList()) {
      String qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdClass);
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(qualifiedRuleName));
    }
  }

  private void translateAbstractProd(ASTAbstractProd abstractProd,
      ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    // translates "extends"
    for (ASTRuleReference ruleReference : abstractProd.getSuperRuleList()) {
      MCProdSymbol ruleSymbol = resolveRule(astGrammar, ruleReference.getName()).get();
      String packageName = getPackageName(ruleSymbol);
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(
          packageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    String qualifiedRuleName;
    for (ASTGenericType typeReference : abstractProd.getASTSuperClassList()) {
      qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdClass);
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(qualifiedRuleName));
    }
  }

  private void translateInterfaceProd(ASTInterfaceProd interfaceProd,
      ASTCDInterface cdInterface, ASTMCGrammar astGrammar) {
    // translates "extends"
    for (ASTRuleReference ruleReference : interfaceProd.getSuperInterfaceRuleList()) {
      MCProdSymbol ruleSymbol = resolveRule(astGrammar, ruleReference.getName()).get();
      String packageName = getPackageName(ruleSymbol);
      cdInterface.getInterfaceList().add(TransformationHelper.createSimpleReference(
          packageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    String qualifiedRuleName;
    for (ASTGenericType typeReference : interfaceProd.getASTSuperInterfaceList()) {
      qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdInterface);
      cdInterface.getInterfaceList().add(
          TransformationHelper.createSimpleReference(qualifiedRuleName));
    }
  }
}
