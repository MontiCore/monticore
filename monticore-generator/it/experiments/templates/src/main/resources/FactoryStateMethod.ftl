<#-- (c) https://github.com/MontiCore/monticore -->

  /**
   * create new state object for ${ast.getName()}
   */
  public static ${ast.getName()}State get${ast.getName()}State(){
    return new ${ast.getName()?cap_first}State();
  }

