/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;

public class ParserMock extends MCConcreteParser {

  private final ASTNode astToBeReturned;

  public ParserMock(ASTNode astToBeReturned) {
    this.astToBeReturned = astToBeReturned;
  }


  @Override
  public Optional<? extends ASTNode> parse(String fileName)
      throws IOException {
    return Optional.of(astToBeReturned);
  }

  @Override
  public Optional<? extends ASTNode> parse(Reader reader) throws IOException {
    throw new UnsupportedOperationException();
  }
}
