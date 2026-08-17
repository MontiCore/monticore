package de.monticore.generating.templateengine.sourcemap;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSource;
import de.monticore.sourcemap.convenience.PositionMapping;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import freemarker.template.Template;
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
    // Do not push config-templates as they should not be reported
    if(Reporting.isConfigTemplate(template.getName()))
      return;

    templates.get().push(template);

    // Ask parent template for its known last absolute pos inside nested template evaluation
    int curLine = curAbsolutePos.get().isEmpty() ? 1 : curAbsolutePos.get().peek().getLeft();
    int curColumn = curAbsolutePos.get().isEmpty() ? 0 : curAbsolutePos.get().peek().getRight();
    curAbsolutePos.get().push(Pair.of(curLine,curColumn));

    assert curAbsolutePos.get().size() == templates.get().size();
  }

  public static void popTemplate(Template template) {
    // Do not consider config-templates
    if(Reporting.isConfigTemplate(template.getName()))
      return;

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
    Map<Integer, SimpleSourceMapping> opendIds = new HashMap<>();
    List<SimpleSourceMapping> validMappings = new ArrayList<>();

    for(SimpleSourceMapping mapping : simpleMappings) {
      if(mapping.sourcePosition.getLine() < 0 || mapping.targetPosition.getLine() < 0){
        Log.warn("Negative lines detected in mapping, ignoring...");
        continue;
      }
      int id = mapping.pairId;
      if(opendIds.containsKey(id)){
        SimpleSourceMapping start = opendIds.remove(id);
        validMappings.add(start);
        validMappings.add(mapping);
      } else {
        opendIds.put(id, mapping);
      }
    }

    // Sort by generated position first, then original position to prevent VLQ line deltas from jumping
    // out of order across generated lines
    validMappings.sort(
            Comparator.comparingInt((SimpleSourceMapping m) -> m.targetPosition.getLine())
                    .thenComparing(m -> m.targetPosition.getColumn())
                    .thenComparing(m -> m.sourcePosition.getLine())
                    .thenComparing(m -> m.sourcePosition.getColumn())

    );
    // Convert to decoded mappings
    List<DecodedMapping> res = new ArrayList<>(validMappings.size());
    for(SimpleSourceMapping mapping : validMappings) {
      URL urlToSource = createSourceURL(mapping.sourcePosition.getFileName());
      res.add(new DecodedMapping(
          new DecodedSource(urlToSource),
          new PositionMapping(urlToSource, mapping.sourcePosition, mapping.targetPosition)
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
    // Suppress recording if this is a config template
    if(Reporting.isConfigTemplate(template.getName()))
      return;

    String content = sw.getCurrentContent();

    int numberOfLinesInContent = numberOfNewLines(content);
    int curGeneratedColPos = getColumnOfLastLine(content);

    // Update absolute position stack so line numbering stays in sync
    Pair<Integer,Integer> absPos = updateAndGetAbsolutePos(numberOfLinesInContent, curGeneratedColPos);

    SourcePosition positionInGeneratedFile = new SourcePosition(absPos.getLeft(), absPos.getRight(), "GenOutput");
    addASTMapping(astNode, isStart, positionInGeneratedFile, pairId);
    addTemplateMapping(lineInTemplate, colInTemplate, templateSource, positionInGeneratedFile, pairId);

    assert curAbsolutePos.get().size() == templates.get().size();
  }

  public void report(int pairId, int lineInTemplate, int colInTemplate, String templateSource) {
    // Suppress recording if this is a config template
    if(Reporting.isConfigTemplate(template.getName()))
      return;

    String content = sw.getCurrentContent();

    int numberOfLinesInContent = numberOfNewLines(content);
    int curGeneratedColPos = getColumnOfLastLine(content);

    // Update absolute position stack so line numbering stays in sync
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
    int absoluteColumn = numberOfLinesInContent == 0 ? columnOffset + curGeneratedColPos : curGeneratedColPos;
    Pair<Integer, Integer> newPos = Pair.of(absoluteLine, absoluteColumn);
    curAbsolutePos.get().push(newPos);
    return newPos;
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
        int line = Math.max(0, startOrEnd.getLine() - 1);
        int col = Math.max(0, startOrEnd.getColumn() );
        SourcePosition s = startOrEnd.getFileName().isPresent()?
            new SourcePosition(line, col, startOrEnd.getFileName().get()) :
            new SourcePosition(line, col);
        Optional<SimpleSourceMapping> startMappingOpt = astMappings.get().stream()
                .filter(mapping -> mapping.pairId == pairId)
                .findFirst();
        if(startMappingOpt.isPresent()) {
          SimpleSourceMapping startMapping = startMappingOpt.get();
          if(startMapping.targetPosition.equals(positionInGeneratedFile)) {
            astMappings.get().remove(startMapping);
          }else{
            astMappings.get().add(new SimpleSourceMapping(s, positionInGeneratedFile, pairId));
          }
        }else{
          astMappings.get().add(new SimpleSourceMapping(s, positionInGeneratedFile, pairId));
        }
      }
    }
  }

  protected void addTemplateMapping(int lineInTemplate, int colInTemplate, String templateSource, SourcePosition positionInGeneratedFile, int pairId) {
    int line = Math.max(0, lineInTemplate);
    int col = Math.max(0, colInTemplate);

    Optional<SimpleSourceMapping> startMappingOpt = mappings.get().stream()
            .filter(m -> m.pairId == pairId)
            .findFirst();
    if(startMappingOpt.isPresent()){
      SimpleSourceMapping startMapping = startMappingOpt.get();
      // If not moved since start-mapping, nothing was generated
      /*if(startMapping.targetPosition.equals(positionInGeneratedFile)) {
        mappings.get().remove(startMapping); // Drop to keep source-map clean
      }else{
        mappings.get().add(new SimpleSourceMapping(new SourcePosition(line, col, templateSource),
                positionInGeneratedFile, pairId));
      }*/
      mappings.get().add(new SimpleSourceMapping(new SourcePosition(line, col, templateSource),
              positionInGeneratedFile, pairId));
    }else{
      // Start mapping
      mappings.get().add(new SimpleSourceMapping(new SourcePosition(line, col, templateSource),
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
