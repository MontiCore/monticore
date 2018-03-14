/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import java.util.Optional;
import java.util.function.UnaryOperator;


import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

/**
 * Creates the ASTCDAttributes corresponding to NonTerminals
 *
 * @author Sebastian Oberhoff
 */
class NonTerminalsWithSymbolReferenceToCDAttributeStereotypes implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTNonTerminal, ASTCDAttribute> link : rootLink.getLinks(ASTNonTerminal.class,
        ASTCDAttribute.class)) {
      final ASTNonTerminal nonTerminal = link.source();
      final ASTCDAttribute cdAttribute = link.target();
      
      if (nonTerminal.isPresentReferencedSymbol()) {
        final Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
            .getMCGrammarSymbol(nonTerminal);
        if (grammarSymbol.isPresent()) {
          final Optional<MCProdSymbol> referencedSymbol = grammarSymbol.get()
              .getProdWithInherited(nonTerminal.getReferencedSymbol());
          if (referencedSymbol.isPresent()) {
            final String referencedSymbolName = TransformationHelper
                .getGrammarName(referencedSymbol.get()) + "." + referencedSymbol.get().getName()
                + "Symbol";
            
            TransformationHelper.addStereoType(cdAttribute,
                MC2CDStereotypes.REFERENCED_SYMBOL.toString(), referencedSymbolName);
          }
        }
      }
    }

    return rootLink;
  }

}
