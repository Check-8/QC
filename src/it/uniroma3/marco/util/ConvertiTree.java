package it.uniroma3.marco.util;

import java.util.ArrayList;
import java.util.List;

public class ConvertiTree {
	public String convertiDaNLP(String tree) {
		char[] treeArray = tree.toCharArray();
		SupportTree current = null;
		int uniqueTag = 0;
		for (int i = 0; i < treeArray.length; i++) {
			char c = treeArray[i];
			if (c == '(') {
				SupportTree temp = new SupportTree();
				temp.setParent(current);
				if (current != null)
					current.addFiglio(temp);
				current = temp;
				i++;
				c = treeArray[i];
				StringBuilder sb = new StringBuilder();
				do {
					sb.append(c);
					i++;
					c = treeArray[i];
				} while (c != ' ');
				current.setTag(sb.toString() + ":" + uniqueTag);
				uniqueTag++;
			}
			if (c == ')') {
				if (current.parent != null)
					current = current.parent;
			}
		}
		toRoot(current);
		return current.toString();
	}

	private SupportTree toRoot(SupportTree tree) {
		while (tree.parent != null) {
			tree = tree.parent;
		}
		return tree;
	}

	private class SupportTree {
		private String tagNodo;
		private List<SupportTree> figli;
		private SupportTree parent;

		public SupportTree() {
			figli = new ArrayList<>();
		}

		public void setTag(String tag) {
			tagNodo = tag;
		}

		public void addFiglio(SupportTree f) {
			figli.add(f);
		}

		public void setParent(SupportTree p) {
			parent = p;
		}

		public String toString() {
			StringBuilder s = new StringBuilder();
			for (SupportTree f : figli) {
				s.append(tagNodo + "-" + f.tagNodo + ";");
			}
			for (SupportTree f : figli) {
				s.append(f.toString());
			}
			return s.toString();
		}
	}
}
