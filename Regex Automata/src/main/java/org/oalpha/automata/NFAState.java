package org.oalpha.automata;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class NFAState<I extends Comparable<I>, T> implements
		Comparable<NFAState<I, T>> {

	public static class KeyWrapper<T> implements Comparable<KeyWrapper<T>> {

		private final Comparator<T> keyComparator;

		private T c;

		public KeyWrapper() {
			this(new Comparator<T>() {

				@Override
				@SuppressWarnings("unchecked")
				public int compare(T a, T b) {
					return a == null ? b == null ? 0 : -1 : b == null ? 1
							: ((Comparable<T>) a).compareTo(b);
				}

			});
		}

		public KeyWrapper(T c) {
			this(new Comparator<T>() {

				@Override
				@SuppressWarnings("unchecked")
				public int compare(T a, T b) {
					return a == null ? b == null ? 0 : -1 : b == null ? 1
							: ((Comparable<T>) a).compareTo(b);
				}

			}, c);
		}

		public KeyWrapper(T c, boolean isEmpty) {
			this(new Comparator<T>() {

				@Override
				@SuppressWarnings("unchecked")
				public int compare(T a, T b) {
					return a == null ? b == null ? 0 : -1 : b == null ? 1
							: ((Comparable<T>) a).compareTo(b);
				}

			}, c, isEmpty);
		}

		public KeyWrapper(Comparator<T> comparator) {
			keyComparator = comparator;
			this.c = null;
		}

		public KeyWrapper(Comparator<T> comparator, T c) {
			keyComparator = comparator;
			this.c = c;
		}

		public KeyWrapper(Comparator<T> comparator, T c, boolean isEmpty) {
			keyComparator = comparator;
			this.c = isEmpty ? null : c;
		}

		public void setKey(T c) {
			this.c = c;
		}

		public T getKey() {
			return c;
		}

		public boolean isEmpty() {
			return c == null;
		}

		public void setEmpty() {
			this.c = null;
		}

		@Override
		public int compareTo(KeyWrapper<T> o) {
			return c == null ? o.c == null ? 0 : -1 : o.c == null ? 1
					: keyComparator.compare(c, o.c);
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof KeyWrapper<?> ? equals((KeyWrapper<?>) o)
					: false;
		}

		public boolean equals(KeyWrapper<?> o) {
			return c == null && o.c == null || c.equals(o.c);
		}

		public String toString() {
			return c == null ? "empty" : "" + (char) c;
		}

	}

	@SuppressWarnings("unused")
	private final Comparator<T> keyComparator;

	private final NavigableMap<KeyWrapper<T>, NavigableSet<NFAState<I, T>>> transitionsTo;

	private final NavigableMap<KeyWrapper<T>, NavigableSet<NFAState<I, T>>> transitionsFrom;

	private final I index;

	public NFAState(I index) {
		this(index, new Comparator<T>() {

			@Override
			@SuppressWarnings("unchecked")
			public int compare(T a, T b) {
				return ((Comparable<T>) a).compareTo(b);
			}

		});
	}

	public NFAState(I index, Comparator<T> comparator) {
		this.keyComparator = comparator;
		this.index = index;
		transitionsTo = new TreeMap<>();
		transitionsFrom = new TreeMap<>();
	}

	@Override
	public int compareTo(NFAState<I, T> o) {
		return index.compareTo(o.index);
	}

	public I getIndex() {
		return index;
	}

	public void link(T key, NFAState<I, T> next) {
		if (next == null || key == null)
			throw new NullPointerException();
		NavigableSet<NFAState<I, T>> set = null;
		if (transitionsTo.containsKey(new KeyWrapper<T>(key)))
			set = transitionsTo.get(new KeyWrapper<T>(key));
		else {
			set = new TreeSet<>();
			transitionsTo.put(new KeyWrapper<T>(key), set);
		}
		set.add(next);
		if (next.transitionsFrom.containsKey(new KeyWrapper<T>(key)))
			set = next.transitionsFrom.get(new KeyWrapper<T>(key));
		else {
			set = new TreeSet<>();
			next.transitionsFrom.put(new KeyWrapper<T>(key), set);
		}
		set.add(this);
	}

	public void unlink(T key, NFAState<I, T> next) {
		if (next == null || key == null)
			throw new NullPointerException();
		NavigableSet<NFAState<I, T>> set = null;
		if (transitionsTo.containsKey(key)) {
			set = transitionsTo.get(key);
			set.remove(next);
			if (set.isEmpty())
				transitionsTo.remove(key);
		}
		if (next.transitionsFrom.containsKey(key)) {
			set = next.transitionsFrom.get(key);
			set.remove(this);
			if (set.isEmpty())
				next.transitionsFrom.remove(key);
		}
	}

	public NavigableSet<NFAState<I, T>> getLinked(T key) {
		return transitionsTo.get(new KeyWrapper<T>(key));
	}

	public NavigableSet<NFAState<I, T>> getLinkedFrom(T key) {
		return transitionsFrom.get(new KeyWrapper<T>(key));
	}

	public NavigableSet<T> getTransitions() {
		NavigableSet<T> out = new TreeSet<>();
		for (KeyWrapper<T> key : transitionsTo.navigableKeySet())
			out.add(key.getKey());
		return out;
	}

	public NavigableSet<T> getFromTransitions() {
		NavigableSet<T> out = new TreeSet<>();
		for (KeyWrapper<T> key : transitionsFrom.navigableKeySet())
			out.add(key.getKey());
		return out;
	}

}