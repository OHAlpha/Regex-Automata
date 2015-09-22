package org.oalpha.exppar;

import static org.oalpha.exppar.Operator.AssociativeDirection.LEFT;
import static org.oalpha.exppar.Operator.AssociativeDirection.RIGHT;
import static org.oalpha.exppar.Operator.OperatorFixity.INFIX;
import static org.oalpha.exppar.Operator.OperatorFixity.POSTFIX;
import static org.oalpha.exppar.Token.ARGUMENT_TYPE;
import static org.oalpha.exppar.Token.OPERATOR_TYPE;
import static org.oalpha.exppar.Token.PARENTHESIS_TYPE;

public class SCATokenizer implements ExpressionTokenizer {

	Operator addition = new Operator(new String[] { "+" }, 2, INFIX, LEFT, 2,
			null);
	Operator subtraction = new Operator(new String[] { "-" }, 2, INFIX, LEFT,
			2, null);
	Operator multiplication = new Operator(new String[] { "*" }, 2, INFIX,
			LEFT, 3, null);
	Operator division = new Operator(new String[] { "/" }, 2, INFIX, LEFT, 3,
			null);
	Operator power = new Operator(new String[] { "^" }, 2, INFIX, RIGHT, 4,
			null);
	Operator factorial = new Operator(new String[] { "!" }, 1, POSTFIX, RIGHT,
			5, null);

	@Override
	public Token[] tokenizeExpression(String expression) {
		char[] chars = expression.toCharArray();
		Token[] tokens = new Token[chars.length];
		for (int i = 0; i < chars.length; i++)
			if (chars[i] >= '0' && chars[i] <= '9')
				tokens[i] = new Token("" + chars[i], chars[i] - '0',
						ARGUMENT_TYPE);
			else if (chars[i] == '(' || chars[i] == ')')
				tokens[i] = new Token("" + chars[i], null, PARENTHESIS_TYPE);
			else if (chars[i] == '+')
				tokens[i] = new Token("" + chars[i], addition, OPERATOR_TYPE);
			else if (chars[i] == '-')
				tokens[i] = new Token("" + chars[i], subtraction, OPERATOR_TYPE);
			else if (chars[i] == '*')
				tokens[i] = new Token("" + chars[i], multiplication,
						OPERATOR_TYPE);
			else if (chars[i] == '/')
				tokens[i] = new Token("" + chars[i], division, OPERATOR_TYPE);
			else if (chars[i] == '^')
				tokens[i] = new Token("" + chars[i], power, OPERATOR_TYPE);
			else if (chars[i] == '!')
				tokens[i] = new Token("" + chars[i], factorial, OPERATOR_TYPE);
		return tokens;
	}

}