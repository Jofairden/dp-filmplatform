package app.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.text.Collator;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/*
	Het volgende is het veelgebruikte MoviesListModel binnen deze applicatie
	Dit model omvat een lijst van films, en ook een gefilterde lijst van films
	Deze gefilterde lijst is gekoppeld aan de lijst van films,
	veranderingen binnen deze lijst zullen direct doorgestuurd worden naar de gefilterde lijst
	
	Dit model bevat enige functies m.b.t. de beschikbare data in de lijst van films
	Ten allen tijde is het de bedoeling dat alleen de lijst van films wordt aangepast,
	want de gefilterde lijst zal deze aanpassingen automatisch doorgestuurd krijgen
	
	Let op dat er veel functies zijn vrijgegeven die niet gebruikt worden,
	dit komt doordat de code meerdere keren is herschreven waardoor bepaalde
	functionaliteiten niet meer worden gebruikt. Toch is deze code blijven staan
	voor de volledigheid
	
	Het is de bedoeling dat alle data vanuit dit model in en uit via dit model gaat,
	en niet via een andere bron. Zo zijn er b.v. functies om een film toe te voegen
	of te verwijderen
	
	De updateChanged() methode moet worden aangeroepen bij sucessvolle veranderingen
	binnen het model. Dit zal de observers de veranderingen doorgeven.
	
	Er zijn ook een aantal property functies aangegeven die ReadOnly properties terugkeren
	van de beschikbare data. Zo is het b.v. mogelijk om een ListView zijn itemsProperty te
	'binden' aan de filteredMoviesProperty. Op deze manier zal onder water het observer
	pattern plaatsvinden, waarna veranderingen in de lijst van films wordt doorgegeven via
	deze property bind. Er is voor gekozen om niet gebruik te maken van deze functionaliteit
	om te laten zien dat ik begrijp hoe deze pattern werkt.
 */
public class MoviesListModel extends Observable {
	
	private ObservableList<MovieModel> movies;
	private FilteredList<MovieModel> filteredMovies;
	
	/*
		De lijst van movies is een ObservableArrayList (een lijst met gevulde observables)
		De gefilterde lijst heeft een bind naar deze lijst, dus zodra de Observables aangepast worden, zal dit direct doorgestuurd worden naar de filtered list.
	 */
	public MoviesListModel() {
		movies = FXCollections.observableArrayList();
		filteredMovies = new FilteredList<>(movies, s -> true);
	}
	
	/*
	    Er zijn veranderingen gemaakt, hier setten wij onze changed state en roepen de observers aan
	 */
	private void updateChanged() {
		setChanged();
		notifyObservers();
	}
	
	/*
		De volgende functies kunnen movies toevoegen aan of verwijderen van de lijst (zonder ze lijst zelf bloot te stellen)
		Deze functies accepteren x,y,...z args of een Collection van MovieModel
	 */
	public void addMovies(MovieModel... movieModels) {
		movies.addAll(movieModels);
		updateChanged();
	}
	
	public void addMovies(Collection<MovieModel> movieModels) {
		movies.addAll(movieModels);
		updateChanged();
	}
	
	public void removeMovies(MovieModel... movieModels) {
		movies.removeAll(movieModels);
		updateChanged();
	}
	
	public void removeMovies(Collection<MovieModel> movieModels) {
		movies.removeAll(movieModels);
		updateChanged();
	}
	
	/*
		De volgende functies kunnen één movie toevoegen of verwijderen
		Als bij het toevoegen de movie al in de lijst zit, dan zal false worden teruggegeven
		Als bij het verwijderen de movie niet in de lijst zit, dan zal false worden teruggegeven
		Bij de volgende functies zijn inverted ifs toegepast om nesting te verminderen.
	 */
	public boolean addMovie(MovieModel movie) {
		if (hasMovieByTitle(s -> s.Title.toLowerCase().equals(movie.Title.toLowerCase())))
			return false;
		
		movies.add(movie);
		updateChanged();
		return true;
	}
	
	public boolean removeMovie(MovieModel movie) {
		if (!movies.contains(movie))
			return false;
		
		movies.remove(movie);
		updateChanged();
		return true;
	}
	
	/*
		De volgende functies kunnen op een specifieke index een movie toevoegen of verwijderen
		Eveneens als de vorige zullen deze functies succession returnen
		Oook hier zijn inverted ifs toegepast om nesting te voorkomen
	 */
	public boolean addAt(int index, MovieModel model) {
		if (index < 0 || index > movies.size() || (hasMovieByTitle(s -> s.Title.toLowerCase().equals(model.Title.toLowerCase()))))
			return false;
		
		movies.add(index, model);
		updateChanged();
		return true;
	}
	
