package org.oalpha.collection;

import java.util.Map.Entry;

public class TEntry<K, V> implements Entry<K, V> {

	private K key;

	private V value;

	public TEntry(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V out = this.value;
		this.value = value;
		return out;
	}

}