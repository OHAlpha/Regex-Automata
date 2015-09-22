package org.oalpha.regex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.oalpha.automata.NFAAutomata;
import org.oalpha.collection.Morphism;
import org.oalpha.exppar.ExpressionTokenizer;
import org.oalpha.exppar.ExpressionTreeNode;
import org.oalpha.exppar.Operator;
import org.oalpha.exppar.ShuntingYard;
import org.oalpha.exppar.Token;

import static org.oalpha.exppar.Operator.AssociativeDirection.LEFT;
import static org.oalpha.exppar.Operator.OperatorFixity.INFIX;
import static org.oalpha.exppar.Operator.OperatorFixity.POSTFIX;
import static org.oalpha.exppar.Token.ARGUMENT_TYPE;
import static org.oalpha.exppar.Token.OPERATOR_TYPE;
import static org.oalpha.exppar.Token.PARENTHESIS_TYPE;

public class RegexAnalyzer {

	public static class CharacterArrayIterator implements Iterator<Character> {

		private char[] arr;

		private int index = 0;

		public CharacterArrayIterator(char[] arr) {
			super();
			this.arr = arr;
		}

		@Override
		public boolean hasNext() {
			return index < arr.length;
		}

		@Override
		public Character next() {
			return new Character(arr[index++]);
		}

	}

	public static class IShift implements Morphism<Integer, Integer> {

		private final Integer nullValue;

		private final Integer shift;

		public IShift(Integer nullValue, Integer shift) {
			super();
			this.nullValue = nullValue;
			this.shift = shift;
		}

		@Override
		public Integer mapA(Integer a) {
			return a == null ? nullValue : a + shift;
		}

		@Override
		public Integer mapB(Integer b) {
			return b.equals(nullValue) ? null : b - shift;
		}

	}

	public static final Operator union = new Operator(new String[] { "|" }, 2,
			INFIX, LEFT, 1, null);

	public static final Operator concatenation = new Operator(
			new String[] { "." }, 2, INFIX, LEFT, 2, null);

	public static final Operator kleeneQuestion = new Operator(
			new String[] { "?" }, 1, POSTFIX, LEFT, 3, null);

	public static final Operator kleeneStar = new Operator(
			new String[] { "*" }, 1, POSTFIX, LEFT, 3, null);

	public static final Operator kleenePlus = new Operator(
			new String[] { "+" }, 1, POSTFIX, LEFT, 3, null);

	public static class RegexTokenizer implements ExpressionTokenizer {

		@Override
		public Token[] tokenizeExpression(String expression) {
			char[] Characters = expression.toCharArray();
			LinkedList<Token> tokens = new LinkedList<>();
			for (int i = 0; i < Characters.length; i++) {
				if (Characters[i] == '|')
					tokens.add(new Token("" + Characters[i], union,
							OPERATOR_TYPE));
				else if (Characters[i] == '?')
					tokens.add(new Token("" + Characters[i], kleeneQuestion,
							OPERATOR_TYPE));
				else if (Characters[i] == '*')
					tokens.add(new Token("" + Characters[i], kleeneStar,
							OPERATOR_TYPE));
				else if (Characters[i] == '+')
					tokens.add(new Token("" + Characters[i], kleenePlus,
							OPERATOR_TYPE));
				else if (Characters[i] == ')')
					tokens.add(new Token("" + Characters[i], null,
							PARENTHESIS_TYPE));
				else if (Characters[i] == '\\') {
					i++;
					if (!tokens.isEmpty()
							&& (tokens.peekLast().isArgument()
									|| tokens.peekLast().getText().equals(")") || tokens
									.peekLast().getText().equals("*")))
						tokens.add(new Token(".", concatenation, OPERATOR_TYPE));
					tokens.add(new Token("" + Characters[i], Characters[i],
							ARGUMENT_TYPE));
				} else if (Characters[i] == '(') {
					if (!tokens.isEmpty()
							&& (tokens.peekLast().isArgument()
									|| tokens.peekLast().getText().equals(")") || tokens
									.peekLast().getText().equals("*")))
						tokens.add(new Token(".", concatenation, OPERATOR_TYPE));
					tokens.add(new Token("" + Characters[i], null,
							PARENTHESIS_TYPE));
				} else {
					if (!tokens.isEmpty()
							&& (tokens.peekLast().isArgument()
									|| tokens.peekLast().getText().equals(")") || tokens
									.peekLast().getText().equals("*")))
						tokens.add(new Token(".", concatenation, OPERATOR_TYPE));
					tokens.add(new Token("" + Characters[i], Characters[i],
							ARGUMENT_TYPE));
				}
			}
			return tokens.toArray(new Token[tokens.size()]);
		}

	}

