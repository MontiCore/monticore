/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine;

import de.monticore.generating.GeneratorSetup;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class adds the signature methods tc.params(...) and tc.result(...) to
 * the existing TemplateController.
 */
public class ExtendedTemplateController extends TemplateController {
  
  /**
   * Ensures tc.params(...) is only called once
   */
  private boolean parametrized = false;
  
  /**
   * Constructor for de.montiarc.generator.codegen.MyTemplateController
   * 
   * @param tcConfig
   * @param templatename
   */
  public ExtendedTemplateController(GeneratorSetup setup, String templatename) {
    super(setup, templatename);
  }
  
  /**
   * Compares types of the passed params with the arguments passed in the
   * template call. Additionally signatures the names of the parameters. Method
   * is important for inter Template calls and for users who do not use the
   * generated template class with its static generate method. All types that
   * are no java library types have to be defined fully qualified.
   * 
   * @param params
   */
  public void params(String... params) {
    checkArgument(!parametrized,
        "0xA5299 Template '" + getTemplatename() + "': tried to invoke params() twice");
    List<Object> arguments = getArguments();
    // throws error when argument size != param size
    checkArgument(params.length == arguments.size(),
        "0xA5296 Template '" + getTemplatename() + "': Parameter size (#" +
            params.length +
            ") and number of arguments (#" + arguments.size() + ") mismatch.");
    List<String> toSignature = new ArrayList<String>();
    
    // compares params and args
    for (int i = 0; i < arguments.size(); i++) {
      String parameter = params[i];
      String paramType = parameter.substring(0, parameter.indexOf(" "));
      paramType = paramType.trim();
      if (paramType.contains(">")) {
        paramType = paramType.substring(0, paramType.indexOf(">"));
      }
      String paramName = parameter.substring(parameter.indexOf(paramType) + paramType.length() +1).trim();
      toSignature.add(paramName);
    }
    
    if (!toSignature.isEmpty()) {
      signature(toSignature);
    }
    parametrized = true;
  }
  
}
