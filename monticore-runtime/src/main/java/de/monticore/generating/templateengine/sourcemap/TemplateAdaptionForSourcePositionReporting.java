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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TemplateAdaptionForSourcePositionReporting {

  private static AtomicInteger pairId = new AtomicInteger(0);

  /**
   * Traverses a FreeMarker template AST and injects position-reporting tags into variable expressions and text blocks.
   * @param template The original FreeMarker template to adapt
   * @param configuration FreeMarker configuration context
   * @return A new template instance embedded with source position reporting markers
   * @throws IOException if template loading or parsing fails
   */
  public static Template adaptTemplateWithPositionMarkers(Template template, Configuration configuration) throws IOException {
    List<TemplateElement> tes = new ArrayList<>();
    TemplateElement rootTreeNode = template.getRootTreeNode();
    inorderTraversal(rootTreeNode, tn -> tes.add((TemplateElement) tn));

    String canonicalForm = rootTreeNode.toString();
    StringBuilder sb = new StringBuilder(canonicalForm);

    Comparator<TemplateElement> firstComp = Comparator.comparingInt(TemplateObject::getEndLine);
    Comparator<TemplateElement> c = firstComp
            .thenComparingInt(TemplateObject::getEndColumn)
            .thenComparing(TemplateObject::getBeginLine)
            .thenComparing(TemplateObject::getBeginColumn)
            .thenComparing(t -> t.getClass().getName()); // Deterministic fallback to ensure always same order
    tes.stream().sorted(c.reversed()).forEach(t -> {
      // DollarVariable is package-private, thus check via class-name
      if (t.getClass().getName().contains("DollarVariable")) {
          addSourcePositionReport(t, sb, canonicalForm, configuration, true);
      }
      if (t instanceof TextBlock) {
        if (!t.getCanonicalForm().isBlank()) {
          // No AST Reporting since this is only text from the template
          // to discuss: Through freemarker-ifs this might still be dependent on the AST variable
          addSourcePositionReport(t, sb, canonicalForm, configuration, false);
        }
      }
    });

    return new Template(template.getName(), sb.toString(), configuration);
  }

  /**
   * Injects start and end position-reporting FreeMarker directives into the template string surrounding a specified
   * template element.
   * @param t The template element being wrapped.
   * @param sb Mutable string builder containing the template
   * @param canonicalForm Original string representation of the template
   * @param configuration FreeMarker configuration context
   * @param reportAstMapping true to include AST node mapping hooks, false for text-only blocks
   */
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
      endPos = buildReportTag(new SourcePosition(endLine-1, endColumn-1, templateSource), curPairId, true, false);
      startPos = buildReportTag(new SourcePosition(startLine-1, startColumn-1, templateSource), curPairId, true, true);
    }else {
      endPos = buildReportTag(new SourcePosition(endLine-1, endColumn-1, templateSource), curPairId, false, false);
      startPos = buildReportTag(new SourcePosition(startLine-1, startColumn-1, templateSource), curPairId, false, false);
    }

    // Inserting at the endPos first as otherwise we mangle with the String
    sb.insert(lineColumnToOffset(canonicalForm, t.getEndLine(), t.getEndColumn()) + 1, endPos);
    sb.insert(lineColumnToOffset(canonicalForm, t.getBeginLine(), t.getBeginColumn()), startPos);
  }

  /** Normalizes FreeMarker line/column coordinates bugs where line-end positions might evaluate below 1.
   * @param pos Raw position value from FreeMarker.
   * @return Correct position value guaranteed to be at least 1.
   */
  private static int increasePositionIfNecessary(int pos) {
    if(pos >= 1) {
      return pos;
    } else {
      return 1;
    }
  }

  /**
   * Constructs the executable FreeMarker interpolation string that invokes the source map calculator's reporting method
   * during template evaluation.
   * @param p The source position being reported.
   * @param pairId Unique identifier linking the span pair.
   * @param expression true if reporting an expression/AST node, false for standard text blocks
   * @param expressionStart true if this tag represents the start boundary of an expression
   * @return formatted FreeMarker string snippet executing the reporting call
   */
  private static String buildReportTag(SourcePosition p, int pairId, boolean expression, boolean expressionStart){
    if(!expression)
      return "${"+ TemplateController.SOURCE_MAP_CALCULATOR +".report(" + pairId + "," + p.getLine() + "," + p.getColumn() +",\""+p.getFileName().get()+ "\")}";
    else{
      return "${"+ TemplateController.SOURCE_MAP_CALCULATOR +".report(" + pairId + "," + p.getLine() + "," + p.getColumn() + ",\""+p.getFileName().get()+"\",ast," + expressionStart +")}";
    }
  }

  /**
   * Converts 1-based line and column numbers into flat character offset index within a string.
   * @param input The target string to search.
   * @param lineNumber 1-based target line number.
   * @param columnNumber 1-based target column number
   * @return zero-based character offset, or -1 if coordinates are out of bounds.
   */
  private static int lineColumnToOffset(String input, int lineNumber, int columnNumber) {
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

  private static void inorderTraversal(TreeNode node, Consumer<TreeNode> c) {
    var children = node.children();
    while (children.hasMoreElements()) {
      TreeNode child = children.nextElement();
      inorderTraversal(child, c);
    }

    c.accept(node);
  }
}
