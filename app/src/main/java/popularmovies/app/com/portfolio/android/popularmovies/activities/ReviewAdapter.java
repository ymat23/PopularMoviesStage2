package popularmovies.app.com.portfolio.android.popularmovies.activities;

/**
 * Created by lenovo on 14-04-2016.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import popularmovies.app.com.portfolio.android.popularmovies.R;
import popularmovies.app.com.portfolio.android.popularmovies.beans.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final static String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private final ArrayList<Review> mReviews;
    private final Callbacks mCallbacks;

    public ReviewAdapter(ArrayList<Review> reviews, Callbacks callbacks) {
        mReviews = reviews;
        mCallbacks = callbacks;
    }

    public interface Callbacks {
        void read(Review review, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Review review = mReviews.get(position);

        holder.mReview = review;
        holder.mContentView.setText(review.getmContent());
        holder.mAuthorView.setText(review.getmAuthor());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.read(review, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.review_content)
        TextView mContentView;
        @Bind(R.id.review_author)
        TextView mAuthorView;
        public Review mReview;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    public void add(List<Review> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }
}
