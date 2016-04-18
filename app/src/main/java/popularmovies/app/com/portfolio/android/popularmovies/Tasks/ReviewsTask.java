package popularmovies.app.com.portfolio.android.popularmovies.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import popularmovies.app.com.portfolio.android.popularmovies.BuildConfig;
import popularmovies.app.com.portfolio.android.popularmovies.Utils.Constants;
import popularmovies.app.com.portfolio.android.popularmovies.beans.Review;
import popularmovies.app.com.portfolio.android.popularmovies.beans.ReviewResponse;
import popularmovies.app.com.portfolio.android.popularmovies.network.MovieDBAPI;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lenovo on 18-04-2016.
 */
public class ReviewsTask extends AsyncTask<Long, Void, List<Review>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = ReviewsTask.class.getSimpleName();
    private final Listener mListener;

    /**
     * Interface definition for a callback to be invoked when reviews are loaded.
     */
    public interface Listener {
        void onReviewsFetchFinished(List<Review> reviews);
    }

    public ReviewsTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Review> doInBackground(Long... params) {
        // If there's no movie id, there's nothing to look up.
        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBAPI service = retrofit.create(MovieDBAPI.class);
        Call<ReviewResponse> call = service.findReviewsById(movieId,
                Constants.MOVIE_DB_API_KEY);
        try {
            Response<ReviewResponse> response = call.execute();
            ReviewResponse reviews = response.body();
            return reviews.getReviews();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        if (reviews != null) {
            mListener.onReviewsFetchFinished(reviews);
        } else {
            mListener.onReviewsFetchFinished(new ArrayList<Review>());
        }
    }
}

