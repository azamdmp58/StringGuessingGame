import java.util.Scanner;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.io.IOException;

public class StringGuessingGame {

    public static void main(String[] args) {
        int incorrectGuesses = 10;
        boolean iterated = false;
        String temp = "";
        Scanner reader = new Scanner(System.in);

        String[] randomWord = apiCall();

        String word = randomWord[0];

        incorrectGuesses = word.length();
        System.out.println("Welcome to the guessing game! Guess one letter "
                + "at a time. You have " + incorrectGuesses + " incorrect "
                + "guesses remaining.");
        String dashedWord = createDashedWord(word);
        System.out.println("The Secret Word Is: " + dashedWord +
                " (" + word.length() + ")");

        do {
            System.out.print("Your Guess: ");
            String guess = reader.nextLine();
            boolean correctGuess = false;

            for (int i = 0; i < word.length(); i++) {
                if (guess.equals(Character.toString(word.charAt(i)))) {
                    correctGuess = true;
                    if (!iterated)
                        temp += Character.toString(word.charAt(i));
                    else {
                        String holder = Character.toString(
                                temp.charAt(i)).replace("-", guess);
                        temp = temp.substring(0, i) + holder +
                                temp.substring(i + 1, temp.length());
                    }
                } else {
                    if (!iterated) {
                        temp += "-";
                    }
                }
            }
            iterated = true;

            if (correctGuess) {
                System.out.println("The word contains the letter " + guess + ".");
            } else {
                System.out.println("The word does not contain the letter " + guess + ".");
                incorrectGuesses--;
                System.out.println("You have " + incorrectGuesses +
                        " incorrect guesses remaining.");
            }

            System.out.println(temp);
            if (temp.equals(word)) {
                System.out.println("You guessed correctly!");
                break;
            }

        } while (incorrectGuesses > 0);

        if (incorrectGuesses <= 0) {
            System.out.println("Sorry, you didn't guess the secret word \"" + word + "\".");
        }

        reader.close();

    }

    private static String createDashedWord(String word) {
        String output = "";
        for (int i = 0; i < word.length(); i++) {
            output += "-";
        }
        return output;
    }

    private static String[] apiCall() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://random-word-api.herokuapp.com/word"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = response.body();
            // Extract the array part manually (limited functionality)
            int startIndex = jsonString.indexOf("[") + 1;
            int endIndex = jsonString.lastIndexOf("]");
            String wordsString = jsonString.substring(startIndex + 1, endIndex - 1);

            // Split the string by comma (assuming comma-separated)
            String[] words = wordsString.split(",");

            // Trim leading/trailing spaces
            for (int i = 0; i < words.length; i++) {
                words[i].trim().replace("\"", "");
            }

            return words;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
