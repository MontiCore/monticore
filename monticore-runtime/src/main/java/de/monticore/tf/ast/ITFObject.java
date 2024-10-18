/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.ast;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;

/**
 * interface that is implemented by generated ast classes that describe
 * instances of all transformation specific AST classes
 *
 */
public interface ITFObject extends ITFElement {

  /**
   * @return the AST class from the DSL that matches for this objects belong to
   */
  Class<? extends ASTNode> _getTFElementType();


  /**
   * @return if a name given for this object is present
   */
  default boolean isPresentSchemaVarName(){
    return false;
  }

  /**
   * @return the name for this object as specified by the user (
   */
  default String getSchemaVarName() {
    Log.error("ITFObject does not have a schema var name!");
    return null;
  }


}
