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

  // This is only needed when we want to use SourceMapCalculator::reportStringHP
  protected static Stack<Pair<Integer, Integer>> positionState = new Stack<>();

  protected static int absoluteLineOffset;
  protected static int absoluteColumnOffset;

  public static List<SimpleSourceMapping> mappings = new ArrayList<>();
  public static List<SimpleSourceMapping> astMappings = new ArrayList<>();
  public static AtomicInteger pairId = new AtomicInteger();

  public static void pushTemplate(Template template) {
    templates.push(template);
    positionState.push(Pair.of(0,0));
    assert positionState.size() == templates.size();
  }

  public static void popTemplate(Template template, StringBuilder content) {
    Pair<Integer, Integer> lastRelativeLineAndCol = positionState.pop();
    if (templates.pop() != template) {
      throw new IllegalStateException();
    }

    // After every template evaluation in the parent template we set the absolute offset to the current offset again
    if(templates.size()==1) {
      var currentPosition = positionState.pop();
      absoluteLineOffset = currentPosition.getLeft();
      absoluteColumnOffset = currentPosition.getRight();
      positionState.push(currentPosition);
    }
    if(templates.size() > 1) {
      absoluteLineOffset += lastRelativeLineAndCol.getLeft();
      // Columns are resetted after every line
      absoluteColumnOffset = lastRelativeLineAndCol.getLeft()>0? 0: lastRelativeLineAndCol.getRight();
    }

    if (templates.isEmpty()) {
      List<DecodedMapping> templateSourceMappings = calculateMappings(mappings, content.toString());
      List<DecodedMapping> astSourceMappings = calculateMappings(astMappings, content.toString());
      Reporting.reportTemplateSourceMapping(template.getName(), templateSourceMappings);
      Reporting.reportASTSourceMapping(template.getName(), astSourceMappings);
      reset();
    }
    assert positionState.size() == templates.size();
  }

  public static List<DecodedMapping> calculateMappings(List<SimpleSourceMapping> simpleMappings, String content) {
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

  // Every Template uses the same StringWriter?
  public SourceMapCalculator(StringWriter sw, Template template) {
    this.sw = sw;
    this.template = template;
  }

  // Probably not needed
  public static void reportStringHP(String content, String source, ASTNode astNode) {
    int curPairId = pairId.getAndIncrement();
    int newLines = numberOfNewLines(content);
    int columnPosOfLastLine = getColumnOfLastLine(content);
    var currentRelativePosition = getCurrentRelativePosition();

    int absoluteFirstLinePos = absoluteLineOffset + currentRelativePosition.getLeft();
    int absoluteFirstColumnPos = absoluteColumnOffset+currentRelativePosition.getRight();

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
    assert positionState.size() == templates.size();
  }

  // This method is bad -> positionState should not be a Stack and it should definitely not be popped and pushed just to iterate it
  protected static Pair<Integer, Integer> getCurrentRelativePosition() {
   List<Pair<Integer, Integer>> posList = new ArrayList<>(positionState.size());

    while(positionState.size() >= 2) {
      posList.add(positionState.pop());
    }
    posList.forEach(e -> positionState.push(e));
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
  }

  public void report(int pairId, int lineInTemplate, int colInTemplate, ASTNode astNode, boolean isStart) {
    String content = sw.toString();

    int numberOfNewLines = numberOfNewLines(content);
    int curGeneratedColPos = getColumnOfLastLine(content);

    // Case we generated at least one new line we reset the absoluteColumnOffset
    if(numberOfNewLines > 0) {
      absoluteColumnOffset = 0;
    }

    int lineOffset = currentlyInMainTemplateForGeneration()? 0 : absoluteLineOffset;
    int colOffset = currentlyInMainTemplateForGeneration()? 0 : absoluteColumnOffset;

    positionState.pop();
    positionState.push(Pair.of(numberOfNewLines, curGeneratedColPos));

    SourcePosition positionInGeneratedFile = new SourcePosition(lineOffset + numberOfNewLines, colOffset +curGeneratedColPos, template.getName());

    addASTMapping(astNode, isStart, positionInGeneratedFile, pairId);
    addTemplateMapping(lineInTemplate, colInTemplate, positionInGeneratedFile, pairId);


    // In this case the given Writer has the absolute position
    // In all other cases the absolute position is updated when the Template is popped
    if(currentlyInMainTemplateForGeneration()) {
      absoluteLineOffset = numberOfNewLines;
    }

    assert positionState.size() == templates.size();
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
    // Note the .lines() method does not recognize a new line if the String ends with it furthermore it returns 1 if the String is not empty
    return (int) (wholeContent+" ").lines().count() -1;
  }

  private static int getColumnOfLastLine(String wholeContent) {
    return wholeContent.lines().reduce((first, second) -> second).orElseGet(() -> "").length();
  }

  protected static boolean currentlyInMainTemplateForGeneration() {
    return templates.size() == 1;
  }

  protected static boolean isChildTemplateForGeneration() {
    return templates.size() > 1;
  }

  public static void reset() {
    absoluteLineOffset = 0;
    absoluteColumnOffset = 0;
    templates.clear();
    positionState.clear();
    mappings.clear();
    astMappings.clear();
  }
}
