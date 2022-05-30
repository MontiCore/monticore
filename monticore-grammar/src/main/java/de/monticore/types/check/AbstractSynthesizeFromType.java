/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesScope;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public abstract class AbstractSynthesizeFromType {

  public abstract MCBasicTypesTraverser getTraverser();

  public IBasicSymbolsScope getScope (IMCBasicTypesScope mcBasicTypesScope){
    // is accepted only here, decided on 07.04.2020
    if(!(mcBasicTypesScope instanceof IBasicSymbolsScope)){
      Log.error("0xA1308 the enclosing scope of the type does not implement the interface IBasicSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IBasicSymbolsScope) mcBasicTypesScope;
  }

  /**
   * Storage in the Visitor: result of the last endVisit.
   * This attribute is synthesized upward.
   */
  public TypeCheckResult typeCheckResult = new TypeCheckResult();

  public Optional<SymTypeExpression> getResult() {
    return Optional.of(getTypeCheckResult().getResult());
  }

  public void init() {
    typeCheckResult = new TypeCheckResult();
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    this.typeCheckResult = typeCheckResult;
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }
}
