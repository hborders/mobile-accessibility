package com.github.hborders.guidebookaccessibilityservice;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class GuidebookAccessibilityService extends AccessibilityService {
	private final static String TAG = GuidebookAccessibilityService.class
			.getName();

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		Log.d(TAG, "Started");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "Stopped");

		return super.onUnbind(intent);
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
		final int eventType = accessibilityEvent.getEventType();
		String eventText = null;
		switch (eventType) {
		case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
			eventText = "Hover Enter: ";
			break;
		default:
			eventText = "Unknown type (code " + eventType + "): ";
			break;
		}

		eventText = eventText + accessibilityEvent.getContentDescription();
		Log.d(TAG, eventText);

		AccessibilityNodeInfo sourceAccessibilityNodeInfo = accessibilityEvent
				.getSource();
		Log.d(TAG, "Source: " + sourceAccessibilityNodeInfo);

		if (sourceAccessibilityNodeInfo != null) {
			AccessibilityNodeInfo backwardAccessibilityNodeInfo = null;
			for (int i = 0; i < 5; i++) {
				backwardAccessibilityNodeInfo = sourceAccessibilityNodeInfo
						.findFocus(View.FOCUS_BACKWARD);
				if (backwardAccessibilityNodeInfo == null) {
					break;
				} else {
					Log.d(TAG, "" + backwardAccessibilityNodeInfo.getText());
				}
			}

			if ((backwardAccessibilityNodeInfo != null)
					&& "General Info".equals(backwardAccessibilityNodeInfo
							.getText())) {
				Log.d(TAG, "Found the stars label");
			}
		}

		AccessibilityNodeInfo rootAccessibilityNodeInfo = getRootInActiveWindow();
		if (rootAccessibilityNodeInfo != null) {
			List<AccessibilityNodeInfo> clickableAccessibilityNodeInfos = clickableAccessibilityNodeInfosOfAccessibilityNodeInfo(rootAccessibilityNodeInfo);
			
			if (clickableAccessibilityNodeInfos.indexOf(sourceAccessibilityNodeInfo) == 3) {
				Log.d(TAG, "Hovered on Stars Button");
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
			clickableAccessibilityNodeInfos.addAll(childClickableAccessibilityNodeInfos);
		}
		
		return clickableAccessibilityNodeInfos;
	}

	@Override
	public void onInterrupt() {
	}
}
