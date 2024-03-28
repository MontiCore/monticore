/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.util;

import de.monticore.tf.odrules._ast.ASTODRule;

/**
 * Created by
 *
 */
public class NameASTPair  {
  private final String name;
  private final ASTODRule ast;

  public NameASTPair(String name, ASTODRule ast){
    this.name = name;
    this.ast = ast;

  }

  public String getName() {
    return name;
  }

  public ASTODRule getAst() {
    return ast;
  }
}
