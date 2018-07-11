${signature()}
<#-- --------------------------------------------------------- 
     Block 1: explicit binding a hook point to a template
     --------------------------------------------------------- -->
${glex.bindHookPoint("aComment1",
    tc.instantiate(
      "de.monticore.generating.templateengine.TemplateHookPoint",
      ["tpl4/SE-Copyright.ftl"]))}
${glex.defineHookPoint(tc,"aComment1",ast)}

<#-- --------------------------------------------------------- 
     Block 2: using a shortcut for a template bind
     --------------------------------------------------------- -->
<#-- or with these shortcuts: -->
${glex.bindTemplateHookPoint("aComment2",
                             "tpl4/SE-Copyright.ftl")}
${glex.defineHookPoint(tc,"aComment2",ast)}
<#-- --------------------------------------------------------- 

     Block 3: using a shortcut for an inline String 
     --------------------------------------------------------- -->
${glex.bindStringHookPoint("aComment3",
                           "// Developed by SE RWTH\n")}
${glex.defineHookPoint(tc,"aComment3",ast)}
