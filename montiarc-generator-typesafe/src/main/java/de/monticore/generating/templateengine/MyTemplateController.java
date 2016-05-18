/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating.templateengine;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import de.monticore.ast.ASTNode;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MyTemplateController extends TemplateController {
  
  private boolean parametrized = false;
  
  private boolean resultized = false;
  
  /**
   * Constructor for de.montiarc.generator.codegen.MyTemplateController
   * 
   * @param tcConfig
   * @param templatename
   */
  public MyTemplateController(TemplateControllerConfiguration tcConfig, String templatename) {
    super(tcConfig, templatename);
  }
  
  /**
   * Compares types of the passed params with the arguments passed in the
   * template call. Additionally signatures the names of the parameters. Method
   * is important for inter Template calls and for users who do not use the
   * generated template class with its static generate method.
   * 
   * @param params
   */
  public void params(String... params) {
    checkArgument(!parametrized,
        "0xA5297 Template '" + getTemplatename() + "': tried to invoke params() twice");
    List<Object> types = getArguments();
    List<String> names = getSignature();
    checkArgument(params.length == types.size(),
        "0xA5298 Template '" + getTemplatename() + "': Parameter size (#" +
            params.length +
            ") and number of arguments (#" + types.size() + ") mismatch.");
    List<String> toSignature = new ArrayList<String>();
    for (int i = 0; i < types.size(); i++) {
      Object argumentType = types.get(i);
      String parameter = params[i];
      String paramType = parameter.substring(0, parameter.indexOf(" "));
      String paramName = parameter.substring(parameter.indexOf(" ") + 1);
      
      // TODO instanceof things
      
      // Case 1: No Signature -> we have to signature the paramnames
      if (names.isEmpty()) {
        toSignature.add(paramName);
      }
      // Case 2: User wrote signature() additionally to params() -> we do not
      // need to signature the parameter names, but compare them to the
      // parameter names in the params() method.
      else {
        String argumentName = names.get(i);
        checkArgument(argumentName.equals(paramName),
            "0xA5298 Template '" + getTemplatename() + "': Parameter name (" +
                paramName +
                ") and name of parameter in signature (" + argumentName + ") mismatch.");
      }
    }
    
    if (!toSignature.isEmpty()) {
      signature(toSignature);
    }
    parametrized = true;
  }
  
  /**
   * Checks whether there are more than one result definitions.
   * 
   * @param result
   */
  public void result(String result) {
    checkArgument(!resultized,
        "0xA5297 Template '" + getTemplatename() + "': tried to invoke result() twice");
    resultized = true;
    
  }
  
}
