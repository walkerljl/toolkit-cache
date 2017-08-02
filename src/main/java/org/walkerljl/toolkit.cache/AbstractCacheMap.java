/*
 * Copyright (c) 2010-present www.walkerljl.org All Rights Reserved.
 * The software source code all copyright belongs to the author, 
 * without permission shall not be any reproduction and transmission.
 */

package org.walkerljl.toolkit.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * AbstractCacheMap
 * 
 * @author lijunlin
 */
public abstract class AbstractCacheMap<K, V> implements Cache<K, V> {

	protected Map<K, CacheObject<K, V>> cacheMap;
	private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
	private final Lock readLock = cacheLock.readLock();
	private final Lock writeLock = cacheLock.writeLock();

	/** max cache size, 0 equals no limit*/
	protected int cacheSize;
	/** default timeout, 0 = no timeout*/
	protected long timeout;
	
	/**
	 * Identifies if objects has custom timeouts.
	 * Should be used to determine if prune for existing objects is needed.
	 */
	protected boolean existCustomTimeout;
	protected int hitCount;
	protected int missCount;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCacheSize() {
		return cacheSize;
	}    

	/**
	 * Returns default cache timeout or <code>0</code> if it is not set.
	 * Timeout can be set individually for each object.
	 */
	@Override
	public long getCacheTimeout() {
		return timeout;
	}

	/**
	 * Returns <code>true</code> if prune of expired objects should be invoked.
	 * For internal use.
	 */
	protected boolean isPruneExpiredActive() {
		return (timeout != 0) || existCustomTimeout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(K key, V object) {
		put(key, object, timeout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(K key, V object, long timeout) {
		writeLock.lock();

		try {
			CacheObject<K,V> co = new CacheObject<K,V>(key, object, timeout);
			if (timeout != 0) {
				existCustomTimeout = true;
			}
			if (isFull()) {
				pruneCache();
			}
			cacheMap.put(key, co);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V get(K key) {
		readLock.lock();

		try {
			CacheObject<K, V> co = cacheMap.get(key);
			if (co == null) {
				missCount++;
				return null;
			}
			if (co.isExpired()) {
				cacheMap.remove(key);
				missCount++;
				return null;
			}
			hitCount++;
			return co.getObject();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<V> iterator() {
		return new CacheValuesIterator<V>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int prune() {
		writeLock.lock();
		try {
			return pruneCache();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFull() {
		if (cacheSize == 0) {
			return false;
		}
		return cacheMap.size() >= cacheSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(K key) {
		writeLock.lock();
		try {
			cacheMap.remove(key);
		}
		finally {
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		writeLock.lock();
		try {
			cacheMap.clear();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return cacheMap.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * Returns hit count.
	 */
	public int getHitCount() {
		return hitCount;
	}

	/**
	 * Returns miss count.
	 */
	public int getMissCount() {
		return missCount;
	}
	
	/**
	 * Prune implementation.
	 */
	protected abstract int pruneCache();
	
	/**
	 * CacheObject
	 * 
	 * @author lijunlin
	 */
	class CacheObject<K2, V2> {
		final K2 key;
		final V2 cachedObject;
		/** time of last access*/
		long lastAccess;
		/** number of accesses*/
		long accessCount;
		/** objects timeout (time-to-live),0 equals no timeout*/
		long ttl;
		
		CacheObject(K2 key, V2 object, long ttl) {
			this.key = key;
			this.cachedObject = object;
			this.ttl = ttl;
			this.lastAccess = System.currentTimeMillis();
		}

		boolean isExpired() {
			if (ttl == 0) {
				return false;
			}
			return lastAccess + ttl < System.currentTimeMillis();
		}
		
		V2 getObject() {
			lastAccess = System.currentTimeMillis();
			accessCount++;
			return cachedObject;
		}
    }
}