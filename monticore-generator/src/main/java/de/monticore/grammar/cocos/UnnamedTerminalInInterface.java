/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._cocos.GrammarASTInterfaceProdCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * this coco checks that an interface does not contain unnamed terminals
 * because they do not influence the ast structure
 */
public class UnnamedTerminalInInterface implements GrammarASTInterfaceProdCoCo {

  public static final String ERROR_CODE = "0xA0120";

  public static final String ERROR_MSG_FORMAT = " Interface '%s' is not allowed to contain the unnamed %s \"%s\".";

  @Override
  public void check(ASTInterfaceProd node) {
    checkUnnamedTerminal(node.getName(), node.getAltList());
  }

  private void checkUnnamedTerminal(String interfaceName, List<ASTAlt> astAltList) {
    for (ASTAlt astAlt : astAltList) {
      for (ASTRuleComponent astRuleComponent : astAlt.getComponentList()) {
        if (astRuleComponent instanceof ASTTerminal && !((ASTTerminal) astRuleComponent).isPresentUsageName()) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, interfaceName, "Terminal", astRuleComponent.getName()));
        } else if (astRuleComponent instanceof ASTKeyTerminal && !((ASTKeyTerminal) astRuleComponent).isPresentUsageName()) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, interfaceName, "KeyTerminal", astRuleComponent.getName()));
        } else if (astRuleComponent instanceof ASTTokenTerminal && !((ASTTokenTerminal) astRuleComponent).isPresentUsageName()) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, interfaceName, "KeyTerminal", astRuleComponent.getName()));
        } else if (astRuleComponent instanceof ASTBlock) {
          checkUnnamedTerminal(interfaceName, ((ASTBlock) astRuleComponent).getAltList());
        }
      }
    }
  }
}
