/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._ast;

import java.util.ArrayList;
import java.util.List;

public interface ASTProd extends ASTProdTOP {
  
  default List<? extends ASTSymbolDefinition> getSymbolDefinitionList()  {
    return new ArrayList<ASTSymbolDefinition>();
 } 
    
  
}


