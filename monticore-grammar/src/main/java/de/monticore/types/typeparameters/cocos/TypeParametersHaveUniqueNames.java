/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typeparameters.cocos;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._ast.ASTTypeParameters;
import de.monticore.types.typeparameters._cocos.TypeParametersASTTypeParametersCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeParametersHaveUniqueNames
    implements TypeParametersASTTypeParametersCoCo {

  @Override
  public void check(ASTTypeParameters node) {
    List<String> names = node.getTypeParameterList().stream()
        .map(ASTTypeParameter::getSymbol)
        .map(TypeSymbol::getName)
        .collect(Collectors.toList());
    Set<String> duplicates = findDuplicates(names);
    for (String dupName : duplicates) {
      Log.error("0xFDC14 The same name \"" + dupName
          + "\" has been used for multiple type parameters."
      );
    }
  }

  // Helper

  protected Set<String> findDuplicates(List<String> listContainingDuplicates) {
    final Set<String> setToReturn = new LinkedHashSet<>();
    final Set<String> set1 = new LinkedHashSet<>();

    for (String modifierName : listContainingDuplicates) {
      if (!set1.add(modifierName)) {
        setToReturn.add(modifierName);
      }
    }
    return setToReturn;
  }

}