	public boolean removeAt(int index) {
		if (index < 0 || index > movies.size() || !(hasMovieByTitle(s -> s.Title.toLowerCase().equals(movies.get(index).Title.toLowerCase()))))
			return false;
		
		movies.remove(index);
		updateChanged();
		return true;
	}
	
	/*
		De volgende functies zijn een meer geavanceerde implementatie van het Proxy pattern
		We gebruiken hier niet een middleman-class (proxy) maar readonly object properties
		Deze fungeren op dezelfde manier: we kunnen data doorgeven maar toch afschermen tegen schrijfrechten, door ze als ReadOnlyObjectProperty te maken
		Dit zou dus ook anders gedaan kunnen worden, namelijk met een MoviesProxy class, maar dit werkt makkelijker
	 */
	public ReadOnlyObjectProperty<ObservableList<MovieModel>> moviesProperty() {
		return new SimpleObjectProperty<>(movies);
	}
	
	public ReadOnlyObjectProperty<ObservableList<MovieModel>> filteredMoviesProperty() {
		return new SimpleObjectProperty<>(filteredMovies);
	}
	
	public ObjectProperty<Predicate<? super MovieModel>> filterProperty() {
		return filteredMovies.predicateProperty();
	}
	
	/*
		De volgende functies geven de grootte van de movies lijst of de filtered movie lijst terug.
	 */
	public int getMoviesSize() {
		return movies.size();
	}
	
	public int getFilteredMoviesSize() {
		return filteredMovies.size();
	}
	
	/*
		De volgende functies geven niet aanpasbare observable lijsten terug van de films of gefilterde films
		Op deze manier kunnen films toch opgevraagd worden zonder dat daarmee de echte data binnen het model
		aan te passen is.
	 */
	public ObservableList<MovieModel> getMovies() {
		return FXCollections.unmodifiableObservableList(movies.sorted());
	}
	
	public ObservableList<MovieModel> getFilteredMovies() {
		return FXCollections.unmodifiableObservableList(filteredMovies.sorted());
	}
	
	/*
        Geeft een lijst van de gebruikte countries terug, of van de countries van de gefilterde films
     */
	public List<String> getCountries() {
		return movies.stream()
				.map(s -> s.Country)
				.sorted(Collator.getInstance())
				.collect(Collectors.toList());
	}
	
	public List<String> getFilteredCountries() {
		return filteredMovies.stream()
				.map(s -> s.Country)
				.sorted(Collator.getInstance())
				.collect(Collectors.toList());
	}
	
	/*
		Bij het zoeken zal de lijst van movies gefilterd worden op de gegeven search predicate
		Deze predicate wordt toegepast op de titel van de film
		Films waarvan de titel het gezegde (predicate) bevat zullen overblijven
	 */
	public void setSearchPredicate(Predicate<MovieModel> predicate) {
		filteredMovies.setPredicate(predicate);
		updateChanged();
	}
	
	/*
		De volgende functie zal het search predicate herstellen
	 */
	public void resetSearchPredicate() {
		filteredMovies.setPredicate(s -> true);
		updateChanged();
	}
	
	
	/*
		De volgende functie zal terugsturen of er een film is met de gegeven titel
		Tevens zijn hieronder bij meerdere functies lambdas en streams geïmplementeerd
		Ik hoop dat het met de comments vrij duidelijk is wat er gebeurd
	 */
	public boolean hasMovieByTitle(Predicate<MovieModel> predicate) {
		return movies.stream()
				.filter(predicate)
				.findFirst()
				.orElse(null) != null;
	}
	
	/*
		De volgende functie zal terugsturen of zich een film bevindt in de gefilterde lijst met de gegeven titel
	 */
	public boolean hasFilteredMovieByTitle(Predicate<MovieModel> predicate) {
		return movies.stream()
				.filter(predicate)
				.findFirst()
				.orElse(null) != null;
	}
	
	/*
		De volgende functie zal de film met de gegeven titel zoeken, of null terugsturen als deze film niet gevonden is
	 */
	public MovieModel findMovieByTitle(Predicate<MovieModel> predicate) {
		return movies.stream()
				.filter(predicate)
				.findFirst()
				.orElse(null);
	}
	
	/*
		De volgende functie zal de indice van film met de gegeven titel zoeken
		Bij een niet gevonden film zal een Exception worden gegooid
	 */
	public int findMovieIndiceByTitle(Predicate<MovieModel> predicate) throws Exception {
		MovieModel movie = movies.stream()
				.filter(predicate)
				.findFirst()
				.orElse(null);
		
		if (movie == null)
			throw new Exception("Movie indice by title was not found");
		
		return movies.indexOf(movie);
	}
	
}
