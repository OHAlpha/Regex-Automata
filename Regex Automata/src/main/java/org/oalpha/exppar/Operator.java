package org.oalpha.exppar;

public class Operator {

	public static enum AssociativeDirection {

		LEFT, RIGHT;

	}

	public static enum OperatorFixity {

		PREFIX, POSTFIX, INFIX, OUTFIX, MIXFIX;

	}

	private String[] identifiers;

	private int arity;

	private OperatorFixity fixity;

	private AssociativeDirection associativeDirection;

	private int precedence;

	private Operator[] hasPrecedenceOver;

	public Operator(String identifier) {
		this(new String[] { identifier }, 2, OperatorFixity.INFIX,
				AssociativeDirection.LEFT, 0, null);
	}

	public Operator(String[] identifiers, int arity, OperatorFixity fixity,
			AssociativeDirection associativeDirection, int precedence,
			Operator[] hasPrecedenceOver) {
		super();
		this.identifiers = identifiers;
		this.arity = arity;
		this.fixity = fixity;
		this.associativeDirection = associativeDirection;
		this.precedence = precedence;
		this.hasPrecedenceOver = hasPrecedenceOver;
	}

	public String[] getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(String[] identifiers) {
		this.identifiers = identifiers;
	}

	public int getArity() {
		return arity;
	}

	public void setArity(int arity) {
		this.arity = arity;
	}

	public OperatorFixity getFixity() {
		return fixity;
	}

	public void setFixity(OperatorFixity fixity) {
		this.fixity = fixity;
	}

	public AssociativeDirection getAssociativeDirection() {
		return associativeDirection;
	}

	public void setAssociativeDirection(
			AssociativeDirection associativeDirection) {
		this.associativeDirection = associativeDirection;
	}

	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	public Operator[] getHasPrecedenceOver() {
		return hasPrecedenceOver;
	}

	public void setHasPrecedenceOver(Operator[] hasPrecedenceOver) {
		this.hasPrecedenceOver = hasPrecedenceOver;
	}

}