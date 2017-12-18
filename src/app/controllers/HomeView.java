package app.controllers;

import app.Main;
import app.models.MovieModel;
import app.services.ViewService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Optional;

public class HomeView extends ViewService {
	
	// De volgende velden zijn afkomstig uit de fxml view
	// en worden gevuld d.m.v. injection vanuit javaFX
	@FXML private ListView<MovieModel> moviesListView;
	@FXML private TitledPane infoPane;
	@FXML private TextField searchTextField;
	@FXML private TextField titleTextField;
	@FXML private TextField releaseYearTextField;
	@FXML private TextField countryTextField;
	@FXML private TextField budgetTextField;
	@FXML private Button deleteMovieButton;
	@FXML private Button statisticsButton;
	@FXML private Button addMovieButton;
	
	/*
		De volgende helper functie geeft de geselecteerde MovieModel in de ListView terug
    */
	private MovieModel getSelectedMovie() {
		return moviesListView.getSelectionModel().getSelectedItem();
	}
	
	/*
		De volgende helper functie geeft de indice van de MovieModele in de ListView terug
	*/
	private int getSelectedIndex() {
		return moviesListView.getSelectionModel().getSelectedIndex();
	}
	
	/*
		De volgende helper functie geeft de daadwerkelijke indice in de movies lijst terug
	 */
	private int getSelectedRealIndex() {
		MovieModel movie = getSelectedMovie();
		
		try {
			return ((HomeController) controller).findMovieIndiceByTitle(movie.Title);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	/*
		De volgende helper functie geeft de grootte van de lijst van films terug (aantal films)
	 */
	private int getListSize() {
		return moviesListView.getItems().size();
	}
	
	/*
		De initialize methode is alleen aangeroepen nadat alle gemarkeerde FXML properties zijn geïnjecteerd
	 */
	@FXML
	public void initialize() {
		
		
		// Met de volgende code voegen we een listener toe aan het selected item property
		// Hiermee kunnen we de getoonde informatie aanpassen wanneer de gebruiker een movie uit de lijst selecteerd
		// Dit is tevens een voorbeeld van een lambda expression
		// Normaal gesproken zou je een ChangeListener<MovieModel> toevoegen, en daarbij de changed methode overriden
		// Met de lambda expresion hoeft dat allemaal niet, en is beschikbaar in Java 8
		moviesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			// Een null check is vereist om bepaalde errors tegen te gaan
			// Na verwijdering kan het namelijk zo zijn dat de gefilterde lijst leeg is
			// Nog vóórdat de filter dan reset, zal een nieuwe movie geselecteerd worden, dit resulteert dan in een NRE
			if (newValue != null) {
				setFields(newValue);
			} else {
				resetFields();
			}
		});
		
		// Zet de fields standaard op lege waardes
		resetFields();
		
		// Voeg een event handler toe voor de zoekbalk
		// De view zal de gegeven predicate doorsturen naar de controller, en de controller zal deze behandelen
		searchTextField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			String searchPredicate = searchTextField.getText();
			((HomeController) controller).updateSearchPredicate(searchPredicate);
		});
		
		/*
			Voeg een event handler toe voor de verwijderknop
			Deze zal reageren op het MOUSE_CLICKED event
			Er zal een alert verschijnen ter confirmatie van het verwijderen
			Als de gebruiker op OK klikt, zal een deletion request naar de controller worden gestuurd
			De controller zal teruggeven of het verwijderen is gelukt,
			waarna een 'smart index selection' volgt
			Dit stuk code zorgt ervoor dat het juiste volgende element wordt geselecteerd
			of dat de zoekspreuk wordt hersteld
		 */
		deleteMovieButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			MovieModel movie = getSelectedMovie();
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle(movie.Title);
			alert.setHeaderText(String.format("Je staat op het punt de film %s te verwijderen", movie.Title));
			alert.setContentText("Weet je zeker dat je deze film wilt verwijderen?");
			
			Optional<ButtonType> result = alert.showAndWait();
			
			// Gebruiker is OK, vervolg
			if (result.isPresent() && result.get() == ButtonType.OK) {
				
				/*
					de softindex is de huidige index in de weergegeven lijst
					dit is belangrijk aangezien de weergegeven lijst de gefilterde lijst betreft,
					en dus niet de lijst met alle films
					
					de index is de daadwerkelijke index is de lijst van alle films
					deze zal gebruikt worden voor het verzoek van verwijdering
					de softindex zal juist gebruikt worden voor het stukje 'smart index selection'
				 */
				
				int softIndex = getSelectedIndex();
				int index = getSelectedRealIndex();
				if (index != -1) {
					if (((HomeController) controller).requestDeletion(index)) {
						
						// na deletion, 'smart index selection'
						int size = getListSize();
						
						if (size > 0) {
							if (softIndex <= size)
								moviesListView.getSelectionModel().select(softIndex);
							else
								moviesListView.getSelectionModel().selectLast();
						} else {
							searchTextField.setText("");
							String searchPredicate = searchTextField.getText();
							((HomeController) controller).updateSearchPredicate(searchPredicate);
						}
					}
				}
			}
		});
		
		
		/*
			De volgende knoppen krijgen een nieuwe eventhandler toegekend
			Deze handlers roepen de enum in de Main klasse aan
			om zo de bijbehorende stage te openen
		 */
		statisticsButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			try {
				Main.ViewStage.STATISTICS.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		
		addMovieButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			try {
				Main.ViewStage.MOVIE.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	/*
		Zal de info velden (controls) instellen op het gegeven model
	 */
	private void setFields(MovieModel model) {
		infoPane.setText(model.toString());
		titleTextField.setText(model.Title);
		releaseYearTextField.setText(model.ReleaseYear);
		countryTextField.setText(model.Country);
		budgetTextField.setText(model.Budget);
		deleteMovieButton.setDisable(false);
	}
	
	/*
		Zal de info velden (controls) resetten en de verwijderknop de-activeren
	 */
	private void resetFields() {
		infoPane.setText("Geen film geselecteerd");
		titleTextField.setText("");
		releaseYearTextField.setText("");
		countryTextField.setText("");
		budgetTextField.setText("");
		deleteMovieButton.setDisable(true);
	}
	
	/*
		De controller zal deze functie aanroepen na een update van de observable
		De doorgestuurde info zal worden gekoppeld aan de lijst van films in deze view
	 */
	public void updateMovies(ObservableList<MovieModel> list) {
		moviesListView.getItems().setAll(list);
	}
	
	@FXML
	public void exitApplication(ActionEvent event) {
		Platform.exit();
	}
	
}
