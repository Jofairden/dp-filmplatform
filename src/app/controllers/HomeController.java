package app.controllers;

import app.services.ControllerService;

import java.util.Observable;

/*
    Dit is de controller voor de home view
    Deze bevat elke functies ter communicatie met het model en de view
 */
public class HomeController extends ControllerService {
	
	/*
		Hier verkrijgen we een update call van het model
		waarna de vernieuwde lijst van movies doorgestuurd zal worden naar de view,
		hier kan de view op reageren
	 */
	@Override
	public void update(Observable o, Object arg) {
		((HomeView) view).updateMovies(model.getFilteredMovies());
	}
	
	/*
		Hier stellen we een nieuwe zoekspreuk in
		afhankelijk van de meegegeven string door de view
		Bij een lege string zal het zoeken gereset worden
		bij een gevulde string zal deze door een cleaner gehaald worden
		en vervolgens worden vergeleken met de titel van een film
	 */
	public void updateSearchPredicate(String searchPredicate) {
		if (searchPredicate == null || searchPredicate.isEmpty())
			model.resetSearchPredicate();
		else
			model.setSearchPredicate(movieModel -> applySearchCleanup(movieModel.Title).contains(applySearchCleanup(searchPredicate)));
	}
	
	/*
		Hier zoeken we de indice in de lijst van films op titel van de film
		Een niet gevonden film zal een Exception gooien, die kan gevangen worden waarna een -1 indice teruggegeven kan worden
		door de bijbehorende functie van de view
	 */
	public int findMovieIndiceByTitle(String title) throws Exception {
		return model.findMovieIndiceByTitle(movieModel -> applySearchCleanup(movieModel.Title).equals(applySearchCleanup(title)));
	}
	
	/*
		Deze functie roept deletion aan op een bepaalde indice,
		gebruikt wanneer een gebruiker de geselecteerde film wil verwijderen
	 */
	public boolean requestDeletion(int index) {
		return model.removeAt(index);
	}
	
	/*
		De volgende functie zal de gegeven predicate 'opschonen'
	 */
	private String applySearchCleanup(String predicate) {
		return predicate.toLowerCase().replaceAll("\\s+", "");
	}
}
