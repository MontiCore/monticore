/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public interface AstPrettyPrinter<T extends ASTNode> {
  
  String prettyPrint(T node);

}
