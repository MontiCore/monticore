/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.SourcePosition;

import java.util.*;

import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;

public interface ISymbol {

  /**
   * @return the symbol name
   */
  String getName();

  /**
   * @return the package of this symbol. The package name of all symbols within an artifact is
   * usually the same. For example, the package name of a state chart <code>p.q.SC</code> and its
   * containing state <code>s</code> are the same, i.e., <code>p.q</code>.
   * @see #getFullName()
   */
  String getPackageName();

  /**
   * @return the package of this symbol. All symbols within an artifact usually have the same
   * package name. For example, the state chart <code>p.q.SC</code> and its containing states all
   * have the package <code>p.q</code>.
   * @see #getPackageName()
   */
  String getFullName();

  /**
   * @return Returns the enclosing scope of this symbol. Symbol classes implementing the
   * {@link ISymbol} interface override this method and refine the return type to the scASTope
   * classes of the language.
   */
  IScope getEnclosingScope();

  /**
   * @return the access modifier, such as public or protected in Java. By default, the
   * {@link AccessModifier#ALL_INCLUSION} is returned, which indicates that the symbol does not have
   * any access modifier. Note that this is not the same as the (implicit) access modifier
   * {@link BasicAccessModifier#PACKAGE_LOCAL} of Java.
   */
  default AccessModifier getAccessModifier() {
    return ALL_INCLUSION;
  }

  /**
   * Sets the access modifier, such as public or protected in Java.
   *
   * @param accessModifier the access modifier
   */
  void setAccessModifier(AccessModifier accessModifier);

  boolean isPresentAstNode();

  ASTNode getAstNode();

  /**
   * @return the position of this symbol in the source model. By default, it is the source position
   * of the ast node.
   */
  default SourcePosition getSourcePosition() {
    if (isPresentAstNode()) {
      return getAstNode().get_SourcePositionStart();
    } else {
      return SourcePosition.getDefaultSourcePosition();
    }
  }

  static <T extends ISymbol> List<T> sortSymbolsByPosition(final Collection<T> unorderedSymbols) {
    final List<T> sortedSymbols = new ArrayList<>(unorderedSymbols);
    Collections.sort(sortedSymbols, Comparator.comparing(ISymbol::getSourcePosition));
    return ImmutableList.copyOf(sortedSymbols);
  }

  default void accept (ITraverser visitor)  {
    visitor.handle(this);
  }

}