	public static final ShuntingYard parser = new ShuntingYard();

	public static final RegexTokenizer tokenizer = new RegexTokenizer();

	public static ExpressionTreeNode parseRegex(String regex) {
		Token[] tokens = parser.parseExpression(tokenizer
				.tokenizeExpression(regex));
		return ExpressionTreeNode.treeTokens(tokens);
	}

	public static NFAAutomata<Integer, Character> convertTree(
			ExpressionTreeNode root) {
		NFAAutomata<Integer, Character> automata = new NFAAutomata<>(0,
				Integer.MAX_VALUE);
		if (root.isOperator()) {
			if (root.getOperator() == union) {
				NFAAutomata<Integer, Character> left = convertTree(root
						.getLeftChild());
				NFAAutomata<Integer, Character> right = convertTree(root
						.getRightChild());
				automata.addStates(left, iRange(1, left.size()));
				automata.addStates(right,
						iRange(left.size() + 1, left.size() + right.size()));
				automata.linkStates(0, 1, null);
				automata.linkStates(0, left.size() + 1, null);
				automata.linkStates(left.size(), Integer.MAX_VALUE, null);
				automata.linkStates(left.size() + right.size(),
						Integer.MAX_VALUE, null);
			} else if (root.getOperator() == concatenation) {
				NFAAutomata<Integer, Character> left = convertTree(root
						.getLeftChild());
				NFAAutomata<Integer, Character> right = convertTree(root
						.getRightChild());
				automata.addStates(left, iRange(1, left.size()));
				automata.addStates(right,
						iRange(left.size() + 1, left.size() + right.size()));
				automata.mergeStates(1, 0);
				automata.mergeStates(left.size() + 1, left.size());
				automata.mergeStates(left.size() + right.size(),
						Integer.MAX_VALUE);
			} else if (root.getOperator() == kleeneQuestion) {
				NFAAutomata<Integer, Character> child = convertTree(root
						.getChild());
				automata.addStates(child, iRange(1, child.size()));
				automata.linkStates(0, 1, null);
				automata.linkStates(child.size(), Integer.MAX_VALUE, null);
				automata.linkStates(0, Integer.MAX_VALUE, null);
			} else if (root.getOperator() == kleeneStar) {
				NFAAutomata<Integer, Character> child = convertTree(root
						.getChild());
				automata.addStates(child, iRange(1, child.size()));
				automata.linkStates(0, 1, null);
				automata.linkStates(child.size(), Integer.MAX_VALUE, null);
				automata.linkStates(0, Integer.MAX_VALUE, null);
			} else if (root.getOperator() == kleenePlus) {
				NFAAutomata<Integer, Character> child = convertTree(root
						.getChild());
				automata.addStates(child, iRange(1, child.size()));
				automata.linkStates(0, 1, null);
				automata.linkStates(child.size(), Integer.MAX_VALUE, null);
				automata.linkStates(child.size(), 1, null);
			} else
				return null;
			return automata;
		} else {
			automata.linkStates(0, Integer.MAX_VALUE, new Character(root
					.getText().charAt(0)));
			return automata;
		}
	}

	private static NavigableSet<Integer> iRange(int min, int max) {
		NavigableSet<Integer> range = new TreeSet<>();
		for (int i = min; i <= max; i++)
			range.add(i);
		return range;
	}

	public static void main(String[] args) {
		test("test", new String[] {
				// should accept
				"test",
				// should reject
				"", "tes", "tast", "testy" });
		// test("(0|(1(01*(00)*0)*1)*)*", new String[] {
		// // should accept
		// "", "0", "00", "11", "000", "011", "110", "0000", "0011",
		// "0110", "1001", "1100", "1111", "00000",
		// // should reject
		// "111", "1000", "ab" });
	}

	public static void test(String exp, String[] queries) {
		Token[] tokens = tokenizer.tokenizeExpression(exp);
		System.out.print("expression: " + exp + "\ntoken:");
		for (Token token : tokens)
			System.out.print(" " + token.getText());
		System.out.print("\npostfix:");
		Token[] postfix = parser.parseExpression(tokens);
		for (Token token : postfix)
			System.out.print(" " + token.getText());
		System.out.println();
		ExpressionTreeNode tree = ExpressionTreeNode.treeTokens(postfix);
		tree.print();
		NFAAutomata<Integer, Character> nfa = convertTree(tree);
		nfa.list();
		for (String query : queries) {
			System.out.println("query: \"" + query + '\"');
			System.out.println("accepted?: "
					+ nfa.evalutae(new CharacterArrayIterator(query
							.toCharArray())));
		}
	}

}