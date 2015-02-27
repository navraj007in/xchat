package com.kss.xchat.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MeetOptionContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<MeetOption> ITEMS = new ArrayList<MeetOption>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, MeetOption> ITEM_MAP = new HashMap<String, MeetOption>();

	static {
		// Add 3 sample items.
		addItem(new MeetOption("1", "Newly Joined Users"));
		addItem(new MeetOption("2", "Currently Active Users"));
		addItem(new MeetOption("3", "Active Chatrooms"));
	}

	private static void addItem(MeetOption item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class MeetOption {
		public String id;
		public String content;

		public MeetOption(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
