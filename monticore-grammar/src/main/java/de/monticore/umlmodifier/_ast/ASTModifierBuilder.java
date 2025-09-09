/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlmodifier._ast;

import de.monticore.umlstereotype.UMLStereotypeMill;
import de.monticore.umlstereotype._ast.ASTStereoValue;

import java.util.Arrays;
import java.util.Optional;

public class ASTModifierBuilder extends ASTModifierBuilderTOP {
  public ASTModifierBuilder addStereoValues(ASTStereoValue... value) {
    if (!this.stereotype.isPresent()) {
      this.stereotype = Optional.of(UMLStereotypeMill.stereotypeBuilder().build());
    }
    this.stereotype.get().addAllValues(Arrays.asList(value));
    return this.realBuilder;
  }

  public ASTModifierBuilder PUBLIC() {
    setPublic(true);
    return this.realBuilder;
  }

  public ASTModifierBuilder PRIVATE() {
    setPrivate(true);
    return this.realBuilder;

  }

  public ASTModifierBuilder PROTECTED() {
    setProtected(true);
    return this.realBuilder;
  }

  public ASTModifierBuilder FINAL() {
    setFinal(true);
    return this.realBuilder;
  }

  public ASTModifierBuilder ABSTRACT() {
    setAbstract(true);
    return this.realBuilder;
  }

  public ASTModifierBuilder LOCAL() {
    setLocal(true);
    return this.realBuilder;
  }

  public ASTModifierBuilder DERIVED() {
    setDerived(true);
    return this.realBuilder;

  }

  public ASTModifierBuilder READONLY() {
    setReadonly(true);
    return this.realBuilder;
  }

  public ASTModifierBuilder STATIC() {
    setStatic(true);
    return this.realBuilder;
  }

}
