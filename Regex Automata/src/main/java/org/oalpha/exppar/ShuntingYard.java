package org.oalpha.exppar;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static org.oalpha.exppar.Operator.AssociativeDirection.LEFT;
import static org.oalpha.exppar.Operator.AssociativeDirection.RIGHT;

;

public class ShuntingYard implements ExpressionParser {

	@Override
	public Token[] parseExpression(Token[] expression) {
		Stack<Token> stack = new Stack<>();
		List<Token> outList = new LinkedList<>();

		for (Token token : expression) {
			if (token.isArgument())
				outList.add(token);
			else if (token.isFunction())
				stack.push(token);
			else if (token.isFunctionArgumentSeparator()) {
				while (!stack.isEmpty() && !stack.peek().getText().equals("("))
					outList.add(stack.pop());
			} else if (token.isOperator()) {
				Operator op = token.getOperator();
				while (!stack.isEmpty() && stack.peek().isOperator()) {
					Operator top = stack.peek().getOperator();
					if ((op.getAssociativeDirection() == LEFT && op
							.getPrecedence() <= top.getPrecedence())
							|| (op.getAssociativeDirection() == RIGHT && op
									.getPrecedence() < top.getPrecedence())) {
						outList.add(stack.pop());
					} else
						break;
				}
				stack.push(token);
			} else if (token.getText().equals("("))
				stack.push(token);
			else if (token.getText().equals(")")) {
				while (!stack.isEmpty() && !stack.peek().getText().equals("("))
					outList.add(stack.pop());
				stack.pop();
				if (!stack.isEmpty() && stack.peek().isFunction())
					outList.add(stack.pop());
			}
		}

		while (!stack.isEmpty())
			outList.add(stack.pop());

		return outList.toArray(new Token[outList.size()]);
	}

	public static void main(String[] args) {
		SCATokenizer tkzr = new SCATokenizer();
		ShuntingYard sy = new ShuntingYard();
		String exp = "!3+4*!2/(1-5)^2!^3";
		Token[] postfix = sy.parseExpression(tkzr.tokenizeExpression(exp));
		System.out.print("infix: " + exp + "\npostfix:");
		for (Token token : postfix)
			System.out.print(" " + token.getText());
	}

}