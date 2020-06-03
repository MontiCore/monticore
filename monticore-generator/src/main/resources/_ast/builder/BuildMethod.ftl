<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass", "mandatoryAttributes")}
        if (!isValid()) {
        <#list mandatoryAttributes as attribute>
            if (${attribute.getName()} == null) {
                Log.error("0xA4522 ${attribute.getName()} of type ${attribute.printType()} must not be null");
            }
        </#list>
          throw new IllegalStateException();
        }
        ${domainClass.getName()} value;
        ${tc.include("_ast.builder.BuildInit")}
        return value;
