package de.monticore.codegen.cd2java.factories;

import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

public class ModifierBuilder {

  private boolean isPublic = false;

  private boolean isProtected = false;

  private boolean isPrivate = false;

  private boolean isStatic = false;

  private boolean isFinal = false;

  private boolean isAbstract = false;

  private ModifierBuilder() {
  }

  public static ModifierBuilder builder() {
    return new ModifierBuilder();
  }

  public ASTModifier build() {
    return CD4AnalysisMill.modifierBuilder()
        .setPublic(isPublic)
        .setProtected(isProtected)
        .setPrivate(isPrivate)
        .setStatic(isStatic)
        .setFinal(isFinal)
        .setAbstract(isAbstract)
        .build();
  }

  public ModifierBuilder Public() {
    isPublic = true;
    return this;
  }

  public ModifierBuilder Protected() {
    isProtected = true;
    return this;
  }

  public ModifierBuilder Private() {
    isPrivate = true;
    return this;
  }

  public ModifierBuilder Static() {
    isStatic = true;
    return this;
  }

  public ModifierBuilder Final() {
    isFinal = true;
    return this;
  }

  public ModifierBuilder Abstract() {
    isAbstract = true;
    return this;
  }
}
