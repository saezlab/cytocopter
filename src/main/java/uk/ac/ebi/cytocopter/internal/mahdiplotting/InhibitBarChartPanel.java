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
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;

public class InhibitBarChartPanel extends JPanel
{
	final CategoryDataset dataset;
	final boolean xMarkers;
	
	public InhibitBarChartPanel(CategoryDataset dataset, boolean xMarkers)
	{
		this.dataset = dataset;
		this.xMarkers = xMarkers;
		
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(80, 160));
		setPreferredSize(new Dimension(80,160));
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
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setTickLabelsVisible(false);
		rangeAxis.setAxisLineVisible(false);
		rangeAxis.setTickMarksVisible(false);
		
		CategoryAxis domainAxis =  (CategoryAxis)plot.getDomainAxis();
		domainAxis.setTickLabelFont(new Font("Dialog", Font.BOLD, 30));
		domainAxis.setTickMarksVisible(xMarkers);
		domainAxis.setAxisLineVisible(xMarkers);
		domainAxis.setTickLabelsVisible(xMarkers);
		
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(227,62,62));
		renderer.setDrawBarOutline(false);
		renderer.setDefaultBarPainter(new StandardBarPainter());
		
		Font font3 = new Font("Dialog", Font.PLAIN, 75); 
		plot.getDomainAxis().setLabelFont(font3);
		plot.getRangeAxis().setLabelFont(font3);
		
        
        ChartPanel cp = new ChartPanel(barChart);
        add(cp);
	}
	
	

}
