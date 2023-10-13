/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._tagging;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;

public class CDTaggingDecorator extends AbstractDecorator {
  protected final TaggerDecorator taggerDecorator;
  protected final TagConformsToSchemaCoCoDecorator coCoDecorator;

  public CDTaggingDecorator(GlobalExtensionManagement glex, TaggerDecorator taggerDecorator, TagConformsToSchemaCoCoDecorator coCoDecorator) {
    super(glex);
    this.taggerDecorator = taggerDecorator;
    this.coCoDecorator = coCoDecorator;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage taggingPackage = getPackage(input, decoratedCD, TaggingConstants.TAGGING_PACKAGE);

    taggingPackage.addAllCDElements(taggerDecorator.decorate());
    taggingPackage.addAllCDElements(coCoDecorator.decorate());
  }

  @Override
  protected String getPackageName(ASTCDCompilationUnit origCD, String subPackage) {
    String cdDefName = origCD.getCDDefinition().getName();
    // Note: We change from the TagDef/TagSchema package into the original grammars package
    if (cdDefName.toLowerCase().endsWith(TaggingConstants.TAGDEFINITION_SUFFIX.toLowerCase()))
      cdDefName = cdDefName.substring(0, cdDefName.length() - TaggingConstants.TAGDEFINITION_SUFFIX.length());
    if (cdDefName.toLowerCase().endsWith(TaggingConstants.TAGSCHEMA_SUFFIX.toLowerCase()))
      cdDefName = cdDefName.substring(0, cdDefName.length() - TaggingConstants.TAGSCHEMA_SUFFIX.length());
    // end changes
    String origPackage = Names.constructQualifiedName(origCD.getCDPackageList(), cdDefName);
    return (subPackage.isEmpty() ? origPackage : Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }
}
