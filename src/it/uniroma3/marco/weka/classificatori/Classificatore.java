package it.uniroma3.marco.weka.classificatori;

import java.text.NumberFormat;
import java.util.Locale;

import it.uniroma3.marco.util.Logger;
import it.uniroma3.marco.weka.WekaAbstract;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Classificatore {
	private static final double NANO = 1000000000;
	private static final String[] CLASS_COARSE = WekaAbstract.CLASS_COARSE;
	private String nomeClassificatore;
	private Class<? extends Classifier> classifierClass;

	public Classificatore(String nomeClassificatore, Class<? extends Classifier> classifierClass) {
		super();
		this.nomeClassificatore = nomeClassificatore;
		this.classifierClass = classifierClass;
	}

	public String esegui(Instances trainingSet, Instances testSet, String descrizioneAttributi) {
		StringBuilder s = new StringBuilder();
		s.append(nomeClassificatore + "\n\t" + descrizioneAttributi + "\n");
		Classifier classifier = null;
		try {
			classifier = classifierClass.newInstance();
			long tStart = System.nanoTime();
			classifier.buildClassifier(trainingSet);

			Evaluation eval = new Evaluation(trainingSet);
			long tStartEvaluation = System.nanoTime();
			eval.evaluateModel(classifier, testSet);
			long tEndClassification = System.nanoTime();

			String strSummary = eval.toSummaryString();
			s.append(strSummary + "\n");

			double[][] confMatrix = eval.confusionMatrix();
			String cmString = matrixToString(confMatrix);
			s.append(cmString + "\n");

			String rpfString = recall_precision_fMeasureToString(eval);
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
			log.log(nomeClassificatore + " - " + descrizioneAttributi);
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

	private String recall_precision_fMeasureToString(Evaluation eval) {
		StringBuilder s = new StringBuilder();
		String dFormat = "%1$1.6f";
		Locale en = Locale.ENGLISH;
		s.append(" Class | Precision|  Recall  | fMeasure |\n");
		for (int i = 0; i < CLASS_COARSE.length; i++) {
			s.append(String.format(en, "%1$6s", CLASS_COARSE[i]) + " | ");
			s.append(String.format(en, dFormat, eval.precision(i)) + " | ");
			s.append(String.format(en, dFormat, eval.recall(i)) + " | ");
			s.append(String.format(en, dFormat, eval.fMeasure(i)) + " |\n");
		}

		s.append("Precision complessiva: ");
		s.append(String.format(en, dFormat, eval.weightedPrecision()) + "\n");
		s.append("Recall complessiva   : ");
		s.append(String.format(en, dFormat, eval.weightedRecall()) + "\n");
		s.append("fMeasure complessiva : ");
		s.append(String.format(en, dFormat, eval.weightedFMeasure()) + "\n");
		return s.toString();
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
