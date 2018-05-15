package no.schedule.javazone.v3.feedback;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.io.model.Feedback;
import no.schedule.javazone.v3.sync.FeedbackApiService;
import no.schedule.javazone.v3.ui.widget.CustomRatingBar;
import no.schedule.javazone.v3.util.AnalyticsHelper;
import no.schedule.javazone.v3.util.Constants;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

import static android.view.View.GONE;

public class SessionFeedbackFragment extends Fragment {
  private CollapsingToolbarLayout mCollapsingToolbar;
  private TextView mSpeakers;
  private FeedbackApiService mFeedbackService;
  private CustomRatingBar mOverallFeedbackBar;
  private CustomRatingBar mSessionRelevantFeedbackBar;
  private CustomRatingBar mContentFeedbackBar;
  private CustomRatingBar mSpeakerFeedbackBar;
  private TextView mSessionFeedbackComments;
  private String mSessionId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.session_feedback_frag, container, false);
    Intent intent = this.getActivity().getIntent();
    mSessionId = intent.getStringExtra(Constants.SESSION_ID);
    mCollapsingToolbar =
        (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
    final Typeface productSans =
        TypefaceUtils.load(getContext().getAssets(), "fonts/ProductSans-Regular.ttf");
    mCollapsingToolbar.setExpandedTitleTypeface(productSans);
    mCollapsingToolbar.setCollapsedTitleTypeface(productSans);
    mSpeakers = (TextView) rootView.findViewById(R.id.feedback_header_session_speakers);
    mOverallFeedbackBar = (CustomRatingBar) rootView.findViewById(R.id.rating_bar_0);
    mSessionRelevantFeedbackBar = (CustomRatingBar) rootView.findViewById(
        R.id.session_relevant_feedback_bar);
    mContentFeedbackBar = (CustomRatingBar) rootView.findViewById(R.id.content_feedback_bar);
    mSpeakerFeedbackBar = (CustomRatingBar) rootView.findViewById(R.id.speaker_feedback_bar);
    mSessionFeedbackComments = (TextView) rootView.findViewById(R.id.feedback_comment_textview);

    rootView.findViewById(R.id.submit_feedback_button).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            submitFeedback();
          }
        }
    );

    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  private void submitFeedback() {
    int overallAnswer = (int) mOverallFeedbackBar.getRating();
    int sessionRelevantAnswer = mSessionRelevantFeedbackBar.getRating();
    int contentAnswer = mContentFeedbackBar.getRating();
    int speakerAnswer = mSpeakerFeedbackBar.getRating();
    String feedbackComment = mSessionFeedbackComments.getText() != null ?
        mSessionFeedbackComments.getText().toString() : "";
    String sessionId = null;
    String eventId = null;

    Pattern pattern = Pattern.compile(".*\\/events\\/(.*)\\/sessions\\/(.*)");
    Matcher matcher = pattern.matcher(mSessionId);

    if (matcher.matches()) {
      eventId = matcher.group(1);
      sessionId = matcher.group(2);
    }

    Feedback jzFeedback = new Feedback(overallAnswer, sessionRelevantAnswer,
        contentAnswer, speakerAnswer, feedbackComment);
    mFeedbackService.postSessionFeedback(eventId, sessionId, generateUniqueVoterId(),jzFeedback);
  }

  public String generateUniqueVoterId() {
    return Settings.Secure.getString(getActivity().getContentResolver(),
        Settings.Secure.ANDROID_ID);
  }
}
