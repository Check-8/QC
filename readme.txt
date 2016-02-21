Per avviare il programma eseguire il main della classe QCMain.
I file train.txt e test.txt contenuti nell cartella res sono utilizzati automaticamente dal programma.
Nel caso si vogliano usare file di train e/o di test diversi, ma con la stessa struttura, si possono usare i parametri -train [path] e -test [path] per indicare dove cercare i file.
I file creati da POSTagger e da TreeTagger sono salvati nella cartella res. Poich� le due classi sono utilizzate solo se questi file non esistono, si devono cancellare i file manualmente se si vuole ripetere il processo di tagging (es.: per diversi file di training o di test).
Tramite il parametro -k [value] � possibile modificare il valore k del KNN. [value] deve essere un intero positivo. Il valore di Default � 5.

Nella cartella tagger sono contenuti i file necessari al funzionamento del PoS tagger e del LexicalizedParser della libreria CoreNLP.

La cartella lib contiene le librerie necessarie al funzionamento del programma.
Eccezione fatta per la libreria usata per il calcolo della distanza fra alberi, che � stata inserita nel codice nel package headliner (la libreria effettuava stampe superflue).

Note: alcuni dei test potrebbero causare OutOfMemoryError, specialmente per test con SMO.
	I test pi� suscettibili a questo sono ParoleConTag e i bigrammi di parole (sia BigrammaParole che BigrammaParoleConTag).
	L'implementazione di KNN � molto grezza. Effettua tutti i confronti senza alcuna ottimizzazione, pu� richiedere molto tempo.
	
Parametri:
	-train [path] : [path] � il percorso al file che si vuole usare per il training.
	-test [path] : [path] � il percorso al file che si vuole usare per il test.
	-k [value] : [value] � il valore k di KNN (numero di vicini usati per determinare la classe).
