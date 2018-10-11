package me.mastercapexd.guiitemgenerator.util;

public final class CooldownChecker {

	private long startTimeMillis;

	public CooldownChecker() {
		this.reset();
	}

	public void reset() {
		this.startTimeMillis = System.currentTimeMillis();
	}

	public long getMillisPassed() {
		return (System.currentTimeMillis() - this.startTimeMillis);
	}

	public boolean isPassed(long time) {
		return this.getMillisPassed() >= time;
	}
}