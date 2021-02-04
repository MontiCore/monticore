// (c) https://github.com/MontiCore/monticore
package de.monticore.grammar.grammar._ast;

import java.util.List;

public interface ASTParserProd extends ASTParserProdTOP {

  public List<ASTRuleReference> getSuperInterfaceRuleList();
}
