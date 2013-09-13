package com.giniem.gindpubs.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import com.giniem.gindpubs.R;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends Fragment {

	public static final String ARG_OBJECT = "object";
	
	private CustomWebView webView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View customView;
    public CustomChromeClient chromeClient = new CustomChromeClient();
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// The last two arguments ensure LayoutParams are inflated
		// properly.
		View rootView = inflater.inflate(R.layout.fragment_collection_object,
				container, false);
		Bundle args = getArguments();

		customViewContainer = (FrameLayout) this.getActivity().findViewById(R.id.customViewContainer);
		
		webView = (CustomWebView) rootView.findViewById(R.id.webpage1);
		webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDefaultZoom(
                WebSettings.ZoomDensity.MEDIUM);
        webView.getSettings().getDefaultZoom();
		webView.setWebChromeClient(chromeClient);
		webView.loadUrl(args.getString(ARG_OBJECT));

		return rootView;
	}

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.getWebView().destroy();
    }
	
	public String getUrl() {
		return this.webView.getUrl();
	}
	
	public boolean inCustomView() {
        return (customView != null);
    }

    public void hideCustomView() {
    	chromeClient.onHideCustomView();
    }

    public CustomWebView getWebView() {
        return this.webView;
    }

	class CustomChromeClient extends WebChromeClient {

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
           onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view,CustomViewCallback callback) {

            if (customView != null) {
                callback.onCustomViewHidden();
                return;
            }
            customView = view;
            webView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (customView == null)
                return;

            webView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            // Hide the custom view.
            customView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(customView);
            customViewCallback.onCustomViewHidden();

            customView = null;
        }
    }
}