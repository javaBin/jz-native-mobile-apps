package no.schedule.javazone.v3.feedback;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import no.schedule.javazone.v3.archframework.PresenterImpl;
import no.schedule.javazone.v3.archframework.UpdatableView;
import no.schedule.javazone.v3.feedback.SessionFeedbackModel.SessionFeedbackQueryEnum;
import no.schedule.javazone.v3.feedback.SessionFeedbackModel.SessionFeedbackUserActionEnum;
import no.schedule.javazone.v3.injection.ModelProvider;
import no.schedule.javazone.v3.io.model.Feedback;
import no.schedule.javazone.v3.sync.FeedbackApiService;
import no.schedule.javazone.v3.ui.widget.CustomRatingBar;
import no.schedule.javazone.v3.util.AnalyticsHelper;
import no.schedule.javazone.v3.util.Constants;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

import static android.view.View.GONE;

public class SessionFeedbackFragment extends Fragment
    implements UpdatableView<SessionFeedbackModel, SessionFeedbackQueryEnum,
    SessionFeedbackUserActionEnum> {
  private CollapsingToolbarLayout mCollapsingToolbar;
  private TextView mSpeakers;
  private CustomRatingBar mOverallFeedbackBar;
  private CustomRatingBar mSessionRelevantFeedbackBar;
  private CustomRatingBar mContentFeedbackBar;
  private CustomRatingBar mSpeakerFeedbackBar;
  private TextView mSessionFeedbackComments;

  private List<UserActionListener<SessionFeedbackUserActionEnum>> listeners = new ArrayList<>();


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.session_feedback_frag, container, false);
    mCollapsingToolbar =
        (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
    final Typeface productSans =
        TypefaceUtils.load(getContext().getAssets(), "fonts/ProductSans-Regular.ttf");
    mCollapsingToolbar.setExpandedTitleTypeface(productSans);
    mCollapsingToolbar.setCollapsedTitleTypeface(productSans);
    mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.app_white));
    mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.app_white));


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
    initPresenter();
  }

  private void submitFeedback() {
    Bundle args = new Bundle();
    args.putInt(SessionFeedbackModel.DATA_RATING_INT, mOverallFeedbackBar.getRating());
    args.putInt(SessionFeedbackModel.DATA_SESSION_RELEVANT_ANSWER_INT,
        mSessionRelevantFeedbackBar.getRating());
    args.putInt(SessionFeedbackModel.DATA_CONTENT_ANSWER_INT, mContentFeedbackBar.getRating());
    args.putInt(SessionFeedbackModel.DATA_SPEAKER_ANSWER_INT, mSpeakerFeedbackBar.getRating());
    args.putString(SessionFeedbackModel.DATA_COMMENT_STRING, mSessionFeedbackComments != null ?
        mSessionFeedbackComments.getText().toString() : "");

    for (UserActionListener<SessionFeedbackUserActionEnum> listener : listeners) {
      listener.onUserAction(SessionFeedbackUserActionEnum.SUBMIT, args);
    }
  }

  @Override
  public void displayData(SessionFeedbackModel model, SessionFeedbackQueryEnum query) {
    switch (query) {
      case SESSION:
        mCollapsingToolbar.setTitle(model.getSessionTitle());
        if (!TextUtils.isEmpty(model.getSessionSpeakers())) {
          mSpeakers.setText(model.getSessionSpeakers());
        } else {
          mSpeakers.setVisibility(GONE);
        }

        // ANALYTICS SCREEN: View Send Session Feedback screen
        // Contains: Session title
        AnalyticsHelper.sendScreenView("Feedback: " + model.getSessionTitle(),
            getActivity());
        break;
    }
  }

  @Override
  public void displayErrorMessage(SessionFeedbackQueryEnum query) {
    // Close the Activity
    getActivity().finish();
  }

  @Override
  public void displayUserActionResult(SessionFeedbackModel model, SessionFeedbackUserActionEnum userAction, boolean success) {

  }


  @Override
  public Uri getDataUri(SessionFeedbackQueryEnum query) {
    switch (query) {
      case SESSION:
        return ((SessionFeedbackActivity) getActivity()).getSessionUri();
      default:
        return null;
    }
  }

  @Override
  public Context getContext() {
    return getActivity();
  }

  @Override
  public void addListener(UserActionListener<SessionFeedbackUserActionEnum> listener) {
    listeners.add(listener);
  }

  private void initPresenter() {
    SessionFeedbackModel model = ModelProvider.provideSessionFeedbackModel(
        ((SessionFeedbackActivity) getActivity()).getSessionUri(), getContext(), getLoaderManager());
    PresenterImpl<SessionFeedbackModel, SessionFeedbackQueryEnum, SessionFeedbackUserActionEnum>
        presenter = new PresenterImpl<>(model, this, SessionFeedbackUserActionEnum.values(),
        SessionFeedbackQueryEnum.values());
    presenter.loadInitialQueries();
  }
}
