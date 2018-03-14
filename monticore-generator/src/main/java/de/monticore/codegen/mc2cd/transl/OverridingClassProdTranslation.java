/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import java.util.Optional;
import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

/**
 * Adds the CDClass corresponding to another rule 'X' as a superclass to the CDClass corresponding
 * to 'Y' if 'Y' overwrites 'X'.
 * 
 * @author Sebastian Oberhoff
 */
public class OverridingClassProdTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      
      Optional<MCProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper
          .resolveRuleInSupersOnly(
              rootLink.source(),
              link.source().getName());
      if (ruleSymbol.isPresent() && !ruleSymbol.get().isExternal()) {
        String qualifiedASTNodeName = TransformationHelper.getPackageName(ruleSymbol.get()) + "AST"
            + ruleSymbol.get().getName();
        link.target().setSuperclass(
            TransformationHelper.createSimpleReference(qualifiedASTNodeName));
      }
      
    }
    
    return rootLink;
  }
}
