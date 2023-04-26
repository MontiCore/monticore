<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("traverserName", "millName", "superNames", "fullSuperNames", "superCDs", "currentCD")}

${traverserName} traverser = ${millName}.traverser();
this.setTraverser(traverser);

this.getTraverser().set${currentCD}Handler(this);

<#list superNames as superName>
    this.${superName} = new ${fullSuperNames[superName?index]}();
    this.getTraverser().set${superCDs[superName?index]}Handler(this.${superName});
</#list>