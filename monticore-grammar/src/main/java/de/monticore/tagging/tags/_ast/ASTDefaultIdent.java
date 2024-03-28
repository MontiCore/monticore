/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.tags._ast;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;

/**
 * The default identifier, which is used to target symbols by their name
 */
public class ASTDefaultIdent extends ASTDefaultIdentTOP {

  public boolean isDefaultIdentifier() {
    return true;
  }

  public ASTNode getIdentifiesElement() {
    Log.error("0x74692: DefaultIdent does not target an element");
    return null;
  }
}
