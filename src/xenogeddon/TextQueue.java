package xenogeddon;

import java.util.ArrayList;

public final class TextQueue extends ArrayList<String> implements IUpdating {

	private static final long DURATION = 2000;

	private int pos = 0;
	private long remove_at = 0;

	public TextQueue() {

	}
	
	public void resetPos() {
		pos = 0;
	}

	public String getString(boolean trunc) {
		if (this.size() > 0) {
			if (trunc) {
				int len = pos;
				if (len > this.get(0).toString().length()) {
					len = this.get(0).toString().length();
				}
				return this.get(0).toString().substring(0, len);
			} else {
				return this.get(0).toString();
			}
		} else {
			return "";
		}
	}

	public void update(float interpolation) {
		if (this.remove_at > 0 && this.size() > 0) {
			if (System.currentTimeMillis() > this.remove_at) {
				this.remove_at = 0;
				if (this.size() > 1) {
					this.remove(0);
					pos = 0;
				}
			}
		} else {
			pos++;
			if (pos > this.getString(false).length()) {
				this.remove_at = System.currentTimeMillis() + DURATION;
			}
		}
	}

}
