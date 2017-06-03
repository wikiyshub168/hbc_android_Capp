package com.hugboga.custom.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.DefaultSSLSocketFactory;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CityBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.net.WebAgent;
import com.hugboga.custom.statistic.sensors.SensorsUtils;
import com.hugboga.custom.utils.ChannelUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.UIUtils;
import com.hugboga.custom.widget.DialogUtil;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

import butterknife.Bind;

public class WebInfoActivity extends BaseActivity implements View.OnKeyListener {

    public static final String TAG = WebInfoActivity.class.getSimpleName();
    public static final String WEB_URL = "web_url";
    public static final String CONTACT_SERVICE = "contact_service";

    public boolean isHttps = false;
    @Bind(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @Bind(R.id.header_right_btn)
    ImageView headerRightBtn;
    @Bind(R.id.header_title)
    TextView headerTitle;
    @Bind(R.id.header_right_txt)
    TextView headerRightTxt;
    @Bind(R.id.webview)
    WebView webView;
    private DialogUtil mDialogUtil;

    private CityBean cityBean;
    private boolean isLogin = false;
    private String url;
    private WebAgent webAgent;
    private String title;

    @Override
    public int getContentViewId() {
        return R.layout.fg_webview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.cityBean = (CityBean)getIntent().getSerializableExtra("cityBean");
        super.onCreate(savedInstanceState);
        initView();
    }

    public void setHeaderTitle(String title) {
        if (headerTitle != null) {
            headerTitle.setText(title);
        }
    }

    WebViewClient webClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (headerTitle != null && view != null && !TextUtils.isEmpty(view.getTitle())) {
                WebInfoActivity.this.title = view.getTitle();
                headerTitle.setText(view.getTitle());
                if (webAgent != null) {
                    webAgent.setTitle(view.getTitle());
                }
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                if (url.contains("/app/detail.html")) {
                    String goodsNo = CommonUtils.getUrlValue(url, "goodsNo");
                    if (TextUtils.isEmpty(goodsNo)) {
                        return false;
                    }
                    Intent intent = new Intent(WebInfoActivity.this, SkuDetailActivity.class);
                    intent.putExtra(WebInfoActivity.WEB_URL, url);
                    intent.putExtra(WebInfoActivity.CONTACT_SERVICE, true);
                    intent.putExtra(Constants.PARAMS_ID, goodsNo);
                    intent.putExtra(Constants.PARAMS_SOURCE, getEventSource());
                    WebInfoActivity.this.startActivity(intent);
                    return true;
                } else if (url.contains("tel:")) {
                    String mobile = url.substring(url.lastIndexOf("/") + 1);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));
                    WebInfoActivity.this.startActivity(intent);
                }

            }
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.cancel();
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
            MLog.e("WebResourceResponse1 =" + url);
            if (isHttps) {
                return processRequest(Uri.parse(url));
            } else {
                return null;
            }
        }

        @Override
        @TargetApi(21)
        public WebResourceResponse shouldInterceptRequest(final WebView view, WebResourceRequest interceptedRequest) {
            MLog.e("WebResourceResponse2 =" + interceptedRequest.getUrl());
            if (isHttps) {
                return processRequest(interceptedRequest.getUrl());
            } else {
                return null;
            }
        }

        private WebResourceResponse processRequest(Uri uri) {
            MLog.d("GET: " + uri.toString());
            try {
                // Setup connection
                URL url = new URL(uri.toString());
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                // Set SSL Socket Factory for this request
                urlConnection.setSSLSocketFactory(DefaultSSLSocketFactory.getSocketFactory(activity).getSslContext().getSocketFactory());
                // Get content, contentType and encoding
                InputStream is = urlConnection.getInputStream();
                String contentType = urlConnection.getContentType();
                String encoding = urlConnection.getContentEncoding();
                // If got a contentType header
                if (contentType != null) {
                    String mimeType = contentType;
                    // Parse mime type from contenttype string
                    if (contentType.contains(";")) {
                        mimeType = contentType.split(";")[0].trim();
                    }
                    Log.d("SSL_PINNING_WEBVIEWS", "Mime: " + mimeType);
                    // Return the response
                    return new WebResourceResponse(mimeType, encoding, is);
                }

            } catch (SSLHandshakeException e) {
                Log.d("SSL_PINNING_WEBVIEWS", e.getLocalizedMessage());
            } catch (Exception e) {
                Log.d("SSL_PINNING_WEBVIEWS", e.getLocalizedMessage());
            }
            // Return empty response for this request
            return new WebResourceResponse(null, null, null);
        }

    };

    WebChromeClient webChromeClient = new WebChromeClient() {


        @Override
        public void onReceivedTitle(WebView view, String _title) {
            super.onReceivedTitle(view, _title);
            if (headerTitle != null) {
                if (!view.getTitle().startsWith("http:") && !TextUtils.isEmpty(view.getTitle())) {
                    WebInfoActivity.this.title = view.getTitle();
                    headerTitle.setText(view.getTitle());
                    if (webAgent != null) {
                        webAgent.setTitle(view.getTitle());
                    }
                }
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            MLog.e("onJsAlert = " + message);
            mDialogUtil.showCustomDialog(message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            mDialogUtil.showCustomDialog(message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            return true;
        }
    };


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            webView.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(url) && url.contains("userId=") && isLogin != UserEntity.getUser().isLogin(this)) {
            isLogin = UserEntity.getUser().isLogin(this);
            url = CommonUtils.replaceUrlValue(url, "userId", UserEntity.getUser().getUserId(this));
            webView.loadUrl(url);
        }
    }

    public void initHeader() {
//        fgTitle.setTextColor(getResources().getColor(R.color.my_content_title_color));
//        fgTitle.setText("客服中心");
        headerLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
        if (this.getIntent().getBooleanExtra(CONTACT_SERVICE, false)) {
            RelativeLayout.LayoutParams headerRightImageParams = new RelativeLayout.LayoutParams(UIUtils.dip2px(38), UIUtils.dip2px(38));
            headerRightImageParams.rightMargin = UIUtils.dip2px(18);
            headerRightImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            headerRightImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
            headerRightBtn.setLayoutParams(headerRightImageParams);
            headerRightBtn.setPadding(0,0,0,0);
            headerRightBtn.setImageResource(R.mipmap.topbar_cs);
            headerRightBtn.setVisibility(View.VISIBLE);
            headerRightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtil.getInstance(activity).showCallDialog();
                }
            });
        }
    }

    public void initView() {
        // 启用javaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webAgent = new WebAgent(this, webView, cityBean, headerLeftBtn, TAG);
        webView.addJavascriptInterface(webAgent, "javaObj");
        webView.setOnKeyListener(this);
        webView.setWebViewClient(webClient);
        webView.setWebChromeClient(webChromeClient);
        webView.setBackgroundColor(0x00000000);
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + " HbcC/" + ChannelUtils.getVersion());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mDialogUtil = DialogUtil.getInstance(activity);
        initHeader();
        isLogin = UserEntity.getUser().isLogin(this);
        url = getIntent().getStringExtra(WEB_URL);
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("h5/cactivity/seckill") && UserEntity.getUser().isLogin(this)) {
                url = url + "&userId=" + UserEntity.getUser().getUserId(this);
            }
            webView.loadUrl(url);
        }
        MLog.e("url=" + url);

        SensorsUtils.setSensorsShowUpWebView(webView);
    }

    @Override
    public String getEventSource() {
        return TextUtils.isEmpty(title) ? "web页面" : title;
    }
}