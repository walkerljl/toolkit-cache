/*
 * Copyright (c) 2010-present www.walkerljl.org All Rights Reserved.
 * The software source code all copyright belongs to the author, 
 * without permission shall not be any reproduction and transmission.
 */
package org.walkerljl.toolkit.cache;

import java.util.LinkedHashMap;
import java.util.Iterator;

/**
 * FIFOCache
 * 
 * @author lijunlin
 */
public class FIFOCache<K, V> extends AbstractCacheMap<K, V> {

	public FIFOCache(int cacheSize) {
		this(cacheSize, 0);
	}

	/**
	 * Creates a new LRU cache.
	 */
	public FIFOCache(int cacheSize, long timeout) {
		this.cacheSize = cacheSize;
		this.timeout = timeout;
		cacheMap = new LinkedHashMap<K, CacheObject<K,V>>(cacheSize + 1, 1.0f, false);
	}

	/**
	 * Prune expired objects and, if cache is still full, the first one.
	 */
	@Override
	protected int pruneCache() {
        int count = 0;
		CacheObject<K,V> first = null;
		Iterator<CacheObject<K,V>> values = cacheMap.values().iterator();
		while (values.hasNext()) {
			CacheObject<K,V> co = values.next();
			if (co.isExpired()) {
				values.remove();
				count++;
			}
			if (first == null) {
				first = co;
			}
		}
		if (isFull()) {
			if (first != null) {
				cacheMap.remove(first.key);
				count++;
			}
		}
		return count;
	}
}