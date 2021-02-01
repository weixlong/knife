package com.wxl.aptmvp.jet;

import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.wxl.gainjet.AbsPresenter;
import com.wxl.gainjet.ViewModelFactory;

/**
 * create file time : 2021/2/1
 * create user : wxl
 * subscribe :
 */
public class MainPresenter extends AbsPresenter {

    TextView textView;

    @Override
    public void onCreated(RxAppCompatActivity context, Object... args) {
        super.onCreated(context, args);
        textView = (TextView) args[0];
        ViewModelFactory.create(context, MainViewModel.class, new Observer<String>() {
            @Override
            public void onChanged(String o) {
                textView.setText(o);
            }
        });
    }
}
