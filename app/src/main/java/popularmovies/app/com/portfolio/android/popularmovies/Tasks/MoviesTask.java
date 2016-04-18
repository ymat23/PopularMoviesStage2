package popularmovies.app.com.portfolio.android.popularmovies.Tasks;

import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import popularmovies.app.com.portfolio.android.popularmovies.BuildConfig;
import popularmovies.app.com.portfolio.android.popularmovies.Utils.Constants;
import popularmovies.app.com.portfolio.android.popularmovies.activities.Command;
import popularmovies.app.com.portfolio.android.popularmovies.beans.Movie;
import popularmovies.app.com.portfolio.android.popularmovies.beans.MovieResponse;
import popularmovies.app.com.portfolio.android.popularmovies.network.MovieDBAPI;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lenovo on 18-04-2016.
 */
public class MoviesTask extends AsyncTask<Void, Void, List<Movie>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = MoviesTask.class.getSimpleName();

    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITES = "favorites";

    // FetchMoviesTask cannot load favorites movies now, it's done by loaders (especially for two pane is
    // comfortable - without force updating left pane on removing/adding a favorite movie. Another
    // case when we simple returns from detail - list of favorites also will be updated, if needed).
    @StringDef({MOST_POPULAR, TOP_RATED, FAVORITES})
    public @interface SORT_BY {
    }

    /**
     * Will be called in {@link MoviesTask#onPostExecute(List)} to notify subscriber to about
     * task completion.
     */
    private final NotifyAboutTaskCompletionCommand mCommand;
    private
    @SORT_BY
    String mSortBy = MOST_POPULAR;

    /**
     * Interface definition for a callback to be invoked when movies are loaded.
     */
    public interface Listener {
        void onFetchFinished(Command command);
    }


    public static class NotifyAboutTaskCompletionCommand implements Command {
        private MoviesTask.Listener mListener;
        // The result of the task execution.
        private List<Movie> mMovies;

        public NotifyAboutTaskCompletionCommand(MoviesTask.Listener listener) {
            mListener = listener;
        }

        @Override
        public void execute() {
            mListener.onFetchFinished(this);
        }

        public List<Movie> getMovies() {
            return mMovies;
        }
    }

    public MoviesTask(@SORT_BY String sortBy, NotifyAboutTaskCompletionCommand command) {
        mCommand = command;
        mSortBy = sortBy;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (movies != null) {
            mCommand.mMovies = movies;
        } else {
            mCommand.mMovies = new ArrayList<>();
        }
        mCommand.execute();
    }

    @Override
    protected List<Movie> doInBackground(Void... params) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBAPI service = retrofit.create(MovieDBAPI.class);
        Call<MovieResponse> call = service.discoverMovies(mSortBy,
                Constants.MOVIE_DB_API_KEY);
        try {
            Response<MovieResponse> response = call.execute();
            MovieResponse movies = response.body();
            return movies.getResults();

        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }
        return null;
    }
}

