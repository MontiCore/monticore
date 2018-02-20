/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * This class can be viewed as a special syntactic translation, whereas other translations are of
 * semantic nature. It only concerns itself with mirroring the MC AST structure over to the CD AST
 * and building the corresponding Link structure as it goes along.
 *
 * @author Sebastian Oberhoff
 */
public class CDASTCreator implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    return new GrammarToCDDefinition()
        .andThen(new ClassProdsToCDClasses())
        .andThen(new AbstractProdsToCDClasses())
        .andThen(new InterfaceProdsToCDInterfaces())
        .andThen(new EnumProdsToCDEnums())
        .andThen(new ExternalProdsToCDInterfaces())
        .andThen(new ASTRulesToCDClassesAndCDInterfaces())
        .andThen(new AttributeInASTsToCDAttributes())
        .andThen(new NonTerminalsToCDAttributes())
        .andThen(new TerminalsToCDAttributes())
        .apply(rootLink);
  }
}
