<#-- (c) https://github.com/MontiCore/monticore -->
  public static ${ast.getName()}State get${ast.getName()}State(){
    return new ${ast.getName()?cap_first}State();
  }