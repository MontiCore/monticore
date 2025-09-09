/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.matching;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.*;

public class ModelTraversal<E extends ITraverser> {

  protected Map<String, Collection<ASTNode>> cName2instances = new HashMap<>();
  protected List<ASTNode> all = new ArrayList<>();
  protected Map<ASTNode, ASTNode> parents= new HashMap<>();
  protected Stack<ASTNode> currentparents = new Stack<>();

  private final E traverser;

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


  public Map<ASTNode, ASTNode> getParents(){
    return parents;
  }

  public E getTraverser() {
    return traverser;
  }
}
