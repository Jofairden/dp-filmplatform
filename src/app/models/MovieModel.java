package app.models;

public class MovieModel {
	
	/*
		Het volgende model definieert een film
		In de opdracht is niet aangegeven dan films aanpasbaar moeten zijn,
		daarom zijn gegevens van films final gemaakt (hierdoor zijn ze niet aan te passen)
		Door de properties final aan te merken zijn ze ophaalbaar
		voor andere stukken code zonder getters te schrijven, dit is handig
		Een film heeft verder geen gedrag van zichzelf
	 */
	public final String Title;
	public final String ReleaseYear;
	public final String Country;
	public final String Budget;
	
	public MovieModel(String title, String releaseYear, String country, String budget) {
		Title = title;
		ReleaseYear = releaseYear;
		Country = country;
		Budget = budget;
	}
	
	/*
		De toString functie heeft een override om zo gemakkelijk de titel
		te tonen wanneer deze wordt aangeroepen, i.p.v. MovieModel@hash
	 */
	@Override
	public String toString() {
		return Title;
	}
}
