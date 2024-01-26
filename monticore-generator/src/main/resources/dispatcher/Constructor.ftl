<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("traverserName", "millName", "currentCD")}

${traverserName} traverser = ${millName}.traverser();
this.setTraverser(traverser);

this.getTraverser().set${currentCD}Handler(this);