/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * Sets the modifiers of CDClasses that resulted from AbstractProds to abstract.
 *
 * @author Sebastian Oberhoff
 */
public class AbstractProdModifierTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    rootLink.getLinks(ASTAbstractProd.class, ASTCDClass.class).stream()
        .map(Link::target)
        .map(ASTCDClass::getModifierOpt)
        .forEach(optionalModifier ->
                optionalModifier.ifPresent(modifier -> modifier.setAbstract(true))
        );
    return rootLink;
  }
}
