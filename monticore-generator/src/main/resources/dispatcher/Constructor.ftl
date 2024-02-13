<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("traverserName", "millName", "currentCD", "superCDs")}

${traverserName} traverser = ${millName}.traverser();
this.setTraverser(traverser);

this.getTraverser().set${currentCD}Handler(this);
<#list superCDs as superCD>
    this.getTraverser().set${superCD}Handler(this);
</#list>
