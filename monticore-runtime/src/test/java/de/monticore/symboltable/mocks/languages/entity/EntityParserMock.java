/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;
import de.se_rwth.commons.Names;

public class EntityParserMock extends MCConcreteParser {
  
  /**
   * Constructor for de.monticore.symboltable.mocks.parsers.ClassParser
   */
  public EntityParserMock() {
    super();
  }

  @Override
  public Optional<? extends ASTNode> parse(String path) throws IOException {
    String packageName = Names.getPackageFromPath(path);

    ASTEntityCompilationUnit compilationUnit = new ASTEntityCompilationUnit();
    compilationUnit.setPackageName(packageName);

    ASTEntity classNode = new ASTEntity();
    compilationUnit.setClassNode(classNode);


    classNode.setName(Names.getSimpleName(path));
    return Optional.of(compilationUnit);
  }

  @Override
  public Optional<? extends ASTNode> parse(Reader reader) throws IOException {
    throw new UnsupportedOperationException();
  }


}
