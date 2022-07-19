package movieReviewClassification;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class ReviewHandler extends AbstractReviewHandler {

        private static int ID;
        /**
         * Loads reviews from a given path. If the given path is a .txt file, then
         * a single review is loaded. Otherwise, if the path is a folder, all reviews
         * in it are loaded.
         * @param filePath The path to the file (or folder) containing the review(sentimentModel).
         * @param realClass The real class of the review (0 = Negative, 1 = Positive
         * 2 = Unknown).
         * @return A list of reviews as objects.
         */

        // inherits the functions from abstractReviewHandler
        @Override
        public void loadReviews(String filePath, int realClass) {

                File f = new File(filePath); //creates a new file instance
                MovieReview movieRev;

                try {
                        if (f.isFile()){ //adds just a single file to  the database

                                movieRev = readReview(filePath, realClass); // calls the readReview method
                                database.put(movieRev.getId(), movieRev);  // puts the review in the database
                                         // outputs the results of the file
                                SentimentAnalysisApp.textArea.append("\nReview imported.\n");
                                SentimentAnalysisApp.textArea.append("ID: " + movieRev.getId());
                                SentimentAnalysisApp.textArea.append("\nText: "
                                        + movieRev.getText().substring(0, 50)+"...");
                                SentimentAnalysisApp.textArea.append("\n\nReal Class: " + movieRev.getRealPolarity());
                                SentimentAnalysisApp.textArea.append("\nClassification result: "
                                        + movieRev.getPredictedPolarity());

                        } else if (f.isDirectory()){ //access folder

                                File[] revDirectory = f.listFiles();
                                int counter = 0; // a counter for the number of classified reviews
                                for (File files : revDirectory) { // scans in all of the files

                                        movieRev = readReview(files.getPath(), realClass);
                                        database.put(movieRev.getId(), movieRev); // puts the reviews in database
                                        if (realClass != 2 && movieRev.getRealPolarity() ==
                                                movieRev.getPredictedPolarity()) {
                                                counter++;
                                        } // itterates when a review is correctly classified
                                }

                                        // outputs if the real class is known
                                if (realClass != 2) {
                                        // Output result: folder
                                        SentimentAnalysisApp.textArea.append("\nFolder imported.");
                                        SentimentAnalysisApp.textArea.append("\nNumber of entries: "
                                                + revDirectory.length);
                                        double accuracy = ((double) counter / (double) revDirectory.length * 100);
                                        SentimentAnalysisApp.textArea.append("\nCorrectly classified: " + counter);
                                        SentimentAnalysisApp.textArea.append("\nMisclassified: "
                                                + (revDirectory.length - counter));
                                        SentimentAnalysisApp.textArea.append("\nAccuracy: "
                                                + String.format("%.1f", accuracy) + "%");;
                                }
                        }
                }catch(Exception e)
                {
                        System.err.println(e.toString());
                        e.printStackTrace();
                }

        }

        /**
         * Reads a single review file and returns it as a MovieReview object.
         * This method also calls the method classifyReview to predict the polarity
         * of the review.
         * @param reviewFilePath A path to a .txt file containing a review.
         * @param realClass The real class entered by the user.
         * @return a MovieReview object.
         * @throws IOException if specified file cannot be openned.
         */

        @Override
        public MovieReview readReview(String reviewFilePath, int realClass) throws IOException {
                // reads the file
                Scanner inFile = new Scanner(new FileReader(reviewFilePath));
                String text = "";
                while (inFile.hasNextLine()) {
                        text += inFile.nextLine();
                }
                // Remove the <br /> occurences in the text and replace them with a space
                text = text.replaceAll("<br />"," ");

                // Create review object, assigning ID and real class
                MovieReview review = new MovieReview(ID, text, realClass);
                // Update ID
                ID++;
                // Classify review
                classifyReview(review);

                return review;

        }

        /**
         * Loads review database.
         */
        @Override
        public void loadSerialDB() throws IOException, ClassNotFoundException {
                SentimentAnalysisApp.textArea.append("\nReading database...");
                // serialize the database
                InputStream file;
                InputStream buffer;
                ObjectInput input;

                        file = new FileInputStream(DATA_FILE_NAME);
                        buffer = new BufferedInputStream(file);
                        input = new ObjectInputStream(buffer);

                        database = (Map<Integer, MovieReview>)input.readObject();

                        // finds the max ID created
                        for (Map.Entry<Integer, MovieReview> entry : database.entrySet()){
                                if (entry.getKey() > ID) {
                                        ID = entry.getKey();
                                }
                        }
                        // adds 1 to the max ID
                        ID = ID + 1;

                        input.close();

                }

        /**
         * Deletes a review from the database, given its id.
         * @param id The id value of the review.
         */

        @Override
        public void deleteReview(int id) {
                if (!database.containsKey(id)) { // prints the ID doesnt exist
                        SentimentAnalysisApp.textArea.append("\nID " + id + " does not exist.\n");
                } else {
                        database.remove(id); // deletes the iD
                        // tells the user id is deleted
                        SentimentAnalysisApp.textArea.append("\nReview " + id + " is deleted.\n");
                }
        }

        /**
         * A map of <id, review> pairs.
         */
        @Override
        public MovieReview searchById(int id) {
                if (database.containsKey(id)) { // searches the database for that id
                        return database.get(id); // returns the information of the ID entered
                }
                return null; // returns if no review is found
        }

        /**
         * Searches the review database for reviews matching a given substring.
         * @param substring The substring to search for.
         * @return A list of review objects matching the search criterion.
         */
        @Override
        public List<MovieReview> searchBySubstring(String substring) {
                List<MovieReview> temp = new ArrayList<MovieReview>(); //creates a temp list
                for (MovieReview review : database.values()) { // finds everything with the substring in the hashmap
                        if (review.getText().indexOf(substring)>= 0) { // if it finds something it adds to the temp
                                temp.add(review);
                        }
                }
                if (!temp.isEmpty()) {
                        return temp;
                } else { // no review has given substring
                        return null;
                }
        }
}
