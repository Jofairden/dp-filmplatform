package app.factories;

import app.models.MovieModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/*
    Hier tonen we het factory pattern
    Deze factory kan één of meerdere neppe films aanmaken en terugsturen aan de caller
    Dit wordt gebruik op neppe film info te genereren for testing purposes
 */
public class MovieFactory {
	
	/*
		Zal één of meerdere films genereren
	 */
	public static Collection<MovieModel> getMovies(int end) {
		List<MovieModel> list = new ArrayList<>();
		for (int i = 0; i < end; i++) {
			list.add(getMovie());
		}
		return list;
	}
	
	/*
		Zal één film genereren
	 */
	public static MovieModel getMovie() {
		String[] words = generateRandomWords(2);
		return new MovieModel(
				words[0],
				Integer.toString(randBetween(1900, 2018)),
				words[1],
				Integer.toString(randBetween(10000000, 200000000))
		);
	}
	
	/*
		Zal één of meerdere woorden genereren
	 */
	private static String[] generateRandomWords(int numberOfWords) {
		String[] randomStrings = new String[numberOfWords];
		Random random = new Random();
		for (int i = 0; i < numberOfWords; i++) {
			char[] word = new char[random.nextInt(8) + 3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
			for (int j = 0; j < word.length; j++) {
				word[j] = (char) ('a' + random.nextInt(26));
			}
			randomStrings[i] = new String(word);
		}
		return randomStrings;
	}
	
	/*
		Zal een willekeurige int waarde genereren
	 */
	private static int randBetween(int start, int end) {
		return start + (int) Math.round(Math.random() * (end - start));
	}
}
