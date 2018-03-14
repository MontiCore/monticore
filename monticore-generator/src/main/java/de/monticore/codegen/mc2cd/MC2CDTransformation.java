/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import java.util.function.Function;

import de.monticore.codegen.mc2cd.manipul.CDManipulation;
import de.monticore.codegen.mc2cd.transl.MC2CDTranslation;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;

/**
 * This is the top-level function accepting a MC AST and taking it all the way
 * through to the finished CD AST.
 *
 * @author Sebastian Oberhoff
 */
public class MC2CDTransformation implements Function<ASTMCGrammar, ASTCDCompilationUnit> {
  
  private GlobalExtensionManagement glex;
  
  public MC2CDTransformation(GlobalExtensionManagement glex) {
    this.glex = glex;
  }
  
  @Override
  public ASTCDCompilationUnit apply(ASTMCGrammar grammar) {
    Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink = new Link<>(grammar,
        CD4AnalysisNodeFactory.createASTCDCompilationUnit(), null);
    
    return new MC2CDTranslation(glex)
        .andThen(Link::target)
        .andThen(new CDManipulation())
        .apply(rootLink);
  }
  
}
