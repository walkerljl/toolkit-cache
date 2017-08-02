/*
 * Copyright (c) 2010-present www.walkerljl.org All Rights Reserved.
 * The software source code all copyright belongs to the author, 
 * without permission shall not be any reproduction and transmission.
 */
package org.walkerljl.toolkit.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * FileLFUCache
 * 
 * @author lijunlin
 */
public class FileLFUCache {

	protected final LFUCache<File, byte[]> cache;
	protected final int maxSize;
	protected final int maxFileSize;

	protected int usedSize;

	/**
	 * Creates file LFU cache with specified size. Sets
	 * {@link #maxFileSize max available file size} to half of this value.
	 */
	public FileLFUCache(int maxSize) {
		this(maxSize, maxSize / 2, 0);
	}

	public FileLFUCache(int maxSize, int maxFileSize) {
		this(maxSize, maxFileSize, 0);
	}

	/**
	 * Creates new File LFU cache.
	 * @param maxSize total cache size in bytes
	 * @param maxFileSize max available file size in bytes, may be 0
	 * @param timeout timeout, may be 0
	 */
	public FileLFUCache(int maxSize, int maxFileSize, long timeout) {
		this.cache = new LFUCache<File, byte[]>(0, timeout) {
			@Override
			public boolean isFull() {
				return usedSize > FileLFUCache.this.maxSize;
			}

			@Override
			protected void onRemove(File key, byte[] cachedObject) {
				usedSize -= cachedObject.length;
			}

		};
		this.maxSize = maxSize;
		this.maxFileSize = maxFileSize;
	}

	/**
	 * Returns max cache size in bytes.
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Returns actually used size in bytes.
	 */
	public int getUsedSize() {
		return usedSize;
	}

	/**
	 * Returns maximum allowed file size that can be added to the cache.
	 * Files larger than this value will be not added, even if there is
	 * enough room.
	 */
	public int getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Returns number of cached files.
	 */
	public int getCachedFilesCount() {
		return cache.size();
	}

	/**
	 * Returns timeout.
	 */
	public long getCacheTimeout() {
		return cache.getCacheTimeout();
	}

	/**
	 * Clears the cache.
	 */
	public void clear() {
		cache.clear();
		usedSize = 0;
	}

	public byte[] getFileBytes(String fileName) throws IOException {
		return getFileBytes(new File(fileName));
	}

	/**
	 * Returns cached file bytes.
	 */
	public byte[] getFileBytes(File file) throws IOException {
		byte[] bytes = cache.get(file);
		if (bytes != null) {
			return bytes;
		}

		// add file
		bytes = readBytes(file);

		if ((maxFileSize != 0) && (file.length() > maxFileSize)) {
			// don't cache files that size exceed max allowed file size
			return bytes;
		}

		usedSize += bytes.length;

		// put file into cache
		// if used size > total, purge() will be invoked
		cache.put(file, bytes);

		return bytes;
	}

	private static byte[] readBytes(File file) throws IOException {
		if (file.exists() == false) {
			throw new FileNotFoundException("Not found:" + file);
		}
		if (file.isFile() == false) {
			throw new IOException("Not a file: " + file);
		}
		long len = file.length();
		if (len >= Integer.MAX_VALUE) {
			throw new IOException("File is larger then max array size");
		}

		byte[] bytes = new byte[(int) len];
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.readFully(bytes);
		randomAccessFile.close();

		return bytes;
	}

}