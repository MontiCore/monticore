/* (c) https://github.com/MontiCore/monticore */

package de.monticore.annotations;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import de.monticore.ast.ASTNode;

/**
 * This is a helper class for various operations related to the {@link Visit} annotation.
 * 
 * @author Sebastian Oberhoff
 */
public final class VisitAnnotations {
  
  private VisitAnnotations() {
  }
  
  /**
   * Extracts all the methods from the passed object carrying the {@link Visit} annotation.
   * 
   * @param visitingObject an object with methods annotated with @visit
   * @return a set containing all methods with the @visit annotation
   * @throws UnsupportedOperationException if one of the methods does not meet the contract set by
   * the @Visit annotation
   */
  public static Set<Method> visitMethods(Object visitingObject) {
    // using a set ensures that public methods, which are returned both by getMethods() and
    // getDeclaredMethods(), aren't gathered twice
    Set<Method> allMethods = new LinkedHashSet<>();
    // gather inherited visit methods
    allMethods.addAll(Arrays.asList(visitingObject.getClass().getMethods()));
    // gather private visit methods
    allMethods.addAll(Arrays.asList(visitingObject.getClass().getDeclaredMethods()));
    
    Set<Method> visitMethods = new LinkedHashSet<>();
    for (Method method : allMethods) {
      if (method.isAnnotationPresent(Visit.class)) {
        checkVisitMethod(method);
        method.setAccessible(true);
        visitMethods.add(method);
      }
    }
    return visitMethods;
  }
  
  /**
   * Checks the validity of a method annotated with the {@link Visit} annotation.
   * 
   * @param visitMethod the method to be checked
   * @throws UnsupportedOperationException if the method violates the contract set by the @visit
   * annotation
   */
  private static void checkVisitMethod(Method visitMethod) {
    
    int parameterCount = visitMethod.getParameterCount();
    if (parameterCount != 1) {
      throw new UnsupportedOperationException(
          "Visit methods must take exactly one argument. The method \"" + visitMethod + "\" took "
              + parameterCount + ".");
    }
    
    Class<?> parameterType = visitMethod.getParameterTypes()[0];
    if (!ASTNode.class.isAssignableFrom(parameterType)) {
      throw new UnsupportedOperationException(
          "Visit methods may only accept subclasses of ASTNode.");
    }
  }
}
