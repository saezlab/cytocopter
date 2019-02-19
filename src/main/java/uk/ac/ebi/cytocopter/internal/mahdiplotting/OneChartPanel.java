package uk.ac.ebi.cytocopter.internal.mahdiplotting;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class OneChartPanel extends JPanel
{
	XYDataset dataset;
	JFreeChart chart=null;
	boolean xMarkers;
	boolean yMarkers;
	

	public OneChartPanel(XYDataset dataset, boolean xMarkers, boolean yMarkers )
	{
		this.dataset = dataset;
		this.xMarkers = xMarkers;
		this.yMarkers = yMarkers;
		
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(160, 160));
		setPreferredSize(new Dimension(160,160));
		//setSize(new Dimension(16, 16));
		drawing();
	}
	
	private void drawing()
	{
		chart = ChartFactory.createXYLineChart(null, null, null, dataset,PlotOrientation.VERTICAL,false,false,false);
		
		int containingSeriesNumbers = dataset.getSeriesCount();
		
		XYPlot plot = chart.getXYPlot();
		
		
		plot.setBackgroundPaint(Color.WHITE);
		
		
		final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

		if (dataset.getSeriesCount() == 2)
		{
			renderer.setSeriesPaint(0, Color.LIGHT_GRAY);
			renderer.setSeriesPaint(1, Color.BLUE);
			renderer.setSeriesShape(0, new Rectangle(-6, -6, 12, 12));
			renderer.setSeriesShape(1, new Rectangle(-6, -6, 12, 12));
			renderer.setSeriesStroke(0, new BasicStroke(5.0f));
			renderer.setSeriesStroke(1, new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
			        1.0f, new float[] {10.0f, 10.0f}, 0.0f));
			renderer.setSeriesShapesVisible(0, true);
			renderer.setSeriesShapesVisible(1, true);
			
			
			
			double x0 = dataset.getYValue(0, 0);
			double xn = dataset.getYValue(0, 1);
			
			double s0 = dataset.getYValue(1, 0);
			double sn = dataset.getYValue(1, 1);
			
			double blueAmount = Math.pow(x0-s0, 2) + Math.pow(xn-sn, 2);
			plot.setBackgroundPaint(new Color(1.0f,1.0f, Math.max(0, 1-(float)blueAmount)));
		}
		else if (dataset.getSeriesCount() == 1)
		{
			renderer.setSeriesPaint(0, new Color(100,149,237));
			renderer.setSeriesShape(0, new Rectangle(-3, -3, 6, 6));
			renderer.setSeriesStroke(0, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(0, true);

		}

		ValueAxis axisX = plot.getDomainAxis();
        axisX.setTickLabelsVisible(xMarkers);
        axisX.setAxisLineVisible(xMarkers);
        axisX.setTickMarksVisible(xMarkers);
        axisX.setTickLabelFont(new Font("Dialog", Font.PLAIN, 30));        
        
        ValueAxis axisY = plot.getRangeAxis();
        axisY.setTickLabelsVisible(yMarkers);
        axisY.setTickMarksVisible(yMarkers);
        axisY.setAxisLineVisible(yMarkers);
        axisY.setRange(0, 1);
        axisY.setTickLabelFont(new Font("Dialog", Font.PLAIN, 20));
        
        ChartPanel cp = new ChartPanel(chart);
        
        add(cp);
	}
	
	
	
	
	
	

}
