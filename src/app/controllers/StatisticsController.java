package app.controllers;

import app.services.ControllerService;

import java.util.Observable;

/*
	Dit is de controller van het statistieken scherm
	Deze zal een update naar de view doorsturen als het model
	veranderingen ondergaat
 */
public class StatisticsController extends ControllerService {
	
	@Override
	public void update(Observable o, Object arg) {
		((StatisticsView) view).updateData(model.getFilteredCountries());
	}
}
