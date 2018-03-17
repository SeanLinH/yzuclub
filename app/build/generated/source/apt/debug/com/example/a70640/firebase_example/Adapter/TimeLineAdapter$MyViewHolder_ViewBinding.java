// Generated code from Butter Knife. Do not modify!
package com.example.a70640.firebase_example.Adapter;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.a70640.firebase_example.R;
import com.github.vipulasri.timelineview.TimelineView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TimeLineAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private TimeLineAdapter.MyViewHolder target;

  @UiThread
  public TimeLineAdapter$MyViewHolder_ViewBinding(TimeLineAdapter.MyViewHolder target,
      View source) {
    this.target = target;

    target.mTimelineView = Utils.findRequiredViewAsType(source, R.id.time_marker, "field 'mTimelineView'", TimelineView.class);
    target.name = Utils.findRequiredViewAsType(source, R.id.club_act_flat_activity_name, "field 'name'", TextView.class);
    target.date = Utils.findRequiredViewAsType(source, R.id.club_act_flat_activity_date, "field 'date'", TextView.class);
    target.time = Utils.findRequiredViewAsType(source, R.id.club_act_flat_activity_time, "field 'time'", TextView.class);
    target.location = Utils.findRequiredViewAsType(source, R.id.club_act_flat_activity_location, "field 'location'", TextView.class);
    target.thumbnail = Utils.findRequiredViewAsType(source, R.id.club_act_flat_activity_thumbnail, "field 'thumbnail'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    TimeLineAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTimelineView = null;
    target.name = null;
    target.date = null;
    target.time = null;
    target.location = null;
    target.thumbnail = null;
  }
}
