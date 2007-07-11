package net.sf.mzmine.modules.dataanalysis.projectionplots;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class ProjectionPlotRenderer extends XYLineAndShapeRenderer {

	private static final Shape dataPointsShape = new Ellipse2D.Float(-3, -3, 7, 7);
	
	private ProjectionPlotDataset dataset;
	
	public ProjectionPlotRenderer(ProjectionPlotDataset dataset) {
		super(false,true);
		this.dataset = dataset;
		this.setShape(dataPointsShape);
	}

	@Override
	public Paint getItemPaint(int series, int item) {
	
		return dataset.getColor(item);
	}
	
	
}
