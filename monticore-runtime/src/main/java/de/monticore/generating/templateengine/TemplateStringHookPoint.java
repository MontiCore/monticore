/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateStringHookPoint extends HookPoint {
  private Template template;

  public TemplateStringHookPoint(String statement) throws IOException {
    super();
    template = new Template("template", new StringReader(statement),
        new Configuration(Configuration.VERSION_2_3_23));
  }
  
  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    return controller.runInEngine(Lists.newArrayList(), template, ast).toString();
  }

  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    return controller.runInEngine(args, template, null).toString();
  }

   @Override
  public String processValue(TemplateController controller, ASTNode node, List<Object> args) {
     return controller.runInEngine(args, template, node).toString();
  }

}
