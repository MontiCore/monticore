/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * Sets the modifiers of CDClasses that resulted from AbstractProds to abstract.
 *
 */
public class AbstractProdModifierTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    rootLink.getLinks(ASTAbstractProd.class, ASTCDClass.class).stream()
        .map(Link::target)
        .map(ASTCDClass::getModifier)
        .forEach(modifier -> modifier.setAbstract(true));
    return rootLink;
  }
}
