package uk.ac.ebi.cytocopter.internal.mahdiplotting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

public class StimBarChartPanel extends JPanel
{
	final CategoryDataset dataset;
	final boolean xMarkers;
	
	public StimBarChartPanel(CategoryDataset dataset, boolean xMarkers)
	{
		this.dataset = dataset;
		this.xMarkers = xMarkers;
		
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(80, 160));
		setPreferredSize(new Dimension(80,160));
		setMaximumSize(new Dimension(80, 160));
		drawing();
	}
	
	public void drawing()
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart barChart = ChartFactory.createBarChart(null, null, null, dataset,          
										PlotOrientation.VERTICAL, false, false, false);
		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeCrosshairVisible(false);
		plot.setRangeZeroBaselineVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setRangeMinorGridlinesVisible(false);
		
		CategoryAxis domainAxis =  (CategoryAxis)plot.getDomainAxis();
		domainAxis.setTickLabelFont(new Font("Dialog", Font.BOLD, 30));
		domainAxis.setTickMarksVisible(xMarkers);
		domainAxis.setAxisLineVisible(xMarkers);
		domainAxis.setTickLabelsVisible(xMarkers);

		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setTickLabelsVisible(false);
		rangeAxis.setAxisLineVisible(false);
		rangeAxis.setTickMarksVisible(false);
		
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(115,179,96));
		renderer.setDrawBarOutline(false);
		
        
        ChartPanel cp = new ChartPanel(barChart);
        add(cp);
	}
	
	

}
