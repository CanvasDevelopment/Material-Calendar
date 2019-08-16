package com.prolificinteractive.materialcalendarview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

class TitleChanger {

  public static final int DEFAULT_ANIMATION_DELAY = 400;
  public static final int DEFAULT_Y_TRANSLATION_DP = 20;

  private final TextView title;
  private final TextView past;
  private final TextView future;
  private final TextView year;

  @NonNull private TitleFormatter titleFormatter = TitleFormatter.DEFAULT;

  private final int animDelay;
  private final int animDuration;
  private final int translate;
  private final Interpolator interpolator = new DecelerateInterpolator(2f);

  private int orientation = MaterialCalendarView.VERTICAL;

  private long lastAnimTime = 0;
  private CalendarDay previousMonth = null;

  private final RelativeLayout topBar;

  public TitleChanger(TextView title, TextView past, TextView future, RelativeLayout topBar, TextView year) {
    this.title = title;
    this.past = past;
    this.future = future;
    this.topBar = topBar;
    this.year = year;
    Resources res = title.getResources();

    animDelay = DEFAULT_ANIMATION_DELAY;

    animDuration = res.getInteger(android.R.integer.config_shortAnimTime) *2;

    translate = (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, DEFAULT_Y_TRANSLATION_DP, res.getDisplayMetrics()
    );
  }

  public void change(final CalendarDay currentMonth,
                     final CalendarDay previousMonth,
                     final CalendarDay nextMonth) {
    long currentTime = System.currentTimeMillis();

    if (currentMonth == null) {
        return;
    }

    if (TextUtils.isEmpty(title.getText()) || (currentTime - lastAnimTime) < animDelay) {
      doChange(currentTime, currentMonth, previousMonth,nextMonth, false);

    }

    if (currentMonth.equals(previousMonth) ||
        (currentMonth.getMonth() == previousMonth.getMonth()
            && currentMonth.getYear() == previousMonth.getYear())) {
      return;
    }

    doChange(currentTime, currentMonth,  previousMonth,nextMonth,false);
  }

  private String simpleDay(CalendarDay month) {
    final CharSequence newTitle = titleFormatter.format(month);
    String[] test = newTitle.toString().split(" ");
    return test[0];
  }

  private void doChange(final long now,
                        final CalendarDay currentMonth,
                        final CalendarDay previousMonth,
                        final CalendarDay nextMonth,
                        boolean animate) {

//    lastAnimTime = now;
//
    String newTitle = simpleDay(currentMonth);
    String yearText = getYear(currentMonth);
//
    title.setText(newTitle);
    past.setText(simpleDay(previousMonth));
    future.setText(simpleDay(nextMonth));
    year.setText(yearText);

    this.previousMonth = currentMonth;
  }

  private String getYear(CalendarDay currentMonth) {
      final CharSequence newTitle = titleFormatter.format(currentMonth);
      String[] test = newTitle.toString().split(" ");
      return test[1];
  }

  private void doTranslation(final TextView title, final int translate) {
    if (orientation == MaterialCalendarView.HORIZONTAL) {
      title.setTranslationX(translate);
    } else {
      title.setTranslationY(translate);
    }
  }

  public void setTitleFormatter(@Nullable final TitleFormatter titleFormatter) {
    this.titleFormatter = titleFormatter == null ? TitleFormatter.DEFAULT : titleFormatter;
  }

  public void setOrientation(int orientation) {
    this.orientation = orientation;
  }

  public int getOrientation() {
    return orientation;
  }

  public void setPreviousMonth(CalendarDay previousMonth) {
    this.previousMonth = previousMonth;
  }

  public void offset(float positionOffset) {
    int width = topBar.getWidth();
    ObjectAnimator topBarAnim = ObjectAnimator.ofFloat(topBar, "translationX", 0-((width*0.43f)* positionOffset));
    topBarAnim.setDuration(0);
    topBarAnim.start();

  }
}
