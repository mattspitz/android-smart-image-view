package com.loopj.android.image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SmartImageView extends ImageView {
	private static final int LOADING_THREADS = 4;
	private static ExecutorService threadPool = Executors.newFixedThreadPool(LOADING_THREADS);

	private SmartImageTask currentTask;

	public SmartImageView(final Context context) {
		super(context);
	}

	public SmartImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public SmartImageView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	// Helpers to set image by URL
	public void setImageUrl(final String url, final String cookie) {
		setImage(new WebImage(url, cookie));
	}

	public void setImageUrl(final String url, final String cookie, final SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url, cookie), completeListener);
	}

	public void setImageUrl(final String url, final String cookie, final Integer fallbackResource) {
		setImage(new WebImage(url, cookie), fallbackResource);
	}

	public void setImageUrl(final String url, final String cookie, final Integer fallbackResource, final SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url, cookie), fallbackResource, completeListener);
	}

	public void setImageUrl(final String url, final String cookie, final Integer fallbackResource, final Integer loadingResource) {
		setImage(new WebImage(url, cookie), fallbackResource, loadingResource);
	}

	public void setImageUrl(final String url, final String cookie, final Integer fallbackResource, final Integer loadingResource, final SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url, cookie), fallbackResource, loadingResource, completeListener);
	}

	public void setImageUrl(final String url) {
		setImageUrl(url, (String) null);
	}

	public void setImageUrl(final String url, final SmartImageTask.OnCompleteListener completeListener) {
		setImageUrl(url, (String) null, completeListener);
	}

	public void setImageUrl(final String url, final Integer fallbackResource) {
		setImageUrl(url, (String) null, fallbackResource);
	}

	public void setImageUrl(final String url, final Integer fallbackResource, final SmartImageTask.OnCompleteListener completeListener) {
		setImageUrl(url, (String) null, fallbackResource, completeListener);
	}

	public void setImageUrl(final String url, final Integer fallbackResource, final Integer loadingResource) {
		setImageUrl(url, (String) null, fallbackResource, loadingResource);
	}

	public void setImageUrl(final String url, final Integer fallbackResource, final Integer loadingResource, final SmartImageTask.OnCompleteListener completeListener) {
		setImageUrl(url, (String) null, fallbackResource, loadingResource, completeListener);
	}

	// Helpers to set image by contact address book id
	public void setImageContact(final long contactId) {
		setImage(new ContactImage(contactId));
	}

	public void setImageContact(final long contactId, final Integer fallbackResource) {
		setImage(new ContactImage(contactId), fallbackResource);
	}

	public void setImageContact(final long contactId, final Integer fallbackResource, final Integer loadingResource) {
		setImage(new ContactImage(contactId), fallbackResource, fallbackResource);
	}

	// Set image using SmartImage object
	public void setImage(final SmartImage image) {
		setImage(image, null, null, null);
	}

	public void setImage(final SmartImage image, final SmartImageTask.OnCompleteListener completeListener) {
		setImage(image, null, null, completeListener);
	}

	public void setImage(final SmartImage image, final Integer fallbackResource) {
		setImage(image, fallbackResource, fallbackResource, null);
	}

	public void setImage(final SmartImage image, final Integer fallbackResource, final SmartImageTask.OnCompleteListener completeListener) {
		setImage(image, fallbackResource, fallbackResource, completeListener);
	}

	public void setImage(final SmartImage image, final Integer fallbackResource, final Integer loadingResource) {
		setImage(image, fallbackResource, loadingResource, null);
	}

	public void setImage(final SmartImage image, final Integer fallbackResource, final Integer loadingResource, final SmartImageTask.OnCompleteListener completeListener) {
		// Set a loading resource
		if (loadingResource != null) {
			setImageResource(loadingResource);
		}

		// Cancel any existing tasks for this image view
		if (currentTask != null) {
			currentTask.cancel();
			currentTask = null;
		}

		// Set up the new task
		currentTask = new SmartImageTask(getContext(), image);
		currentTask.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
			@Override
			public void onComplete(final Bitmap bitmap) {
				if (bitmap != null) {
					setImageBitmap(bitmap);
				} else {
					// Set fallback resource
					if (fallbackResource != null) {
						setImageResource(fallbackResource);
					}
				}

				if (completeListener != null) {
					completeListener.onComplete();
				}
			}
		});

		// Run the task in a threadpool
		threadPool.execute(currentTask);
	}

	public static void cancelAllTasks() {
		threadPool.shutdownNow();
		threadPool = Executors.newFixedThreadPool(LOADING_THREADS);
	}
}