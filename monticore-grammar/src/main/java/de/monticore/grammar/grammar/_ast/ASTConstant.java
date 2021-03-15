
/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._ast;

import de.monticore.grammar.LexNamer;

public class ASTConstant extends ASTConstantTOP {

  @Override
  public String getName() {
    if (isPresentKeyConstant()) {
      return getKeyConstant().getString(0);
    }
    if (isPresentTokenConstant()) {
      return getTokenConstant().getString();
    }
    return getString();
  }

  public String getHumanName() {
    String name;

    if (isPresentUsageName()) {
      name = getUsageName();
    } else {
      String constName = getName();
      if (matchesJavaIdentifier(constName)) {
        name = constName;
      } else {
        name = LexNamer.createGoodName(constName);
       }
    }
    return name;
  }

  private boolean matchesJavaIdentifier(String checkedString) {
    if (checkedString == null || checkedString.length() == 0) {
      return false;
    }
    char[] stringAsChars = checkedString.toCharArray();
    if (!Character.isJavaIdentifierStart(stringAsChars[0])) {
      return false;
    }
    for (int i = 1; i < stringAsChars.length; i++) {
      if (!Character.isJavaIdentifierPart(stringAsChars[i])) {
        return false;
      }
    }
    return true;
  }

}
