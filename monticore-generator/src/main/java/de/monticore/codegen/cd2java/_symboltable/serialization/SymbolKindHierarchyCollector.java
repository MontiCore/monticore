/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import net.sourceforge.plantuml.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class calculates a hierarchy of symbol kinds and stores these in a map. Each entry of the
 * map maps the name of a symbol-defining type to the name of a (transitive) supertype that also
 * defines a symbol.
 * Example:
 * symbol A extends B;
 * B extends C;
 * symbol C extends D;
 * symbol D;
 * Produces the following map: {"A"->"C", "C"->"D"}
 */
public class SymbolKindHierarchyCollector implements CD4AnalysisVisitor {

//  protected String currentType = null;
//
//  private Map<String, String> kindHierarchy = new HashMap<>();
//
//  public static Map<String, String> calculateKindHierarchy(List<ASTCDType> symbolProds) {
//    // iterate over all locally defined symbol classes
//    SymbolKindHierarchyCollector visitor = new SymbolKindHierarchyCollector();
//    symbolProds.forEach(s -> s.getSymbol().accept(visitor));
//    System.out.println(":::: calculate");
//    return visitor.getKindHierarchy();
//  }

  public static Map<String, String> calculateKindHierarchy(List<ASTCDType> symbolProds, SymbolTableService service) {
    Map<String, String> kindHierarchy = new HashMap<>();
    // iterate over all locally defined symbol classes
    for (ASTCDType s : symbolProds) {
      System.out.println(":::: traverse "+s.getName());
      CDTypeSymbol symbol = s.getSymbol();
      if(hasInheritedSymbolStereotype(symbol)){
        String symbolName = service.getSymbolFullName(s);
        String superSymbolName = getSuperSymbolName(symbol);
        kindHierarchy.put(symbolName, superSymbolName);
        System.out.println(":::: put "+symbolName+"  "+superSymbolName);
      }
    }
    return kindHierarchy;
  }

//  public static Map<String, String> calculateKindHierarchy(List<ASTCDType> symbolProds) {
//    Map<String, String> kindHierarchy = new HashMap<>();
//    // iterate over all locally defined symbol classes
//    for (ASTCDType s : symbolProds) {
//      System.out.println(":::: traverse "+s.getName() +" "+s.getSymbol().getName());
//      CDTypeSymbol symbol = s.getSymbol();
//      boolean hasSuperSymbol = hasInheritedSymbolStereotype(symbol);
//      while(hasSuperSymbol){
//        CDTypeSymbol superSymbol = getInheritedSymbol(symbol);
//        System.out.println(":::: put "+symbol.getFullName()+"  "+superSymbol.getFullName());
//        if(null != superSymbol){
//          kindHierarchy.put(symbol.getFullName(), superSymbol.getFullName());
//        }
//        symbol = superSymbol;
//        hasSuperSymbol = hasInheritedSymbolStereotype(symbol);
//      }
//    }
//    return kindHierarchy;
//  }

//  public Map<String, String> getKindHierarchy() {
//    return kindHierarchy;
//  }
//
//  @Override public void visit(CDTypeSymbol symbol) {
//    System.out.println(":::: visit " + symbol.getName() + " with " + currentType);
//    if (hasSymbolStereotype(symbol)) {
//      if (null != currentType) {
//        System.out.println(":::: add " + symbol.getName() + " with " + currentType);
//        kindHierarchy.put(currentType, symbol.getName());
//      }
//      currentType = symbol.getName();
//    }
//  }
//
//  @Override public void traverse(CDTypeSymbol symbol) {
//    System.out.println(":::: traverse "+symbol.getName());
//    for (CDTypeSymbol superType : symbol.getSuperTypes()) {
//      System.out.println(":::: traversing "+symbol.getName() +" with " + superType.getName());
//      superType.accept(this);
//    }
//    currentType = null;
//  }

  protected static String getSuperSymbolName(CDTypeSymbol symbol) {
    return symbol.getStereotype(MC2CDStereotypes.INHERITED_SYMBOL.toString()).get().getValue();
  }

  protected static boolean hasSymbolStereotype(CDTypeSymbol symbol) {
    return symbol.getStereotype(MC2CDStereotypes.SYMBOL.toString()).isPresent();
  }

  protected static boolean hasInheritedSymbolStereotype(CDTypeSymbol symbol) {
    if(null == symbol){
      return false;
    }
    return symbol.getStereotype(MC2CDStereotypes.INHERITED_SYMBOL.toString()).isPresent();
  }

  protected static CDTypeSymbol getInheritedSymbol(CDTypeSymbol symbol) {
    String name = symbol.getStereotype(MC2CDStereotypes.INHERITED_SYMBOL.toString()).get().getValue();
    name = name.substring(0, name.length()-"Symbol".length());
    Optional<CDTypeSymbol> superSymbol = symbol.getEnclosingScope().resolveCDType(name);
    if(superSymbol.isPresent()){
      return superSymbol.get();
    }
    Log.error("0xA184D Cannot find symbol '"+name+"' that is the super kind of '"+symbol.getName()+"'!");
    return null;
  }
}
