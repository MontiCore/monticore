<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
var optional = this.${attribute.getName()}.get();
if (optional == null){
    Log.info("supplier for ${attribute.getName()} is null", "isPresent${attribute.getName()}");
    return false;
}
return optional.isPresent();
