package com.test.developerslife;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private List<HumourPost> mHumourPosts;
    private int mCurrentPostPosition;
    private static final String KEY_GIF = "GIF_";
    private static final String KEY_DESCRIPTION = "DESCRIPTION_";
    private static final String KEY_CURRENT_POST = "CURRENT_POST";
    private static final String KEY_POST_COUNT = "POST_COUNT";

    private ImageView gif_image_view;
    private ImageButton previous_button;
    private ImageButton next_button;
    private TextView description_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHumourPosts = new ArrayList<>();


        gif_image_view = findViewById(R.id.gif_image_view);
        previous_button = findViewById(R.id.previous_button);
        next_button = findViewById(R.id.next_button);
        description_text_view = findViewById(R.id.description_text_view);

        if (savedInstanceState == null){
            mCurrentPostPosition = 0;
            previous_button.setEnabled(false);

        }

        previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousPosition();
                loadPost();
                if (mCurrentPostPosition == 0) {
                    previous_button.setEnabled(false);
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPosition();
                loadPost();
                previous_button.setEnabled(true);
            }
        });

        loadPost();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        for(int post = 0; post < mHumourPosts.size(); post++) {
            HumourPost postCopy = mHumourPosts.get(post);
            outState.putString(KEY_GIF + post, postCopy.getGif());
            outState.putString(KEY_DESCRIPTION + post, postCopy.getDescription());
        }
        outState.putInt(KEY_CURRENT_POST, mCurrentPostPosition);
        outState.putInt(KEY_POST_COUNT, mHumourPosts.size());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        int postCount = savedInstanceState.getInt(KEY_POST_COUNT);

        for(int post = 0; post < postCount; post++) {
            mHumourPosts.add(new HumourPost(savedInstanceState.getString(KEY_GIF + post), savedInstanceState.getString(KEY_DESCRIPTION + post)));
        }

        mCurrentPostPosition = savedInstanceState.getInt(KEY_CURRENT_POST);
        if (mCurrentPostPosition == 0) {
            previous_button.setEnabled(false);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void loadPost() {
        NetworkService.getInstance().getJSONApi()
                .getRandomPost()
                .enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        Post randomPost = response.body();
                        if (mHumourPosts.size() == mCurrentPostPosition) {
                            String gif = randomPost.getGifURL();
                            if (gif != null) {
                                mHumourPosts.add(new HumourPost(gif, randomPost.getDescription()));
                            } else {
                                previousPosition();
                                return;
                            }
                        }

                        Glide.with(getApplicationContext())
                                .asGif()
                                .load(mHumourPosts.get(mCurrentPostPosition).getGif())
                                .into(gif_image_view);
                        description_text_view.setText(mHumourPosts.get(mCurrentPostPosition).getDescription());
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), getString(R.string.load_error),
                                Toast.LENGTH_SHORT).show();
                        previousPosition();
                    }
                });
    }

    private void nextPosition() {
        mCurrentPostPosition++;
    }

    private void previousPosition() {
        mCurrentPostPosition--;
    }
}