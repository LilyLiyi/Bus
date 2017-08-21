package com.scrat.app.bus.common;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.scrat.app.bus.R;

/**
 * Created by yixuanxuan on 16/5/15.
 */
public abstract class BaseFragment extends Fragment implements BaseView {
    protected void showMsg(String content) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, content, Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), content, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    protected boolean isDestroyed() {
        if (getView() == null) {
            return true;
        }

        if (getActivity() == null) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity().isDestroyed()) {
                return true;
            }
        }

        return getActivity().isFinishing();
    }

}
