package de.monticore.source_mapping;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.SourcePosition;
import freemarker.template.Template;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.StringWriter;
import java.util.*;

public class SourceMappingUtil {
  protected static Stack<Template> templates = new Stack<>();
  protected static int lastReportedPosition = 0;
  protected static int offset;
  public static List<SimpleSourceMapping> mappings = new ArrayList<>();
  public static List<SimpleSourceMapping> astMappings = new ArrayList<>();

  public static void pushTemplate(Template template) {
    if(templates.isEmpty()){
      lastReportedPosition = 0;
    }
    templates.push(template);
    offset = lastReportedPosition;
  }

  public static void popTemplate(Template template, StringBuilder content) {
    if (templates.pop() != template) {
      throw new IllegalStateException();
    }

    if (templates.size() <= 1) {
      offset = 0;
    } else {
      offset = lastReportedPosition;
    }

    if (templates.isEmpty()) {
      System.out.println("Result: "+calculateMappings(astMappings, content.toString()));
      System.out.println("Result: "+calculateMappings(mappings, content.toString()));
      //Reporting.reportTemplateSourceMapping(template.getName(), res);

      astMappings.clear();
      mappings.clear();
    }
  }

  public static List<SourceMapping> calculateMappings(List<SimpleSourceMapping> simpleMappings, String content) {
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
    List<SourceMapping> res = new ArrayList<>();
    for (Pair<SimpleSourceMapping, SimpleSourceMapping> pair : pairs) {
      SimpleSourceMapping p1 = pair.getKey();
      SimpleSourceMapping p2 = pair.getValue();

      p1.calcTargetPosition(contentString);
      p2.calcTargetPosition(contentString);

      res.add(new SourceMapping(
          new PositionMarker(p1.sourcePosition, p1.targetPosition),
          new PositionMarker(p2.sourcePosition, p2.targetPosition)
      ));
    }
    return res;
  }

  final StringWriter sw;
  final Template template;

  public SourceMappingUtil(StringWriter sw, Template template) {
    this.sw = sw;
    this.template = template;
  }

  public void reportStart(int pairId, int lineInTemplate, int colInTemplate, ASTNode astNode) {
    String content = sw.toString();
    int pos = content.length();
    if(astNode!=null) {
      if(astNode.isPresent_SourcePositionStart()) {
        astMappings.add(new SimpleSourceMapping(astNode.get_SourcePositionStart(), (offset + pos), pairId));
      }
    }
    mappings.add(new SimpleSourceMapping(new SourcePosition(lineInTemplate, colInTemplate, template.getName()), (offset + pos), pairId));
    lastReportedPosition = pos;
  }

  public void reportEnd(int pairId, int lineInTemplate, int colInTemplate, ASTNode astNode) {
    String content = sw.toString();
    int pos = content.length();
    if(astNode!=null) {
      if(astNode.isPresent_SourcePositionStart()) {
        astMappings.add(new SimpleSourceMapping(astNode.get_SourcePositionEnd(), (offset + pos), pairId));
      }
    }
    mappings.add(new SimpleSourceMapping(new SourcePosition(lineInTemplate, colInTemplate, template.getName()), (offset + pos), pairId));
    lastReportedPosition = pos;
  }
}
