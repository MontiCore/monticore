/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator.util;

import java.nio.file.Path;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.CommonSymbol;

/**
 * Every generated main template class implements this interface.
 *
 * @author  Jerome Pfeiffer
 *          $Date$
 *
 */
public interface GeneratorInterface {
  
  public void generate(Path filepath, ASTNode node, CommonSymbol symbol);
  
}
