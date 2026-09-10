/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.matching;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.*;

/**
 * @deprecated This traversal should no longer be used. Pattern matching now relies on
 * incremental indices instead of this traversal-based state.
 */
@Deprecated(since = "7.9.0")
public class ModelTraversal<E extends ITraverser> {

  protected Map<String, Collection<ASTNode>> cName2instances = new LinkedHashMap<>();
  protected List<ASTNode> all = new ArrayList<>();
  protected Map<ASTNode, ASTNode> parents= new LinkedHashMap<>();
  protected Stack<ASTNode> currentparents = new Stack<>();

  protected final E traverser;

  protected ModelTraversal(E traverser) {
    this.traverser = traverser;
  }

  public Collection<ASTNode> getInstances(String className) {
    if (cName2instances.containsKey(className)) {
      return cName2instances.get(className);
    }
    return new LinkedList<>();
  }

  public List<ASTNode> getAll(){
    return all;
  }

  public boolean containsKey(String key){
    return cName2instances.containsKey(key);
  }

  public ASTNode getParent(ASTNode node){
    return parents.get(node);
  }

  public void reset() {
    this.cName2instances.clear();
    this.all.clear();
    this.parents.clear();
    this.currentparents.clear();
    this.getTraverser().clearTraversedElements();
  }

  public Map<ASTNode, ASTNode> getParents(){
    return parents;
  }

  public E getTraverser() {
    return traverser;
  }
}
