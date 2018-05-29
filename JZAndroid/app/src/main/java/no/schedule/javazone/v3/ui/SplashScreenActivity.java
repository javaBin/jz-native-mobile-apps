package no.schedule.javazone.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import no.schedule.javazone.v3.schedule.*;

public class SplashScreenActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = new Intent(this, no.schedule.javazone.v3.schedule.ScheduleActivity.class);
    startActivity(intent);
    finish();
  }
}
