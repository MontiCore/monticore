package de.monticore.generating.templateengine.source_mapping;

import de.monticore.generating.templateengine.TemplateController;
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

import static de.monticore.generating.templateengine.source_mapping.SourceMapCalculator.pairId;

public class SourcePositionMapper {

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

    // The Freemarker Engine uses Source Positions starting at line and column 1, but we report them zero based
    int curPairId = pairId.getAndIncrement();
    String endPos = reportingExpressionEnd(new SourcePosition(t.getEndLine()-1, t.getEndColumn()-1, t.getTemplate().getName()), curPairId);
    String startPos = reportingExpressionStart(new SourcePosition(t.getBeginLine()-1, t.getBeginColumn()-1, t.getTemplate().getName()), curPairId);

    // Inserting at the endPos first as otherwise we mangle with the String
    sb.insert(lineColumnToOffset(canonicalForm, t.getEndLine(), t.getEndColumn()) + 1, endPos);
    sb.insert(lineColumnToOffset(canonicalForm, t.getBeginLine(), t.getBeginColumn()), startPos);
  }

  protected static String reportingExpressionStart(SourcePosition p, int pairId) {
    return "${"+ TemplateController.SOURCE_MAP_CALCULATOR +".report(" + pairId + "," + +p.getLine() + "," + p.getColumn() + ",ast, true)}";
  }

  protected static String reportingExpressionEnd(SourcePosition p, int pairId) {
    return "${"+TemplateController.SOURCE_MAP_CALCULATOR +".report(" + pairId + "," + +p.getLine() + "," + p.getColumn() + ",ast, false)}";
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
