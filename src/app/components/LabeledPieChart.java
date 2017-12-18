package app.components;

import javafx.collections.ListChangeListener;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

// De volgende uitbreiding van de klasse PieChart is gemaakt om labels op de taartuitsparingen weer te geven
public class LabeledPieChart extends PieChart {
	
	private final Map<Data,Text> _labels = new HashMap<>();
	
	public LabeledPieChart() {
		super();
		this.getData().addListener((ListChangeListener.Change<? extends Data> c) -> {
			addLabels();
		});
		
	}
	
	@Override
	protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
		super.layoutChartChildren(top, left, contentWidth, contentHeight);
		
		double centerX = contentWidth / 2 + left;
		double centerY = contentHeight / 2 + top;
		
		layoutLabels(centerX, centerY);
	}
	
	// Het volgende voegt een label toe aan de kaart, die het aantal keren en % op het cirkeldiagram weergeeft
	private void addLabels() {
		
		for (Text label : _labels.values()) {
			this.getChartChildren().remove(label);
		}
		_labels.clear();
		for (Data vData : getData()) {
			final Text dataText;
			final short yValue = (short) vData.getPieValue();
			dataText = new Text(Short.toString(yValue) + String.format(" (%s%%)", yValue / getData().stream().mapToDouble(Data::getPieValue).sum() * 100.0));
			dataText.setFill(Color.WHITE);
			dataText.setFont(Font.font("Arial", 10));
			final Tooltip tTooltip = new Tooltip();
			String word1 = yValue > 1 ? "zijn" : "is";
			String word2 = yValue > 1 ? "films" : "film";
			tTooltip.setText(String.format("Er %s %s %s gemaakt in het land %s", word1, Short.toString(yValue), word2, vData.getName()));
			Tooltip.install(vData.getNode(), tTooltip);
			_labels.put(vData, dataText);
			this.getChartChildren().add(dataText);
		}
	}
	
	// De volgende code berekent waar de labels moeten worden geplaatst
	// Er is een wijziging geïmplementeerd om posities dienovereenkomstig aan te passen na het toevoegen van percentagewaarden
	private void layoutLabels(double centerX, double centerY) {
		
		double total = 0.0;
		for (Data d : this.getData()) {
			total += d.getPieValue();
		}
		double scale = (total != 0) ? 360 / total : 0;
		
		for (Map.Entry<Data,Text> entry : _labels.entrySet()) {
			Data vData = entry.getKey();
			Text vText = entry.getValue();
			
			Region vNode = (Region) vData.getNode();
			Arc arc = (Arc) vNode.getShape();
			
			double start = arc.getStartAngle();
			
			double size = (isClockwise()) ? (-scale * Math.abs(vData.getPieValue())) : (scale * Math.abs(vData.getPieValue()));
			final double angle = normalizeAngle(start + (size / 2));
			final double sproutX = calcX(angle, arc.getRadiusX() / 2, centerX);
			final double sproutY = calcY(angle, arc.getRadiusY() / 2, centerY);
			
			vText.relocate(
					sproutX - vText.getBoundsInLocal().getWidth() / 2,
					sproutY - vText.getBoundsInLocal().getHeight() / 2);
		}
		
	}
	
	private static double normalizeAngle(double angle) {
		double a = angle % 360;
		if (a <= -180) {
			a += 360;
		}
		if (a > 180) {
			a -= 360;
		}
		return a;
	}
	
	private static double calcX(double angle, double radius, double centerX) {
		return (double) (centerX + radius * Math.cos(Math.toRadians(-angle)));
	}
	
	private static double calcY(double angle, double radius, double centerY) {
		return (double) (centerY + radius * Math.sin(Math.toRadians(-angle)));
	}
	
}

