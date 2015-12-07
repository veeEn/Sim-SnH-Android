package in.simhas.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int TIME_GAP = 350;
    protected WebView webView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    // This event handler is to handle the 'back button' touch events.
    // Without this method, the application closes when the user touches 'back button'.
    private long lastPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://www.simhas.in/?source=androidv0.1Beta";

        webView = (WebView) findViewById(R.id.canvas);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient() {
            // this method prevents opening the links in the default browser.
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Open the web URL's with thin the app
                if (url.startsWith("http://www.simhas.in") || url.startsWith("https://www.simhas.in")) {
                    view.loadUrl(url);
                    return false;
                }

                // Otherwise allow the OS to handle things like tel, mailto, etc.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCod, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(),
                        "It looks like the internet connection is down, or\n" + description, Toast.LENGTH_LONG).show();

                String errorMessage = "<html><center><b>Couldn't reach Simha's servers</b><br/>" +
                        "Please check your internet connection.<br/><br/>" +
                        "If you believe the internet connection is fine, <br/>please reach out to us on " +
                        "<a href=\"tel:+919964585787\" target=\"_blank\">+91 9964 585 787</a>.</center></html>";
                webView.loadData(errorMessage, "text/html", null);
            }
        });
        webView.loadUrl(url);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Log.v("MainActivity", "You have just swiped down.");
                webView.loadUrl("javascript:window.location.reload(true)");

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (event.getDownTime() - lastPressedTime < TIME_GAP) { // If double tapped
                        finish();                                               // then close the app
                    } else {                                                // If it's not a double tap
                        if (webView.canGoBack()) {                              // and if there's a back page,
                            webView.goBack();                                       // then go to the back page.
                            lastPressedTime = event.getEventTime();
                        } else {                                                // and if there's no back page
                            Toast.makeText(getApplicationContext(),                 // then show the closing tip
                                    "Tap twice to confirm exit.", Toast.LENGTH_SHORT).show();
                            lastPressedTime = event.getEventTime();                 // and set lastPressedTime to analyse the following tap
                        }
                    }
                    return true;
            }
        }
        return false;
    }
}
