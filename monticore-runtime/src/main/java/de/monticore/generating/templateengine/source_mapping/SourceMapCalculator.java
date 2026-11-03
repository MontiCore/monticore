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
  protected static Stack<Pair<Integer, Integer>> positionState = new Stack<>();

  protected static int lastReportedPosition = 0;

  //protected static int relativeLastLine = 0;
  //protected static int relativeLastColumn = 0;

  // Offset where a template being evaluated starts in the currently generated file
  protected static int offset;

  protected static int absoluteLineOffset;
  protected static int absoluteColumnOffset;

  public static List<SimpleSourceMapping> mappings = new ArrayList<>();
  public static List<SimpleSourceMapping> astMappings = new ArrayList<>();
  public static AtomicInteger pairId = new AtomicInteger();

  public static void pushTemplate(Template template) {
    if(templates.isEmpty()){
      lastReportedPosition = 0;
    }
    templates.push(template);
    offset = lastReportedPosition;
    positionState.push(Pair.of(0,0));
  }

  public static void popTemplate(Template template, StringBuilder content) {
    if (templates.pop() != template) {
      throw new IllegalStateException();
    }

    if (templates.size() <= 1) {
      offset = 0;
    } else {
      offset = lastReportedPosition;
      Pair<Integer, Integer> lastContext = positionState.pop();
      absoluteLineOffset += lastContext.getLeft();
      // Columns are resetted after every line
      absoluteColumnOffset = lastContext.getRight();
    }

    if (templates.isEmpty()) {
      List<DecodedMapping> templateSourceMappings = calculateMappings(mappings, content.toString());
      List<DecodedMapping> astSourceMappings = calculateMappings(astMappings, content.toString());
      Reporting.reportTemplateSourceMapping(template.getName(), templateSourceMappings);
      Reporting.reportASTSourceMapping(template.getName(), astSourceMappings);

      astMappings.clear();
      mappings.clear();
    }
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
    String contentString = content;
    List<DecodedMapping> res = new ArrayList<>();
    for (Pair<SimpleSourceMapping, SimpleSourceMapping> pair : pairs) {
      SimpleSourceMapping p1 = pair.getKey();
      SimpleSourceMapping p2 = pair.getValue();

      //p1.calcTargetPosition(contentString);
      //p2.calcTargetPosition(contentString);

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

  public static void reportStringHP(String content, String source, ASTNode astNode) {
    int curPairId = pairId.get();
    int curGeneratedLine = (int) content.lines().count();
    int curGeneratedCol = getColumnOfLastLine(content);
    var currentPosition = positionState.pop();
    int relativeLastLine = currentPosition.getLeft();
    int relativeLastColumn = curGeneratedCol>0? curGeneratedCol : currentPosition.getRight();
    if(astNode!=null) {
      if(astNode.isPresent_SourcePositionStart()) {
        astMappings.add(new SimpleSourceMapping(astNode.get_SourcePositionStart(),
            new SourcePosition(absoluteLineOffset + curGeneratedLine, absoluteColumnOffset +curGeneratedCol,"SHP"+source), curPairId));
      }
      if(astNode.isPresent_SourcePositionStart()) {
        astMappings.add(new SimpleSourceMapping(astNode.get_SourcePositionEnd(),
            new SourcePosition(absoluteLineOffset + curGeneratedLine, absoluteColumnOffset +curGeneratedCol,"SHP"+source), curPairId));
      }
    }
    int endLine = (int) content.lines().count();
    int endCol = getColumnOfLastLine(content);
    System.out.println("Line "+endLine+ " Column "+endCol+" Templ "+source+ " Stack Size "+templates.size());

    mappings.add(new SimpleSourceMapping(new SourcePosition(0, 0, "SHP"+source),
        new SourcePosition(absoluteLineOffset + relativeLastLine, absoluteColumnOffset + relativeLastColumn,source), curPairId));
    mappings.add(new SimpleSourceMapping(new SourcePosition(endLine,endCol, "SHP"+source),
        new SourcePosition(absoluteLineOffset + relativeLastLine+ curGeneratedLine, absoluteColumnOffset + relativeLastColumn+curGeneratedCol,source), curPairId));
  }

  public void reportStart(int pairId, int lineInTemplate, int colInTemplate, ASTNode astNode) {
    String content = sw.toString();
    int pos = content.length();
    int curGeneratedLine = (int) content.lines().count();
    int curGeneratedCol = getColumnOfLastLine(content);
    if(astNode!=null) {
      if(astNode.isPresent_SourcePositionStart()) {
        astMappings.add(new SimpleSourceMapping(astNode.get_SourcePositionStart(),
            new SourcePosition(absoluteLineOffset + curGeneratedLine, absoluteColumnOffset +curGeneratedCol,template.getName()), pairId));
      }
    }
    mappings.add(new SimpleSourceMapping(new SourcePosition(lineInTemplate, colInTemplate, template.getName()),
        new SourcePosition(absoluteLineOffset + curGeneratedLine, absoluteColumnOffset +curGeneratedCol,template.getName()), pairId));
    lastReportedPosition = pos;
    positionState.pop();
    positionState.push(Pair.of(curGeneratedLine, curGeneratedCol));
  }

  public void reportEnd(int pairId, int lineInTemplate, int colInTemplate, ASTNode astNode) {
    String content = sw.toString();
    int pos = content.length();
    int curGeneratedLine = (int) content.lines().count();
    int curGeneratedCol = getColumnOfLastLine(content);
    if(astNode!=null) {
      if(astNode.isPresent_SourcePositionEnd()) {
        astMappings.add(new SimpleSourceMapping(astNode.get_SourcePositionEnd(), new SourcePosition(absoluteLineOffset + curGeneratedLine, absoluteColumnOffset +curGeneratedCol,template.getName()), pairId));
      }
    }
    mappings.add(new SimpleSourceMapping(new SourcePosition(lineInTemplate, colInTemplate, template.getName()), new SourcePosition(absoluteLineOffset + curGeneratedLine, absoluteColumnOffset +curGeneratedCol,template.getName()), pairId));
    lastReportedPosition = pos;
    positionState.pop();
    positionState.push(Pair.of(curGeneratedLine, curGeneratedCol));
  }

  private static int getColumnOfLastLine(String wholeContent) {
    return wholeContent.lines().reduce((first, second) -> second).orElseGet(() -> "").length();
  }
}
