/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;

import de.monticore.genericgraphics.view.figures.connections.locators.EndPointFigureLocator;
import de.monticore.genericgraphics.view.figures.connections.locators.FigureArrowLocator;


/**
 * <p>
 * An implementation of the {@link IFigureEndPolylineConnection} interface by
 * extension of the {@link PolylineConnection} implementation.
 * </p>
 * 
 * @author Tim Enger
 */
public class FigureEndPolylineConnection extends ExtendedPolylineConnection implements IFigureEndPolylineConnection {
  
  private Label sourceLabel;
  private Label targetLabel;
  
  private RotatableDecoration sourceArrow;
  private RotatableDecoration targetArrow;
  
  @Override
  public void setSourceDecoration(RotatableDecoration dec) {
    if (sourceArrow == dec) {
      return;
    }
    if (sourceArrow != null) {
      remove(sourceArrow);
    }
    sourceArrow = dec;
    if (sourceArrow != null) {
      add(sourceArrow, new FigureArrowLocator(this, ConnectionLocator.SOURCE, sourceLabel));
    }
  }
  
  @Override
  public void setTargetDecoration(RotatableDecoration dec) {
    if (targetArrow == dec) {
      return;
    }
    if (targetArrow != null) {
      remove(targetArrow);
    }
    targetArrow = dec;
    if (targetArrow != null) {
      add(targetArrow, new FigureArrowLocator(this, ConnectionLocator.TARGET, targetLabel));
    }
  }
  
  @Override
  public void setSourceEndpointLabelDecoration(String text) {
    sourceLabel = new Label(text);
    sourceLabel.setBackgroundColor(ColorConstants.white);
    sourceLabel.setBorder(new LineBorder(ColorConstants.black));
    sourceLabel.setOpaque(true);
    ConnectionLocator loc = new EndPointFigureLocator(this, ConnectionLocator.SOURCE, sourceLabel);
    add(sourceLabel, loc);
  }
  
  @Override
  public void setTargetEndpointLabelDecoration(String text) {
    targetLabel = new Label(text);
    targetLabel.setBackgroundColor(ColorConstants.white);
    targetLabel.setBorder(new LineBorder(ColorConstants.black));
    targetLabel.setOpaque(true);
    ConnectionLocator loc = new EndPointFigureLocator(this, ConnectionLocator.TARGET, targetLabel);
    add(targetLabel, loc);
  }
  
  @Override
  protected RotatableDecoration getTargetDecoration() {
    return targetArrow;
  }
  
  @Override
  protected RotatableDecoration getSourceDecoration() {
    return sourceArrow;
  }
}
