/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SynthesizeSymTypeFromMCSimpleGenericTypes;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;

import java.util.List;
import java.util.Optional;

public class SynthesizeFromMCSGT4Grammar extends SynthesizeSymTypeFromMCSimpleGenericTypes {

  @Override
  protected SymTypeExpression handleIfNotFound(ASTMCGenericType type, List<SymTypeExpression> arguments) {
    TypeSymbol surrogate = new TypeSymbolSurrogate(String.join(".", type.getNameList()));
    surrogate.setEnclosingScope(getScope(type.getEnclosingScope()));
    return SymTypeExpressionFactory.createGenerics(surrogate, arguments);
  }
}
