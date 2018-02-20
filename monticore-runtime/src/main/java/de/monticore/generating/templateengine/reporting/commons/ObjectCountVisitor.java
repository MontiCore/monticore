/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import java.util.Map;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.SourcePosition;

import com.google.common.collect.Maps;

import de.monticore.visitor.CommonVisitor;

/**
 * We use this visit mechanism to count instances of AST-Node-Types classes. The
 * type2count member maps the AST-Node-Type as String to it's object count.
 *
 * @author BM
 */
public class ObjectCountVisitor implements CommonVisitor {
  
  private Map<String, Integer> type2count;
  
  private Map<String, Integer> type2countPos;
  
  private int totalCount;
  
  @Override
  public void visit(ASTNode a) {
    if (a == null) {
      return;
    }
    totalCount++;
    String key = Layouter.nodeName(a);
    MapUtil.incMapValue(type2count, key);
    // count astnodes with source position
    if (!a.get_SourcePositionStart().equals(SourcePosition.getDefaultSourcePosition())) {
      MapUtil.incMapValue(type2countPos, key);
    }
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
   * Constructor for reporting.ObjectCountVisitor
   */
  public ObjectCountVisitor() {
    super();
    this.type2count = Maps.newHashMap();
    this.type2countPos = Maps.newHashMap();
  }
  
}
