package app.controllers;

import app.models.MovieModel;
import app.services.ViewService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/*
	De movie view is de weergave voor het toevoegen van een film
	Deze view bestaat uit een aantal invulbare velden en een knop voor het toevoegen
	De view zal ingevulde gegevens doorsturen naar de controller ter validatie
	Mits de validate success acht, zal de view een creation request doorsturen naar de controller,
	om zo de nieuwe film aan te maken.
 */
public class MovieView extends ViewService {
	
	// De volgende velden zijn afkomstig uit de fxml view
	// en worden gevuld d.m.v. injection vanuit javaFX
	@FXML private TextField titleTextField;
	@FXML private TextField releaseYearTextField;
	@FXML private TextField countryTextField;
	@FXML private TextField budgetTextField;
	@FXML private Button addMovieButton;
	
	@FXML
	public void initialize() {
		
		/*
			We voegen een event handler toe aan onze toevoegen knop
			Deze handler zal de ingevoerde data proberen te controleren
			Bij foute invoer zal dit invulveld rood gloeien
			Bij een al bestaande film zal hierover een popup tonen,
		 */
		addMovieButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			
			/*
				Valideer alle velden
				b staat voor de totale validatie, en moet true zijn als alle validatie success acht
			 */
			boolean b;
			boolean validTitle = ((MovieController) controller).isTitleValid(titleTextField.getText());
			boolean validYear = ((MovieController) controller).isYearValid(releaseYearTextField.getText());
			boolean validCountry = ((MovieController) controller).isCountryValid(countryTextField.getText());
			boolean validBudget = ((MovieController) controller).isBudgetValid(budgetTextField.getText());
			
			// Is alles valide geacht?
			b = validTitle && validYear && validCountry && validBudget;
			
			// Clear de styles altijd
			clearTextFieldStyle(titleTextField);
			clearTextFieldStyle(releaseYearTextField);
			clearTextFieldStyle(countryTextField);
			clearTextFieldStyle(budgetTextField);
			
			// Bij validation errors zal een veld namelijk altijd error markup ontvangen
			if (!validTitle) {
				errTextField(titleTextField);
			}
			if (!validYear) {
				errTextField(releaseYearTextField);
			}
			if (!validCountry) {
				errTextField(countryTextField);
			}
			if (!validBudget) {
				errTextField(budgetTextField);
			}
			
			// Validation success
			if (b) {
				// Stuur een creation request naar de controller
				boolean succession = ((MovieController) controller).requestMovieCreation(
						new MovieModel(
								titleTextField.getText(),
								releaseYearTextField.getText(),
								countryTextField.getText(),
								budgetTextField.getText()
						)
				);
				
				// Na succession zullen de velden gereset worden
				// Bij een al bestaande titel zal een popup tonen
				if (succession)
					clearControls();
				else {
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Toevoegen mislukt");
					alert.setHeaderText("Het aanmaken van de film " + titleTextField.getText() + "  is mislukt");
					alert.setContentText("De kans is groot dat deze film al bestaat, probeer een andere titel!");
					
					alert.showAndWait();
				}
			}
		});
	}
	
	/*
		Stijl een veld bij invalide input met een rode gloed
	 */
	private void errTextField(TextField field) {
		field.setStyle("-fx-text-box-border: red; -fx-focus-color: red ;");
	}
	
	/*
		Zal het gegeven veld zijn stijl resetten
	 */
	private void clearTextFieldStyle(TextField field) {
		field.setStyle("");
	}
	
	/*
		Zall alle velden legen en stijlen resetten
	 */
	private void clearControls() {
		titleTextField.setText("");
		releaseYearTextField.setText("");
		countryTextField.setText("");
		budgetTextField.setText("");
		clearTextFieldStyle(titleTextField);
		clearTextFieldStyle(releaseYearTextField);
		clearTextFieldStyle(countryTextField);
		clearTextFieldStyle(budgetTextField);
	}
}
