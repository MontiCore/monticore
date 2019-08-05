package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class ReferenceSymbolSameAttributeVisitor implements Grammar_WithConceptsVisitor {

  public static final String ERROR_CODE = "0xA4100";

  public static final String ERROR_MSG_FORMAT = " The attributes with the UsageName %s, cannot reference to the different symbols %s and %s.";
  protected Map<String, String> map = new HashMap<>();

  @Override
  public void visit(de.monticore.grammar.grammar._ast.ASTNonTerminal node) {
    if(node.isPresentUsageName() && node.isPresentReferencedSymbol()){
      String usageName = node.getUsageName();
      if(map.containsKey(usageName) && !map.get(usageName).equals(node.getReferencedSymbol())){
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, "\""+usageName+"\"",
            map.get(usageName), node.getReferencedSymbol(), node.get_SourcePositionStart()));
      }
      map.put(usageName, node.getReferencedSymbol());
    }
  }

}
