${tc.signature("mandatoryAttributes")}
        <#list mandatoryAttributes as attribute>
        if (${attribute.getName()} == null) {
            return false;
        }
        </#list>
        return true;