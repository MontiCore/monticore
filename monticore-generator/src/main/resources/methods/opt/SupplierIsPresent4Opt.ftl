<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
var optional = this.${attribute.getName()}.get();
if (optional == null){
    Log.warn("supplier for ${attribute.getName()} is null");
    return false;
}
return optional.isPresent();
