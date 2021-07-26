/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.utils.Link;

import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Adds the CDClass corresponding to another rule 'X' as a superclass to the CDClass corresponding
 * to 'Y' if 'Y' overwrites 'X'.
 * 
 */
public class OverridingClassProdTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      
      Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper
          .resolveRuleInSupersOnly(
              link.source(),
              link.source().getName());
      if (ruleSymbol.isPresent() && !ruleSymbol.get().isIsExternal()) {
        String qualifiedASTNodeName = TransformationHelper.getPackageName(ruleSymbol.get()) + "AST"
            + ruleSymbol.get().getName();
        if (!link.target().isPresentCDExtendUsage()) {
          link.target().setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().build());
        }
        link.target().getCDExtendUsage().addSuperclass(
            TransformationHelper.createObjectType(qualifiedASTNodeName));
      }
      
    }
    
    return rootLink;
  }
}
