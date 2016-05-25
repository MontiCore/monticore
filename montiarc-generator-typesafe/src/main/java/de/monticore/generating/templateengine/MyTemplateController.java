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
   * @see de.monticore.generating.templateengine.TemplateController#processTemplate(java.lang.String, de.monticore.ast.ASTNode, java.util.List)
   */
  @Override
  public String processTemplate(String templateName, ASTNode astNode,
      List<Object> passedArguments) {
    return super.processTemplate(templateName, astNode, passedArguments);
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
    List<Object> arguments = getArguments();
    List<String> names = getSignature();
    checkArgument(params.length == arguments.size(),
        "0xA5298 Template '" + getTemplatename() + "': Parameter size (#" +
            params.length +
            ") and number of arguments (#" + arguments.size() + ") mismatch.");
    List<String> toSignature = new ArrayList<String>();
    for (int i = 0; i < arguments.size(); i++) {
      Object argument = arguments.get(i);
      String parameter = params[i];
      String paramType = parameter.substring(0, parameter.indexOf(" "));
      if(paramType.contains("<")){
        paramType = paramType.substring(0, paramType.indexOf("<"));
      }
      String paramName = parameter.substring(parameter.indexOf(" ") + 1);
      Class argumentClass = argument.getClass();
      // TODO instanceof things
//      try {
//        if (paramType.contains(".")) {
//          checkArgument(argumentClass.isAssignableFrom(Class.forName(paramType)),
//              "0xA5299 Template '" + getTemplatename() + "': Argument type (" +
//                  argument +
//                  ") and type of parameter in params (" + paramType + ") mismatch.");
//        }
//        else {
//          if (!isAssignableWithJavaLibrary(paramType)) {
//            throw new IllegalArgumentException("0xA5299 Template '" + getTemplatename()
//                + "': Argument type (" +
//                argument +
//                ") and type of parameter in params (" + paramType + ") mismatch.");
//          }
//        }
//      }
//      catch (ClassNotFoundException e) {
//        e.printStackTrace();
//      }
      
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
            "0xA5300 Template '" + getTemplatename() + "': Parameter name (" +
                paramName +
                ") and name of parameter in signature (" + argumentName + ") mismatch.");
      }
    }
    
    if (!toSignature.isEmpty()) {
      signature(toSignature);
    }
    parametrized = true;
  }
  
//  /**
//   * TODO: Write me!
//   * 
//   * @param argumentClass
//   * @return
//   */
//  private boolean isAssignableWithJavaLibrary(String paramType) {
//    String[] classNamesToCheck = { "List", "int", "Map", "double", "Integer",
//        "Double", "String", "Long", "Short", "double", "short", "Set",
//        "long", "Character", "char", "Float", "float", "boolean",
//        "Boolean"};
//    for (String c : classNamesToCheck) {
//      if (paramType.contains(c)) {
//        return true;
//      }
//    }
//    
//    return false;
//  }
  
  /**
   * Checks whether there are more than one result definitions.
   * 
   * @param result
   */
  public void result(String result) {
    checkArgument(!resultized,
        "0xA5301 Template '" + getTemplatename() + "': tried to invoke result() twice");
    resultized = true;
    
  }
  
}
