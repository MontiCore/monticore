/* (c) https://github.com/MontiCore/monticore */
package _templates.templates.maintemplates;

import java.nio.file.Path;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.CommonSymbol;

public class HelloMainImpl extends HelloMain{

  /**
   * @see _templates.templates.maintemplates.HelloMain#doGenerate(java.nio.file.Path, de.monticore.ast.ASTNode, de.monticore.symboltable.CommonSymbol)
   */
  @Override
  public void generate(Path filepath, ASTNode node, CommonSymbol symbol) {
    generate(filepath, node, "s", 2, 2.1);
  }


  
}
