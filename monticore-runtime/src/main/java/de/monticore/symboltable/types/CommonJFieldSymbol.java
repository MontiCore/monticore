/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * @author Pedram Mir Seyed Nazari
 */
public abstract class CommonJFieldSymbol<T extends JTypeReference<? extends JTypeSymbol>> extends CommonSymbol implements JFieldSymbol {

  private T type;

  private boolean isFinal;
  private boolean isStatic;
  private boolean isParameter = false;

  public CommonJFieldSymbol(String name, JAttributeSymbolKind kind, T type) {
    super(name, kind);
    this.type = type;
  }

  @Override
  public T getType() {
    return type;
  }

  public void setType(T type) {
    this.type = type;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
  }

  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }

  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }

  @Override
  public boolean isFinal() {
    return isFinal;
  }

  public void setParameter(boolean isParameter) {
    this.isParameter = isParameter;
  }

  @Override
  public boolean isParameter() {
    return isParameter;
  }

  public void setPrivate() {
    setAccessModifier(BasicAccessModifier.PRIVATE);
  }

  public void setProtected() {
    setAccessModifier(BasicAccessModifier.PROTECTED);
  }

  public void setPublic() {
    setAccessModifier(BasicAccessModifier.PUBLIC);
  }

  @Override
  public boolean isPrivate() {
    return getAccessModifier().equals(BasicAccessModifier.PRIVATE);
  }

  @Override
  public boolean isProtected() {
    return getAccessModifier().equals(BasicAccessModifier.PROTECTED);
  }

  @Override
  public boolean isPublic() {
    return getAccessModifier().equals(BasicAccessModifier.PUBLIC);
  }

}
