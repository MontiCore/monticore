/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class calculates a hierarchy of symbol kinds and stores these in a map. Each entry of the
 * map maps the name of a symbol-defining type to the name of a (transitive) supertype that also
 * defines a symbol.
 * Example:
 *   symbol A extends B;
 *   B extends C;
 *   symbol C extends D;
 *   symbol D;
 * Produces the following map: {"A"->"C", "C"->"D"}
 */
public class SymbolKindHierarchyVisitor implements CD4AnalysisVisitor {

  protected String currentType = "";

  private Map<String, String> kindHierarchy = new HashMap<>();

  public static Map<String,String> calculateKindHierarchy(List<ASTCDType> symbolProds) {
    // iterate over all locally defined symbol classes
    SymbolKindHierarchyVisitor visitor = new SymbolKindHierarchyVisitor();
    for (ASTCDType symbolProd : symbolProds) {
      symbolProd.accept(visitor.getRealThis());
    }
    return visitor.getKindHierarchy();
  }

  public Map<String, String> getKindHierarchy() {
    return kindHierarchy;
  }

  @Override public void visit(ASTCDClass node) {
    if (hasSymbolStereotype(node)) {
      kindHierarchy.put(currentType, node.getName());
    }
  }

  @Override public void visit(ASTCDInterface node) {
    if (hasSymbolStereotype(node)) {
      kindHierarchy.put(currentType, node.getName());
    }
  }

  @Override public void traverse(ASTCDClass node) {
    if (hasSymbolStereotype(node)) {
      currentType = node.getName();
      if (node.isPresentSuperclass()) {
        node.getSuperclass().accept(getRealThis());
      }
      for (ASTMCObjectType intf : node.getInterfaceList()) {
        intf.accept(getRealThis());
      }
    }
  }

  @Override public void traverse(ASTCDInterface node) {
    if (hasSymbolStereotype(node)) {
      currentType = node.getName();
      for (ASTMCObjectType intf : node.getInterfaceList()) {
        intf.accept(getRealThis());
      }
    }
  }

  protected boolean hasSymbolStereotype(ASTCDType type) {
    if (type.getModifier().isPresentStereotype()) {
      return type.getModifier().getStereotype().getValueList().stream()
          .anyMatch(v -> v.getName().equals(
              MC2CDStereotypes.SYMBOL));
    }
    return false;
  }
}
