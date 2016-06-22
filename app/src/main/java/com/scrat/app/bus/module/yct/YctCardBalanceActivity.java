package com.scrat.app.bus.module.yct;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scrat.app.bus.R;
import com.scrat.app.bus.common.BaseActivity;
import com.scrat.app.bus.module.ConfigSharePreferences;
import com.scrat.app.core.utils.ActivityUtils;
import com.scrat.app.core.utils.NetUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by yixuanxuan on 16/6/1.
 */
public class YctCardBalanceActivity extends BaseActivity implements View.OnClickListener {
    private static final String sExtraKey = "card_id";
    private static final String sUrlFormat = "http://weixin.gzyct.com/busiqry/user-card-balance!otherBalanceQry.action?openid=oKYOJjqvCJWcqNJDVLJF9lZIYVNY&cardno=%s";

    public static void startActivity(Context ctx, String cardId) {
        Intent i = new Intent(ctx, YctCardBalanceActivity.class);
        i.putExtra(sExtraKey, cardId);
        ctx.startActivity(i);
    }

    private EditText mSearchEt;
    private ImageView mSearchIv;
    private ImageView mBackIv;
    private ProgressDialog mProgressBar;
    private AsyncTask<Boolean, String, Boolean> mTask;

    private TextView mYctIdTv;
    private TextView mBalancdTv;
    private TextView mTimesTv;
    private TextView mEndDtTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_yct_balance);

        mSearchIv = (ImageView) findViewById(R.id.iv_search);
        mBackIv = (ImageView) findViewById(R.id.iv_back);
        mBackIv.setOnClickListener(this);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mSearchIv.setOnClickListener(this);
        mYctIdTv = (TextView) findViewById(R.id.tv_yct_id);
        mBalancdTv = (TextView) findViewById(R.id.tv_balance);
        mTimesTv = (TextView) findViewById(R.id.tv_times);
        mEndDtTv = (TextView) findViewById(R.id.tv_end_dt);

        mProgressBar = ProgressDialog.show(YctCardBalanceActivity.this, "羊城通余额", "正在查询...");

        String cardId = getIntent().getStringExtra(sExtraKey);
        if (TextUtils.isEmpty(cardId)) {
            cardId = ConfigSharePreferences.getInstance().getCardId();
        }
        if (!TextUtils.isEmpty(cardId)) {
            mSearchEt.setText(cardId);
            showContent(cardId);
        } else {
            mProgressBar.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (mTask != null) {
            mTask.cancel(false);
        }
        super.onDestroy();
    }

    private void showContent(final String cardId) {
        ActivityUtils.hideKeyboard(this);
        final String url = String.format(sUrlFormat, cardId);
        if (mTask != null) {
            mTask.cancel(false);
        }

        if (!NetUtil.isNetworkAvailable()) {
            onNoNetworkError();
            return;
        }

        mTask = new AsyncTask<Boolean, String, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.contentPanel).setVisibility(View.GONE);
                findViewById(R.id.ll_tip).setVisibility(View.VISIBLE);
                mTimesTv.setText("");
                mYctIdTv.setText("");
                mBalancdTv.setText("");
                mEndDtTv.setText("");
                mProgressBar.show();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if (values == null || values.length != 2)
                    return;

                String key = values[0];
                String content = values[1];
                if (key.contains("羊")) {
                    // 羊城通卡号
                    mYctIdTv.setText(content);
                } else if (key.contains("据")) {
                    // 数据截止时间
                    mEndDtTv.setText(content);
                } else if (key.contains("余")) {
                    // 余额（元）
                    mBalancdTv.setText(content);
                } else if (key.contains("当")) {
                    mTimesTv.setText(content);
                }
            }

            @Override
            protected Boolean doInBackground(Boolean... params) {
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements trs = doc.select("table").select("tr");
                    int totalTr = trs.size();
                    if (totalTr == 0)
                        return Boolean.FALSE;

                    for (int i = 0; i < totalTr; i++) {
                        Elements tds = trs.get(i).select("td");
                        if (tds.size() != 2) {
                            continue;
                        }

                        String key = tds.get(0).text();
                        String content = tds.get(1).text();
                        publishProgress(key, content);
                    }
                    return Boolean.TRUE;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                mProgressBar.dismiss();
                if (aBoolean == null || !aBoolean) {
                    showToask("服务器有点抽风, 请重试...");
                } else {
                    findViewById(R.id.contentPanel).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_tip).setVisibility(View.GONE);
                    ConfigSharePreferences.getInstance().setCardId(cardId);
                }
            }
        };
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {
        if (v == mSearchIv) {
            String content = mSearchEt.getText().toString();
            if (TextUtils.isEmpty(content)) {
                showToask("请输入正确的羊城通卡号");
                return;
            }

            showContent(content);
        } else if (v == mBackIv) {
            finish();
        }
    }
}
