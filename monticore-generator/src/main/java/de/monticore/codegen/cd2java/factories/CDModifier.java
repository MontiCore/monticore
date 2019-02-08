package de.monticore.codegen.cd2java.factories;

import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

public class CDModifier {

  public static final ASTModifier PUBLIC = CDModifier.builder().Public().build();

  public static final ASTModifier PUBLIC_FINAL = CDModifier.builder().Public().Final().build();

  public static final ASTModifier PUBLIC_ABSTRACT = CDModifier.builder().Public().Abstract().build();

  public static final ASTModifier PUBLIC_STATIC = CDModifier.builder().Public().Static().build();

  public static final ASTModifier PUBLIC_STATIC_FINAL = CDModifier.builder().Public().Static().Final().build();

  public static final ASTModifier PROTECTED = CDModifier.builder().Protected().build();

  public static final ASTModifier PROTECTED_FINAL = CDModifier.builder().Protected().Final().build();

  public static final ASTModifier PROTECTED_ABSTRACT = CDModifier.builder().Protected().Abstract().build();

  public static final ASTModifier PROTECTED_STATIC = CDModifier.builder().Protected().Static().build();

  public static final ASTModifier PROTECTED_STATIC_FINAL = CDModifier.builder().Protected().Static().Final().build();

  public static final ASTModifier PACKAGE_PRIVATE = CDModifier.builder().build();

  public static final ASTModifier PACKAGE_PRIVATE_FINAL = CDModifier.builder().Final().build();

  public static final ASTModifier PACKAGE_PRIVATE_ABSTRACT = CDModifier.builder().Abstract().build();

  public static final ASTModifier PACKAGE_PRIVATE_STATIC = CDModifier.builder().Static().build();

  public static final ASTModifier PACKAGE_PRIVATE_STATIC_FINAL = CDModifier.builder().Static().Final().build();

  public static final ASTModifier PRIVATE = CDModifier.builder().Private().build();

  public static final ASTModifier PRIVATE_FINAL = CDModifier.builder().Private().Final().build();

  public static final ASTModifier PRIVATE_STATIC = CDModifier.builder().Private().Static().build();

  public static final ASTModifier PRIVATE_STATIC_FINAL = CDModifier.builder().Private().Static().build();

  private boolean isPublic = false;

  private boolean isProtected = false;

  private boolean isPrivate = false;

  private boolean isStatic = false;

  private boolean isFinal = false;

  private boolean isAbstract = false;

  private CDModifier() {
  }

  private static CDModifier builder() {
    return new CDModifier();
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

  private CDModifier Public() {
    isPublic = true;
    return this;
  }

  private CDModifier Protected() {
    isProtected = true;
    return this;
  }

  private CDModifier Private() {
    isPrivate = true;
    return this;
  }

  private CDModifier Static() {
    isStatic = true;
    return this;
  }

  private CDModifier Final() {
    isFinal = true;
    return this;
  }

  private CDModifier Abstract() {
    isAbstract = true;
    return this;
  }
}
