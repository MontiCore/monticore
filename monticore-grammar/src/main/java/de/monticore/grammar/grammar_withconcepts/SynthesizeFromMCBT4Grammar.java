/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.Optional;

public class SynthesizeFromMCBT4Grammar extends SynthesizeSymTypeFromMCBasicTypes {

  @Override
  protected Optional<SymTypeExpression> handleIfNotFound(ASTMCQualifiedName qName) {
    TypeSymbol surrogate = new TypeSymbolSurrogate(qName.getQName());
    surrogate.setEnclosingScope(getScope(qName.getEnclosingScope()));
    return Optional.of(SymTypeExpressionFactory.createTypeObject(surrogate));
  }
}
