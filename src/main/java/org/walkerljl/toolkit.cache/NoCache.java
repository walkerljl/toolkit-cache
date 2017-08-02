/*
 * Copyright (c) 2010-present www.walkerljl.org All Rights Reserved.
 * The software source code all copyright belongs to the author, 
 * without permission shall not be any reproduction and transmission.
 */
package org.walkerljl.toolkit.cache;

import java.util.Iterator;

/**
 * Simple no-cache implementations of {@link Cache} for situation when cache
 * needs to be quickly turned-off.
 * 
 * @author lijunlin
 */
public class NoCache<K, V> implements Cache<K, V> {

	@Override
	public int getCacheSize() {
		return 0;
	}

	@Override
	public long getCacheTimeout() {
		return 0;
	}

	@Override
	public void put(K key, V object) {
		// ignore
	}

	@Override
	public void put(K key, V object, long timeout) {
		// ignore
	}

	@Override
	public V get(K key) {
		return null;
	}

	@Override
	public Iterator<V> iterator() {
		return null;
	}

	@Override
	public int prune() {
		return 0;
	}

	@Override
	public boolean isFull() {
		return true;
	}

	@Override
	public void remove(K key) {
		// ignore
	}

	@Override
	public void clear() {
		// ignore
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}