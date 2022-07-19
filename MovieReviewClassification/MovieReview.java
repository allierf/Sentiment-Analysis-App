package movieReviewClassification;

public class MovieReview implements java.io.Serializable {

    public MovieReview(int id, String text, int realPolarity){

        // class attributes
        this.id = id;
        this.text = text;
        this.realPolarity = realPolarity;
        this.predictedPolarity = 0;
    }

    //The Ids of the reviews
    private final int id;
    //The text
    private final String text;
    //predicted polarity
    private int predictedPolarity;
    //the ground truth
    private final int realPolarity;
    //gets the id
    public int getId(){
        return id;
    }
    //gets the text from the reviews
    public String getText(){ return text; }
    // gets the predicted Polarity
    public int getPredictedPolarity(){
        return predictedPolarity;
    }
    // sets predicted polarity
    public void setPredictedPolarity(int predictedPolarity){
        this.predictedPolarity = predictedPolarity;
    }
    // gets the real polarity
    public int getRealPolarity(){
        return realPolarity;
    }




}
