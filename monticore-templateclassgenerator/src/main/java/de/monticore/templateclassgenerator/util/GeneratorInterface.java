/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator.util;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.CommonSymbol;

import java.nio.file.Path;

/**
 * Every generated main template class implements this interface.
 *
 */
public interface GeneratorInterface {
  
  public void generate(Path filepath, ASTNode node, CommonSymbol symbol);
  
}
