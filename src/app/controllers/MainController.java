package app.controllers;

import app.factories.MovieFactory;
import app.models.MoviesListModel;
import app.services.ControllerService;
import app.services.ViewService;

/*
    Dit is de overkoepelende controller van het systeem
    De controller kent het model en de views van de applicate
    Views kennen alleen hun controller, maar niet het model
    De views zullen hun bijbehorende controller moeten gebruiken als het model nodig is
    
    Het model is een Observable, de controllers zijn Observers en worden als observer toegevoegd aan het model
    Het model zal bij veranderingen de observers hierover informeren, waarna relevante data zo weer doorgestuurd kan worden naar de view via de controller
 */
public class MainController {
	
	private MoviesListModel model;
	
	// De views (Services)
	private ViewService homeView;
	private ViewService statisticsView;
	private ViewService movieView;
	
	// De controllers (Services)
	private ControllerService homeController;
	private ControllerService statisticsController;
	private ControllerService movieController;
	
	public MainController(MoviesListModel model, ViewService[] views, ControllerService[] controllers) {
		this.model = model;
		
		// Eerst koppelen we de gegeven services aan onze variables
		this.homeView = views[0];
		this.statisticsView = views[1];
		this.movieView = views[2];
		
		this.homeController = controllers[0];
		this.statisticsController = controllers[1];
		this.movieController = controllers[2];
		
		// Nu koppelen we de controllers aan het bijbehorende model
		this.homeController.setModel(model);
		this.statisticsController.setModel(model);
		this.movieController.setModel(model);
		
		// Nu koppelen we de controllers aan de bijbehorende views, en de views aan de bijbehorende controllers
		this.homeController.setView(this.homeView);
		this.statisticsController.setView(this.statisticsView);
		this.movieController.setView(this.movieView);
		
		this.homeView.setController(this.homeController);
		this.statisticsView.setController(this.statisticsController);
		this.movieView.setController(this.movieController);
		
		// Nu voegen we de controllers toe als observers aan het model
		this.model.addObserver(this.homeController);
		this.model.addObserver(this.statisticsController);
		this.model.addObserver(this.movieController);
		
		// Maak een x aantal neppe films for testing purposes
		this.model.addMovies(MovieFactory.getMovies(25));
	}
}
