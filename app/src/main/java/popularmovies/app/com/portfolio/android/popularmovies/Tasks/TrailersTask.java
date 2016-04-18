package popularmovies.app.com.portfolio.android.popularmovies.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import popularmovies.app.com.portfolio.android.popularmovies.BuildConfig;
import popularmovies.app.com.portfolio.android.popularmovies.Utils.Constants;
import popularmovies.app.com.portfolio.android.popularmovies.beans.Trailer;
import popularmovies.app.com.portfolio.android.popularmovies.beans.TrailerResponse;
import popularmovies.app.com.portfolio.android.popularmovies.network.MovieDBAPI;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lenovo on 18-04-2016.
 */
public class TrailersTask extends AsyncTask<Long, Void, List<Trailer>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = TrailersTask.class.getSimpleName();
    private final Listener mListener;

    /**
     * Interface definition for a callback to be invoked when trailers are loaded.
     */
    public interface Listener {
        void onFetchFinished(List<Trailer> trailers);
    }

    public TrailersTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Trailer> doInBackground(Long... params) {
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
        Call<TrailerResponse> call = service.findTrailersById(movieId,
                Constants.MOVIE_DB_API_KEY);
        try {
            Response<TrailerResponse> response = call.execute();
            TrailerResponse trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if (trailers != null) {
            mListener.onFetchFinished(trailers);
        } else {
            mListener.onFetchFinished(new ArrayList<Trailer>());
        }
    }
}

