/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

/**
 * Wrapper class for the last result in a type check
 * @deprecated of no use anymore, ISynthesize/IDerive are deprecated
 */
@Deprecated
public class TypeCheckResult {

  protected Optional<SymTypeExpression> result;

  protected boolean type;

  protected boolean method;

  protected boolean field;

  public TypeCheckResult(){
    this.result = Optional.empty();
    this.field = false;
    this.method = false;
    this.type = false;
  }

  public SymTypeExpression getResult() {
    return result.get();
  }

  public boolean isPresentResult() {
    return result.isPresent();
  }

  public void setResult(SymTypeExpression result){
    this.result = Optional.ofNullable(result);
  }
  
  public void setResultAbsent() {
    this.result = Optional.empty();
  }

  public void reset(){
    setResultAbsent();
    field = false;
    method = false;
    type = false;
  }

  public void setType() {
    this.type = true;
    this.field = false;
    this.method = false;
  }

  public void setMethod() {
    this.method = true;
    this.type=false;
    this.field=false;
  }

  public void setField() {
    this.field = true;
    this.type=false;
    this.method=false;
  }

  public void unsetType(){
    this.type=false;
  }

  public boolean isField() {
    return field;
  }

  public boolean isMethod() {
    return method;
  }

  public boolean isType() {
    return type;
  }

  public TypeCheckResult copy(){
    TypeCheckResult result = new TypeCheckResult();
    result.result = this.result;
    result.type = type;
    result.field = field;
    result.method = method;
    return result;
  }
}
