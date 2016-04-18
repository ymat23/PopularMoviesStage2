package popularmovies.app.com.portfolio.android.popularmovies.beans;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 17-04-2016.
 */
public class TrailerResponse {

    @SerializedName("results")
    private List<Trailer> trailers = new ArrayList<>();

    public List<Trailer> getTrailers() {
        return trailers;
    }
}
