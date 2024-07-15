/* (c) https://github.com/MontiCore/monticore */
import de.monticore.ast.ASTNode;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tags._ast.ASTValuedTag;
import de.monticore.tagtest.cdbasis4tags._ast.ASTCDClass;
import de.monticore.tagtest.cdbasis4tags._ast.ASTCDCompilationUnit;
import de.monticore.tagtest.cdbasis4tagstagdefinition.CDBasis4TagsTagDefinitionMill;
import de.monticore.tagtest.cdbasis4tags.CDBasis4TagsMill;
import de.monticore.umlstereotype.UMLStereotypeMill;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereoValueBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.tagging.TagRepository;
import de.monticore.tagtest.cdbasis4tags._tagging.CDBasis4TagsTagger;

import de.monticore.tagtest.cdbasis4tags._visitor.CDBasis4TagsVisitor2;
import de.monticore.tagtest.cdbasis4tags._visitor.CDBasis4TagsTraverser;
import java.io.File;
import java.util.*;

/**
 * This test aims to replicate parts of CD4A to test the tagging infrastucture
 * in regards to interface usage (such as types)
 *
 * In addition, we demonstrate the interaction between tags and stereotypes
 */
public class CDTest {
  @BeforeClass
  public static void init() {
    CDBasis4TagsTagDefinitionMill.init();
  }

  @Test
  public void test() throws Exception{
     Optional<ASTCDCompilationUnit> ast = CDBasis4TagsMill.parser().parse(new File("src/test/resources/models/Door.cd").toString());
    Assert.assertTrue(ast.isPresent());
    CDBasis4TagsMill.scopesGenitorDelegator().createFromAST(ast.get());


    Optional<ASTTagUnit> doorsThatAreOpenableTags = TagRepository.loadTagModel(new File("src/test/resources/models/DoorsThatAreOpenable.tags"));
    Assert.assertTrue(doorsThatAreOpenableTags.isPresent());

    Optional<de.monticore.tagtest.cdbasis4tags._symboltable.CDTypeSymbol> theDoorSymbol = ast.get().getEnclosingScope().resolveCDType("TheDoor");
    Optional<de.monticore.tagtest.cdbasis4tags._symboltable.CDTypeSymbol> aSingleDoorSymbol = ast.get().getEnclosingScope().resolveCDType("ASingleDoor");
    Assert.assertTrue(theDoorSymbol.isPresent());
    Assert.assertTrue(aSingleDoorSymbol.isPresent());

    List<ASTTag> tagsForTheDoor = new ArrayList<>(CDBasis4TagsTagger.getInstance().getTags(theDoorSymbol.get()));
    Assert.assertEquals(1, tagsForTheDoor.size());

    List<ASTTag> tagsForASingleDoor = new ArrayList<>(CDBasis4TagsTagger.getInstance().getTags(aSingleDoorSymbol.get()));
    Assert.assertEquals(1, tagsForASingleDoor.size());


    // Turn stereos into tags
    ASTTagUnit tempTagUnit = TagRepository.loadTempTagModel("temp").get();
    tagsFromStereo(ast.get(), tempTagUnit);

    tagsForASingleDoor.addAll(CDBasis4TagsTagger.getInstance().getTags(aSingleDoorSymbol.get()));
    Assert.assertEquals(2, tagsForASingleDoor.size());

    stereoFromTags(ast.get(), Collections.singleton(doorsThatAreOpenableTags.get()));

    String pretty = CDBasis4TagsMill.prettyPrint(ast.get(), false);
  }

  // The Stereo<->Tag tooling should be, eventually, generated
  protected void tagsFromStereo(ASTNode astNode, ASTTagUnit tagUnit) {
    CDBasis4TagsTraverser t =  CDBasis4TagsMill.traverser();
    t.add4CDBasis4Tags(new CDBasis4TagsFromStereo(tagUnit));

    astNode.accept(t);
  }

  // The Stereo<->Tag tooling should be, eventually, generated
  protected void stereoFromTags(ASTNode astNode, Collection<ASTTagUnit> tagUnits) {
    CDBasis4TagsTraverser t =  CDBasis4TagsMill.traverser();
    t.add4CDBasis4Tags(new CDBasis4TagsToStereo(tagUnits));

    astNode.accept(t);
  }

  // The Stereo<->Tag tooling should be, eventually, generated
  class CDBasis4TagsFromStereo implements CDBasis4TagsVisitor2 {

    protected ASTTagUnit tagUnit;

    public CDBasis4TagsFromStereo(ASTTagUnit tagUnit) {
      this.tagUnit = tagUnit;
    }

    @Override
    public void visit(ASTCDClass node) {
      if (node.getModifier().isPresentStereotype()) {
         for (ASTStereoValue value: node.getModifier().getStereotype().getValuesList()) {
           CDBasis4TagsTagger.getInstance().addTag(node, tagUnit, getTagFromStereo(value));
         }
      }
    }
  }

  // The Stereo<->Tag tooling should be, eventually, generated
  class CDBasis4TagsToStereo implements CDBasis4TagsVisitor2 {

    protected Collection<ASTTagUnit> tagUnits;

    public CDBasis4TagsToStereo(Collection<ASTTagUnit> tagUnits) {
      this.tagUnits = tagUnits;
    }

    @Override
    public void visit(ASTCDClass node) {
      List<ASTTag> tags = CDBasis4TagsTagger.getInstance().getTags(node, this.tagUnits);
      for (ASTTag tag : tags) {
        if (!node.getModifier().isPresentStereotype())
          node.getModifier().setStereotype(UMLStereotypeMill.stereotypeBuilder().build());

        ASTStereoValueBuilder builder = UMLStereotypeMill.stereoValueBuilder();
        if (tag instanceof ASTSimpleTag) {
          builder.setName(((ASTSimpleTag) tag).getName());
          builder.setContent("");
        } else if (tag instanceof ASTValuedTag) {
          builder.setName(((ASTValuedTag) tag).getName());
          builder.setName(((ASTValuedTag) tag).getValue());
        }
        node.getModifier().getStereotype().addValues(builder.build());
      }
    }
  }

  protected ASTTag getTagFromStereo(ASTStereoValue value) {
    if (value.getValue().isEmpty()) {
      return TagsMill.simpleTagBuilder().setName(value.getName()).build();
    }
    return TagsMill.valuedTagBuilder()
            .setName(value.getName())
            .setValue(value.getValue())
            .build();
  }

}
