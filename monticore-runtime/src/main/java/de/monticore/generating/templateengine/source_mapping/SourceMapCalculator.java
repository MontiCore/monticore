package de.monticore.generating.templateengine.source_mapping;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.SourcePosition;
import freemarker.template.Template;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SourceMapCalculator {
  protected static Stack<Template> templates = new Stack<>();

  // We need this for nested template evaluations
  protected static Stack<Pair<Integer, Integer>> curAbsolutePos = new Stack<>();

  public static List<SimpleSourceMapping> mappings = new ArrayList<>();
  public static List<SimpleSourceMapping> astMappings = new ArrayList<>();
  public static AtomicInteger pairId = new AtomicInteger();

  public static void pushTemplate(Template template) {
    templates.push(template);

    // Ask parent template for its known last absolute pos inside nested template evaluation
    int curLine = curAbsolutePos.isEmpty()? 0 : curAbsolutePos.peek().getLeft();
    int curColumn = curAbsolutePos.isEmpty()? 0 : curAbsolutePos.peek().getRight();
    curAbsolutePos.push(Pair.of(curLine,curColumn));

    assert curAbsolutePos.size() == templates.size();
  }

  public static void popTemplate(Template template) {
    if (templates.pop() != template) {
      throw new IllegalStateException();
    }

    curAbsolutePos.pop();

    if(templates.size() != curAbsolutePos.size()) {
      throw new IllegalStateException();
    }

    if (templates.isEmpty()) {
      List<DecodedMapping> templateSourceMappings = calculateMappings(mappings);
      List<DecodedMapping> astSourceMappings = calculateMappings(astMappings);
      Reporting.reportASTSourceMapping(astSourceMappings);
      Reporting.reportTemplateSourceMapping(templateSourceMappings);
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
      return new URL("file:/"+fileOpt.orElseGet(() -> "#"));
    } catch(MalformedURLException e){
      // handle somehow
      throw new RuntimeException(e);
    }
  }

  final StringWriter sw;
  final Template template;

  // Everytime a template is executed it uses a new StringWriter instance
  public SourceMapCalculator(StringWriter sw, Template template) {
    this.sw = sw;
    this.template = template;
  }

  // Probably not needed
/*  public static void reportStringHP(String content, String source, ASTNode astNode) {
    int curPairId = pairId.getAndIncrement();
    int newLines = numberOfNewLines(content);
    int columnPosOfLastLine = getColumnOfLastLine(content);
    var currentRelativePosition = getCurrentRelativePosition();

    int absoluteFirstLinePos = *//*lastPrintedPositionInFinalGeneratedOutput.getKey() + *//*currentRelativePosition.getLeft();
    int absoluteFirstColumnPos = *//*lastPrintedPositionInFinalGeneratedOutput.getRight() +*//*currentRelativePosition.getRight();

    int absoluteLastLinePos = absoluteFirstLinePos + newLines;
    int absoluteLastColumnPos = newLines > 0? columnPosOfLastLine : columnPosOfLastLine + absoluteFirstColumnPos;

    SourcePosition generatedStart = new SourcePosition(absoluteFirstLinePos, absoluteFirstColumnPos);
    SourcePosition generatedEnd = new SourcePosition(absoluteLastLinePos, absoluteLastColumnPos);

    addASTMapping(astNode, true,generatedStart,curPairId);
    addASTMapping(astNode, false, generatedEnd,curPairId);

    //System.out.println("Line "+(newLines+1)+ " Column "+columnPosOfLastLine+" Templ "+source+ " Stack Size "+templates.size());

    mappings.add(new SimpleSourceMapping(new SourcePosition(0, 0, "SHP"+source),
        generatedStart, curPairId));

    // Theoretically we have to load the template here and check for its last pos
    mappings.add(new SimpleSourceMapping(new SourcePosition(newLines+1, columnPosOfLastLine,"SHP"+source),
        generatedEnd, curPairId));
    assert curAbsolutePos.size() == templates.size();
  }*/

  // This method is bad -> positionState should not be a Stack and it should definitely not be popped and pushed just to iterate it
/*  protected static Pair<Integer, Integer> getCurrentRelativePosition() {
   List<Pair<Integer, Integer>> posList = new ArrayList<>(curAbsolutePos.size());

    while(curAbsolutePos.size() >= 2) {
      posList.add(curAbsolutePos.pop());
    }
    posList.forEach(e -> curAbsolutePos.push(e));
    int relativeLine = 0;
    int relativeColumn = 0;
    Collections.reverse(posList);
    for (Pair<Integer, Integer> pos : posList) {
      relativeLine+=pos.getLeft();
      if(pos.getLeft() > 0) {
        relativeColumn = pos.getRight();
      } else {
        relativeColumn += pos.getRight();
      }
    }
    return Pair.of(relativeLine, relativeColumn);
  }*/

  public void report(int pairId, int lineInTemplate, int colInTemplate, ASTNode astNode, boolean isStart) {
    String content = sw.toString();

    int numberOfLinesInContent = numberOfNewLines(content);
    int curGeneratedColPos = getColumnOfLastLine(content);

    Pair<Integer,Integer> absPos = updateAndGetAbsolutePos(numberOfLinesInContent, curGeneratedColPos);

    SourcePosition positionInGeneratedFile = new SourcePosition(absPos.getLeft(), absPos.getRight(), template.getName());
    addASTMapping(astNode, isStart, positionInGeneratedFile, pairId);
    addTemplateMapping(lineInTemplate, colInTemplate, positionInGeneratedFile, pairId);

    assert curAbsolutePos.size() == templates.size();
  }

  public void report(int pairId, int lineInTemplate, int colInTemplate) {
    String content = sw.toString();

    int numberOfLinesInContent = numberOfNewLines(content);
    int curGeneratedColPos = getColumnOfLastLine(content);

    Pair<Integer,Integer> absPos = updateAndGetAbsolutePos(numberOfLinesInContent, curGeneratedColPos);

    SourcePosition positionInGeneratedFile = new SourcePosition(absPos.getLeft(), absPos.getRight(), template.getName());
    addTemplateMapping(lineInTemplate, colInTemplate, positionInGeneratedFile, pairId);

    assert curAbsolutePos.size() == templates.size();
  }

  /**
   * This function does not add a new position state but updates the current one
   */
  private static Pair<Integer, Integer> updateAndGetAbsolutePos(int numberOfLinesInContent, int curGeneratedColPos) {

    curAbsolutePos.pop();

    int lineOffset = 0;
    int columnOffset = 0;
    if(!curAbsolutePos.empty()) {
      Pair<Integer, Integer> offsetFromParentTemplate = curAbsolutePos.peek();
      lineOffset = offsetFromParentTemplate.getLeft();
      columnOffset = offsetFromParentTemplate.getRight();
    }

    int absoluteLine = lineOffset + numberOfLinesInContent;
    int absoluteColumn = numberOfLinesInContent==0? columnOffset + curGeneratedColPos : curGeneratedColPos;
    curAbsolutePos.push(Pair.of(absoluteLine, absoluteColumn));
    return curAbsolutePos.peek();
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
        astMappings.add(new SimpleSourceMapping(s, positionInGeneratedFile, pairId));
      }
    }
  }

  protected void addTemplateMapping(int lineInTemplate, int colInTemplate, SourcePosition positionInGeneratedFile, int pairId) {
    mappings.add(new SimpleSourceMapping(new SourcePosition(lineInTemplate, colInTemplate, template.getName()),
        positionInGeneratedFile, pairId));
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
    return templates.size() == 1;
  }

  protected static boolean isChildTemplateForGeneration() {
    return templates.size() > 1;
  }

  public static void reset() {
    templates.clear();
    curAbsolutePos.clear();
    mappings.clear();
    astMappings.clear();
  }
}
