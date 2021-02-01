package com.wxl.gainjet;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment;
import com.trello.rxlifecycle2.components.support.RxDialogFragment;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

/**
 * create file time : 2021/1/29
 * create user : wxl
 * subscribe :
 */
public class ViewModelFactory {

    private RxAppCompatActivity mRxAppCompatActivity;
    private RxAppCompatDialogFragment mRxAppCompatDialogFragment;
    private RxFragment mRxFragment;
    private RxDialogFragment mRxDialogFragment;
    private RxFragmentActivity mRxFragmentActivity;

    private ViewModelFactory(RxAppCompatActivity mRxAppCompatActivity) {
        this.mRxAppCompatActivity = mRxAppCompatActivity;
    }

    private ViewModelFactory(RxAppCompatDialogFragment mRxAppCompatDialogFragment) {
        this.mRxAppCompatDialogFragment = mRxAppCompatDialogFragment;
    }

    private ViewModelFactory(RxFragment mRxFragment) {
        this.mRxFragment = mRxFragment;
    }

    private ViewModelFactory(RxDialogFragment mRxDialogFragment) {
        this.mRxDialogFragment = mRxDialogFragment;
    }

    private ViewModelFactory(RxFragmentActivity mRxFragmentActivity) {
        this.mRxFragmentActivity = mRxFragmentActivity;
    }

    private ViewModelFactory() {
    }

    private <T extends AbsViewModel> T observer(Class<T> viewModel, Observer observer) {
        final T t = new ViewModelProvider(mRxAppCompatActivity, new ViewModelProvider.NewInstanceFactory()).get(viewModel);
        if(observer != null ) {
            t.getLiveData().observe(mRxAppCompatActivity, observer);
        }
        t.setOnClearedCallback(new AbsViewModel.OnViewModelClearedCallback() {
            @Override
            public void onCleared(AbsLiveData data) {
                if(observer != null) {
                    data.removeObservers(mRxAppCompatActivity);
                }
                mRxAppCompatActivity = null;
            }
        });
        return t;
    }


    private <T extends AbsViewModel> T observerRxFragmentActivity(Class<T> viewModel, Observer observer) {
        final T t = new ViewModelProvider(mRxFragmentActivity, new ViewModelProvider.NewInstanceFactory()).get(viewModel);
        if(observer != null ) {
            t.getLiveData().observe(mRxFragmentActivity, observer);
        }
        t.setOnClearedCallback(new AbsViewModel.OnViewModelClearedCallback() {
            @Override
            public void onCleared(AbsLiveData data) {
                if(observer != null) {
                    data.removeObservers(mRxFragmentActivity);
                }
                mRxFragmentActivity = null;
            }
        });
        return t;
    }

    private <T extends AbsViewModel> T observerRxFragment(Class<T> viewModel, Observer observer) {
        final T t = new ViewModelProvider(mRxFragment, new ViewModelProvider.NewInstanceFactory()).get(viewModel);
        if(observer != null ) {
            t.getLiveData().observe(mRxFragment, observer);
        }
        t.setOnClearedCallback(new AbsViewModel.OnViewModelClearedCallback() {
            @Override
            public void onCleared(AbsLiveData data) {
                if(observer != null) {
                    data.removeObservers(mRxFragment);
                }
                mRxFragment = null;
            }
        });
        return t;
    }


    private <T extends AbsViewModel> T observerDialogFragment(Class<T> viewModel, Observer observer) {
        final T t = new ViewModelProvider(mRxAppCompatDialogFragment, new ViewModelProvider.NewInstanceFactory()).get(viewModel);
        if(observer != null ) {
            t.getLiveData().observe(mRxAppCompatDialogFragment, observer);
        }
        t.setOnClearedCallback(new AbsViewModel.OnViewModelClearedCallback() {
            @Override
            public void onCleared(AbsLiveData data) {
                if(observer != null) {
                    data.removeObservers(mRxAppCompatDialogFragment);
                }
                mRxAppCompatDialogFragment = null;
            }
        });
        return t;
    }


    private <T extends AbsViewModel> T observerRxDialogFragment(Class<T> viewModel, Observer observer) {
        final T t = new ViewModelProvider(mRxDialogFragment, new ViewModelProvider.NewInstanceFactory()).get(viewModel);
        if(observer != null ) {
            t.getLiveData().observe(mRxDialogFragment, observer);
        }
        t.setOnClearedCallback(new AbsViewModel.OnViewModelClearedCallback() {
            @Override
            public void onCleared(AbsLiveData data) {
                if(observer != null) {
                    data.removeObservers(mRxDialogFragment);
                }
                mRxDialogFragment = null;
            }
        });
        return t;
    }


    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param observer
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxAppCompatActivity context, Class<T> viewModel, Observer observer) {
        return new ViewModelFactory(context).observer(viewModel, observer);
    }

    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxAppCompatActivity context, Class<T> viewModel) {
        return new ViewModelFactory(context).observer(viewModel, null);
    }

    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param observer
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxFragmentActivity context, Class<T> viewModel, Observer observer) {
        return new ViewModelFactory(context).observerRxFragmentActivity(viewModel, observer);
    }


    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxFragmentActivity context, Class<T> viewModel) {
        return new ViewModelFactory(context).observerRxFragmentActivity(viewModel, null);
    }


    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param observer
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxAppCompatDialogFragment context, Class<T> viewModel, Observer observer) {
        return new ViewModelFactory(context).observerDialogFragment(viewModel, observer);
    }



    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxAppCompatDialogFragment context, Class<T> viewModel) {
        return new ViewModelFactory(context).observerDialogFragment(viewModel, null);
    }


    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param observer
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxFragment context, Class<T> viewModel, Observer observer) {
        return new ViewModelFactory(context).observerRxFragment(viewModel, observer);
    }


    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxFragment context, Class<T> viewModel) {
        return new ViewModelFactory(context).observerRxFragment(viewModel, null);
    }


    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param observer
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxDialogFragment context, Class<T> viewModel, Observer observer) {
        return new ViewModelFactory(context).observerRxFragment(viewModel, observer);
    }


    /**
     * 创建一个ViewModel
     * @param context
     * @param viewModel
     * @param
     * @param <T>
     * @return
     */
    public static <T extends AbsViewModel> T create(RxDialogFragment context, Class<T> viewModel) {
        return new ViewModelFactory(context).observerRxDialogFragment(viewModel, null);
    }
}

