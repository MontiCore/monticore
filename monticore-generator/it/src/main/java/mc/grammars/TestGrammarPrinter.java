/* (c) https://github.com/MontiCore/monticore */

package mc.grammars;

import de.se_rwth.commons.Names;
import mc.grammar.ittestgrammar._ast.ASTGenericType;

/**
 * Some helper methods for GrammarDSL
 * 
 * @author krahn
 */
public class TestGrammarPrinter extends de.monticore.grammar.HelperGrammar {
  
  public static String printGenericType(ASTGenericType genericType) {
    
    StringBuilder b = new StringBuilder();
    
    b.append(Names.getQualifiedName(genericType.getNameList()));
    
    boolean first = true;
    for (ASTGenericType t : genericType.getGenericTypeList()) {
      if (first) {
        b.append("<");
        first = false;
      }
      else {
        b.append(",");
        
      }
      
      b.append(printGenericType(t));
    }
    
    if (!first) {
      b.append(">");
    }
    
    int dimension = genericType.getDimension();
    for (int i = dimension; i > 0; i--) {
      b.append("[]");
    }
    
    return b.toString();
  }
  
 
  
}
