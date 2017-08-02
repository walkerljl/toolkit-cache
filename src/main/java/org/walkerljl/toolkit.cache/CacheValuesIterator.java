/*
 * Copyright (c) 2010-present www.walkerljl.org All Rights Reserved.
 * The software source code all copyright belongs to the author, 
 * without permission shall not be any reproduction and transmission.
 */
package org.walkerljl.toolkit.cache;

import java.util.Iterator;

/**
 * CacheValuesIterator
 * 
 * @author lijunlin
 */
public class CacheValuesIterator<V> implements Iterator<V> {

	private Iterator<? extends AbstractCacheMap<?, V>.CacheObject<?, V>> iterator;
	private AbstractCacheMap<?,V>.CacheObject<?,V> nextValue;
	
	CacheValuesIterator(AbstractCacheMap<?,V> abstractCacheMap) {
		iterator = abstractCacheMap.cacheMap.values().iterator();
		nextValue();
	}
	
	/**
	 * Resolves next value. If next value doesn't exist, next value will be <code>null</code>.
	 */
	private void nextValue() {
		while (iterator.hasNext()) {
			nextValue = iterator.next();
			if (!nextValue.isExpired()) {
				return;
			}
		}
		nextValue = null;
	}
	
	/**
	 * Returns <code>true</code> if there are more elements in the cache.
	 */
	@Override
	public boolean hasNext() {
		return nextValue != null;
	}

	/**
	 * Returns next non-expired element from the cache.
	 */
	@Override
	public V next() {
		V cachedObject = nextValue.cachedObject;
		nextValue();
		return cachedObject;
	}

	/**
	 * Removes current non-expired element from the cache.
	 */
	@Override
	public void remove() {
		iterator.remove();
	}
}