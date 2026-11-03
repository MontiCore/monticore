package de.monticore.generating.templateengine.sourcemap;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSource;
import de.monticore.sourcemap.convenience.PositionMapping;
import de.se_rwth.commons.SourcePosition;
import freemarker.template.Template;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SourceMapCalculator {
  protected static ThreadLocal<Stack<Template>> templates = ThreadLocal.withInitial(() -> new Stack<>());

  // We need this for nested template evaluations
  protected static ThreadLocal<Stack<Pair<Integer, Integer>>> curAbsolutePos = ThreadLocal.withInitial(() -> new Stack<>());

  public static ThreadLocal<List<SimpleSourceMapping>> mappings = ThreadLocal.withInitial(ArrayList::new);
  public static ThreadLocal<List<SimpleSourceMapping>> astMappings = ThreadLocal.withInitial(ArrayList::new);
  public static ThreadLocal<AtomicInteger> pairId = ThreadLocal.withInitial(AtomicInteger::new);

  public static void pushTemplate(Template template) {
    templates.get().push(template);

    // Ask parent template for its known last absolute pos inside nested template evaluation
    int curLine = curAbsolutePos.get().isEmpty()? 0 : curAbsolutePos.get().peek().getLeft();
    int curColumn = curAbsolutePos.get().isEmpty()? 0 : curAbsolutePos.get().peek().getRight();
    curAbsolutePos.get().push(Pair.of(curLine,curColumn));

    assert curAbsolutePos.get().size() == templates.get().size();
  }

  public static void popTemplate(Template template) {
    if (templates.get().pop() != template) {
      throw new IllegalStateException();
    }

    curAbsolutePos.get().pop();

    if(templates.get().size() != curAbsolutePos.get().size()) {
      throw new IllegalStateException();
    }

    if (templates.get().isEmpty()) {
      flushMappings();
      reset();
    }
  }

  public static List<DecodedMapping> calculateMappings(List<SimpleSourceMapping> simpleMappings) {
    // group by pairId
    List<Pair<SimpleSourceMapping, SimpleSourceMapping>> pairs = new ArrayList<>();
    Map<Integer, SimpleSourceMapping> openIds = new HashMap<>();
    for (SimpleSourceMapping mapping : simpleMappings) {
      int id = mapping.pairId;
      if (openIds.containsKey(id)) {
        SimpleSourceMapping start = openIds.remove(id);
        pairs.add(new ImmutablePair<>(start, mapping));
      } else {
        openIds.put(id, mapping);
      }
    }

    // convert position => line, row
    List<DecodedMapping> res = new ArrayList<>();
    for (Pair<SimpleSourceMapping, SimpleSourceMapping> pair : pairs) {
      SimpleSourceMapping p1 = pair.getKey();
      SimpleSourceMapping p2 = pair.getValue();

      URL urlToSource = createSourceURL(p1.sourcePosition.getFileName());
      res.add(new DecodedMapping(
          new DecodedSource(urlToSource),
          new PositionMapping(urlToSource, p1.sourcePosition, p1.targetPosition)
      ));
      res.add(new DecodedMapping(
          new DecodedSource(urlToSource),
          new PositionMapping(urlToSource, p2.sourcePosition, p2.targetPosition)
      ));
    }
    return res;
  }

  private static URL createSourceURL(Optional<String> fileOpt) {
    try {
      return new URL(fileOpt.orElse(""));
    } catch(MalformedURLException e){
      try {
        return new URL("file:/"+fileOpt.orElseGet(() -> "#"));
      } catch (MalformedURLException e2) {
        // handle somehow
        throw new RuntimeException(e2);
      }
    }
  }

  final FreeMarkerTemplateEngine.ContentWriter sw;
  final Template template;

  // Everytime a template is executed it uses a new StringWriter instance
  public SourceMapCalculator(FreeMarkerTemplateEngine.ContentWriter sw, Template template) {
    this.sw = sw;
    this.template = template;
  }

  public void report(int pairId, int lineInTemplate, int colInTemplate, String templateSource, ASTNode astNode, boolean isStart) {
    String content = sw.getCurrentContent();

    int numberOfLinesInContent = numberOfNewLines(content);
    int curGeneratedColPos = getColumnOfLastLine(content);

    Pair<Integer,Integer> absPos = updateAndGetAbsolutePos(numberOfLinesInContent, curGeneratedColPos);

    SourcePosition positionInGeneratedFile = new SourcePosition(absPos.getLeft(), absPos.getRight(), "GenOutput");
    addASTMapping(astNode, isStart, positionInGeneratedFile, pairId);
    addTemplateMapping(lineInTemplate, colInTemplate, templateSource, positionInGeneratedFile, pairId);

    assert curAbsolutePos.get().size() == templates.get().size();
  }

  public void report(int pairId, int lineInTemplate, int colInTemplate, String templateSource) {
    String content = sw.getCurrentContent();

    int numberOfLinesInContent = numberOfNewLines(content);
    int curGeneratedColPos = getColumnOfLastLine(content);

    Pair<Integer,Integer> absPos = updateAndGetAbsolutePos(numberOfLinesInContent, curGeneratedColPos);

    SourcePosition positionInGeneratedFile = new SourcePosition(absPos.getLeft(), absPos.getRight(), "GenOutput");
    addTemplateMapping(lineInTemplate, colInTemplate, templateSource, positionInGeneratedFile, pairId);

    assert curAbsolutePos.get().size() == templates.get().size();
  }

  /**
   * This function does not add a new position state but updates the current one
   */
  private static Pair<Integer, Integer> updateAndGetAbsolutePos(int numberOfLinesInContent, int curGeneratedColPos) {
    curAbsolutePos.get().pop();

    int lineOffset = 0;
    int columnOffset = 0;
    if(!curAbsolutePos.get().empty()) {
      Pair<Integer, Integer> offsetFromParentTemplate = curAbsolutePos.get().peek();
      lineOffset = offsetFromParentTemplate.getLeft();
      columnOffset = offsetFromParentTemplate.getRight();
    }

    int absoluteLine = lineOffset + numberOfLinesInContent;
    int absoluteColumn = numberOfLinesInContent==0? columnOffset + curGeneratedColPos : curGeneratedColPos;
    curAbsolutePos.get().push(Pair.of(absoluteLine, absoluteColumn));
    return curAbsolutePos.get().peek();
  }

  /**
   * Experiments showed that MontiCore Parsers create SourcePositions that are one-based for line numbers and zero-based
   * for column numbers
   * @param astNode
   * @param isStart
   * @param positionInGeneratedFile
   * @param pairId
   */
  protected static void addASTMapping(ASTNode astNode, boolean isStart, SourcePosition positionInGeneratedFile, int pairId) {
    if(astNode!=null) {
      SourcePosition startOrEnd = null;
      if(isStart && astNode.isPresent_SourcePositionStart()) {
        startOrEnd= astNode.get_SourcePositionStart();
      } else if(!isStart && astNode.isPresent_SourcePositionEnd()) {
        startOrEnd= astNode.get_SourcePositionEnd();
      }
      if(startOrEnd != null) {
        // Zero based in line and column numbers
        SourcePosition s = startOrEnd.getFileName().isPresent()?
            new SourcePosition(startOrEnd.getLine()-1, startOrEnd.getColumn(), startOrEnd.getFileName().get()) :
            new SourcePosition(startOrEnd.getLine()-1, startOrEnd.getColumn());
        astMappings.get().add(new SimpleSourceMapping(s, positionInGeneratedFile, pairId));
      }
    }
  }

  protected void addTemplateMapping(int lineInTemplate, int colInTemplate, String templateSource, SourcePosition positionInGeneratedFile, int pairId) {
    if(mappings.get().stream().map(mapping -> mapping.pairId).anyMatch(i -> i == pairId)
        && mappings.get().get(mappings.get().size()-1).pairId != pairId) {
      mappings.get().removeIf(m -> m.pairId == pairId);
    } else if(!mappings.get().isEmpty() && mappings.get().get(mappings.get().size()-1).targetPosition.equals(positionInGeneratedFile) && mappings.get().get(mappings.get().size()-1).pairId == pairId) {
      // Nothing was generated
      mappings.get().remove(mappings.get().size()-1);
    } else {
      mappings.get().add(new SimpleSourceMapping(new SourcePosition(lineInTemplate, colInTemplate, templateSource),
          positionInGeneratedFile, pairId));
    }
  }

  private static int numberOfNewLines(String wholeContent) {
    // Note the String::lines method does not recognize a new line if the String ends with it furthermore it returns 1 if the String is not empty
    return (int) (wholeContent+" ").lines().count() -1;
  }

  private static int getColumnOfLastLine(String wholeContent) {
    // We add a Space at the end, so the String::lines method really returns the last line
    return (wholeContent+" ").lines().reduce((first, second) -> second).orElse("").length() - 1;
  }

  protected static boolean currentlyInMainTemplateForGeneration() {
    return templates.get().size() == 1;
  }

  protected static boolean isChildTemplateForGeneration() {
    return templates.get().size() > 1;
  }

  public static void flushMappings(){
    List<DecodedMapping> templateSourceMappings = calculateMappings(mappings.get());
    List<DecodedMapping> astSourceMappings = calculateMappings(astMappings.get());
    Reporting.reportASTSourceMapping(astSourceMappings);
    Reporting.reportTemplateSourceMapping(templateSourceMappings);
    clearMappings();
  }

  public static void clearMappings(){
    mappings.get().clear();
    mappings.remove();
    astMappings.get().clear();
    astMappings.remove();
  }

  public static void reset() {
    templates.get().clear();
    templates.remove();
    curAbsolutePos.get().clear();
    curAbsolutePos.remove();
    mappings.get().clear();
    mappings.remove();
    astMappings.get().clear();
    astMappings.remove();
  }
}
