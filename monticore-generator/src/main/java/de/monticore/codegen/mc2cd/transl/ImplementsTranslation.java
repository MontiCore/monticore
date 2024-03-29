/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.utils.Link;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Checks if the source rules were implementing interface rules and sets the
 * implemented interfaces of the target nodes accordingly.
 * 
 */
public class ImplementsTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(
        ASTClassProd.class,
        ASTCDClass.class)) {
      translateClassProd(link.source(), link.target(), rootLink.source());
    }
    
    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(
        ASTAbstractProd.class,
        ASTCDClass.class)) {
      translateAbstractProd(link.source(), link.target(), rootLink.source());
    }
    
    return rootLink;
  }
  
  protected void translateClassProd(ASTClassProd classProd,
      ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    MCGrammarSymbol grammarSymbol = astGrammar.getSymbol();
    // translates "implements"
    for (ASTRuleReference ruleReference : classProd.getSuperInterfaceRuleList()) {
      Optional<ProdSymbol> ruleSymbol = grammarSymbol.getProdWithInherited(ruleReference.getName());
      if (!ruleSymbol.isPresent()) {
        Log.error("0xA0137 The rule '" + ruleReference.getName() + "' does not exist!", ruleReference.get_SourcePositionStart());
      }
      if (!cdClass.isPresentCDInterfaceUsage()) {
        cdClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
      }
      cdClass.getCDInterfaceUsage().addInterface(
          TransformationHelper.createObjectType(TransformationHelper
              .getPackageName(ruleSymbol.get())
              + "AST"
              + ruleReference.getName()));
    }
    
    // translates "astimplements"
    String qualifiedRuleName;
    for (ASTMCType typeReference : classProd.getASTSuperInterfaceList()) {
      qualifiedRuleName = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(
              typeReference, astGrammar, cdClass);
      if (!cdClass.isPresentCDInterfaceUsage()) {
        cdClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
      }
      cdClass.getCDInterfaceUsage().addInterface(
          TransformationHelper.createObjectType(qualifiedRuleName));
    }
  }
  
  protected void translateAbstractProd(ASTAbstractProd abstractProd,
      ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    // translates "implements"
    for (ASTRuleReference ruleReference : abstractProd
        .getSuperInterfaceRuleList()) {
      MCGrammarSymbol grammarSymbol = astGrammar.getSymbol();
      Optional<ProdSymbol> ruleSymbol = grammarSymbol.getProdWithInherited(ruleReference.getName());
      if (!ruleSymbol.isPresent()) {
        Log.error("0xA0138 The rule '" + ruleReference.getName() + "' does not exist!");
      }
      if (!cdClass.isPresentCDInterfaceUsage()) {
        cdClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
      }
      cdClass.getCDInterfaceUsage().addInterface(
          TransformationHelper.createObjectType(TransformationHelper
              .getPackageName(ruleSymbol.get())
              + "AST"
              + ruleReference.getName()));
    }
    
    // translates "astimplements"
    String qualifiedRuleName;
    for (ASTMCType typeReference : abstractProd.getASTSuperInterfaceList()) {
      qualifiedRuleName = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(
              typeReference, astGrammar, cdClass);
      if (!cdClass.isPresentCDInterfaceUsage()) {
        cdClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
      }
      cdClass.getCDInterfaceUsage().addInterface(
          TransformationHelper.createObjectType(qualifiedRuleName));
    }
  }
  
}
