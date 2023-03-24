/* (c) https://github.com/MontiCore/monticore */
package de.monticore.simplecd;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import java.util.Arrays;
import java.util.List;

public class MCQualifiedNameFacade {
  private static final String PACKAGE_SEPARATOR = "\\.";

  public static ASTMCQualifiedName createQualifiedName(String qName) {
    return MCBasicTypesMill.mCQualifiedNameBuilder()
      .setPartsList(MCQualifiedNameFacade.createPartList(qName))
      .build();
  }

  public static List<String> createPartList(String qName) {
    return Arrays.asList(qName.split(PACKAGE_SEPARATOR));
  }
}

