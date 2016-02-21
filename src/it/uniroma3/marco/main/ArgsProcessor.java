package it.uniroma3.marco.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ArgsProcessor {
	public static final String TRAINSET_KEY = "key_train";
	public static final String TESTSET_KEY = "key_test";
	public static final String TAGGED_TRAINSET_KEY = "key_tag_train";
	public static final String TAGGED_TESTSET_KEY = "key_tag_train";
	public static final String TREE_TRAINSET_KEY = "key_tree_train";
	public static final String TREE_TESTSET_KEY = "key_tree_train";

	private static final String DEFAULT_TRAININGSET_PATH = "train.txt";
	private static final String DEFAULT_TESTSET_PATH = "test.txt";

	private static final String TAGGED_TRAININGSET_PATH = "taggedTrain.txt";
	private static final String TAGGED_TESTSET_PATH = "taggedTest.txt";

	private static final String TREE_TRAININGSET_PATH = "treeTrain.txt";
	private static final String TREE_TESTSET_PATH = "treeTest.txt";

	private static final String TRAIN_PARAM = "-train";
	private static final String TEST_PARAM = "-test";

	private static final String MSG_ERROR_DEFAULT = "Usato valore di default.";
	private static final String MSG_ERROR_INVALID_PATH = "[path] non valido.";
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
