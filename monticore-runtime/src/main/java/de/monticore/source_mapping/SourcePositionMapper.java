package de.monticore.source_mapping;

import de.se_rwth.commons.SourcePosition;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateObject;
import freemarker.core.TextBlock;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SourcePositionMapper {
  protected static AtomicInteger pairId = new AtomicInteger();

  public static Template adaptTemplateWithPositionMarkers(Template result, Configuration configuration) throws IOException {
    List<TemplateElement> tes = new ArrayList<>();
    TemplateElement rootTreeNode = result.getRootTreeNode();
    inorderTraversal(rootTreeNode, tn -> tes.add((TemplateElement) tn));

    String canonicalForm = rootTreeNode.toString();
    StringBuilder sb = new StringBuilder(canonicalForm);

    Comparator<TemplateElement> firstComp = Comparator.comparingInt(TemplateObject::getEndLine);
    Comparator<TemplateElement> c = firstComp.thenComparingInt(TemplateObject::getEndColumn);
    tes.stream().sorted(c.reversed()).forEach(t -> {
      if (t.getClass().getName().contains("DollarVariable")) {
        addSourcePositionReport(t, sb, canonicalForm);
      }
      if (t instanceof TextBlock) {
        if (!t.getCanonicalForm().isBlank()) {
          addSourcePositionReport(t, sb, canonicalForm);
        }
      }
    });

    return new Template(result.getName(), sb.toString(), configuration);
  }

  private static void addSourcePositionReport(TemplateElement t, StringBuilder sb, String canonicalForm) {
    String endPos = reportingExpressionEnd(new SourcePosition(t.getEndLine(), t.getEndColumn(), t.getTemplate().getName()), pairId.get());
    String startPos = reportingExpressionStart(new SourcePosition(t.getBeginLine(), t.getBeginColumn(), t.getTemplate().getName()), pairId.get());
    sb.insert(lineColumnToOffset(canonicalForm, t.getEndLine(), t.getEndColumn()) + 1, endPos);
    sb.insert(lineColumnToOffset(canonicalForm, t.getBeginLine(), t.getBeginColumn()), startPos);
    pairId.getAndIncrement();
  }

  protected static String reportingExpressionStart(SourcePosition p, int pairId) {
    return "${sourceMappingUtil.reportStart(" + pairId + "," + +p.getLine() + "," + p.getColumn() + ",ast)}";
  }

  protected static String reportingExpressionEnd(SourcePosition p, int pairId) {
    return "${sourceMappingUtil.reportEnd(" + pairId + "," + +p.getLine() + "," + p.getColumn() + ",ast)}";
  }

  public static int lineColumnToOffset(String input, int lineNumber, int columnNumber) {
    int currentLine = 1;
    int offset = 0;

    for (int i = 0; i < input.length(); i++) {
      if (currentLine == lineNumber) {
        return offset + columnNumber - 1;
      }

      if (input.charAt(i) == '\n') {
        currentLine++;
        offset = i + 1;
      }
    }

    return -1;
  }

  protected static void inorderTraversal(TreeNode node, Consumer<TreeNode> c) {
    var children = node.children();
    while (children.hasMoreElements()) {
      TreeNode child = children.nextElement();
      inorderTraversal(child, c);
    }

    c.accept(node);
  }
}
