package org.oalpha.exppar;

public class Token {

	public static final int ARGUMENT_TYPE = 0;

	public static final int FUNCTION_TYPE = 1;

	public static final int FUNCTION_ARGUMENT_SEPARATOR_TYPE = 2;

	public static final int OPERATOR_TYPE = 3;

	public static final int PARENTHESIS_TYPE = 4;

	private final String text;

	private final Object value;

	private final int type;

	public Token(String text, Object value, int type) {
		super();
		this.text = text;
		this.value = value;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public Object getValue() {
		return value;
	}

	public Object getArgument() {
		return (Object) value;
	}

	public Operator getOperator() {
		return (Operator) value;
	}

	public int getType() {
		return type;
	}

	public boolean isArgument() {
		return type == ARGUMENT_TYPE;
	}

	public boolean isFunction() {
		return type == FUNCTION_TYPE;
	}

	public boolean isFunctionArgumentSeparator() {
		return type == FUNCTION_ARGUMENT_SEPARATOR_TYPE;
	}

	public boolean isOperator() {
		return type == OPERATOR_TYPE;
	}

	public boolean isParenthesis() {
		return type == PARENTHESIS_TYPE;
	}

}