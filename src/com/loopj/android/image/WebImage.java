package com.loopj.android.image;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WebImage implements SmartImage {
	private static final int CONNECT_TIMEOUT = 5000;
	private static final int READ_TIMEOUT = 10000;

	private static WebImageCache webImageCache;

	private final String url;
	private final String cookie;

	public WebImage(final String url, final String cookie) {
		this.url = url;
		this.cookie = cookie;
	}

	@Override
	public Bitmap getBitmap(final Context context) {
		// Don't leak context
		if (webImageCache == null) {
			webImageCache = new WebImageCache(context);
		}

		// Try getting bitmap from cache first
		Bitmap bitmap = null;
		if (url != null) {
			bitmap = webImageCache.get(url);
			if (bitmap == null) {
				bitmap = getBitmapFromUrl(url);
				if (bitmap != null) {
					webImageCache.put(url, bitmap);
				}
			}
		}

		return bitmap;
	}

	private Bitmap getBitmapFromUrl(final String url) {
		Bitmap bitmap = null;

		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			if (cookie != null)
				conn.setRequestProperty("Cookie", cookie);
			bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public static void removeFromCache(final String url) {
		if (webImageCache != null) {
			webImageCache.remove(url);
		}
	}
}
