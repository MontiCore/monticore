/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import de.monticore.symboltable.IScope;
import de.monticore.tagging.tags._ast.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AbstractTagger {

  /**
   * Construct the difference of two scopes in regards to the named scopes
   */
  protected List<String> getScopeDifferences(IScope scope, IScope target) {
    List<String> scopeStack = new ArrayList<>();
    while (scope != target) {
      if (!scope.isPresentName()) break;
      scopeStack.add(0, scope.getName());
      scope = scope.getEnclosingScope();
    }
    return scopeStack;
  }

  protected static Stream<ASTTargetElement> findTargetsBy(ASTTagUnit astTag, String symbolName) {
    return astTag.getTagsList().stream()
            .filter(t -> t.getModelElementIdentifierList().stream().anyMatch(i -> isIdentified(i, symbolName)));
  }

  protected static Stream<ASTTargetElement> findTargetsBy(ASTContext astContext, String symbolName) {
    return astContext.getTagsList().stream()
            .filter(t -> t.getModelElementIdentifierList().stream().anyMatch(i -> isIdentified(i, symbolName)));
  }

  public static boolean isIdentified(ASTModelElementIdentifier elementIdentifier, String string) {
    if (elementIdentifier instanceof ASTDefaultIdent) {
      return ((ASTDefaultIdent) elementIdentifier).getMCQualifiedName().toString().equals(string);
    }
    return false;
  }

  protected static Stream<ASTContext> findContextBy(ASTTagUnit astTag, String symbolName) {
    return astTag.getContextsList().stream()
            .filter(c -> isIdentified(c.getModelElementIdentifier(), symbolName));
  }

  protected static Stream<ASTContext> findContextBy(ASTContext astTag, String symbolName) {
    return astTag.getContextsList().stream()
            .filter(c -> isIdentified(c.getModelElementIdentifier(), symbolName));
  }

  protected de.monticore.symboltable.IScope getArtifactScope(@Nonnull de.monticore.symboltable.IScope s) {
    while (!(s instanceof de.monticore.symboltable.IArtifactScope)) {
      s = s.getEnclosingScope();
    }
    return s;

  }
}
