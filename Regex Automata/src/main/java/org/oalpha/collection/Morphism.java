package org.oalpha.collection;

public interface Morphism<A, B> {
	
	B mapA(A a);
	
	A mapB(B b);

}