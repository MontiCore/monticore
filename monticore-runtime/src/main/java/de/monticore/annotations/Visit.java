/* (c) https://github.com/MontiCore/monticore */

package de.monticore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The visit annotation was designed to be used in conjunction with the
 * {@link de.monticore.utils.ASTTraverser ASTTraverser} class. Any method annotated with @Visit
 * <i>must</i> have exactly one parameter which must be a subclass of ASTNode (which includes
 * ASTNode itself). It may be inherited and declared private, as well as any other visibility.
 *
 * @author Sebastian Oberhoff
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Visit {
}
