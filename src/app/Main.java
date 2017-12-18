package app;

import app.controllers.HomeController;
import app.controllers.MainController;
import app.controllers.MovieController;
import app.controllers.StatisticsController;
import app.models.MoviesListModel;
import app.services.ControllerService;
import app.services.ViewService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
	© Daniël Zondervan (2017) - INF/DP Eindopdr.
 */

/*
	Dit is de entrypoint van de applicatie
	We maken hier een stuk code aan, om bij het starten van de applicatie de views en bijbehorende controllers in te laden
	De fxml viewes moeten éénmaal ingeladen worden, want de bijbehorende controller wordt op dat moment ook ingeladen en geinitialiseerd
	Ook staat hieronder de code die de overkoepelende controller aanmaakt en vervolgens aanroept, en daarin de ViewServices en ControllerServices meegeeft
	Dit zijn twee aparte abstracte classes die ik heb aangemaakt om bepaalde aanroepen te vereenvoudigen, en bevatten eveneens de bijbehorende properties met getters en/of setters.
	Zo heeft op deze manier een ViewService altijd beschikking tot de bijbehorende controller, en de ControllerService op zijn beurt weer beschikking over het model en de view.
	
	Het thuisscherm (basisscherm) is hernoemd naar 'Home' om verwarring met Main en MainController te voorkomen
	De klassen xView zijn de bijbehorende fxml controllers voor de fxml views
	De klassen xController zijn de daadwerkelijke controllers voor deze onderdelen
 */
public class Main extends Application {
	
	private MoviesListModel model;
	private MainController controller;
	
	// We slaan de fxml views tijdelijk op om deze aan de enum te koppelen
	private static Parent homeView;
	private static Parent statisticsView;
	private static Parent movieView;
	
	/*
		Hieronder is een enum gemaakt om verschillende stages op een gemakkelijke manier te kunnen openen
		Elke 'ViewStage' is gekoppeld aan de bijbehorende fxml view, en kan worden aangeroepen met .open()
		e.g. Main.ViewStage.HOME.open() zal het hoofdscherm openen
		Aanroep hiervan is te zien in HomeView
		
		De enum houdt ook bij of een stage al actief is
		Bij het opnieuw activeren van een stage zal deze naar voren worden gebracht
		(i.p.v. een nieuwe te openen)
		De actieve stage wordt gereset bij het sluiten hiervan
		
		Deze enum is modulair opgebouwd om het zo makkelijk mogelijk
		te maken om niewe stages toe te voegen aan de applicatie
	 */
	public enum ViewStage {
		HOME(homeView), STATISTICS(statisticsView), MOVIE(movieView);
		
		private Parent view;
		private Stage _activeStage;
		private boolean valid;
		
		// Een ViewStage is standaard invalid,
		// alleen met een gekoppelde view is deze valid
		ViewStage() {
			valid = false;
		}
		
		ViewStage(Parent view) {
			this.view = view;
			valid = true;
		}
		
		public void open() throws IOException {
			if (valid) {
				if (_activeStage != null) {
					_activeStage.toFront();
				} else {
					_activeStage = new Stage();
					_activeStage.setTitle("Filmplatform");
					_activeStage.setScene(view.getScene());
					_activeStage.setOnCloseRequest(event -> _activeStage = null);
					_activeStage.show();
				}
			}
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// We maken het model aan
		model = new MoviesListModel();
		
		// De fxml views en controllers worden ingeladen
		// De scene voor de fxml views worden gelijk aangemaakt en gekoppeld d.m.v. dependency injection
		FXMLLoader fxmlLoader = new FXMLLoader();
		homeView = fxmlLoader.load(getClass().getResource("/app/resources/fxml/home.fxml").openStream());
		ViewService homeViewService = (ViewService) fxmlLoader.getController();
		homeViewService.setScene(new Scene(homeView, 800, 500));
		
		fxmlLoader = new FXMLLoader();
		statisticsView = fxmlLoader.load(getClass().getResource("/app/resources/fxml/statistics.fxml").openStream());
		ViewService statisticsViewService = (ViewService) fxmlLoader.getController();
		statisticsViewService.setScene(new Scene(statisticsView, 800, 500));
		
		fxmlLoader = new FXMLLoader();
		movieView = fxmlLoader.load(getClass().getResource("/app/resources/fxml/movie.fxml").openStream());
		ViewService movieViewService = (ViewService) fxmlLoader.getController();
		movieViewService.setScene(new Scene(movieView, 400, 300));
		
		// We maken de overkoepelende controller aan
		// Deze heeft toegang tot onze View- en Controller- Services en stelt deze goed in, zie de MainController klasse
		controller = new MainController(
				model,
				new ViewService[] {homeViewService, statisticsViewService, movieViewService},
				new ControllerService[] {new HomeController(), new StatisticsController(), new MovieController()});
		
		// Laat het hoofdscherm zien
		// Deze stage stellen we gelijk on onze eigen HOME stage
		// Bij het sluiten van de HOME stage zal de hele applicatie sluiten
		ViewStage.HOME.open();
		primaryStage = ViewStage.HOME._activeStage;
		primaryStage.setOnCloseRequest(event -> Platform.exit());
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
