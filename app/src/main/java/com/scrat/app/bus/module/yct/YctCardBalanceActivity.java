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
import com.scrat.app.bus.net.OkHttpHelper;
import com.scrat.app.bus.report.ViewReport;
import com.scrat.app.bus.utils.ActivityUtils;

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
    private AsyncTask<Void, Void, YctWechatDetail> mTask;

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

        String cardId = getIntent().getStringExtra(sExtraKey);
        if (TextUtils.isEmpty(cardId)) {
            cardId = ConfigSharePreferences.getInstance().getCardId();
        }
        if (!TextUtils.isEmpty(cardId)) {
            mSearchEt.setText(cardId);
            showContent(cardId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewReport.reportView(this, "yct_balance");
    }

    @Override
    public void showLoading() {
        if (mProgressBar == null) {
            mProgressBar = ProgressDialog.show(YctCardBalanceActivity.this, "羊城通余额", "正在查询...");
        } else {
            mProgressBar.show();
        }
    }

    @Override
    public void hideLoading() {
        if (mProgressBar == null)
            return;

        mProgressBar.dismiss();
    }

    @Override
    protected void onDestroy() {
        if (mTask != null) {
            mTask.cancel(false);
        }
        super.onDestroy();
    }

    private static class YctWechatDetail {
        private String cardId;
        private String endDt;
        private String balance;
        private String times;

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getEndDt() {
            return endDt;
        }

        public void setEndDt(String endDt) {
            this.endDt = endDt;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getTimes() {
            return times;
        }

        public void setTimes(String times) {
            this.times = times;
        }

        @Override
        public String toString() {
            return "YctWechatDetail{" +
                    "cardId='" + cardId + '\'' +
                    ", endDt='" + endDt + '\'' +
                    ", balance='" + balance + '\'' +
                    ", times='" + times + '\'' +
                    '}';
        }
    }

    private void showContent(final String cardId) {
        ActivityUtils.hideKeyboard(this);
        final String url = String.format(sUrlFormat, cardId);
        if (mTask != null) {
            mTask.cancel(false);
        }

        mTask = new AsyncTask<Void, Void, YctWechatDetail>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.contentPanel).setVisibility(View.GONE);
                findViewById(R.id.ll_tip).setVisibility(View.VISIBLE);
                mTimesTv.setText("");
                mYctIdTv.setText("");
                mBalancdTv.setText("");
                mEndDtTv.setText("");
                showLoading();
            }

            @Override
            protected YctWechatDetail doInBackground(Void... params) {
                String head = "<table class=\"btn-blue\" width=\"100%\">";
                String botton = "</table>";
                YctWechatDetail detail = new YctWechatDetail();
                try {
                    String content = OkHttpHelper.getInstance().get(url);
                    content = content.substring(content.indexOf(head) + head.length(), content.indexOf(botton) - botton.length()).replaceAll("[\\t\\n\\r]", "");
                    String[] datas = content.replace(" ", "").split("</tr><tr>");
                    for (String data : datas) {
                        String[] cols = data.split("</td><td>");
                        if (cols.length != 2) {
                            continue;
                        }

                        String key = cols[0];
                        String value = cols[1].replace("</td>", "");
                        if (key.contains("羊")) {
                            // 羊城通卡号
                            detail.setCardId(value);
                        } else if (key.contains("据")) {
                            // 数据截止时间
                            if (value.length() > 10) {
                                value = value.substring(0, 10) + " " + value.substring(10, value.length());
                            }
                            detail.setEndDt(value);
                        } else if (key.contains("余")) {
                            // 余额（元）
                            detail.setBalance(value);
                        } else if (key.contains("当")) {
                            detail.setTimes(value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return detail;
            }

            @Override
            protected void onPostExecute(YctWechatDetail detail) {
                super.onPostExecute(detail);
                hideLoading();
                if (detail == null) {
                    showToask("服务器有点抽风, 请重试...");
                } else {
                    mYctIdTv.setText(detail.getCardId());
                    mEndDtTv.setText(detail.getEndDt());
                    mBalancdTv.setText(detail.getBalance());
                    mTimesTv.setText(detail.getTimes());
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
