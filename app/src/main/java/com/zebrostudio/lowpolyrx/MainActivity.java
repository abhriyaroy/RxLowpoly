package com.zebrostudio.lowpolyrx;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.zebrostudio.imagecomparisonview.ImageComparisonView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

  private Disposable disposable;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final MaterialDialog lowpolyWaitLoader = new MaterialDialog.Builder(this)
        .widgetColor(colorRes(R.color.white))
        .contentColor(colorRes(R.color.white))
        .content(stringRes(R.string.lowpoly_wait_loader_message))
        .backgroundColor(colorRes(R.color.colorPrimary))
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .cancelable(false)
        .build();

    lowpolyWaitLoader.show();

    disposable = generateLowpolyFromResource()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Bitmap>() {
          @Override public void accept(Bitmap bitmap) {
            ImageComparisonView imageComparisonView = findViewById(R.id.imageComparisonView);
            imageComparisonView.setImages(R.mipmap.captain, bitmap);
            lowpolyWaitLoader.dismiss();
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) {
            lowpolyWaitLoader.dismiss();
          }
        });
  }

  @Override
  protected void onDestroy() {
    if (!disposable.isDisposed()){
      disposable.dispose();
    }
    super.onDestroy();
  }

  private Single<Bitmap> generateLowpolyFromResource() {
    return new LowPolyRx().getLowPolyImage(this, R.mipmap.captain);
  }

  private int colorRes(@ColorRes int color) {
    return this.getResources().getColor(color);
  }

  private String stringRes(@StringRes int string) {
    return this.getString(string);
  }
}
