<#-- (c) https://github.com/MontiCore/monticore -->
<#assign n = ast.getName()>
  public static ${n}State get${n}State() {
    return ${n?uncap_first};
  }
 