package org.oalpha.collection;

import java.util.Map;
import java.util.NavigableMap;

public class MapMorphism<A, B> implements Morphism<A, B> {

	private Map<A, B> mA;

	private Map<B, A> mB;

	public MapMorphism(NavigableMap<A, B> mA, NavigableMap<B, A> mB) {
		this.mA = mA;
		this.mB = mB;
	}

	@Override
	public B mapA(A a) {
		return mA.get(a);
	}

	@Override
	public A mapB(B b) {
		return mB.get(b);
	}

}