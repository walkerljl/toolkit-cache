/*
 * Copyright (c) 2010-present www.walkerljl.org All Rights Reserved.
 * The software source code all copyright belongs to the author, 
 * without permission shall not be any reproduction and transmission.
 */
package org.walkerljl.toolkit.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRUCache
 * 最近最少使用
 * 首先淘汰最长时间未被使用的元素
 * @author lijunlin
 */
public class LRUCache<K, V> extends AbstractCacheMap<K, V> {

	public LRUCache(int cacheSize) {
		this(cacheSize, 0);
	}

	/**
	 * Creates a new LRU cache.
	 */
	public LRUCache(int cacheSize, long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new LinkedHashMap<K, CacheObject<K,V>>(cacheSize + 1, 1.0f, true) {
			private static final long serialVersionUID = 1L;
			@Override
			protected boolean removeEldestEntry(Map.Entry<K, CacheObject<K,V>> eldest) {
				return LRUCache.this.removeEldestEntry(size());
			}
		};
	}

	/**
	 * Removes the eldest entry if current cache size exceed cache size.
	 */
	protected boolean removeEldestEntry(int currentSize) {
		if (cacheSize == 0) {
			return false;
		}
		return currentSize > cacheSize;
	}

	/**
	 * Prune only expired objects, <code>LinkedHashMap</code> will take care of LRU if needed.
	 */
	@Override
	protected int pruneCache() {
		if (!isPruneExpiredActive()) {
			return 0;
		}
        int count = 0;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				count++;
			}
		}
		return count;
	}
}