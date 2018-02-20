/* (c) https://github.com/MontiCore/monticore */

package de.monticore.modelloader;

import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelCoordinate;

/**
 * This interface abstracts away the origin of ASTs, allowing higher levels to be ignorant of
 * whether ASTs are parsed from a File using I/O or if they're parsed from editor input.
 *
 * @author Sebastian Oberhoff, Pedram Mir Seyed Nazari
 */
public interface AstProvider<T extends ASTNode> {

  T getRootNode(ModelCoordinate modelCoordinate);

}
