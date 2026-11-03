package com.dimaxstudio.horario;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.JavascriptInterface;
import android.webkit.CookieManager;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private WebView webView;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        webView = new WebView(this);
        webView.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.addJavascriptInterface(new Object() { @JavascriptInterface public void noop() {} }, "HorarioBridge");
        try (InputStream in = getAssets().open("index.html")) {
            byte[] bytes = new byte[in.available()];
            int offset = 0, count;
            while (offset < bytes.length && (count = in.read(bytes, offset, bytes.length-offset)) > 0) offset += count;
            webView.loadDataWithBaseURL("https://horario.dimaxstudio.app/", new String(bytes, StandardCharsets.UTF_8), "text/html", "UTF-8", null);
        } catch (Exception ex) { webView.loadUrl("about:blank"); }
        setContentView(webView);
    }
    @Override public void onBackPressed() {
        webView.evaluateJavascript("androidBack()", value -> {
            if ("false".equals(value)) {
                new android.app.AlertDialog.Builder(this).setMessage("¿Salir de Horario?").setNegativeButton("Cancelar", (d,w)->{}).setPositiveButton("Salir", (d,w)->finish()).show();
            }
        });
    }
    @Override protected void onDestroy() { if (webView != null) webView.destroy(); super.onDestroy(); }
}
