package app.controllers;

import app.models.MovieModel;
import app.services.ControllerService;

import java.util.Observable;

/*
	De Controller voor het aanmaken van een film
	Deze bevat elke functies ter communicatie met het model en de view, evenals validatie van de input vanuit de view
 */
public class MovieController extends ControllerService {
	
	public boolean requestMovieCreation(MovieModel movieModel) {
		return model.addMovie(movieModel);
	}
	
	public boolean isTitleValid(String title) {
		return title.length() >= 4 && title.length() <= 64;
	}
	
	public boolean isYearValid(String year) {
		return onlyNumerics(year) && year.length() == 4;
	}
	
	public boolean isCountryValid(String country) {
		return country.length() >= 4 && country.length() <= 64;
	}
	
	public boolean isBudgetValid(String budget) {
		return onlyNumerics(budget);
	}
	
	private boolean onlyNumerics(String input) {
		return input.matches("[0-9]+");
	}
	
	@Override
	public void update(Observable o, Object arg) {
	
	}
}
