package org.oalpha.exppar;

import java.util.Stack;

public class ExpressionTreeNode {

	public static ExpressionTreeNode treeTokens(Token[] tokens) {
		Stack<ExpressionTreeNode> stack = new Stack<>();
		for (int i = 0; i < tokens.length; i++)
			if (tokens[i].isArgument())
				stack.push(new ExpressionTreeNode(tokens[i]));
			else if (tokens[i].isOperator()) {
				ExpressionTreeNode[] children = new ExpressionTreeNode[tokens[i]
						.getOperator().getArity()];
				for (int j = 0; j < children.length; j++)
					children[children.length - 1 - j] = stack.pop();
				stack.push(new ExpressionTreeNode(tokens[i], children));
			}
		return stack.pop();
	}

	private Token token;

	private ExpressionTreeNode[] children;

	public ExpressionTreeNode(Token token) {
		this(token, null);
	}

	public ExpressionTreeNode(Token token, ExpressionTreeNode[] children) {
		super();
		this.token = token;
		this.children = children == null ? new ExpressionTreeNode[0] : children;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public ExpressionTreeNode getChild() {
		return children[0];
	}

	public void setChild(ExpressionTreeNode child) {
		this.children[0] = child;
	}

	public ExpressionTreeNode getLeftChild() {
		return children[0];
	}

	public void setLeftChild(ExpressionTreeNode leftChild) {
		this.children[0] = leftChild;
	}

	public ExpressionTreeNode getRightChild() {
		return children[1];
	}

	public void setRightChild(ExpressionTreeNode rightChild) {
		this.children[1] = rightChild;
	}

	public int getNumChildren() {
		return children.length;
	}

	public ExpressionTreeNode[] getChildren() {
		return children;
	}

	public void setChildren(ExpressionTreeNode[] children) {
		this.children = children;
	}

	public void print() {
		print("", "");
	}

	public void print(String tab) {
		print(tab, tab);
	}

	public void print(String prefix, String tab) {
		switch (children.length) {
		case 0:
			System.out.println(prefix + token.getText());
			break;
		case 1:
			children[0].print(tab + token.getText() + '\t', tab + '\t');
			break;
		case 2:
			children[0].print(tab + '\t');
			System.out.println(prefix + token.getText());
			children[1].print(tab + '\t');
			break;
		}
	}

	public String getText() {
		return token.getText();
	}

	public Object getValue() {
		return token.getValue();
	}

	public Object getArgument() {
		return token.getArgument();
	}

	public Operator getOperator() {
		return token.getOperator();
	}

	public int getType() {
		return token.getType();
	}

	public boolean isArgument() {
		return token.isArgument();
	}

	public boolean isFunction() {
		return token.isFunction();
	}

	public boolean isFunctionArgumentSeparator() {
		return token.isFunctionArgumentSeparator();
	}

	public boolean isOperator() {
		return token.isOperator();
	}

	public boolean isParenthesis() {
		return token.isParenthesis();
	}

}