/*
 * Copyright (c) 2010-present www.walkerljl.org All Rights Reserved.
 * The software source code all copyright belongs to the author, 
 * without permission shall not be any reproduction and transmission.
 */
package org.walkerljl.toolkit.cache;

import java.util.HashMap;
import java.util.Iterator;

/**
 * LFUCache
 * 最近最不常用
 * 首先淘汰访问次数最少的元素
 * @author lijunlin
 */
public class LFUCache<K,V> extends AbstractCacheMap<K,V> {

	public LFUCache(int maxSize) {
		this(maxSize, 0);
	}

	public LFUCache(int maxSize, long timeout) {
		this.cacheSize = maxSize;
		this.timeout = timeout;
		cacheMap = new HashMap<K, CacheObject<K,V>>(maxSize + 1);
	}

	/**
	 * Prunes expired and, if cache is still full, the LFU element(s) from the cache.
	 * On LFU removal, access count is normalized to value which had removed object.
	 * Returns the number of removed objects.
	 */
	@Override
	protected int pruneCache() {
        int count = 0;
		CacheObject<K,V> comin = null;

		// remove expired items and find cached object with minimal access count
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				onRemove(co.key, co.cachedObject);
				count++;
				continue;
			}
			
			if (comin == null) {
				comin = co;
			} else {
				if (co.accessCount < comin.accessCount) {
					comin = co;
				}
			}
		}

		if (!isFull()) {
			return count;
		}

		// decrease access count to all cached objects
		if (comin != null) {
			long minAccessCount = comin.accessCount;

			values = cacheMap.values().iterator();
			while (values.hasNext()) {
				CacheObject<K, V> co = values.next();
				co.accessCount -= minAccessCount;
				if (co.accessCount <= 0) {
					values.remove();
					onRemove(co.key, co.cachedObject);
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Callback method invoked on cached object removal.
	 * By default does nothing.
	 */
	protected void onRemove(K key, V cachedObject) {
		
	}
}