/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.layout.kieler;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.cau.cs.kieler.core.kgraph.KGraphData;
import de.cau.cs.kieler.kiml.LayoutContext;
import de.cau.cs.kieler.kiml.LayoutDataService;
import de.cau.cs.kieler.kiml.LayoutOptionData;
import de.cau.cs.kieler.kiml.config.DefaultLayoutConfig;
import de.cau.cs.kieler.kiml.config.ILayoutConfig;
import de.cau.cs.kieler.kiml.config.IMutableLayoutConfig;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.monticore.genericgraphics.controller.editparts.IMCDiagramEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCEditPart;

/**
 * An implementation of the {@link ILayoutConfig}.
 * 
 * @author Tim Enger
 */
public class MCLayoutConfig implements IMutableLayoutConfig {
  
  /**
   * Priority (not sure where exactly this is needed :))
   */
  public static final int PRIORITY = 30;
  
  /**
   * Store all the values set in layout view in this map.<br>
   * Note: the data is not persistant
   */
  private Map<IMCEditPart, Map<LayoutOptionData<?>, Object>> values;
  
  /**
   * Constructor
   */
  public MCLayoutConfig() {
    values = new HashMap<IMCEditPart, Map<LayoutOptionData<?>, Object>>();
  }
  
  @Override
  public boolean isSet(LayoutOptionData<?> optionData, LayoutContext context) {
    if (getValue(optionData, context) != null) {
      return true;
    }
    return false;
  }
  
  @Override
  public int getPriority() {
    return PRIORITY;
  }
  
  @Override
  public void enrich(LayoutContext context) {
    // make option visible in the layout view
    Object editPart = context.getProperty(LayoutContext.DIAGRAM_PART);
    if (editPart instanceof IMCDiagramEditPart) {
      // editPart is the diagram
      context.setProperty(LayoutContext.OPT_TARGETS, EnumSet.of(LayoutOptionData.Target.PARENTS));
    }
    else if (editPart instanceof IMCEditPart) {
      // editPart is part of the diagram
      context.setProperty(LayoutContext.OPT_TARGETS, EnumSet.of(LayoutOptionData.Target.NODES));
    }
    
    // should the options list be created?
    if (context.getProperty(DefaultLayoutConfig.OPT_MAKE_OPTIONS)) {
      
      // set the layout algorithm
      @SuppressWarnings("unchecked")
      LayoutOptionData<String> algorithmOptionData = (LayoutOptionData<String>) LayoutDataService.getInstance().getOptionData(LayoutOptions.ALGORITHM.getId());
      if (algorithmOptionData != null) {
        Object contentLayoutHint = getValue(algorithmOptionData, context);
        if (contentLayoutHint != null) {
          context.setProperty(DefaultLayoutConfig.CONTENT_HINT, contentLayoutHint);
        }
      }
    }
  }
  
  @Override
  public Object getValue(LayoutOptionData<?> optionData, LayoutContext context) {
    Object object = context.getProperty(LayoutContext.DIAGRAM_PART);
    if (object instanceof IMCEditPart) {
      IMCEditPart ep = (IMCEditPart) context.getProperty(LayoutContext.DIAGRAM_PART);
      return getMapValue(ep, optionData);
    }
    return null;
  }
  
  @Override
  public void setValue(LayoutOptionData<?> optionData, LayoutContext context, Object value) {
    Object object = context.getProperty(LayoutContext.DIAGRAM_PART);
    if (object instanceof IMCEditPart) {
      IMCEditPart ep = (IMCEditPart) object;
      putMapValue(ep, optionData, value);
    }
  }
  
  @Override
  public void transferValues(KGraphData graphData, LayoutContext context) {
    Object object = context.getProperty(LayoutContext.DIAGRAM_PART);
    if (object instanceof IMCEditPart) {
      IMCEditPart ep = (IMCEditPart) object;
      Map<LayoutOptionData<?>, Object> map = values.get(ep);
      if (map != null) {
        for (Entry<LayoutOptionData<?>, Object> entry : map.entrySet()) {
          graphData.setProperty(entry.getKey(), entry.getValue());
        }
      }
    }
  }
  
  @Override
  public void clearValues(LayoutContext context) {
    values.clear();
  }
  
  private void putMapValue(IMCEditPart ep, LayoutOptionData<?> optionData, Object value) {
    Map<LayoutOptionData<?>, Object> map = values.get(ep);
    if (map == null) {
      map = new HashMap<LayoutOptionData<?>, Object>();
      values.put(ep, map);
    }
    map.put(optionData, value);
  }
  
  private Object getMapValue(IMCEditPart ep, LayoutOptionData<?> optionData) {
    Map<LayoutOptionData<?>, Object> map = values.get(ep);
    if (map != null) {
      return map.get(optionData);
    }
    return null;
  }
  
}
