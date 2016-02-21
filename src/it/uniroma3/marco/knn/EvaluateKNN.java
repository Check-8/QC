package it.uniroma3.marco.knn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import it.uniroma3.marco.util.Logger;
import it.uniroma3.marco.util.Util;
import it.uniroma3.marco.weka.WekaAbstract;

public class EvaluateKNN {
	private static final String[] CLASS_COARSE = WekaAbstract.CLASS_COARSE;
	private static final double NANO = 1000000000;
	private KNN knn;

	public EvaluateKNN() {
		knn = new KNN();
	}

	public String valuta(String pathTrain, String pathTest) {
		StringBuilder s = new StringBuilder();
		s.append("KNN" + "\n\t" + "Albero sintattico" + "\n");
		try {
			double[][] confMatrix = new double[CLASS_COARSE.length][CLASS_COARSE.length];
			long tStart = System.nanoTime();
			knn.training(pathTrain);
			long tStartEvaluation = System.nanoTime();

			List<String> testSet = getTestSet(pathTest);
			for (String test : testSet) {
				String classe = Util.soloTesta(test);
				int index = classe.lastIndexOf(":");
				if (index > 0)
					classe = classe.substring(0, index);
				String daValutare = Util.eliminaTesta(test);
				String classeTrovata = knn.determinaClasse(5, daValutare);
				int j = indexOfClass(classeTrovata);
				int i = indexOfClass(classe);
				confMatrix[i][j] += 1;
			}
			long tEndClassification = System.nanoTime();

			// String strSummary = eval.toSummaryString();
			// s.append(strSummary + "\n");

			String cmString = matrixToString(confMatrix);
			s.append(cmString + "\n");

			String rpfString = recall_precision_fMeasureToString(confMatrix);
			s.append(rpfString + "\n");

			long dTotal = (tEndClassification - tStart);
			long dEval = (tEndClassification - tStartEvaluation);
			long dAddes = (tStartEvaluation - tStart);
			String formatTotTime = timeFormat(dTotal);
			String formatEvaTime = timeFormat(dEval);
			String formatAddTime = timeFormat(dAddes);
			s.append("Durata addestramento            : " + formatAddTime + "\n");
			s.append("Durata valutazione              : " + formatEvaTime + "\n");
			s.append("Durata addestramento+valutazione: " + formatTotTime + "\n");

			Logger log = Logger.instance;
			log.log("KNN" + " - " + "Albero sintattico");
			log.log(cmString);
			log.log(rpfString);
			log.log("Durata addestramento            : " + formatAddTime);
			log.log("Durata valutazione              : " + formatEvaTime);
			log.log("Durata addestramento+valutazione: " + formatTotTime);
			log.log("---\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s.toString();
	}

	private List<String> getTestSet(String pathTest) {
		List<String> testSet = new ArrayList<>();
		try (FileReader fr = new FileReader(pathTest); Scanner scan = new Scanner(fr)) {
			while (scan.hasNextLine()) {
				String sentence = scan.nextLine();
				testSet.add(sentence);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testSet;
	}

	private String recall_precision_fMeasureToString(double[][] confMatrix) {
		StringBuilder s = new StringBuilder();
		String dFormat = "%1$1.6f";
		Locale en = Locale.ENGLISH;
		s.append(" Class | Precision|  Recall  | fMeasure |\n");
		for (int i = 0; i < CLASS_COARSE.length; i++) {
			s.append(String.format(en, "%1$6s", CLASS_COARSE[i]) + " | ");
			s.append(String.format(en, dFormat, precision(i, confMatrix)) + " | ");
			s.append(String.format(en, dFormat, recall(i, confMatrix)) + " | ");
			s.append(String.format(en, dFormat, fMeasure(i, confMatrix)) + " |\n");
		}

		s.append("Precision complessiva: ");
		s.append(String.format(en, dFormat, globalPrecision(confMatrix)) + "\n");
		s.append("Recall complessiva   : ");
		s.append(String.format(en, dFormat, globalRecall(confMatrix)) + "\n");
		s.append("fMeasure complessiva : ");
		s.append(String.format(en, dFormat, globalFMeasure(confMatrix)) + "\n");
		return s.toString();
	}

	private double globalFMeasure(double[][] confMatrix) {
		double val = 0;
		for (int i = 0; i < confMatrix.length; i++) {
			val += fMeasure(i, confMatrix);
		}
		val /= confMatrix.length;
		return val;
	}

	private double globalRecall(double[][] confMatrix) {
		double val = 0;
		for (int i = 0; i < confMatrix.length; i++) {
			val += recall(i, confMatrix);
		}
		val /= confMatrix.length;
		return val;
	}

	private double globalPrecision(double[][] confMatrix) {
		double val = 0;
		for (int i = 0; i < confMatrix.length; i++) {
			val += precision(i, confMatrix);
		}
		val /= confMatrix.length;
		return val;
	}

	private double fMeasure(int i, double[][] confMatrix) {
		double val = 0;
		double p = precision(i, confMatrix);
		double r = recall(i, confMatrix);
		val = (2 * p * r) / (p + r);
		return val;
	}

	private double recall(int i, double[][] confMatrix) {
		double val = 0;
		for (int c = 0; c < confMatrix.length; c++) {
			val += confMatrix[i][c];
		}
		val = confMatrix[i][i] / val;
		return val;
	}

	private double precision(int i, double[][] confMatrix) {
		double val = 0;
		for (int c = 0; c < confMatrix.length; c++) {
			val += confMatrix[c][i];
		}
		val = confMatrix[i][i] / val;
		return val;
	}

	private String matrixToString(double[][] matrix) {
		StringBuilder s = new StringBuilder();
		s.append("     ");
		for (String cc : CLASS_COARSE) {
			s.append(String.format(Locale.ENGLISH, "%1$5s ", cc));
		}
		s.append("\n");
		int i = 0;
		for (double[] dd : matrix) {
			s.append(String.format(Locale.ENGLISH, "%1$4s", CLASS_COARSE[i]));
			s.append("[");
			boolean flag = true;
			for (double d : dd) {
				if (flag) {
					flag = false;
				} else
					s.append(",");
				s.append(String.format(Locale.ENGLISH, "%1$5.0f", d));
			}
			s.append("]\n");
			i++;
		}
		return s.toString();
	}

	// { "HUM", "NUM", "ABBR", "LOC", "DESC", "ENTY" };

	private int indexOfClass(String classe) {
		if (classe.equals("HUM"))
			return 0;
		if (classe.equals("NUM"))
			return 1;
		if (classe.equals("ABBR"))
			return 2;
		if (classe.equals("LOC"))
			return 3;
		if (classe.equals("DESC"))
			return 4;
		if (classe.equals("ENTY"))
			return 5;
		return -1;
	}

	private String timeFormat(long timeInNano) {
		double tInSec = timeInNano / NANO;
		double nanoSec = tInSec - ((int) tInSec);

		tInSec = tInSec - nanoSec;
		nanoSec *= NANO;
		double tInMin = 0;
		double tInHou = 0;
		if (tInSec >= 60) {
			tInMin = tInSec / 60;
			tInSec = tInSec % 60;
		}
		if (tInMin >= 60) {
			tInHou = tInMin / 60;
			tInMin = tInMin % 60;
		}
		NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		format.setGroupingUsed(false);
		format.setMaximumFractionDigits(0);
		format.setMinimumIntegerDigits(9);
		String nanoString = format.format(nanoSec);
		String timeString = String.format("%1$1.0fh %2$2.0fm %3$2.0f.", tInHou, tInMin, tInSec);
		timeString += nanoString + "s";
		return timeString;
	}
}
