package de.monticore.expressions.streamexpressions._ast.util;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.Comparator;

/**
 * Sorting based on source Position,
 * intended to sort siblings/cousins/etc. and nothing else.
 * If useful, this could be moved elsewhere.
 */
public class ASTNodeSiblingComparator implements Comparator<ASTNode> {

  @Override
  public int compare(ASTNode o1, ASTNode o2) {
    int start = compare(o1.get_SourcePositionStart(), o2.get_SourcePositionStart());
    int end = compare(o1.get_SourcePositionEnd(), o2.get_SourcePositionEnd());
    // check if we are indeed sorting siblings or similar
    if (!((start < 0 && end < 0)
        || (start == 0 && end == 0)
        || (start > 0 || end > 0)
    )) {
      Log.error("0xFD54C internal error: unexpected ASTNodes compared");
    }
    return start;
  }

  protected int compare(SourcePosition o1, SourcePosition o2) {
    // check if we are indeed sorting siblings or similar
    if (!o1.getFileName().equals(o2.getFileName())) {
      Log.error("0xFD54D internal error: unexpected ASTNodes compared");
    }
    return o1.compareTo(o2);
  }

}
