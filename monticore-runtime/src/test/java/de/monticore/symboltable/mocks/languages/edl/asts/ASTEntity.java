/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.edl.asts;

import com.google.common.collect.Lists;
import de.monticore.symboltable.mocks.asts.ASTSymbol;
import de.monticore.ast.ASTNode;

import java.util.List;

public class ASTEntity extends ASTSymbol {
  
  public ASTEntity() {
    setSpansScope(true);
    setDefinesNamespace(true);
  }
  
  public List<ASTProperty> getProperties() {
    List<ASTProperty> properties = Lists.newArrayList();
    
    for (ASTNode child : get_Children()) {
      if (child instanceof ASTProperty) {
        properties.add((ASTProperty) child);
      }
    }
    
    return properties;
  }
  
}
