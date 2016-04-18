package popularmovies.app.com.portfolio.android.popularmovies.network;

import popularmovies.app.com.portfolio.android.popularmovies.beans.MovieResponse;
import popularmovies.app.com.portfolio.android.popularmovies.beans.ReviewResponse;
import popularmovies.app.com.portfolio.android.popularmovies.beans.TrailerResponse;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by LENOVO on 03-03-2016.
 */
public interface MovieDBAPI {

    @GET("3/movie/{sort_by}")
    Call<MovieResponse> discoverMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<TrailerResponse> findTrailersById(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<ReviewResponse> findReviewsById(@Path("id") long movieId, @Query("api_key") String apiKey);
}
