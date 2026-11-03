package de.monticore.generating.templateengine.sourcemap;

import com.google.common.io.Resources;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SourceMapCalculator {
  private static ThreadLocal<Stack<Template>> templates = ThreadLocal.withInitial(() -> new Stack<>());

  // We need this for nested template evaluations
  private static ThreadLocal<Stack<Pair<Integer, Integer>>> curAbsolutePos = ThreadLocal.withInitial(() -> new Stack<>());

  private static ThreadLocal<List<SimpleSourceMapping>> mappings = ThreadLocal.withInitial(ArrayList::new);
  private static ThreadLocal<List<SimpleSourceMapping>> astMappings = ThreadLocal.withInitial(ArrayList::new);
  private static ThreadLocal<List<SimpleIncludeMapping>> includeMappings = ThreadLocal.withInitial(ArrayList::new);
  private static ThreadLocal<Integer> baseLineOffset = ThreadLocal.withInitial(() -> 0);

  public static void pushTemplate(Template template) {
    // Do not push config-templates as they should not be reported
    if(Reporting.isConfigTemplate(template.getName()))
      return;

    templates.get().push(template);

    // Ask parent template for its known last absolute pos inside nested template evaluation
    int curLine = curAbsolutePos.get().isEmpty() ? getBaseLineOffset() : curAbsolutePos.get().peek().getLeft();
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

  public static void setBaseLineOffset(int offset){
    baseLineOffset.set(offset);
  }

  public static int getBaseLineOffset(){
    return baseLineOffset.get();
  }

  private static List<IncludeSpan> calculateIncludeMappings(List<SimpleIncludeMapping> includeMappings){
    Map<Integer, SimpleIncludeMapping> openMappings = new HashMap<>();
    List<IncludeSpan> includes = new ArrayList<>();

    for(SimpleIncludeMapping mapping : includeMappings){
      if(mapping.sourcePosition.getLine() < 0 || mapping.sourcePosition.getColumn() < 0){
        Log.warn("Negative lines detected in mapping, ignoring...");
        continue;
      }
      int id = mapping.pairId;
      if(openMappings.containsKey(id)){
        SimpleIncludeMapping start = openMappings.remove(id);
        if(!start.targetTemplate.equals(mapping.targetTemplate)) {
          Log.warn("Inconsistent target templates in include mapping");
          continue;
        }
        if(start.sourcePosition.getFileName().isEmpty() || mapping.sourcePosition.getFileName().isEmpty()){
          Log.warn("No source file name given for include mapping.");
          continue;
        }
        if(!start.sourcePosition.getFileName().get().equals(mapping.sourcePosition.getFileName().get())){
          Log.warn("Inconsistent source file name given for include mapping.");
          continue;
        }
        includes.add(new IncludeSpan(start.sourcePosition, mapping.sourcePosition, mapping.targetTemplate));
      }else{
        openMappings.put(id, mapping);
      }
    }

    return includes;
  }

  public static List<DecodedMapping> calculateMappings(List<SimpleSourceMapping> simpleMappings) {
    Map<Integer, SimpleSourceMapping> openSpans = new HashMap<>();

    List<MappingSpan> completedSpans = new ArrayList<>();
    for(SimpleSourceMapping mapping : simpleMappings) {
      if(mapping.sourcePosition.getLine() < 0 || mapping.targetPosition.getLine() < 0){
        Log.warn("Negative lines detected in mapping, ignoring...");
        continue;
      }

      int id = mapping.pairId;
      if(openSpans.containsKey(id)) {
        SimpleSourceMapping start = openSpans.remove(id);
        completedSpans.add(new MappingSpan(id, start, mapping));
      }else{
        openSpans.put(id, mapping);
      }
    }

    // Filter out zero-width generated spans (directives that did not emit any code)
    List<SimpleSourceMapping> validMappings = new ArrayList<>();
    for(MappingSpan span : completedSpans) {
      if(span.isZeroWidthGenerated()) {
        continue;
      }
      validMappings.add(span.start);
    }

    // Deduplicate: If multiple mappings land on the same (generatedLine/Column), keep only the
    // first one. This corresponds to the most inner one in nested templates (as there the "end" is found first when
    // the completed spans are searched
    Map<String, SimpleSourceMapping> deduplicatedPairs = new LinkedHashMap<>();
    for(SimpleSourceMapping current : validMappings) {
      String key = current.targetPosition.getLine() + ":" + current.targetPosition.getColumn();
      if(deduplicatedPairs.containsKey(key))
        continue;
      deduplicatedPairs.put(key, current);
    }
    List<SimpleSourceMapping> deduplicatedMappings = new ArrayList<>(deduplicatedPairs.values());

    // Sort by generated coordinates (for VLQ deltas)
    deduplicatedMappings.sort(
            Comparator.comparingInt((SimpleSourceMapping m) -> m.targetPosition.getLine())
                    .thenComparing(m -> m.targetPosition.getColumn())
                    .thenComparing(m -> m.sourcePosition.getLine())
                    .thenComparing(m -> m.sourcePosition.getColumn())
    );

    // Convert to decoded mappings
    List<DecodedMapping> res = new ArrayList<>(deduplicatedMappings.size());
    for(SimpleSourceMapping mapping : deduplicatedMappings) {
      URL urlToSource = createSourceURL(mapping.sourcePosition.getFileName());
      // debug:
      String content = readSourceContent(urlToSource);
      res.add(new DecodedMapping(
          new DecodedSource(urlToSource, content),
          new PositionMapping(urlToSource, mapping.sourcePosition, mapping.targetPosition)
      ));
    }
    return res;
  }

  private static String readSourceContent(URL url){
    try {
      return Resources.toString(url, StandardCharsets.UTF_8);
    }catch (IOException e) {
      return null;
    }
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

  public void reportInclude(int pairId, int lineInTemplate, int colInTemplate, String templateSource, String includedTemplate, ASTNode astNode, boolean isStart){
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
    addIncludeMapping(lineInTemplate, colInTemplate, templateSource, includedTemplate, pairId);

    assert curAbsolutePos.get().size() == templates.get().size();
  }

  /**
   * This function does not add a new position state but updates the current one
   */
  private static Pair<Integer, Integer> updateAndGetAbsolutePos(int numberOfLinesInContent, int curGeneratedColPos) {
    // The stack is never empty when this is called for a report, due to the push/pop validation
    curAbsolutePos.get().pop();

    int lineOffset;
    int columnOffset = 0;
    if(!curAbsolutePos.get().empty()) {
      Pair<Integer, Integer> offsetFromParentTemplate = curAbsolutePos.get().peek();
      lineOffset = offsetFromParentTemplate.getLeft();
      columnOffset = offsetFromParentTemplate.getRight();
    }else{
      lineOffset = getBaseLineOffset();
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
      if(startMapping.targetPosition.equals(positionInGeneratedFile)) {
        mappings.get().remove(startMapping); // Drop to keep source-map clean
      }else{
        mappings.get().add(new SimpleSourceMapping(new SourcePosition(line, col, templateSource),
                positionInGeneratedFile, pairId));
      }
    }else{
      // Start mapping
      mappings.get().add(new SimpleSourceMapping(new SourcePosition(line, col, templateSource),
              positionInGeneratedFile, pairId));
    }

  }

  protected void addIncludeMapping(int lineInTemplate, int colInTemplate, String templateSource, String includedTemplate, int pairId) {
    int line = Math.max(0, lineInTemplate);
    int col = Math.max(0, colInTemplate);

    Optional<SimpleIncludeMapping> startMappingOpt = includeMappings.get().stream()
            .filter(m -> m.pairId == pairId)
            .findFirst();

    SourcePosition pos = new SourcePosition(line, col, templateSource);
    if(startMappingOpt.isPresent()){
      SimpleIncludeMapping startMapping = startMappingOpt.get();
      // If nothing is between start and end of the include-statement (should never happen), drop to keep output clean
      if(startMapping.sourcePosition.equals(pos)) {
        includeMappings.get().remove(startMapping);
      }else{
        includeMappings.get().add(new SimpleIncludeMapping(pos, pairId, includedTemplate));
      }
    }else{
      // Start mapping
      includeMappings.get().add(new SimpleIncludeMapping(pos, pairId, includedTemplate));
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

  public static void flushMappings(){
    List<DecodedMapping> templateSourceMappings = calculateMappings(mappings.get());
    List<DecodedMapping> astSourceMappings = calculateMappings(astMappings.get());
    List<IncludeSpan> includeSpans = calculateIncludeMappings(includeMappings.get());
    Reporting.reportASTSourceMapping(astSourceMappings);
    Reporting.reportTemplateSourceMapping(templateSourceMappings);
    Reporting.reportTemplateIncludeSpan(includeSpans);
    clearMappings();
  }

  public static void clearMappings(){
    mappings.get().clear();
    mappings.remove();
    astMappings.get().clear();
    astMappings.remove();
    includeMappings.get().clear();
    includeMappings.remove();
  }

  public static void reset() {
    clearMappings();
    templates.get().clear();
    templates.remove();
    curAbsolutePos.get().clear();
    curAbsolutePos.remove();
    baseLineOffset.remove();
  }


  private static class MappingSpan{
    final int pairId;
    final SimpleSourceMapping start;
    final SimpleSourceMapping end;

    MappingSpan(int pairId, SimpleSourceMapping start, SimpleSourceMapping end) {
      this.pairId = pairId;
      this.start = start;
      this.end = end;
    }

    boolean isZeroWidthGenerated(){
      return start.targetPosition.getLine() == end.targetPosition.getLine()
              && start.targetPosition.getColumn() == end.targetPosition.getColumn();
    }
  }

  private static class SimpleIncludeMapping {
    final SourcePosition sourcePosition;
    final int pairId;
    final String targetTemplate;

    SimpleIncludeMapping(SourcePosition sourcePosition, int pairId, String targetTemplate) {
      this.sourcePosition = sourcePosition;
      this.pairId = pairId;
      this.targetTemplate = targetTemplate;
    }

    public String toString(){
      return sourcePosition.toString() + " -> " + targetTemplate;
    }
  }
}
