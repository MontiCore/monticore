/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;

public class ReferenceSymbolSameAttribute implements GrammarASTMCGrammarCoCo {

  @Override
  public void check(ASTMCGrammar node) {
    for (ASTClassProd classProd : node.getClassProdList()) {
      ReferenceSymbolSameAttributeVisitor visitor = new ReferenceSymbolSameAttributeVisitor();
      GrammarTraverser traverser = GrammarMill.traverser();
      traverser.add4Grammar(visitor);
      classProd.accept(traverser);
    }
    for (ASTAbstractProd abstractProd : node.getAbstractProdList()) {
      ReferenceSymbolSameAttributeVisitor visitor = new ReferenceSymbolSameAttributeVisitor();
      GrammarTraverser traverser = GrammarMill.traverser();
      traverser.add4Grammar(visitor);
      abstractProd.accept(traverser);
    }
    for (ASTInterfaceProd interfaceProd : node.getInterfaceProdList()) {
      ReferenceSymbolSameAttributeVisitor visitor = new ReferenceSymbolSameAttributeVisitor();
      GrammarTraverser traverser = GrammarMill.traverser();
      traverser.add4Grammar(visitor);
      interfaceProd.accept(traverser);
    }
  }
}
