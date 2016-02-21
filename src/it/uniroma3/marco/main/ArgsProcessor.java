package it.uniroma3.marco.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ArgsProcessor {
	public static final String TRAINSET_KEY = "key_train";
	public static final String TESTSET_KEY = "key_test";
	public static final String TAGGED_TRAINSET_KEY = "key_tag_train";
	public static final String TAGGED_TESTSET_KEY = "key_tag_test";
	public static final String TREE_TRAINSET_KEY = "key_tree_train";
	public static final String TREE_TESTSET_KEY = "key_tree_test";
	public static final String KNN_K = "key_knn";

	private static final String DEFAULT_TRAININGSET_PATH = "res/train.txt";
	private static final String DEFAULT_TESTSET_PATH = "res/test.txt";

	private static final String TAGGED_TRAININGSET_PATH = "res/taggedTrain.txt";
	private static final String TAGGED_TESTSET_PATH = "res/taggedTest.txt";

	private static final String TREE_TRAININGSET_PATH = "res/treeTrain.txt";
	private static final String TREE_TESTSET_PATH = "res/treeTest.txt";

	private static final String DEFAULT_KNN = "5";

	private static final String TRAIN_PARAM = "-train";
	private static final String TEST_PARAM = "-test";
	private static final String KNN_PARAM = "-k";

	private static final String MSG_ERROR_DEFAULT = "Usato valore di default.";
	private static final String MSG_ERROR_INVALID_PATH = "[path] non valido.";
	private static final String MSG_ERROR_INVALID_K = "[val] deve essere un intero positivo";
	private static final String MSG_INCORRECT = "Uso corretto: ";
	private Map<String, String> prop;

	public ArgsProcessor() {
		prop = new HashMap<>();
		prop.put(TRAINSET_KEY, DEFAULT_TRAININGSET_PATH);
		prop.put(TESTSET_KEY, DEFAULT_TESTSET_PATH);

		prop.put(TAGGED_TRAINSET_KEY, TAGGED_TRAININGSET_PATH);
		prop.put(TAGGED_TESTSET_KEY, TAGGED_TESTSET_PATH);

		prop.put(TREE_TRAINSET_KEY, TREE_TRAININGSET_PATH);
		prop.put(TREE_TESTSET_KEY, TREE_TESTSET_PATH);

		prop.put(KNN_K, DEFAULT_KNN);
	}

	public void processaArgomenti(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals(TRAIN_PARAM)) {
				i++;
				if (i >= args.length) {
					System.err.println(MSG_INCORRECT + TRAIN_PARAM + " [path]");
					System.err.println(MSG_ERROR_DEFAULT);
				} else {
					String temp = args[i];
					File file = new File(temp);
					if (file.exists() && file.canRead())
						setProp(TRAINSET_KEY, temp);
					else {
						System.err.println(MSG_ERROR_INVALID_PATH + " " + MSG_ERROR_DEFAULT);
					}
				}
			}
			if (arg.equals(TEST_PARAM)) {
				i++;
				if (i >= args.length) {
					System.err.println(MSG_INCORRECT + TEST_PARAM + " [path]");
					System.err.println(MSG_ERROR_DEFAULT);
				} else {
					String temp = args[i];
					File file = new File(temp);
					if (file.exists() && file.canRead())
						setProp(TESTSET_KEY, temp);
					else {
						System.err.println(MSG_ERROR_INVALID_PATH + " " + MSG_ERROR_DEFAULT);
					}
				}
			}
			if (arg.equals(KNN_PARAM)) {
				i++;
				if (i >= args.length) {
					System.err.println(MSG_INCORRECT + TEST_PARAM + " [val]");
					System.err.println(MSG_ERROR_DEFAULT);
				} else {
					String temp = args[i];
					try {
						Integer num = Integer.parseInt(temp);
						if (num <= 0)
							throw new NumberFormatException("Numero negativo o zero");
						setProp(KNN_K, temp);
					} catch (NumberFormatException e) {
						System.err.println(MSG_ERROR_INVALID_K + " " + MSG_ERROR_DEFAULT);
					}
				}
			}
		}
	}

	private void setProp(String key, String value) {
		if (key == null || value == null)
			return;
		prop.put(key, value);
	}

	public String getProp(String key) {
		if (key == null)
			return null;
		return prop.get(key);
	}
}
