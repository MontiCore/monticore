/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorSetup;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class TemplateStringHookPoint extends HookPoint {
  private Template template;

  public TemplateStringHookPoint(String statement) throws IOException {
    super();
    template = new Template("template", new StringReader(statement),
        new Configuration(GeneratorSetup.FREEMARKER_VERSION));
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
