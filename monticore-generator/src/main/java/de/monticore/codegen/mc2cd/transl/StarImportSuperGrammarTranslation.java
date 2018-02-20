/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.TypesMill;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

/**
 * Adds the fully qualified names of supergrammars as star-imports to the ASTCDCompilationUnit of
 * the CD AST.
 * 
 * @author Sebastian Oberhoff
 */
public class StarImportSuperGrammarTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    ASTMCGrammar grammar = rootLink.source();
    if (grammar.getSymbol().isPresent()) {
      MCGrammarSymbol symbol = (MCGrammarSymbol) grammar.getSymbol().get();
      for (MCGrammarSymbol superSymbol : symbol.getSuperGrammarSymbols()) {
        List<String> names = Arrays.asList(superSymbol.getFullName().split("\\."));
        ASTImportStatement importStatement = TypesMill.importStatementBuilder().setImportList(names)
            .setStar(true).build();
        ;
        rootLink.target().getImportStatementList().add(importStatement);
      }
    }  
    return rootLink;
  }
  
}
