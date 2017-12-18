package app.controllers;

import app.services.ViewService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import java.util.*;

/*
	De statistics view bevat een PieChart van javaFX
	Deze chart zal met pie cutouts weergeven hoeveel films uit welke landen komen
	d.m.v. het observer pattern zal deze chart geüpdatet worden als de lijst van films
	een update ondervindt.
	
	De chart zal zichzelf alleen updaten wanneer nieuw gestuurde countries verschillen
	van de cached countries
	
	Dit zal voorkomen dat de chart zal animeren na het toevoegen van een film
	terwijl deze nieuwe toegevoegde film niet toont door de vastgestelde zoekspreuk
	Het is uiteraard per definitie al overbodig om de updaten wanneer de nieuwe data gelijk staat aan de al beschikbare data
 */
public class StatisticsView extends ViewService {
	
	private List<String> storedCountries;
	@FXML private PieChart chart;
	
	@FXML
	public void initialize() {
	
	}
	
	/*
		De volgende functie zal het aantal countries terruggeven,
		of -1 als er geen countries gecached zijn
	 */
	private int getCountriesCount() {
		if (storedCountries == null)
			return -1;
		else return storedCountries.size();
	}
	
	/*
		De volgende functie zal terruggeven of de nieuwe lijst
		van countries hetzelfde is als de gecachde lijst
	 */
	private boolean areAllCountriesTheSame(List<String> countries) {
		if (getCountriesCount() == -1)
			return false;
		
		List<String> tempList = new ArrayList<>(storedCountries);
		tempList.retainAll(countries);
		return countries.size() == storedCountries.size();
	}
	
	/*
		De volgende functie word aangeroepen door de controller
		en zal de data van de piechart updaten mits deze nieuwe data
		anders is dan de gecachde data, de pie data zal tevens
		gesorteerd worden op alfabet van a-z nog vóór het koppelen
		daarvan aan de pie chart
	 */
	public void updateData(List<String> countries) {
		
		if (!areAllCountriesTheSame(countries)) {
			
			storedCountries = new ArrayList<>(countries);
			
			HashMap<String,Integer> map = new HashMap<>();
			
			for (String obj : countries) {
				int count = 1;
				
				if (map.containsKey(obj)) {
					count = map.get(obj);
					++count;
				}
				
				map.put(obj, count);
			}
			
			ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
			
			for (Map.Entry<String,Integer> movieIntegerEntry : map.entrySet()) {
				Map.Entry entry = (Map.Entry) movieIntegerEntry;
				data.add(new PieChart.Data(entry.getKey().toString(), (Integer) entry.getValue()));
			}
			
			// Het volgende zal de piechart data sorteren op naam
			data.sort(Comparator.comparing(PieChart.Data::getName));
			chart.getData().setAll(data);
		}
	}
	
}
