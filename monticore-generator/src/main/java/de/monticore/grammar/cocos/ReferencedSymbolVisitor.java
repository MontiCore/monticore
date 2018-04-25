package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarVisitor;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

public class ReferencedSymbolVisitor extends ReferencedSymbolExists implements GrammarVisitor {

  protected ReferencedSymbolVisitor realThis;

  private MCGrammarSymbol grammarSymbol;

  public ReferencedSymbolVisitor(MCGrammarSymbol grammarSymbol){
    this.grammarSymbol = grammarSymbol;
    realThis = this;
  }

  @Override
  public void visit(ASTMCGrammar node){
    for(ASTClassProd astClassProd :node.getClassProdList()){
      astClassProd.accept(getRealThis());
    }
    for(ASTInterfaceProd astInterfaceProd :node.getInterfaceProdList()){
      astInterfaceProd.accept(getRealThis());
    }for(ASTAbstractProd astAbstractProd :node.getAbstractProdList()){
      astAbstractProd.accept(getRealThis());
    }
  }

  @Override
  public void visit(ASTNonTerminal node) {
    if (node.isPresentReferencedSymbol()) {
      String symbol = node.getReferencedSymbol();
      if (grammarSymbol.getProdWithInherited(symbol).isPresent()) {
        if (grammarSymbol.getProdWithInherited(symbol).get().isSymbolDefinition()) {
          return;
        }
      }
      Log.error(String.format(ERROR_CODE + String.format(ERROR_MSG_FORMAT, symbol),
          node.get_SourcePositionStart()));
    }
  }
@Override
   public ReferencedSymbolVisitor getRealThis() {
    return realThis;
  }

  public void setRealThis(ReferencedSymbolVisitor realThis){
    this.realThis = realThis;
  }

}
