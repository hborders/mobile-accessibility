package com.github.hborders.guidebookaccessibilityservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class GuidebookAccessibilityService extends AccessibilityService {
	private final static String TAG = GuidebookAccessibilityService.class
			.getName();

	private boolean textToSpeechInitialized;

	private TextToSpeech textToSpeech;

	private boolean expectingViewClicked;
	private boolean expectingViewHoverExit;
	private boolean expectingViewHoverEnter;

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		Log.v(TAG, "Started");
		textToSpeech = new TextToSpeech(getApplicationContext(),
				new OnInitListener() {
					@Override
					public void onInit(int status) {
						if (status == TextToSpeech.SUCCESS) {
							Log.d(TAG, "TextToSpeech initialized");
							textToSpeech.setLanguage(Locale.US);
							textToSpeechInitialized = true;
						} else {
							Log.e(TAG, "TextToSpeech initialization failed");
						}
					}
				});
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(TAG, "Stopped");

		return super.onUnbind(intent);
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
		Log.v(TAG, "Event: " + accessibilityEvent);

		if ((accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_HOVER_ENTER)
				|| (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_HOVER_EXIT)
				|| (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)) {
			AccessibilityNodeInfo sourceAccessibilityNodeInfo = accessibilityEvent
					.getSource();
			Log.v(TAG, "Source: " + sourceAccessibilityNodeInfo);

			AccessibilityNodeInfo rootAccessibilityNodeInfo = getRootInActiveWindow();
			Log.v(TAG, "Root: " + rootAccessibilityNodeInfo);
			if (rootAccessibilityNodeInfo != null) {
				if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_HOVER_ENTER) {
					if (expectingViewHoverEnter) {
						Log.d(TAG, "Got the expected hover enter");
						expectingViewHoverEnter = false;
					} else if (textToSpeechInitialized) {
						List<AccessibilityNodeInfo> clickableAccessibilityNodeInfos = clickableAccessibilityNodeInfosOfAccessibilityNodeInfo(rootAccessibilityNodeInfo);

						if (clickableAccessibilityNodeInfos
								.indexOf(sourceAccessibilityNodeInfo) == 3) {
							Log.d(TAG, "Hovered on Stars Button, clicking it");
							expectingViewClicked = true;
							sourceAccessibilityNodeInfo
									.performAction(AccessibilityNodeInfo.ACTION_CLICK);
						}
					} else {
						Log.d(TAG, "Text to speech not initialized");
					}
				} else if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_HOVER_EXIT) {
					if (expectingViewHoverExit) {
						expectingViewHoverExit = false;

						List<AccessibilityNodeInfo> clickableAccessibilityNodeInfos = clickableAccessibilityNodeInfosOfAccessibilityNodeInfo(rootAccessibilityNodeInfo);
						if (clickableAccessibilityNodeInfos.size() >= 4) {
							AccessibilityNodeInfo starsButtonAccessibilityInfo = clickableAccessibilityNodeInfos
									.get(3);
							expectingViewHoverEnter = true;
							Log.d(TAG,
									"Got the expected hover exit, focusing the stars button again");
							starsButtonAccessibilityInfo
									.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
						}
					}
				} else {
					if (expectingViewClicked) {
						expectingViewClicked = false;
						List<AccessibilityNodeInfo> overallAccessibilityNodeInfos = rootAccessibilityNodeInfo
								.findAccessibilityNodeInfosByText("Overall");
						if (!overallAccessibilityNodeInfos.isEmpty()) {
							AccessibilityNodeInfo overallAccessibilityNodeInfo = overallAccessibilityNodeInfos
									.get(0);
							Log.i(TAG, "overall: "
									+ overallAccessibilityNodeInfo.getText());
							if (overallAccessibilityNodeInfo.getText() != null) {
								textToSpeech.speak(
										overallAccessibilityNodeInfo.getText().toString(),
										TextToSpeech.QUEUE_FLUSH, null);
							}
							expectingViewHoverExit = true;
							Log.d(TAG,
									"Got the expected clicked. Grabbed the overall data and dismissing the popover");
							performGlobalAction(GLOBAL_ACTION_BACK);
						}
					}
				}
			}
		}
	}

	private ArrayList<AccessibilityNodeInfo> clickableAccessibilityNodeInfosOfAccessibilityNodeInfo(
			AccessibilityNodeInfo accessibilityNodeInfo) {
		ArrayList<AccessibilityNodeInfo> clickableAccessibilityNodeInfos = new ArrayList<AccessibilityNodeInfo>();
		if (accessibilityNodeInfo.isClickable()) {
			clickableAccessibilityNodeInfos.add(accessibilityNodeInfo);
		}
		for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
			AccessibilityNodeInfo childAccessibilityNodeInfo = accessibilityNodeInfo
					.getChild(i);
			ArrayList<AccessibilityNodeInfo> childClickableAccessibilityNodeInfos = clickableAccessibilityNodeInfosOfAccessibilityNodeInfo(childAccessibilityNodeInfo);
			clickableAccessibilityNodeInfos
					.addAll(childClickableAccessibilityNodeInfos);
		}

		return clickableAccessibilityNodeInfos;
	}

	private void printAccessibilityNodeInfoTree(
			AccessibilityNodeInfo accessibilityNodeInfo, String prefix) {
		Log.v(TAG, prefix + accessibilityNodeInfo);
		String childPrefix = prefix + "\t";
		for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
			AccessibilityNodeInfo childAccessibilityNodeInfo = accessibilityNodeInfo
					.getChild(i);
			printAccessibilityNodeInfoTree(childAccessibilityNodeInfo,
					childPrefix);
		}
	}

	@Override
	public void onInterrupt() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (textToSpeechInitialized) {
			textToSpeech.shutdown();
		}
	}
}
