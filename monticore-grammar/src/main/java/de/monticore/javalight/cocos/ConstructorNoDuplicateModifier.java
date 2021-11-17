/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTConstructorDeclaration;
import de.monticore.javalight._cocos.JavaLightASTConstructorDeclarationCoCo;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.JavaLightFullPrettyPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstructorNoDuplicateModifier implements JavaLightASTConstructorDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0808";

  public static final String ERROR_MSG_FORMAT = " modifier '%s' is mentioned more than once in the constructor '%s'. ";

  protected String prettyprint(ASTMCModifier a) {
    JavaLightFullPrettyPrinter printer = new JavaLightFullPrettyPrinter(new IndentPrinter());
    a.accept(printer.getTraverser());
    return printer.getPrinter().getContent();
  }

  public Set<String> findDuplicates(List<String> listContainingDuplicates) {
    final Set<String> setToReturn = new HashSet<>();
    final Set<String> set1 = new HashSet<>();

    for (String yourString : listContainingDuplicates) {
      if (!set1.add(yourString)) {
        setToReturn.add(yourString);
      }
    }
    return setToReturn;
  }

  // JLS3 8.8.3-1
  @Override
  public void check(ASTConstructorDeclaration node) {
    //print the modifier -> add to list
    List<String> listModifier = new ArrayList<>();
    for (ASTMCModifier modifier : node.getMCModifierList()) {
      listModifier.add(prettyprint(modifier));
    }
    Set<String> duplicates = findDuplicates(listModifier);
    for (String duplicate : duplicates) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, duplicate, node.getName()),
          node.get_SourcePositionStart());
    }
  }
}
