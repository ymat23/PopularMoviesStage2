package popularmovies.app.com.portfolio.android.popularmovies.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import popularmovies.app.com.portfolio.android.popularmovies.R;
import popularmovies.app.com.portfolio.android.popularmovies.Tasks.MoviesTask;
import popularmovies.app.com.portfolio.android.popularmovies.beans.Movie;
import popularmovies.app.com.portfolio.android.popularmovies.data.MovieContract;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MoviesTask.Listener, MovieAdapter.Callbacks {

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";
    private static final int FAVORITE_MOVIES_LOADER = 0;
    private boolean mTablet;
    private RetainedFragment mRetainedFragment;
    private MovieAdapter mAdapter;
    private String mSortBy = MoviesTask.MOST_POPULAR;

    @Bind(R.id.movie_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.title_movie_list);
        setSupportActionBar(mToolbar);

        String tag = RetainedFragment.class.getName();
        this.mRetainedFragment = (RetainedFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (this.mRetainedFragment == null) {
            this.mRetainedFragment = new RetainedFragment();
            getSupportFragmentManager().beginTransaction().add(this.mRetainedFragment, tag).commit();
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources()
                .getInteger(R.integer.grid_number_cols)));
        mAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(mAdapter);

        mTablet = findViewById(R.id.movie_detail_container) != null;

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(EXTRA_SORT_BY);
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                mAdapter.add(movies);
                findViewById(R.id.progress).setVisibility(View.GONE);

                if (mSortBy.equals(MoviesTask.FAVORITES)) {
                    getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
                }
            }
            updateEmptyState();
        } else {
            // Fetch Movies only if savedInstanceState == null
            fetchMovies(mSortBy);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = mAdapter.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_MOVIES, movies);
        }
        outState.putString(EXTRA_SORT_BY, mSortBy);

        if (!mSortBy.equals(MoviesTask.FAVORITES)) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_list_activity, menu);

        switch (mSortBy) {
            case MoviesTask.MOST_POPULAR:
                menu.findItem(R.id.sort_by_most_popular).setChecked(true);
                break;
            case MoviesTask.TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;
            case MoviesTask.FAVORITES:
                menu.findItem(R.id.sort_by_favorites).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_top_rated:
                if (mSortBy.equals(MoviesTask.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = MoviesTask.TOP_RATED;
                fetchMovies(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_most_popular:
                if (mSortBy.equals(MoviesTask.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = MoviesTask.MOST_POPULAR;
                fetchMovies(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_favorites:
                mSortBy = MoviesTask.FAVORITES;
                item.setChecked(true);
                fetchMovies(mSortBy);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void open(Movie movie, int position) {
        if (mTablet) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailActivityFragment.EXTRA_MOVIE, movie);
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivityFragment.EXTRA_MOVIE, movie);
            startActivity(intent);
        }
    }

    @Override
    public void onFetchFinished(Command command) {
        if (command instanceof MoviesTask.NotifyAboutTaskCompletionCommand) {
            mAdapter.add(((MoviesTask.NotifyAboutTaskCompletionCommand) command).getMovies());
            updateEmptyState();
            findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.add(cursor);
        updateEmptyState();
        findViewById(R.id.progress).setVisibility(View.GONE);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        return new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private void fetchMovies(String sortBy) {
        if (!sortBy.equals(MoviesTask.FAVORITES)) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            MoviesTask.NotifyAboutTaskCompletionCommand command =
                    new MoviesTask.NotifyAboutTaskCompletionCommand(this.mRetainedFragment);
            new MoviesTask(sortBy, command).execute();
        } else {
            getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
        }
    }

    private void updateEmptyState() {
        if (mAdapter.getItemCount() == 0) {
            if (mSortBy.equals(MoviesTask.FAVORITES)) {
                findViewById(R.id.empty_state_container).setVisibility(View.GONE);
                findViewById(R.id.empty_state_favorites_container).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.empty_state_container).setVisibility(View.VISIBLE);
                findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.empty_state_container).setVisibility(View.GONE);
            findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
        }
    }

    public static class RetainedFragment extends Fragment implements MoviesTask.Listener {

        private boolean mPaused = false;
        private Command mWaitingCommand = null;

        public RetainedFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            mPaused = true;
        }

        @Override
        public void onResume() {
            super.onResume();
            mPaused = false;
            if (mWaitingCommand != null) {
                onFetchFinished(mWaitingCommand);
            }
        }

        @Override
        public void onFetchFinished(Command command) {
            if (getActivity() instanceof MoviesTask.Listener && !mPaused) {
                MoviesTask.Listener listener = (MoviesTask.Listener) getActivity();
                listener.onFetchFinished(command);
                mWaitingCommand = null;
            } else {
                mWaitingCommand = command;
            }
        }
    }
}