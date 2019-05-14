package de.monticore.codegen.mc2cd.transl;

import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.symboltable.SymbolTableGenerator;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolDefinition;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.GeneratorHelper.SYMBOL;

public class SymbolAndScopeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> links) {
    for (Link<ASTClassProd, ASTCDClass> link : links.getLinks(ASTClassProd.class, ASTCDClass.class)) {
      final ASTClassProd astClassProd = link.source();
      final ASTCDClass astcdClass = link.target();

      for (ASTSymbolDefinition symbolDefinition : astClassProd.getSymbolDefinitionList()) {
        if (symbolDefinition.isGenSymbol()) {
          final Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
              .getMCGrammarSymbol(astClassProd);
          if (symbolDefinition.isPresentSymbolName()
              && grammarSymbol.isPresent()) {
            //extra information into stereotype value if a symboltype is already defined in the grammar
            String symbolName = symbolDefinition.getSymbolName();
            String qualifiedName = grammarSymbol.get().getFullName().toLowerCase() + "." +
                SymbolTableGenerator.PACKAGE + "." + symbolName;
            TransformationHelper.addStereoType(astcdClass,
                MC2CDStereotypes.SYMBOL.toString(), qualifiedName + SYMBOL);
          } else {
            TransformationHelper.addStereoType(astcdClass,
                MC2CDStereotypes.SYMBOL.toString());
          }
        } else {
          if (symbolDefinition.isGenScope()) {
            TransformationHelper.addStereoType(astcdClass,
                MC2CDStereotypes.SCOPE.toString());
          }
        }
      }
    }
    return links;
  }
}
