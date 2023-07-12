/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import com.google.common.collect.Maps;
import de.monticore.ast.ASTNode;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.SourcePosition;

import java.util.Map;


/**
 * We use this visit mechanism to count instances of AST-Node-Types classes. The
 * type2count member maps the AST-Node-Type as String to it's object count.
 *
 */
public class ObjectCountVisitor implements IVisitor {
  
  protected Map<String, Integer> type2count;
  
  protected Map<String, Integer> type2countPos;
  
  protected int totalCount;

  protected int maxDepth;

  protected int depth;
  
  @Override
  public void visit(ASTNode a) {
    totalCount++;
    depth++;
    String key = Layouter.nodeName(a);
   type2count.merge(key, 1, Integer::sum);
    // count astnodes with source position
    if (!a.get_SourcePositionStart().equals(SourcePosition.getDefaultSourcePosition())) {
      type2countPos.merge(key, 1, Integer::sum);
    }
  }

  @Override
  public void endVisit(ASTNode a) {
    if (depth>maxDepth) {
      maxDepth = depth;
    }
    depth--;
  }

  /**
   * Return the result map
   */
  public Map<String, Integer> getObjectCountMap() {
    return this.type2count;
  }
  
  /**
   * Return the result map
   */
  public Map<String, Integer> getObjectCountMapPos() {
    return this.type2countPos;
  }
  
  /**
   * Return the total object count
   */
  public int getTotalCount() {
    return this.totalCount;
  }

  /**
   * Return the max depth
   */
  public int getMaxDepth() {
    return this.maxDepth;
  }

  /**
   * Constructor for reporting.ObjectCountVisitor
   */
  public ObjectCountVisitor() {
    this.type2count = Maps.newHashMap();
    this.type2countPos = Maps.newHashMap();
    this.totalCount = 0;
    this.maxDepth = 0;
    this.depth = 0;
  }

  public void clear() {
    this.type2count.clear();
    this.type2countPos.clear();
    this.totalCount = 0;
    this.maxDepth = 0;
    this.depth = 0;
  }
  
}
