package org.oalpha.automata;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.oalpha.collection.MapMorphism;
import org.oalpha.collection.Morphism;

public class NFAAutomata<I extends Comparable<I>, T> {

	private NavigableMap<I, NFAState<I, T>> states = new TreeMap<>();

	public NFAAutomata(I start, I end) {
		states.put(start, new NFAState<I, T>(start));
		states.put(end, new NFAState<I, T>(end));
	}

	public int size() {
		return states.size();
	}

	public void addState(I index) {
		if (states.firstKey().compareTo(index) >= 0
				|| states.lastKey().compareTo(index) <= 0)
			throw new IllegalArgumentException();
		states.put(index, new NFAState<I, T>(index));
	}

	public Morphism<I, I> addStates(NFAAutomata<I, T> automata,
			NavigableSet<I> range) {
		NavigableMap<I, I> mA = new TreeMap<>(), mB = new TreeMap<>();
		for (I index : automata.states.keySet()) {
			I nextI = range.first();
			range.remove(nextI);
			mA.put(index, nextI);
			mB.put(nextI, index);
			states.put(nextI, new NFAState<I, T>(nextI));
		}
		Morphism<I, I> map = new MapMorphism<I, I>(mA, mB);
		for (I index : automata.states.keySet()) {
			NFAState<I, T> stateA = automata.states.get(index);
			NFAState<I, T> stateB = states.get(map.mapA(index));
			for (T transition : stateA.getTransitions())
				for (NFAState<I, T> next : stateA.getLinked(transition))
					try {
						I mapped = map.mapA(next.getIndex());
						stateB.link(transition, states.get(mapped));
					} catch (NullPointerException e) {
						System.err.println("transition: " + transition
								+ ", nextIndex: " + next.getIndex());
						throw e;
					}
		}
		return map;
	}

	public NFAState<I, T> removeState(I index) {
		return states.remove(index);
	}

	public NFAState<I, T> getState(I index) {
		return states.get(index);
	}

	public void linkStates(I from, I to, T transition) {
		if (from == null || to == null || transition == null)
			throw new NullPointerException();
		states.get(from).link(transition, states.get(to));
	}

	public void unlinkStates(I from, I to, T transition) {
		if (from == null || to == null || transition == null)
			throw new NullPointerException();
		states.get(from).unlink(transition, states.get(to));
	}

	public void mergeStates(I sourceIndex, I destIndex) {
		NFAState<I, T> source = states.get(sourceIndex), dest = states
				.get(destIndex);
		for (T transition : source.getTransitions()) {
			for (NFAState<I, T> next : source.getLinked(transition)) {
				dest.link(transition, next);
			}
		}
		for (T transition : source.getFromTransitions()) {
			for (NFAState<I, T> from : source.getLinkedFrom(transition)) {
				from.link(transition, dest);
			}
		}
		states.remove(destIndex);
	}

	public NFAState<I, T> getInitialState() {
		return states.firstEntry().getValue();
	}

	public NFAState<I, T> getFinalState() {
		return states.lastEntry().getValue();
	}

	public I getStart() {
		return states.firstKey();
	}

	public I getEnd() {
		return states.lastKey();
	}

	public boolean evalutae(Iterator<T> iterator) {
		System.out.print("keys:");
		for (I index : states.keySet())
			System.out.print(" " + index);
		System.out.println();
		int it = 0;
		NavigableSet<I> staging = new TreeSet<>();
		NavigableSet<I> state = new TreeSet<>();
		staging.add(states.firstKey());
		do {
			while (!staging.isEmpty()) {
				I index = staging.first();
				staging.remove(index);
				NFAState<I, T> cur = states.get(index);
				try {
					NavigableSet<NFAState<I, T>> ns = cur.getLinked(null);
					if (ns != null)
						for (NFAState<I, T> next : ns) {
							I nextIndex = next.getIndex();
							if (!(state.contains(nextIndex)
									|| staging.contains(nextIndex) || index
										.equals(nextIndex)))
								staging.add(next.getIndex());
						}
				} catch (NullPointerException e) {
					System.err.println("index: " + index);
					// e.printStackTrace();
					throw e;
				}
				state.add(index);
			}
			System.out.print("\titeration " + it + ":");
			for (I index : state)
				System.out.print(" " + index);
			System.out.println();
			it++;
			if (!iterator.hasNext())
				return state.contains(states.lastKey());
			if (state.isEmpty())
				return false;
			T transition = iterator.next();
			for (I index : state) {
				NFAState<I, T> cur = states.get(index);
				NavigableSet<NFAState<I, T>> ns = cur.getLinked(transition);
				if (ns != null)
					for (NFAState<I, T> next : ns)
						staging.add(next.getIndex());
			}
			state.clear();
		} while (true);
	}

	public void list() {
		for (I index : states.keySet()) {
			NFAState<I, T> state = states.get(index);
			System.out.println("state " + index + ":");
			for (T transition : state.getTransitions()) {
				System.out.print("\ttransition " + transition + ":");
				for (NFAState<I, T> next : state.getLinked(transition))
					System.out.print(" " + next.getIndex());
				System.out.println();
			}
		}
	}

}