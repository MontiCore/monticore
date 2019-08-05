/* (c) https://github.com/MontiCore/monticore */
package _templates.templates.maintemplates;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ISymbol;

import java.nio.file.Path;

public class HelloMainImpl extends HelloMain{

  @Override
  public void generate(Path filepath, ASTNode node, ISymbol symbol) {
    generate(filepath, node, "s", 2, 2.1);
  }


  
}
