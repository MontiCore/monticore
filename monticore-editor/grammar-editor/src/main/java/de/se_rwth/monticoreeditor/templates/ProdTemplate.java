/* (c) https://github.com/MontiCore/monticore */

package de.se_rwth.monticoreeditor.templates;

import static de.se_rwth.langeditor.util.Misc.loadImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

import com.google.common.collect.Lists;

public class ProdTemplate {
  
  public ProdTemplate() {
  
  }
  
  public List<TemplateProposal> getTemplateProposals(ITextViewer viewer, int offset,
      String prefix) {
    ArrayList<TemplateProposal> ret = Lists.newArrayList();
    DocumentTemplateContext docContext = new DocumentTemplateContext(new TemplateContextType(),
        viewer.getDocument(), offset - prefix.length(), prefix.length());
    Region region = new Region(offset, viewer.getDocument().getLength());
    
    // LexProd
    addProposal(ret, docContext, region, prefix, "LexProd", "token ${Name} = ${Alts};",
        "icons/tnames_co.gif");
        
    // Interface Prod
    addProposal(ret, docContext, region, prefix, "InterfaceProd", "interface ${Name} = ${Alts};",
        "icons/intf_obj.gif");
        
    // Enum Prod
    addProposal(ret, docContext, region, prefix, "EnumProd", "enum ${Name} = ${Constants};",
        "icons/element.gif");
        
    // External Prod
    addProposal(ret, docContext, region, prefix, "ExtProd", "external ${Name};",
        "icons/element.gif");
        
    // Abstract Prod
    addProposal(ret, docContext, region, prefix, "AbstractProd", "abstract ${Name} = ${Alts};",
        "icons/class_abs_tsk.gif");
        
    // AST rule
    addProposal(ret, docContext, region, prefix, "AstRule", "ast ${Name} = ${AttributeOrMethod};",
        "icons/source_attach_attrib.gif");
        
    // ClassProd
    addProposal(ret, docContext, region, prefix, "ClassProd", "${Name} = ${Alts};",
        "icons/teamstrm_rep.gif");
        
    return ret;
  }
  
  protected void addProposal(List<TemplateProposal> ret, DocumentTemplateContext docContext,
      Region region,
      String prefix, String templateName, String replace, String imagePath) {
    if (templateName.startsWith(prefix)) {
      Template simpleTemplate = new Template(templateName,
          "Insert a simple " + templateName + " template",
          IDocument.DEFAULT_CONTENT_TYPE, replace, false);
      Optional<Image> img = loadImage(imagePath);
      ret.add(new TemplateProposal(simpleTemplate, docContext, region, img.get()));
    }
  }
  
}
