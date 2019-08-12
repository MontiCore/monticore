<#if isCurrentDiagram>
    ${cocoCollectionName}.add(coco);
<#else>
    /* add it to the corresponding language's checker.
    * The findFirst is always present because we add at least
    * one checker during initialization. This checker is used, so we
    * do not modify composed checkers.
    */
    ${checker}.stream().findFirst().get().addCoCo(coco);
</#if>
return this;
