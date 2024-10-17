/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;

import java.util.List;

/**
 * This class can be used to calculate potential qualified names for a symbol.
 * It needs the packageDeclaration of the model, the import statments specified in the model and the simple name of the symbol.
 */
public class CalculatePotentialQNames {

  public static List<String> calculateQualifiedNames(ASTMCPackageDeclaration packageDeclaration, List<ASTMCImportStatement> importStatements, String simpleName){
    List<String> potentialQNames = Lists.newArrayList();
    //if in a spanned scope
    potentialQNames.add(simpleName);
    //if in the same package
    if(packageDeclaration!=null && !packageDeclaration.getMCQualifiedName().getQName().isEmpty()) {
      potentialQNames.add(packageDeclaration.getMCQualifiedName().getQName() + "." + simpleName);
    }
    //import statements
    for(ASTMCImportStatement importStatement: importStatements){
      if(importStatement.getMCQualifiedName() != null && !importStatement.getMCQualifiedName().getQName().isEmpty()) {
        if (importStatement.isStar()) {
          potentialQNames.add(importStatement.getQName() + "." + simpleName);
        } else {
          if (importStatement.getMCQualifiedName().getBaseName().equals(simpleName)) {
            potentialQNames.add(importStatement.getQName());
          }
        }
      }
    }
    return potentialQNames;
  }
}
