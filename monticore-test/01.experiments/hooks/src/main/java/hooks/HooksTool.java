/* (c) https://github.com/MontiCore/monticore */
package hooks;

import automata.TemplatesTool;
import de.monticore.generating.templateengine.TemplateHookPoint;

/**
 * Main class for the Hooks Example DSL tool.
 * It extends the Template Example Tool and replaces the template 
 * for satte attributes
 */
public class HooksTool extends TemplatesTool {
  
  public HooksTool(String[] args) {
    super(args);
  }
  
  public static void main(String[] args) {
    new HooksTool(args);
  }
  
  
  /**
   * Initializes the global extension management
   */
  protected void initGlex() {
  super.initGlex();

    // This replaces standardtemplate StatechartStateAttributes.ftl
    // by our local MyStateAttributes.ftl
    glex.replaceTemplate("StatechartStateAttributes.ftl", 
            new TemplateHookPoint(        "MyStateAttributes.ftl"));

    // This adds the decorator template MyStateGetter.ftl 
    // after standardtemplate StatechartStateAttributes.ftl
    // (even if it is also replaced)
    glex.setAfterTemplate("StatechartStateAttributes.ftl",
                new TemplateHookPoint("MyStateGetter.ftl"));

    // This fills (binds) explicitly defined hook points
    // using a fixed string 
    glex.bindStringHookPoint(
        "<JavaBlock>?ConcreteState:handle", "count++;");
    glex.bindStringHookPoint("<Field>*ConcreteState", "int count;");

  }
  
}
