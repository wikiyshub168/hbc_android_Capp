package com.hugboga.custom.fragment;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huangbaoche.hbcframe.data.net.DefaultSSLSocketFactory;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.request.RequestWebInfo;
import com.hugboga.custom.widget.DialogUtil;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

/**
 * Created by ZHZEPHI on 2015/7/31.
 */
@ContentView(R.layout.fg_webview)
public class FgWebInfo extends BaseFragment implements View.OnKeyListener {

    public static final String Web_URL = "web_url";
    public boolean isHttps = false;
    private DialogUtil mDialogUtil;

    @ViewInject(R.id.webview)
    WebView webView;

    class javaObj {

        @JavascriptInterface
        public void requestUrl(String type, String url, String callback) {
            loadRequest(type, url, null, callback);
        }

        @JavascriptInterface
        public void requestBody(String url, String requestBody, String callback) {
            loadRequest("body", url, requestBody, callback);
        }
    }
    //请求
    private void loadRequest(final String type, final String url, final String requestBody, final String callback){
        MLog.e("url = "+url);
        MLog.e("type = "+type);
        MLog.e("requestBody = "+requestBody);
        MLog.e("callback = "+callback);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestWebInfo requestWebInfo = new RequestWebInfo(getActivity(),url, type, requestBody, callback);
                requestData(requestWebInfo);
            }
        });

    }

    WebViewClient webClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
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
            MLog.d( "GET: " + uri.toString());
            try {
                // Setup connection
                URL url = new URL(uri.toString());
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                // Set SSL Socket Factory for this request
                urlConnection.setSSLSocketFactory(DefaultSSLSocketFactory.getSocketFactory(getActivity()).getSslContext().getSocketFactory());
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
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            fgTitle.setText(view.getTitle());
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
    protected int getBusinessType() {
        return Constants.BUSINESS_TYPE_OTHER;
    }

    @Override
    protected void inflateContent() {
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        if(request instanceof RequestWebInfo){
            RequestWebInfo requestWebInfo = (RequestWebInfo)request;
            String callback="javascript:" + requestWebInfo.callBack + "('" + requestWebInfo.getData()+ "')";
            MLog.e("callback="+callback);
            webView.loadUrl(callback);
        }
    }

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
        webView.destroy();
        super.onDestroy();
    }

    @Override
    protected void initHeader() {
//        fgTitle.setTextColor(getResources().getColor(R.color.my_content_title_color));
//        fgTitle.setText("客服中心");
    }

    @Override
    protected void initView() {
        // 启用javaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.addJavascriptInterface(new javaObj(), "javaObj");
        webView.setOnKeyListener(this);
        webView.setWebViewClient(webClient);
        webView.setWebChromeClient(webChromeClient);
        webView.setBackgroundColor(0x00000000);
        mDialogUtil =  DialogUtil.getInstance(getActivity());
    }

    @Override
    protected Callback.Cancelable requestData() {
        String url = getArguments().getString(Web_URL);
//        url = "https://res2.huangbaoche.com/h5/gmsg/message.html?messageId=100068621&guideId=200000001445";
//        url="https://www.baidu.com";
        if (!TextUtils.isEmpty(url)) {
            MLog.e("url=" + url);
         /*   if (url.startsWith("https://")) {
                isHttps = true;
            } else {
                isHttps = false;
            }*/
            webView.loadUrl(url);
        }
//        webView.loadUrl("http://res.dev.hbc.tech/h5/greg/index.html");
        return null;
    }
}