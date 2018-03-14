 /* (c) https://github.com/MontiCore/monticore */
 package de.monticore.symboltable;

 import java.util.Optional;

 import de.monticore.ast.ASTNode;
 import de.monticore.symboltable.modifiers.AccessModifier;
 import de.se_rwth.commons.SourcePosition;
 import de.se_rwth.commons.logging.Log;

/**
 * Super type for all symbols of the symbol table.
 *
 * @author  Pedram Mir Seyed Nazari
 *
 */
public interface Symbol {

  /**
   * @return the symbol name
   */
  String getName();

  /**
   * @return the package of this symbol. The package name of all symbols within
   * an artifact is usually the same. For example, the package name of a state
   * chart <code>p.q.SC</code> and its containing state <code>s</code> are the
   * same, i.e., <code>p.q</code>.
   *
   * @see #getFullName()
   */
  String getPackageName();

  /**
   * @return the package of this symbol. All symbols within  an artifact usually
   * have the same package name. For example, the state chart <code>p.q.SC</code>
   * and its containing states all have the package <code>p.q</code>.
   *
   * @see #getPackageName()
   */
  String getFullName();


  /**
   * @return the symbol kind
   */
  SymbolKind getKind();

  /**
   * @return true, if this symbol is of the <code>kind</code>.
   * @see SymbolKind#isKindOf(SymbolKind)
   */
  default boolean isKindOf(SymbolKind kind) {
    return getKind().isKindOf(Log.errorIfNull(kind));
  }

  /**
   * @return the access modifier, such as public or protected in Java. By default, the {@link
   * AccessModifier#ALL_INCLUSION} is returned, which indicates that
   * the symbol does not have any access modifier. Note that this is not the same as the (implicit) access
   * modifier {@link de.monticore.symboltable.modifiers.BasicAccessModifier#PACKAGE_LOCAL} of Java.
   */
  default AccessModifier getAccessModifier() {
    return AccessModifier.ALL_INCLUSION;
  }

  /**
   * Sets the access modifier, such as public or protected in Java.
   * @param accessModifier the access modifier
   */
  void setAccessModifier(AccessModifier accessModifier);

  /**
   * @param node the corresponding ast node
   */
  void setAstNode(ASTNode node);

  /**
   * @return the corresponding ast node
   */
  Optional<ASTNode> getAstNode();

  /**
   * @return the position of this symbol in the source model. By default, it is the source position
   * of the ast node.
   *
   * @see #getAstNode()
   */
  default SourcePosition getSourcePosition() {
    if (getAstNode().isPresent()) {
      return getAstNode().get().get_SourcePositionStart();
    }
    else {
      return SourcePosition.getDefaultSourcePosition();
    }
  }

  /**
   * @return the enclosing scope of this symbol, i.e., the scope that defines this symbol.
   */
  Scope getEnclosingScope();

  /**
   * @param scope the enclosing scope of this symbol, i.e., the scope that defines this symbol.
   */
  void setEnclosingScope(MutableScope scope);
  
}
