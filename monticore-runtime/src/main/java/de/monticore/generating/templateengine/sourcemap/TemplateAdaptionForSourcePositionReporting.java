package de.monticore.generating.templateengine.sourcemap;

import de.monticore.generating.templateengine.TemplateController;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
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
import java.util.function.Consumer;

import static de.monticore.generating.templateengine.sourcemap.SourceMapCalculator.pairId;

public class TemplateAdaptionForSourcePositionReporting {

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
        addSourcePositionReport(t, sb, canonicalForm, configuration,true);
      }
      if (t instanceof TextBlock) {
        if (!t.getCanonicalForm().isBlank()) {
          // No AST Reporting since this is only text from the template
          // to discuss: Through freemarker-ifs this might still be dependent on the AST variable
          addSourcePositionReport(t, sb, canonicalForm, configuration, false);
        }
      }
    });

    return new Template(result.getName(), sb.toString(), configuration);
  }

  private static void addSourcePositionReport(TemplateElement t, StringBuilder sb, String canonicalForm, Configuration configuration, boolean reportAstMapping) {

    // The Freemarker Engine uses Source Positions starting at line and column 1, but we report them zero based
    int curPairId = pairId.getAndIncrement();
    String endPos;
    String startPos;

    String templateSource = t.getTemplate().getName();
    try {
      templateSource = configuration.getTemplateLoader().findTemplateSource(t.getTemplate().getName()).toString();
    } catch (IOException e) {
      Log.warn("Could not find fully qualified source URL of Template.");
    }

    int startLine = increasePositionIfNecessary(t.getBeginLine());
    int startColumn = increasePositionIfNecessary(t.getBeginColumn());
    int endLine = increasePositionIfNecessary(t.getEndLine());
    int endColumn = increasePositionIfNecessary(t.getEndColumn());

    if(reportAstMapping) {
      endPos = reportingExpressionEnd(new SourcePosition(endLine-1, endColumn-1, templateSource), curPairId);
      startPos = reportingExpressionStart(new SourcePosition(startLine-1, startColumn-1, templateSource), curPairId);
    } else {
      endPos = reportingTextStart(new SourcePosition(endLine-1, endColumn-1, templateSource), curPairId);
      startPos = reportingTextEnd(new SourcePosition(startLine-1, startColumn-1, templateSource), curPairId);
    }

    // Inserting at the endPos first as otherwise we mangle with the String
    sb.insert(lineColumnToOffset(canonicalForm, t.getEndLine(), t.getEndColumn()) + 1, endPos);
    sb.insert(lineColumnToOffset(canonicalForm, t.getBeginLine(), t.getBeginColumn()), startPos);
  }

  /* Freemarker uses Column position starting at 1, but there seems to exist a
  * bug, when a Line ends with "sometext\n" where there suddenly is an end column
  * position at 0 */
  private static int increasePositionIfNecessary(int pos) {
    if(pos >= 1) {
      return pos;
    } else {
      return 1;
    }
  }

  protected static String reportingTextStart(SourcePosition p, int pairId) {
    return "${"+ TemplateController.SOURCE_MAP_CALCULATOR +".report(" + pairId + "," + +p.getLine() + "," + p.getColumn() +",\""+p.getFileName().get()+ "\")}";
  }

  protected static String reportingTextEnd(SourcePosition p, int pairId) {
    return "${"+TemplateController.SOURCE_MAP_CALCULATOR +".report(" + pairId + "," + +p.getLine() + "," + p.getColumn() +",\""+p.getFileName().get()+ "\")}";
  }

  protected static String reportingExpressionStart(SourcePosition p, int pairId) {
    return "${"+ TemplateController.SOURCE_MAP_CALCULATOR +".report(" + pairId + "," + +p.getLine() + "," + p.getColumn() + ",\""+p.getFileName().get()+"\",ast, true)}";
  }

  protected static String reportingExpressionEnd(SourcePosition p, int pairId) {
    return "${"+TemplateController.SOURCE_MAP_CALCULATOR +".report(" + pairId + "," + +p.getLine() + "," + p.getColumn() + ", \""+p.getFileName().get()+"\",ast, false)}";
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
