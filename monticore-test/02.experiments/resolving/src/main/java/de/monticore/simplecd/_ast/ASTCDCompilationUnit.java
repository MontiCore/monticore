/* (c) https://github.com/MontiCore/monticore */
package de.monticore.simplecd._ast;

import java.util.ArrayList;
import java.util.List;

public class ASTCDCompilationUnit extends ASTCDCompilationUnitTOP {

  public int sizePackage() {
    return this.getCDPackageList().size();
  }

  public List<String> getCDPackageList() {
    if (isPresentMCPackageDeclaration()) {
      return this.mCPackageDeclaration.get().getMCQualifiedName().getPartsList();
    } else {
      return new ArrayList<String>();
    }
  }

}
