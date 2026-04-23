/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.util;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * used in CodeGen Visitors.
 */
public class Node2Name {

  protected static Node2Name delegate;

  // Map that keeps track of the number of variables
  // that have already been generated of this type
  protected Map<Class<? extends ASTNode>, Integer> class2NumOfInstances =
      new HashMap<>();

  // Maps each instance to its number
  protected Map<ASTNode, Integer> instance2Number = new WeakHashMap<>();

  /**
   * Maps a given ASTNode to a (part of a) name that can be used for variables
   * or similar.
   */
  public static String getName(ASTNode ast) {
    return getDelegate()._getName(ast);
  }

  protected String _getName(ASTNode ast) {
    Preconditions.checkNotNull(ast);
    Class<? extends ASTNode> c = ast.getClass();

    // Assign number to instance if it has not been done before
    // and increment the counter
    if (!instance2Number.containsKey(ast)) {
      int number = class2NumOfInstances.getOrDefault(c, 0);
      instance2Number.put(ast, number);
      class2NumOfInstances.put(c, number + 1);
    }

    String res = "_" + class2Name(c) + instance2Number.get(ast);
    return res;
  }

  protected String class2Name(Class<? extends ASTNode> c) {
    // remove "AST"
    return c.getSimpleName().substring(3);
  }

  // static delegate
  public static void init() {
    Log.trace("init Node2Name", "CodeGen setup");
    setDelegate(new Node2Name());
  }

  public static void reset() {
    Node2Name.delegate = null;
  }

  protected static void setDelegate(Node2Name newDelegate) {
    Node2Name.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static Node2Name getDelegate() {
    if (Node2Name.delegate == null) {
      init();
    }
    return Node2Name.delegate;
  }
}
