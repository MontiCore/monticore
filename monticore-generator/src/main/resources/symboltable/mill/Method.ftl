<#-- (c) https://github.com/MontiCore/monticore -->

  ${tc.signature("className", "attribute", "builderName")}
  <#assign millName = "mill" + attribute>

  public static ${builderName} ${builderName?uncap_first}() {
    if(${millName} == null) {
      ${millName} = getMill();
    }

    return ${millName}._${builderName?uncap_first}();
  }

  protected ${builderName} _${builderName?uncap_first}() {
    return new ${builderName}();
  }
