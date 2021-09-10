/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

public class SynthesizeFromMCBT4Grammar extends SynthesizeSymTypeFromMCBasicTypes {

  @Override
  public void endVisit(ASTMCQualifiedName qName) {
    TypeSymbol surrogate = new TypeSymbolSurrogate(qName.getQName());
    surrogate.setEnclosingScope(getScope(qName.getEnclosingScope()));
    SymTypeExpression symType = SymTypeExpressionFactory.createTypeObject(surrogate);

    typeCheckResult.setCurrentResult(symType);
  }

}
