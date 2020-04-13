<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = ast.getName()>
  protected static ${name}State ${name?uncap_first} =
			new ${ast.getName()?cap_first}State();
