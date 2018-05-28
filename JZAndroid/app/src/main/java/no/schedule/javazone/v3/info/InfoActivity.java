package no.schedule.javazone.v3.info;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.navigation.NavigationModel;
import no.schedule.javazone.v3.ui.BaseActivity;

public class InfoActivity extends BaseActivity {

  private InfoContract.Presenter mPresenter;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.info_act);
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    setFullscreenLayout();

    InfoPagerFragment infoPagerFragment = (InfoPagerFragment) getSupportFragmentManager()
        .findFragmentById(R.id.main_content);
    mPresenter = new InfoPresenter(this, infoPagerFragment);
    mPresenter.initEventInfo();
    mPresenter.initAboutInfo();
    mPresenter.initTravelInfo();
    infoPagerFragment.setPresenter(mPresenter);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected NavigationModel.NavigationItemEnum getSelfNavDrawerItem() {
    return NavigationModel.NavigationItemEnum.INFO;
  }

  @Override
  public boolean canSwipeRefreshChildScrollUp() {
    return true;
  }

  @Override
  protected int getNavigationTitleId() {
    return R.string.title_info;
  }
}
