package com.example.android.bakingapp.ui.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.JsonData;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.Serializable;
import java.util.Map;


public class StepInstruction extends Fragment implements ExoPlayer.EventListener {

    private static final String CODE_MAP = "hashmap";
    private static final String CODE_PLAYBACK_POSITION = "playbackPosition";

    private Map<String, String> step;
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView videoView;

    private StepInstructionClickListener stepInstructionCallBack;

    public StepInstruction() {
    }

    public static StepInstruction newInstance(Map<String, String> stepParam) {
        StepInstruction fragment = new StepInstruction();
        Bundle args = new Bundle();
        args.putSerializable(CODE_MAP, (Serializable) stepParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = (Map<String, String>) getArguments().getSerializable(CODE_MAP);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_instruction, container, false);
        String instruction = step.get(JsonData.STEP_DESC);

        TextView instructionTextView = view.findViewById(R.id.tv_instruction);
        instructionTextView.setText(instruction);
        //set button listeners
        ImageView leftButton = view.findViewById(R.id.button_previous);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLeftClicked();
            }
        });
        ImageView rightButton = view.findViewById(R.id.button_next);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRightClicked();
            }
        });

        videoView = view.findViewById(R.id.video_player);

        exoPlayer = ExoPlayerFactory.newSimpleInstance(
                getContext(),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        //set up player
        initializePlayer();
        videoView.setUseController(true);
        videoView.requestFocus();
        videoView.setPlayer(exoPlayer);

        if (savedInstanceState != null) {
            exoPlayer.seekTo(savedInstanceState.getLong(CODE_PLAYBACK_POSITION));
        }


        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(this);

        if(!getResources().getBoolean(R.bool.isTablet) &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            RelativeLayout instructionLayout = view.findViewById(R.id.instruction_layout);
            instructionLayout.setVisibility(View.GONE);
        }

        return view;
    }

    //prepare player with media
    private void initializePlayer() {
        if (step.get(JsonData.STEP_VIDEO_URL).isEmpty()) {
            videoView.setVisibility(View.GONE);
        }
        Uri uri = Uri.parse(step.get(JsonData.STEP_VIDEO_URL));
        MediaSource mediaSource = buildMediaSource(uri);
        LoopingMediaSource loopingSource = new LoopingMediaSource(mediaSource);
        exoPlayer.prepare(loopingSource);
    }

    //extract data from uri
    private MediaSource buildMediaSource(Uri uri) {
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(),
                "bakingApp"),
                bandwidthMeterA);

        return new ExtractorMediaSource(uri,
                dataSourceFactory,
                new DefaultExtractorsFactory(),
                null,
                null);
    }

    private void onLeftClicked() {
        if (stepInstructionCallBack != null) {
            stepInstructionCallBack.onVideoLeftClick();
        }
    }

    private void onRightClicked() {
        if (stepInstructionCallBack != null) {
            stepInstructionCallBack.onVideoRightClick();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StepInstructionClickListener) {
            stepInstructionCallBack = (StepInstructionClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StepInstructionClickListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        exoPlayer.removeListener(this);
        exoPlayer.release();
        stepInstructionCallBack = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CODE_PLAYBACK_POSITION, exoPlayer.getCurrentPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayer.stop();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY) {
            videoView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            videoView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }



    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    public interface StepInstructionClickListener {
        void onVideoLeftClick();
        void onVideoRightClick();
    }
}
