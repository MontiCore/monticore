/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTGrammarAnnotation;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;

import static de.se_rwth.commons.logging.Log.error;
import static java.lang.String.format;

public class ProdWithDoubleAnnos implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4119";

  public static final String ERROR_MSG_FORMAT = " The production %s should not use the annotation %s twice.";

  @Override
  public void check(ASTProd node) {
    boolean isOverriden = false;
    boolean isDeprecated = false;
    for (ASTGrammarAnnotation anno: node.getGrammarAnnotationsList()) {
      if (isDeprecated && anno.isDeprecated()) {
        error(format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName(), "@Deprecated"),  node.get_SourcePositionStart());
      }
      if (isOverriden && anno.isOverride()) {
        error(format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName(), "@Override"),  node.get_SourcePositionStart());
      }
      isDeprecated = anno.isDeprecated();
      isOverriden= anno.isOverride();
    }
  }

}
