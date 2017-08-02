/*
 * Copyright (c) 2010-present www.walkerljl.org All Rights Reserved.
 * The software source code all copyright belongs to the author, 
 * without permission shall not be any reproduction and transmission.
 */
package org.walkerljl.toolkit.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimedCache
 * 
 * @author lijunlin
 */
public class TimedCache<K, V> extends AbstractCacheMap<K, V> {

	public TimedCache(long timeout) {
		this.cacheSize = 0;
		this.timeout = timeout;
		cacheMap = new HashMap<K, CacheObject<K,V>>();
	}

	/**
	 * Prunes expired elements from the cache. Returns the number of removed objects.
	 */
	@Override
	protected int pruneCache() {
        int count = 0;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K, V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				count++;
			}
		}
		return count;
	}

	protected Timer pruneTimer;

	/**
	 * Schedules prune.
	 */
	public void schedulePrune(long delay) {
		if (pruneTimer != null) {
			pruneTimer.cancel();
		}
		pruneTimer = new Timer();
		pruneTimer.schedule(
				new TimerTask() {
					@Override
					public void run() {
						prune();
					}
				}, delay, delay
		);
	}

	/**
	 * Cancels prune schedules.
	 */
	public void cancelPruneSchedule() {
		if (pruneTimer != null) {
			pruneTimer.cancel();
			pruneTimer = null;
		}
	}
}