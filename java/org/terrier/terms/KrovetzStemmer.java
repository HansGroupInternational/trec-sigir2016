/*
 * Copyright 2015 University of Padua, Italy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terrier.terms;

import java.util.HashMap;

/**
 * Krovetz stemmer, implemented from the Lemur project code BSD License
 * (http://lemurproject.org/galago-license); Copyright 2003, Center for
 * Intelligent Information Retrieval, University of Massachusetts, Amherst. All
 * rights reserved.
 * 
 * @author <a href="mailto:silvello@dei.unipd.it">Gianmaria Silvello</a>
 * @version 0.1
 * @since 0.1
 * 
 */
public class KrovetzStemmer extends StemmerTermPipeline {

	/**
	 * Default size of the cache that stores <code>(word,stem)</code> pairs.
	 * <p>
	 * This speeds up processing since Kstem works by sucessive
	 * "transformations" to the input word until a suitable stem is found.
	 */
	// static public int DEFAULT_CACHE_SIZE = 20000;
	static private final int MaxWordLen = 100;
	static private final String[] exceptionWords = { "aide", "bathe", "caste", "cute", "dame", "dime", "doge", "done",
			"dune", "envelope", "gage", "grille", "grippe", "lobe", "mane", "mare", "nape", "node", "pane", "pate",
			"plane", "pope", "programme", "quite", "ripe", "rote", "rune", "sage", "severe", "shoppe", "sine", "slime",
			"snipe", "steppe", "suite", "swinge", "tare", "tine", "tope", "tripe", "twine" };
	static private final String[][] directConflations = { { "aging", "age" }, { "going", "go" }, { "goes", "go" },
			{ "lying", "lie" }, { "using", "use" }, { "owing", "owe" }, { "suing", "sue" }, { "dying", "die" },
			{ "tying", "tie" }, { "vying", "vie" }, { "aged", "age" }, { "used", "use" }, { "vied", "vie" },
			{ "cued", "cue" }, { "died", "die" }, { "eyed", "eye" }, { "hued", "hue" }, { "iced", "ice" },
			{ "lied", "lie" }, { "owed", "owe" }, { "sued", "sue" }, { "toed", "toe" }, { "tied", "tie" },
			{ "does", "do" }, { "doing", "do" }, { "aeronautical", "aeronautics" }, { "mathematical", "mathematics" },
			{ "political", "politics" }, { "metaphysical", "metaphysics" }, { "cylindrical", "cylinder" },
			{ "nazism", "nazi" }, { "ambiguity", "ambiguous" }, { "barbarity", "barbarous" },
			{ "credulity", "credulous" }, { "generosity", "generous" }, { "spontaneity", "spontaneous" },
			{ "unanimity", "unanimous" }, { "voracity", "voracious" }, { "fled", "flee" },
			{ "miscarriage", "miscarry" }, { "appendices", "appendix" }, { "babysitting", "babysit" },
			{ "bater", "bate" }, { "belying", "belie" }, { "bookshelves", "bookshelf" },
			{ "bootstrapped", "bootstrap" }, { "bootstrapping", "bootstrap" }, { "checksummed", "checksum" },
			{ "checksumming", "checksum" }, { "crises", "crisis" }, { "dwarves", "dwarf" }, { "eerily", "eerie" },
			{ "housewives", "housewife" }, { "midwives", "midwife" }, { "oases", "oasis" },
			{ "parentheses", "parenthesis" }, { "scarves", "scarf" }, { "synopses", "synopsis" },
			{ "syntheses", "synthesis" }, { "taxied", "taxi" }, { "testes", "testicle" }, { "theses", "thesis" },
			{ "thieves", "thief" }, { "vortices", "vortex" }, { "wharves", "wharf" }, { "wolves", "wolf" },
			{ "yourselves", "yourself" } };
	static private final String[][] countryNationality = { { "afghan", "afghanistan" }, { "african", "africa" },
			{ "albanian", "albania" }, { "algerian", "algeria" }, { "american", "america" }, { "andorran", "andorra" },
			{ "angolan", "angola" }, { "arabian", "arabia" }, { "argentine", "argentina" }, { "armenian", "armenia" },
			{ "asian", "asia" }, { "australian", "australia" }, { "austrian", "austria" },
			{ "azerbaijani", "azerbaijan" }, { "azeri", "azerbaijan" }, { "bangladeshi", "bangladesh" },
			{ "belgian", "belgium" }, { "bermudan", "bermuda" }, { "bolivian", "bolivia" }, { "bosnian", "bosnia" },
			{ "botswanan", "botswana" }, { "brazilian", "brazil" }, { "british", "britain" },
			{ "bulgarian", "bulgaria" }, { "burmese", "burma" }, { "californian", "california" },
			{ "cambodian", "cambodia" }, { "canadian", "canada" }, { "chadian", "chad" }, { "chilean", "chile" },
			{ "chinese", "china" }, { "colombian", "colombia" }, { "croat", "croatia" }, { "croatian", "croatia" },
			{ "cuban", "cuba" }, { "cypriot", "cyprus" }, { "czechoslovakian", "czechoslovakia" },
			{ "danish", "denmark" }, { "egyptian", "egypt" }, { "equadorian", "equador" }, { "eritrean", "eritrea" },
			{ "estonian", "estonia" }, { "ethiopian", "ethiopia" }, { "european", "europe" }, { "fijian", "fiji" },
			{ "filipino", "philippines" }, { "finnish", "finland" }, { "french", "france" }, { "gambian", "gambia" },
			{ "georgian", "georgia" }, { "german", "germany" }, { "ghanian", "ghana" }, { "greek", "greece" },
			{ "grenadan", "grenada" }, { "guamian", "guam" }, { "guatemalan", "guatemala" }, { "guinean", "guinea" },
			{ "guyanan", "guyana" }, { "haitian", "haiti" }, { "hawaiian", "hawaii" }, { "holland", "dutch" },
			{ "honduran", "honduras" }, { "hungarian", "hungary" }, { "icelandic", "iceland" },
			{ "indonesian", "indonesia" }, { "iranian", "iran" }, { "iraqi", "iraq" }, { "iraqui", "iraq" },
			{ "irish", "ireland" }, { "israeli", "israel" }, { "italian", "italy" }, { "jamaican", "jamaica" },
			{ "japanese", "japan" }, { "jordanian", "jordan" }, { "kampuchean", "cambodia" }, { "kenyan", "kenya" },
			{ "korean", "korea" }, { "kuwaiti", "kuwait" }, { "lankan", "lanka" }, { "laotian", "laos" },
			{ "latvian", "latvia" }, { "lebanese", "lebanon" }, { "liberian", "liberia" }, { "libyan", "libya" },
			{ "lithuanian", "lithuania" }, { "macedonian", "macedonia" }, { "madagascan", "madagascar" },
			{ "malaysian", "malaysia" }, { "maltese", "malta" }, { "mauritanian", "mauritania" },
			{ "mexican", "mexico" }, { "micronesian", "micronesia" }, { "moldovan", "moldova" },
			{ "monacan", "monaco" }, { "mongolian", "mongolia" }, { "montenegran", "montenegro" },
			{ "moroccan", "morocco" }, { "myanmar", "burma" }, { "namibian", "namibia" }, { "nepalese", "nepal" },
			// {"netherlands", "dutch"},
			{ "nicaraguan", "nicaragua" }, { "nigerian", "nigeria" }, { "norwegian", "norway" }, { "omani", "oman" },
			{ "pakistani", "pakistan" }, { "panamanian", "panama" }, { "papuan", "papua" },
			{ "paraguayan", "paraguay" }, { "peruvian", "peru" }, { "portuguese", "portugal" },
			{ "romanian", "romania" }, { "rumania", "romania" }, { "rumanian", "romania" }, { "russian", "russia" },
			{ "rwandan", "rwanda" }, { "samoan", "samoa" }, { "scottish", "scotland" }, { "serb", "serbia" },
			{ "serbian", "serbia" }, { "siam", "thailand" }, { "siamese", "thailand" }, { "slovakia", "slovak" },
			{ "slovakian", "slovak" }, { "slovenian", "slovenia" }, { "somali", "somalia" }, { "somalian", "somalia" },
			{ "spanish", "spain" }, { "swedish", "sweden" }, { "swiss", "switzerland" }, { "syrian", "syria" },
			{ "taiwanese", "taiwan" }, { "tanzanian", "tanzania" }, { "texan", "texas" }, { "thai", "thailand" },
			{ "tunisian", "tunisia" }, { "turkish", "turkey" }, { "ugandan", "uganda" }, { "ukrainian", "ukraine" },
			{ "uruguayan", "uruguay" }, { "uzbek", "uzbekistan" }, { "venezuelan", "venezuela" },
			{ "vietnamese", "viet" }, { "virginian", "virginia" }, { "yemeni", "yemen" }, { "yugoslav", "yugoslavia" },
			{ "yugoslavian", "yugoslavia" }, { "zambian", "zambia" }, { "zealander", "zealand" },
			{ "zimbabwean", "zimbabwe" } };
	static private final String[] supplementDict = { "aids", "applicator", "capacitor", "digitize", "electromagnet",
			"ellipsoid", "exosphere", "extensible", "ferromagnet", "graphics", "hydromagnet", "polygraph", "toroid",
			"superconduct", "backscatter", "connectionism" };
	static private final String[] properNouns = { "abrams", "achilles", "acropolis", "adams", "agnes", "aires",
			"alexander", "alexis", "alfred", "algiers", "alps", "amadeus", "ames", "amos", "andes", "angeles",
			"annapolis", "antilles", "aquarius", "archimedes", "arkansas", "asher", "ashly", "athens", "atkins",
			"atlantis", "avis", "bahamas", "bangor", "barbados", "barger", "bering", "brahms", "brandeis", "brussels",
			"bruxelles", "cairns", "camoros", "camus", "carlos", "celts", "chalker", "charles", "cheops", "ching",
			"christmas", "cocos", "collins", "columbus", "confucius", "conners", "connolly", "copernicus", "cramer",
			"cyclops", "cygnus", "cyprus", "dallas", "damascus", "daniels", "davies", "davis", "decker", "denning",
			"dennis", "descartes", "dickens", "doris", "douglas", "downs", "dreyfus", "dukakis", "dulles", "dumfries",
			"ecclesiastes", "edwards", "emily", "erasmus", "euphrates", "evans", "everglades", "fairbanks", "federales",
			"fisher", "fitzsimmons", "fleming", "forbes", "fowler", "france", "francis", "goering", "goodling", "goths",
			"grenadines", "guiness", "hades", "harding", "harris", "hastings", "hawkes", "hawking", "hayes", "heights",
			"hercules", "himalayas", "hippocrates", "hobbs", "holmes", "honduras", "hopkins", "hughes", "humphreys",
			"illinois", "indianapolis", "inverness", "iris", "iroquois", "irving", "isaacs", "italy", "james", "jarvis",
			"jeffreys", "jesus", "jones", "josephus", "judas", "julius", "kansas", "keynes", "kipling", "kiwanis",
			"lansing", "laos", "leeds", "levis", "leviticus", "lewis", "louis", "maccabees", "madras", "maimonides",
			"maldive", "massachusetts", "matthews", "mauritius", "memphis", "mercedes", "midas", "mingus",
			"minneapolis", "mohammed", "moines", "morris", "moses", "myers", "myknos", "nablus", "nanjing", "nantes",
			"naples", "neal", "netherlands", "nevis", "nostradamus", "oedipus", "olympus", "orleans", "orly", "papas",
			"paris", "parker", "pauling", "peking", "pershing", "peter", "peters", "philippines", "phineas", "pisces",
			"pryor", "pythagoras", "queens", "rabelais", "ramses", "reynolds", "rhesus", "rhodes", "richards", "robins",
			"rodgers", "rogers", "rubens", "sagittarius", "seychelles", "socrates", "texas", "thames", "thomas",
			"tiberias", "tunis", "venus", "vilnius", "wales", "warner", "wilkins", "williams", "wyoming", "xmas",
			"yonkers", "zeus", "frances", "aarhus", "adonis", "andrews", "angus", "antares", "aquinas", "arcturus",
			"ares", "artemis", "augustus", "ayers", "barnabas", "barnes", "becker", "bejing", "biggs", "billings",
			"boeing", "boris", "borroughs", "briggs", "buenos", "calais", "caracas", "cassius", "cerberus", "ceres",
			"cervantes", "chantilly", "chartres", "chester", "connally", "conner", "coors", "cummings", "curtis",
			"daedalus", "dionysus", "dobbs", "dolores", "edmonds" };

	private static class DictEntry {

		boolean exception;
		String root;

		public DictEntry(String root, boolean isException) {
			this.root = root;
			this.exception = isException;
		}
	}

	private static HashMap dict_ht = null;
	// private int MaxCacheSize;
	// private HashMap stem_ht = null;
	private StringBuffer word;
	private int j; /* index of final letter in stem (within word) */

	private int k; /*
					 * INDEX of final letter in word. You must add 1 to k to get
					 * the current length of word. When you want the length of
					 * word, use the method wordLength, which returns (k+1).
					 */

	// private void initializeStemHash() {
	// stem_ht = new HashMap();
	// }
	private char finalChar() {
		return word.charAt(k);
	}

	private char penultChar() {
		return word.charAt(k - 1);
	}

	private boolean isVowel(int index) {
		return !isCons(index);
	}

	private boolean isCons(int index) {
		char ch;

		ch = word.charAt(index);

		if ((ch == 'a') || (ch == 'e') || (ch == 'i') || (ch == 'o') || (ch == 'u')) {
			return false;
		}
		if ((ch != 'y') || (index == 0)) {
			return true;
		} else {
			return (!isCons(index - 1));
		}
	}

	private static synchronized void initializeDictHash() {
		DictEntry defaultEntry;
		DictEntry entry;

		if (dict_ht != null) {
			return;
		}

		dict_ht = new HashMap();
		for (int i = 0; i < exceptionWords.length; i++) {
			if (!dict_ht.containsKey(exceptionWords[i])) {
				entry = new DictEntry(exceptionWords[i], true);
				dict_ht.put(exceptionWords[i], entry);
			} else {
				System.out.println("Warning: Entry [" + exceptionWords[i] + "] already in dictionary 1");
			}
		}

		for (int i = 0; i < directConflations.length; i++) {
			if (!dict_ht.containsKey(directConflations[i][0])) {
				entry = new DictEntry(directConflations[i][1], false);
				dict_ht.put(directConflations[i][0], entry);
			} else {
				System.out.println("Warning: Entry [" + directConflations[i][0] + "] already in dictionary 2");
			}
		}

		for (int i = 0; i < countryNationality.length; i++) {
			if (!dict_ht.containsKey(countryNationality[i][0])) {
				entry = new DictEntry(countryNationality[i][1], false);
				dict_ht.put(countryNationality[i][0], entry);
			} else {
				System.out.println("Warning: Entry [" + countryNationality[i][0] + "] already in dictionary 3");
			}
		}

		defaultEntry = new DictEntry(null, false);

		String[] array;
		array = KStemData1.data;

		for (int i = 0; i < array.length; i++) {
			if (!dict_ht.containsKey(array[i])) {
				dict_ht.put(array[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + array[i] + "] already in dictionary 4");
			}
		}

		array = KStemData2.data;
		for (int i = 0; i < array.length; i++) {
			if (!dict_ht.containsKey(array[i])) {
				dict_ht.put(array[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + array[i] + "] already in dictionary 4");
			}
		}

		array = KStemData3.data;
		for (int i = 0; i < array.length; i++) {
			if (!dict_ht.containsKey(array[i])) {
				dict_ht.put(array[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + array[i] + "] already in dictionary 4");
			}
		}

		array = KStemData4.data;
		for (int i = 0; i < array.length; i++) {
			if (!dict_ht.containsKey(array[i])) {
				dict_ht.put(array[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + array[i] + "] already in dictionary 4");
			}
		}

		array = KStemData5.data;
		for (int i = 0; i < array.length; i++) {
			if (!dict_ht.containsKey(array[i])) {
				dict_ht.put(array[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + array[i] + "] already in dictionary 4");
			}
		}

		array = KStemData6.data;
		for (int i = 0; i < array.length; i++) {
			if (!dict_ht.containsKey(array[i])) {
				dict_ht.put(array[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + array[i] + "] already in dictionary 4");
			}
		}

		array = KStemData7.data;
		for (int i = 0; i < array.length; i++) {
			if (!dict_ht.containsKey(array[i])) {
				dict_ht.put(array[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + array[i] + "] already in dictionary 4");
			}
		}

		for (int i = 0; i < KStemData8.data.length; i++) {
			if (!dict_ht.containsKey(KStemData8.data[i])) {
				dict_ht.put(KStemData8.data[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + KStemData8.data[i] + "] already in dictionary 4");
			}
		}

		for (int i = 0; i < supplementDict.length; i++) {
			if (!dict_ht.containsKey(supplementDict[i])) {
				dict_ht.put(supplementDict[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + supplementDict[i] + "] already in dictionary 5");
			}
		}

		for (int i = 0; i < properNouns.length; i++) {
			if (!dict_ht.containsKey(properNouns[i])) {
				dict_ht.put(properNouns[i], defaultEntry);
			} else {
				System.out.println("Warning: Entry [" + properNouns[i] + "] already in dictionary 6");
			}
		}
	}

	private boolean isAlpha(char ch) {
		if ((ch >= 'a') && (ch <= 'z')) {
			return true;
		}
		if ((ch >= 'A') && (ch <= 'Z')) {
			return true;
		}
		return false;
	}

	/* length of stem within word */
	private int stemLength() {
		return j + 1;
	}

	private boolean endsIn(String s) {
		boolean match;
		int sufflength = s.length();

		int r = word.length()
				- sufflength; /* length of word before this suffix */
		if (sufflength > k) {
			return false;
		}

		match = true;
		for (int r1 = r, i = 0; (i < sufflength) && (match); i++, r1++) {
			if (s.charAt(i) != word.charAt(r1)) {
				match = false;
			}
		}

		if (match) {
			j = r - 1; /* index of the character BEFORE the posfix */
		} else {
			j = k;
		}
		return match;
	}

	private DictEntry wordInDict() {
		String s = word.toString();
		return (DictEntry) dict_ht.get(s);
	}

	/* Convert plurals to singular form, and '-ies' to 'y' */
	private void plural() {
		if (finalChar() == 's') {
			if (endsIn("ies")) {
				word.setLength(j + 3);
				k--;
				if (lookup(word.toString())) /* ensure calories -> calorie */ {
					return;
				}
				k++;
				word.append('s');
				setSuffix("y");
			} else if (endsIn("es")) {
				/* try just removing the "s" */
				word.setLength(j + 2);
				k--;

				/*
				 * note: don't check for exceptions here. So, `aides' -> `aide',
				 * but `aided' -> `aid'. The exception for double s is used to
				 * prevent crosses -> crosse. This is actually correct if
				 * crosses is a plural noun (a type of racket used in lacrosse),
				 * but the verb is much more common
				 */

				if ((j > 0) && (lookup(word.toString())) && !((word.charAt(j) == 's') && (word.charAt(j - 1) == 's'))) {
					return;
				}

				/* try removing the "es" */

				word.setLength(j + 1);
				k--;
				if (lookup(word.toString())) {
					return;
				}

				/* the default is to retain the "e" */
				word.append('e');
				k++;
				return;
			} else {
				if (word.length() > 3 && penultChar() != 's' && !endsIn("ous")) {
					/*
					 * unless the word ends in "ous" or a double "s", remove the
					 * final "s"
					 */

					word.setLength(k);
					k--;
				}
			}
		}
	}

	private void setSuffix(String s) {
		setSuff(s, s.length());
	}

	/* replace old suffix with s */
	private void setSuff(String s, int len) {
		word.setLength(j + 1);
		for (int l = 0; l < len; l++) {
			word.append(s.charAt(l));
		}
		k = j + len;
	}

	/* Returns true if s is found in the dictionary */
	private boolean lookup(String s) {
		if (dict_ht.containsKey(s)) {
			return true;
		} else {
			return false;
		}
	}

	/* convert past tense (-ed) to present, and `-ied' to `y' */
	private void pastTense() {
		/*
		 * Handle words less than 5 letters with a direct mapping This prevents
		 * (fled -> fl).
		 */

		if (word.length() <= 4) {
			return;
		}

		if (endsIn("ied")) {
			word.setLength(j + 3);
			k--;
			if (lookup(word.toString())) /*
											 * we almost always want to convert
											 * -ied to -y, but
											 */ {
				return; /* this isn't true for short words (died->die) */
			}
			k++; /* I don't know any long words that this applies to, */
			word.append('d'); /* but just in case... */
			setSuffix("y");
			return;
		}

		/* the vowelInStem() is necessary so we don't stem acronyms */
		if (endsIn("ed") && vowelInStem()) {
			/* see if the root ends in `e' */
			word.setLength(j + 2);
			k = j + 1;

			DictEntry entry = wordInDict();
			if (entry != null) {
				if (!entry.exception) /*
										 * if it's in the dictionary and not an
										 * exception
										 */ {
					return;
				}
			}

			/* try removing the "ed" */
			word.setLength(j + 1);
			k = j;
			if (lookup(word.toString())) {
				return;
			}

			/*
			 * try removing a doubled consonant. if the root isn't found in the
			 * dictionary, the default is to leave it doubled. This will
			 * correctly capture `backfilled' -> `backfill' instead of
			 * `backfill' -> `backfille', and seems correct most of the time
			 */

			if (doubleC(k)) {
				word.setLength(k);
				k--;
				if (lookup(word.toString())) {
					return;
				}
				word.append(word.charAt(k));
				k++;
				return;
			}

			/* if we have a `un-' prefix, then leave the word alone */
			/* (this will sometimes screw up with `under-', but we */
			/* will take care of that later) */

			if ((word.charAt(0) == 'u') && (word.charAt(1) == 'n')) {
				word.append('e');
				word.append('d');
				k = k + 2;
				return;
			}

			/*
			 * it wasn't found by just removing the `d' or the `ed', so prefer
			 * to end with an `e' (e.g., `microcoded' -> `microcode').
			 */

			word.setLength(j + 1);
			word.append('e');
			k = j + 1;
			return;
		}
	}

	/* return TRUE if word ends with a double consonant */
	private boolean doubleC(int i) {
		if (i < 1) {
			return false;
		}

		if (word.charAt(i) != word.charAt(i - 1)) {
			return false;
		}
		return (isCons(i));
	}

	private boolean vowelInStem() {
		for (int i = 0; i < stemLength(); i++) {
			if (isVowel(i)) {
				return true;
			}
		}
		return false;
	}

	/* handle `-ing' endings */
	private void aspect() {
		/*
		 * handle short words (aging -> age) via a direct mapping. This prevents
		 * (thing -> the) in the version of this routine that ignores
		 * inflectional variants that are mentioned in the dictionary (when the
		 * root is also present)
		 */

		if (word.length() <= 5) {
			return;
		}

		/* the vowelinstem() is necessary so we don't stem acronyms */
		if (endsIn("ing") && vowelInStem()) {

			/* try adding an `e' to the stem and check against the dictionary */
			word.setCharAt(j + 1, 'e');
			word.setLength(j + 2);
			k = j + 1;

			DictEntry entry = wordInDict();
			if (entry != null) {
				if (!entry.exception) /*
										 * if it's in the dictionary and not an
										 * exception
										 */ {
					return;
				}
			}

			/* adding on the `e' didn't work, so remove it */
			word.setLength(k);
			k--; /* note that `ing' has also been removed */

			if (lookup(word.toString())) {
				return;
			}

			/* if I can remove a doubled consonant and get a word, then do so */
			if (doubleC(k)) {
				k--;
				word.setLength(k + 1);
				if (lookup(word.toString())) {
					return;
				}
				word.append(word.charAt(k)); /* restore the doubled consonant */

				/* the default is to leave the consonant doubled */
				/* (e.g.,`fingerspelling' -> `fingerspell'). Unfortunately */
				/*
				 * `bookselling' -> `booksell' and `mislabelling' ->
				 * `mislabell').
				 */
				/*
				 * Without making the algorithm significantly more complicated,
				 * this
				 */
				/* is the best I can do */
				k++;
				return;
			}

			/*
			 * the word wasn't in the dictionary after removing the stem, and
			 * then checking with and without a final `e'. The default is to add
			 * an `e' unless the word ends in two consonants, so `microcoding'
			 * -> `microcode'. The two consonants restriction wouldn't normally
			 * be necessary, but is needed because we don't try to deal with
			 * prefixes and compounds, and most of the time it is correct (e.g.,
			 * footstamping -> footstamp, not footstampe; however, decoupled ->
			 * decoupl). We can prevent almost all of the incorrect stems if we
			 * try to do some prefix analysis first
			 */

			if ((j > 0) && isCons(j) && isCons(j - 1)) {
				k = j;
				word.setLength(k + 1);
				return;
			}

			word.setLength(j + 1);
			word.append('e');
			k = j + 1;
			return;
		}
	}

	/*
	 * this routine deals with -ity endings. It accepts -ability, -ibility, and
	 * -ality, even without checking the dictionary because they are so
	 * productive. The first two are mapped to -ble, and the -ity is remove for
	 * the latter
	 */
	private void ityEndings() {
		int old_k = k;

		if (endsIn("ity")) {
			word.setLength(j + 1); /* try just removing -ity */
			k = j;
			if (lookup(word.toString())) {
				return;
			}
			word.append('e'); /* try removing -ity and adding -e */
			k = j + 1;
			if (lookup(word.toString())) {
				return;
			}
			word.setCharAt(j + 1, 'i');
			word.append("ty");
			k = old_k;
			/*
			 * the -ability and -ibility endings are highly productive, so just
			 * accept them
			 */
			if ((j > 0) && (word.charAt(j - 1) == 'i') && (word.charAt(j) == 'l')) {
				word.setLength(j - 1);
				word.append("le"); /* convert to -ble */
				k = j;
				return;
			}

			/* ditto for -ivity */
			if ((j > 0) && (word.charAt(j - 1) == 'i') && (word.charAt(j) == 'v')) {
				word.setLength(j + 1);
				word.append('e'); /* convert to -ive */
				k = j + 1;
				return;
			}
			/* ditto for -ality */
			if ((j > 0) && (word.charAt(j - 1) == 'a') && (word.charAt(j) == 'l')) {
				word.setLength(j + 1);
				k = j;
				return;
			}

			/*
			 * if the root isn't in the dictionary, and the variant *is* there,
			 * then use the variant. This allows `immunity'->`immune', but
			 * prevents `capacity'->`capac'. If neither the variant nor the root
			 * form are in the dictionary, then remove the ending as a default
			 */

			if (lookup(word.toString())) {
				return;
			}

			/* the default is to remove -ity altogether */
			word.setLength(j + 1);
			k = j;
			return;
		}
	}

	/* handle -ence and -ance */
	private void nceEndings() {
		int old_k = k;
		char word_char;

		if (endsIn("nce")) {
			if (!((word.charAt(j) == 'e') || (word.charAt(j) == 'a'))) {
				return;
			}
			word_char = word.charAt(j);
			word.setLength(j);
			word.append(
					'e'); /* try converting -e/ance to -e (adherance/adhere) */
			k = j;
			if (lookup(word.toString())) {
				return;
			}
			word.setLength(j); /*
								 * try removing -e/ance altogether
								 * (disappearance/disappear)
								 */
			k = j - 1;
			if (lookup(word.toString())) {
				return;
			}
			word.append(word_char); /* restore the original ending */
			word.append("nce");
			k = old_k;
		}
		return;
	}

	/* handle -ness */
	private void nessEndings() {
		if (endsIn("ness")) { /*
								 * this is a very productive endings, so just
								 * accept it
								 */
			word.setLength(j + 1);
			k = j;
			if (word.charAt(j) == 'i') {
				word.setCharAt(j, 'y');
			}
		}
		return;
	}

	/* handle -ism */
	private void ismEndings() {
		if (endsIn("ism")) { /*
								 * this is a very productive ending, so just
								 * accept it
								 */
			word.setLength(j + 1);
			k = j;
		}
		return;
	}

	/* this routine deals with -ment endings. */
	private void mentEndings() {
		int old_k = k;

		if (endsIn("ment")) {
			word.setLength(j + 1);
			k = j;
			if (lookup(word.toString())) {
				return;
			}
			word.append("ment");
			k = old_k;
		}
		return;
	}

	/* this routine deals with -ize endings. */
	private void izeEndings() {
		int old_k = k;

		if (endsIn("ize")) {
			word.setLength(j + 1); /* try removing -ize entirely */
			k = j;
			if (lookup(word.toString())) {
				return;
			}
			word.append('i');

			if (doubleC(j)) { /* allow for a doubled consonant */
				word.setLength(j);
				k = j - 1;
				if (lookup(word.toString())) {
					return;
				}
				word.append(word.charAt(j - 1));
			}

			word.setLength(j + 1);
			word.append('e'); /* try removing -ize and adding -e */
			k = j + 1;
			if (lookup(word.toString())) {
				return;
			}
			word.setLength(j + 1);
			word.append("ize");
			k = old_k;
		}
		return;
	}

	/* handle -ency and -ancy */
	private void ncyEndings() {
		if (endsIn("ncy")) {
			if (!((word.charAt(j) == 'e') || (word.charAt(j) == 'a'))) {
				return;
			}
			word.setCharAt(j + 2, 't'); /* try converting -ncy to -nt */
			word.setLength(j + 3);
			k = j + 2;

			if (lookup(word.toString())) {
				return;
			}

			word.setCharAt(j + 2,
					'c'); /* the default is to convert it to -nce */
			word.append('e');
			k = j + 3;
		}
		return;
	}

	/* handle -able and -ible */
	private void bleEndings() {
		int old_k = k;
		char word_char;

		if (endsIn("ble")) {
			if (!((word.charAt(j) == 'a') || (word.charAt(j) == 'i'))) {
				return;
			}
			word_char = word.charAt(j);
			word.setLength(j); /* try just removing the ending */
			k = j - 1;
			if (lookup(word.toString())) {
				return;
			}
			if (doubleC(k)) { /* allow for a doubled consonant */
				word.setLength(k);
				k--;
				if (lookup(word.toString())) {
					return;
				}
				k++;
				word.append(word.charAt(k - 1));
			}
			word.setLength(j);
			word.append('e'); /* try removing -a/ible and adding -e */
			k = j;
			if (lookup(word.toString())) {
				return;
			}
			word.setLength(j);
			word.append("ate"); /* try removing -able and adding -ate */
			/* (e.g., compensable/compensate) */
			k = j + 2;
			if (lookup(word.toString())) {
				return;
			}
			word.setLength(j);
			word.append(word_char); /* restore the original values */
			word.append("ble");
			k = old_k;
		}
		return;
	}

	/*
	 * handle -ic endings. This is fairly straightforward, but this is also the
	 * only place we try *expanding* an ending, -ic -> -ical. This is to handle
	 * cases like `canonic' -> `canonical'
	 */
	private void icEndings() {
		if (endsIn("ic")) {
			word.setLength(j + 3);
			word.append("al"); /* try converting -ic to -ical */
			k = j + 4;
			if (lookup(word.toString())) {
				return;
			}

			word.setCharAt(j + 1, 'y'); /* try converting -ic to -y */
			word.setLength(j + 2);
			k = j + 1;
			if (lookup(word.toString())) {
				return;
			}

			word.setCharAt(j + 1, 'e'); /* try converting -ic to -e */
			if (lookup(word.toString())) {
				return;
			}

			word.setLength(j + 1); /* try removing -ic altogether */
			k = j;
			if (lookup(word.toString())) {
				return;
			}
			word.append("ic"); /* restore the original ending */
			k = j + 2;
		}
		return;
	}

	/* handle some derivational endings */
	/*
	 * this routine deals with -ion, -ition, -ation, -ization, and -ication. The
	 * -ization ending is always converted to -ize
	 */
	private void ionEndings() {
		int old_k = k;

		if (endsIn("ization")) { /*
									 * the -ize ending is very productive, so
									 * simply accept it as the root
									 */
			word.setLength(j + 3);
			word.append('e');
			k = j + 3;
			return;
		}

		if (endsIn("ition")) {
			word.setLength(j + 1);
			word.append('e');
			k = j + 1;
			if (lookup(word.toString())) /*
											 * remove -ition and add `e', and
											 * check against the dictionary
											 */ {
				return; /* (e.g., definition->define, opposition->oppose) */
			}

			/* restore original values */
			word.setLength(j + 1);
			word.append("ition");
			k = old_k;
		}

		if (endsIn("ation")) {
			word.setLength(j + 3);
			word.append('e');
			k = j + 3;
			if (lookup(word.toString())) /*
											 * remove -ion and add `e', and
											 * check against the dictionary
											 */ {
				return; /* (elmination -> eliminate) */
			}

			word.setLength(j + 1);
			word.append('e'); /*
								 * remove -ation and add `e', and check against
								 * the dictionary
								 */
			k = j + 1;
			if (lookup(word.toString())) {
				return;
			}

			word.setLength(j + 1);/*
									 * just remove -ation (resignation->resign)
									 * and check dictionary
									 */
			k = j;
			if (lookup(word.toString())) {
				return;
			}

			/* restore original values */
			word.setLength(j + 1);
			word.append("ation");
			k = old_k;
		}

		/*
		 * test -ication after -ation is attempted (e.g.,
		 * `complication->complicate' rather than `complication->comply')
		 */

		if (endsIn("ication")) {
			word.setLength(j + 1);
			word.append('y');
			k = j + 1;
			if (lookup(word.toString())) /*
											 * remove -ication and add `y', and
											 * check against the dictionary
											 */ {
				return; /* (e.g., amplification -> amplify) */
			}

			/* restore original values */
			word.setLength(j + 1);
			word.append("ication");
			k = old_k;
		}

		if (endsIn("ion")) {
			word.setLength(j + 1);
			word.append('e');
			k = j + 1;
			if (lookup(word.toString())) /*
											 * remove -ion and add `e', and
											 * check against the dictionary
											 */ {
				return;
			}

			word.setLength(j + 1);
			k = j;
			if (lookup(word.toString())) /*
											 * remove -ion, and if it's found,
											 * treat that as the root
											 */ {
				return;
			}

			/* restore original values */
			word.setLength(j + 1);
			word.append("ion");
			k = old_k;
		}

		return;
	}

	/*
	 * this routine deals with -er, -or, -ier, and -eer. The -izer ending is
	 * always converted to -ize
	 */
	private void erAndOrEndings() {
		int old_k = k;

		char word_char; /* so we can remember if it was -er or -or */

		if (endsIn(
				"izer")) { /*
							 * -ize is very productive, so accept it as the root
							 */
			word.setLength(j + 4);
			k = j + 3;
			return;
		}

		if (endsIn("er") || endsIn("or")) {
			word_char = word.charAt(j + 1);
			if (doubleC(j)) {
				word.setLength(j);
				k = j - 1;
				if (lookup(word.toString())) {
					return;
				}
				word.append(
						word.charAt(j - 1)); /* restore the doubled consonant */
			}

			if (word.charAt(j) == 'i') { /* do we have a -ier ending? */
				word.setCharAt(j, 'y');
				word.setLength(j + 1);
				k = j;
				if (lookup(word
						.toString())) /* yes, so check against the dictionary */ {
					return;
				}
				word.setCharAt(j, 'i'); /* restore the endings */
				word.append('e');
			}

			if (word.charAt(j) == 'e') { /* handle -eer */
				word.setLength(j);
				k = j - 1;
				if (lookup(word.toString())) {
					return;
				}
				word.append('e');
			}

			word.setLength(j + 2); /* remove the -r ending */
			k = j + 1;
			if (lookup(word.toString())) {
				return;
			}
			word.setLength(j + 1); /* try removing -er/-or */
			k = j;
			if (lookup(word.toString())) {
				return;
			}
			word.append('e'); /* try removing -or and adding -e */
			k = j + 1;
			if (lookup(word.toString())) {
				return;
			}
			word.setLength(j + 1);
			word.append(word_char);
			word.append('r'); /* restore the word to the way it was */
			k = old_k;
		}

	}

	/*
	 * this routine deals with -ly endings. The -ally ending is always converted
	 * to -al Sometimes this will temporarily leave us with a non-word (e.g.,
	 * heuristically maps to heuristical), but then the -al is removed in the
	 * next step.
	 */
	private void lyEndings() {
		int old_k = k;

		if (endsIn("ly")) {

			word.setCharAt(j + 2, 'e'); /* try converting -ly to -le */

			if (lookup(word.toString())) {
				return;
			}
			word.setCharAt(j + 2, 'y');

			word.setLength(j + 1); /* try just removing the -ly */
			k = j;

			if (lookup(word.toString())) {
				return;
			}

			if ((j > 0) && (word.charAt(j - 1) == 'a') && (word
					.charAt(j) == 'l')) /* always convert -ally to -al */ {
				return;
			}
			word.append("ly");
			k = old_k;

			if ((j > 0) && (word.charAt(j - 1) == 'a') && (word
					.charAt(j) == 'b')) { /* always convert -ably to -able */
				word.setCharAt(j + 2, 'e');
				k = j + 2;
				return;
			}

			if (word.charAt(j) == 'i') { /* e.g., militarily -> military */
				word.setLength(j);
				word.append('y');
				k = j;
				if (lookup(word.toString())) {
					return;
				}
				word.setLength(j);
				word.append("ily");
				k = old_k;
			}

			word.setLength(j + 1); /* the default is to remove -ly */

			k = j;
		}
		return;
	}

	/*
	 * this routine deals with -al endings. Some of the endings from the
	 * previous routine are finished up here.
	 */
	private void alEndings() {
		int old_k = k;

		if (word.length() < 4) {
			return;
		}
		if (endsIn("al")) {
			word.setLength(j + 1);
			k = j;
			if (lookup(word.toString())) /* try just removing the -al */ {
				return;
			}

			if (doubleC(j)) { /* allow for a doubled consonant */
				word.setLength(j);
				k = j - 1;
				if (lookup(word.toString())) {
					return;
				}
				word.append(word.charAt(j - 1));
			}

			word.setLength(j + 1);
			word.append('e'); /* try removing the -al and adding -e */
			k = j + 1;
			if (lookup(word.toString())) {
				return;
			}

			word.setLength(j + 1);
			word.append("um"); /* try converting -al to -um */
			/* (e.g., optimal - > optimum ) */
			k = j + 2;
			if (lookup(word.toString())) {
				return;
			}

			word.setLength(j + 1);
			word.append("al"); /* restore the ending to the way it was */
			k = old_k;

			if ((j > 0) && (word.charAt(j - 1) == 'i') && (word.charAt(j) == 'c')) {
				word.setLength(j - 1); /* try removing -ical */
				k = j - 2;
				if (lookup(word.toString())) {
					return;
				}

				word.setLength(j - 1);
				word.append('y');/*
									 * try turning -ical to -y (e.g.,
									 * bibliographical)
									 */
				k = j - 1;
				if (lookup(word.toString())) {
					return;
				}

				word.setLength(j - 1);
				word.append("ic"); /* the default is to convert -ical to -ic */
				k = j;
				return;
			}

			if (word.charAt(
					j) == 'i') { /* sometimes -ial endings should be removed */
				word.setLength(
						j); /* (sometimes it gets turned into -y, but we */
				k = j - 1; /* aren't dealing with that case for now) */
				if (lookup(word.toString())) {
					return;
				}
				word.append("ial");
				k = old_k;
			}

		}
		return;
	}

	/*
	 * this routine deals with -ive endings. It normalizes some of the -ative
	 * endings directly, and also maps some -ive endings to -ion.
	 */
	private void iveEndings() {
		int old_k = k;

		if (endsIn("ive")) {
			word.setLength(j + 1); /* try removing -ive entirely */
			k = j;
			if (lookup(word.toString())) {
				return;
			}

			word.append('e'); /* try removing -ive and adding -e */
			k = j + 1;
			if (lookup(word.toString())) {
				return;
			}
			word.setLength(j + 1);
			word.append("ive");
			if ((j > 0) && (word.charAt(j - 1) == 'a') && (word.charAt(j) == 't')) {
				word.setCharAt(j - 1,
						'e'); /* try removing -ative and adding -e */
				word.setLength(j); /* (e.g., determinative -> determine) */
				k = j - 1;
				if (lookup(word.toString())) {
					return;
				}
				word.setLength(j - 1); /* try just removing -ative */
				if (lookup(word.toString())) {
					return;
				}

				word.append("ative");
				k = old_k;
			}

			/* try mapping -ive to -ion (e.g., injunctive/injunction) */
			word.setCharAt(j + 2, 'o');
			word.setCharAt(j + 3, 'n');
			if (lookup(word.toString())) {
				return;
			}

			word.setCharAt(j + 2, 'v'); /* restore the original values */
			word.setCharAt(j + 3, 'e');
			k = old_k;
		}
		return;
	}

	/**
	 * constructor
	 */
	public KrovetzStemmer() {
		super();
	}

	/**
	 * constructor
	 * 
	 * @param next
	 */
	public KrovetzStemmer(TermPipeline next) {
		super(next);
	}

	@Override
	public String stem(String term) {
		
		if (dict_ht == null) {
			initializeDictHash();
		}
		
		boolean stemIt;
		String result;
		String original;

		// if (stem_ht == null) {
		// initializeStemHash();
		// }

		k = term.length() - 1;

		/*
		 * If the word is too long or too short, or not entirely alphabetic,
		 * just lowercase copy it into stem and return
		 */
		stemIt = true;
		if ((k <= 1) || (k >= MaxWordLen - 1)) {
			stemIt = false;
		} else {
			word = new StringBuffer(term.length());
			for (int i = 0; i < term.length(); i++) {
				char ch = Character.toLowerCase(term.charAt(i));
				word.append(ch);
				if (!isAlpha(ch)) {
					stemIt = false;
					break;
				}
			}
		}
		if (!stemIt) {
			return term.toLowerCase();
		}
		/* Check to see if it's in the cache */
		original = word.toString();
		// if (stem_ht.containsKey(original)) {
		// return (String) stem_ht.get(original);
		// }

		result = original; /* default response */

		/*
		 * This while loop will never be executed more than one time; it is here
		 * only to allow the break statement to be used to escape as soon as a
		 * word is recognized
		 */

		DictEntry entry = null;

		while (true) {
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			plural();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			pastTense();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			aspect();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			ityEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			nessEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			ionEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			erAndOrEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			lyEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			alEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			iveEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			izeEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			mentEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			bleEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			ismEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			icEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			ncyEndings();
			entry = wordInDict();
			if (entry != null) {
				break;
			}
			nceEndings();
			entry = wordInDict();
			break;
		}

		/*
		 * try for a direct mapping (allows for cases like `Italian'->`Italy'
		 * and `Italians'->`Italy')
		 */
		if (entry != null) {
			if (entry.root != null) {
				result = entry.root;
			} else {
				result = word.toString();
			}
		} else {
			result = word.toString();
		}

		/* Enter into cache, at the place not used by the last cache hit */
		// if (stem_ht.size() < MaxCacheSize) {
		/* Add term to cache */
		// stem_ht.put(original, result);
		// }

		return result;
	}

	static class KStemData1 {
		private KStemData1() {
		}

		// KStemData1 ... KStemData8 are created from "head_word_list.txt"
		static String[] data = { "aback", "abacus", "abandon", "abandoned", "abase", "abash", "abate", "abattoir",
				"abbess", "abbey", "abbot", "abbreviate", "abbreviation", "abc", "abdicate", "abdomen", "abduct",
				"abed", "aberrant", "aberration", "abet", "abeyance", "abhor", "abhorrent", "abide", "abiding",
				"abilities", "ability", "abject", "abjure", "ablative", "ablaut", "ablaze", "able", "ablution",
				"ablutions", "ably", "abnegation", "abnormal", "abo", "aboard", "abode", "abolish", "abolition",
				"abominable", "abominate", "abomination", "aboriginal", "aborigine", "abort", "abortion", "abortionist",
				"abortive", "abound", "about", "above", "aboveboard", "abracadabra", "abrade", "abrasion", "abrasive",
				"abreast", "abridge", "abridgement", "abridgment", "abroad", "abrogate", "abrupt", "abscess", "abscond",
				"absence", "absent", "absentee", "absenteeism", "absently", "absinth", "absinthe", "absolute",
				"absolutely", "absolution", "absolutism", "absolve", "absorb", "absorbent", "absorbing", "absorption",
				"abstain", "abstemious", "abstention", "abstinence", "abstract", "abstracted", "abstraction",
				"abstruse", "absurd", "abundance", "abundant", "abuse", "abusive", "abut", "abutment", "abysmal",
				"abyss", "acacia", "academic", "academician", "academy", "accede", "accelerate", "acceleration",
				"accelerator", "accent", "accentuate", "accept", "acceptable", "acceptance", "access", "accessible",
				"accession", "accessory", "accidence", "accident", "accidental", "acclaim", "acclamation",
				"acclimatize", "acclivity", "accolade", "accommodate", "accommodating", "accommodation",
				"accommodations", "accompaniment", "accompanist", "accompany", "accomplice", "accomplish",
				"accomplished", "accomplishment", "accord", "accordance", "according", "accordingly", "accordion",
				"accost", "account", "accountable", "accountancy", "accountant", "accoutrements", "accredit",
				"accretion", "accrue", "accumulate", "accumulation", "accumulative", "accumulator", "accuracy",
				"accurate", "accursed", "accusation", "accusative", "accuse", "accused", "accustom", "accustomed",
				"ace", "acerbity", "acetate", "acetic", "acetylene", "ache", "achieve", "achievement", "achoo", "acid",
				"acidify", "acidity", "acidulated", "acidulous", "acknowledge", "acknowledgement", "acknowledgment",
				"acme", "acne", "acolyte", "aconite", "acorn", "acoustic", "acoustics", "acquaint", "acquaintance",
				"acquaintanceship", "acquiesce", "acquiescent", "acquire", "acquisition", "acquisitive", "acquit",
				"acquittal", "acre", "acreage", "acrid", "acrimony", "acrobat", "acrobatic", "acrobatics", "acronym",
				"across", "acrostic", "act", "acting", "actinism", "action", "actionable", "activate", "active",
				"activist", "activity", "actor", "actress", "acts", "actual", "actuality", "actually", "actuary",
				"actuate", "acuity", "acumen", "acupuncture", "acute", "adage", "adagio", "adam", "adamant", "adapt",
				"adaptable", "adaptation", "adapter", "adaptor", "adc", "add", "addendum", "adder", "addict",
				"addiction", "addictive", "addition", "additional", "additive", "addle", "address", "addressee",
				"adduce", "adenoidal", "adenoids", "adept", "adequate", "adhere", "adherence", "adherent", "adhesion",
				"adhesive", "adieu", "adipose", "adj", "adjacent", "adjective", "adjoin", "adjourn", "adjudge",
				"adjudicate", "adjunct", "adjure", "adjust", "adjutant", "adman", "admass", "administer",
				"administration", "administrative", "administrator", "admirable", "admiral", "admiralty", "admiration",
				"admire", "admirer", "admissible", "admission", "admit", "admittance", "admitted", "admittedly",
				"admixture", "admonish", "admonition", "admonitory", "ado", "adobe", "adolescent", "adopt", "adoption",
				"adoptive", "adorable", "adoration", "adore", "adorn", "adornment", "adrenalin", "adrift", "adroit",
				"adulate", "adulation", "adult", "adulterate", "adulterer", "adultery", "adumbrate", "adv", "advance",
				"advanced", "advancement", "advances", "advantage", "advantageous", "advent", "adventist",
				"adventitious", "adventure", "adventurer", "adventuress", "adventurous", "adverb", "adverbial",
				"adversary", "adverse", "adversity", "advert", "advertise", "advertisement", "advertising", "advice",
				"advisable", "advise", "advisedly", "adviser", "advisor", "advisory", "advocacy", "advocate", "adz",
				"adze", "aegis", "aeon", "aerate", "aerial", "aerie", "aerobatic", "aerobatics", "aerodrome",
				"aerodynamic", "aerodynamics", "aeronautics", "aeroplane", "aerosol", "aerospace", "aertex", "aery",
				"aesthete", "aesthetic", "aesthetics", "aether", "aethereal", "aetiology", "afar", "affable", "affair",
				"affect", "affectation", "affected", "affecting", "affection", "affectionate", "affiance", "affidavit",
				"affiliate", "affiliation", "affinity", "affirm", "affirmative", "affix", "afflict", "affliction",
				"affluent", "afford", "afforest", "affray", "affricate", "affront", "aficionado", "afield", "afire",
				"aflame", "afloat", "afoot", "aforesaid", "aforethought", "afraid", "afresh", "afrikaans", "afrikaner",
				"afro", "aft", "after", "afterbirth", "aftercare", "aftereffect", "afterglow", "afterlife", "aftermath",
				"afternoon", "afternoons", "afters", "aftershave", "aftertaste", "afterthought", "afterwards", "again",
				"against", "agape", "agate", "age", "ageing", "ageless", "agency", "agenda", "agent", "agglomerate",
				"agglutination", "agglutinative", "aggrandisement", "aggrandizement", "aggravate", "aggravation",
				"aggregate", "aggregation", "aggression", "aggressive", "aggressor", "aggrieved", "aggro", "aghast",
				"agile", "agitate", "agitation", "agitator", "aglow", "agnostic", "ago", "agog", "agonise", "agonised",
				"agonising", "agonize", "agonized", "agonizing", "agony", "agoraphobia", "agoraphobic", "agrarian",
				"agree", "agreeable", "agreeably", "agreement", "agriculture", "agronomy", "aground", "ague", "aha",
				"ahead", "ahem", "ahoy", "aid", "ail", "aileron", "ailment", "aim", "aimless", "air", "airbase",
				"airbed", "airbladder", "airborne", "airbrake", "airbrick", "airbus", "aircraft", "aircraftman",
				"aircrew", "aircushion", "airdrop", "airedale", "airfield", "airflow", "airforce", "airgun", "airhole",
				"airhostess", "airily", "airing", "airlane", "airless", "airletter", "airlift", "airline", "airliner",
				"airlock", "airmail", "airman", "airplane", "airpocket", "airport", "airs", "airshaft", "airship",
				"airsick", "airspace", "airspeed", "airstrip", "airtight", "airway", "airwoman", "airworthy", "airy",
				"aisle", "aitch", "ajar", "akimbo", "akin", "alabaster", "alack", "alacrity", "alarm", "alarmist",
				"alas", "albatross", "albeit", "albino", "album", "albumen", "alchemist", "alchemy", "alcohol",
				"alcoholic", "alcoholism", "alcove", "alder", "alderman", "ale", "alehouse", "alert", "alfalfa",
				"alfresco", "algae", "algebra", "algorithm", "alias", "alibi", "alien", "alienate", "alienation",
				"alienist", "alight", "align", "alignment", "alike", "alimentary", "alimony", "aline", "alinement",
				"alive", "alkali", "alkaline", "all", "allah", "allay", "allegation", "allege", "allegedly",
				"allegiance", "allegorical", "allegory", "allegretto", "allegro", "alleluia", "allergic", "allergy",
				"alleviate", "alley", "alleyway", "alliance", "allied", "alligator", "alliteration", "alliterative",
				"allocate", "allocation", "allopathy", "allot", "allotment", "allow", "allowable", "allowance", "alloy",
				"allspice", "allude", "allure", "allurement", "allusion", "alluvial", "alluvium", "ally", "almanac",
				"almanack", "almighty", "almond", "almoner", "almost", "alms", "aloe", "aloft", "alone", "along",
				"alongside", "aloof", "alopecia", "aloud", "alpaca", "alpenhorn", "alpenstock", "alpha", "alphabet",
				"alphabetical", "alpine", "already", "alright", "alsatian", "also", "altar", "altarpiece", "alter",
				"alteration", "altercation", "alternate", "alternative", "alternator", "although", "altimeter",
				"altitude", "alto", "altogether", "altruism", "altruist", "alum", "aluminium", "alumna", "alumnus",
				"alveolar", "always", "alyssum", "amalgam", "amalgamate", "amanuensis", "amass", "amateur",
				"amateurish", "amatory", "amaze", "amazing", "amazon", "ambassador", "ambassadorial", "amber",
				"ambergris", "ambidextrous", "ambience", "ambient", "ambiguous", "ambit", "ambition", "ambitious",
				"ambivalent", "amble", "ambrosia", "ambulance", "ambush", "ame", "ameba", "ameliorate", "amen",
				"amenable", "amend", "amendment", "amends", "amenity", "americanise", "americanism", "americanize",
				"amethyst", "amiable", "amicable", "amid", "amidships", "amir", "amiss", "amity", "ammeter", "ammo",
				"ammonia", "ammonite", "ammunition", "amnesia", "amnesty", "amoeba", "amoebic", "amok", "among",
				"amoral", "amorous", "amorphous", "amortise", "amortize", "amount", "amour", "amp", "amperage",
				"ampersand", "amphetamine", "amphibian", "amphibious", "amphitheater", "amphitheatre", "amphora",
				"ample", "amplifier", "amplify", "amplitude", "ampoule", "amputate", "amputee", "amuck", "amulet",
				"amuse", "amusement", "anachronism", "anaconda", "anaemia", "anaemic", "anaesthesia", "anaesthetic",
				"anaesthetist", "anagram", "anal", "analgesia", "analgesic", "analog", "analogize", "analogous",
				"analogue", "analogy", "analyse", "analysis", "analyst", "analytic", "anapaest", "anarchic",
				"anarchism", "anarchist", "anarchy", "anathema", "anathematize", "anatomical", "anatomist", "anatomy",
				"ancestor", "ancestral", "ancestry", "anchor", "anchorage", "anchorite", "anchovy", "ancient",
				"ancients", "ancillary", "and", "andante", "andiron", "androgynous", "anecdotal", "anecdote", "anemia",
				"anemometer", "anemone", "anesthesia", "anesthetise", "anesthetize", "anew", "angel", "angelica",
				"angelus", "anger", "angle", "anglican", "anglicise", "anglicism", "anglicize", "angling", "anglophile",
				"anglophilia", "anglophobe", "anglophobia", "angora", "angostura", "angry", "angst", "anguish",
				"anguished", "angular", "aniline", "animadversion", "animadvert", "animal", "animalcule", "animalism",
				"animate", "animation", "animism", "animosity", "animus", "anis", "anise", "aniseed", "ankle", "anklet",
				"annals", "anneal", "annex", "annexation", "annexe", "annihilate", "anniversary", "annotate",
				"annotation", "announce", "announcement", "announcer", "annoy", "annoyance", "annual", "annuity",
				"annul", "annular", "annunciation", "anode", "anodyne", "anoint", "anomalous", "anomaly", "anon",
				"anonymity", "anonymous", "anopheles", "anorak", "anorexia", "another", "answer", "answerable", "ant",
				"antacid", "antagonism", "antagonist", "antagonize", "antarctic", "ante", "anteater", "antecedence",
				"antecedent", "antecedents", "antechamber", "antedate", "antediluvian", "antelope", "antenatal",
				"antenna", "antepenultimate", "anterior", "anteroom", "anthem", "anther", "anthill", "anthology",
				"anthracite", "anthrax", "anthropocentric", "anthropoid", "anthropologist", "anthropology",
				"anthropomorphic", "anthropomorphism", "anthropophagous", "anthropophagy", "antiaircraft", "antibiotic",
				"antibody", "antic", "anticipate", "anticipation", "anticipatory", "anticlerical", "anticlimax",
				"anticlockwise", "antics", "anticyclone", "antidote", "antifreeze", "antigen", "antihero",
				"antihistamine", "antiknock", "antilogarithm", "antimacassar", "antimatter", "antimony", "antipathetic",
				"antipathy", "antipersonnel", "antipodal", "antipodes", "antiquarian", "antiquary", "antiquated",
				"antique", "antiquity", "antirrhinum", "antiseptic", "antisocial", "antithesis", "antithetic",
				"antitoxin", "antler", "antonym", "anus", "anvil", "anxiety", "anxious", "any", "anybody", "anyhow",
				"anyplace", "anyroad", "anything", "anyway", "anywhere", "aorta", "apace", "apanage", "apart",
				"apartheid", "apartment", "apartments", "apathetic", "apathy", "ape", "aperient", "aperitif",
				"aperture", "apex", "aphasia", "aphasic", "aphid", "aphorism", "aphoristic", "aphrodisiac", "apiarist",
				"apiary", "apices", "apiculture", "apiece", "apish", "aplomb", "apocalypse", "apocalyptic", "apocrypha",
				"apocryphal", "apogee", "apologetic", "apologetics", "apologia", "apologise", "apologist", "apologize",
				"apology", "apophthegm", "apoplectic", "apoplexy", "apostasy", "apostate", "apostatise", "apostatize",
				"apostle", "apostolic", "apostrophe", "apostrophize", "apothecary", "apothegm", "apotheosis", "appal",
				"appall", "appalling", "appanage", "apparatus", "apparel", "apparent", "apparently", "apparition",
				"appeal", "appealing", "appear", "appearance", "appearances", "appease", "appeasement", "appellant",
				"appellate", "appellation", "append", "appendage", "appendectomy", "appendicitis", "appendix",
				"appertain", "appetite", "appetizer", "appetizing", "applaud", "applause", "apple", "applejack",
				"appliance", "applicable", "applicant", "application", "applied", "apply", "appoint", "appointment",
				"appointments", "apportion", "apposite", "apposition", "appraisal", "appraise", "appreciable",
				"appreciate", "appreciation", "appreciative", "apprehend", "apprehension", "apprehensive", "apprentice",
				"apprenticeship", "apprise", "appro", "approach", "approachable", "approbation", "approbatory",
				"appropriate", "appropriation", "approval", "approve", "approx", "approximate", "approximation",
				"appurtenance", "apricot", "april", "apron", "apropos", "apse", "apt", "aptitude", "aqualung",
				"aquamarine", "aquaplane", "aquarium", "aquatic", "aquatint", "aqueduct", "aqueous", "aquiline", "arab",
				"arabesque", "arabic", "arable", "arachnid", "arak", "arbiter", "arbitrary", "arbitrate", "arbitration",
				"arbitrator", "arbor", "arboreal", "arboretum", "arbour", "arc", "arcade", "arcadia", "arcane", "arch",
				"archaeology", "archaic", "archaism", "archangel", "archbishop", "archbishopric", "archdeacon",
				"archdeaconry", "archdiocese", "archduke", "archeology", "archer", "archery", "archetype",
				"archimandrite", "archipelago", "architect", "architecture", "archive", "archway", "arctic", "ardent",
				"ardor", "ardour", "arduous", "are", "area", "areca", "arena", "argent", "argon", "argot", "arguable",
				"argue", "argument", "argumentative", "aria", "arid", "aries", "aright", "arise", "aristocracy",
				"aristocrat", "aristocratic", "arithmetic", "arithmetician", "ark", "arm", "armada", "armadillo",
				"armament", "armature", "armband", "armchair", "armed", "armful", "armhole", "armistice", "armlet",
				"armor", "armorer", "armorial", "armory", "armour", "armoured", "armourer", "armoury", "armpit", "arms",
				"army", "aroma", "aromatic", "arose", "around", "arouse", "arpeggio", "arquebus", "arrack", "arraign",
				"arrange", "arrangement", "arrant", "arras", "array", "arrears", "arrest", "arrival", "arrive",
				"arrogance", "arrogant", "arrogate", "arrow", "arrowhead", "arrowroot", "arse", "arsenal", "arsenic",
				"arson", "art", "artefact", "arterial", "arteriosclerosis", "artery", "artful", "arthritis",
				"artichoke", "article", "articles", "articulate", "articulated", "articulateness", "articulation",
				"artifact", "artifice", "artificer", "artificial", "artillery", "artisan", "artist", "artiste",
				"artistic", "artistry", "artless", "arts", "arty", "arum", "asbestos", "ascend", "ascendancy",
				"ascendant", "ascendency", "ascendent", "ascension", "ascent", "ascertain", "ascetic", "ascribe",
				"ascription", "asepsis", "aseptic", "asexual", "ash", "ashamed", "ashbin", "ashcan", "ashen", "ashes",
				"ashore", "ashtray", "ashy", "aside", "asinine", "ask", "askance", "askew", "aslant", "asleep", "asp",
				"asparagus", "aspect", "aspectual", "aspen", "asperity", "aspersion", "asphalt", "asphodel", "asphyxia",
				"asphyxiate", "aspic", "aspidistra", "aspirant", "aspirate", "aspiration", "aspire", "aspirin", "ass",
				"assagai", "assail", "assailant", "assassin", "assassinate", "assault", "assay", "assegai",
				"assemblage", "assemble", "assembly", "assemblyman", "assent", "assert", "assertion", "assertive",
				"assess", "assessment", "assessor", "asset", "asseverate", "assiduity", "assiduous", "assign",
				"assignation", "assignment", "assimilate", "assimilation", "assist", "assistance", "assistant",
				"assize", "assizes", "associate", "association", "assonance", "assort", "assorted", "assortment",
				"asst", "assuage", "assume", "assumption", "assurance", "assure", "assured", "aster", "asterisk",
				"astern", "asteroid", "asthma", "astigmatic", "astigmatism", "astir", "astonish", "astonishment",
				"astound", "astrakhan", "astral", "astray", "astride", "astringent", "astrolabe", "astrologer",
				"astrology", "astronaut", "astronautics", "astronomer", "astronomical", "astronomy", "astrophysics",
				"astute", "asunder", "asylum", "asymmetric", "atavism", "atchoo", "ate", "atelier", "atheism",
				"atheist", "athlete", "athletic", "athletics", "athwart", "atishoo", "atlas", "atmosphere",
				"atmospheric", "atmospherics", "atoll", "atom", "atomic", "atomise", "atomize", "atonal", "atonality",
				"atone", "atop", "atrocious", "atrocity", "atrophy", "attach", "attachment", "attack", "attain",
				"attainder", "attainment", "attar", "attempt", "attend", "attendance", "attendant", "attention",
				"attentive", "attenuate", "attest", "attestation", "attested", "attic", "attire", "attitude",
				"attitudinise", "attitudinize", "attorney", "attract", "attraction", "attractive", "attributable",
				"attribute", "attribution", "attributive", "attrition", "attune", "atypical", "aubergine", "aubrietia",
				"auburn", "auction", "auctioneer", "audacious", "audacity", "audible", "audience", "audio",
				"audiometer", "audit", "audition", "auditor", "auditorium", "auditory", "auger", "aught", "augment",
				"augmentation", "augur", "augury", "august", "auk", "aunt", "aura", "aural", "aureole", "auricle",
				"auricular", "auriferous", "aurora", "auscultation", "auspices", "auspicious", "aussie", "austere",
				"austerity", "australasian", "autarchy", "autarky", "authentic", "authenticate", "authenticity",
				"author", "authoress", "authorisation", "authorise", "authoritarian", "authoritative", "authority",
				"authorization", "authorize", "authorship", "autism", "autistic", "auto", "autobahn",
				"autobiographical", "autobiography", "autocracy", "autocrat", "autoeroticism", "autograph", "automat",
				"automate", "automatic", "automation", "automatism", "automaton", "automobile", "autonomous",
				"autonomy", "autopsy", "autostrada", "autosuggestion", "autumn", "autumnal", "auxiliary", "avail",
				"available", "avalanche", "avarice", "avaricious", "avatar", "avaunt", "avenge", "avenue", "aver",
				"average", "averse", "aversion", "aversive", "avert", "aviary", "aviation", "aviator", "avid",
				"avocado", "avocation", "avocet", "avoid", "avoidance", "avoirdupois", "avow", "avowal", "avowed",
				"avuncular", "await", "awake", "awaken", "awakening", "award", "aware", "awash", "away", "awe",
				"awesome", "awestruck", "awful", "awfully", "awhile", "awkward", "awl", "awning", "awoke", "awoken",
				"awry", "axe", "axiom", "axiomatic", "axis", "axle", "axolotl", "ayah", "aye", "azalea", "azimuth",
				"azure", "baa", "babble", "babbler", "babe", "babel", "baboo", "baboon", "babu", "baby", "babyhood",
				"babyish", "baccalaureate", "baccara", "baccarat", "bacchanal", "baccy", "bachelor", "bacillus", "back",
				"backache", "backbench", "backbite", "backbone", "backbreaking", "backchat", "backcloth", "backcomb",
				"backdate", "backdrop", "backer", "backfire", "backgammon", "background", "backhand", "backhanded",
				"backhander", "backing", "backlash", "backlog", "backmost", "backpedal", "backside", "backslide",
				"backspace", "backstage", "backstairs", "backstay", "backstroke", "backtrack", "backup", "backward",
				"backwards", "backwash", "backwater", "backwoods", "backwoodsman", "backyard", "bacon", "bacteria",
				"bacteriology", "bactrian", "bad", "bade", "badge", "badger", "badinage", "badly", "badminton",
				"baffle", "baffling", "bag", "bagatelle", "bagful", "baggage", "baggy", "bagpipes", "bags", "bah",
				"bail", "bailey", "bailiff", "bairn", "bait", "baize", "bake", "bakelite", "baker", "bakery",
				"baksheesh", "balaclava", "balalaika", "balance", "balanced", "balcony", "bald", "balderdash",
				"balding", "baldly", "baldric", "bale", "baleful", "balk", "ball", "ballad", "ballade", "ballast",
				"ballcock", "ballerina", "ballet", "ballistic", "ballistics", "ballocks", "balloon", "ballooning",
				"balloonist", "ballot", "ballpoint", "ballroom", "balls", "bally", "ballyhoo", "balm", "balmy",
				"baloney", "balsa", "balsam", "balustrade", "bamboo", "bamboozle", "ban", "banal", "banana", "band",
				"bandage", "bandana", "bandanna", "bandbox", "bandeau", "bandit", "banditry", "bandmaster", "bandoleer",
				"bandolier", "bandsman", "bandstand", "bandwagon", "bandy", "bane", "baneful", "bang", "banger",
				"bangle", "banian", "banish", "banister", "banjo", "bank", "bankbook", "banker", "banking", "bankrupt",
				"bankruptcy", "banner", "bannock", "banns", "banquet", "banshee", "bantam", "bantamweight", "banter",
				"banyan", "baobab", "baptise", "baptism", "baptist", "baptize", "bar", "barb", "barbarian", "barbaric",
				"barbarise", "barbarism", "barbarize", "barbarous", "barbecue", "barbed", "barbel", "barber",
				"barbican", "barbiturate", "barcarole", "barcarolle", "bard", "bare", "bareback", "barebacked",
				"barefaced", "barefoot", "bareheaded", "barelegged", "barely", "bargain", "barge", "bargee", "baritone",
				"barium", "bark", "barker", "barley", "barleycorn", "barmaid", "barman", "barmy", "barn", "barnacle",
				"barnstorm", "barnyard", "barograph", "barometer", "baron", "baroness", "baronet", "baronetcy",
				"baronial", "barony", "baroque", "barque", "barrack", "barracks", "barracuda", "barrage", "barred",
				"barrel", "barren", "barricade", "barricades", "barrier", "barring", "barrister", "barrow", "bartender",
				"barter", "basalt", "base", "baseball", "baseboard", "baseless", "baseline", "basement", "bases",
				"bash", "bashful", "basic", "basically", "basics", "basil", "basilica", "basilisk", "basin", "basis",
				"bask", "basket", "basketball", "basketful", "basketry", "basketwork", "bass", "basset", "bassinet",
				"bassoon", "bast", "bastard", "bastardise", "bastardize", "bastardy", "baste", "bastinado", "bastion",
				"bat", "batch", "bated", "bath", "bathing", "bathos", "bathrobe", "bathroom", "baths", "bathtub",
				"bathysphere", "batik", "batiste", "batman", "baton", "bats", "batsman", "battalion", "batten",
				"batter", "battery", "battle", "battleax", "battleaxe", "battlefield", "battlements", "battleship",
				"batty", "bauble", "baulk", "bauxite", "bawd", "bawdy", "bawl", "bay", "bayonet", "bayou", "bazaar",
				"bazooka", "bbc", "beach", "beachcomber", "beachhead", "beachwear", "beacon", "bead", "beading",
				"beadle", "beady", "beagle", "beagling", "beak", "beaker", "beam", "bean", "beanpole", "beanstalk",
				"bear", "bearable", "beard", "bearded", "bearer", "bearing", "bearings", "bearish", "bearskin", "beast",
				"beastly", "beat", "beaten", "beater", "beatific", "beatification", "beatify", "beating", "beatitude",
				"beatitudes", "beatnik", "beau", "beaujolais", "beaut", "beauteous", "beautician", "beautiful",
				"beautify", "beauty", "beaver", "bebop", "becalmed", "because", "beck", "beckon", "become", "becoming",
				"bed", "bedaub", "bedbug", "bedclothes", "bedding", "bedeck", "bedevil", "bedewed", "bedfellow",
				"bedimmed", "bedlam", "bedouin", "bedpan", "bedpost", "bedraggled", "bedridden", "bedrock", "bedroom",
				"bedside", "bedsore", "bedspread", "bedstead", "bedtime", "bee", "beech", "beef", "beefcake",
				"beefeater", "beefsteak", "beefy", "beehive", "beeline", "been", "beer", "beery", "beeswax", "beet",
				"beetle", "beetling", "beetroot", "beeves", "befall", "befit", "befitting", "before", "beforehand",
				"befriend", "befuddle", "beg", "beget", "beggar", "beggarly", "beggary", "begin", "beginner",
				"beginning", "begone", "begonia", "begorra", "begot", "begotten", "begrudge", "beguile", "begum",
				"begun", "behalf", "behave", "behavior", "behaviorism", "behaviour", "behaviourism", "behead",
				"behemoth", "behest", "behind", "behindhand", "behold", "beholden", "behove", "beige", "being",
				"belabor", "belabour", "belated", "belay", "belch", "beleaguer", "belfry", "belie", "belief",
				"believable", "believe", "believer", "belittle", "bell", "belladonna", "bellboy", "belle", "bellflower",
				"bellicose", "belligerency", "belligerent", "bellow", "bellows", "belly", "bellyache", "bellyful",
				"belong", "belongings", "beloved", "below", "belt", "belted", "belting", "beltway", "bemoan", "bemused",
				"ben", "bench", "bencher", "bend", "bended", "bends", "beneath", "benedictine", "benediction",
				"benedictus", "benefaction", "benefactor", "benefice", "beneficent", "beneficial", "beneficiary",
				"benefit", "benevolence", "benevolent", "benighted", "benign", "benignity", "bent", "benumbed",
				"benzedrine", "benzene", "benzine", "bequeath", "bequest", "berate", "bereave", "bereaved",
				"bereavement", "bereft", "beret", "beriberi", "berk", "berry", "berserk", "berth", "beryl", "beseech",
				"beseem", "beset", "besetting", "beside", "besides", "besiege", "besmear", "besmirch", "besom",
				"besotted", "besought", "bespattered", "bespeak", "bespoke", "best", "bestial", "bestiality",
				"bestiary", "bestir", "bestow", "bestrew", "bestride", "bet", "beta", "betake", "betel", "bethel",
				"bethink", "betide", "betimes", "betoken", "betray", "betrayal", "betroth", "betrothal", "betrothed",
				"better", "betterment", "betters", "bettor", "between", "betwixt", "bevel", "beverage", "bevy",
				"bewail", "beware", "bewilder", "bewitch", "bey", "beyond", "bezique", "bhang", "bias", "bib", "bible",
				"biblical", "bibliographer", "bibliography", "bibliophile", "bibulous", "bicarb", "bicarbonate",
				"bicentenary", "bicentennial", "biceps", "bicker", "bicycle", "bid", "biddable", "bidding", "bide",
				"bidet", "biennial", "bier", "biff", "bifocals", "bifurcate", "big", "bigamist", "bigamous", "bigamy",
				"bighead", "bight", "bigot", "bigoted", "bigotry", "bigwig", "bijou", "bike", "bikini", "bilabial",
				"bilateral", "bilberry", "bile", "bilge", "bilingual", "bilious", "bilk", "bill", "billboard", "billet",
				"billfold", "billhook", "billiard", "billiards", "billion", "billow", "billposter", "billy", "biltong",
				"bimetallic", "bimetallism", "bimonthly", "bin", "binary", "bind", "binder", "bindery", "binding",
				"bindweed", "binge", "bingo", "binnacle", "binocular", "binoculars", "binomial", "biochemistry",
				"biodegradable", "biographer", "biographical", "biography", "biological", "biology", "biomedical",
				"bionic", "biosphere", "biotechnology", "bipartisan", "bipartite", "biped", "biplane", "birch", "bird",
				"birdie", "birdlime", "birdseed", "biretta", "biro", "birth", "birthday", "birthmark", "birthplace",
				"birthrate", "birthright", "biscuit", "bisect", "bisexual", "bishop", "bishopric", "bismuth", "bison",
				"bisque", "bistro", "bit", "bitch", "bitchy", "bite", "biting", "bitter", "bittern", "bitters",
				"bittersweet", "bitty", "bitumen", "bituminous", "bivalve", "bivouac", "biweekly", "bizarre", "blab",
				"blabber", "blabbermouth", "black", "blackamoor", "blackball", "blackberry", "blackbird", "blackboard",
				"blackcurrant", "blacken", "blackguard", "blackhead", "blacking", "blackjack", "blackleg", "blacklist",
				"blackly", "blackmail", "blackout", "blackshirt", "blacksmith", "blackthorn", "bladder", "blade",
				"blaeberry", "blah", "blame", "blameless", "blameworthy", "blanch", "blancmange", "bland",
				"blandishments", "blank", "blanket", "blare", "blarney", "blaspheme", "blasphemous", "blasphemy",
				"blast", "blasted", "blatant", "blather", "blaze", "blazer", "blazes", "blazing", "blazon", "blazonry",
				"bleach", "bleachers", "bleak", "bleary", "bleat", "bleed", "bleeder", "bleeding", "bleep", "blemish",
				"blench", "blend", "blender", "bless", "blessed", "blessing", "blether", "blew", "blight", "blighter",
				"blimey", "blimp", "blind", "blinder", "blinders", "blindfold", "blink", "blinkered", "blinkers",
				"blinking", "blip", "bliss", "blister", "blistering", "blithe", "blithering", "blitz", "blizzard",
				"bloated", "bloater", "blob", "bloc", "block", "blockade", "blockage", "blockbuster", "blockhead",
				"blockhouse", "bloke", "blond", "blood", "bloodbath", "bloodcurdling", "bloodhound", "bloodless",
				"bloodletting", "bloodshed", "bloodshot", "bloodstain", "bloodstock", "bloodstream", "bloodsucker",
				"bloodthirsty", "bloody", "bloom", "bloomer", "bloomers", "blooming", "blossom", "blot", "blotch",
				"blotter", "blotto", "blouse", "blow", "blower", "blowfly", "blowgun", "blowhard", "blowhole",
				"blowlamp", "blown", "blowout", "blowpipe", "blowsy", "blowy", "blowzy", "blubber", "bludgeon", "blue",
				"bluebag", "bluebeard", "bluebell", "blueberry", "bluebird", "bluebottle", "bluecoat", "bluefish",
				"bluejacket", "blueprint", "blues", "bluestocking", "bluff", "blunder", "blunderbuss", "blunt",
				"bluntly", "blur", "blurb", "blurt", "blush", "bluster", "blustery", "boa", "boar", "board", "boarder",
				"boarding", "boardinghouse", "boardroom", "boards", "boardwalk", "boast", "boaster", "boastful", "boat",
				"boater", "boathouse", "boatman", "boatswain", "bob", "bobbin", "bobby", "bobcat", "bobolink",
				"bobsleigh", "bobtail", "bobtailed", "bock", "bod", "bode", "bodice", "bodily", "boding", "bodkin",
				"body", "bodyguard", "bodywork", "boer", "boffin", "bog", "bogey", "boggle", "boggy", "bogie", "bogus",
				"bohemian", "boil", "boiler", "boisterous", "bold", "boldface", "boldfaced", "bole", "bolero", "boll",
				"bollard", "bollocks", "boloney", "bolshevik", "bolshevism", "bolshy", "bolster", "bolt", "bolthole",
				"bomb", "bombard", "bombardier", "bombardment", "bombast", "bomber", "bombproof", "bombshell",
				"bombsight", "bombsite", "bonanza", "bonbon", "bond", "bondage", "bonded", "bondholder", "bonds",
				"bone", "boned", "bonehead", "boner", "bonesetter", "boneshaker", "bonfire", "bongo", "bonhomie",
				"bonito", "bonkers", "bonnet", "bonny", "bonsai", "bonus", "bony", "bonzer", "boo", "boob", "boobs",
				"booby", "boodle", "boohoo", "book", "bookable", "bookbindery", "bookbinding", "bookcase", "bookend",
				"booking", "bookish", "bookkeeping", "booklet", "bookmaker", "bookmark", "bookmobile", "bookplate",
				"books", "bookseller", "bookshop", "bookstall", "bookwork", "bookworm", "boom", "boomerang", "boon",
				"boor", "boost", "booster", "boot", "bootblack", "booted", "bootee", "booth", "bootlace", "bootleg",
				"bootless", "boots", "bootstraps", "booty", "booze", "boozer", "boozy", "bop", "bopper", "boracic",
				"borage", "borax", "bordeaux", "bordello", "border", "borderer", "borderland", "borderline", "bore",
				"borealis", "borehole", "borer", "born", "borne", "boron", "borough", "borrow", "borrowing", "borscht",
				"borshcht", "borstal", "borzoi", "bosh", "bosom", "bosomy", "boss", "bossy", "bosun", "botanical",
				"botanise", "botanist", "botanize", "botany", "botch", "both", "bother", "botheration", "bothersome",
				"bottle", "bottleful", "bottleneck", "bottom", "bottomless", "botulism", "boudoir", "bouffant",
				"bougainvillaea", "bougainvillea", "bough", "bought", "bouillabaisse", "bouillon", "boulder",
				"boulevard", "bounce", "bouncer", "bouncing", "bouncy", "bound", "boundary", "bounden", "bounder",
				"boundless", "bounds", "bounteous", "bountiful", "bounty", "bouquet", "bourbon", "bourgeois",
				"bourgeoisie", "bourn", "bourne", "bourse", "bout", "boutique", "bouzouki", "bovine", "bovril",
				"bovver", "bow", "bowdlerise", "bowdlerize", "bowed", "bowel", "bowels", "bower", "bowerbird", "bowing",
				"bowl", "bowler", "bowlful", "bowline", "bowling", "bowls", "bowman", "bowser", "bowshot", "bowsprit",
				"bowwow", "box", "boxer", "boxful", "boxing", "boxwood", "boy", "boycott", "boyfriend", "boyhood",
				"boyish", "boys", "bra", "brace", "bracelet", "bracelets", "braces", "bracing", "bracken", "bracket",
				"brackish", "bract", "bradawl", "brae", "brag", "braggadocio", "braggart", "brahman", "braid",
				"braille", "brain", "brainchild", "brainless", "brainpan", "brains", "brainstorm", "brainwash",
				"brainwashing", "brainwave", "brainy", "braise", "brake", "bramble", "bran", "branch", "brand",
				"brandish", "brandy", "brash", "brass", "brasserie", "brassiere", "brassy", "brat", "bravado", "brave",
				"bravo", "bravura", "brawl", "brawn", "brawny", "bray", "brazen", "brazier", "bre", "breach", "bread",
				"breadbasket", "breadboard", "breadcrumb", "breaded", "breadfruit", "breadline", "breadth",
				"breadthways", "breadwinner", "break", "breakage", "breakaway", "breakdown", "breaker", "breakfast",
				"breakneck", "breakout", "breakthrough", "breakup", "breakwater", "bream", "breast", "breastbone",
				"breastplate", "breaststroke", "breastwork", "breath", "breathalyse", "breathalyser", "breathe",
				"breather", "breathing", "breathless", "breathtaking", "breathy", "breech", "breeches", "breed",
				"breeder", "breeding", "breeze", "breezeblock", "breezy", "brethren", "breve", "brevet", "breviary",
				"brevity", "brew", "brewer", "brewery", "briar", "bribe", "bribery", "brick", "brickbat", "brickfield",
				"bricklayer", "brickwork", "bridal", "bride", "bridegroom", "bridesmaid", "bridge", "bridgehead",
				"bridgework", "bridle", "brie", "brief", "briefcase", "briefing", "briefs", "brier", "brig", "brigade",
				"brigadier", "brigand", "brigandage", "brigantine", "bright", "brighten", "brill", "brilliancy",
				"brilliant", "brilliantine", "brim", "brimful", "brimfull", "brimstone", "brindled", "brine", "bring",
				"brink", "brinkmanship", "brioche", "briquet", "briquette", "brisk", "brisket", "bristle", "bristly",
				"bristols", "brit", "britches", "britisher", "briton", "brittle", "broach", "broad", "broadcast",
				"broadcasting", "broadcloth", "broaden", "broadloom", "broadminded", "broadsheet", "broadside",
				"broadsword", "broadways", "brocade", "broccoli", "brochure", "brogue", "broil", "broiler", "broke",
				"broken", "broker", "brolly", "bromide", "bromine", "bronchial", "bronchitis", "bronco", "brontosaurus",
				"bronze", "brooch", "brood", "broody", "brook", "broom", "broomstick", "broth", "brothel", "brother",
				"brotherhood", "brougham", "brought", "brouhaha", "brow", "browbeat", "brown", "brownie", "brownstone",
				"browse", "brucellosis", "bruin", "bruise", "bruiser", "bruising", "bruit", "brunch", "brunet",
				"brunette", "brunt", "brush", "brushwood", "brushwork", "brusque", "brutal", "brutalise", "brutality",
				"brutalize", "brute", "brutish", "bubble", "bubbly", "buccaneer", "buck", "buckboard", "bucked",
				"bucket", "buckle", "buckler", "buckram", "buckshee", "buckshot", "buckskin", "bucktooth", "buckwheat",
				"bucolic", "bud", "buddhism", "budding", "buddy", "budge", "budgerigar", "budget", "budgetary", "buff",
				"buffalo", "buffer", "buffet", "buffoon", "buffoonery", "bug", "bugaboo", "bugbear", "bugger",
				"buggered", "buggery", "buggy", "bughouse", "bugle", "bugrake", "buhl", "build", "builder", "building",
				"buildup", "bulb", "bulbous", "bulbul", "bulge", "bulk", "bulkhead", "bulky", "bull", "bulldog",
				"bulldoze", "bulldozer", "bullet", "bulletin", "bulletproof", "bullfight", "bullfighting", "bullfinch",
				"bullfrog", "bullheaded", "bullion", "bullnecked", "bullock", "bullring", "bullshit", "bully",
				"bullyboy", "bulrush", "bulwark", "bum", "bumble", "bumblebee", "bumboat", "bumf", "bummer", "bump",
				"bumper", "bumph", "bumpkin", "bumptious", "bumpy", "bun", "bunch", "bundle", "bung", "bungalow",
				"bunghole", "bungle", "bunion", "bunk", "bunker", "bunkered", "bunkhouse", "bunkum", "bunny", "bunting",
				"buoy", "buoyancy", "bur", "burberry", "burble", "burden", "burdensome", "burdock", "bureau",
				"bureaucracy", "bureaucrat", "bureaucratic", "burg", "burgeon", "burgess", "burgh", "burgher",
				"burglar", "burglary", "burgle", "burgomaster", "burgundy", "burial", "burlap", "burlesque", "burly",
				"burn", "burner", "burning", "burnish", "burnous", "burnouse", "burnt", "burp", "burr", "burro",
				"burrow", "bursar", "bursary", "burst", "burthen", "burton", "bury", "bus", "busby", "bush", "bushbaby",
				"bushed", "bushel", "bushwhack", "bushy", "business", "businesslike", "businessman", "busk", "busker",
				"busman", "bust", "bustard", "buster", "bustle", "busy", "busybody", "but", "butane", "butch",
				"butcher", "butchery", "butler", "butt", "butter", "buttercup", "butterfingers", "butterfly",
				"buttermilk", "butterscotch", "buttery", "buttock", "buttocks", "button", "buttonhole", "buttonhook",
				"buttons", "buttress", "buxom", "buy", "buyer", "buzz", "buzzard", "buzzer", "bye", "byelaw", "bygone",
				"bygones", "bylaw", "bypass", "byplay", "byre", "bystander", "byway", "byways", "byword", "byzantine",
				"cab", "cabal", "cabaret", "cabbage", "cabbie", "cabby", "cabdriver", "caber", "cabin", "cabinet",
				"cable", "cablegram", "caboodle", "caboose", "cabriolet", "cacao", "cache", "cachet", "cachou",
				"cackle", "cacophony", "cactus", "cad", "cadaver", "cadaverous", "caddie", "caddy", "cadence",
				"cadenza", "cadet", "cadge", "cadi", "cadmium", "cadre", "caerphilly", "caesura", "cafeteria",
				"caffeine", "caftan", "cage", "cagey", "cahoots", "caiman", "caique", "cairn", "caisson", "cajole",
				"cake", "calabash", "calaboose", "calamitous", "calamity", "calcify", "calcination", "calcine",
				"calcium", "calculable", "calculate", "calculating", "calculation", "calculator", "calculus", "caldron",
				"calendar", "calender", "calends", "calf", "calfskin", "caliber", "calibrate", "calibration", "calibre",
				"calico", "caliper", "calipers", "caliph", "caliphate", "calisthenic", "calisthenics", "calk", "call",
				"calla", "callboy", "caller", "calligraphy", "calling", "calliper", "callipers", "callisthenic",
				"callisthenics", "callous", "callow", "callus", "calm", "calomel", "calorie", "calorific", "calumniate",
				"calumny", "calvary", "calve", "calves", "calvinism", "calypso", "calyx", "cam", "camaraderie",
				"camber", "cambric", "came", "camel", "camelhair", "camellia", "camembert", "cameo", "camera",
				"cameraman", "camisole", "camomile", "camouflage", "camp", "campaign", "campanile", "campanology",
				"campanula", "camper", "campfire", "campground", "camphor", "camphorated", "campion", "campsite",
				"campus", "camshaft", "can", "canal", "canalise", "canalize", "canard", "canary", "canasta", "cancan",
				"cancel", "cancellation", "cancer", "cancerous", "candela", "candelabrum", "candid", "candidate",
				"candidature", "candidly", "candied", "candle", "candlelight", "candlemas", "candlepower",
				"candlestick", "candlewick", "candor", "candour", "candy", "candyfloss", "candytuft", "cane", "canine",
				"canis", "canister", "canker", "canna", "cannabis", "canned", "cannelloni", "cannery", "cannibal",
				"cannibalise", "cannibalism", "cannibalize", "cannon", "cannonade", "cannonball", "cannot", "canny",
				"canoe", "canon", "canonical", "canonicals", "canonise", "canonize", "canoodle", "canopy", "canst",
				"cant", "cantab", "cantabrigian", "cantaloup", "cantaloupe", "cantankerous", "cantata", "canteen",
				"canter", "canticle", "cantilever", "canto", "canton", "cantonment", "cantor", "canvas", "canvass",
				"canyon", "cap", "capabilities", "capability", "capable", "capacious", "capacity", "caparison", "cape",
				"caper", "capillarity", "capillary", "capital", "capitalisation", "capitalise", "capitalism",
				"capitalist", "capitalization", "capitalize", "capitals", "capitation", "capitol", "capitulate",
				"capitulation", "capitulations", "capon", "capriccio", "caprice", "capricious", "capricorn", "capsicum",
				"capsize", "capstan", "capsule", "captain", "caption", "captious", "captivate", "captive", "captivity",
				"captor", "capture", "car", "carafe", "caramel", "carapace", "carat", "caravan", "caravanning",
				"caravanserai", "caraway", "carbide", "carbine", "carbohydrate", "carbolic", "carbon", "carbonated",
				"carbonation", "carboniferous", "carbonise", "carbonize", "carborundum", "carboy", "carbuncle",
				"carburetor", "carburettor", "carcase", "carcass", "carcinogen", "card", "cardamom", "cardboard",
				"cardiac", "cardigan", "cardinal", "cardpunch", "cards", "cardsharp", "care", "careen", "career",
				"careerist", "carefree", "careful", "careless", "caress", "caret", "caretaker", "careworn", "cargo",
				"caribou", "caricature", "caries", "carillon", "carious", "carmelite", "carmine", "carnage", "carnal",
				"carnation", "carnelian", "carnival", "carnivore", "carnivorous", "carob", "carol", "carotid",
				"carousal", "carouse", "carousel", "carp", "carpal", "carpenter", "carpentry", "carpet", "carpetbag",
				"carpetbagger", "carpeting", "carport", "carpus", "carriage", "carriageway", "carrier", "carrion",
				"carrot", "carroty", "carrousel", "carry", "carryall", "carrycot", "carryout", "carsick", "cart",
				"cartage", "cartel", "carter", "carthorse", "cartilage", "cartilaginous", "cartographer", "cartography",
				"carton", "cartoon", "cartridge", "cartwheel", "carve", "carver", "carving", "caryatid", "cascade",
				"cascara", "case", "casebook", "casein", "casework", };
	}

	static class KStemData2 {
		private KStemData2() {
		}

		static String[] data = { "cash", "cashew", "cashier", "cashmere", "casing", "casino", "cask", "casket",
				"casque", "cassava", "casserole", "cassette", "cassock", "cassowary", "cast", "castanets", "castaway",
				"castellated", "caster", "castigate", "casting", "castle", "castor", "castrate", "casual", "casualty",
				"casuist", "casuistry", "cat", "cataclysm", "catacomb", "catafalque", "catalepsy", "catalog",
				"catalogue", "catalpa", "catalysis", "catalyst", "catamaran", "catapult", "cataract", "catarrh",
				"catastrophe", "catatonic", "catcall", "catch", "catcher", "catching", "catchpenny", "catchphrase",
				"catchword", "catchy", "catechise", "catechism", "catechize", "categorical", "categorise", "categorize",
				"category", "cater", "caterer", "caterpillar", "caterwaul", "catfish", "catgut", "catharsis",
				"cathartic", "cathedral", "catheter", "cathode", "catholic", "catholicism", "catholicity", "catkin",
				"catnap", "catnip", "catsup", "cattle", "catty", "catwalk", "caucus", "caudal", "caught", "caul",
				"cauldron", "cauliflower", "caulk", "causal", "causality", "causation", "causative", "cause",
				"causeless", "causeway", "caustic", "cauterise", "cauterize", "caution", "cautionary", "cautious",
				"cavalcade", "cavalier", "cavalry", "cavalryman", "cave", "caveat", "caveman", "cavern", "cavernous",
				"caviar", "caviare", "cavil", "cavity", "cavort", "cavy", "caw", "cay", "cayman", "cease", "ceaseless",
				"cedar", "cede", "cedilla", "ceiling", "celandine", "celebrant", "celebrate", "celebrated",
				"celebration", "celebrity", "celerity", "celery", "celestial", "celibacy", "celibate", "cell", "cellar",
				"cellarage", "cellist", "cello", "cellophane", "cellular", "celluloid", "cellulose", "celsius",
				"celtic", "cement", "cemetery", "cenotaph", "censor", "censorious", "censorship", "censure", "census",
				"cent", "centaur", "centavo", "centenarian", "centenary", "centennial", "center", "centerboard",
				"centerpiece", "centigrade", "centigram", "centigramme", "centime", "centimeter", "centimetre",
				"centipede", "central", "centralise", "centralism", "centralize", "centre", "centreboard",
				"centrepiece", "centrifugal", "centrifuge", "centripetal", "centrist", "centurion", "century",
				"cephalic", "ceramic", "ceramics", "cereal", "cerebellum", "cerebral", "cerebration", "cerebrum",
				"ceremonial", "ceremonious", "ceremony", "cerise", "cert", "certain", "certainly", "certainty",
				"certifiable", "certificate", "certificated", "certify", "certitude", "cerulean", "cervical", "cervix",
				"cessation", "cession", "cesspit", "cetacean", "chablis", "chaconne", "chafe", "chaff", "chaffinch",
				"chagrin", "chain", "chair", "chairman", "chairmanship", "chairperson", "chairwoman", "chaise",
				"chalet", "chalice", "chalk", "chalky", "challenge", "challenging", "chamber", "chamberlain",
				"chambermaid", "chambers", "chameleon", "chamiomile", "chamois", "chamomile", "champ", "champagne",
				"champaign", "champion", "championship", "chance", "chancel", "chancellery", "chancellor", "chancery",
				"chancy", "chandelier", "chandler", "change", "changeable", "changeless", "changeling", "changeover",
				"channel", "chant", "chanterelle", "chanticleer", "chantry", "chanty", "chaos", "chaotic", "chap",
				"chapel", "chapelgoer", "chaperon", "chaperone", "chapfallen", "chaplain", "chaplaincy", "chaplet",
				"chaps", "chapter", "char", "charabanc", "character", "characterise", "characteristic",
				"characterization", "characterize", "characterless", "charade", "charades", "charcoal", "chard",
				"charge", "chargeable", "charged", "charger", "chariot", "charioteer", "charisma", "charismatic",
				"charitable", "charity", "charlady", "charlatan", "charleston", "charlock", "charlotte", "charm",
				"charmer", "charming", "chart", "charter", "chartreuse", "charwoman", "chary", "charybdis", "chase",
				"chaser", "chasm", "chassis", "chaste", "chasten", "chastise", "chastisement", "chastity", "chasuble",
				"chat", "chatelaine", "chattel", "chatter", "chatterbox", "chatty", "chauffeur", "chauvinism",
				"chauvinist", "cheap", "cheapen", "cheapskate", "cheat", "check", "checkbook", "checked", "checker",
				"checkerboard", "checkers", "checklist", "checkmate", "checkoff", "checkout", "checkpoint", "checkrail",
				"checkrein", "checkroom", "checkup", "cheddar", "cheek", "cheekbone", "cheeky", "cheep", "cheer",
				"cheerful", "cheering", "cheerio", "cheerleader", "cheerless", "cheers", "cheery", "cheese",
				"cheesecake", "cheesecloth", "cheeseparing", "cheetah", "chef", "chem", "chemical", "chemise",
				"chemist", "chemistry", "chemotherapy", "chenille", "cheque", "chequebook", "chequer", "cherish",
				"cheroot", "cherry", "cherub", "chervil", "chess", "chessboard", "chessman", "chest", "chesterfield",
				"chestnut", "chesty", "chevalier", "chevron", "chevvy", "chevy", "chew", "chi", "chianti",
				"chiaroscuro", "chic", "chicanery", "chicano", "chichi", "chick", "chicken", "chickenfeed",
				"chickenhearted", "chickpea", "chickweed", "chicle", "chicory", "chide", "chief", "chiefly",
				"chieftain", "chieftainship", "chiffon", "chiffonier", "chiffonnier", "chigger", "chignon", "chihuahua",
				"chilblain", "child", "childbearing", "childbirth", "childhood", "childish", "childlike", "chile",
				"chill", "chiller", "chilli", "chilly", "chimaera", "chime", "chimera", "chimerical", "chimney",
				"chimneybreast", "chimneypiece", "chimneypot", "chimneystack", "chimneysweep", "chimpanzee", "chin",
				"china", "chinatown", "chinaware", "chinchilla", "chine", "chink", "chinless", "chinook", "chinstrap",
				"chintz", "chinwag", "chip", "chipboard", "chipmunk", "chippendale", "chipping", "chippy", "chiromancy",
				"chiropody", "chiropractic", "chirp", "chirpy", "chisel", "chiseler", "chiseller", "chit", "chitchat",
				"chivalrous", "chivalry", "chive", "chivvy", "chivy", "chloride", "chlorinate", "chlorine",
				"chloroform", "chlorophyll", "chock", "chocolate", "choice", "choir", "choirboy", "choirmaster",
				"choke", "choker", "chokey", "choky", "choler", "cholera", "choleric", "cholesterol", "chomp", "choose",
				"choosey", "choosy", "chop", "chopfallen", "chophouse", "chopper", "choppers", "choppy", "chopstick",
				"choral", "chorale", "chord", "chore", "choreographer", "choreography", "chorine", "chorister",
				"chortle", "chorus", "chose", "chosen", "chow", "chowder", "christ", "christen", "christendom",
				"christening", "christian", "christianity", "christlike", "christmastime", "chromatic", "chrome",
				"chromium", "chromosome", "chronic", "chronicle", "chronograph", "chronological", "chronology",
				"chronometer", "chrysalis", "chrysanthemum", "chub", "chubby", "chuck", "chuckle", "chug", "chukker",
				"chum", "chummy", "chump", "chunk", "chunky", "church", "churchgoer", "churching", "churchwarden",
				"churchyard", "churl", "churlish", "churn", "chute", "chutney", "cia", "cicada", "cicatrice",
				"cicerone", "cid", "cider", "cif", "cigar", "cigaret", "cigarette", "cinch", "cincture", "cinder",
				"cinderella", "cinders", "cine", "cinema", "cinematograph", "cinematography", "cinnamon", "cinquefoil",
				"cipher", "circa", "circadian", "circle", "circlet", "circuit", "circuitous", "circular", "circularise",
				"circularize", "circulate", "circulation", "circumcise", "circumcision", "circumference", "circumflex",
				"circumlocution", "circumnavigate", "circumscribe", "circumscription", "circumspect", "circumstance",
				"circumstances", "circumstantial", "circumvent", "circus", "cirque", "cirrhosis", "cirrus", "cissy",
				"cistern", "citadel", "citation", "cite", "citizen", "citizenry", "citizenship", "citron", "citrous",
				"citrus", "city", "civet", "civic", "civics", "civies", "civil", "civilian", "civilisation", "civilise",
				"civility", "civilization", "civilize", "civilly", "civvies", "clack", "clad", "claim", "claimant",
				"clairvoyance", "clairvoyant", "clam", "clambake", "clamber", "clammy", "clamor", "clamorous",
				"clamour", "clamp", "clampdown", "clamshell", "clan", "clandestine", "clang", "clanger", "clangor",
				"clangour", "clank", "clannish", "clansman", "clap", "clapboard", "clapper", "clapperboard", "clappers",
				"claptrap", "claque", "claret", "clarification", "clarify", "clarinet", "clarinetist", "clarinettist",
				"clarion", "clarity", "clarts", "clash", "clasp", "class", "classic", "classical", "classicism",
				"classicist", "classics", "classification", "classified", "classify", "classless", "classmate",
				"classroom", "classy", "clatter", "clause", "claustrophobia", "claustrophobic", "clavichord",
				"clavicle", "claw", "clay", "claymore", "clean", "cleaner", "cleanliness", "cleanly", "cleanse",
				"cleanser", "cleanup", "clear", "clearance", "clearing", "clearinghouse", "clearly", "clearout",
				"clearway", "cleat", "cleavage", "cleave", "cleaver", "clef", "cleft", "clematis", "clemency",
				"clement", "clench", "clerestory", "clergy", "clergyman", "clerical", "clerihew", "clerk", "clever",
				"clew", "click", "client", "clientele", "cliff", "cliffhanger", "climacteric", "climactic", "climate",
				"climatic", "climatology", "climax", "climb", "climber", "clime", "clinch", "clincher", "cline",
				"cling", "clinging", "clingy", "clinic", "clinical", "clink", "clinker", "clip", "clipboard", "clipper",
				"clippers", "clippie", "clipping", "clique", "cliquey", "cliquish", "clitoris", "cloaca", "cloak",
				"cloakroom", "clobber", "cloche", "clock", "clockwise", "clockwork", "clod", "cloddish", "clodhopper",
				"clog", "cloggy", "cloister", "clone", "clop", "close", "closed", "closedown", "closefisted", "closet",
				"closure", "clot", "cloth", "clothe", "clothes", "clothesbasket", "clotheshorse", "clothesline",
				"clothier", "clothing", "cloture", "cloud", "cloudbank", "cloudburst", "cloudless", "cloudy", "clout",
				"clove", "cloven", "clover", "cloverleaf", "clown", "clownish", "cloy", "club", "clubbable", "clubfoot",
				"clubhouse", "cluck", "clue", "clueless", "clump", "clumsy", "clung", "cluster", "clutch", "clutches",
				"clutter", "coach", "coachbuilder", "coachman", "coachwork", "coadjutor", "coagulant", "coagulate",
				"coal", "coalbunker", "coalesce", "coalface", "coalfield", "coalhole", "coalhouse", "coalition",
				"coalmine", "coalscuttle", "coarse", "coarsen", "coast", "coastal", "coaster", "coastguard",
				"coastguardsman", "coastline", "coastwise", "coat", "coating", "coax", "cob", "cobalt", "cobber",
				"cobble", "cobbler", "cobblers", "cobblestone", "cobra", "cobweb", "cocaine", "coccyx", "cochineal",
				"cochlea", "cock", "cockade", "cockatoo", "cockchafer", "cockcrow", "cockerel", "cockeyed", "cockfight",
				"cockhorse", "cockle", "cockleshell", "cockney", "cockpit", "cockroach", "cockscomb", "cocksure",
				"cocktail", "cocky", "coco", "cocoa", "coconut", "cocoon", "cod", "coda", "coddle", "code", "codeine",
				"codex", "codger", "codicil", "codify", "codling", "codpiece", "codswallop", "coed", "coeducation",
				"coefficient", "coelacanth", "coequal", "coerce", "coercion", "coercive", "coeternal", "coeval",
				"coexist", "coexistence", "coffee", "coffeepot", "coffer", "cofferdam", "coffers", "coffin", "cog",
				"cogency", "cogent", "cogitate", "cogitation", "cognac", "cognate", "cognition", "cognitive",
				"cognizance", "cognizant", "cognomen", "cognoscenti", "cogwheel", "cohabit", "cohere", "coherence",
				"coherent", "cohesion", "cohesive", "cohort", "coif", "coiffeur", "coiffure", "coil", "coin", "coinage",
				"coincide", "coincidence", "coincident", "coincidental", "coir", "coitus", "coke", "col", "cola",
				"colander", "cold", "coleslaw", "coley", "colic", "colicky", "colitis", "collaborate", "collaboration",
				"collaborationist", "collage", "collapse", "collapsible", "collar", "collarbone", "collate",
				"collateral", "collation", "colleague", "collect", "collected", "collection", "collective",
				"collectivise", "collectivism", "collectivize", "collector", "colleen", "college", "collegiate",
				"collide", "collie", "collier", "colliery", "collision", "collocate", "collocation", "colloquial",
				"colloquialism", "colloquy", "collude", "collusion", "collywobbles", "cologne", "colon", "colonel",
				"colonial", "colonialism", "colonialist", "colonies", "colonise", "colonist", "colonize", "colonnade",
				"colony", "color", "coloration", "coloratura", "colored", "colorfast", "colorful", "coloring",
				"colorless", "colors", "colossal", "colossally", "colossus", "colostrum", "colour", "coloured",
				"colourfast", "colourful", "colouring", "colourless", "colours", "colt", "colter", "coltish",
				"columbine", "column", "columnist", "coma", "comatose", "comb", "combat", "combatant", "combative",
				"comber", "combination", "combinations", "combinatorial", "combine", "combo", "combustible",
				"combustion", "come", "comeback", "comecon", "comedian", "comedienne", "comedown", "comedy", "comely",
				"comer", "comestible", "comet", "comfit", "comfort", "comfortable", "comforter", "comfrey", "comfy",
				"comic", "comical", "comics", "cominform", "coming", "comintern", "comity", "comma", "command",
				"commandant", "commandeer", "commander", "commanding", "commandment", "commando", "commemorate",
				"commemoration", "commemorative", "commence", "commencement", "commend", "commendable", "commendation",
				"commendatory", "commensurable", "commensurate", "comment", "commentary", "commentate", "commentator",
				"commerce", "commercial", "commercialise", "commercialism", "commercialize", "commie", "commiserate",
				"commiseration", "commissar", "commissariat", "commissary", "commission", "commissionaire",
				"commissioner", "commit", "commitment", "committal", "committed", "committee", "committeeman",
				"commode", "commodious", "commodity", "commodore", "common", "commonage", "commonalty", "commoner",
				"commonly", "commonplace", "commons", "commonweal", "commonwealth", "commotion", "communal", "commune",
				"communicable", "communicant", "communicate", "communication", "communications", "communicative",
				"communion", "communism", "communist", "community", "commutable", "commutation", "commutative",
				"commutator", "commute", "commuter", "compact", "compacted", "companion", "companionable",
				"companionship", "companionway", "company", "comparable", "comparative", "comparatively", "compare",
				"comparison", "compartment", "compartmentalise", "compartmentalize", "compass", "compassion",
				"compassionate", "compatibility", "compatible", "compatriot", "compeer", "compel", "compendious",
				"compendium", "compensate", "compensation", "compensatory", "compere", "compete", "competence",
				"competent", "competition", "competitive", "competitor", "compilation", "compile", "complacency",
				"complacent", "complain", "complainant", "complaint", "complaisance", "complaisant", "complement",
				"complementary", "complete", "completely", "completion", "complex", "complexion", "complexity",
				"compliance", "compliant", "complicate", "complicated", "complication", "complicity", "compliment",
				"complimentary", "compliments", "complin", "compline", "comply", "compo", "component", "comport",
				"comportment", "compose", "composer", "composite", "composition", "compositor", "compost", "composure",
				"compote", "compound", "comprehend", "comprehensible", "comprehension", "comprehensive", "compress",
				"compressible", "compression", "compressor", "comprise", "compromise", "comptometer", "comptroller",
				"compulsion", "compulsive", "compulsory", "compunction", "computation", "compute", "computer",
				"computerize", "comrade", "comradeship", "coms", "con", "concatenate", "concatenation", "concave",
				"concavity", "conceal", "concealment", "concede", "conceit", "conceited", "conceivable", "conceive",
				"concentrate", "concentrated", "concentration", "concentric", "concept", "conception", "conceptual",
				"conceptualise", "conceptualize", "concern", "concerned", "concernedly", "concerning", "concert",
				"concerted", "concertgoer", "concertina", "concertmaster", "concerto", "concession", "concessionaire",
				"concessive", "conch", "conchology", "concierge", "conciliate", "conciliation", "conciliatory",
				"concise", "concision", "conclave", "conclude", "conclusion", "conclusive", "concoct", "concoction",
				"concomitance", "concomitant", "concord", "concordance", "concordant", "concordat", "concourse",
				"concrete", "concubinage", "concubine", "concupiscence", "concur", "concurrence", "concurrent",
				"concuss", "concussion", "condemn", "condemnation", "condensation", "condense", "condenser",
				"condescend", "condescension", "condign", "condiment", "condition", "conditional", "conditions",
				"condole", "condolence", "condom", "condominium", "condone", "condor", "conduce", "conducive",
				"conduct", "conduction", "conductive", "conductivity", "conductor", "conduit", "cone", "coney",
				"confabulate", "confabulation", "confection", "confectioner", "confectionery", "confederacy",
				"confederate", "confederation", "confer", "conference", "confess", "confessed", "confession",
				"confessional", "confessor", "confetti", "confidant", "confide", "confidence", "confident",
				"confidential", "confiding", "configuration", "confine", "confinement", "confines", "confirm",
				"confirmation", "confirmed", "confiscate", "confiscatory", "conflagration", "conflate", "conflict",
				"confluence", "conform", "conformable", "conformation", "conformist", "conformity", "confound",
				"confounded", "confraternity", "confront", "confrontation", "confucian", "confucianism", "confuse",
				"confusion", "confute", "conga", "congeal", "congenial", "congenital", "congest", "congestion",
				"conglomerate", "conglomeration", "congrats", "congratulate", "congratulations", "congratulatory",
				"congregate", "congregation", "congregational", "congregationalism", "congress", "congressional",
				"congressman", "congruent", "congruity", "congruous", "conic", "conical", "conifer", "coniferous",
				"conj", "conjectural", "conjecture", "conjoin", "conjoint", "conjugal", "conjugate", "conjugation",
				"conjunction", "conjunctiva", "conjunctive", "conjunctivitis", "conjuncture", "conjure", "conjurer",
				"conjuror", "conk", "conker", "conkers", "connect", "connected", "connection", "connective",
				"connexion", "connivance", "connive", "connoisseur", "connotation", "connotative", "connote",
				"connubial", "conquer", "conquest", "conquistador", "consanguineous", "consanguinity", "conscience",
				"conscientious", "conscious", "consciousness", "conscript", "conscription", "consecrate",
				"consecration", "consecutive", "consensus", "consent", "consequence", "consequent", "consequential",
				"consequently", "conservancy", "conservation", "conservationist", "conservatism", "conservative",
				"conservatoire", "conservatory", "conserve", "consider", "considerable", "considerably", "considerate",
				"consideration", "considered", "considering", "consign", "consignee", "consigner", "consignment",
				"consignor", "consist", "consistency", "consistent", "consistory", "consolation", "consolatory",
				"console", "consolidate", "consols", "consonance", "consonant", "consort", "consortium", "conspectus",
				"conspicuous", "conspiracy", "conspirator", "conspiratorial", "conspire", "constable", "constabulary",
				"constancy", "constant", "constellation", "consternation", "constipate", "constipation", "constituency",
				"constituent", "constitute", "constitution", "constitutional", "constitutionalism", "constitutionally",
				"constitutive", "constrain", "constrained", "constraint", "constrict", "constriction", "constrictor",
				"construct", "construction", "constructive", "constructor", "construe", "consubstantiation", "consul",
				"consular", "consulate", "consult", "consultancy", "consultant", "consultation", "consultative",
				"consulting", "consume", "consumer", "consummate", "consummation", "consumption", "consumptive",
				"contact", "contagion", "contagious", "contain", "contained", "container", "containerise",
				"containerize", "containment", "contaminate", "contamination", "contemplate", "contemplation",
				"contemplative", "contemporaneous", "contemporary", "contempt", "contemptible", "contemptuous",
				"contend", "contender", "content", "contented", "contention", "contentious", "contentment", "contents",
				"contest", "contestant", "context", "contextual", "contiguity", "contiguous", "continence", "continent",
				"continental", "contingency", "contingent", "continual", "continuance", "continuation", "continue",
				"continuity", "continuo", "continuous", "continuum", "contort", "contortion", "contortionist",
				"contour", "contraband", "contrabass", "contraception", "contraceptive", "contract", "contractile",
				"contraction", "contractor", "contractual", "contradict", "contradiction", "contradictory",
				"contradistinction", "contrail", "contraindication", "contralto", "contraption", "contrapuntal",
				"contrariety", "contrariwise", "contrary", "contrast", "contravene", "contravention", "contretemps",
				"contribute", "contribution", "contributor", "contributory", "contrite", "contrition", "contrivance",
				"contrive", "contrived", "control", "controller", "controversial", "controversy", "controvert",
				"contumacious", "contumacy", "contumelious", "contumely", "contuse", "contusion", "conundrum",
				"conurbation", "convalesce", "convalescence", "convalescent", "convection", "convector", "convene",
				"convener", "convenience", "convenient", "convenor", "convent", "conventicle", "convention",
				"conventional", "conventionality", "converge", "conversant", "conversation", "conversational",
				"conversationalist", "conversazione", "converse", "conversion", "convert", "converter", "convertible",
				"convex", "convexity", "convey", "conveyance", "conveyancer", "conveyancing", "conveyer", "conveyor",
				"convict", "conviction", "convince", "convinced", "convincing", "convivial", "convocation", "convoke",
				"convoluted", "convolution", "convolvulus", "convoy", "convulse", "convulsion", "convulsive", "cony",
				"coo", "cook", "cooker", "cookery", "cookhouse", "cookie", "cooking", "cookout", "cool", "coolant",
				"cooler", "coolie", "coon", "coop", "cooper", "cooperate", "cooperation", "cooperative", "coordinate",
				"coordinates", "coordination", "coot", "cop", "cope", "copeck", "copier", "copilot", "coping",
				"copingstone", "copious", "copper", "copperhead", "copperplate", "coppersmith", "coppice", "copra",
				"coptic", "copula", "copulate", "copulative", "copy", "copybook", "copyboy", "copycat", "copydesk",
				"copyhold", "copyist", "copyright", "copywriter", "coquetry", "coquette", "cor", "coracle", "coral",
				"corbel", "cord", "cordage", "cordial", "cordiality", "cordially", "cordillera", "cordite", "cordon",
				"cords", "corduroy", "core", "corelate", "coreligionist", "corer", "corespondent", "corgi", "coriander",
				"corinthian", "cork", "corkage", "corked", "corker", "corkscrew", "corm", "cormorant", "corn",
				"corncob", "corncrake", "cornea", "cornelian", "corner", "cornerstone", "cornet", "cornfield",
				"cornflakes", "cornflower", "cornice", "cornish", "cornucopia", "corny", "corolla", "corollary",
				"corona", "coronary", "coronation", "coroner", "coronet", "corpora", "corporal", "corporate",
				"corporation", "corporeal", "corps", "corpse", "corpulence", "corpulent", "corpus", "corpuscle",
				"corral", "correct", "correction", "correctitude", "corrective", "correlate", "correlation",
				"correlative", "correspond", "correspondence", "correspondent", "corresponding", "corridor", "corrie",
				"corrigendum", "corroborate", "corroboration", "corroborative", "corroboree", "corrode", "corrosion",
				"corrosive", "corrugate", "corrugation", "corrupt", "corruption", "corsage", "corsair", "corse",
				"corselet", "corset", "cortex", "cortisone", "corundum", "coruscate", "corvette", "cos", "cosh",
				"cosignatory", "cosine", "cosmetic", "cosmetician", "cosmic", "cosmogony", "cosmology", "cosmonaut",
				"cosmopolitan", "cosmos", "cosset", "cost", "costermonger", "costive", "costly", "costs", "costume",
				"costumier", "cosy", "cot", "cotangent", "cote", "coterie", "coterminous", "cotillion", "cottage",
				"cottager", "cottar", "cotter", "cotton", "cottonseed", "cottontail", "cotyledon", "couch", "couchant",
				"couchette", "cougar", "cough", "could", "couldst", "coulter", "council", "councillor", "counsel",
				"counsellor", "counselor", "count", "countable", "countdown", "countenance", "counter", "counteract",
				"counterattack", "counterattraction", "counterbalance", "counterblast", "counterclaim",
				"counterclockwise", "counterespionage", "counterfeit", "counterfoil", "counterintelligence",
				"counterirritant", "countermand", "countermarch", "countermeasure", "counteroffensive", "counterpane",
				"counterpart", "counterpoint", "counterpoise", "countersign", "countersink", "countertenor",
				"countervail", "countess", "countinghouse", "countless", "countrified", "country", "countryman",
				"countryside", "county", "coup", "couple", "couplet", "coupling", "coupon", "courage", "courageous",
				"courgette", "courier", "course", "courser", "coursing", "court", "courteous", "courtesan", "courtesy",
				"courthouse", "courtier", "courting", "courtly", "courtroom", "courtship", "courtyard", "couscous",
				"cousin", "couture", "cove", "coven", "covenant", "coventry", "cover", "coverage", "covering",
				"coverlet", "covert", "covet", "covetous", "covey", "cow", "coward", "cowardice", "cowardly", "cowbell",
				"cowboy", "cowcatcher", "cower", "cowgirl", "cowhand", "cowheel", "cowherd", "cowhide", "cowl",
				"cowlick", "cowling", "cowman", "cowpat", "cowpox", "cowrie", "cowry", "cowshed", "cowslip", "cox",
				"coxcomb", "coy", "coyote", "coypu", "cozen", "cozy", "cpa", "crab", "crabbed", "crabby", "crabgrass",
				"crabwise", "crack", "crackbrained", "crackdown", "cracked", "cracker", "crackers", "crackle",
				"crackleware", "crackling", "crackpot", "cracksman", "crackup", "cradle", "craft", "craftsman",
				"crafty", "crag", "craggy", "crake", "cram", "crammer", "cramp", "cramped", "crampon", "cramps",
				"cranberry", "crane", "cranial", "cranium", "crank", "crankshaft", "cranky", "cranny", "crap", "crape",
				"crappy", "craps", "crash", "crashing", "crass", "crate", "crater", "cravat", "crave", "craven",
				"craving", "crawl", "crawler", "crawlers", "crayfish", "crayon", "craze", "crazy", "creak", "creaky",
				"cream", "creamer", "creamery", "creamy", "crease", "create", "creation", "creative", "creativity",
				"creator", "creature", "credence", "credentials", "credibility", "credible", "credit", "creditable",
				"creditor", "credo", "credulous", "creed", "creek", "creel", "creep", "creeper", "creepers", "creeps",
				"creepy", "cremate", "crematorium", "crenelated", "crenellated", "creole", "creosote", "crept",
				"crepuscular", "crescendo", "crescent", "cress", "crest", "crested", "crestfallen", "cretaceous",
				"cretin", "cretonne", "crevasse", "crevice", "crew", "crewman", "crib", "cribbage", "crick", "cricket",
				"cricketer", "crier", "cries", "crikey", "crime", "criminal", "criminology", "crimp", "crimplene",
				"crimson", "cringe", "crinkle", "crinkly", "crinoid", "crinoline", "cripes", "cripple", "crisis",
				"crisp", "crispy", "crisscross", "criterion", "critic", "critical", "criticise", "criticism",
				"criticize", "critique", "critter", "croak", "crochet", "crock", "crockery", "crocodile", "crocus",
				"croft", "crofter", "croissant", "cromlech", "crone", "crony", "crook", "crooked", "croon", "crooner",
				"crop", "cropper", "croquet", "croquette", "crore", "crosier", "cross", "crossbar", "crossbeam",
				"crossbenches", "crossbones", "crossbow", "crossbred", "crossbreed", "crosscheck", "crosscurrent",
				"crosscut", "crossfire", "crossing", "crossover", "crosspatch", "crosspiece", "crossply", "crossroad",
				"crossroads", "crosstree", "crosswalk", "crosswind", "crosswise", "crossword", "crotch", "crotchet",
				"crotchety", "crouch", "croup", "croupier", "crouton", "crow", "crowbar", "crowd", "crowded",
				"crowfoot", "crown", "crozier", "crucial", "crucible", "crucifix", "crucifixion", "cruciform",
				"crucify", "crude", "crudity", "cruel", "cruelty", "cruet", "cruise", "cruiser", "crumb", "crumble",
				"crumbly", "crummy", "crumpet", "crumple", "crunch", "crupper", "crusade", "cruse", "crush", "crust",
				"crustacean", "crusty", "crutch", "crux", "cry", "crybaby", "crying", "crypt", "cryptic", "cryptogram",
				"cryptography", "crystal", "crystalline", "crystallise", "crystallize", "cub", "cubbyhole", "cube",
				"cubic", "cubical", "cubicle", "cubism", "cubit", "cubs", "cuckold", "cuckoldry", "cuckoo", "cucumber",
				"cud", "cuddle", "cuddlesome", "cuddly", "cudgel", "cue", "cuff", "cuffs", "cuirass", "cuisine",
				"culinary", "cull", "cullender", "culminate", "culmination", "culotte", "culottes", "culpable",
				"culprit", "cult", "cultivable", "cultivate", "cultivated", "cultivation", "cultivator", "cultural",
				"culture", "cultured", "culvert", "cumber", "cumbersome", "cumin", "cummerbund", "cumulative",
				"cumulonimbus", "cumulus", "cuneiform", "cunnilingus", "cunning", "cunt", "cup", "cupbearer",
				"cupboard", "cupid", "cupidity", "cupola", "cuppa", "cupping", "cupric", "cur", "curable", "curacy",
				"curate", "curative", "curator", "curb", "curd", "curdle", "cure", "curettage", "curfew", "curia",
				"curio", "curiosity", "curious", "curl", "curler", "curlew", "curlicue", "curling", "curly", "curlycue",
				"curmudgeon", "currant", "currency", "current", "curriculum", "currish", "curry", "curse", "cursed",
				"cursive", "cursory", "curt", "curtail", "curtain", "curtains", "curtsey", "curtsy", "curvaceous",
				"curvacious", "curvature", "curve", "cushion", "cushy", "cusp", "cuspidor", "cuss", "cussed", "custard",
				"custodial", "custodian", "custody", "custom", "customary", "customer", "customs", "cut", "cutaway",
				"cutback", "cuticle", "cutlass", "cutler", "cutlery", "cutlet", "cutoff", "cutout", "cutpurse",
				"cutter", "cutthroat", "cutting", "cuttlefish", "cutworm", "cwm", "cwt", "cyanide", "cybernetics",
				"cyclamate", "cyclamen", "cycle", "cyclic", "cyclist", "cyclone", "cyclopaedia", "cyclopedia",
				"cyclostyle", "cyclotron", "cyder", "cygnet", "cylinder", "cymbal", "cynic", "cynical", "cynicism",
				"cynosure", "cypher", "cypress", "cyrillic", "cyst", "cystitis", "cytology", "czar", "czarina", "czech",
				"dab", "dabble", "dabchick", "dabs", "dace", "dachshund", "dactyl", "dad", "daddy", "dado", "daemon",
				"daffodil", "daft", "dagger", "dago", "daguerreotype", "dahlia", "daily", "dainty", "daiquiri", "dairy",
				"dairying", "dairymaid", "dairyman", "dais", "daisy", "dale", "dalliance", "dally", "dalmation", "dam",
				"damage", "damages", "damascene", "damask", "damn", "damnable", "damnation", "damnedest", "damning",
				"damocles", "damp", "dampen", "damper", "dampish", "damsel", "damson", "dance", "dandelion", "dander",
				"dandified", "dandle", "dandruff", "dandy", "danger", "dangerous", "dangle", "dank", "dapper",
				"dappled", "dare", "daredevil", "daresay", "daring", "dark", "darken", "darkey", "darkroom", "darky",
				"darling", "darn", "darning", "dart", "dartboard", "dartmoor", "darts", "dash", "dashboard", "dashed",
				"dashing", "data", "date", "dated", "dateless", "dateline", "dates", "dative", "daub", "daughter",
				"daunt", "dauntless", "dauphin", "davit", "dawdle", "dawn", "day", "dayboy", "daybreak", "daydream",
				"daylight", "dayroom", "days", "daytime", "daze", "dazzle", "ddt", "deacon", "dead", "deaden",
				"deadline", "deadlock", "deadly", "deadpan", "deadweight", "deaf", "deafen", "deal", "dealer",
				"dealing", "dealings", "dean", "deanery", "dear", "dearest", "dearie", "dearly", "dearth", "deary",
				"death", "deathbed", "deathblow", "deathless", "deathlike", "deathly", "deathwatch", "deb", "debar",
				"debark", "debase", "debatable", "debate", "debater", "debauch", "debauchee", "debauchery", "debenture",
				"debilitate", "debility", "debit", "debonair", "debone", "debouch", "debrief", "debris", "debt",
				"debtor", "debug", "debunk", "debut", "debutante", "decade", "decadence", "decadent", "decalogue",
				"decamp", "decant", "decanter", "decapitate", "decathlon", "decay", "decease", "deceased", "deceit",
				"deceitful", "deceive", "decelerate", "december", "decencies", "decency", "decent", "decentralise",
				"decentralize", "deception", "deceptive", "decibel", "decide", "decided", "decidedly", "deciduous",
				"decimal", "decimalise", "decimalize", "decimate", "decipher", "decision", "decisive", "deck",
				"deckchair", "deckhand", "declaim", "declamation", "declaration", "declare", "declared", "declassify",
				"declension", "declination", "decline", "declivity", "declutch", "decoction", "decode", "decolonise",
				"decolonize", "decompose", "decompress", "decongestant", "decontaminate", "decontrol", "decorate",
				"decoration", "decorative", "decorator", "decorous", "decorum", "decoy", "decrease", "decree",
				"decrepit", "decrepitude", "decry", "dedicate", "dedicated", "dedication", "deduce", "deduct",
				"deduction", "deductive", "deed", "deem", "deep", "deepen", "deer", "deerstalker", "def", "deface",
				"defame", "default", "defeat", "defeatism", "defecate", "defect", "defection", "defective", "defence",
				"defend", "defendant", "defense", "defensible", "defensive", "defer", "deference", "defiance",
				"defiant", "deficiency", "deficient", "deficit", "defile", "define", "definite", "definitely",
				"definition", "definitive", "deflate", "deflation", "deflationary", "deflect", "deflection", "deflower",
				"defoliant", "defoliate", "deforest", "deform", "deformation", "deformity", "defraud", "defray",
				"defrock", "defrost", "deft", "defunct", "defuse", "defy", "degauss", "degeneracy", "degenerate",
				"degeneration", "degenerative", "degrade", "degree", "dehorn", "dehumanise", "dehumanize", "dehydrate",
				"deice", "deification", "deify", "deign", "deism", "deity", "dejected", "dejection", "dekko", "delay",
				"delectable", "delectation", "delegacy", "delegate", "delegation", "delete", "deleterious", "deletion",
				"delft", "deliberate", "deliberation", "deliberative", "delicacy", "delicate", "delicatessen",
				"delicious", "delight", "delightful", "delimit", "delineate", "delinquency", "delinquent",
				"deliquescent", "delirious", "delirium", "deliver", "deliverance", "delivery", "deliveryman", "dell",
				"delouse", "delphic", "delphinium", "delta", "delude", "deluge", "delusion", "delusive", "delve",
				"demagnetise", "demagnetize", "demagogic", "demagogue", "demagoguery", "demand", "demanding",
				"demarcate", "demarcation", "demean", "demeanor", "demeanour", "demented", "demerit", "demesne",
				"demigod", "demijohn", "demilitarise", "demilitarize", "demise", "demist", "demister", "demo", "demob",
				"demobilise", "demobilize", "democracy", "democrat", "democratic", "democratise", "democratize",
				"demography", "demolish", "demolition", "demon", "demonetise", "demonetize", "demoniacal", "demonic",
				"demonstrable", "demonstrate", "demonstration", "demonstrative", "demonstrator", "demoralise",
				"demoralize", "demote", "demotic", "demur", "demure", "demystify", "den", "denationalise",
				"denationalize", "denial", "denier", "denigrate", "denim", "denims", "denizen", "denominate",
				"denomination", "denominational", "denominator", "denotation", "denote", "denouement", "denounce",
				"dense", "density", "dent", "dental", "dentifrice", "dentist", "dentistry", "denture", "dentures",
				"denude", "denunciation", "deny", "deodorant", "deodorise", "deodorize", "depart", "departed",
				"department", "departure", "depend", "dependable", "dependant", "dependence", "dependency", "dependent",
				"depict", "depilatory", "deplete", "deplorable", "deplore", "deploy", "deponent", "depopulate",
				"deport", "deportee", "deportment", "depose", "deposit", "deposition", "depositor", "depository",
				"depot", "deprave", "depravity", "deprecate", "deprecatory", "depreciate", "depreciatory",
				"depredation", "depress", "depressed", "depression", "deprivation", "deprive", "deprived", "depth",
				"depths", "deputation", "depute", "deputise", "deputize", "deputy", "derail", "derange", "derby",
				"derelict", "dereliction", "deride", "derision", "derisive", "derisory", "derivative", "derive",
				"dermatitis", "dermatology", "derogate", "derogatory", "derrick", "derv", "dervish", "des",
				"desalinise", "desalinize", "descale", "descant", "descend", "descendant", "descended", "descent",
				"describe", "description", "descriptive", "descry", "desecrate", "desegregate", "desensitise",
				"desensitize", "desert", "deserter", "desertion", "deserts", "deserve", "deservedly", "deserving",
				"desiccant", "desiccate", "desideratum", "design", "designate", "designation", "designedly", "designer",
				"designing", "designs", "desirable", "desire", "desirous", "desist", "desk", "deskwork", "desolate",
				"despair", "despairing", "despatch", "despatches", "desperado", "desperate", "desperation",
				"despicable", "despise", "despite", "despoil", "despondent", "despot", "despotic", "despotism",
				"dessert", "dessertspoon", "dessertspoonful", "destination", "destined", "destiny", "destitute",
				"destroy", "destroyer", "destruction", "destructive", "desuetude", "desultory", "detach", "detached",
				"detachedly", "detachment", "detail", "detailed", "detain", "detainee", "detect", "detection",
				"detective", "detector", "detention", "deter", "detergent", "deteriorate", "determinant",
				"determination", "determine", "determined", "determiner", "determinism", "deterrent", "detest",
				"dethrone", "detonate", "detonation", "detonator", "detour", "detract", "detractor", "detrain",
				"detriment", "detritus", "deuce", "deuced", "deuteronomy", "devaluation", "devalue", "devastate",
				"devastating", "develop", "developer", "development", "developmental", "deviance", "deviant", "deviate",
				"deviation", "deviationist", "device", "devil", "devilish", "devilishly", "devilment", "devious",
				"devise", "devitalise", "devitalize", "devoid", "devolution", "devolve", "devote", "devoted", "devotee",
				"devotion", "devotional", "devotions", "devour", "devout", "devoutly", "dew", "dewdrop", "dewlap",
				"dewpond", "dewy", "dexterity", "dexterous", "dextrose", "dhoti", "dhow", "diabetes", "diabetic",
				"diabolic", "diabolical", "diacritic", "diacritical", "diadem", "diaeresis", "diagnose", "diagnosis",
				"diagnostic", "diagonal", "diagram", "dial", "dialect", "dialectic", "dialectician", "dialog",
				"dialogue", "diameter", "diametrically", "diamond", "diaper", "diaphanous", "diaphragm", "diarist",
				"diarrhea", "diarrhoea", "diary", "diaspora", "diatom", "diatribe", "dibble", "dice", "dicey",
				"dichotomy", "dick", "dicker", "dickie", "dicky", "dickybird", "dictaphone", "dictate", "dictation",
				"dictator", "dictatorial", "dictatorship", "diction", "dictionary", "dictum", "did", "didactic",
				"diddle", "didst", "die", "diehard", "dieresis", "diet", "dietary", "dietetic", "dietetics",
				"dietician", "dietitian", "differ", "difference", "different", "differential", "differentiate",
				"difficult", "difficulty", "diffident", "diffract", "diffuse", "diffusion", "dig", "digest",
				"digestion", "digestive", "digger", "digging", "diggings", "digit", "digital", "dignified", "dignify",
				"dignitary", "dignity", "digraph", "digress", "digression", "digs", "dike", "dilapidated",
				"dilapidation", "dilapidations", "dilate", "dilatory", "dildo", "dilemma", "dilettante", "diligence",
				"diligent", "dill", "dillydally", "dilute", "dilution", "dim", "dimension", "dimensions", "diminish",
				"diminuendo", "diminution", "diminutive", "dimity", "dimple", "dimwit", "din", "dinar", "dine", "diner",
				"dingdong", "dinghy", "dingle", "dingo", "dingy", "dink", "dinkum", "dinky", "dinner", "dinosaur",
				"dint", "diocese", "dioxide", "dip", "diphtheria", "diphthong", "diploma", "diplomacy", "diplomat",
				"diplomatic", "diplomatically", "diplomatist", "dipper", "dipsomania", "dipsomaniac", "dipstick",
				"dipswitch", "diptych", "dire", "direct", "direction", "directional", "directions", "directive",
				"directly", "director", "directorate", "directorship", "directory", "direful", "dirge", "dirigible",
				"dirk", "dirndl", "dirt", "dirty", "disability", "disable", "disabled", "disabuse", "disadvantage",
				"disadvantageous", "disaffected", "disaffection", "disaffiliate", "disafforest", "disagree",
				"disagreeable", "disagreement", "disallow", "disappear", "disappearance", "disappoint", "disappointed",
				"disappointing", "disappointment", "disapprobation", "disapproval", "disapprove", "disarm",
				"disarmament", "disarrange", "disarray", "disassociate", "disaster", "disastrous", "disavow", "disband",
				"disbar", "disbelief", "disbelieve", "disburden", "disburse", "disbursement", "disc", "discard",
				"discern", "discerning", "discernment", "discharge", "disciple", "discipleship", "disciplinarian",
				"disciplinary", "discipline", "disclaim", "disclaimer", "disclose", "disclosure", "disco", "discolor",
				"discoloration", "discolour", "discolouration", "discomfit", "discomfiture", "discomfort", "discommode",
				"discompose", "disconcert", "disconnect", "disconnected", "disconnection", "disconsolate", "discontent",
				"discontented", "discontinue", "discontinuity", "discontinuous", "discord", "discordance", "discordant",
				"discotheque", "discount", "discountenance", "discourage", "discouragement", "discourse",
				"discourteous", "discourtesy", "discover", "discovery", "discredit", "discreditable", "discreet",
				"discrepancy", "discrete", "discretion", "discretionary", "discriminate", "discriminating",
				"discrimination", "discriminatory", "discursive", "discus", "discuss", "discussion", "disdain",
				"disdainful", "disease", "disembark", "disembarrass", "disembodied", "disembowel", "disembroil",
				"disenchant", "disencumber", "disendow", "disengage", "disengaged", "disentangle", "disequilibrium",
				"disestablish", "disfavor", "disfavour", "disfigure", "disforest", "disfranchise", "disfrock",
				"disgorge", "disgrace", "disgraceful", "disgruntled", "disguise", "disgust", "dish", "dishabille",
				"disharmony", "dishcloth", "dishearten", "dishes", "dishevelled", "dishful", "dishonest", "dishonesty",
				"dishonor", "dishonorable", "dishonour", "dishonourable", "dishwasher", "dishwater", "dishy",
				"disillusion", "disillusioned", "disillusionment", "disincentive", "disinclination", "disinclined",
				"disinfect", "disinfectant", "disinfest", "disingenuous", "disinherit", "disintegrate", "disinter",
				"disinterested", "disjoint", "disjointed", "disjunctive", "disk", "dislike", "dislocate", "dislocation",
				"dislodge", "disloyal", "dismal", "dismantle", "dismast", "dismay", "dismember", "dismiss", "dismissal",
				"dismount", "disobedient", "disobey", "disoblige", "disorder", "disorderly", "disorganise",
				"disorganize", "disorientate", "disown", "disparage", "disparate", "disparity", "dispassionate",
				"dispatch", "dispatches", "dispel", "dispensable", "dispensary", "dispensation", "dispense",
				"dispenser", "dispersal", "disperse", "dispersion", "dispirit", "displace", "displacement", "display",
				"displease", "displeasure", "disport", "disposable", "disposal", "dispose", "disposed", "disposition",
				"dispossess", "dispossessed", "disproof", "disproportion", "disproportionate", "disprove", "disputable",
				"disputant", "disputation", "disputatious", "dispute", "disqualification", "disqualify", "disquiet",
				"disquietude", "disquisition", "disregard", "disrelish", "disremember", "disrepair", "disreputable",
				"disrepute", "disrespect", "disrobe", "disrupt", "dissatisfaction", "dissatisfy", "dissect",
				"dissection", "dissemble", "disseminate", "dissension", "dissent", "dissenter", "dissenting",
				"dissertation", "disservice", "dissever", "dissident", "dissimilar", "dissimilarity", "dissimulate",
				"dissipate", "dissipated", "dissipation", "dissociate", "dissoluble", "dissolute", "dissolution",
				"dissolve", "dissonance", "dissonant", "dissuade", "distaff", "distal", "distance", "distant",
				"distantly", "distaste", };
	}

	static class KStemData3 {
		private KStemData3() {
		}

		static String[] data = { "distasteful", "distemper", "distempered", "distend", "distension", "distil",
				"distill", "distillation", "distiller", "distillery", "distinct", "distinction", "distinctive",
				"distinguish", "distinguishable", "distinguished", "distort", "distortion", "distract", "distracted",
				"distraction", "distrain", "distraint", "distrait", "distraught", "distress", "distressing",
				"distribute", "distribution", "distributive", "distributor", "district", "distrust", "distrustful",
				"disturb", "disturbance", "disturbed", "disunion", "disunite", "disunity", "disuse", "disused",
				"disyllabic", "disyllable", "ditch", "dither", "dithers", "ditto", "ditty", "diuretic", "diurnal",
				"divagate", "divan", "dive", "diver", "diverge", "divergence", "divers", "diverse", "diversify",
				"diversion", "diversionary", "diversity", "divert", "divertimento", "divertissement", "divest",
				"divide", "dividend", "dividers", "divination", "divine", "diviner", "divingboard", "divinity",
				"divisible", "division", "divisive", "divisor", "divorce", "divot", "divulge", "divvy", "dixie",
				"dixieland", "dizzy", "djinn", "dna", "do", "dobbin", "doc", "docile", "dock", "docker", "docket",
				"dockyard", "doctor", "doctoral", "doctorate", "doctrinaire", "doctrinal", "doctrine", "document",
				"documentary", "documentation", "dodder", "doddering", "doddle", "dodge", "dodgems", "dodger", "dodgy",
				"dodo", "doe", "doer", "doeskin", "doff", "dog", "dogcart", "dogcatcher", "dogfight", "dogfish",
				"dogged", "doggerel", "doggie", "doggo", "doggone", "doggy", "doghouse", "dogie", "dogleg", "dogma",
				"dogmatic", "dogmatics", "dogmatism", "dogs", "dogsbody", "dogtooth", "dogtrot", "dogwood", "doh",
				"doily", "doings", "doldrums", "dole", "doleful", "doll", "dollar", "dollop", "dolly", "dolmen",
				"dolor", "dolorous", "dolour", "dolphin", "dolt", "domain", "dome", "domed", "domestic", "domesticate",
				"domesticity", "domicile", "domiciliary", "dominance", "dominant", "dominate", "domination", "domineer",
				"dominican", "dominion", "domino", "dominoes", "don", "donate", "donation", "donjon", "donkey",
				"donkeywork", "donnish", "donor", "doodle", "doodlebug", "doom", "doomsday", "door", "doorbell",
				"doorframe", "doorkeeper", "doorknob", "doorknocker", "doorman", "doormat", "doornail", "doorplate",
				"doorscraper", "doorstep", "doorstopper", "doorway", "dope", "dopey", "dopy", "doric", "dormant",
				"dormer", "dormitory", "dormouse", "dorsal", "dory", "dosage", "dose", "doss", "dosser", "dosshouse",
				"dossier", "dost", "dot", "dotage", "dote", "doth", "doting", "dottle", "dotty", "double", "doubles",
				"doublet", "doublethink", "doubloon", "doubly", "doubt", "doubtful", "doubtless", "douche", "dough",
				"doughnut", "doughty", "doughy", "dour", "douse", "dove", "dovecote", "dovetail", "dowager", "dowdy",
				"dowel", "dower", "down", "downbeat", "downcast", "downdraft", "downdraught", "downer", "downfall",
				"downgrade", "downhearted", "downhill", "downpour", "downright", "downstage", "downstairs",
				"downstream", "downtown", "downtrodden", "downward", "downwards", "downwind", "downy", "dowry", "dowse",
				"doxology", "doyen", "doyley", "doze", "dozen", "dozy", "dpt", "drab", "drabs", "drachm", "drachma",
				"draconian", "draft", "draftee", "draftsman", "drafty", "drag", "draggled", "draggy", "dragnet",
				"dragoman", "dragon", "dragonfly", "dragoon", "drain", "drainage", "drainpipe", "drake", "dram",
				"drama", "dramatic", "dramatics", "dramatise", "dramatist", "dramatize", "drank", "drape", "draper",
				"drapery", "drastic", "drat", "draught", "draughtboard", "draughts", "draughtsman", "draughty", "draw",
				"drawback", "drawbridge", "drawer", "drawers", "drawing", "drawl", "drawn", "drawstring", "dray",
				"dread", "dreadful", "dreadfully", "dreadnaught", "dreadnought", "dream", "dreamboat", "dreamer",
				"dreamland", "dreamless", "dreamlike", "dreamy", "drear", "dreary", "dredge", "dredger", "dregs",
				"drench", "dress", "dressage", "dresser", "dressing", "dressmaker", "dressy", "drew", "dribble",
				"driblet", "dribs", "drier", "drift", "driftage", "drifter", "driftnet", "driftwood", "drill", "drily",
				"drink", "drinkable", "drinker", "drip", "dripping", "drive", "drivel", "driver", "driveway", "driving",
				"drizzle", "drogue", "droll", "drollery", "dromedary", "drone", "drool", "droop", "drop", "dropkick",
				"droplet", "dropout", "dropper", "droppings", "drops", "dropsy", "dross", "drought", "drove", "drover",
				"drown", "drowse", "drowsy", "drub", "drudge", "drudgery", "drug", "drugget", "druggist", "drugstore",
				"druid", "drum", "drumbeat", "drumfire", "drumhead", "drummer", "drumstick", "drunk", "drunkard",
				"drunken", "drupe", "dry", "dryad", "dryer", "dual", "dub", "dubbin", "dubiety", "dubious", "ducal",
				"ducat", "duchess", "duchy", "duck", "duckboards", "duckling", "ducks", "duckweed", "ducky", "duct",
				"ductile", "dud", "dude", "dudgeon", "duds", "due", "duel", "duenna", "dues", "duet", "duff", "duffel",
				"duffer", "duffle", "dug", "dugout", "duke", "dukedom", "dukes", "dulcet", "dulcimer", "dull",
				"dullard", "duly", "dumb", "dumbbell", "dumbfound", "dumbwaiter", "dumfound", "dummy", "dump", "dumper",
				"dumpling", "dumps", "dumpy", "dun", "dunce", "dunderhead", "dung", "dungaree", "dungarees", "dungeon",
				"dunghill", "dunk", "duo", "duodecimal", "duodenum", "duologue", "dupe", "duplex", "duplicate",
				"duplicator", "duplicity", "durable", "duration", "durbar", "duress", "durex", "during", "durst",
				"dusk", "dusky", "dust", "dustbin", "dustbowl", "dustcart", "dustcoat", "duster", "dustman", "dustpan",
				"dustsheet", "dustup", "dusty", "dutch", "dutiable", "dutiful", "duty", "duvet", "dwarf", "dwell",
				"dwelling", "dwindle", "dyarchy", "dye", "dyestuff", "dyeworks", "dyke", "dynamic", "dynamics",
				"dynamism", "dynamite", "dynamo", "dynasty", "dysentery", "dyslexia", "dyspepsia", "dyspeptic", "each",
				"eager", "eagle", "eaglet", "ear", "earache", "eardrum", "eared", "earful", "earl", "earliest",
				"earlobe", "early", "earmark", "earmuff", "earn", "earnest", "earnings", "earphone", "earpiece",
				"earplug", "earring", "earshot", "earth", "earthbound", "earthen", "earthenware", "earthling",
				"earthly", "earthnut", "earthquake", "earthshaking", "earthwork", "earthworm", "earthy", "earwax",
				"earwig", "ease", "easel", "easily", "east", "eastbound", "easter", "easterly", "eastern", "easterner",
				"easternmost", "easy", "easygoing", "eat", "eatable", "eatables", "eater", "eats", "eaves", "eavesdrop",
				"ebb", "ebony", "ebullience", "ebullient", "eccentric", "eccentricity", "ecclesiastic",
				"ecclesiastical", "ecg", "echelon", "echo", "eclectic", "eclipse", "ecliptic", "eclogue", "ecological",
				"ecologically", "ecology", "economic", "economical", "economically", "economics", "economise",
				"economist", "economize", "economy", "ecosystem", "ecstasy", "ecstatic", "ect", "ectoplasm",
				"ecumenical", "ecumenicalism", "eczema", "edam", "eddy", "edelweiss", "eden", "edge", "edgeways",
				"edging", "edgy", "edible", "edibles", "edict", "edification", "edifice", "edify", "edit", "edition",
				"editor", "editorial", "editorialise", "editorialize", "educate", "educated", "education",
				"educational", "educationist", "educator", "educe", "eec", "eeg", "eel", "eerie", "efface", "effect",
				"effective", "effectively", "effectiveness", "effectives", "effects", "effectual", "effectually",
				"effectuate", "effeminacy", "effeminate", "effendi", "effervesce", "effete", "efficacious", "efficacy",
				"efficiency", "efficient", "effigy", "efflorescence", "effluent", "efflux", "effort", "effortless",
				"effrontery", "effulgence", "effulgent", "effusion", "effusive", "eft", "egalitarian", "egg", "eggcup",
				"egghead", "eggnog", "eggplant", "eggshell", "egis", "eglantine", "ego", "egocentric", "egoism",
				"egoist", "egotism", "egotist", "egregious", "egress", "egret", "eiderdown", "eight", "eighteen",
				"eightsome", "eighty", "eisteddfod", "either", "ejaculate", "ejaculation", "eject", "ejector", "eke",
				"ekg", "elaborate", "elaboration", "eland", "elapse", "elastic", "elasticity", "elastoplast", "elate",
				"elated", "elation", "elbow", "elbowroom", "elder", "elderberry", "elderflower", "elderly", "eldest",
				"elect", "election", "electioneer", "electioneering", "elective", "elector", "electoral", "electorate",
				"electric", "electrical", "electrician", "electricity", "electrify", "electrocardiogram",
				"electrocardiograph", "electrocute", "electrode", "electroencephalogram", "electroencephalograph",
				"electrolysis", "electrolyte", "electron", "electronic", "electronics", "electroplate", "eleemosynary",
				"elegant", "elegiac", "elegy", "element", "elemental", "elementary", "elements", "elephant",
				"elephantiasis", "elephantine", "elevate", "elevated", "elevation", "elevator", "eleven", "elevenses",
				"elf", "elfin", "elfish", "elicit", "elide", "eligible", "eliminate", "elite", "elitism", "elixir",
				"elizabethan", "elk", "elkhound", "ellipse", "ellipsis", "elliptic", "elm", "elocution", "elocutionary",
				"elocutionist", "elongate", "elongation", "elope", "eloquence", "eloquent", "else", "elsewhere",
				"elucidate", "elucidatory", "elude", "elusive", "elver", "elves", "elvish", "elysian", "elysium",
				"emaciate", "emanate", "emancipate", "emancipation", "emasculate", "embalm", "embankment", "embargo",
				"embark", "embarkation", "embarrass", "embarrassment", "embassy", "embattled", "embed", "embellish",
				"ember", "embezzle", "embitter", "emblazon", "emblem", "emblematic", "embodiment", "embody", "embolden",
				"embolism", "embonpoint", "embosomed", "emboss", "embowered", "embrace", "embrasure", "embrocation",
				"embroider", "embroidery", "embroil", "embryo", "embryonic", "emend", "emendation", "emerald", "emerge",
				"emergence", "emergency", "emergent", "emeritus", "emery", "emetic", "emigrant", "emigrate", "eminence",
				"eminent", "eminently", "emir", "emirate", "emissary", "emission", "emit", "emmentaler", "emmenthaler",
				"emollient", "emolument", "emote", "emotion", "emotional", "emotionalism", "emotionally", "emotive",
				"empanel", "empathy", "emperor", "emphasis", "emphasise", "emphasize", "emphatic", "emphatically",
				"emphysema", "empire", "empirical", "empiricism", "emplacement", "emplane", "employ", "employable",
				"employee", "employer", "employment", "emporium", "empower", "empress", "emptily", "empty", "empurpled",
				"empyreal", "empyrean", "emu", "emulate", "emulation", "emulsify", "emulsion", "enable", "enabling",
				"enact", "enactment", "enamel", "enamelware", "enamored", "enamoured", "encamp", "encampment",
				"encapsulate", "encase", "encaustic", "encephalitis", "enchain", "enchant", "enchanter", "enchanting",
				"enchantment", "encipher", "encircle", "enclave", "enclose", "enclosure", "encode", "encomium",
				"encompass", "encore", "encounter", "encourage", "encouragement", "encroach", "encroachment", "encrust",
				"encumber", "encumbrance", "encyclical", "encyclopaedia", "encyclopaedic", "encyclopedia",
				"encyclopedic", "end", "endanger", "endear", "endearing", "endearment", "endeavor", "endeavour",
				"endemic", "ending", "endive", "endless", "endocrine", "endorse", "endow", "endowment", "endpaper",
				"endurance", "endure", "enduring", "endways", "enema", "enemy", "energetic", "energize", "energy",
				"enervate", "enfeeble", "enfilade", "enfold", "enforce", "enfranchise", "engage", "engaged",
				"engagement", "engaging", "engender", "engine", "engineer", "engineering", "english", "englishman",
				"engraft", "engrave", "engraving", "engross", "engrossing", "engulf", "enhance", "enigma", "enigmatic",
				"enjoin", "enjoy", "enjoyable", "enjoyment", "enkindle", "enlarge", "enlargement", "enlighten",
				"enlightened", "enlightenment", "enlist", "enliven", "enmesh", "enmity", "ennoble", "ennui", "enormity",
				"enormous", "enormously", "enough", "enplane", "enquire", "enquiring", "enquiry", "enrage", "enrapture",
				"enrich", "enrol", "enroll", "enrollment", "enrolment", "ensanguined", "ensconce", "ensemble",
				"enshrine", "enshroud", "ensign", "enslave", "ensnare", "ensue", "ensure", "entail", "entangle",
				"entanglement", "entente", "enter", "enteritis", "enterprise", "enterprising", "entertain",
				"entertainer", "entertaining", "entertainment", "enthral", "enthrall", "enthrone", "enthroned",
				"enthuse", "enthusiasm", "enthusiast", "entice", "enticement", "entire", "entirety", "entitle",
				"entity", "entomb", "entomology", "entourage", "entrails", "entrain", "entrance", "entrant", "entrap",
				"entreat", "entreaty", "entrench", "entrenched", "entrenchment", "entrepreneur", "entresol", "entropy",
				"entrust", "entry", "entwine", "enumerate", "enunciate", "enunciation", "envelop", "envenom",
				"enviable", "envious", "environed", "environment", "environmental", "environmentalist", "environs",
				"envisage", "envoi", "envoy", "envy", "enzyme", "eon", "epaulet", "epaulette", "ephemeral", "epic",
				"epicenter", "epicentre", "epicure", "epicurean", "epidemic", "epidermis", "epidiascope", "epiglottis",
				"epigram", "epigrammatic", "epilepsy", "epileptic", "epilogue", "epiphany", "episcopacy", "episcopal",
				"episcopalian", "episode", "episodic", "epistle", "epistolary", "epitaph", "epithet", "epitome",
				"epitomise", "epitomize", "epoch", "eponymous", "equability", "equable", "equal", "equalise",
				"equalitarian", "equality", "equalize", "equally", "equanimity", "equate", "equation", "equator",
				"equatorial", "equerry", "equestrian", "equidistant", "equilateral", "equilibrium", "equine",
				"equinoctial", "equinox", "equip", "equipage", "equipment", "equipoise", "equitable", "equitation",
				"equities", "equity", "equivalence", "equivalent", "equivocal", "equivocate", "equivocation", "era",
				"eradicate", "eradicator", "erase", "eraser", "erasure", "ere", "erect", "erectile", "erection",
				"eremite", "erg", "ergo", "ergonomics", "ermine", "erode", "erogenous", "erosion", "erotic", "erotica",
				"eroticism", "err", "errand", "errant", "erratic", "erratum", "erroneous", "error", "ersatz", "erse",
				"eructation", "erudite", "erupt", "eruption", "erysipelas", "escalate", "escalator", "escalope",
				"escapade", "escape", "escapee", "escapement", "escapism", "escapology", "escarpment", "eschatology",
				"eschew", "escort", "escritoire", "escutcheon", "eskimo", "esophagus", "esoteric", "esp", "espalier",
				"especial", "especially", "esperanto", "espionage", "esplanade", "espousal", "espouse", "espresso",
				"espy", "essay", "essence", "essential", "essentially", "establish", "establishment", "estaminet",
				"estate", "esteem", "esthete", "esthetic", "esthetics", "estimable", "estimate", "estimation",
				"estimator", "estrange", "estrangement", "estrogen", "estuary", "etch", "etching", "eternal",
				"eternity", "ether", "ethereal", "ethic", "ethical", "ethically", "ethics", "ethnic", "ethnically",
				"ethnographer", "ethnography", "ethnologist", "ethnology", "ethos", "ethyl", "etiolate", "etiology",
				"etiquette", "etymologist", "etymology", "eucalyptus", "eucharist", "euclidean", "euclidian", "eugenic",
				"eugenics", "eulogise", "eulogist", "eulogistic", "eulogize", "eulogy", "eunuch", "euphemism",
				"euphemistic", "euphonious", "euphonium", "euphony", "euphoria", "euphuism", "eurasian", "eureka",
				"eurhythmic", "eurhythmics", "eurocrat", "eurodollar", "eurythmic", "eurythmics", "euthanasia",
				"evacuate", "evacuee", "evade", "evaluate", "evanescent", "evangelic", "evangelical", "evangelise",
				"evangelist", "evangelize", "evaporate", "evasion", "evasive", "eve", "even", "evening", "evenings",
				"evens", "evensong", "event", "eventful", "eventide", "eventual", "eventuality", "eventually",
				"eventuate", "ever", "evergreen", "everlasting", "everlastingly", "evermore", "every", "everybody",
				"everyday", "everything", "everywhere", "evict", "evidence", "evident", "evidently", "evil", "evildoer",
				"evince", "eviscerate", "evocative", "evoke", "evolution", "evolutionary", "evolve", "ewe", "ewer",
				"exacerbate", "exact", "exacting", "exaction", "exactly", "exaggerate", "exaggeration", "exalt",
				"exaltation", "exalted", "exam", "examination", "examine", "example", "exasperate", "exasperation",
				"excavate", "excavation", "excavator", "exceed", "exceedingly", "excel", "excellence", "excellency",
				"excellent", "excelsior", "except", "excepted", "excepting", "exception", "exceptionable",
				"exceptional", "excerpt", "excess", "excesses", "excessive", "exchange", "exchequer", "excise",
				"excision", "excitable", "excite", "excited", "excitement", "exciting", "exclaim", "exclamation",
				"exclamatory", "exclude", "excluding", "exclusion", "exclusive", "exclusively", "excogitate",
				"excommunicate", "excommunication", "excoriate", "excrement", "excrescence", "excreta", "excrete",
				"excretion", "excruciating", "exculpate", "excursion", "excursionist", "excusable", "excuse",
				"execrable", "execrate", "executant", "execute", "execution", "executioner", "executive", "executor",
				"exegesis", "exemplary", "exemplification", "exemplify", "exempt", "exemption", "exercise", "exercises",
				"exert", "exertion", "exeunt", "exhalation", "exhale", "exhaust", "exhaustion", "exhaustive", "exhibit",
				"exhibition", "exhibitionism", "exhibitor", "exhilarate", "exhilarating", "exhort", "exhortation",
				"exhume", "exigency", "exigent", "exiguous", "exile", "exist", "existence", "existent", "existential",
				"existentialism", "existing", "exit", "exodus", "exogamy", "exonerate", "exorbitant", "exorcise",
				"exorcism", "exorcist", "exorcize", "exotic", "expand", "expanse", "expansion", "expansive",
				"expatiate", "expatriate", "expect", "expectancy", "expectant", "expectation", "expectations",
				"expectorate", "expediency", "expedient", "expedite", "expedition", "expeditionary", "expeditious",
				"expel", "expend", "expendable", "expenditure", "expense", "expenses", "expensive", "experience",
				"experienced", "experiment", "experimental", "experimentation", "expert", "expertise", "expiate",
				"expiration", "expire", "explain", "explanation", "explanatory", "expletive", "explicable", "explicate",
				"explicit", "explode", "exploded", "exploit", "exploration", "exploratory", "explore", "explosion",
				"explosive", "expo", "exponent", "exponential", "export", "exportation", "exporter", "expose",
				"exposition", "expostulate", "exposure", "expound", "express", "expression", "expressionism",
				"expressionless", "expressive", "expressly", "expressway", "expropriate", "expulsion", "expunge",
				"expurgate", "exquisite", "extant", "extemporaneous", "extempore", "extemporise", "extemporize",
				"extend", "extension", "extensive", "extent", "extenuate", "extenuation", "exterior", "exteriorise",
				"exteriorize", "exterminate", "external", "externalise", "externalize", "externally", "externals",
				"exterritorial", "extinct", "extinction", "extinguish", "extinguisher", "extirpate", "extol", "extort",
				"extortion", "extortionate", "extortions", "extra", "extract", "extraction", "extracurricular",
				"extraditable", "extradite", "extrajudicial", "extramarital", "extramural", "extraneous",
				"extraordinarily", "extraordinary", "extrapolate", "extraterrestrial", "extraterritorial",
				"extravagance", "extravagant", "extravaganza", "extravert", "extreme", "extremely", "extremism",
				"extremities", "extremity", "extricate", "extrinsic", "extrovert", "extrude", "exuberance", "exuberant",
				"exude", "exult", "exultant", "exultation", "eye", "eyeball", "eyebrow", "eyecup", "eyeful", "eyeglass",
				"eyeglasses", "eyelash", "eyelet", "eyelid", "eyeliner", "eyepiece", "eyes", "eyeshot", "eyesight",
				"eyesore", "eyestrain", "eyetooth", "eyewash", "eyewitness", "eyot", "eyrie", "eyry", "fabian", "fable",
				"fabled", "fabric", "fabricate", "fabrication", "fabulous", "fabulously", "face", "facecloth",
				"faceless", "facet", "facetious", "facial", "facile", "facilitate", "facilities", "facility", "facing",
				"facings", "facsimile", "fact", "faction", "factious", "factitious", "factor", "factorial", "factorise",
				"factorize", "factory", "factotum", "factual", "faculty", "fad", "fade", "faeces", "faerie", "faery",
				"fag", "fagged", "faggot", "fagot", "fahrenheit", "faience", "fail", "failing", "failure", "fain",
				"faint", "fair", "fairground", "fairly", "fairway", "fairy", "fairyland", "faith", "faithful",
				"faithfully", "faithless", "fake", "fakir", "falcon", "falconer", "falconry", "fall", "fallacious",
				"fallacy", "fallen", "fallible", "fallout", "fallow", "falls", "false", "falsehood", "falsetto",
				"falsies", "falsify", "falsity", "falter", "fame", "famed", "familial", "familiar", "familiarise",
				"familiarity", "familiarize", "familiarly", "family", "famine", "famish", "famished", "famous",
				"famously", "fan", "fanatic", "fanaticism", "fancier", "fancies", "fanciful", "fancy", "fancywork",
				"fandango", "fanfare", "fang", "fanlight", "fanny", "fantasia", "fantastic", "fantasy", "far",
				"faraway", "farce", "fare", "farewell", "farfetched", "farinaceous", "farm", "farmer", "farmhand",
				"farmhouse", "farming", "farmyard", "farrago", "farrier", "farrow", "farsighted", "fart", "farther",
				"farthest", "farthing", "fascia", "fascinate", "fascinating", "fascination", "fascism", "fascist",
				"fashion", "fashionable", "fast", "fasten", "fastener", "fastening", "fastidious", "fastness", "fat",
				"fatal", "fatalism", "fatalist", "fatality", "fatally", "fate", "fated", "fateful", "fates", "fathead",
				"father", "fatherhood", "fatherly", "fathom", "fathomless", "fatigue", "fatigues", "fatless", "fatted",
				"fatten", "fatty", "fatuity", "fatuous", "faucet", "fault", "faultfinding", "faultless", "faulty",
				"faun", "fauna", "favor", "favorable", "favored", "favorite", "favoritism", "favour", "favourable",
				"favoured", "favourite", "favouritism", "favours", "fawn", "fay", "faze", "fbi", "fealty", "fear",
				"fearful", "fearless", "fearsome", "feasible", "feast", "feat", "feather", "featherbed",
				"featherbrained", "featherweight", "feathery", "feature", "featureless", "features", "febrile",
				"february", "feces", "feckless", "fecund", "fed", "federal", "federalism", "federalist", "federate",
				"federation", "fee", "feeble", "feebleminded", "feed", "feedback", "feedbag", "feeder", "feel",
				"feeler", "feeling", "feelings", "feet", "feign", "feint", "feldspar", "felicitate", "felicitous",
				"felicity", "feline", "fell", "fellah", "fellatio", "fellow", "fellowship", "felon", "felony",
				"felspar", "felt", "felucca", "fem", "female", "feminine", "femininity", "feminism", "feminist",
				"femur", "fen", "fence", "fencer", "fencing", "fend", "fender", "fennel", "feoff", "feral", "ferment",
				"fermentation", "fern", "ferocious", "ferocity", "ferret", "ferroconcrete", "ferrous", "ferrule",
				"ferry", "ferryboat", "ferryman", "fertile", "fertilise", "fertility", "fertilize", "fertilizer",
				"ferule", "fervent", "fervid", "fervor", "fervour", "festal", "fester", "festival", "festive",
				"festivity", "festoon", "fetal", "fetch", "fetching", "fete", "fetid", "fetish", "fetishism",
				"fetishist", "fetlock", "fetter", "fettle", "fetus", "feud", "feudal", "feudalism", "feudatory",
				"fever", "fevered", "feverish", "feverishly", "few", "fey", "fez", "fiasco", "fiat", "fib", "fiber",
				"fiberboard", "fiberglass", "fibre", "fibreboard", "fibreglass", "fibrositis", "fibrous", "fibula",
				"fichu", "fickle", "fiction", "fictional", "fictionalisation", "fictionalization", "fictitious",
				"fiddle", "fiddler", "fiddlesticks", "fiddling", "fidelity", "fidget", "fidgets", "fidgety", "fie",
				"fief", "field", "fielder", "fieldwork", "fiend", "fiendish", "fiendishly", "fierce", "fiery", "fiesta",
				"fife", "fifteen", "fifth", "fifty", "fig", "fight", "fighter", "figment", "figurative", "figure",
				"figured", "figurehead", "figures", "figurine", "filament", "filbert", "filch", "file", "filet",
				"filial", "filibuster", "filigree", "filings", "fill", "filler", "fillet", "filling", "fillip", "filly",
				"film", "filmable", "filmstrip", "filmy", "filter", "filth", "filthy", "fin", "finable", "final",
				"finale", "finalise", "finalist", "finality", "finalize", "finally", "finance", "finances", "financial",
				"financially", "financier", "finch", "find", "finder", "finding", "fine", "fineable", "finely",
				"finery", "finesse", "finger", "fingerboard", "fingering", "fingernail", "fingerplate", "fingerpost",
				"fingerprint", "fingerstall", "fingertip", "finicky", "finis", "finish", "finished", "finite", "fink",
				"fiord", "fir", "fire", "firearm", "fireball", "firebomb", "firebox", "firebrand", "firebreak",
				"firebrick", "firebug", "fireclay", "firecracker", "firedamp", "firedog", "firefly", "fireguard",
				"firelight", "firelighter", "fireman", "fireplace", "firepower", "fireproof", "fireside", "firestorm",
				"firetrap", "firewalking", "firewatcher", "firewater", "firewood", "firework", "fireworks", "firkin",
				"firm", "firmament", "first", "firstborn", "firstfruits", "firsthand", "firstly", "firth", "firtree",
				"fiscal", "fish", "fishcake", "fisherman", "fishery", "fishing", "fishmonger", "fishplate", "fishwife",
				"fishy", "fissile", "fission", "fissionable", "fissure", "fist", "fisticuffs", "fistula", "fit",
				"fitful", "fitment", "fitness", "fitted", "fitter", "fitting", "five", "fiver", "fives", "fix",
				"fixation", "fixative", "fixed", "fixedly", "fixity", "fixture", "fizz", "fizzle", "fizzy", "fjord",
				"flabbergast", "flabby", "flaccid", "flag", "flagellant", "flagellate", "flageolet", "flagon",
				"flagpole", "flagrancy", "flagrant", "flagship", "flagstaff", "flagstone", "flail", "flair", "flak",
				"flake", "flaky", "flambeau", "flamboyant", "flame", "flamenco", "flaming", "flamingo", "flammable",
				"flan", "flange", "flank", "flannel", "flannelette", "flannels", "flap", "flapjack", "flapper", "flare",
				"flared", "flares", "flash", "flashback", "flashbulb", "flashcube", "flasher", "flashgun", "flashlight",
				"flashy", "flask", "flat", "flatcar", "flatfish", "flatfoot", "flatiron", "flatlet", "flatly",
				"flatten", "flatter", "flattery", "flattop", "flatulence", "flaunt", "flautist", "flavor", "flavoring",
				"flavour", "flavouring", "flaw", "flawless", "flax", "flaxen", "flay", "flea", "fleabag", "fleabite",
				"fleapit", "fleck", "fledged", "fledgling", "flee", "fleece", "fleecy", "fleet", "fleeting", "flesh",
				"fleshings", "fleshly", "fleshpot", "fleshy", "flew", "flex", "flexible", "flibbertigibbet", "flick",
				"flicker", "flicks", "flier", "flies", "flight", "flightless", "flighty", "flimsy", "flinch", "fling",
				"flint", "flintlock", "flinty", "flip", "flippancy", "flippant", "flipper", "flipping", "flirt",
				"flirtation", "flirtatious", "flit", "flitch", "flivver", "float", "floatation", "floating", "flock",
				"floe", "flog", "flogging", "flood", "floodgate", "floodlight", "floor", "floorboard", "flooring",
				"floorwalker", "floosy", "floozy", "flop", "floppy", "flora", "floral", "floriculture", "florid",
				"florin", "florist", "floss", "flotation", "flotilla", "flounce", "flounder", "flour", "flourish",
				"flourmill", "floury", "flout", "flow", "flower", "flowerbed", "flowered", "flowering", "flowerless",
				"flowerpot", "flowery", "flowing", "flown", "flu", "fluctuate", "flue", "fluency", "fluent", "fluff",
				"fluffy", "fluid", "fluidity", "fluke", "flukey", "fluky", "flume", "flummery", "flummox", "flung",
				"flunk", "flunkey", "flunky", "fluorescent", "fluoridate", "fluoride", "fluorine", "flurry", "flush",
				"flushed", "fluster", "flute", "fluting", "flutist", "flutter", "fluvial", "flux", "fly", "flyaway",
				"flyblown", "flyby", "flycatcher", "flyer", "flying", "flyleaf", "flyover", "flypaper", "flypast",
				"flysheet", "flyswatter", "flytrap", "flyweight", "flywheel", "flywhisk", "foal", "foam", "fob",
				"focal", "focus", "fodder", "foe", "foeman", "foetal", "foetus", "fog", "fogbank", "fogbound", "fogey",
				"foggy", "foghorn", "fogy", "foible", "foil", "foist", "fold", "foldaway", "folder", "foliage", "folio",
				"folk", "folklore", "folklorist", "folks", "folksy", "folktale", "folkway", "follicle", "follow",
				"follower", "following", "folly", "foment", "fomentation", "fond", "fondant", "fondle", "fondly",
				"fondu", "fondue", "font", "food", "foodstuff", "fool", "foolery", "foolhardy", "foolish", "foolproof",
				"foolscap", "foot", "footage", "football", "footbath", "footboard", "footbridge", "footer", "footfall",
				"foothill", "foothold", "footing", "footle", "footlights", "footling", "footloose", "footman",
				"footnote", "footpad", "footpath", "footplate", "footprint", "footrace", "footsie", "footslog",
				"footsore", "footstep", "footstool", "footsure", "footwear", "footwork", "fop", "foppish", "for",
				"forage", "foray", "forbear", "forbearance", "forbearing", "forbid", "forbidden", "forbidding", "force",
				"forced", "forceful", "forcemeat", "forceps", "forces", "forcible", "forcibly", "ford", "fore",
				"forearm", "forebode", "foreboding", "forecast", "forecastle", "foreclose", "foreclosure", "forecourt",
				"foredoomed", "forefather", "forefinger", "forefoot", "forefront", "forego", "foregoing", "foreground",
				"forehand", "forehead", "foreign", "foreigner", "foreknowledge", "foreland", "foreleg", "forelock",
				"foreman", "foremost", "forename", "forenoon", "forensic", "foreordain", "forepart", "foreplay",
				"forerunner", "foresail", "foresee", "foreseeable", "foreshadow", "foreshore", "foreshorten",
				"foresight", "foreskin", "forest", "forestall", "forester", "forestry", "foreswear", "foretaste",
				"foretell", "forethought", "forever", "forewarn", "forewent", "forewoman", "foreword", "forfeit",
				"forfeiture", "forgather", "forgave", "forge", "forger", "forgery", "forget", "forgetful", "forging",
				"forgivable", "forgive", "forgiveable", "forgiveness", "forgiving", "forgo", "fork", "forked",
				"forkful", "forklift", "forlorn", "form", "formal", "formaldehyde", "formalin", "formalise",
				"formalism", "formality", "formalize", "format", "formation", "formative", "formbook", "former",
				"formerly", "formica", "formidable", "formless", "formula", "formulaic", "formulate", "formulation",
				"fornicate", "fornication", "forrader", "forsake", "forsooth", "forswear", "forsythia", "fort", "forte",
				"forth", "forthcoming", "forthright", "forthwith", "fortieth", "fortification", "fortify", "fortissimo",
				"fortitude", "fortnight", "fortnightly", "fortress", "fortuitous", "fortunate", "fortunately",
				"fortune", "forty", "forum", "forward", "forwarding", "forwardly", "forwardness", "forwent", "foss",
				"fosse", "fossil", "fossilise", "fossilize", "foster", "fought", "foul", "found", "foundation",
				"foundations", "founder", "foundling", "foundry", "fount", "fountain", "fountainhead", "four",
				"foureyes", "fourpenny", "fours", "foursquare", "fourteen", "fourth", "fowl", "fox", "foxglove",
				"foxhole", "foxhound", "foxhunt", "foxtrot", "foxy", "foyer", "fracas", "fraction", "fractional",
				"fractionally", "fractious", "fracture", "fragile", "fragment", "fragmentary", "fragmentation",
				"fragrance", "fragrant", "frail", "frailty", "frame", "frames", "framework", "franc", "franchise",
				"franciscan", "frank", "frankfurter", "frankincense", "franklin", "frankly", "frantic", "fraternal",
				"fraternise", "fraternity", "fraternize", "fratricide", "frau", "fraud", "fraudulence", "fraudulent",
				"fraught", "fraulein", "fray", "frazzle", "freak", "freakish", "freckle", "free", "freebee", "freebie",
				"freeboard", "freebooter", "freeborn", "freedman", "freedom", "freehand", "freehanded", "freehold",
				"freeholder", "freelance", "freeload", "freely", "freeman", "freemason", "freemasonry", "freepost",
				"freesia", "freestanding", "freestone", "freestyle", "freethinker", "freeway", "freewheel",
				"freewheeling", "freewill", "freeze", "freezer", "freezing", "freight", "freighter", "freightliner",
				"frenchman", "frenetic", "frenzied", "frenzy", "frequency", "frequent", "fresco", "fresh", "freshen",
				"fresher", "freshet", "freshly", "freshwater", "fret", "fretful", "fretsaw", "fretwork", "freudian",
				"friable", "friar", "friary", "fricassee", "fricative", "friction", "friday", "fridge", "friend",
				"friendless", "friendly", "friends", "friendship", "frier", "frieze", "frig", "frigate", "frigging",
				"fright", "frighten", "frightened", "frightful", "frightfully", "frigid", "frigidity", "frill",
				"frilled", "frills", "frilly", "fringe", "frippery", "frisbee", "frisian", "frisk", "frisky", "frisson",
				"fritter", "frivolity", "frivolous", "frizz", "frizzle", "frizzy", "fro", "frock", "frog", "frogged",
				"frogman", "frogmarch", "frogspawn", "frolic", "frolicsome", "from", "frond", "front", "frontage",
				"frontal", "frontbench", "frontier", "frontiersman", "frontispiece", "frost", "frostbite",
				"frostbitten", "frostbound", "frosting", "frosty", "froth", "frothy", "frown", "frowst", "frowsty",
				"frowsy", "frowzy", "froze", "frozen", "frs", "fructification", "fructify", "frugal", "frugality",
				"fruit", "fruitcake", "fruiterer", "fruitful", "fruition", "fruitless", "fruits", "fruity", "frump",
				"frustrate", "frustration", "fry", "fryer", "fuchsia", "fuck", "fucker", "fucking", "fuddle", "fudge",
				"fuehrer", "fuel", "fug", "fugitive", "fugue", "fuhrer", "fulcrum", "fulfil", "fulfill", "fulfillment",
				"fulfilment", "full", "fullback", "fuller", "fully", "fulmar", "fulminate", "fulmination", "fulness",
				"fulsome", "fumble", "fume", "fumes", "fumigate", "fun", "function", "functional", "functionalism",
				"functionalist", "functionary", "fund", "fundamental", "fundamentalism", "fundamentally", "funds",
				"funeral", "funerary", "funereal", "funfair", "fungicide", "fungoid", "fungous", "fungus", "funicular",
				"funk", "funky", "funnel", "funnies", "funnily", "funny", "fur", "furbelow", "furbish", "furious",
				"furiously", "furl", "furlong", "furlough", "furnace", "furnish", "furnishings", "furniture", "furore",
				"furrier", "furrow", "furry", "further", "furtherance", "furthermore", "furthermost", "furthest",
				"furtive", "fury", "furze", "fuse", "fused", "fuselage", "fusilier", "fusillade", "fusion", "fuss",
				"fusspot", "fussy", "fustian", "fusty", "futile", "futility", "future", "futureless", "futures",
				"futurism", "futuristic", "futurity", "fuzz", "fuzzy", "gab", "gabardine", "gabble", "gaberdine",
				"gable", "gabled", "gad", "gadabout", "gadfly", "gadget", "gadgetry", "gaelic", "gaff", "gaffe",
				"gaffer", "gag", "gaga", "gaggle", "gaiety", "gaily", "gain", "gainful", "gainfully", "gainsay", "gait",
				"gaiter", "gal", "gala", "galactic", "galantine", "galaxy", "gale", "gall", "gallant", "gallantry",
				"galleon", "gallery", "galley", "gallic", "gallicism", "gallivant", "gallon", "gallop", "galloping",
				"gallows", "gallstone", "galore", "galosh", "galumph", "galvanic", "galvanise", "galvanism",
				"galvanize", "gambit", "gamble", "gamboge", "gambol", "game", "gamecock", "gamekeeper", "games",
				"gamesmanship", "gamey", "gamma", "gammon", "gammy", "gamp", "gamut", "gamy", "gander", "gang",
				"ganger", "gangling", "ganglion", "gangplank", "gangrene", "gangster", "gangway", "gannet", "gantry",
				"gaol", "gaolbird", "gaoler", "gap", "gape", "gapes", "garage", "garb", "garbage", "garble", "garden",
				"gardenia", "gardening", "gargantuan", "gargle", "gargoyle", "garish", "garland", "garlic", "garment",
				"garner", "garnet", "garnish", "garret", "garrison", "garrote", "garrotte", "garrulity", "garrulous",
				"garter", "gas", "gasbag", "gaseous", "gash", "gasholder", "gasify", "gasket", "gaslight", "gasman",
				"gasolene", "gasoline", "gasp", "gassy", "gastric", "gastritis", "gastroenteritis", "gastronomy",
				"gasworks", "gat", "gate", "gatecrash", "gatehouse", "gatekeeper", "gatepost", "gateway", "gather",
				"gathering", "gauche", "gaucherie", "gaucho", "gaudy", "gauge", "gaunt", "gauntlet", "gauze", "gave",
				"gavel", "gavotte", "gawk", "gawky", "gawp", "gay", "gayness", "gaze", "gazebo", "gazelle", "gazette",
				"gazetteer", "gazump", "gce", "gear", "gearbox", "gecko", "gee", "geese", "geezer", "geisha", "gel",
				"gelatine", "gelatinous", "geld", "gelding", "gelignite", "gem", "gemini", "gen", "gendarme", "gender",
				"gene", "genealogist", "genealogy", "genera", "general", "generalisation", "generalise",
				"generalissimo", "generality", "generalization", "generalize", "generally", "generate", "generation",
				"generative", "generator", "generic", "generous", "genesis", "genetic", "geneticist", "genetics",
				"genial", "geniality", "genie", "genital", "genitals", "genitive", "genius", "genocide", "genre",
				"gent", "genteel", "gentian", "gentile", "gentility", "gentle", "gentlefolk", "gentleman",
				"gentlemanly", "gentlewoman", "gently", "gentry", "gents", "genuflect", "genuine", "genus",
				"geocentric", "geographer", "geography", "geologist", "geology", "geometric", "geometry", "geophysics",
				"geopolitics", "georgette", "geranium", "geriatric", "geriatrician", "geriatrics", "germ", "germane",
				"germanic", "germicide", "germinal", "germinate", "gerontology", "gerrymander", "gerund", "gestalt",
				"gestapo", "gestation", "gesticulate", "gesture", "get", "getaway", "getup", "geum", "gewgaw", "geyser",
				"gharry", "ghastly", "ghat", "ghaut", "ghee", "gherkin", "ghetto", "ghi", "ghost", "ghostly", "ghoul",
				"ghoulish", "ghq", "ghyll", "giant", "giantess", "gibber", "gibberish", "gibbet", "gibbon", "gibbous",
				"gibe", "giblets", "giddy", "gift", "gifted", "gig", "gigantic", "giggle", "gigolo", "gild", "gilded",
				"gilding", "gill", "gillie", "gilly", "gilt", "gimcrack", "gimlet", "gimmick", "gimmicky", "gin",
				"ginger", "gingerbread", "gingerly", "gingham", "gingivitis", "gingko", "ginkgo", "ginseng", "gipsy",
				"giraffe", "gird", "girder", "girdle", "girl", "girlfriend", "girlhood", "girlie", "girlish", "girly",
				"giro", "girt", "girth", "gist", "give", "giveaway", "given", "gizzard", "glacial", "glacier", "glad",
				"gladden", "glade", "gladiator", "gladiolus", "gladly", "glamor", "glamorise", "glamorize", "glamorous",
				"glamour", "glamourous", "glance", "glancing", "gland", "glandular", "glare", "glaring", "glass",
				"glassblower", "glasscutter", "glasses", "glasshouse", "glassware", "glassworks", "glassy", "glaucoma",
				"glaucous", "glaze", "glazier", "glazing", "glc", "gleam", "glean", "gleaner", "gleanings", "glebe",
				"glee", "gleeful", "glen", "glengarry", "glib", "glide", "glider", "gliding", "glimmer", "glimmerings",
				"glimpse", "glint", "glissade", "glissando", "glisten", "glister", "glitter", "glittering", "gloaming",
				"gloat", "global", "globe", "globefish", "globetrotter", "globular", "globule", "glockenspiel", "gloom",
				"gloomy", "gloria", "glorification", "glorify", "glorious", "glory", "gloss", "glossary", "glossy",
				"glottal", "glottis", "glove", "glow", "glower", "glowing", "glucose", "glue", "gluey", "glum", "glut",
				"gluten", "glutinous", "glutton", "gluttonous", "gluttony", "glycerin", "glycerine", "gnarled", "gnash",
				"gnat", "gnaw", "gnawing", "gneiss", "gnocchi", "gnome", "gnp", "gnu", "goad", "goal", "goalkeeper",
				"goalmouth", "goalpost", "goat", "goatee", "goatherd", "goatskin", "gob", "gobbet", "gobble",
				"gobbledegook", "gobbledygook", "gobbler", "goblet", "goblin", "god", "godchild", "goddam", "goddamn",
				"goddie", "godforsaken", "godhead", "godless", "godlike", "godly", "godown", "godparent", "gods",
				"godsend", "godspeed", "goer", "goggle", "goggles", "goings", "goiter", "goitre", "gold", "goldbeater",
				"golden", "goldfield", "goldfinch", "goldfish", "goldmine", "goldsmith", "golf", "goliath", "golliwog",
				"golly", "gollywog", "gonad", "gondola", "gondolier", "gone", "goner", "gong", "gonna", "gonorrhea",
				"gonorrhoea", "goo", "good", "goodbye", "goodish", "goodly", "goodness", "goodnight", "goods",
				"goodwill", "goody", "gooey", "goof", "goofy", "googly", "goon", "goose", "gooseberry", "gooseflesh",
				"goosestep", "gopher", "gore", "gorge", "gorgeous", "gorgon", "gorgonzola", "gorilla", "gormandise",
				"gormandize", "gormless", "gorse", "gory", "gosh", "gosling", "gospel", "gossamer", "gossip", "gossipy",
				"got", "gothic", "gotta", "gotten", "gouache", "gouda", "gouge", "goulash", "gourd", "gourmand",
				"gourmet", "gout", "gouty", "govern", "governance", "governess", "governing", "government", "governor",
				"gown", "gpo", "grab", "grace", "graceful", "graceless", "graces", "gracious", "gradation", "grade",
				"gradient", "gradual", "graduate", "graduation", "graffiti", "graft", "grafter", "grail", "grain",
				"gram", "grammar", "grammarian", "grammatical", "gramme", "gramophone", "grampus", "gran", "granary",
				"grand", "grandad", "grandchild", "granddad", "granddaughter", "grandee", "grandeur", "grandfather",
				"grandiloquent", "grandiose", "grandma", "grandmother", "grandpa", "grandparent", "grandson",
				"grandstand", "grange", "granite", "grannie", "granny", "grant", };
	}

	static class KStemData4 {
		private KStemData4() {
		}

		static String[] data = { "granular", "granulate", "granule", "grape", "grapefruit", "grapeshot", "grapevine",
				"graph", "graphic", "graphical", "graphically", "graphite", "graphology", "grapnel", "grapple", "grasp",
				"grasping", "grass", "grasshopper", "grassland", "grassy", "grate", "grateful", "grater",
				"gratification", "gratify", "gratifying", "grating", "gratis", "gratitude", "gratuitous", "gratuity",
				"grave", "gravel", "gravelly", "gravestone", "graveyard", "gravitate", "gravitation", "gravity",
				"gravure", "gravy", "gray", "graybeard", "grayish", "graze", "grease", "greasepaint", "greaseproof",
				"greaser", "greasy", "great", "greatcoat", "greater", "greatly", "grebe", "grecian", "greed", "greedy",
				"green", "greenback", "greenery", "greenfly", "greengage", "greengrocer", "greenhorn", "greenhouse",
				"greenish", "greenroom", "greens", "greenwood", "greet", "greeting", "gregarious", "gremlin", "grenade",
				"grenadier", "grenadine", "grew", "grey", "greybeard", "greyhound", "greyish", "grid", "griddle",
				"gridiron", "grief", "grievance", "grieve", "grievous", "griffin", "grill", "grim", "grimace", "grime",
				"grimy", "grin", "grind", "grinder", "grindstone", "gringo", "grip", "gripe", "gripes", "gripping",
				"grisly", "grist", "gristle", "grit", "grits", "grizzle", "grizzled", "groan", "groat", "groats",
				"grocer", "groceries", "grocery", "grog", "groggy", "groin", "groom", "groove", "groover", "groovy",
				"grope", "gropingly", "gross", "grotesque", "grotto", "grotty", "grouch", "ground", "grounding",
				"groundless", "groundnut", "grounds", "groundsel", "groundsheet", "groundsman", "groundwork", "group",
				"groupie", "grouping", "grouse", "grove", "grovel", "grow", "grower", "growl", "grown", "growth",
				"groyne", "grub", "grubby", "grudge", "grudging", "gruel", "grueling", "gruelling", "gruesome", "gruff",
				"grumble", "grumbling", "grumpy", "grundyism", "grunt", "gryphon", "guano", "guarantee", "guarantor",
				"guaranty", "guard", "guarded", "guardhouse", "guardian", "guardianship", "guardrail", "guardroom",
				"guardsman", "guava", "gubernatorial", "gudgeon", "guerilla", "guerrilla", "guess", "guesswork",
				"guest", "guesthouse", "guestroom", "guffaw", "guidance", "guide", "guidelines", "guild", "guilder",
				"guildhall", "guile", "guileless", "guillemot", "guillotine", "guilt", "guilty", "guinea", "guipure",
				"guise", "guitar", "gulch", "gulden", "gulf", "gull", "gullet", "gulley", "gullible", "gully", "gulp",
				"gum", "gumbo", "gumboil", "gumboot", "gumdrop", "gummy", "gumption", "gun", "gunboat", "gundog",
				"gunfire", "gunge", "gunman", "gunmetal", "gunnel", "gunner", "gunnery", "gunnysack", "gunpoint",
				"gunpowder", "gunrunner", "gunshot", "gunshy", "gunsmith", "gunwale", "guppy", "gurgle", "guru", "gush",
				"gusher", "gushing", "gushy", "gusset", "gust", "gustatory", "gusto", "gusty", "gut", "gutless", "guts",
				"gutsy", "gutter", "guttersnipe", "guttural", "guv", "guvnor", "guy", "guzzle", "gym", "gymkhana",
				"gymnasium", "gymnast", "gymnastic", "gymnastics", "gymslip", "gynaecology", "gynecology", "gyp",
				"gypsum", "gypsy", "gyrate", "gyration", "gyroscope", "gyves", "haberdasher", "haberdashery",
				"habiliment", "habit", "habitable", "habitat", "habitation", "habitual", "habituate", "hacienda",
				"hack", "hackles", "hackney", "hackneyed", "hacksaw", "hackwork", "had", "haddock", "hadji", "haft",
				"hag", "haggard", "haggis", "haggle", "hagiography", "haiku", "hail", "hailstone", "hailstorm", "hair",
				"hairbrush", "haircut", "hairdo", "hairdresser", "hairgrip", "hairless", "hairline", "hairnet",
				"hairpiece", "hairpin", "hairspring", "hairy", "hajji", "hake", "halberd", "halcyon", "hale", "half",
				"halfback", "halfpence", "halfpenny", "halfpennyworth", "halftone", "halfway", "halibut", "halitosis",
				"hall", "halleluja", "halliard", "hallmark", "hallo", "hallow", "hallstand", "hallucinate",
				"hallucination", "hallucinatory", "hallucinogenic", "hallway", "halma", "halo", "halt", "halter",
				"halterneck", "halting", "halve", "halves", "halyard", "ham", "hamadryad", "hamburger", "hamlet",
				"hammer", "hammock", "hamper", "hamster", "hamstring", "hand", "handbag", "handball", "handbarrow",
				"handbill", "handbook", "handbrake", "handcart", "handclap", "handcuff", "handcuffs", "handful",
				"handgun", "handhold", "handicap", "handicraft", "handiwork", "handkerchief", "handle", "handlebars",
				"handler", "handloom", "handmade", "handmaiden", "handout", "handpick", "handrail", "handshake",
				"handsome", "handstand", "handwork", "handwriting", "handwritten", "handy", "handyman", "hang",
				"hangar", "hangdog", "hanger", "hanging", "hangings", "hangman", "hangnail", "hangout", "hangover",
				"hangup", "hank", "hanker", "hankering", "hankie", "hanky", "hansard", "hansom", "hap", "haphazard",
				"hapless", "haply", "happen", "happening", "happily", "happiness", "happy", "harangue", "harass",
				"harassment", "harbinger", "harbor", "harbour", "hard", "hardback", "hardboard", "hardbound", "harden",
				"hardheaded", "hardihood", "hardiness", "hardly", "hardness", "hardship", "hardtop", "hardware",
				"hardwearing", "hardwood", "hardy", "hare", "harebell", "harebrained", "harelip", "harem", "haricot",
				"hark", "harlequin", "harlequinade", "harlot", "harm", "harmless", "harmonic", "harmonica", "harmonise",
				"harmonium", "harmonize", "harmony", "harness", "harp", "harpoon", "harpsichord", "harpy", "harquebus",
				"harridan", "harrier", "harrow", "harrowing", "harry", "harsh", "hart", "hartal", "hartebeest",
				"harvest", "harvester", "has", "hash", "hashish", "hasp", "hassle", "hassock", "hast", "haste",
				"hasten", "hasty", "hat", "hatband", "hatch", "hatchback", "hatchery", "hatchet", "hatching",
				"hatchway", "hate", "hateful", "hath", "hatless", "hatpin", "hatred", "hatter", "hauberk", "haughty",
				"haul", "haulage", "haulier", "haulm", "haunch", "haunt", "haunting", "hautbois", "hautboy", "hauteur",
				"havana", "have", "haven", "haver", "haversack", "haves", "havoc", "haw", "hawk", "hawker", "hawser",
				"hawthorn", "hay", "haycock", "hayfork", "haymaker", "haystack", "haywire", "hazard", "hazardous",
				"haze", "hazel", "hazy", "head", "headache", "headband", "headboard", "headcheese", "headdress",
				"header", "headfirst", "headgear", "headhunter", "heading", "headland", "headless", "headlight",
				"headline", "headlong", "headman", "headmaster", "headphone", "headpiece", "headquarters", "headrest",
				"headroom", "headset", "headship", "headshrinker", "headstall", "headstone", "headstrong", "headway",
				"headwind", "headword", "heady", "heal", "health", "healthful", "healthy", "heap", "hear", "hearer",
				"hearing", "hearken", "hearsay", "hearse", "heart", "heartache", "heartbeat", "heartbreak",
				"heartbreaking", "heartbroken", "heartburn", "hearten", "heartening", "heartfelt", "hearth",
				"hearthrug", "heartily", "heartless", "heartrending", "heartsease", "heartsick", "heartstrings",
				"heartthrob", "heartwarming", "heartwood", "hearty", "heat", "heated", "heater", "heath", "heathen",
				"heather", "heating", "heatstroke", "heave", "heaven", "heavenly", "heavenwards", "heavy",
				"heavyhearted", "heavyweight", "hebdomadal", "hebraic", "hebrew", "hecatomb", "heck", "heckle",
				"hectare", "hectic", "hector", "hedge", "hedgehog", "hedgehop", "hedgerow", "hedonism", "heed", "heel",
				"heelball", "hefty", "hegemony", "hegira", "heifer", "height", "heighten", "heinous", "heir", "heiress",
				"heirloom", "hejira", "held", "helicopter", "heliograph", "heliotrope", "heliport", "helium", "hell",
				"hellcat", "hellene", "hellenic", "hellenistic", "hellish", "hellishly", "hello", "helm", "helmet",
				"helmeted", "helmsman", "helot", "help", "helpful", "helping", "helpless", "helpmate", "helve", "hem",
				"hemisphere", "hemline", "hemlock", "hemoglobin", "hemophilia", "hemophiliac", "hemorrhage",
				"hemorrhoid", "hemp", "hempen", "hemstitch", "hen", "henbane", "hence", "henceforth", "henchman",
				"henna", "hennaed", "henpecked", "hepatitis", "heptagon", "her", "herald", "heraldic", "heraldry",
				"herb", "herbaceous", "herbage", "herbal", "herbalist", "herbivorous", "herculean", "herd", "herdsman",
				"here", "hereabouts", "hereafter", "hereby", "hereditament", "hereditary", "heredity", "herein",
				"hereinafter", "hereof", "heresy", "heretic", "hereto", "heretofore", "hereunder", "hereupon",
				"herewith", "heritable", "heritage", "hermaphrodite", "hermetic", "hermit", "hermitage", "hernia",
				"hero", "heroic", "heroics", "heroin", "heroism", "heron", "heronry", "herpes", "herr", "herring",
				"herringbone", "hers", "herself", "hertz", "hesitancy", "hesitant", "hesitate", "hesitation",
				"hesperus", "hessian", "heterodox", "heterodoxy", "heterogeneous", "heterosexual", "heuristic",
				"heuristics", "hew", "hewer", "hex", "hexagon", "hexagram", "hexameter", "hey", "heyday", "hiatus",
				"hibernate", "hibiscus", "hiccough", "hiccup", "hick", "hickory", "hide", "hideaway", "hidebound",
				"hideous", "hiding", "hie", "hierarchy", "hieroglyph", "hieroglyphics", "high", "highball", "highborn",
				"highboy", "highbrow", "higher", "highfalutin", "highland", "highlander", "highlands", "highlight",
				"highly", "highness", "highpitched", "highroad", "highway", "highwayman", "hijack", "hike", "hilarious",
				"hilarity", "hill", "hillbilly", "hillock", "hillside", "hilly", "hilt", "him", "himself", "hind",
				"hinder", "hindmost", "hindquarters", "hindrance", "hindsight", "hindu", "hinduism", "hinge", "hint",
				"hinterland", "hip", "hipbath", "hippie", "hippodrome", "hippopotamus", "hippy", "hipster", "hire",
				"hireling", "hirsute", "his", "hiss", "hist", "histamine", "histology", "historian", "historic",
				"historical", "history", "histrionic", "histrionics", "hit", "hitch", "hitchhike", "hither", "hitherto",
				"hive", "hives", "hms", "hoard", "hoarding", "hoarfrost", "hoarse", "hoary", "hoax", "hob", "hobble",
				"hobbledehoy", "hobby", "hobbyhorse", "hobgoblin", "hobnail", "hobnob", "hobo", "hock", "hockey", "hod",
				"hodgepodge", "hoe", "hog", "hoggish", "hogmanay", "hogshead", "hogwash", "hoist", "hold", "holdall",
				"holder", "holding", "holdover", "holdup", "hole", "holiday", "holidaymaker", "holiness", "holler",
				"hollow", "holly", "hollyhock", "hollywood", "holocaust", "holograph", "holstein", "holster", "holy",
				"homage", "homburg", "home", "homecoming", "homegrown", "homeland", "homelike", "homely", "homemade",
				"homeopath", "homeopathy", "homeric", "homesick", "homespun", "homestead", "hometown", "homeward",
				"homewards", "homework", "homey", "homicidal", "homicide", "homiletic", "homiletics", "homily",
				"homing", "hominy", "homoeopath", "homoeopathy", "homogeneous", "homogenise", "homogenize", "homograph",
				"homonym", "homophone", "homosexual", "homy", "hone", "honest", "honestly", "honesty", "honey",
				"honeybee", "honeycomb", "honeycombed", "honeydew", "honeyed", "honeymoon", "honeysuckle", "honk",
				"honkie", "honky", "honor", "honorable", "honorarium", "honorary", "honorific", "honors", "honour",
				"honourable", "honours", "hooch", "hood", "hooded", "hoodlum", "hoodoo", "hoodwink", "hooey", "hoof",
				"hook", "hookah", "hooked", "hooker", "hookey", "hookup", "hookworm", "hooky", "hooligan", "hoop",
				"hooray", "hoot", "hooter", "hoover", "hooves", "hop", "hope", "hopeful", "hopefully", "hopeless",
				"hopper", "hopscotch", "horde", "horizon", "horizontal", "hormone", "horn", "hornbeam", "hornbill",
				"horned", "hornet", "hornpipe", "horny", "horology", "horoscope", "horrendous", "horrible", "horrid",
				"horrific", "horrify", "horror", "horrors", "horse", "horseback", "horsebox", "horseflesh", "horsefly",
				"horsehair", "horselaugh", "horseman", "horsemanship", "horsemeat", "horseplay", "horsepower",
				"horseracing", "horseradish", "horseshit", "horseshoe", "horsewhip", "horsewoman", "horsy", "hortative",
				"horticulture", "hosanna", "hose", "hosier", "hosiery", "hospice", "hospitable", "hospital",
				"hospitalise", "hospitality", "hospitalize", "host", "hostage", "hostel", "hosteler", "hosteller",
				"hostelry", "hostess", "hostile", "hostilities", "hostility", "hostler", "hot", "hotbed", "hotchpotch",
				"hotel", "hotelier", "hotfoot", "hothead", "hothouse", "hotly", "hotplate", "hotpot", "hottentot",
				"hound", "hour", "hourglass", "houri", "hourly", "house", "houseboat", "housebound", "houseboy",
				"housebreaker", "housebroken", "housecoat", "housecraft", "housedog", "housefather", "housefly",
				"houseful", "household", "householder", "housekeeper", "housekeeping", "housemaid", "houseman",
				"housemaster", "housemother", "houseroom", "housetops", "housewarming", "housewife", "housewifery",
				"housework", "housing", "hove", "hovel", "hover", "hovercraft", "how", "howdah", "howdy", "however",
				"howitzer", "howl", "howler", "howling", "howsoever", "hoyden", "hrh", "hub", "hubbub", "hubby",
				"hubcap", "hubris", "huckaback", "huckleberry", "huckster", "huddle", "hue", "huff", "huffish", "huffy",
				"hug", "huge", "hugely", "huguenot", "huh", "hula", "hulk", "hulking", "hull", "hullabaloo", "hullo",
				"hum", "human", "humane", "humanise", "humanism", "humanitarian", "humanitarianism", "humanities",
				"humanity", "humanize", "humankind", "humanly", "humble", "humbug", "humdinger", "humdrum", "humerus",
				"humid", "humidify", "humidity", "humidor", "humiliate", "humility", "hummingbird", "hummock", "humor",
				"humorist", "humorous", "humour", "hump", "humpback", "humph", "humus", "hun", "hunch", "hunchback",
				"hundred", "hundredweight", "hung", "hunger", "hungry", "hunk", "hunkers", "hunt", "hunter", "hunting",
				"huntress", "huntsman", "hurdle", "hurl", "hurling", "hurray", "hurricane", "hurried", "hurry", "hurt",
				"hurtful", "hurtle", "husband", "husbandman", "husbandry", "hush", "husk", "husky", "hussar", "hussy",
				"hustings", "hustle", "hustler", "hut", "hutch", "hutment", "huzza", "huzzah", "hyacinth", "hyaena",
				"hybrid", "hybridise", "hybridize", "hydra", "hydrangea", "hydrant", "hydrate", "hydraulic",
				"hydraulics", "hydrocarbon", "hydroelectric", "hydrofoil", "hydrogen", "hydrophobia", "hydroplane",
				"hydroponics", "hydrotherapy", "hyena", "hygiene", "hygienic", "hymen", "hymeneal", "hymn", "hymnal",
				"hyperbola", "hyperbole", "hyperbolic", "hypercritical", "hypermarket", "hypersensitive", "hyphen",
				"hyphenate", "hypnosis", "hypnotise", "hypnotism", "hypnotist", "hypnotize", "hypo", "hypochondria",
				"hypochondriac", "hypocrisy", "hypocrite", "hypodermic", "hypotenuse", "hypothermia", "hypothesis",
				"hypothetical", "hysterectomy", "hysteria", "hysterical", "hysterics", "iamb", "iberian", "ibex",
				"ibidem", "ibis", "icbm", "ice", "iceberg", "icebound", "icebox", "icebreaker", "icefall", "icehouse",
				"iceman", "icicle", "icing", "icon", "iconoclast", "icy", "idea", "ideal", "idealise", "idealism",
				"idealist", "idealize", "ideally", "idem", "identical", "identification", "identify", "identikit",
				"identity", "ideogram", "ideology", "ides", "idiocy", "idiom", "idiomatic", "idiosyncrasy", "idiot",
				"idle", "idol", "idolater", "idolatrous", "idolatry", "idolise", "idolize", "idyl", "idyll", "igloo",
				"igneous", "ignite", "ignition", "ignoble", "ignominious", "ignominy", "ignoramus", "ignorance",
				"ignorant", "ignore", "iguana", "ikon", "ilex", "ilk", "ill", "illegal", "illegality", "illegible",
				"illegitimate", "illiberal", "illicit", "illimitable", "illiterate", "illness", "illogical",
				"illuminate", "illuminating", "illumination", "illuminations", "illusion", "illusionist", "illusory",
				"illustrate", "illustration", "illustrative", "illustrator", "illustrious", "image", "imagery",
				"imaginable", "imaginary", "imagination", "imaginative", "imagine", "imam", "imbalance", "imbecile",
				"imbecility", "imbed", "imbibe", "imbroglio", "imbue", "imitate", "imitation", "imitative", "imitator",
				"immaculate", "immanence", "immanent", "immaterial", "immature", "immeasurable", "immediacy",
				"immediate", "immediately", "immemorial", "immense", "immensely", "immensity", "immerse", "immersion",
				"immigrant", "immigrate", "imminence", "imminent", "immobile", "immobilise", "immobilize", "immoderate",
				"immodest", "immolate", "immoral", "immorality", "immortal", "immortalise", "immortality",
				"immortalize", "immovable", "immune", "immunise", "immunize", "immure", "immutable", "imp", "impact",
				"impacted", "impair", "impala", "impale", "impalpable", "impanel", "impart", "impartial", "impassable",
				"impasse", "impassioned", "impassive", "impatience", "impatient", "impeach", "impeccable",
				"impecunious", "impedance", "impede", "impediment", "impedimenta", "impel", "impending", "impenetrable",
				"impenitent", "imperative", "imperceptible", "imperfect", "imperial", "imperialism", "imperialist",
				"imperialistic", "imperil", "imperious", "imperishable", "impermanent", "impermeable", "impersonal",
				"impersonate", "impertinent", "imperturbable", "impervious", "impetigo", "impetuous", "impetus",
				"impiety", "impinge", "impious", "impish", "implacable", "implant", "implement", "implicate",
				"implication", "implicit", "implore", "implosion", "imply", "impolite", "impolitic", "imponderable",
				"import", "importance", "important", "importation", "importunate", "importune", "impose", "imposing",
				"imposition", "impossible", "impostor", "imposture", "impotent", "impound", "impoverish",
				"impracticable", "impractical", "imprecation", "impregnable", "impregnate", "impresario", "impress",
				"impression", "impressionable", "impressionism", "impressionist", "impressionistic", "impressive",
				"imprimatur", "imprint", "imprison", "improbability", "improbable", "impromptu", "improper",
				"impropriety", "improve", "improvement", "improvident", "improvise", "imprudent", "impudent", "impugn",
				"impulse", "impulsion", "impulsive", "impunity", "impure", "impurity", "imputation", "impute",
				"inability", "inaccessible", "inaccurate", "inaction", "inactive", "inadequacy", "inadequate",
				"inadmissible", "inadvertent", "inalienable", "inamorata", "inane", "inanimate", "inanition", "inanity",
				"inapplicable", "inappropriate", "inapt", "inaptitude", "inarticulate", "inartistic", "inattention",
				"inattentive", "inaudible", "inaugural", "inaugurate", "inauspicious", "inboard", "inborn", "inbound",
				"inbred", "inbreeding", "inc", "incalculable", "incandescent", "incantation", "incapable",
				"incapacitate", "incapacity", "incarcerate", "incarnate", "incarnation", "incautious", "incendiarism",
				"incendiary", "incense", "incentive", "inception", "incertitude", "incessant", "incest", "incestuous",
				"inch", "inchoate", "incidence", "incident", "incidental", "incidentally", "incidentals", "incinerate",
				"incinerator", "incipience", "incipient", "incise", "incision", "incisive", "incisor", "incite",
				"incivility", "inclement", "inclination", "incline", "inclined", "inclose", "inclosure", "include",
				"included", "including", "inclusion", "inclusive", "incognito", "incoherent", "incombustible", "income",
				"incoming", "incommensurable", "incommensurate", "incommode", "incommodious", "incommunicable",
				"incommunicado", "incommunicative", "incomparable", "incompatible", "incompetence", "incompetent",
				"incomplete", "incomprehensible", "incomprehensibly", "incomprehension", "inconceivable",
				"inconclusive", "incongruity", "incongruous", "inconsequent", "inconsequential", "inconsiderable",
				"inconsiderate", "inconsistent", "inconsolable", "inconspicuous", "inconstant", "incontestable",
				"incontinent", "incontrovertible", "inconvenience", "inconvenient", "incorporate", "incorporated",
				"incorporeal", "incorrect", "incorrigible", "incorruptible", "increase", "increasingly", "incredible",
				"incredulity", "incredulous", "increment", "incriminate", "incrust", "incrustation", "incubate",
				"incubation", "incubator", "incubus", "inculcate", "inculpate", "incumbency", "incumbent", "incur",
				"incurable", "incurious", "incursion", "incurved", "indebted", "indecent", "indecipherable",
				"indecision", "indecisive", "indecorous", "indecorum", "indeed", "indefatigable", "indefensible",
				"indefinable", "indefinite", "indefinitely", "indelible", "indelicate", "indemnification", "indemnify",
				"indemnity", "indent", "indentation", "indenture", "independence", "independent", "indescribable",
				"indestructible", "indeterminable", "indeterminate", "index", "indian", "indicate", "indication",
				"indicative", "indicator", "indices", "indict", "indictable", "indifferent", "indigenous", "indigent",
				"indigestible", "indigestion", "indignant", "indignation", "indignity", "indigo", "indirect",
				"indiscernible", "indiscipline", "indiscreet", "indiscretion", "indiscriminate", "indispensable",
				"indisposed", "indisposition", "indisputable", "indissoluble", "indistinct", "indistinguishable",
				"individual", "individualise", "individualism", "individuality", "individualize", "individually",
				"indivisible", "indocile", "indoctrinate", "indolent", "indomitable", "indoor", "indoors", "indorse",
				"indrawn", "indubitable", "induce", "inducement", "induct", "induction", "inductive", "indue",
				"indulge", "indulgence", "indulgent", "industrial", "industrialise", "industrialism", "industrialist",
				"industrialize", "industrious", "industry", "inebriate", "inedible", "ineducable", "ineffable",
				"ineffaceable", "ineffective", "ineffectual", "inefficient", "inelastic", "inelegant", "ineligible",
				"ineluctable", "inept", "ineptitude", "inequality", "inequitable", "inequity", "ineradicable", "inert",
				"inertia", "inescapable", "inessential", "inestimable", "inevitable", "inexact", "inexactitude",
				"inexcusable", "inexhaustible", "inexorable", "inexpediency", "inexpedient", "inexpensive",
				"inexperience", "inexperienced", "inexpert", "inexpiable", "inexplicable", "inexplicably",
				"inexpressible", "inextinguishable", "inextricable", "infallible", "infallibly", "infamous", "infamy",
				"infancy", "infant", "infanticide", "infantile", "infantry", "infantryman", "infatuated", "infatuation",
				"infect", "infection", "infectious", "infelicitous", "infer", "inference", "inferential", "inferior",
				"infernal", "inferno", "infertile", "infest", "infidel", "infidelity", "infield", "infighting",
				"infiltrate", "infiltration", "infinite", "infinitesimal", "infinitive", "infinitude", "infinity",
				"infirm", "infirmary", "infirmity", "inflame", "inflamed", "inflammable", "inflammation",
				"inflammatory", "inflatable", "inflate", "inflated", "inflation", "inflationary", "inflect",
				"inflection", "inflexible", "inflexion", "inflict", "infliction", "inflow", "influence", "influential",
				"influenza", "influx", "info", "inform", "informal", "informant", "information", "informative",
				"informed", "informer", "infra", "infraction", "infrared", "infrastructure", "infrequent", "infringe",
				"infuriate", "infuse", "infusion", "ingathering", "ingenious", "ingenuity", "ingenuous", "ingest",
				"inglenook", "inglorious", "ingoing", "ingot", "ingraft", "ingrained", "ingratiate", "ingratiating",
				"ingratitude", "ingredient", "ingress", "ingrown", "inhabit", "inhabitant", "inhale", "inhaler",
				"inharmonious", "inhere", "inherent", "inherently", "inherit", "inheritance", "inhibit", "inhibited",
				"inhibition", "inhospitable", "inhuman", "inhumane", "inhumanity", "inimical", "inimitable",
				"iniquitous", "iniquity", "initial", "initially", "initiate", "initiation", "initiative", "inject",
				"injection", "injudicious", "injunction", "injure", "injurious", "injury", "injustice", "ink",
				"inkbottle", "inkling", "inkpad", "inkstand", "inkwell", "inky", "inlaid", "inland", "inlay", "inlet",
				"inmate", "inmost", "inn", "innards", "innate", "inner", "inning", "innings", "innkeeper", "innocent",
				"innocuous", "innovate", "innovation", "innuendo", "innumerable", "inoculate", "inoffensive",
				"inoperable", "inoperative", "inopportune", "inordinate", "inorganic", "input", "inquest", "inquietude",
				"inquire", "inquiring", "inquiry", "inquisition", "inquisitive", "inquisitor", "inquisitorial",
				"inroad", "inrush", "insalubrious", "insane", "insanitary", "insanity", "insatiable", "insatiate",
				"inscribe", "inscription", "inscrutable", "insect", "insecticide", "insectivore", "insectivorous",
				"insecure", "inseminate", "insemination", "insensate", "insensibility", "insensible", "insensitive",
				"inseparable", "insert", "insertion", "inset", "inshore", "inside", "insider", "insidious", "insight",
				"insignia", "insignificant", "insincere", "insinuate", "insinuation", "insipid", "insist", "insistence",
				"insistency", "insistent", "insole", "insolent", "insoluble", "insolvable", "insolvent", "insomnia",
				"insomniac", "insouciance", "inspect", "inspection", "inspector", "inspectorate", "inspectorship",
				"inspiration", "inspire", "inspired", "instability", "install", "installation", "installment",
				"instalment", "instance", "instant", "instantaneous", "instantly", "instead", "instep", "instigate",
				"instigation", "instil", "instill", "instinct", "instinctive", "institute", "institution", "instruct",
				"instruction", "instructive", "instructor", "instructress", "instrument", "instrumental",
				"instrumentalist", "instrumentality", "instrumentation", "insubordinate", "insubstantial",
				"insufferable", "insufficiency", "insufficient", "insular", "insularity", "insulate", "insulation",
				"insulator", "insulin", "insult", "insuperable", "insupportable", "insurance", "insure", "insured",
				"insurer", "insurgent", "insurmountable", "insurrection", "intact", "intaglio", "intake", "intangible",
				"integer", "integral", "integrate", "integrated", "integrity", "integument", "intellect",
				"intellectual", "intelligence", "intelligent", "intelligentsia", "intelligible", "intemperate",
				"intend", "intended", "intense", "intensifier", "intensify", "intensity", "intensive", "intent",
				"intention", "intentional", "intentions", "inter", "interact", "interaction", "interbreed",
				"intercalary", "intercalate", "intercede", "intercept", "interceptor", "intercession", "interchange",
				"interchangeable", "intercity", "intercollegiate", "intercom", "intercommunicate", "intercommunion",
				"intercontinental", "intercourse", "interdenominational", "interdependent", "interdict", "interest",
				"interested", "interesting", "interests", "interface", "interfere", "interference", "interim",
				"interior", "interject", "interjection", "interlace", "interlard", "interleave", "interline",
				"interlinear", "interlink", "interlock", "interlocutor", "interloper", "interlude", "intermarriage",
				"intermarry", "intermediary", "intermediate", "interment", "intermezzo", "interminable", "intermingle",
				"intermission", "intermittent", "intern", "internal", "internalise", "internalize", "international",
				"internationale", "internationalise", "internationalism", "internationalize", "interne", "internecine",
				"internee", "internment", "interpellate", "interpenetrate", "interpersonal", "interplanetary",
				"interplay", "interpol", "interpolate", "interpolation", "interpose", "interposition", "interpret",
				"interpretation", "interpretative", "interpreter", "interracial", "interregnum", "interrelate",
				"interrelation", "interrogate", "interrogative", "interrogatory", "interrupt", "intersect",
				"intersection", "intersperse", "interstate", "interstellar", "interstice", "intertribal", "intertwine",
				"interurban", "interval", "intervene", "intervention", "interview", "interweave", "intestate",
				"intestinal", "intestine", "intimacy", "intimate", "intimidate", "intimidation", "into", "intolerable",
				"intolerant", "intonation", "intone", "intoxicant", "intoxicate", "intractable", "intramural",
				"intransigent", "intransitive", "intravenous", "intrench", "intrepid", "intricacy", "intricate",
				"intrigue", "intrinsic", "intro", "introduce", "introduction", "introductory", "introit",
				"introspection", "introspective", "introvert", "introverted", "intrude", "intruder", "intrusion",
				"intrusive", "intrust", "intuit", "intuition", "intuitive", "intumescence", "inundate", "inundation",
				"inure", "invade", "invalid", "invalidate", "invalidism", "invaluable", "invariable", "invasion",
				"invective", "inveigh", "inveigle", "invent", "invention", "inventive", "inventor", "inventory",
				"inverse", "inversion", "invert", "invertebrate", "invest", "investigate", "investiture", "investment",
				"inveterate", "invidious", "invigilate", "invigorate", "invincible", "inviolable", "inviolate",
				"invisible", "invitation", "invite", "inviting", "invocation", "invoice", "invoke", "involuntary",
				"involve", "involved", "invulnerable", "inward", "inwardness", "inwards", "inwrought", "iodin",
				"iodine", "iodise", "iodize", "ion", "ionic", "ionise", "ionize", "ionosphere", "iota", "iou", "ipa",
				"ira", "irascible", "irate", "ire", "iridescent", "iridium", "irishman", "irk", "irksome", "iron",
				"ironclad", "ironic", "ironically", "ironing", "ironmonger", "ironmongery", "ironmould", "irons",
				"ironstone", "ironware", "ironwork", "ironworks", "irony", "irradiate", "irrational", "irreconcilable",
				"irrecoverable", "irredeemable", "irreducible", "irrefutable", "irregular", "irregularity",
				"irrelevance", "irrelevant", "irreligious", "irremediable", "irremovable", "irreparable",
				"irreplaceable", "irrepressible", "irreproachable", "irresistible", "irresolute", "irresponsible",
				"irretrievable", "irreverent", "irreversible", "irrevocable", "irrigate", "irritable", "irritant",
				"irritate", "irritation", "irruption", "isinglass", "islam", "island", "islander", "isle", "islet",
				"ism", "isobar", "isolate", "isolated", "isolation", "isolationism", "isotherm", "isotope", "israelite",
				"issue", "isthmus", "ita", "italic", "italicise", "italicize", "italics", "itch", "itchy", "item",
				"itemise", "itemize", "iterate", "itinerant", "itinerary", "itn", "its", "itself", "itv", "iud",
				"ivied", "ivory", "ivy", "jab", "jabber", "jack", "jackal", "jackanapes", "jackaroo", "jackass",
				"jackboot", "jackdaw", "jackeroo", "jacket", "jackpot", "jackrabbit", "jacobean", "jacobite", "jade",
				"jaded", "jaffa", "jag", "jagged", "jaguar", "jail", "jailbird", "jailbreak", "jailer", "jailor",
				"jalopy", "jam", "jamb", "jamboree", "jammy", "jangle", "janissary", "janitor", "january", "japan",
				"jape", "japonica", "jar", "jargon", "jasmine", "jasper", "jaundice", "jaundiced", "jaunt", "jaunty",
				"javelin", "jaw", "jawbone", "jawbreaker", "jaws", "jay", "jaywalk", "jazz", "jazzy", "jealous",
				"jealousy", "jeans", "jeep", "jeer", "jehovah", "jejune", "jell", "jellied", "jello", "jelly",
				"jellyfish", "jemmy", "jenny", "jeopardise", "jeopardize", "jeopardy", "jerboa", "jeremiad", "jerk",
				"jerkin", "jerky", "jeroboam", "jerry", "jersey", "jest", "jester", "jesting", "jesuit", "jesuitical",
				"jet", "jetsam", "jettison", "jetty", "jew", "jewel", "jeweled", "jeweler", "jewelled", "jeweller",
				"jewellery", "jewelry", "jewess", "jewish", "jezebel", "jib", "jibe", "jiffy", "jig", "jigger",
				"jiggered", "jiggle", "jigsaw", "jihad", "jilt", "jiminy", "jimjams", "jimmy", "jingle", "jingo",
				"jingoism", "jinks", "jinn", "jinrikisha", "jinx", "jitney", "jitterbug", "jitters", "jiujitsu", "jive",
				"jnr", "job", "jobber", "jobbery", "jobbing", "jobless", "jockey", "jockstrap", "jocose", "jocular",
				"jocund", "jodhpurs", "jog", "joggle", "john", "johnny", "join", "joiner", "joinery", "joint", "joist",
				"joke", "joker", "jollification", "jollity", "jolly", "jolt", "jolty", "jonah", "jonquil", "josh",
				"jostle", "jot", "jotter", "jotting", "joule", "journal", "journalese", "journalism", "journalist",
				"journey", "journeyman", "joust", "jove", "jovial", "jowl", "joy", "joyful", "joyless", "joyous",
				"joyride", "joystick", "jubilant", "jubilation", "jubilee", "judaic", "judaism", "judder", "judge",
				"judgement", "judgment", "judicature", "judicial", "judiciary", "judicious", "judo", "jug",
				"juggernaut", "juggle", "juice", "juicy", "jujitsu", "juju", "jujube", "jukebox", "julep", "july",
				"jumble", "jumbo", "jump", "jumper", "jumps", "jumpy", "junction", "juncture", "june", "jungle",
				"junior", "juniper", "junk", "junket", "junketing", "junkie", "junky", "junoesque", "junta", "jupiter",
				"juridical", "jurisdiction", "jurisprudence", "jurist", "juror", "jury", "juryman", "just", "justice",
				"justifiable", "justification", "justified", "justify", "jut", "jute", "juvenile", "juxtapose",
				"juxtaposition", "kaffir", "kafir", "kaftan", "kail", "kaiser", "kale", "kaleidoscope", "kaleidoscopic",
				"kalends", "kampong", "kangaroo", "kaolin", "kapok", "kappa", "kaput", "karat", "karate", "karma",
				"katydid", "kayak", "kazoo", "kebab", "kebob", "kedgeree", "keel", "keelhaul", "keen", "keep", "keeper",
				"keeping", "keeps", "keepsake", "keg", "kelp", "kelvin", "ken", "kennel", "kennels", "kepi", "kept",
				"kerb", "kerchief", "kerfuffle", "kernel", "kerosene", "kerosine", "kersey", "kestrel", "ketch",
				"ketchup", "kettle", "kettledrum", "key", "keyboard", "keyhole", "keyless", "keynote", "keypunch",
				"keystone", "khaki", "khalif", "khalifate", "khan", "kibbutz", "kibosh", "kick", "kickback", "kicker",
				"kickoff", "kicks", "kid", "kiddie", "kiddy", "kidnap", "kidney", "kike", "kill", "killer", "killing",
				"killjoy", "kiln", "kilo", "kilogram", "kilogramme", "kilohertz", "kiloliter", "kilolitre", "kilometer",
				"kilometre", "kilowatt", "kilt", "kimono", "kin", "kind", "kindergarten", "kindle", "kindling",
				"kindly", "kindness", "kindred", "kine", "kinetic", "kinetics", "kinfolk", "king", "kingcup", "kingdom",
				"kingfisher", "kingly", "kingmaker", "kingpin", "kings", "kingship", "kink", "kinky", "kinsfolk",
				"kinship", "kinsman", "kiosk", "kip", "kipper", "kirk", "kirsch", "kirtle", "kismet", "kiss", "kisser",
				"kit", "kitchen", "kitchenette", "kite", "kitsch", "kitten", "kittenish", "kittiwake", "kitty", "kiwi",
				"klaxon", "kleenex", "kleptomania", "kleptomaniac", "knack", "knacker", "knackered", "knapsack",
				"knave", "knavery", "knead", "knee", "kneecap", "kneel", "knell", "knew", "knickerbockers", "knickers",
				"knife", "knight", "knighthood", "knightly", "knit", "knitter", "knitting", "knitwear", "knives",
				"knob", "knobbly", "knobkerrie", "knock", "knockabout", "knockdown", "knocker", "knockers", "knockout",
				"knoll", "knot", "knothole", "knotty", "knout", "know", "knowing", "knowingly", "knowledge",
				"knowledgeable", "known", "knuckle", "koala", "kohl", "kohlrabi", "kookaburra", "kopeck", "kopek",
				"kopje", "koppie", "koran", "kosher", "kowtow", "kraal", "kremlin", "kris", "krona", "krone", "kudos",
				"kukri", "kumis", "kumquat", "kuomintang", "kurus", "kvass", "kwashiorkor", "kwela", "laager", "lab",
				"label", "labial", "labor", "laboratory", "laborer", "laborious", "labour", "labourer", "labourite",
				"labrador", "laburnum", "labyrinth", "lace", "lacerate", "laceration", "lachrymal", "lachrymose",
				"lack", "lackadaisical", "lackey", "lacking", "lackluster", "lacklustre", "laconic", "lacquer",
				"lacrosse", "lactation", "lactic", "lactose", "lacuna", "lacy", "lad", "ladder", "laddie", "laddy",
				"laden", "ladies", "lading", "ladle", "lady", "ladybird", "ladylike", "ladyship", "lag", "lager",
				"laggard", "lagging", "lagoon", "laid", "lain", "lair", "laird", "laity", "lake", "lam", "lama",
				"lamaism", "lamasery", "lamb", "lambaste", "lambent", "lambkin", "lamblike", "lambskin", "lame",
				"lament", "lamentable", "lamentation", "laminate", "lamming", "lamp", "lampoon", "lamppost", "lamprey",
				"lampshade", "lance", "lancer", "lancers", "lancet", "land", "landau", "landed", "landfall", "landing",
				"landlady", "landlocked", "landlord", "landlubber", "landmark", "landmine", "lands", "landscape",
				"landslide", "landslip", "landward", "landwards", "lane", "language", "languid", "languish", "languor",
				"lank", "lanky", "lanolin", "lantern", "lanternslide", "lanyard", "lap", "lapdog", "lapel", "lapidary",
				"lapse", "lapsed", "lapwing", "larboard", "larceny", "larch", "lard", "larder", "large", "largely",
				"largess", "largesse", "largo", "lariat", "lark", "larkspur", "larrup", "larva", "laryngeal",
				"laryngitis", "laryngoscope", "larynx", "lasagna", "lascivious", "laser", "lash", "lashing", "lashings",
				"lass", "lasso", "last", "lasting", "lastly", "lat", "latch", "latchkey", "late", "latecomer", "lately",
				"latent", "lateral", "latest", "latex", "lath", "lathe", "lather", "latin", "latinise", "latinize",
				"latitude", "latitudes", "latitudinal", "latitudinarian", "latrine", "latter", "latterly", "lattice",
				"laud", "laudable", "laudanum", "laudatory", "laugh", "laughable", "laughingstock", "laughter",
				"launch", "launder", "launderette", "laundress", "laundry", "laureate", "laurel", "laurels", "lava",
				"lavatory", "lave", "lavender", "lavish", "law", "lawful", "lawless", "lawn", "lawsuit", "lawyer",
				"lax", "laxative", "laxity", "lay", "layabout", "layer", "layette", "layman", "layout", "laze", "lazy",
				"lbw", "lcm", "lea", "leach", "lead", "leaden", "leader", "leadership", "leading", "leads", "leaf",
				"leafage", "leafed", "leaflet", "leafy", "league", "leak", "leakage", "leaky", "lean", "leaning",
				"leap", "leapfrog", "learn", "learned", "learner", "learning", "lease", "leasehold", "leash", "least",
				"leastways", "leather", "leatherette", "leathery", "leave", "leaved", "leaven", "leavening", "leaves",
				"leavings", "lech", "lecher", "lecherous", "lechery", "lectern", "lecture", "lecturer", "lectureship",
				"led", "ledge", "ledger", "lee", "leech", "leek", "leer", "leery", "lees", "leeward", "leeway", "left",
				"leftist", "leftovers", "leftward", "leftwards", "leg", "legacy", "legal", "legalise", "legality",
				"legalize", "legate", "legatee", "legation", "legato", "legend", "legendary", "leger", "legerdemain",
				"legged", "leggings", "leggy", "legible", "legion", "legionary", "legislate", "legislation",
				"legislative", "legislator", "legislature", "legit", "legitimate", "legitimatise", "legitimatize",
				"legroom", "legume", "leguminous", "lei", "leisure", "leisured", "leisurely", "leitmotif", "leitmotive",
				"lemming", "lemon", "lemonade", "lemur", "lend", "length", "lengthen", "lengthways", "lengthy",
				"lenience", "lenient", "lenity", "lens", "lent", "lentil", "lento", "leo", "leonine", "leopard",
				"leotard", "leper", "leprechaun", "leprosy", "lesbian", "lesion", "less", "lessee", "lessen", "lesser",
				"lesson", "lessor", "lest", "let", "letdown", "lethal", "lethargy", "letraset", "letter", "letterbox",
				"lettered", "letterhead", "lettering", "letterpress", "letters", "letting", "lettuce", "letup",
				"leucocyte", "leucotomy", "leukaemia", "leukemia", "leukocyte", "levee", "level", "leveler", "leveller",
				"lever", "leverage", "leveret", "leviathan", "levitate", "levity", "levodopa", "levy", "lewd",
				"lexical", "lexicographer", "lexicography", "lexicon", "lexis", "liability", "liable", "liaise",
				"liaison", "liana", "liar", "lib", "libation", "libel", "libellous", "libelous", "liberal",
				"liberalise", "liberalism", "liberality", "liberalize", "liberally", "liberate", "liberated",
				"liberation", "libertarian", "liberties", "libertine", "liberty", "libidinous", "libido", "libra",
				"librarian", "library", "librettist", "libretto", "lice", "licence", "licenced", "license", "licensed",
				"licensee", "licentiate", "licentious", "lichen", "licit", "lick", "licking", "licorice", "lid", "lido",
				"lie", "lieder", "lief", "liege", "lien", "lieu", "lieutenant", "life", "lifeblood", "lifeboat",
				"lifeguard", "lifeless", "lifelike", "lifeline", "lifelong", "lifer", "lifetime", "lift", "liftboy",
				"ligament", "ligature", "light", "lighten", "lighter", "lighterage", "lighthouse", "lighting",
				"lightly", "lightness", "lightning", "lights", "lightship", "lightweight", "ligneous", "lignite",
				"likable", "like", "likeable", "likelihood", "likely", "liken", "likeness", "likes", "likewise",
				"liking", "lilac", "lilliputian", "lilo", "lilt", "lily", "limb", "limber", "limbo", "lime", "limeade",
				"limejuice", "limekiln", "limelight", "limerick", "limestone", "limey", "limit", "limitation",
				"limited", "limiting", "limitless", "limn", "limousine", "limp", "limpet", "limpid", "limy", "linchpin",
				"linctus", "linden", "line", "lineage", "lineal", "lineament", "linear", "lineman", "linen", "lineout",
				"liner", "linertrain", "lines", "lineshooter", "linesman", "lineup", "ling", "linger", "lingerie",
				"lingering", "lingo", "lingual", "linguist", "linguistic", "linguistics", "liniment", "lining", "link",
				"linkage", "linkman", "links", "linkup", "linnet", "linocut", "linoleum", "linotype", "linseed", "lint",
				"lintel", "lion", "lionize", "lip", "lipid", "lipstick", "liquefaction", "liquefy", "liquescent",
				"liqueur", "liquid", "liquidate", "liquidation", "liquidator", "liquidity", "liquidize", "liquidizer",
				"liquor", "liquorice", "lira", "lisle", "lisp", "lissom", "lissome", "list", "listen", "listenable",
				"listener", "listless", "lists", "lit", "litany", "litchi", "liter", "literacy", "literal", "literally",
				"literary", "literate", "literati", "literature", "lithe", "lithium", "lithograph", "lithographic",
				"lithography", "litigant", "litigate", "litigation", "litigious", "litmus", "litotes", "litre",
				"litter", "litterateur", "litterbin", "litterlout", "little", "littoral", "liturgical", "liturgy",
				"livable", "live", "liveable", "livelihood", "livelong", "lively", "liven", "liver", "liveried",
				"liverish", "livery", "liveryman", "lives", "livestock", "livid", "living", "lizard", "llama", "load",
				"loaded", "loadstar", "loadstone", "loaf", "loafsugar", "loam", "loan", "loanword", "loath", "loathe",
				"loathing", "loathsome", "loaves", "lob", "lobby", "lobed", "lobotomy", "lobster", "lobsterpot",
				"local", "locale", "localise", "localism", "locality", "localize", "locally", "locate", "located",
				"location", "loch", "loci", };
	}

	static class KStemData5 {
		private KStemData5() {
		}

		static String[] data = { "lock", "locker", "locket", "lockjaw", "locknut", "lockout", "locks", "locksmith",
				"lockstitch", "lockup", "loco", "locomotion", "locomotive", "locum", "locus", "locust", "locution",
				"lode", "lodestar", "lodestone", "lodge", "lodgement", "lodger", "lodging", "lodgings", "lodgment",
				"loess", "loft", "lofted", "lofty", "log", "loganberry", "logarithm", "logarithmic", "logbook",
				"logger", "loggerheads", "loggia", "logic", "logical", "logically", "logician", "logistic", "logistics",
				"logjam", "logrolling", "loin", "loincloth", "loins", "loiter", "loll", "lollipop", "lollop", "lolly",
				"lone", "lonely", "loner", "lonesome", "long", "longboat", "longbow", "longevity", "longhaired",
				"longhand", "longheaded", "longhop", "longing", "longish", "longitude", "longitudinal", "longship",
				"longshoreman", "longsighted", "longstanding", "longstop", "longsuffering", "longueur", "longways",
				"longwearing", "longwinded", "longwise", "loo", "loofa", "loofah", "look", "looker", "lookout", "looks",
				"loom", "loon", "loony", "loop", "loophole", "loose", "loosebox", "loosen", "loot", "lop", "lope",
				"loppings", "loquacious", "loquat", "lord", "lordly", "lords", "lordship", "lore", "lorgnette", "lorn",
				"lorry", "lose", "loser", "loss", "lost", "lot", "loth", "lotion", "lottery", "lotto", "lotus", "loud",
				"loudhailer", "loudmouth", "loudspeaker", "lough", "lounge", "lounger", "lour", "louse", "lousy",
				"lout", "louver", "louvre", "lovable", "love", "loveable", "lovebird", "lovechild", "loveless",
				"lovelorn", "lovely", "lovemaking", "lover", "lovers", "lovesick", "lovey", "loving", "low", "lowborn",
				"lowbred", "lowbrow", "lowdown", "lower", "lowermost", "lowland", "lowlander", "lowly", "loyal",
				"loyalist", "loyalty", "lozenge", "lsd", "ltd", "lubber", "lubricant", "lubricate", "lubricator",
				"lubricious", "lucerne", "lucid", "luck", "luckless", "lucky", "lucrative", "lucre", "ludicrous",
				"ludo", "luff", "lug", "luggage", "lugger", "lughole", "lugsail", "lugubrious", "lugworm", "lukewarm",
				"lull", "lullaby", "lumbago", "lumbar", "lumber", "lumberjack", "lumberman", "lumberyard", "luminary",
				"luminous", "lumme", "lummox", "lummy", "lump", "lumpish", "lumpy", "lunacy", "lunar", "lunate",
				"lunatic", "lunch", "lunchtime", "lung", "lunge", "lungfish", "lungpower", "lupin", "lurch", "lure",
				"lurgy", "lurid", "lurk", "luscious", "lush", "lust", "luster", "lustful", "lustre", "lustrous",
				"lusty", "lutanist", "lute", "lutenist", "luv", "luxuriant", "luxuriate", "luxurious", "luxury",
				"lychee", "lychgate", "lye", "lymph", "lymphatic", "lynch", "lynx", "lyre", "lyrebird", "lyric",
				"lyrical", "lyricism", "lyricist", "lyrics", "mac", "macabre", "macadam", "macadamise", "macadamize",
				"macaroni", "macaroon", "macaw", "mace", "macerate", "mach", "machete", "machiavellian", "machination",
				"machine", "machinegun", "machinery", "machinist", "mackerel", "mackintosh", "macrobiotic", "macrocosm",
				"mad", "madam", "madame", "madcap", "madden", "maddening", "madder", "made", "madeira", "mademoiselle",
				"madhouse", "madly", "madman", "madness", "madonna", "madrigal", "maelstrom", "maenad", "maestro",
				"mafia", "mag", "magazine", "magenta", "maggot", "maggoty", "magi", "magic", "magical", "magician",
				"magisterial", "magistracy", "magistrate", "magma", "magnanimity", "magnanimous", "magnate", "magnesia",
				"magnesium", "magnet", "magnetic", "magnetise", "magnetism", "magnetize", "magneto", "magnificat",
				"magnification", "magnificent", "magnifier", "magnify", "magniloquent", "magnitude", "magnolia",
				"magnum", "magpie", "magus", "maharaja", "maharajah", "maharanee", "maharani", "mahatma", "mahlstick",
				"mahogany", "mahout", "maid", "maiden", "maidenhair", "maidenhead", "maidenhood", "maidenly",
				"maidservant", "mail", "mailbag", "mailbox", "maim", "main", "mainland", "mainline", "mainly",
				"mainmast", "mains", "mainsail", "mainspring", "mainstay", "mainstream", "maintain", "maintenance",
				"maisonette", "maisonnette", "maize", "majestic", "majesty", "majolica", "major", "majordomo",
				"majorette", "majority", "make", "maker", "makeshift", "making", "makings", "malachite", "maladjusted",
				"maladministration", "maladroit", "malady", "malaise", "malapropism", "malapropos", "malaria",
				"malarial", "malay", "malcontent", "malcontented", "male", "malediction", "malefactor", "maleficent",
				"malevolent", "malfeasance", "malformation", "malformed", "malfunction", "malice", "malicious",
				"malign", "malignancy", "malignant", "malignity", "malinger", "mall", "mallard", "malleable", "mallet",
				"mallow", "malmsey", "malnutrition", "malodorous", "malpractice", "malt", "malthusian", "maltreat",
				"maltster", "mama", "mamba", "mambo", "mamma", "mammal", "mammary", "mammon", "mammoth", "mammy", "man",
				"manacle", "manage", "manageable", "management", "manager", "manageress", "managerial", "manatee",
				"mandarin", "mandate", "mandatory", "mandible", "mandolin", "mandrake", "mandrill", "maneuver",
				"maneuverable", "manful", "manganese", "mange", "manger", "mangle", "mango", "mangosteen", "mangrove",
				"mangy", "manhandle", "manhole", "manhood", "manhour", "mania", "maniac", "maniacal", "manic",
				"manicure", "manicurist", "manifest", "manifestation", "manifesto", "manifold", "manikin", "manila",
				"manilla", "manipulate", "manipulation", "mankind", "manly", "manna", "manned", "mannequin", "manner",
				"mannered", "mannerism", "mannerly", "manners", "mannikin", "mannish", "manoeuverable", "manoeuvre",
				"manometer", "manor", "manorial", "manpower", "mansard", "manse", "manservant", "mansion", "mansions",
				"manslaughter", "mantelpiece", "mantelshelf", "mantilla", "mantis", "mantle", "mantrap", "manual",
				"manufacture", "manufacturer", "manumit", "manure", "manuscript", "manx", "many", "maoism", "maori",
				"map", "maple", "mapping", "maquis", "mar", "marabou", "marabout", "maraschino", "marathon", "maraud",
				"marble", "marbled", "marbles", "marc", "marcasite", "march", "marchioness", "margarine", "margin",
				"marginal", "marguerite", "marigold", "marihuana", "marijuana", "marimba", "marina", "marinade",
				"marinate", "marine", "mariner", "marionette", "marital", "maritime", "marjoram", "mark", "markdown",
				"marked", "marker", "market", "marketeer", "marketer", "marketing", "marketplace", "marking",
				"marksman", "marksmanship", "markup", "marl", "marlinespike", "marmalade", "marmoreal", "marmoset",
				"marmot", "marocain", "maroon", "marquee", "marquess", "marquetry", "marquis", "marriage",
				"marriageable", "married", "marrow", "marrowbone", "marrowfat", "marry", "mars", "marsala",
				"marseillaise", "marsh", "marshal", "marshmallow", "marshy", "marsupial", "mart", "marten", "martial",
				"martian", "martin", "martinet", "martini", "martinmas", "martyr", "martyrdom", "marvel", "marvellous",
				"marvelous", "marxism", "marzipan", "mascara", "mascot", "masculine", "masculinity", "maser", "mash",
				"mashie", "mask", "masked", "masochism", "mason", "masonic", "masonry", "masque", "masquerade", "mass",
				"massacre", "massage", "masses", "masseur", "massif", "massive", "massy", "mast", "mastectomy",
				"master", "masterful", "masterly", "mastermind", "masterpiece", "mastership", "masterstroke", "mastery",
				"masthead", "mastic", "masticate", "mastiff", "mastitis", "mastodon", "mastoid", "mastoiditis",
				"masturbate", "mat", "matador", "match", "matchbox", "matching", "matchless", "matchlock", "matchmaker",
				"matchstick", "matchwood", "mate", "material", "materialise", "materialism", "materialist",
				"materialize", "maternal", "maternity", "matey", "mathematician", "mathematics", "matins", "matriarch",
				"matriarchy", "matricide", "matriculate", "matrimony", "matrix", "matron", "matronly", "matt", "matter",
				"matting", "mattins", "mattock", "mattress", "maturation", "mature", "maturity", "maudlin", "maul",
				"maulstick", "maunder", "mausoleum", "mauve", "maverick", "maw", "mawkish", "maxi", "maxim", "maximal",
				"maximise", "maximize", "maximum", "may", "maybe", "maybeetle", "mayday", "mayfly", "mayhem",
				"mayonnaise", "mayor", "mayoralty", "mayoress", "maypole", "mayst", "maze", "mazed", "mazurka",
				"mccarthyism", "mead", "meadow", "meadowsweet", "meager", "meagre", "meal", "mealie", "mealtime",
				"mealy", "mealybug", "mean", "meander", "meanderings", "meaning", "meaningful", "meaningless", "means",
				"meant", "meantime", "meanwhile", "measles", "measly", "measurable", "measure", "measured",
				"measureless", "measurement", "meat", "meatball", "meaty", "mecca", "mechanic", "mechanical",
				"mechanics", "mechanise", "mechanism", "mechanistic", "mechanize", "medal", "medalist", "medallion",
				"medallist", "meddle", "meddlesome", "media", "mediaeval", "medial", "median", "mediate", "medic",
				"medical", "medicament", "medicare", "medicate", "medication", "medicinal", "medicine", "medico",
				"medieval", "mediocre", "mediocrity", "meditate", "meditation", "meditative", "mediterranean", "medium",
				"medlar", "medley", "meed", "meek", "meerschaum", "meet", "meeting", "meetinghouse", "megadeath",
				"megahertz", "megalith", "megalithic", "megalomania", "megalomaniac", "megaphone", "megaton", "megrim",
				"meiosis", "melancholia", "melancholic", "melancholy", "meld", "melee", "meliorate", "meliorism",
				"mellifluous", "mellow", "melodic", "melodious", "melodrama", "melodramatic", "melody", "melon", "melt",
				"melting", "member", "membership", "membrane", "membranous", "memento", "memo", "memoir", "memoirs",
				"memorabilia", "memorable", "memorandum", "memorial", "memorise", "memorize", "memory", "memsahib",
				"men", "menace", "menagerie", "mend", "mendacious", "mendacity", "mendelian", "mendicant", "mending",
				"menfolk", "menial", "meningitis", "meniscus", "menopause", "menses", "menstrual", "menstruate",
				"mensurable", "mensuration", "mental", "mentality", "menthol", "mentholated", "mention", "mentor",
				"menu", "meow", "mephistopheles", "mercantile", "mercenary", "mercer", "mercerise", "mercerize",
				"merchandise", "merchant", "merchantman", "merciful", "merciless", "mercurial", "mercury", "mercy",
				"mere", "merely", "meretricious", "merge", "merger", "meridian", "meridional", "meringue", "merino",
				"merit", "meritocracy", "meritorious", "mermaid", "merman", "merriment", "merry", "merrymaking", "mesa",
				"mescalin", "mescaline", "mesdames", "mesdemoiselles", "meseems", "mesh", "mesmeric", "mesmerise",
				"mesmerism", "mesmerist", "mesmerize", "mess", "message", "messenger", "messiah", "messianic",
				"messieurs", "messmate", "messrs", "messuage", "messy", "mestizo", "met", "metabolic", "metabolise",
				"metabolism", "metabolize", "metacarpal", "metal", "metalanguage", "metallic", "metallurgist",
				"metallurgy", "metalwork", "metamorphose", "metamorphosis", "metaphor", "metaphorical", "metaphysics",
				"metatarsal", "mete", "metempsychosis", "meteor", "meteoric", "meteorite", "meteoroid", "meteorologist",
				"meteorology", "meter", "methane", "methinks", "method", "methodical", "methodism", "methodology",
				"meths", "methuselah", "meticulous", "metre", "metric", "metrical", "metrication", "metricise",
				"metricize", "metro", "metronome", "metropolis", "metropolitan", "mettle", "mettlesome", "mew", "mews",
				"mezzanine", "mezzo", "mezzotint", "miaow", "miasma", "mica", "mice", "michaelmas", "mick", "mickey",
				"microbe", "microbiologist", "microbiology", "microcosm", "microelectronics", "microfiche", "microfilm",
				"micromesh", "micrometer", "micron", "microorganism", "microphone", "microscope", "microscopic",
				"microsecond", "microwave", "mid", "midair", "midcourse", "midday", "midden", "middle", "middlebrow",
				"middleman", "middleweight", "middling", "midge", "midget", "midi", "midland", "midlands", "midmost",
				"midnight", "midpoint", "midriff", "midshipman", "midships", "midst", "midsummer", "midway", "midweek",
				"midwest", "midwicket", "midwife", "midwifery", "mien", "miffed", "might", "mightily", "mighty",
				"mignonette", "migraine", "migrant", "migrate", "migration", "migratory", "mikado", "mike", "milady",
				"mild", "mildew", "mildly", "mile", "mileage", "mileometer", "miler", "milestone", "milieu",
				"militancy", "militant", "militarise", "militarism", "militarize", "military", "militate", "militia",
				"militiaman", "milk", "milker", "milkmaid", "milkman", "milksop", "milkweed", "milky", "mill",
				"millboard", "milldam", "millenarian", "millenium", "millepede", "miller", "millet", "millibar",
				"milligram", "milligramme", "milliliter", "millilitre", "millimeter", "millimetre", "milliner",
				"millinery", "million", "millionaire", "millipede", "millpond", "millrace", "millstone", "millwheel",
				"millwright", "milometer", "milord", "milt", "mime", "mimeograph", "mimetic", "mimic", "mimicry",
				"mimosa", "min", "minaret", "minatory", "mince", "mincemeat", "mincer", "mincingly", "mind", "minded",
				"mindful", "mindless", "mine", "minefield", "minelayer", "miner", "mineral", "mineralogist",
				"mineralogy", "minestrone", "minesweeper", "mingle", "mingy", "mini", "miniature", "miniaturist",
				"minibus", "minim", "minimal", "minimise", "minimize", "minimum", "mining", "minion", "minister",
				"ministerial", "ministrant", "ministration", "ministry", "miniver", "mink", "minnow", "minor",
				"minority", "minotaur", "minster", "minstrel", "minstrelsy", "mint", "minuet", "minus", "minuscule",
				"minute", "minutely", "minuteman", "minutes", "minutia", "minx", "miracle", "miraculous", "mirage",
				"mire", "mirror", "mirth", "miry", "misadventure", "misadvise", "misalliance", "misanthrope",
				"misanthropy", "misapplication", "misapply", "misapprehend", "misapprehension", "misappropriate",
				"misbegotten", "misbehave", "misbehaved", "misbehavior", "misbehaviour", "miscalculate", "miscall",
				"miscarry", "miscast", "miscegenation", "miscellaneous", "miscellany", "mischance", "mischief",
				"mischievous", "misconceive", "misconception", "misconduct", "misconstruction", "misconstrue",
				"miscount", "miscreant", "miscue", "misdate", "misdeal", "misdeed", "misdemeanor", "misdemeanour",
				"misdirect", "misdoing", "miser", "miserable", "miserably", "miserly", "misery", "misfire", "misfit",
				"misfortune", "misgiving", "misgovern", "misguide", "misguided", "mishandle", "mishap", "mishear",
				"mishit", "mishmash", "misinform", "misinterpret", "misjudge", "misjudgement", "misjudgment", "mislay",
				"mislead", "mismanage", "mismatch", "misname", "misnomer", "misogynist", "misogyny", "misplace",
				"misprint", "mispronounce", "mispronunciation", "misquote", "misread", "misreport", "misrepresent",
				"misrule", "miss", "missal", "misshapen", "missile", "missing", "mission", "missionary", "missis",
				"missive", "misspell", "misspend", "misstate", "misstatement", "missus", "missy", "mist", "mistake",
				"mistaken", "mister", "mistime", "mistletoe", "mistral", "mistranslate", "mistress", "mistrial",
				"mistrust", "mistrustful", "mists", "misty", "misunderstand", "misunderstanding", "misuse", "mite",
				"miter", "mitigate", "mitosis", "mitre", "mitt", "mitten", "mix", "mixed", "mixer", "mixture", "mizen",
				"mizzen", "mizzenmast", "mizzle", "mnemonic", "mnemonics", "moa", "moan", "moat", "moated", "mob",
				"mobile", "mobilisation", "mobilise", "mobility", "mobilization", "mobilize", "mobster", "moccasin",
				"mocha", "mock", "mockers", "mockery", "mockingbird", "modal", "mode", "model", "moderate",
				"moderately", "moderation", "moderations", "moderato", "moderator", "modern", "modernise", "modernism",
				"modernistic", "modernity", "modernize", "modest", "modesty", "modicum", "modification", "modifier",
				"modify", "modish", "mods", "modular", "modulate", "modulation", "module", "moggy", "mogul", "moh",
				"mohair", "mohammedan", "mohammedanism", "moiety", "moist", "moisten", "moisture", "moisturise",
				"moisturize", "moke", "molar", "molasses", "mold", "molder", "molding", "moldy", "mole", "molecular",
				"molecule", "molehill", "moleskin", "molest", "moll", "mollify", "mollusc", "mollusk", "mollycoddle",
				"molt", "molten", "molto", "molybdenum", "mom", "moment", "momentarily", "momentary", "momentous",
				"moments", "momentum", "momma", "mommy", "monarch", "monarchic", "monarchism", "monarchist", "monarchy",
				"monastery", "monastic", "monasticism", "monaural", "monday", "monetary", "money", "moneybags",
				"moneybox", "moneychanger", "moneyed", "moneylender", "moneymaker", "moneys", "monger", "mongol",
				"mongolism", "mongoose", "mongrel", "monies", "monitor", "monk", "monkey", "mono", "monochrome",
				"monocle", "monogamous", "monogamy", "monogram", "monograph", "monolith", "monolithic", "monolog",
				"monologue", "monomania", "monomaniac", "mononucleosis", "monophonic", "monophthong", "monoplane",
				"monopolise", "monopolist", "monopolize", "monopoly", "monorail", "monosyllabic", "monosyllable",
				"monotheism", "monotone", "monotonous", "monotony", "monotype", "monoxide", "monsieur", "monsignor",
				"monsoon", "monster", "monstrance", "monstrosity", "monstrous", "montage", "month", "monthly",
				"monument", "monumental", "monumentally", "moo", "mooch", "moocow", "mood", "moody", "moon", "moonbeam",
				"mooncalf", "moonlight", "moonlit", "moonshine", "moonstone", "moonstruck", "moony", "moor", "moorhen",
				"moorings", "moorish", "moorland", "moose", "moot", "mop", "mope", "moped", "moppet", "moquette",
				"moraine", "moral", "morale", "moralise", "moralist", "moralistic", "morality", "moralize", "morally",
				"morals", "morass", "moratorium", "morbid", "morbidity", "mordant", "more", "morello", "moreover",
				"mores", "moresque", "morganatic", "morgue", "moribund", "mormon", "mormonism", "morn", "morning",
				"mornings", "morocco", "moron", "moronic", "morose", "morpheme", "morphemics", "morpheus", "morphine",
				"morphology", "morrow", "morsel", "mortal", "mortality", "mortally", "mortar", "mortarboard",
				"mortgage", "mortgagee", "mortgagor", "mortice", "mortician", "mortification", "mortify", "mortise",
				"mortuary", "mosaic", "moselle", "mosey", "moslem", "mosque", "mosquito", "moss", "mossy", "most",
				"mostly", "mote", "motel", "motet", "moth", "mothball", "mothballs", "mother", "motherhood", "motherly",
				"mothproof", "motif", "motion", "motionless", "motions", "motivate", "motivation", "motive", "motley",
				"motocross", "motor", "motorbike", "motorboat", "motorcade", "motorcar", "motorcycle", "motorcyclist",
				"motoring", "motorise", "motorist", "motorize", "motorman", "motorway", "mottled", "motto", "mould",
				"moulder", "moulding", "mouldy", "moult", "mound", "mount", "mountain", "mountaineer", "mountaineering",
				"mountainous", "mountainside", "mountaintop", "mountebank", "mountie", "mourn", "mourner", "mournful",
				"mourning", "mouse", "mouser", "mousetrap", "moussaka", "mousse", "moustache", "mousy", "mouth",
				"mouthful", "mouthorgan", "mouthpiece", "mouthwash", "movable", "move", "moveable", "movement",
				"movements", "mover", "movie", "movies", "moving", "mow", "mower", "mpg", "mph", "mra", "mrs", "msc",
				"much", "muchness", "mucilage", "muck", "muckheap", "muckrake", "mucky", "mucous", "mucus", "mud",
				"muddle", "muddy", "mudflat", "mudguard", "mudpack", "mudslinger", "muesli", "muezzin", "muff",
				"muffin", "muffle", "muffler", "mufti", "mug", "mugger", "muggins", "muggy", "mugwump", "muhammadan",
				"muhammadanism", "mulatto", "mulberry", "mulch", "mulct", "mule", "muleteer", "mulish", "mull",
				"mullah", "mullet", "mulligatawny", "mullion", "mullioned", "multifarious", "multiform", "multilateral",
				"multilingual", "multimillionaire", "multiple", "multiplex", "multiplication", "multiplicity",
				"multiply", "multiracial", "multistorey", "multitude", "multitudinous", "mum", "mumble", "mummer",
				"mummery", "mummify", "mumming", "mummy", "mumps", "munch", "mundane", "municipal", "municipality",
				"munificence", "munificent", "muniments", "munition", "munitions", "mural", "murder", "murderous",
				"murk", "murky", "murmur", "murphy", "murrain", "muscatel", "muscle", "muscled", "muscleman",
				"muscovite", "muscular", "muse", "museum", "mush", "mushroom", "mushy", "music", "musical", "musically",
				"musician", "musicianship", "musk", "musket", "musketeer", "musketry", "muskmelon", "muskrat", "musky",
				"muslim", "muslin", "musquash", "muss", "mussel", "must", "mustache", "mustachio", "mustang", "mustard",
				"muster", "musty", "mutable", "mutant", "mutation", "mute", "muted", "mutilate", "mutilation",
				"mutineer", "mutinous", "mutiny", "mutt", "mutter", "mutton", "muttonchops", "mutual", "mutuality",
				"muzak", "muzzle", "muzzy", "mycology", "myelitis", "myna", "mynah", "myopia", "myriad", "myrrh",
				"myrtle", "myself", "mysterious", "mystery", "mystic", "mystical", "mysticism", "mystification",
				"mystify", "mystique", "myth", "mythical", "mythological", "mythologist", "mythology", "myxomatosis",
				"nab", "nabob", "nacelle", "nacre", "nadir", "nag", "naiad", "nail", "nailbrush", "naive", "naivete",
				"naivety", "naked", "name", "namedrop", "nameless", "namely", "nameplate", "namesake", "nanny", "nap",
				"napalm", "naphtha", "naphthalene", "napkin", "nappy", "narc", "narcissism", "narcissus", "narcotic",
				"nark", "narky", "narrate", "narration", "narrative", "narrator", "narrow", "narrowly", "narrows",
				"narwhal", "nasal", "nasalise", "nasalize", "nascent", "nasturtium", "nasty", "natal", "nation",
				"national", "nationalise", "nationalism", "nationalist", "nationalistic", "nationality", "nationalize",
				"nationwide", "native", "nativity", "nato", "natter", "natty", "natural", "naturalise", "naturalism",
				"naturalist", "naturalistic", "naturalize", "naturally", "naturalness", "nature", "naturism",
				"naturopath", "naught", "naughty", "nausea", "nauseate", "nauseous", "nautch", "nautical", "nautilus",
				"naval", "nave", "navel", "navigable", "navigate", "navigation", "navigator", "navvy", "navy", "nay",
				"nazi", "nco", "neanderthal", "neapolitan", "near", "nearby", "nearly", "nearside", "nearsighted",
				"neat", "nebula", "nebular", "nebulous", "necessaries", "necessarily", "necessary", "necessitate",
				"necessitous", "necessity", "neck", "neckband", "neckerchief", "necklace", "necklet", "neckline",
				"necktie", "neckwear", "necromancer", "necromancy", "necrophilia", "necrophiliac", "necropolis",
				"nectar", "nectarine", "need", "needful", "needle", "needless", "needlessly", "needlewoman",
				"needlework", "needs", "needy", "nefarious", "negate", "negative", "neglect", "neglectful", "negligee",
				"negligence", "negligent", "negligible", "negotiable", "negotiate", "negotiation", "negress", "negro",
				"negus", "neigh", "neighbor", "neighborhood", "neighboring", "neighborly", "neighbour", "neighbourhood",
				"neighbouring", "neighbourly", "neither", "nelson", "nemesis", "neoclassical", "neocolonialism",
				"neolithic", "neologism", "neon", "neonate", "neophyte", "neoplasm", "nephew", "nephritis", "nepotism",
				"neptune", "nereid", "nerve", "nerveless", "nerves", "nervous", "nervy", "ness", "nest", "nesting",
				"nestle", "nestling", "nestor", "net", "netball", "nether", "nethermost", "nets", "nett", "netting",
				"nettle", "network", "neural", "neuralgia", "neurasthenia", "neurasthenic", "neuritis", "neurologist",
				"neurology", "neurosis", "neurotic", "neuter", "neutral", "neutralise", "neutrality", "neutralize",
				"neutralizer", "neutron", "never", "nevermore", "nevertheless", "new", "newborn", "newcomer", "newel",
				"newfangled", "newfoundland", "newly", "newlywed", "newmarket", "news", "newsagent", "newsboy",
				"newscast", "newscaster", "newsletter", "newsmonger", "newspaper", "newsprint", "newsreel", "newsroom",
				"newssheet", "newsstand", "newsvendor", "newsworthy", "newsy", "newt", "newtonian", "next", "nexus",
				"nhs", "niacin", "nib", "nibble", "niblick", "nibs", "nice", "nicely", "nicety", "niche", "nick",
				"nickel", "nicker", "nicknack", "nickname", "nicotine", "niece", "niff", "nifty", "niggard",
				"niggardly", "nigger", "niggle", "niggling", "nigh", "night", "nightcap", "nightclothes", "nightclub",
				"nightdress", "nightfall", "nighthawk", "nightingale", "nightjar", "nightlife", "nightlight",
				"nightline", "nightlong", "nightly", "nightmare", "nights", "nightshade", "nightshirt", "nightstick",
				"nighttime", "nihilism", "nilotic", "nimble", "nimbus", "nimrod", "nincompoop", "nine", "ninepin",
				"ninepins", "nines", "nineteen", "ninety", "ninny", "ninth", "nip", "nipper", "nippers", "nipping",
				"nipple", "nippy", "nirvana", "nisi", "nit", "niter", "nitpick", "nitpicking", "nitrate", "nitre",
				"nitric", "nitrochalk", "nitrogen", "nitroglycerin", "nitroglycerine", "nitrous", "nitwit", "nix",
				"nob", "nobble", "nobility", "noble", "nobleman", "nobly", "nobody", "nocturnal", "nocturne", "nod",
				"nodal", "noddle", "nodular", "nodule", "noel", "noes", "nog", "noggin", "nohow", "noise", "noisome",
				"noisy", "nomad", "nomadic", "nomenclature", "nominal", "nominate", "nomination", "nominative",
				"nominee", "nonage", "nonagenarian", "nonaggression", "nonaligned", "nonalignment", "nonassertive",
				"nonce", "nonchalance", "nonchalant", "noncombatant", "noncommittal", "nonconductor", "nonconformist",
				"nonconformity", "noncontributory", "nondescript", "none", "nonentity", "nonesuch", "nonetheless",
				"nonfiction", "nonflammable", "nonintervention", "nonobservance", "nonpareil", "nonpayment", "nonplus",
				"nonproliferation", "nonresident", "nonrestrictive", "nonsense", "nonsensical", "nonskid", "nonsmoker",
				"nonstandard", "nonstarter", "nonstick", "nonstop", "nonunion", "nonverbal", "nonviolence",
				"nonviolent", "nonwhite", "noodle", "nook", "noon", "noonday", "noose", "nope", "nor", "nordic", "norm",
				"normal", "normalise", "normality", "normalize", "normally", "norman", "normative", "north",
				"northbound", "northeast", "northeaster", "northeasterly", "northeastern", "northeastward",
				"northeastwards", "northerly", "northern", "northerner", "northernmost", "northward", "northwards",
				"northwest", "northwester", "northwesterly", "northwestern", "northwestward", "northwestwards", "nos",
				"nose", "nosebag", "nosebleed", "nosecone", "nosedive", "nosegay", "nosey", "nosh", "nostalgia",
				"nostril", "nostrum", "nosy", "not", "notability", "notable", "notably", "notarise", "notarize",
				"notary", "notation", "notch", "note", "notebook", "notecase", "noted", "notepaper", "noteworthy",
				"nothing", "nothingness", "notice", "noticeable", "notifiable", "notification", "notify", "notion",
				"notional", "notions", "notoriety", "notorious", "notwithstanding", "nougat", "nought", "noun",
				"nourish", "nourishment", "nous", "nova", "novel", "novelette", "novelettish", "novelist", "novella",
				"novelty", "november", "novice", "noviciate", "novitiate", "novocaine", "now", "nowadays", "nowhere",
				"nowise", "noxious", "nozzle", "nth", "nuance", "nub", "nubile", "nuclear", "nucleus", "nude", "nudge",
				"nudism", "nudity", "nugatory", "nugget", "nuisance", "null", "nullah", "nullify", "nullity", "numb",
				"number", "numberless", "numberplate", "numbers", "numbly", "numbskull", "numeracy", "numeral",
				"numerate", "numeration", "numerator", "numerical", "numerology", "numerous", "numinous", "numismatic",
				"numismatics", "numskull", "nun", "nuncio", "nunnery", "nuptial", "nuptials", "nurse", "nurseling",
				"nursemaid", "nursery", "nurseryman", "nursing", "nursling", "nurture", "nut", "nutcase", "nutcracker",
				"nuthouse", "nutmeg", "nutria", "nutrient", "nutriment", "nutrition", "nutritious", "nutritive", "nuts",
				"nutshell", "nutty", "nuzzle", "nylon", "nylons", "nymph", "nymphet", "nymphomania", "nymphomaniac",
				"oaf", "oak", "oaken", "oakum", "oap", "oar", "oarlock", "oarsman", "oarsmanship", "oasis", "oat",
				"oatcake", "oath", "oatmeal", "oats", "obbligato", "obdurate", "obeah", "obedient", "obeisance",
				"obelisk", "obese", "obey", "obfuscate", "obituary", "object", "objection", "objectionable",
				"objective", "objector", "oblation", "obligate", "obligation", "obligatory", "oblige", "obliging",
				"oblique", "obliterate", "oblivion", "oblivious", "oblong", "obloquy", "obnoxious", "oboe", "oboist",
				"obscene", "obscenity", "obscurantism", "obscure", "obscurity", "obsequies", "obsequious", "observable",
				"observance", "observant", "observation", "observations", "observatory", "observe", "observer",
				"observing", "obsess", "obsession", "obsessional", "obsessive", "obsidian", "obsolescent", "obsolete",
				"obstacle", "obstetrician", "obstetrics", "obstinate", "obstreperous", "obstruct", "obstruction",
				"obstructionism", "obstructive", "obtain", "obtainable", "obtrude", "obtrusive", "obtuse", "obverse",
				"obviate", "obvious", "obviously", "ocarina", "occasion", "occasional", "occident", "occidental",
				"occult", "occupancy", "occupant", "occupation", "occupational", "occupier", "occupy", "occur",
				"occurrence", "ocean", "oceangoing", "oceanography", "ocelot", "ocher", "ochre", "octagon", "octane",
				"octave", "octavo", "octet", "october", "octogenarian", "octopus", "octosyllabic", "ocular", "oculist",
				"odalisque", "odd", "oddball", "oddity", "oddly", "oddment", "odds", "ode", "odious", "odium", "odor",
				"odoriferous", "odorous", "odour", "odyssey", "oecumenical", "oecumenicalism", "oesophagus",
				"oestrogen", "off", "offal", "offbeat", "offence", "offend", "offender", "offense", "offensive",
				"offer", "offering", "offertory", "offhand", "office", "officeholder", "officer", "offices", "official",
				"officialdom", "officialese", "officially", "officiate", "officious", "offing", "offish", "offprint",
				"offset", "offshoot", "offshore", "offside", "offspring", "offstage", "oft", "often", "ogle", "ogre",
				"ohm", "oho", "oil", "oilcake", "oilcan", "oilcloth", "oiled", "oilfield", "oilman", "oilrig", "oils",
				"oilskin", "oilskins", "oily", "oink", "ointment", "okapi", "okay", "okra", "old", "olden", "oldish",
				"oldster", "oleaginous", "oleander", "oleograph", "olfactory", "oligarch", "oligarchy", "olive",
				"olympiad", "olympian", "olympic", "ombudsman", "omega", "omelet", "omelette", "omen", "ominous",
				"omission", "omit", "omnibus", "omnipotent", "omnipresent", "omniscient", "omnivorous", "once",
				"oncoming", "one", "onerous", "oneself", "onetime", "ongoing", "onion", "onlooker", "only",
				"onomatopoeia", "onrush", "onset", "onshore", "onside", "onslaught", "onto", "ontology", "onus",
				"onward", "onwards", "onyx", "oodles", "oof", "oomph", "oops", "ooze", "opacity", "opal", "opalescent",
				"opaque", "ope", "open", "opencast", "opener", "openhearted", "opening", "openly", "openwork", "opera",
				"operable", "operate", "operation", "operational", "operative", "operator", "operetta", "ophthalmia",
				"ophthalmic", "ophthalmology", "ophthalmoscope", "opiate", "opine", "opinion", "opinionated", "opium",
				"opossum", "opponent", "opportune", "opportunism", "opportunity", "oppose", "opposite", "opposition",
				"oppress", "oppression", "oppressive", "oppressor", "opprobrious", "opprobrium", "ops", "opt",
				"optative", "optic", "optical", "optician", "optics", "optimism", "optimum", "option", "optional",
				"opulence", "opulent", "opus", "oracle", "oracular", "oral", "orange", "orangeade", "orangeman",
				"orangutang", "oration", "orator", "oratorical", "oratorio", "oratory", "orb", "orbit", "orchard",
				"orchestra", "orchestral", "orchestrate", "orchid", "ordain", "ordeal", "order", "ordered", "orderly",
				"orders", "ordinal", "ordinance", "ordinand", "ordinarily", "ordinary", "ordinate", "ordination",
				"ordnance", "ordure", "ore", "oregano", "organ", "organdie", "organdy", "organic", "organisation",
				"organise", "organised", "organism", "organist", "organization", "organize", "organized", "orgasm",
				"orgiastic", "orgy", "orient", "oriental", "orientalist", "orientate", "orientation", "orifice",
				"origin", "original", "originality", "originally", "originate", "oriole", "orison", "orlon", "ormolu",
				"ornament", "ornamental", "ornamentation", "ornate", "ornery", "ornithology", "orotund", "orphan",
				"orphanage", "orrery", "orrisroot", "orthodontic", "orthodontics", "orthodox", "orthodoxy",
				"orthography", "orthopaedic", "orthopaedics", "orthopedic", "orthopedics", "ortolan", "oryx", "oscar",
				"oscillate", "oscillation", "oscillator", "oscillograph", "oscilloscope", "osculation", "osier",
				"osmosis", "osprey", "osseous", "ossification", "ossify", "ostensible", "ostentation", "osteoarthritis",
				"osteopath", "osteopathy", "ostler", "ostracise", "ostracize", "ostrich", "other", "otherwise",
				"otherworldly", "otiose", "otter", "ottoman", "oubliette", "ouch", "ought", "ounce", "our", "ours",
				"ourselves", "ousel", "oust", "out", "outback", "outbalance", "outbid", "outbound", "outbrave",
				"outbreak", "outbuilding", "outburst", "outcast", "outcaste", "outclass", "outcome", "outcrop",
				"outcry", "outdated", "outdistance", "outdo", "outdoor", "outdoors", "outer", "outermost", "outface",
				"outfall", "outfield", "outfight", "outfit", "outflank", "outflow", "outfox", "outgeneral", "outgoing",
				"outgoings", "outgrow", "outgrowth", "outhouse", "outing", "outlandish", "outlast", "outlaw", "outlay",
				"outlet", "outline", "outlive", "outlook", "outlying", "outmaneuver", "outmanoeuvre", "outmarch",
				"outmatch", "outmoded", "outmost", "outnumber", "outpatient", "outplay", "outpoint", "outpost",
				"outpourings", "output", "outrage", "outrageous", "outrange", "outrank", "outride", "outrider",
				"outrigger", "outright", "outrival", "outrun", "outsell", "outset", "outshine", "outside", "outsider",
				"outsize", "outskirts", "outsmart", "outspoken", "outspread", "outstanding", "outstay", "outstretched",
				"outstrip", "outtalk", "outvote", "outward", "outwardly", "outwards", "outwear", "outweigh", "outwit",
				"outwork", "outworn", "ouzel", "ouzo", "ova", "oval", "ovarian", "ovary", "ovation", "oven", "ovenware",
				"over", "overact", "overage", "overall", "overalls", "overarch", "overarm", "overawe", "overbalance",
				"overbear", "overbearing", "overbid", "overblown", "overboard", "overburden", "overcall",
				"overcapitalise", "overcapitalize", "overcast", "overcharge", "overcloud", "overcoat", "overcome",
				"overcompensate", "overcrop", "overcrowd", "overdevelop", "overdo", "overdone", "overdose", "overdraft",
				"overdraw", "overdrawn", "overdress", "overdrive", "overdue", "overestimate", "overexpose", "overflow",
				"overfly", "overgrown", "overgrowth", "overhand", "overhang", "overhaul", "overhead", "overheads",
				"overhear", "overjoyed", "overkill", "overland", "overlap", "overlay", "overleaf", "overleap",
				"overload", "overlong", "overlook", "overlord", "overly", "overman", "overmaster", "overmuch",
				"overnight", "overpass", "overpay", "overplay", "overpopulated", "overpopulation", "overpower",
				"overpowering", "overprint", "overrate", "overreach", "override", "overriding", "overrule", "overrun",
				"overseas", "oversee", "overseer", "oversell", "oversexed", "overshadow", "overshoe", "overshoot",
				"overside", "oversight", "oversimplify", "oversleep", "overspill", "overstate", "overstatement",
				"overstay", "oversteer", "overstep", "overstock", "overstrung", "overstuffed", "oversubscribed",
				"overt", "overtake", "overtax", "overthrow", "overtime", "overtone", "overtones", "overtop",
				"overtrump", "overture", "overtures", "overturn", "overweening", "overweight", "overwhelm",
				"overwhelming", "overwork", "overwrought", "oviduct", "oviparous", "ovoid", "ovulate", "ovum", "owe",
				"owl", "owlet", "owlish", "own", "owner", "ownership", "oxbridge", "oxcart", "oxeye", "oxide",
				"oxidise", "oxidize", "oxon", "oxonian", "oxtail", "oxyacetylene", "oxygen", "oxygenate", "oyez",
				"oyster", "oystercatcher", "ozone", "pabulum", "pace", "pacemaker", "pacesetter", "pachyderm",
				"pacific", "pacifier", "pacifism", "pacifist", "pacify", "pack", "package", "packed", "packer",
				"packet", "packing", "packsaddle", "pact", "pad", "padding", "paddle", "paddock", "paddy", "padlock",
				"padre", "paean", "paederast", "paederasty", "paediatrician", "paediatrics", "paella", "paeony",
				"pagan", "paganism", "page", "pageant", "pageantry", "pagination", "pagoda", "paid", "pail",
				"paillasse", "pain", "pained", "painful", "painkiller", "painless", "pains", "painstaking", "paint",
				"paintbrush", "painter", "painting", "paints", "paintwork", "pair", "paisley", "pajama", "pajamas",
				"pal", "palace", "paladin", "palais", "palakeen", "palanquin", "palatable", "palatal", "palatalize",
				"palate", "palatial", "palatinate", "palaver", "pale", "paleface", "paleography", "paleolithic",
				"paleontology", "palette", "palfrey", "palimpsest", "palindrome", "paling", "palings", "palisade",
				"palish", "pall", "palladian", "pallbearer", "pallet", "palliasse", "palliate", "palliation",
				"palliative", "pallid", "pallor", "pally", "palm", "palmer", "palmetto", "palmist", "palmistry",
				"palmy", "palomino", "palpable", "palpate", "palpitate", "palpitation", "palsied", "palsy", "palter",
				"paltry", "pampas", "pamper", "pamphlet", "pamphleteer", "pan", "panacea", "panache", "panama",
				"panatela", "panatella", "pancake", "panchromatic", "pancreas", "panda", "pandemic", "pandemonium",
				"pander", "pandit", "panegyric", "panel", "paneling", "panelist", "panelling", "panellist", "pang",
				"panhandle", "panic", "panicky", "panjabi", "panjandrum", "pannier", "pannikin", "panoplied", "panoply",
				"panorama", "panpipes", "pansy", "pant", "pantaloon", "pantaloons", "pantechnicon", "pantheism",
				"pantheon", "panther", "panties", "pantile", "panto", "pantograph", "pantomime", "pantry", "pants",
				"panty", "panzer", "pap", "papa", "papacy", "papadum", "papal", "papaya", "paper", "paperback",
				"paperboy", "paperhanger", "papers", "paperweight", "paperwork", "papery", "papist", "papoose", "pappy",
				"paprika", "papyrus", "par", "parable", "parabola", "parachute", "parachutist", "paraclete", "parade",
				"paradigm", "paradigmatic", "paradise", "paradisiacal", "paradox", "paraffin", "paragon", "paragraph",
				"parakeet", "parallel", "parallelism", "parallelogram", "paralyse", "paralysis", "paralytic",
				"paralyze", "paramilitary", "paramount", "paramountcy", "paramour", "paranoia", "paranoiac", "paranoid",
				"parapet", "paraphernalia", "paraphrase", "paraplegia", "paraplegic", "paraquat", "paras", "parasite",
				"parasitic", "parasol", "parathyroid", "paratrooper", "paratroops", "paratyphoid", "parboil", "parcel",
				"parch", "parchment", "pard", "pardon", "pardonable", "pardonably", "pardoner", "pare", "parent",
				"parentage", "parental", "parenthesis", "parenthetic", "parenthood", "parer", "parhelion", "pariah",
				"paring", "parish", "parishioner", "parisian", "parity", "park", "parka", "parkin", "parking",
				"parkland", "parky", "parlance", "parley", "parliament", "parliamentarian", "parliamentary", "parlor",
				"parlour", "parlous", "parmesan", "parochial", "parodist", "parody", "parole", "paroxysm", "parquet",
				"parr", "parricide", "parrot", "parry", "parse", "parsee", "parsi", "parsimonious", "parsimony",
				"parsley", "parsnip", "parson", "parsonage", "part", "partake", "parterre", "parthenogenesis",
				"partial", "partiality", "partially", "participant", "participate", "participation", "participial",
				"participle", "particle", "particular", "particularise", "particularity", "particularize",
				"particularly", "particulars", "parting", "partisan", "partita", "partition", "partitive", "partizan",
				"partly", "partner", "partnership", "partook", "partridge", "parts", "parturition", "party", "parvenu",
				"paschal", "pasha", "pass", "passable", "passage", "passageway", "passbook", "passenger", "passerby",
				"passim", "passing", "passion", "passionate", "passionately", "passionflower", "passive", "passivity",
				"passivize", "passkey", "passover", "passport", "password", "past", "pasta", "paste", "pasteboard",
				"pastel", "pastern", "pasteurise", "pasteurize", "pastiche", "pastille", "pastime", "pasting", "pastor",
				"pastoral", "pastorale", "pastorate", "pastrami", "pastry", "pasturage", "pasture", "pasty", "pat",
				"patch", "patchouli", "patchwork", "patchy", "patella", "patent", "patentee", "patently", "pater",
				"paterfamilias", "paternal", "paternalism", "paternity", "paternoster", "path", "pathan", "pathetic",
				"pathfinder", "pathological", "pathologist", "pathology", "pathos", "pathway", "patience", "patient",
				"patina", "patio", "patisserie", "patois", "patrial", "patriarch", "patriarchal", "patriarchate",
				"patriarchy", "patrician", "patricide", "patrimony", "patriot", "patriotic", "patriotism", "patrol",
				"patrolman", "patron", "patronage", "patroness", "patronise", "patronize", "patronymic", "patten",
				"patter", "pattern", "patty", "paucity", "paunch", "paunchy", "pauper", "pauperise", "pauperism",
				"pauperize", "pause", "pavan", "pavane", "pave", "paved", "pavement", "pavilion", "paving", "paw",
				"pawky", "pawl", "pawn", "pawnbroker", "pawnshop", "pawpaw", "pay", "payable", "payday", "payee",
				"payer", "payload", "paymaster", "payment", "paynim", "payoff", "payola", "payroll", "pea", "peace",
				"peaceable", "peaceful", "peacekeeping", "peacemaker", "peacetime", "peach", "peachick", "peacock",
				"peafowl", "peahen", "peak", "peaked", "peaky", "peal", "peanut", "peanuts", "pear", "pearl", "pearly",
				"pearmain", "peasant", "peasantry", "peashooter", "peat", "pebble", "pebbledash", "pebbly", "pecan",
				"peccadillo", "peccary", "peck", "pecker", "peckish", "pectic", "pectin", "pectoral", "peculate",
				"peculiar", "peculiarity", "peculiarly", "pecuniary", "pedagogue", "pedagogy", "pedal", };
	}

	static class KStemData6 {
		private KStemData6() {
		}

		static String[] data = { "pedant", "pedantic", "pedantry", "peddle", "peddler", "pederast", "pederasty",
				"pedestal", "pedestrian", "pediatrician", "pediatrics", "pedicab", "pedicel", "pedicure", "pedigree",
				"pediment", "pedlar", "pedometer", "pee", "peek", "peekaboo", "peel", "peeler", "peelings", "peep",
				"peeper", "peephole", "peepul", "peer", "peerage", "peeress", "peerless", "peeve", "peevish", "peewit",
				"peg", "pejorative", "pekinese", "pekingese", "pekoe", "pelagic", "pelf", "pelican", "pellagra",
				"pellet", "pellucid", "pelmet", "pelota", "pelt", "pelvic", "pelvis", "pemican", "pemmican", "pen",
				"penal", "penalise", "penalize", "penalty", "penance", "pence", "penchant", "pencil", "pendant",
				"pendent", "pending", "pendulous", "pendulum", "penetrate", "penetrating", "penetration", "penetrative",
				"penguin", "penicillin", "peninsula", "penis", "penitent", "penitential", "penitentiary", "penknife",
				"penmanship", "pennant", "penniless", "pennon", "penny", "pennyweight", "pennywort", "penology",
				"pension", "pensionable", "pensioner", "pensive", "pentagon", "pentagram", "pentameter", "pentateuch",
				"pentathlon", "pentecost", "penthouse", "penultimate", "penumbra", "penurious", "penury", "peon",
				"peony", "people", "pep", "pepper", "peppercorn", "peppermint", "peppery", "pepsin", "peptic", "per",
				"peradventure", "perambulate", "perambulator", "perceive", "percentage", "percentile", "perceptible",
				"perception", "perceptive", "perch", "perchance", "percipient", "percolate", "percolator", "percussion",
				"percussionist", "perdition", "peregrination", "peremptory", "perennial", "perfect", "perfectible",
				"perfection", "perfectionist", "perfectly", "perfidious", "perfidy", "perforate", "perforation",
				"perforce", "perform", "performance", "performer", "perfume", "perfumier", "perfunctory", "pergola",
				"perhaps", "perigee", "perihelion", "peril", "perilous", "perimeter", "period", "periodic",
				"periodical", "periods", "peripatetic", "peripheral", "periphery", "periphrasis", "periphrastic",
				"periscope", "perish", "perishable", "perisher", "perishing", "peristyle", "peritonitis", "periwig",
				"periwinkle", "perjure", "perjurer", "perjury", "perk", "perky", "perm", "permafrost", "permanence",
				"permanency", "permanent", "permanganate", "permeable", "permeate", "permissible", "permission",
				"permissive", "permit", "permutation", "permute", "pernicious", "pernickety", "pernod", "peroration",
				"peroxide", "perpendicular", "perpetrate", "perpetual", "perpetuate", "perpetuity", "perplex",
				"perplexed", "perplexity", "perquisite", "perry", "persecute", "persecution", "perseverance",
				"persevere", "persevering", "persian", "persiflage", "persimmon", "persist", "persistence",
				"persistent", "persnickety", "person", "persona", "personable", "personage", "personal", "personalise",
				"personalities", "personality", "personalize", "personally", "personification", "personify",
				"personnel", "perspective", "perspex", "perspicacious", "perspiration", "perspire", "persuade",
				"persuasion", "persuasive", "pert", "pertain", "pertinacious", "pertinent", "perturb", "perturbation",
				"peruke", "peruse", "pervade", "pervasive", "perverse", "perversion", "perversity", "pervert", "peseta",
				"pesky", "peso", "pessary", "pessimism", "pessimist", "pest", "pester", "pesticide", "pestiferous",
				"pestilence", "pestilent", "pestle", "pet", "petal", "petaled", "petalled", "petard", "peterman",
				"petite", "petition", "petitioner", "petrel", "petrifaction", "petrify", "petrochemical", "petrol",
				"petroleum", "petrology", "petticoat", "pettifogging", "pettish", "petty", "petulant", "petunia", "pew",
				"pewit", "pewter", "peyote", "pfennig", "phaeton", "phagocyte", "phalanx", "phalarope", "phallic",
				"phallus", "phantasmagoria", "phantasmal", "phantasy", "phantom", "pharaoh", "pharisaic", "pharisee",
				"pharmaceutical", "pharmacist", "pharmacology", "pharmacopoeia", "pharmacy", "pharyngitis", "pharynx",
				"phase", "phd", "pheasant", "phenobarbitone", "phenol", "phenomenal", "phenomenally", "phenomenon",
				"phew", "phi", "phial", "philander", "philanthropic", "philanthropist", "philanthropy", "philatelist",
				"philately", "philharmonic", "philhellene", "philippic", "philistine", "philological", "philologist",
				"philology", "philosopher", "philosophical", "philosophise", "philosophize", "philosophy", "philter",
				"philtre", "phizog", "phlebitis", "phlebotomy", "phlegm", "phlegmatic", "phlox", "phobia", "phoenician",
				"phoenix", "phone", "phoneme", "phonemic", "phonemics", "phonetic", "phonetician", "phonetics",
				"phoney", "phonic", "phonics", "phonograph", "phonology", "phony", "phooey", "phosphate",
				"phosphorescence", "phosphorescent", "phosphoric", "phosphorus", "photo", "photocopier", "photocopy",
				"photoelectric", "photogenic", "photograph", "photographer", "photographic", "photography",
				"photosensitive", "photosensitize", "photostat", "photosynthesis", "phototsensitise", "phrasal",
				"phrase", "phrasebook", "phraseology", "phrenetic", "phrenology", "phthisis", "phut", "phylloxera",
				"phylum", "physic", "physical", "physically", "physician", "physicist", "physics", "physio",
				"physiognomy", "physiology", "physiotherapy", "physique", "pianissimo", "pianist", "piano", "pianola",
				"piaster", "piastre", "piazza", "pibroch", "picador", "picaresque", "piccalilli", "piccaninny",
				"piccolo", "pick", "pickaback", "pickaninny", "pickax", "pickaxe", "picked", "picker", "pickerel",
				"picket", "pickings", "pickle", "pickled", "pickpocket", "picky", "picnic", "picnicker", "pictorial",
				"picture", "pictures", "picturesque", "piddle", "piddling", "pidgin", "pie", "piebald", "piece",
				"piecemeal", "pieces", "piecework", "piecrust", "pied", "pier", "pierce", "piercing", "pierrot",
				"piety", "piezoelectric", "piffle", "piffling", "pig", "pigeon", "pigeonhole", "piggery", "piggish",
				"piggy", "piggyback", "piggybank", "pigheaded", "piglet", "pigment", "pigmentation", "pigmy", "pignut",
				"pigskin", "pigsticking", "pigsty", "pigswill", "pigtail", "pike", "pikestaff", "pilaster", "pilau",
				"pilchard", "pile", "piles", "pileup", "pilfer", "pilferage", "pilgrim", "pilgrimage", "pill",
				"pillage", "pillar", "pillbox", "pillion", "pillock", "pillory", "pillow", "pillowcase", "pilot",
				"pimento", "pimp", "pimpernel", "pimple", "pin", "pinafore", "pincer", "pincers", "pinch", "pinchbeck",
				"pinched", "pinchpenny", "pincushion", "pine", "pineal", "pineapple", "pinecone", "pinewood", "piney",
				"ping", "pinhead", "pinion", "pink", "pinkeye", "pinkie", "pinkish", "pinko", "pinky", "pinnace",
				"pinnacle", "pinnate", "pinny", "pinpoint", "pinprick", "pinstripe", "pint", "pinta", "pintable",
				"pinup", "pinwheel", "piny", "pioneer", "pious", "piousness", "pip", "pipal", "pipe", "pipeline",
				"piper", "pipes", "pipette", "piping", "pipit", "pippin", "pipsqueak", "piquant", "pique", "piquet",
				"piracy", "piranha", "pirate", "pirouette", "piscatorial", "pish", "piss", "pissed", "pistachio",
				"pistil", "pistol", "piston", "pit", "pitch", "pitchblende", "pitcher", "pitchfork", "piteous",
				"pitfall", "pith", "pithead", "pithy", "pitiable", "pitiful", "pitiless", "pitman", "piton", "pittance",
				"pituitary", "pity", "pivot", "pivotal", "pixie", "pixilated", "pixy", "pizza", "pizzicato", "placard",
				"placate", "place", "placebo", "placed", "placekick", "placement", "placenta", "placid", "placket",
				"plagarise", "plagarize", "plagiarism", "plague", "plaguey", "plaice", "plaid", "plain", "plainly",
				"plainsman", "plainsong", "plainspoken", "plaint", "plaintiff", "plaintive", "plait", "plan",
				"planchette", "planet", "planetarium", "planetary", "plangent", "plank", "planking", "plankton",
				"planner", "plant", "plantain", "plantation", "planter", "plaque", "plash", "plasma", "plaster",
				"plasterboard", "plastered", "plasterer", "plastering", "plastic", "plasticine", "plasticity",
				"plastics", "plastron", "plate", "plateau", "platelayer", "platform", "plating", "platinum",
				"platitude", "platonic", "platoon", "platter", "platypus", "plaudit", "plausible", "play", "playable",
				"playback", "playbill", "playboy", "player", "playful", "playgoer", "playground", "playgroup",
				"playhouse", "playmate", "playpen", "playroom", "playsuit", "plaything", "playtime", "playwright",
				"plaza", "plea", "pleach", "plead", "pleading", "pleadings", "pleasant", "pleasantry", "please",
				"pleased", "pleasing", "pleasurable", "pleasure", "pleat", "pleb", "plebeian", "plebiscite", "plectrum",
				"pled", "pledge", "pleistocene", "plenary", "plenipotentiary", "plenitude", "plenteous", "plentiful",
				"plenty", "pleonasm", "plethora", "pleurisy", "plexus", "pliable", "pliant", "pliers", "plight",
				"plimsoll", "plinth", "pliocene", "plod", "plodder", "plonk", "plop", "plosive", "plot", "plough",
				"ploughboy", "ploughman", "ploughshare", "plover", "plow", "plowboy", "plowman", "plowshare", "ploy",
				"pluck", "plucky", "plug", "plughole", "plum", "plumage", "plumb", "plumbago", "plumber", "plumbing",
				"plume", "plumed", "plummet", "plummy", "plump", "plunder", "plunge", "plunger", "plunk", "pluperfect",
				"plural", "pluralism", "plurality", "pluribus", "plus", "plush", "plushy", "pluto", "plutocracy",
				"plutocrat", "plutonium", "ply", "plywood", "pneumatic", "pneumoconiosis", "pneumonia", "poach",
				"poacher", "pock", "pocked", "pocket", "pocketbook", "pocketful", "pocketknife", "pockmark",
				"pockmarked", "pod", "podgy", "podiatry", "podium", "poem", "poesy", "poet", "poetaster", "poetess",
				"poetic", "poetical", "poetry", "pogrom", "poignancy", "poignant", "poinsettia", "point", "pointed",
				"pointer", "pointillism", "pointless", "points", "pointsman", "poise", "poised", "poison", "poisonous",
				"poke", "poker", "pokerwork", "poky", "polack", "polar", "polarisation", "polarise", "polarity",
				"polarization", "polarize", "polaroid", "polaroids", "polder", "pole", "poleax", "poleaxe", "polecat",
				"polemic", "polemical", "polemics", "police", "policeman", "policewoman", "policy", "polio", "polish",
				"polisher", "politburo", "polite", "politic", "politicalise", "politicalize", "politician",
				"politicise", "politicize", "politicking", "politico", "politics", "polity", "polka", "poll", "pollard",
				"pollen", "pollinate", "polling", "pollster", "pollutant", "pollute", "pollution", "polly", "pollyanna",
				"polo", "polonaise", "polony", "poltergeist", "poltroon", "poly", "polyandrous", "polyandry",
				"polyanthus", "polyester", "polyethylene", "polygamist", "polygamous", "polygamy", "polyglot",
				"polygon", "polymath", "polymer", "polymorphous", "polyp", "polyphony", "polypus", "polystyrene",
				"polysyllable", "polytechnic", "polytheism", "polythene", "polyurethane", "pomade", "pomander",
				"pomegranate", "pomeranian", "pommel", "pommy", "pomp", "pompom", "pomposity", "pompous", "ponce",
				"poncho", "poncy", "pond", "ponder", "ponderous", "pone", "pong", "poniard", "pontiff", "pontifical",
				"pontificals", "pontificate", "pontoon", "pony", "ponytail", "pooch", "poodle", "poof", "pooh", "pool",
				"poolroom", "pools", "poop", "pooped", "poor", "poorhouse", "poorly", "poorness", "poove", "pop",
				"popadam", "popadum", "popcorn", "popery", "popgun", "popinjay", "popish", "poplar", "poplin", "poppa",
				"popper", "poppet", "poppy", "poppycock", "popshop", "popsy", "populace", "popular", "popularise",
				"popularity", "popularize", "popularly", "populate", "population", "populism", "populist", "populous",
				"porcelain", "porch", "porcine", "porcupine", "pore", "pork", "porker", "porky", "porn", "pornography",
				"porosity", "porous", "porphyry", "porpoise", "porridge", "porringer", "port", "portable", "portage",
				"portal", "portals", "portcullis", "portend", "portent", "portentous", "porter", "porterage",
				"porterhouse", "portfolio", "porthole", "portico", "portion", "portly", "portmanteau", "portrait",
				"portraitist", "portraiture", "portray", "portrayal", "pose", "poser", "poseur", "posh", "posit",
				"position", "positional", "positive", "positively", "positiveness", "positivism", "positron", "posse",
				"possess", "possessed", "possession", "possessive", "possessor", "posset", "possibility", "possible",
				"possibly", "possum", "post", "postage", "postal", "postbag", "postbox", "postcard", "postcode",
				"postdate", "poster", "posterior", "posterity", "postern", "postgraduate", "posthaste", "posthumous",
				"postilion", "postillion", "posting", "postman", "postmark", "postmaster", "postmortem", "postpaid",
				"postpone", "postprandial", "postscript", "postulant", "postulate", "posture", "postwar", "posy", "pot",
				"potable", "potash", "potassium", "potation", "potato", "potbellied", "potbelly", "potboiler",
				"potbound", "poteen", "potency", "potent", "potentate", "potential", "potentiality", "pothead",
				"pother", "potherb", "pothole", "potholing", "pothouse", "pothunter", "potion", "potluck", "potpourri",
				"potsherd", "potshot", "pottage", "potted", "potter", "potteries", "pottery", "potty", "pouch", "pouf",
				"pouffe", "poulterer", "poultice", "poultry", "pounce", "pound", "poundage", "pounding", "pour", "pout",
				"poverty", "powder", "powdered", "powdery", "power", "powerboat", "powerful", "powerhouse", "powerless",
				"powers", "powwow", "pox", "pps", "practicable", "practical", "practicality", "practically", "practice",
				"practiced", "practise", "practised", "practitioner", "praesidium", "praetor", "praetorian",
				"pragmatic", "pragmatism", "prairie", "praise", "praises", "praiseworthy", "praline", "pram", "prance",
				"prank", "prankster", "prat", "prate", "pratfall", "prattle", "prawn", "praxis", "pray", "prayer",
				"preach", "preachify", "preamble", "prearrange", "prebend", "prebendary", "precarious", "precast",
				"precaution", "precede", "precedence", "precedent", "preceding", "precentor", "precept", "preceptor",
				"precession", "precinct", "precincts", "preciosity", "precious", "precipice", "precipitate",
				"precipitation", "precipitous", "precise", "precisely", "precision", "preclude", "precocious",
				"precognition", "preconceived", "preconception", "precondition", "precook", "precursor", "predator",
				"predatory", "predecease", "predecessor", "predestinate", "predestination", "predestine",
				"predetermine", "predeterminer", "predicament", "predicate", "predicative", "predict", "predictable",
				"prediction", "predigest", "predilection", "predispose", "predisposition", "predominance",
				"predominant", "predominantly", "predominate", "preeminent", "preeminently", "preempt", "preemption",
				"preemptive", "preen", "preexist", "preexistence", "prefab", "prefabricate", "prefabricated", "preface",
				"prefatory", "prefect", "prefecture", "prefer", "preferable", "preference", "preferential",
				"preferment", "prefigure", "prefix", "pregnancy", "pregnant", "preheat", "prehensile", "prehistoric",
				"prehistory", "prejudge", "prejudice", "prejudiced", "prejudicial", "prelacy", "prelate", "prelim",
				"preliminary", "prelims", "preliterate", "prelude", "premarital", "premature", "premeditate",
				"premeditated", "premier", "premise", "premises", "premiss", "premium", "premonition", "premonitory",
				"prenatal", "prentice", "preoccupation", "preoccupied", "preoccupy", "preordain", "prep", "prepack",
				"preparation", "preparatory", "prepare", "prepared", "preparedness", "prepay", "preponderance",
				"preponderant", "preponderate", "preposition", "prepositional", "prepossessed", "prepossessing",
				"prepossession", "preposterous", "prepuce", "prerecord", "prerequisite", "prerogative", "presage",
				"presbyter", "presbyterian", "presbytery", "preschool", "prescient", "prescribe", "prescribed",
				"prescript", "prescription", "prescriptive", "presence", "present", "presentable", "presentation",
				"presenter", "presentiment", "presently", "presents", "preservable", "preservation", "preservative",
				"preserve", "preserver", "preset", "preshrunk", "preside", "presidency", "president", "presidential",
				"presidium", "press", "pressed", "pressgang", "pressing", "pressman", "pressmark", "pressure",
				"pressurise", "pressurize", "prestidigitation", "prestige", "prestigious", "prestissimo", "presto",
				"prestressed", "presumable", "presume", "presumption", "presumptive", "presumptuous", "presuppose",
				"presupposition", "pretence", "pretend", "pretended", "pretender", "pretense", "pretension",
				"pretentious", "pretentiousness", "preterit", "preterite", "preternatural", "pretext", "pretor",
				"pretorian", "prettify", "prettily", "pretty", "pretzel", "prevail", "prevailing", "prevalent",
				"prevaricate", "prevent", "prevention", "preventive", "preview", "previous", "prevision", "prewar",
				"prey", "price", "priceless", "pricey", "prick", "prickle", "prickly", "pricy", "pride", "priest",
				"priesthood", "priestly", "prig", "priggish", "prim", "primacy", "primaeval", "primal", "primarily",
				"primary", "primate", "prime", "primer", "primeval", "priming", "primitive", "primogeniture",
				"primordial", "primp", "primrose", "primula", "primus", "prince", "princedom", "princely", "princess",
				"principal", "principality", "principally", "principle", "principled", "principles", "prink", "print",
				"printable", "printer", "printing", "printout", "prior", "priority", "priory", "prise", "prism",
				"prismatic", "prison", "prisoner", "prissy", "pristine", "prithee", "privacy", "private", "privateer",
				"privation", "privet", "privilege", "privileged", "privily", "privy", "prize", "prizefight", "prizeman",
				"pro", "probability", "probable", "probably", "probate", "probation", "probationer", "probe", "probity",
				"problem", "problematic", "proboscis", "procedural", "procedure", "proceed", "proceeding",
				"proceedings", "proceeds", "process", "procession", "processional", "proclaim", "proclamation",
				"proclivity", "proconsul", "proconsulate", "procrastinate", "procreate", "proctor", "procure",
				"procurer", "prod", "prodigal", "prodigious", "prodigy", "produce", "producer", "product", "production",
				"productive", "productivity", "proem", "prof", "profanation", "profane", "profanity", "profess",
				"professed", "professedly", "profession", "professional", "professionalism", "professor",
				"professorial", "professorship", "proffer", "proficient", "profile", "profit", "profitable",
				"profiteer", "profligacy", "profligate", "profound", "profundity", "profuse", "profusion", "progenitor",
				"progeny", "progesterone", "prognathous", "prognosis", "prognostic", "prognosticate", "prognostication",
				"program", "programer", "programmer", "progress", "progression", "progressive", "prohibit",
				"prohibition", "prohibitionist", "prohibitive", "prohibitory", "project", "projectile", "projection",
				"projectionist", "projector", "prolapse", "prole", "prolegomena", "proletarian", "proletariat",
				"proliferate", "proliferation", "prolific", "prolix", "prolog", "prologue", "prolong", "prolongation",
				"prolonged", "prom", "promenade", "promenader", "prominence", "prominent", "promiscuity", "promiscuous",
				"promise", "promising", "promontory", "promote", "promoter", "promotion", "prompt", "prompter",
				"promptness", "promulgate", "pron", "prone", "prong", "pronominal", "pronoun", "pronounce",
				"pronounceable", "pronounced", "pronouncement", "pronto", "pronunciamento", "pronunciation", "proof",
				"proofread", "prop", "propaganda", "propagandise", "propagandist", "propagandize", "propagate",
				"propagation", "propane", "propel", "propellant", "propellent", "propeller", "propensity", "proper",
				"properly", "propertied", "property", "prophecy", "prophesy", "prophet", "prophetess", "prophetic",
				"prophets", "prophylactic", "prophylaxis", "propinquity", "propitiate", "propitiatory", "propitious",
				"propjet", "proponent", "proportion", "proportional", "proportionate", "proportions", "proposal",
				"propose", "proposition", "propound", "proprietary", "proprieties", "proprietor", "proprietress",
				"propriety", "propulsion", "propulsive", "propylene", "prorogation", "prorogue", "prosaic",
				"proscenium", "proscribe", "proscription", "prose", "prosecute", "prosecution", "prosecutor",
				"proselyte", "proselytise", "proselytize", "prosody", "prospect", "prospective", "prospector",
				"prospects", "prospectus", "prosper", "prosperity", "prosperous", "prostate", "prosthesis",
				"prostitute", "prostitution", "prostrate", "prostration", "prosy", "protagonist", "protean", "protect",
				"protection", "protectionism", "protective", "protector", "protectorate", "protein", "protest",
				"protestant", "protestation", "protocol", "proton", "protoplasm", "prototype", "protozoa", "protozoan",
				"protozoon", "protract", "protraction", "protractor", "protrude", "protrusion", "protrusive",
				"protuberance", "protuberant", "proud", "provable", "prove", "proven", "provenance", "provender",
				"proverb", "proverbial", "proverbially", "proverbs", "provide", "provided", "providence", "provident",
				"providential", "provider", "providing", "province", "provinces", "provincial", "provision",
				"provisional", "provisions", "proviso", "provocation", "provocative", "provoke", "provoking", "provost",
				"prow", "prowess", "prowl", "prowler", "prox", "proximal", "proximate", "proximity", "proximo", "proxy",
				"prude", "prudence", "prudent", "prudential", "prudery", "prudish", "prune", "pruning", "prurience",
				"prurient", "pruritus", "prussian", "pry", "psalm", "psalmist", "psalmody", "psalms", "psalter",
				"psaltery", "psephology", "pseud", "pseudonym", "pseudonymous", "pshaw", "psittacosis", "psoriasis",
				"psst", "psyche", "psychedelic", "psychiatric", "psychiatrist", "psychiatry", "psychic", "psycho",
				"psychoanalyse", "psychoanalysis", "psychoanalyst", "psychoanalytic", "psychoanalyze", "psychokinesis",
				"psychological", "psychologist", "psychology", "psychopath", "psychosis", "psychosomatic",
				"psychotherapy", "psychotic", "pta", "ptarmigan", "pterodactyl", "pto", "ptomaine", "pub", "puberty",
				"pubic", "public", "publican", "publication", "publicise", "publicist", "publicity", "publicize",
				"publish", "publisher", "publishing", "puce", "puck", "pucker", "puckish", "pud", "pudding", "puddle",
				"pudendum", "pudgy", "pueblo", "puerile", "puerility", "puerperal", "puff", "puffball", "puffed",
				"puffer", "puffin", "puffy", "pug", "pugilism", "pugilist", "pugnacious", "pugnacity", "puissance",
				"puissant", "puke", "pukka", "pulchritude", "pulchritudinous", "pule", "pull", "pullback", "pullet",
				"pulley", "pullman", "pullout", "pullover", "pullthrough", "pullulate", "pulmonary", "pulp", "pulpit",
				"pulsar", "pulsate", "pulsation", "pulse", "pulverise", "pulverize", "puma", "pumice", "pummel", "pump",
				"pumpernickel", "pumpkin", "pun", "punch", "punchy", "punctilio", "punctilious", "punctual",
				"punctuate", "punctuation", "puncture", "pundit", "pungent", "punic", "punish", "punishable",
				"punishing", "punishment", "punitive", "punjabi", "punk", "punkah", "punnet", "punster", "punt", "puny",
				"pup", "pupa", "pupate", "pupil", "puppet", "puppeteer", "puppy", "purblind", "purchase", "purchaser",
				"purdah", "pure", "pureblooded", "purebred", "puree", "purely", "pureness", "purgation", "purgative",
				"purgatory", "purge", "purification", "purify", "purist", "puritan", "puritanical", "purity", "purl",
				"purler", "purlieus", "purloin", "purple", "purplish", "purport", "purpose", "purposeful",
				"purposeless", "purposely", "purposive", "purr", "purse", "purser", "pursuance", "pursue", "pursuer",
				"pursuit", "purulent", "purvey", "purveyance", "purveyor", "purview", "pus", "push", "pushbike",
				"pushcart", "pushchair", "pushed", "pusher", "pushover", "pushy", "pusillanimous", "puss", "pussy",
				"pussycat", "pussyfoot", "pustule", "put", "putative", "putrefaction", "putrefactive", "putrefy",
				"putrescent", "putrid", "putsch", "putt", "puttee", "putter", "putto", "putty", "puzzle", "puzzlement",
				"puzzler", "pvc", "pygmy", "pyjama", "pyjamas", "pylon", "pyorrhea", "pyorrhoea", "pyramid", "pyre",
				"pyrex", "pyrexia", "pyrites", "pyromania", "pyromaniac", "pyrotechnic", "pyrotechnics", "python",
				"pyx", "qed", "qty", "qua", "quack", "quackery", "quad", "quadragesima", "quadrangle", "quadrangular",
				"quadrant", "quadrilateral", "quadrille", "quadrillion", "quadroon", "quadruped", "quadruple",
				"quadruplet", "quadruplicate", "quaff", "quagga", "quagmire", "quail", "quaint", "quake", "quaker",
				"qualification", "qualifications", "qualified", "qualifier", "qualify", "qualitative", "quality",
				"qualm", "quandary", "quantify", "quantitative", "quantity", "quantum", "quarantine", "quark",
				"quarrel", "quarrelsome", "quarry", "quart", "quarter", "quarterdeck", "quarterfinal", "quartering",
				"quarterly", "quartermaster", "quarters", "quarterstaff", "quartet", "quartette", "quarto", "quartz",
				"quasar", "quash", "quatercentenary", "quatrain", "quaver", "quay", "quean", "queasy", "queen",
				"queenly", "queer", "quell", "quench", "quenchless", "querulous", "query", "quest", "question",
				"questionable", "questioner", "questioning", "questionnaire", "quetzal", "queue", "quibble", "quick",
				"quicken", "quickie", "quicklime", "quicksand", "quicksilver", "quickstep", "quid", "quiescent",
				"quiet", "quieten", "quietism", "quietude", "quietus", "quiff", "quill", "quilt", "quilted", "quin",
				"quince", "quinine", "quinquagesima", "quinsy", "quintal", "quintessence", "quintet", "quintette",
				"quintuplet", "quip", "quire", "quirk", "quisling", "quit", "quits", "quittance", "quitter", "quiver",
				"quixotic", "quiz", "quizmaster", "quizzical", "quod", "quoit", "quoits", "quondam", "quorum", "quota",
				"quotable", "quotation", "quote", "quoth", "quotidian", "quotient", "rabbi", "rabbinical", "rabbit",
				"rabble", "rabelaisian", "rabid", "rabies", "rac", "raccoon", "race", "racecourse", "racehorse",
				"raceme", "racer", "races", "racetrack", "racial", "racialism", "racially", "racing", "rack", "racket",
				"racketeer", "racketeering", "rackets", "raconteur", "racoon", "racquet", "racquets", "racy", "radar",
				"radial", "radiance", "radiant", "radiate", "radiation", "radiator", "radical", "radicalise",
				"radicalism", "radicalize", "radicle", "radii", "radio", "radioactive", "radioactivity", "radiogram",
				"radiograph", "radiographer", "radiography", "radioisotope", "radiolocation", "radiology",
				"radiotherapist", "radiotherapy", "radish", "radium", "radius", "raffia", "raffish", "raffle", "raft",
				"rafter", "raftered", "raftsman", "rag", "raga", "ragamuffin", "ragbag", "rage", "ragged", "raglan",
				"ragout", "ragtag", "ragtime", "raid", "raider", "rail", "railhead", "railing", "raillery", "railroad",
				"rails", "railway", "raiment", "rain", "rainbow", "raincoat", "raindrop", "rainfall", "rainproof",
				"rains", "rainstorm", "rainwater", "rainy", "raise", "raisin", "raj", "raja", "rajah", "rake", "rakish",
				"rallentando", "rally", "ram", "ramadan", "ramble", "rambler", "rambling", "rambunctious", "ramekin",
				"ramification", "ramify", "ramjet", "ramp", "rampage", "rampant", "rampart", "ramrod", "ramshackle",
				"ran", "ranch", "rancher", "rancid", "rancor", "rancorous", "rancour", "rand", "random", "randy",
				"ranee", "rang", "range", "ranger", "rani", "rank", "ranker", "ranking", "rankle", "ranks", "ransack",
				"ransom", "rant", "rap", "rapacious", "rapacity", "rape", "rapid", "rapids", "rapier", "rapine",
				"rapist", "rapport", "rapprochement", "rapscallion", "rapt", "rapture", "rapturous", "rare", "rarebit",
				"rarefied", "rarefy", "rarely", "raring", "rarity", "rascal", "rascally", "rash", "rasher", "rasp",
				"raspberry", "rat", "ratable", "ratchet", "rate", "rateable", "ratepayer", "rather", "ratify", "rating",
				"ratio", "ratiocination", "ration", "rational", "rationale", "rationalise", "rationalism",
				"rationalist", "rationalize", "rations", "ratlin", "ratline", "rats", "rattan", "ratter", "rattle",
				"rattlebrained", "rattlesnake", "rattletrap", "rattling", "ratty", "raucous", "raunchy", "ravage",
				"ravages", "rave", "ravel", "raven", "ravening", "ravenous", "raver", "ravine", "raving", "ravings",
				"ravioli", "ravish", "ravishing", "ravishment", "raw", "rawhide", "ray", "rayon", "raze", "razor",
				"razorback", "razzle", "reach", "react", "reaction", "reactionary", "reactivate", "reactive", "reactor",
				"read", "readable", "readdress", "reader", "readership", "readily", "readiness", "reading", "readjust",
				"readout", "ready", "reafforest", "reagent", "real", "realign", "realisable", "realisation", "realise",
				"realism", "realist", "realistic", "reality", "realizable", "realization", "realize", "really", "realm",
				"realpolitik", "realtor", "realty", "ream", "reanimate", "reap", "reaper", "reappear", "reappraisal",
				"rear", "rearguard", "rearm", "rearmament", "rearmost", "rearrange", "rearward", "rearwards", "reason",
				"reasonable", "reasonably", "reasoned", "reasoning", "reassure", "rebarbative", "rebate", "rebel",
				"rebellion", "rebellious", "rebind", "rebirth", "reborn", "rebound", "rebuff", "rebuild", "rebuke",
				"rebus", "rebut", "rebuttal", "recalcitrance", "recalcitrant", "recall", "recant", "recap",
				"recapitulate", "recapitulation", "recapture", "recast", "recce", "recd", "recede", "receipt",
				"receipts", "receivable", "receive", "received", "receiver", "receivership", "receiving", "recent",
				"recently", "receptacle", "reception", "receptionist", "receptive", "recess", "recession",
				"recessional", "recessive", "recharge", "recidivist", "recipe", "recipient", "reciprocal",
				"reciprocate", "reciprocity", "recital", "recitation", "recitative", "recite", "reck", "reckless",
				"reckon", "reckoner", "reckoning", "reclaim", "reclamation", "recline", "recluse", "recognise",
				"recognition", "recognizance", "recognize", "recoil", "recollect", "recollection", "recommend",
				"recommendation", "recompense", "reconcile", "reconciliation", "recondite", "recondition",
				"reconnaissance", "reconnoiter", "reconnoitre", "reconsider", "reconstitute", "reconstruct",
				"reconstruction", "record", "recorder", "recording", "recordkeeping", "recount", "recoup", "recourse",
				"recover", "recovery", "recreant", "recreate", "recreation", "recreational", "recriminate",
				"recrimination", "recrudescence", "recruit", "rectal", "rectangle", "rectangular", "rectification",
				"rectifier", "rectify", "rectilinear", "rectitude", "recto", "rector", "rectory", "rectum", "recumbent",
				"recuperate", "recuperative", "recur", "recurrence", "recurrent", "recurved", "recusant", "recycle",
				"red", "redbreast", "redbrick", "redcap", "redcoat", "redcurrant", "redden", "reddish", "redecorate",
				"redeem", "redeemer", "redemption", "redemptive", "redeploy", "redhead", "rediffusion", "redirect",
				"redistribute", "redo", "redolence", "redolent", "redouble", "redoubt", "redoubtable", "redound",
				"redress", "redskin", "reduce", "reduction", "redundancy", "redundant", "reduplicate", "redwing",
				"redwood", "reecho", "reed", "reeds", "reeducate", "reedy", "reef", "reefer", "reek", "reel", "reentry",
				"reeve", "ref", "reface", "refashion", "refectory", "refer", "referee", "reference", "referendum",
				"refill", "refine", "refined", "refinement", "refiner", "refinery", "refit", "reflate", "reflation",
				"reflect", "reflection", "reflective", "reflector", "reflex", "reflexes", "reflexive", "refloat",
				"refoot", "reforest", "reform", "reformation", "reformatory", "refract", "refractory", "refrain",
				"refresh", "refresher", "refreshing", "refreshment", "refreshments", "refrigerant", "refrigerate",
				"refrigeration", "refrigerator", "reft", "refuel", "refuge", "refugee", "refulgence", "refulgent",
				"refund", "refurbish", "refusal", "refuse", "refutable", "refutation", "refute", "regain", "regal",
				"regale", "regalia", "regard", "regardful", "regarding", "regardless", "regards", "regatta", "regency",
				"regenerate", "regent", "reggae", "regicide", "regime", "regimen", "regiment", "regimental",
				"regimentals", "regina", "region", "regional", "regions", "register", "registrar", "registration",
				"registry", "regnant", "regress", "regressive", "regret", "regrets", "regrettable", "regrettably",
				"regroup", "regular", "regularise", "regularity", "regularize", "regularly", "regulate", "regulation",
				"regulator", "regulo", "regurgitate", "rehabilitate", "rehash", "rehear", "rehearsal", "rehearse",
				"rehouse", "reich", "reification", "reify", "reign", "reimburse", "reimbursement", "rein",
				"reincarnate", "reincarnation", "reindeer", "reinforce", "reinforcement", "reinforcements", "reins",
				"reinstate", "reinsure", "reissue", "reiterate", "reject", "rejection", "rejoice", "rejoicing",
				"rejoicings", "rejoin", "rejoinder", "rejuvenate", "rekindle", "relaid", "relapse", "relate", "related",
				"relation", "relational", "relations", "relationship", "relative", "relatively", "relativism",
				"relativistic", "relativity", "relax", "relaxation", "relaxing", "relay", "release", "relegate",
				"relent", "relentless", "relevance", "relevant", "reliability", "reliable", "reliance", "reliant",
				"relic", "relics", "relict", "relief", "relieve", "relieved", "religion", "religious", "religiously",
				"reline", "relinquish", "reliquary", "relish", "relive", "reload", "relocate", "reluctance",
				"reluctant", "reluctantly", "rely", "remain", "remainder", "remains", "remake", "remand", "remark",
				"remarkable", "remarkably", "remarry", "remediable", "remedial", "remedy", "remember", "remembrance",
				"remilitarise", "remilitarize", "remind", "reminder", "reminisce", "reminiscence", "reminiscences",
				"reminiscent", "remiss", "remission", "remit", "remittance", "remittent", "remnant", "remodel",
				"remold", "remonstrance", "remonstrate", "remorse", "remorseful", "remote", "remotely", "remould",
				"remount", "removal", "remove", "remover", "remunerate", "remunerative", "renaissance", "renal",
				"rename", "renascent", "rend", "render", "rendering", "rendezvous", "rendition", "renegade", "renege",
				"renegue", "renew", "renewable", "renewal", "rennet", "renounce", "renovate", "renown", "renowned",
				"rent", "rental", "renter", "rentier", "renunciation", "reopen", "reorganise", "reorganize", "rep",
				"repaid", "repair", "reparable", "reparation", "reparations", "repartee", "repast", "repatriate",
				"repay", "repayable", "repayment", "repeal", "repeat", "repeated", "repeatedly", "repeater",
				"repeating", "repel", "repellent", "repent", "repentance", "repentant", "repercussion", "repertoire",
				"repertory", "repetition", "repetitious", "repine", "replace", "replacement", "replay", "replenish",
				"replete", "repletion", "replica", "replicate", "reply", "repoint", "report", "reportage", "reportedly",
				"reporter", "repose", "repository", "repossess", "repot", "repp", "reprehend", "reprehensible",
				"represent", "representation", "representational", "representations", "representative", "repress",
				"repressed", "repression", "repressive", "reprieve", "reprimand", "reprint", "reprisal", "reprise",
				"reproach", "reprobate", "reproduce", "reproducer", "reproduction", "reproductive", "reproof",
				"reprove", "reproving", "reptile", "reptilian", "republic", "republican", "republicanism", "repudiate",
				"repugnance", "repugnant", "repulse", "repulsion", "repulsive", "reputable", "reputation", "repute",
				"reputed", "reputedly", "request", "requiem", "require", "requirement", "requisite", "requisition",
				"requital", "requite", "reredos", "rerun", "rescind", "rescript", "rescue", "research", "reseat",
				"resemblance", "resemble", "resent", "resentment", "reservation", "reserve", "reserved", "reservedly",
				"reservist", "reservoir", "reset", "resettle", "reshuffle", "reside", "residence", "residency",
				"resident", "residential", "residual", "residuary", "residue", "resign", "resignation", "resigned",
				"resilience", "resilient", "resin", "resinated", "resist", "resistance", "resistant", "resistor",
				"resole", "resolute", "resolution", "resolvable", "resolve", "resonance", "resonant", "resonate",
				"resonator", "resort", "resound", "resounding", "resource", "resourceful", "resources", "respect",
				"respectability", "respectable", "respecter", "respectful", "respecting", "respective", "respectively",
				"respects", "respiration", "respirator", "respiratory", "respire", "respite", "resplendence",
				"resplendent", "respond", "respondent", "response", "responsibility", "responsible", "responsibly",
				"responsive", "rest", "restage", "restate", "restaurant", "restaurateur", "restful", "restitution",
				"restive", "restless", "restock", "restoration", "restorative", "restore", "restorer", "restrain",
				"restrained", "restraint", "restrict", "restricted", "restriction", "restrictive", "restructure",
				"result", "resultant", "resume", "resumption", "resurface", "resurgence", "resurgent", "resurrect",
				"resurrection", "resuscitate", "retail", "retailer", "retain", "retainer", "retake", "retaliate",
				"retaliation", "retaliatory", "retard", "retarded", "retch", "retd", "retell", "retention", "retentive",
				"rethink", "reticence", "reticent", "reticulated", "reticulation", "reticule", "retina", "retinue",
				"retire", "retired", "retirement", "retiring", "retort", "retouch", "retrace", "retract", "retractable",
				"retractile", "retraction", "retread", "retreat", "retrench", "retrial", "retraining", "retribution",
				"retributive", "retrieval", "retrieve", "retriever", "retroactive", "retroflex", "retrograde",
				"retrogress", "retrogressive", "retrospect", "retrospection", "retrospective", "retroversion",
				"retsina", "return", "returnable", "returns", "reunion", "reunite", "reuse", "rev", "revalue", "revamp",
				"reveal", "revealing", "reveille", "revel", "revelation", "revelry", "revenge", "revenue",
				"reverberant", "reverberate", "reverberation", "revere", "reverence", "reverend", "reverent",
				"reverential", "reverie", "revers", "reversal", "reverse", "reversion", "reversionary", "revert",
				"revetment", "review", "reviewer", "revile", "revise", "revision", "revisionism", "revitalise",
				"revitalize", "revival", "revivalist", "revive", "revivify", "revocable", "revocation", "revoke",
				"revolt", "revolting", "revolution", "revolutionary", "revolutionise", "revolutionize", "revolve",
				"revolver", "revolving", "revue", "revulsion", "reward", "rewarding", "rewards", "rewire", "reword",
				"rewrite", "rex", "rhapsodise", "rhapsodize", "rhapsody", "rhea", "rhenish", "rheostat", "rhetoric",
				"rhetorical", "rhetorically", "rhetorician", "rheum", "rheumatic", "rheumaticky", "rheumatics",
				"rheumatism", "rheumatoid", "rhinestone", "rhinoceros", "rhizome", "rhododendron", "rhomboid",
				"rhombus", "rhubarb", "rhyme", "rhymed", "rhymester", "rhythm", "rhythmic", "rib", "ribald", "ribaldry",
				"ribbed", "ribbing", "ribbon", "riboflavin", "rice", "rich", "riches", "richly", "richness", "rick",
				"rickets", "rickety", "ricksha", "rickshaw", "ricochet", "rid", "riddance", "ridden", "riddle", "ride",
				"rider", "riderless", "ridge", "ridgepole", "ridicule", "ridiculous", "riding", "riesling", "rife",
				"riff", "riffle", "riffraff", "rifle", "rifleman", "rifles", "rifling", "rift", "rig", "rigging",
				"right", "righteous", "rightful", "rightist", "rightly", "rights", "rightward", "rightwards", "rigid",
				"rigidity", "rigmarole", "rigor", "rigorous", "rigour", "rile", "rill", "rim", "rime", "rind",
				"rinderpest", "ring", "ringer", "ringleader", "ringlet", "ringmaster", "ringside", "ringworm", "rink",
				"rinse", "riot", "riotous", "rip", "riparian", "ripcord", "ripen", "riposte", "ripple", "ripsaw",
				"riptide", "rise", "riser", "risibility", "risible", "rising", "risk", "risky", "risotto", "rissole",
				"rite", "ritual", "ritualism", "ritzy", "rival", "rivalry", "rive", "river", "riverbed", "riverside",
				"rivet", "riveter", "riveting", "riviera", "rivulet", "rna", "roach", "road", "roadbed", "roadblock",
				"roadhouse", "roadman", "roadside", "roadstead", "roadster", "roadway", "roadworthy", "roam", "roan",
				"roar", "roaring", "roast", "roaster", "roasting", "rob", "robber", "robbery", "robe", "robin", "robot",
				"robust", "rock", "rockbound", "rocker", "rockery", "rocket", "rocketry", "rocks", "rocky", "rococo",
				"rod", "rode", "rodent", "rodeo", "rodomontade", "roe", "roebuck", "rogation", "roger", "rogue",
				"roguery", "roguish", "roisterer", "role", "roll", "roller", "rollicking", "rolling", "rolls", "romaic",
				"roman", "romance", "romanesque", "romantic", "romanticise", "romanticism", "romanticize", "romany",
				"romish", "romp", "romper", "rompers", "rondeau", "rondo", "roneo", "rood", "roodscreen", "roof",
				"roofing", "roofless", "rooftree", "rook", "rookery", "rookie", "room", "roomer", "roommate", "rooms",
				"roomy", "roost", "rooster", "root", "rooted", "rootless", "roots", "rope", "ropedancer", "ropes",
				"ropewalk", "ropeway", "ropey", "ropy", "roquefort", "rosary", "rose", "roseate", "rosebud", "roseleaf",
				"rosemary", "rosette", "rosewater", "rosewood", "rosin", "roster", "rostrum", "rosy", "rot", "rota",
				"rotary", "rotate", "rotation", "rotatory", "rotgut", "rotisserie", "rotogravure", "rotor", "rotten",
				"rottenly", "rotter", "rotund", "rotunda", "rouble", "rouge", "rough", "roughage", "roughcast",
				"roughen", "roughhouse", "roughly", "roughneck", "roughness", "roughrider", "roughshod", "roulette",
				"round", "roundabout", "roundel", "roundelay", "rounders", "roundhead", "roundhouse", "roundish",
				"roundly", "rounds", "roundsman", "roundup", "roup", "rouse", "rousing", "roustabout", "rout", "route",
				"routine", "roux", "rove", "rover", "row", "rowan", "rowanberry", "rowdy", "rowdyism", "rowel", "rower",
				"rowing", "rowlock", "royal", "royalist", "royalty", "rpm", "rsm", "rsvp", "rub", "rubber", "rubberise",
				"rubberize", "rubberneck", "rubbery", "rubbing", "rubbish", "rubbishy", "rubble", "rubdown", "rubella",
				"rubicon", "rubicund", "ruble", "rubric", "ruby", "ruck", "rucksack", "ruckus", "ruction", "ructions",
				"rudder", "ruddle", "ruddy", "rude", "rudely", "rudiment", "rudimentary", "rudiments", "rue", "rueful",
				"ruff", "ruffian", "ruffianly", "ruffle", "rug", "rugby", "rugged", "ruin", "ruination", "ruinous",
				"ruins", "rule", "rulebook", "ruler", "ruling", "rum", "rumba", "rumble", "rumbling", "rumbustious",
				"ruminant", "ruminate", "ruminative", "rummage", "rummy", "rumor", "rumored", "rumormonger", "rumour",
				"rumoured", "rumourmonger", "rump", "rumple", "rumpus", "run", "runaway", "rung", "runnel", "runner",
				"running", "runny", "runs", "runt", "runway", };
	}

	static class KStemData7 {
		private KStemData7() {
		}

		static String[] data = { "rupee", "rupture", "rural", "ruritanian", "ruse", "rush", "rushes", "rushlight",
				"rusk", "russet", "rust", "rustic", "rusticate", "rustication", "rustle", "rustler", "rustless",
				"rustling", "rustproof", "rusty", "rut", "ruthless", "rutting", "rye", "sabbatarian", "sabbath",
				"sabbatical", "saber", "sable", "sabot", "sabotage", "saboteur", "sabra", "sabre", "sac", "saccharin",
				"saccharine", "sacerdotal", "sacerdotalism", "sachet", "sack", "sackbut", "sackcloth", "sacral",
				"sacrament", "sacramental", "sacred", "sacrifice", "sacrificial", "sacrilege", "sacrilegious",
				"sacristan", "sacristy", "sacroiliac", "sacrosanct", "sad", "sadden", "saddle", "saddlebag", "saddler",
				"saddlery", "sadducee", "sadhu", "sadism", "sadly", "sadomasochism", "safari", "safe", "safebreaker",
				"safeguard", "safekeeping", "safety", "saffron", "sag", "saga", "sagacious", "sagacity", "sagebrush",
				"sago", "sahib", "said", "sail", "sailcloth", "sailing", "sailor", "sailplane", "saint", "sainted",
				"saintly", "saith", "sake", "saki", "salaam", "salable", "salacious", "salacity", "salad", "salamander",
				"salami", "salaried", "salary", "sale", "saleable", "saleroom", "sales", "salesclerk", "salesgirl",
				"saleslady", "salesman", "salesmanship", "salient", "saliferous", "salify", "saline", "salinometer",
				"saliva", "salivary", "salivate", "sallow", "sally", "salmon", "salmonella", "salon", "saloon",
				"salsify", "salt", "saltcellar", "saltire", "saltlick", "saltpan", "saltpeter", "saltpetre", "salts",
				"saltshaker", "saltwater", "salty", "salubrious", "salutary", "salutation", "salute", "salvage",
				"salvation", "salvationist", "salve", "salvedge", "salver", "salvia", "salvo", "samaritan",
				"samaritans", "samba", "same", "sameness", "samovar", "sampan", "sample", "sampler", "samurai",
				"sanatorium", "sanctify", "sanctimonious", "sanction", "sanctities", "sanctity", "sanctuary", "sanctum",
				"sanctus", "sand", "sandal", "sandalwood", "sandbag", "sandbank", "sandbar", "sandblast", "sandbox",
				"sandboy", "sandcastle", "sander", "sandglass", "sandman", "sandpaper", "sandpiper", "sandpit", "sands",
				"sandshoe", "sandstone", "sandstorm", "sandwich", "sandy", "sane", "sang", "sangfroid", "sangria",
				"sanguinary", "sanguine", "sanitary", "sanitation", "sanitorium", "sanity", "sank", "sans", "sanskrit",
				"sap", "sapience", "sapient", "sapless", "sapling", "sapper", "sapphic", "sapphire", "sappy", "sapwood",
				"saraband", "sarabande", "sarcasm", "sarcastic", "sarcophagus", "sardine", "sardonic", "sarge", "sari",
				"sarky", "sarong", "sarsaparilla", "sartorial", "sash", "sashay", "sass", "sassafras", "sassy", "sat",
				"satan", "satanic", "satanism", "satchel", "sate", "sateen", "satellite", "satiable", "satiate",
				"satiety", "satin", "satinwood", "satiny", "satire", "satirical", "satirise", "satirize",
				"satisfaction", "satisfactory", "satisfy", "satisfying", "satrap", "satsuma", "saturate", "saturation",
				"saturday", "saturn", "saturnalia", "saturnine", "satyr", "sauce", "saucepan", "saucer", "saucy",
				"sauerkraut", "sauna", "saunter", "saurian", "sausage", "sauterne", "sauternes", "savage", "savagery",
				"savanna", "savannah", "savant", "save", "saveloy", "saver", "saving", "savings", "savior", "saviour",
				"savor", "savory", "savour", "savoury", "savoy", "savvy", "saw", "sawbones", "sawbuck", "sawdust",
				"sawhorse", "sawmill", "sawpit", "sawyer", "saxifrage", "saxon", "saxophone", "saxophonist", "say",
				"saying", "scab", "scabbard", "scabby", "scabies", "scabious", "scabrous", "scads", "scaffold",
				"scaffolding", "scalar", "scalawag", "scald", "scalding", "scale", "scalene", "scallion", "scallop",
				"scallywag", "scalp", "scalpel", "scaly", "scamp", "scamper", "scampi", "scan", "scandal", "scandalise",
				"scandalize", "scandalmonger", "scandalous", "scandinavian", "scanner", "scansion", "scant", "scanty",
				"scapegoat", "scapegrace", "scapula", "scar", "scarab", "scarce", "scarcely", "scarcity", "scare",
				"scarecrow", "scared", "scaremonger", "scarf", "scarify", "scarlet", "scarp", "scarper", "scary",
				"scat", "scathing", "scatology", "scatter", "scatterbrain", "scatterbrained", "scattered", "scatty",
				"scavenge", "scavenger", "scenario", "scenarist", "scene", "scenery", "sceneshifter", "scenic", "scent",
				"scepter", "sceptic", "sceptical", "scepticism", "sceptre", "schedule", "schema", "schematic",
				"schematize", "scheme", "scherzo", "schism", "schismatic", "schist", "schizoid", "schizophrenia",
				"schizophrenic", "schmaltz", "schmalz", "schnapps", "schnitzel", "schnorkel", "scholar", "scholarly",
				"scholarship", "scholastic", "scholasticism", "school", "schoolboy", "schoolhouse", "schooling",
				"schoolman", "schoolmarm", "schoolmaster", "schoolmastering", "schoolmate", "schoolwork", "schooner",
				"schwa", "sciatic", "sciatica", "science", "scientific", "scientist", "scientology", "scimitar",
				"scintilla", "scintillate", "scion", "scissor", "scissors", "sclerosis", "scoff", "scold", "scollop",
				"sconce", "scone", "scoop", "scoot", "scooter", "scope", "scorbutic", "scorch", "scorcher", "scorching",
				"score", "scoreboard", "scorebook", "scorecard", "scorekeeper", "scoreless", "scorer", "scorn",
				"scorpio", "scorpion", "scotch", "scoundrel", "scoundrelly", "scour", "scourer", "scourge", "scout",
				"scoutmaster", "scow", "scowl", "scrabble", "scrag", "scraggly", "scraggy", "scram", "scramble",
				"scrap", "scrapbook", "scrape", "scraper", "scrapings", "scrappy", "scraps", "scratch", "scratchpad",
				"scratchy", "scrawl", "scrawny", "scream", "screamingly", "scree", "screech", "screed", "screen",
				"screening", "screenplay", "screw", "screwball", "screwdriver", "screwy", "scribble", "scribbler",
				"scribe", "scrimmage", "scrimp", "scrimshank", "scrimshaw", "scrip", "script", "scripted", "scriptural",
				"scripture", "scriptwriter", "scrivener", "scrofula", "scrofulous", "scroll", "scrollwork", "scrooge",
				"scrotum", "scrounge", "scrub", "scrubber", "scrubby", "scruff", "scruffy", "scrum", "scrumcap",
				"scrumhalf", "scrummage", "scrumptious", "scrumpy", "scrunch", "scruple", "scrupulous", "scrutineer",
				"scrutinise", "scrutinize", "scrutiny", "scuba", "scud", "scuff", "scuffle", "scull", "scullery",
				"scullion", "sculptor", "sculptural", "sculpture", "scum", "scupper", "scurf", "scurrility",
				"scurrilous", "scurry", "scurvy", "scut", "scutcheon", "scuttle", "scylla", "scythe", "sea", "seabed",
				"seabird", "seaboard", "seaborne", "seafaring", "seafood", "seafront", "seagirt", "seagoing", "seagull",
				"seahorse", "seakale", "seal", "sealer", "sealing", "sealskin", "sealyham", "seam", "seaman",
				"seamanlike", "seamanship", "seamstress", "seamy", "seaplane", "seaport", "sear", "search", "searching",
				"searchlight", "searing", "seascape", "seashell", "seashore", "seasick", "seaside", "season",
				"seasonable", "seasonal", "seasoning", "seat", "seating", "seawall", "seaward", "seawards", "seawater",
				"seaway", "seaweed", "seaworthy", "sec", "secateurs", "secede", "secession", "seclude", "secluded",
				"seclusion", "seclusive", "second", "secondary", "seconds", "secrecy", "secret", "secretarial",
				"secretariat", "secretary", "secrete", "secretion", "secretive", "sect", "sectarian", "section",
				"sectional", "sectionalism", "sector", "secular", "secularise", "secularism", "secularize", "secure",
				"security", "sedan", "sedate", "sedation", "sedative", "sedentary", "sedge", "sediment", "sedimentary",
				"sedimentation", "sedition", "seditious", "seduce", "seduction", "seductive", "sedulous", "see", "seed",
				"seedbed", "seedcake", "seedling", "seedsman", "seedy", "seeing", "seek", "seem", "seeming",
				"seemingly", "seemly", "seen", "seep", "seepage", "seer", "seersucker", "seesaw", "seethe", "segment",
				"segmentation", "segregate", "segregated", "segregation", "seigneur", "seine", "seismic", "seismograph",
				"seismology", "seize", "seizure", "seldom", "select", "selection", "selective", "selector", "selenium",
				"self", "selfish", "selfless", "selfsame", "sell", "seller", "sellotape", "selvage", "selves",
				"semantic", "semantics", "semaphore", "semblance", "semeiology", "semen", "semester", "semibreve",
				"semicircle", "semicolon", "semiconductor", "semidetached", "semifinal", "semifinalist", "seminal",
				"seminar", "seminarist", "seminary", "semiology", "semiprecious", "semiquaver", "semitic", "semitone",
				"semitropical", "semivowel", "semiweekly", "semolina", "sempstress", "sen", "senate", "senator",
				"senatorial", "send", "sender", "senescence", "senescent", "seneschal", "senile", "senility", "senior",
				"seniority", "senna", "sensation", "sensational", "sensationalism", "sense", "senseless", "senses",
				"sensibility", "sensible", "sensitise", "sensitive", "sensitivity", "sensitize", "sensor", "sensory",
				"sensual", "sensualist", "sensuality", "sensuous", "sent", "sentence", "sententious", "sentient",
				"sentiment", "sentimental", "sentimentalise", "sentimentalism", "sentimentality", "sentimentalize",
				"sentinel", "sentry", "sepal", "separable", "separate", "separation", "separatism", "separator",
				"sepia", "sepoy", "sepsis", "september", "septet", "septic", "septicaemia", "septicemia",
				"septuagenarian", "septuagesima", "septuagint", "sepulcher", "sepulchral", "sepulchre", "sequel",
				"sequence", "sequencing", "sequent", "sequential", "sequester", "sequestrate", "sequestration",
				"sequin", "sequoia", "seraglio", "seraph", "seraphic", "sere", "serenade", "serendipity", "serene",
				"serf", "serfdom", "serge", "sergeant", "serial", "serialise", "serialize", "seriatim", "sericulture",
				"series", "serif", "seriocomic", "serious", "seriously", "sermon", "sermonise", "sermonize", "serous",
				"serpent", "serpentine", "serrated", "serried", "serum", "serval", "servant", "serve", "server",
				"servery", "service", "serviceable", "serviceman", "serviette", "servile", "serving", "servitor",
				"servitude", "servomechanism", "servomotor", "sesame", "session", "sessions", "set", "setback",
				"setscrew", "setsquare", "sett", "settee", "setter", "setting", "settle", "settled", "settlement",
				"settler", "seven", "seventeen", "seventy", "sever", "several", "severally", "severance", "severity",
				"sew", "sewage", "sewer", "sewerage", "sewing", "sex", "sexagenarian", "sexagesima", "sexism", "sexist",
				"sexless", "sextant", "sextet", "sexton", "sextuplet", "sexual", "sexuality", "sexy", "sforzando",
				"sgt", "shabby", "shack", "shackle", "shad", "shade", "shades", "shading", "shadow", "shadowbox",
				"shadowy", "shady", "shaft", "shag", "shagged", "shaggy", "shagreen", "shah", "shake", "shakedown",
				"shaker", "shakes", "shako", "shaky", "shale", "shall", "shallop", "shallot", "shallow", "shallows",
				"shalom", "shalt", "sham", "shaman", "shamble", "shambles", "shame", "shamefaced", "shameful",
				"shameless", "shammy", "shampoo", "shamrock", "shandy", "shanghai", "shank", "shantung", "shanty",
				"shantytown", "shape", "shaped", "shapely", "shard", "share", "sharecropper", "shareholder", "shares",
				"shark", "sharkskin", "sharp", "sharpen", "sharpener", "sharper", "sharpshooter", "shatter", "shave",
				"shaver", "shaving", "shawl", "shay", "she", "sheaf", "shear", "shears", "sheath", "sheathe",
				"sheathing", "shebang", "shebeen", "shed", "sheen", "sheep", "sheepdip", "sheepdog", "sheepfold",
				"sheepish", "sheepskin", "sheer", "sheet", "sheeting", "sheik", "sheikdom", "sheikh", "sheikhdom",
				"sheila", "shekels", "shelduck", "shelf", "shell", "shellac", "shellacking", "shellfish", "shellshock",
				"shelter", "sheltered", "shelve", "shelves", "shelving", "shenanigan", "shepherd", "shepherdess",
				"sheraton", "sherbet", "sherd", "sheriff", "sherpa", "sherry", "shew", "shh", "shibboleth", "shield",
				"shift", "shiftless", "shifty", "shilling", "shimmer", "shin", "shinbone", "shindig", "shindy", "shine",
				"shiner", "shingle", "shingles", "shining", "shinny", "shinto", "shiny", "ship", "shipboard",
				"shipbroker", "shipbuilding", "shipmate", "shipment", "shipper", "shipping", "shipshape", "shipwreck",
				"shipwright", "shipyard", "shire", "shires", "shirk", "shirring", "shirt", "shirtfront", "shirting",
				"shirtsleeve", "shirttail", "shirtwaist", "shirtwaister", "shirty", "shit", "shits", "shitty", "shiver",
				"shivers", "shivery", "shoal", "shock", "shocker", "shockheaded", "shocking", "shockproof", "shod",
				"shoddy", "shoe", "shoeblack", "shoehorn", "shoelace", "shoemaker", "shoeshine", "shoestring", "shone",
				"shoo", "shook", "shoot", "shop", "shopkeeper", "shoplift", "shopsoiled", "shopworn", "shore", "shorn",
				"short", "shortage", "shortbread", "shortcake", "shortcoming", "shorten", "shortening", "shortfall",
				"shorthand", "shorthanded", "shorthorn", "shortie", "shortly", "shorts", "shortsighted", "shorty",
				"shot", "shotgun", "should", "shoulder", "shouldst", "shout", "shouting", "shove", "shovel",
				"shovelboard", "show", "showboat", "showcase", "showdown", "shower", "showery", "showgirl", "showing",
				"showman", "showmanship", "shown", "showpiece", "showplace", "showroom", "showy", "shrank", "shrapnel",
				"shred", "shredder", "shrew", "shrewd", "shrewish", "shriek", "shrift", "shrike", "shrill", "shrimp",
				"shrine", "shrink", "shrinkage", "shrive", "shrivel", "shroud", "shrub", "shrubbery", "shrug", "shuck",
				"shucks", "shudder", "shuffle", "shuffleboard", "shufty", "shun", "shunt", "shunter", "shush", "shut",
				"shutdown", "shutter", "shuttle", "shuttlecock", "shy", "shyster", "sibilant", "sibling", "sibyl",
				"sibylline", "sic", "sick", "sickbay", "sickbed", "sicken", "sickening", "sickle", "sickly", "sickness",
				"sickroom", "side", "sidearm", "sideboard", "sideboards", "sidecar", "sidekick", "sidelight",
				"sideline", "sidelong", "sidereal", "sidesaddle", "sideshow", "sideslip", "sidesman", "sidesplitting",
				"sidestep", "sidestroke", "sideswipe", "sidetrack", "sidewalk", "sideward", "sidewards", "sideways",
				"siding", "sidle", "siege", "sienna", "sierra", "siesta", "sieve", "sift", "sifter", "sigh", "sight",
				"sighted", "sightless", "sightly", "sightscreen", "sightsee", "sightseer", "sign", "signal", "signaler",
				"signalise", "signalize", "signaller", "signally", "signalman", "signatory", "signature", "signer",
				"signet", "significance", "significant", "signification", "signify", "signor", "signora", "signorina",
				"signpost", "signposted", "silage", "silence", "silencer", "silent", "silhouette", "silica", "silicate",
				"silicon", "silicone", "silicosis", "silk", "silken", "silkworm", "silky", "sill", "sillabub", "silly",
				"silo", "silt", "silvan", "silver", "silverfish", "silverside", "silversmith", "silverware", "silvery",
				"simian", "similar", "similarity", "similarly", "simile", "similitude", "simmer", "simony", "simper",
				"simple", "simpleton", "simplicity", "simplify", "simply", "simulacrum", "simulate", "simulated",
				"simulation", "simulator", "simultaneous", "sin", "since", "sincere", "sincerely", "sincerity",
				"sinecure", "sinew", "sinewy", "sinful", "sing", "singe", "singhalese", "singing", "single",
				"singleness", "singles", "singlestick", "singlet", "singleton", "singly", "singsong", "singular",
				"singularly", "sinhalese", "sinister", "sink", "sinker", "sinless", "sinner", "sinology", "sinuous",
				"sinus", "sip", "siphon", "sir", "sire", "siren", "sirloin", "sirocco", "sirrah", "sis", "sisal",
				"sissy", "sister", "sisterhood", "sisterly", "sit", "sitar", "site", "sitter", "sitting", "situated",
				"situation", "six", "sixpence", "sixteen", "sixty", "sizable", "size", "sizeable", "sizzle", "sizzler",
				"skate", "skateboard", "skedaddle", "skeet", "skein", "skeleton", "skeptic", "skeptical", "skepticism",
				"sketch", "sketchpad", "sketchy", "skew", "skewbald", "skewer", "ski", "skibob", "skid", "skidlid",
				"skidpan", "skiff", "skiffle", "skilful", "skill", "skilled", "skillet", "skillful", "skim", "skimmer",
				"skimp", "skimpy", "skin", "skinflint", "skinful", "skinhead", "skinny", "skint", "skip", "skipper",
				"skirl", "skirmish", "skirt", "skit", "skitter", "skittish", "skittle", "skittles", "skive", "skivvy",
				"skua", "skulduggery", "skulk", "skull", "skullcap", "skullduggery", "skunk", "sky", "skydiving",
				"skyhook", "skyjack", "skylark", "skylight", "skyline", "skyrocket", "skyscraper", "skywriting", "slab",
				"slack", "slacken", "slacker", "slacks", "slag", "slagheap", "slain", "slake", "slalom", "slam",
				"slander", "slanderous", "slang", "slangy", "slant", "slantwise", "slap", "slapdash", "slaphappy",
				"slapstick", "slash", "slat", "slate", "slattern", "slaty", "slaughter", "slaughterhouse", "slave",
				"slaver", "slavery", "slavic", "slavish", "slay", "sleazy", "sled", "sledge", "sledgehammer", "sleek",
				"sleep", "sleeper", "sleepless", "sleepwalker", "sleepy", "sleepyhead", "sleet", "sleeve", "sleigh",
				"slender", "slenderise", "slenderize", "slept", "sleuth", "slew", "slewed", "slice", "slick", "slicker",
				"slide", "slight", "slightly", "slim", "slimy", "sling", "slingshot", "slink", "slip", "slipcover",
				"slipknot", "slipover", "slipper", "slippery", "slippy", "slips", "slipshod", "slipstream", "slipway",
				"slit", "slither", "slithery", "sliver", "slivovitz", "slob", "slobber", "sloe", "slog", "slogan",
				"sloop", "slop", "slope", "sloppy", "slosh", "sloshed", "slot", "sloth", "slothful", "slouch", "slough",
				"sloven", "slovenly", "slow", "slowcoach", "slowworm", "sludge", "slue", "slug", "sluggard", "sluggish",
				"sluice", "sluiceway", "slum", "slumber", "slumberous", "slummy", "slump", "slung", "slunk", "slur",
				"slurp", "slurry", "slush", "slut", "sly", "smack", "smacker", "small", "smallholder", "smallholding",
				"smallpox", "smalls", "smarmy", "smart", "smarten", "smash", "smashed", "smasher", "smashing",
				"smattering", "smear", "smell", "smelly", "smelt", "smile", "smirch", "smirk", "smite", "smith",
				"smithereens", "smithy", "smitten", "smock", "smocking", "smog", "smoke", "smoker", "smokescreen",
				"smokestack", "smoking", "smoky", "smolder", "smooch", "smooth", "smoothie", "smoothy", "smorgasbord",
				"smote", "smother", "smoulder", "smudge", "smug", "smuggle", "smut", "smutty", "snack", "snaffle",
				"snag", "snail", "snake", "snakebite", "snaky", "snap", "snapdragon", "snapper", "snappish", "snappy",
				"snapshot", "snare", "snarl", "snatch", "snazzy", "sneak", "sneaker", "sneaking", "sneaky", "sneer",
				"sneeze", "snick", "snicker", "snide", "sniff", "sniffle", "sniffles", "sniffy", "snifter", "snigger",
				"snip", "snippet", "snips", "snitch", "snivel", "snob", "snobbery", "snobbish", "snog", "snood",
				"snook", "snooker", "snoop", "snooper", "snoot", "snooty", "snooze", "snore", "snorkel", "snort",
				"snorter", "snot", "snotty", "snout", "snow", "snowball", "snowberry", "snowbound", "snowdrift",
				"snowdrop", "snowfall", "snowfield", "snowflake", "snowline", "snowman", "snowplough", "snowplow",
				"snowshoe", "snowstorm", "snowy", "snr", "snub", "snuff", "snuffer", "snuffle", "snug", "snuggle",
				"soak", "soaked", "soaking", "soap", "soapbox", "soapstone", "soapsuds", "soapy", "soar", "sob",
				"sober", "sobriety", "sobriquet", "soccer", "sociable", "social", "socialise", "socialism", "socialist",
				"socialite", "socialize", "society", "sociology", "sock", "socket", "sod", "soda", "sodden", "sodium",
				"sodomite", "sodomy", "soever", "sofa", "soft", "softball", "soften", "softhearted", "softie",
				"software", "softwood", "softy", "soggy", "soigne", "soignee", "soil", "sojourn", "sol", "solace",
				"solar", "solarium", "sold", "solder", "soldier", "soldierly", "soldiery", "sole", "solecism", "solely",
				"solemn", "solemnise", "solemnity", "solemnize", "solicit", "solicitor", "solicitous", "solicitude",
				"solid", "solidarity", "solidify", "solidity", "solidus", "soliloquise", "soliloquize", "soliloquy",
				"solipsism", "solitaire", "solitary", "solitude", "solo", "soloist", "solstice", "soluble", "solution",
				"solve", "solvency", "solvent", "somber", "sombre", "sombrero", "some", "somebody", "someday",
				"somehow", "somersault", "something", "sometime", "sometimes", "someway", "somewhat", "somewhere",
				"somnambulism", "somnolent", "son", "sonar", "sonata", "song", "songbird", "songbook", "songster",
				"sonic", "sonnet", "sonny", "sonority", "sonorous", "sonsy", "soon", "soot", "soothe", "soothsayer",
				"sop", "sophism", "sophisticate", "sophisticated", "sophistication", "sophistry", "sophomore",
				"soporific", "sopping", "soppy", "soprano", "sorbet", "sorcerer", "sorcery", "sordid", "sore",
				"sorehead", "sorely", "sorghum", "sorority", "sorrel", "sorrow", "sorry", "sort", "sortie", "sos",
				"sot", "sottish", "sou", "soubrette", "soubriquet", "sough", "sought", "soul", "soulful", "soulless",
				"sound", "soundings", "soundproof", "soundtrack", "soup", "sour", "source", "sourdough", "sourpuss",
				"sousaphone", "souse", "soused", "south", "southbound", "southeast", "southeaster", "southeasterly",
				"southeastern", "southeastward", "southeastwards", "southerly", "southern", "southerner",
				"southernmost", "southpaw", "southward", "southwards", "southwest", "southwester", "southwesterly",
				"southwestern", "southwestward", "southwestwards", "souvenir", "sovereign", "sovereignty", "soviet",
				"sow", "sox", "soy", "soybean", "sozzled", "spa", "space", "spacecraft", "spaceship", "spacesuit",
				"spacing", "spacious", "spade", "spadework", "spaghetti", "spake", "spam", "span", "spangle", "spaniel",
				"spank", "spanking", "spanner", "spar", "spare", "spareribs", "sparing", "spark", "sparkle", "sparkler",
				"sparks", "sparrow", "sparse", "spartan", "spasm", "spasmodic", "spastic", "spat", "spatchcock",
				"spate", "spatial", "spatter", "spatula", "spavin", "spawn", "spay", "speak", "speakeasy", "speaker",
				"speakership", "spear", "spearhead", "spearmint", "spec", "special", "specialise", "specialised",
				"specialist", "speciality", "specialize", "specialized", "specially", "specie", "species", "specific",
				"specifically", "specification", "specifics", "specify", "specimen", "specious", "speck", "speckle",
				"spectacle", "spectacled", "spectacles", "spectacular", "spectator", "specter", "spectral", "spectre",
				"spectroscope", "spectrum", "speculate", "speculation", "speculative", "speech", "speechify",
				"speechless", "speed", "speedboat", "speeding", "speedometer", "speedway", "speedwell", "speedy",
				"spelaeology", "speleology", "spell", "spellbind", "spelling", "spend", "spender", "spendthrift",
				"spent", "sperm", "spermaceti", "spermatozoa", "spew", "sphagnum", "sphere", "spherical", "spheroid",
				"sphincter", "sphinx", "spice", "spicy", "spider", "spidery", "spiel", "spigot", "spike", "spikenard",
				"spiky", "spill", "spillover", "spillway", "spin", "spinach", "spinal", "spindle", "spindly", "spine",
				"spineless", "spinet", "spinnaker", "spinner", "spinney", "spinster", "spiny", "spiral", "spire",
				"spirit", "spirited", "spiritless", "spirits", "spiritual", "spiritualise", "spiritualism",
				"spirituality", "spiritualize", "spirituous", "spirt", "spit", "spite", "spitfire", "spittle",
				"spittoon", "spiv", "splash", "splashy", "splat", "splatter", "splay", "splayfoot", "spleen",
				"splendid", "splendiferous", "splendor", "splendour", "splenetic", "splice", "splicer", "splint",
				"splinter", "split", "splits", "splitting", "splotch", "splurge", "splutter", "spoil", "spoilage",
				"spoils", "spoilsport", "spoke", "spoken", "spokeshave", "spokesman", "spoliation", "spondee", "sponge",
				"spongy", "sponsor", "spontaneous", "spoof", "spook", "spooky", "spool", "spoon", "spoonerism",
				"spoonful", "spoor", "sporadic", "spore", "sporran", "sport", "sporting", "sportive", "sports",
				"sportsman", "sportsmanlike", "sportsmanship", "sporty", "spot", "spotless", "spotlight", "spotted",
				"spotter", "spotty", "spouse", "spout", "sprain", "sprang", "sprat", "sprawl", "spray", "sprayer",
				"spread", "spree", "sprig", "sprigged", "sprightly", "spring", "springboard", "springbok", "springtime",
				"springy", "sprinkle", "sprinkler", "sprinkling", "sprint", "sprite", "sprocket", "sprout", "spruce",
				"sprung", "spry", "spud", "spume", "spun", "spunk", "spur", "spurious", "spurn", "spurt", "sputter",
				"sputum", "spy", "spyglass", "squab", "squabble", "squad", "squadron", "squalid", "squall", "squalor",
				"squander", "square", "squash", "squashy", "squat", "squatter", "squaw", "squawk", "squeak", "squeaky",
				"squeal", "squeamish", "squeegee", "squeeze", "squeezer", "squelch", "squib", "squid", "squidgy",
				"squiffy", "squiggle", "squint", "squirarchy", "squire", "squirearchy", "squirm", "squirrel", "squirt",
				"squirter", "sri", "srn", "ssh", "stab", "stabbing", "stabilise", "stabiliser", "stability",
				"stabilize", "stabilizer", "stable", "stabling", "staccato", "stack", "stadium", "staff", "stag",
				"stage", "stagecoach", "stager", "stagestruck", "stagger", "staggering", "staggers", "staging",
				"stagnant", "stagnate", "stagy", "staid", "stain", "stainless", "stair", "staircase", "stairs",
				"stairwell", "stake", "stakeholder", "stakes", "stalactite", "stalagmite", "stale", "stalemate",
				"stalk", "stall", "stallholder", "stallion", "stalls", "stalwart", "stamen", "stamina", "stammer",
				"stamp", "stampede", "stance", "stanch", "stanchion", "stand", "standard", "standardise", "standardize",
				"standby", "standing", "standoffish", "standpipe", "standpoint", "standstill", "stank", "stanza",
				"staple", "stapler", "star", "starboard", "starch", "starchy", "stardom", "stardust", "stare",
				"starfish", "stargazer", "stargazing", "staring", "stark", "starkers", "starlet", "starlight",
				"starling", "starlit", "starry", "stars", "start", "starter", "starters", "startle", "starvation",
				"starve", "starveling", "stash", "state", "statecraft", "statehood", "stateless", "stately",
				"statement", "stateroom", "states", "stateside", "statesman", "static", "statics", "station",
				"stationary", "stationer", "stationery", "stationmaster", "statistic", "statistician", "statistics",
				"statuary", "statue", "statuesque", "statuette", "stature", "status", "statute", "statutory", "staunch",
				"stave", "staves", "stay", "stayer", "stays", "std", "stead", "steadfast", "steady", "steak", "steal",
				"stealth", "stealthy", "steam", "steamboat", "steamer", "steamroller", "steamship", "steed", "steel",
				"steelworker", "steelworks", "steely", "steelyard", "steenbok", "steep", "steepen", "steeple",
				"steeplechase", "steeplejack", "steer", "steerage", "steerageway", "steersman", "stein", "steinbok",
				"stele", "stellar", "stem", "stench", "stencil", "stenographer", "stenography", "stentorian", "step",
				"stepbrother", "stepchild", "stepladder", "stepparent", "steps", "stepsister", "stereo", "stereoscope",
				"stereoscopic", "stereotype", "sterile", "sterilise", "sterility", "sterilize", "sterling", "stern",
				"sternum", "steroid", "stertorous", "stet", "stethoscope", "stetson", "stevedore", "stew", "steward",
				"stewardess", "stewardship", "stewed", "stick", "sticker", "stickleback", "stickler", "stickpin",
				"sticks", "sticky", "stiff", "stiffen", "stiffener", "stiffening", "stifle", "stigma", "stigmata",
				"stigmatise", "stigmatize", "stile", "stiletto", "still", "stillbirth", "stillborn", "stillroom",
				"stilly", "stilt", "stilted", "stilton", "stimulant", "stimulate", "stimulus", "sting", "stinger",
				"stingo", "stingray", "stingy", "stink", "stinking", "stint", "stipend", "stipendiary", "stipple",
				"stipulate", "stipulation", "stir", "stirrer", "stirring", "stirrup", "stitch", "stoat", "stock",
				"stockade", "stockbreeder", "stockbroker", "stockcar", "stockfish", "stockholder", "stockily",
				"stockinet", "stockinette", "stocking", "stockist", "stockjobber", "stockman", "stockpile", "stockpot",
				"stockroom", "stocks", "stocktaking", "stocky", "stockyard", "stodge", "stodgy", "stoic", "stoical",
				"stoicism", "stoke", "stokehold", "stoker", "stole", "stolen", "stolid", "stomach", "stomachache",
				"stomachful", "stomp", "stone", "stonebreaker", "stonecutter", "stoned", "stoneless", "stonemason",
				"stonewall", "stoneware", "stonework", "stony", "stood", "stooge", "stool", "stoolpigeon", "stoop",
				"stop", "stopcock", "stopgap", "stopover", "stoppage", "stopper", "stopping", "stopwatch", "storage",
				"store", "storehouse", "storekeeper", "storeroom", "stores", "storey", "storied", "stork", "storm",
				"stormbound", "stormy", "story", "storybook", "storyteller", "stoup", "stout", "stouthearted", "stove",
				"stovepipe", "stow", "stowage", "stowaway", "straddle", "stradivarius", "strafe", "straggle",
				"straggly", "straight", "straightaway", "straightedge", "straighten", "straightforward", "straightway",
				"strain", "strained", "strainer", "strait", "straitened", "straitjacket", "straitlaced", "straits",
				"strand", "stranded", "strange", "stranger", "strangle", "stranglehold", "strangulate", "strangulation",
				"strap", "straphanging", "strapless", "strapping", "strata", "stratagem", "strategic", "strategist",
				"strategy", "stratification", "stratify", "stratosphere", "stratum", "straw", "strawberry",
				"strawboard", "stray", "streak", "streaker", "streaky", "stream", "streamer", "streamline",
				"streamlined", "street", "streetcar", "streetwalker", "strength", "strengthen", "strenuous",
				"streptococcus", "streptomycin", "stress", "stretch", "stretcher", "stretchy", "strew", "strewth",
				"striated", "striation", "stricken", "strict", "stricture", "stride", "stridency", "strident",
				"stridulate", "strife", "strike", "strikebound", "strikebreaker", "strikebreaking", "striker",
				"striking", "string", "stringency", "stringent", "strings", "stringy", "strip", "stripe", "striped",
				"stripling", "stripper", "striptease", "stripy", "strive", "strode", "stroke", "stroll", "stroller",
				"strolling", "strong", "strongarm", "strongbox", "stronghold", "strontium", "strop", "strophe",
				"stroppy", "strove", "struck", "structural", "structure", "strudel", "struggle", "strum", "strumpet",
				"strung", "strut", "strychnine", "stub", "stubble", "stubborn", "stubby", "stucco", "stuck", "stud",
				"studbook", "student", "studied", "studio", "studious", "study", "stuff", "stuffing", "stuffy",
				"stultify", "stumble", "stump", "stumper", "stumpy", "stun", "stung", "stunk", "stunner", "stunning",
				"stunt", "stupefaction", "stupefy", "stupendous", "stupid", "stupidity", "stupor", "sturdy", "sturgeon",
				"stutter", "sty", "stye", "stygian", "style", "stylise", "stylish", "stylist", "stylistic",
				"stylistics", "stylize", "stylus", "stymie", "styptic", "suasion", "suave", "sub", "subaltern",
				"subatomic", "subcommittee", "subconscious", "subcontinent", "subcontract", "subcontractor",
				"subcutaneous", "subdivide", "subdue", "subdued", "subedit", "subeditor", "subheading", "subhuman",
				"subject", "subjection", "subjective", "subjoin", "subjugate", "subjunctive", "sublease", "sublet",
				"sublieutenant", "sublimate", "sublime", "subliminal", "submarine", "submariner", "submerge",
				"submergence", "submersible", "submission", "submissive", "submit", "subnormal", "suborbital",
				"subordinate", "suborn", "subplot", "subpoena", "subscribe", "subscriber", "subscription", "subsequent",
				"subservience", "subservient", "subside", "subsidence", "subsidiary", "subsidise", "subsidize",
				"subsidy", "subsist", "subsistence", "subsoil", "subsonic", "substance", "substandard", "substantial",
				"substantially", "substantiate", "substantival", "substantive", "substation", "substitute",
				"substratum", "substructure", "subsume", "subtenant", "subtend", "subterfuge", "subterranean",
				"subtitle", "subtitles", "subtle", "subtlety", "subtopia", "subtract", "subtraction", "subtropical",
				"suburb", "suburban", "suburbanite", "suburbia", "suburbs", "subvention", "subversive", "subvert",
				"subway", "succeed", "success", "successful", "succession", "successive", "successor", "succinct",
				"succor", "succour", "succubus", "succulence", "succulent", "succumb", "such", "suchlike", "suck",
				"sucker", "suckle", "suckling", "sucrose", "suction", "sudden", "suds", "sue", "suet", "suffer",
				"sufferable", "sufferance", "sufferer", "suffering", "suffice", "sufficiency", "sufficient", "suffix",
				"suffocate", "suffragan", "suffrage", "suffragette", "suffuse", "sugar", "sugarcane", "sugarcoated",
				"sugarloaf", "sugary", "suggest", "suggestible", "suggestion", "suggestive", "suicidal", "suicide",
				"suit", "suitability", "suitable", "suitcase", "suiting", "suitor", "sulfate", "sulfide", "sulfur",
				"sulfuret", "sulfurous", "sulk", "sulks", "sulky", "sullen", "sully", "sulphate", "sulphide", "sulphur",
				"sulphuret", "sulphurous", "sultan", "sultana", "sultanate", "sultry", "sum", "sumac", "sumach",
				"summarise", "summarize", "summary", "summat", "summation", "summer", "summerhouse", "summertime",
				"summery", "summit", "summon", "summons", "sump", "sumptuary", "sumptuous", "sun", "sunbaked",
				"sunbathe", "sunbeam", "sunblind", "sunbonnet", "sunburn", "sunburnt", "sundae", "sunday", "sundeck",
				"sunder", "sundew", "sundial", "sundown", "sundowner", "sundrenched", "sundries", "sundry", "sunfish",
				"sunflower", "sung", "sunglasses", "sunk", "sunken", "sunlamp", "sunless", "sunlight", "sunlit",
				"sunny", "sunray", "sunrise", "sunroof", "sunset", "sunshade", "sunshine", "sunspot", "sunstroke",
				"suntan", "suntrap", "sup", "super", "superabundance", "superabundant", "superannuate", "superannuated",
				"superannuation", "superb", "supercharged", "supercharger", "supercilious", "superconductivity",
				"superduper", "superego", "superficial", "superficies", "superfine", "superfluity", "superfluous",
				"superhuman", "superimpose", "superintend", "superintendent", "superior", "superlative",
				"superlatively", "superman", "supermarket", "supernal", "supernatural", "supernova", "supernumerary",
				"superscription", "supersede", "supersession", "supersonic", "superstar", "superstition",
				"superstitious", "superstructure", "supertax", "supervene", "supervise", "supervisory", "supine",
				"supper", "supplant", "supple", "supplement", "supplementary", "suppliant", "supplicant", "supplicate",
				"supplier", "supplies", "supply", "support", "supportable", "supporter", "supportive", "suppose",
				"supposed", "supposedly", "supposing", "supposition", "suppository", "suppress", "suppression",
				"suppressive", "suppressor", "suppurate", "supranational", "supremacist", "supremacy", "supreme",
				"surcharge", "surcoat", "surd", "sure", "surefire", "surefooted", "surely", "surety", "surf", "surface",
				"surfboard", "surfboat", "surfeit", "surfer", "surge", "surgeon", "surgery", "surgical", "surly",
				"surmise", "surmount", "surname", "surpass", "surpassing", "surplice", "surplus", "surprise",
				"surprising", "surreal", "surrealism", "surrealist", "surrealistic", "surrender", "surreptitious",
				"surrey", "surrogate", "surround", "surrounding", "surroundings", "surtax", "surveillance", "survey",
				"surveyor", "survival", "survive", "survivor", "susceptibilities", "susceptibility", "susceptible",
				"suspect", "suspend", "suspender", "suspenders", "suspense", "suspension", "suspicion", "suspicious",
				"sustain", "sustenance", "suttee", "suture", "suzerain", "suzerainty", "svelte", "swab", "swaddle",
				"swag", "swagger", "swain", "swallow", "swallowtailed", "swam", "swami", "swamp", "swampy", "swan",
				"swank", "swanky", "swansdown", "swansong", "swap", "sward", "swarf", "swarm", "swarthy",
				"swashbuckler", "swashbuckling", "swastika", "swat", "swatch", "swath", "swathe", "swatter", "sway",
				"swayback", "swear", "swearword", "sweat", "sweatband", "sweated", "sweater", "sweatshirt", "sweatshop",
				"sweaty", "swede", "sweep", "sweeper", "sweeping", "sweepings", "sweepstake", "sweepstakes", "sweet",
				"sweetbread", "sweetbriar", "sweetbrier", "sweeten", "sweetener", "sweetening", "sweetheart", "sweetie",
				"sweetish", "sweetmeat", "sweets", "swell", "swelling", "swelter", "sweltering", "swept", "swerve",
				"swift", "swig", "swill", "swim", "swimming", "swimmingly", "swindle", "swine", "swineherd", "swing",
				"swingeing", "swinger", "swinging", "swinish", "swipe", "swirl", "swish", "switch", "switchback",
				"switchblade", "switchboard", "switchgear", "switchman", "swivel", "swiz", "swizzle", "swollen",
				"swoon", "swoop", "swop", "sword", "swordfish", "swordplay", "swordsman", "swordsmanship", "swordstick",
				"swore", "sworn", "swot", "swum", "swung", "sybarite", "sybaritic", "sycamore", "sycophant",
				"sycophantic", "sylabub", "syllabary", "syllabic", "syllabify", "syllable", "syllabub", "syllabus",
				"syllogism", "syllogistic", "sylph", "sylphlike", "sylvan", "symbiosis", "symbol", "symbolic",
				"symbolise", "symbolism", "symbolist", "symbolize", "symmetrical", "symmetry", "sympathetic",
				"sympathies", "sympathise", "sympathize", "sympathy", "symphonic", "symphony", "symposium", "symptom",
				"symptomatic", "synagogue", "sync", "synch", "synchonise", "synchromesh", "synchronize", "synchrotron",
				"syncopate", "syncope", "syndic", "syndicalism", "syndicate", "syndrome", "synod", "synonym",
				"synonymous", "synopsis", "synoptic", "syntactic", "syntax", "synthesis", "synthesise", "synthesiser",
				"synthesize", "synthesizer", "synthetic", "syphilis", "syphilitic", "syphon", "syringe", "syrup",
				"syrupy", "system", "systematic", "systematise", "systematize", "systemic", "tab", "tabard", "tabasco",
				"tabby", "tabernacle", "table", "tableau", "tablecloth", "tableland", "tablemat", "tablespoon",
				"tablespoonful", "tablet", "tableware", "tabloid", "taboo", "tabor", "tabular", "tabulate", "tabulator",
				"tacit", "taciturn", "tack", "tackiness", "tackle", "tacky", "tact", "tactic", "tactical", "tactician",
				"tactics", "tactile", "tactual", "tadpole", "taffeta", "taffrail", "taffy", "tag", "tail", "tailback",
				"tailboard", "tailcoat", "taillight", "tailor", "tailpiece", "tails", "tailspin", "tailwind", "taint",
				"take", "takeaway", "takeoff", "takeover", "taking", "takings", "talc", "tale", "talebearer", "talent",
				"talented", "talisman", "talk", "talkative", "talker", "talkie", "talks", "tall", "tallboy", "tallow",
				"tally", "tallyho", "tallyman", "talmud", "talon", "tamale", "tamarind", "tamarisk", "tambour",
				"tambourine", "tame", "tammany", "tamp", "tamper", "tampon", "tan", "tandem", "tang", "tangent",
				"tangential", "tangerine", "tangible", "tangle", "tango", "tank", "tankard", "tanker", "tanner",
				"tannery", "tannin", "tanning", "tannoy", "tansy", "tantalise", "tantalize", "tantalus", "tantamount",
				"tantrum", "taoism", "tap", "tape", "taper", "tapestry", "tapeworm", "tapioca", "tapir", "tappet",
				"taproom", "taproot", "taps", "tar", "tarantella", "tarantula", "tarboosh", "tardy", "target", "tariff",
				"tarmac", "tarn", "tarnish", "taro", "tarot", "tarpaulin", "tarragon", "tarry", "tarsal", "tarsus",
				"tart", "tartan", "tartar", "task", "taskmaster", "tassel", "taste", "tasteful", "tasteless", "taster",
				"tasty", "tat", "tatas", "tatter", "tattered", "tatters", "tatting", "tattle", "tattoo", "tattooist",
				"tatty", "taught", "taunt", "taurus", "taut", "tautological", "tautology", "tavern", "tawdry", "tawny",
				"tawse", "tax", "taxation", "taxi", "taxidermist", "taxidermy", "taximeter", "taxonomy", "tea",
				"teabag", "teacake", "teach", "teacher", "teaching", "teacup", "teacupful", "teagarden", "teahouse",
				"teak", "teakettle", "teal", "tealeaf", "team", "teamster", "teamwork", "teapot", "tear", "tearaway",
				"teardrop", "tearful", "teargas", "tearjerker", "tearless", "tearoom", "tease", "teasel", "teaser",
				"teaspoon", "teaspoonful", "teat", "teatime", "teazle", "tech", "technical", "technicality",
				"technician", "technique", "technocracy", "technocrat", "technological", "technologist", "technology",
				"techy", "tedious", "tedium", "tee", "teem", "teeming", "teenage", "teenager", "teens", "teenybopper",
				"teeter", "teeth", "teethe", "teetotal", "teetotaler", "teetotaller", "teflon", "tegument", "tele",
				"telecast", "telecommunications", "telegram", "telegraph", "telegrapher", "telegraphese", "telegraphic",
				"telemarketing", "telemeter", "telemetry", "teleology", "telepathic", "telepathist", "telepathy",
				"telephone", "telephonist", "telephony", "telephotograph", "telephotography", "teleprinter",
				"teleprompter", "telescope", "telescopic", "televise", "television", "televisual", "telex", "telfer",
				"tell", "teller", "telling", "telltale", "telly", "telpher", "telstar", "temerity", "temp", "temper",
				"tempera", "temperament", "temperamental", "temperance", "temperate", "temperature", "tempest",
				"tempestuous", "template", "temple", "templet", "tempo", "temporal", "temporary", "temporise",
				"temporize", "tempt", "temptation", "ten", "tenable", "tenacious", "tenacity", "tenancy", "tenant",
				"tenantry", "tench", "tend", "tendency", "tendentious", "tender", "tenderfoot", "tenderhearted",
				"tenderise", "tenderize", "tenderloin", "tendon", "tendril", "tenement", "tenet", "tenner", "tennis",
				"tenon", };
	}

	static class KStemData8 {
		private KStemData8() {
		}

		static String[] data = { "tenor", "tenpin", "tense", "tensile", "tension", "tent", "tentacle", "tentative",
				"tenterhooks", "tenuity", "tenuous", "tenure", "tepee", "tepid", "tequila", "tercentenary",
				"tercentennial", "term", "termagant", "terminable", "terminal", "terminate", "termination",
				"terminology", "terminus", "termite", "terms", "tern", "terpsichorean", "terrace", "terracotta",
				"terrain", "terrapin", "terrestrial", "terrible", "terribly", "terrier", "terrific", "terrifically",
				"terrify", "territorial", "territory", "terror", "terrorise", "terrorism", "terrorize", "terrycloth",
				"terse", "tertian", "tertiary", "terylene", "tessellated", "test", "testament", "testamentary",
				"testate", "testator", "tester", "testicle", "testify", "testimonial", "testimony", "testis", "testy",
				"tetanus", "tetchy", "tether", "teutonic", "text", "textbook", "textile", "textual", "texture",
				"thalidomide", "than", "thane", "thank", "thankful", "thankless", "thanks", "thanksgiving", "thankyou",
				"that", "thatch", "thaw", "the", "theater", "theatergoer", "theatre", "theatregoer", "theatrical",
				"theatricals", "thee", "theft", "thegn", "their", "theirs", "theism", "them", "theme", "themselves",
				"then", "thence", "thenceforth", "theocracy", "theocratic", "theodolite", "theologian", "theology",
				"theorem", "theoretical", "theoretically", "theorise", "theorist", "theorize", "theory", "theosophy",
				"therapeutic", "therapeutics", "therapist", "therapy", "there", "thereabouts", "thereafter", "thereby",
				"therefore", "therein", "thereinafter", "thereof", "thereon", "thereto", "thereunder", "thereupon",
				"therm", "thermal", "thermionic", "thermionics", "thermodynamics", "thermometer", "thermonuclear",
				"thermoplastic", "thermos", "thermosetting", "thermostat", "thesaurus", "these", "thesis", "thespian",
				"thews", "they", "thick", "thicken", "thickener", "thicket", "thickheaded", "thickness", "thickset",
				"thief", "thieve", "thieving", "thievish", "thigh", "thimble", "thimbleful", "thin", "thine", "thing",
				"thingamajig", "thingamujig", "things", "think", "thinkable", "thinking", "thinner", "third", "thirst",
				"thirsty", "thirteen", "thirty", "this", "thistle", "thistledown", "thither", "thole", "thong",
				"thorax", "thorn", "thorny", "thorough", "thoroughbred", "thoroughfare", "thoroughgoing", "those",
				"thou", "though", "thought", "thoughtful", "thoughtless", "thousand", "thraldom", "thrall", "thralldom",
				"thrash", "thrashing", "thread", "threadbare", "threadlike", "threat", "threaten", "three",
				"threepence", "threnody", "thresh", "thresher", "threshold", "threw", "thrice", "thrift", "thrifty",
				"thrill", "thriller", "thrive", "throat", "throaty", "throb", "throes", "thrombosis", "throne",
				"throng", "throstle", "throttle", "through", "throughout", "throughput", "throughway", "throw",
				"throwaway", "throwback", "thru", "thrum", "thrush", "thrust", "thruster", "thruway", "thud", "thug",
				"thuggery", "thumb", "thumbnail", "thumbscrew", "thumbtack", "thump", "thumping", "thunder",
				"thunderbolt", "thunderclap", "thundercloud", "thundering", "thunderous", "thunderstorm",
				"thunderstruck", "thundery", "thurible", "thursday", "thus", "thwack", "thwart", "thy", "thyme",
				"thyroid", "thyself", "tiara", "tibia", "tic", "tick", "ticker", "tickertape", "ticket", "ticking",
				"tickle", "tickler", "ticklish", "tidal", "tidbit", "tiddler", "tiddley", "tiddleywinks", "tiddly",
				"tiddlywinks", "tide", "tidemark", "tidewater", "tideway", "tidings", "tidy", "tie", "tiebreaker",
				"tiepin", "tier", "tiff", "tiffin", "tig", "tiger", "tigerish", "tight", "tighten", "tightfisted",
				"tightrope", "tights", "tightwad", "tigress", "tike", "tilde", "tile", "till", "tillage", "tiller",
				"tilt", "timber", "timbered", "timberline", "timbre", "timbrel", "time", "timekeeper", "timeless",
				"timely", "timepiece", "timer", "times", "timesaving", "timeserver", "timeserving", "timetable",
				"timework", "timeworn", "timid", "timing", "timorous", "timothy", "timpani", "timpanist", "tin",
				"tincture", "tinder", "tinderbox", "tinfoil", "ting", "tingaling", "tinge", "tingle", "tinker",
				"tinkle", "tinny", "tinplate", "tinsel", "tint", "tintack", "tintinnabulation", "tiny", "tip", "tippet",
				"tipple", "tipstaff", "tipster", "tipsy", "tiptoe", "tirade", "tire", "tired", "tireless", "tiresome",
				"tiro", "tissue", "tit", "titan", "titanic", "titanium", "titbit", "titfer", "tithe", "titillate",
				"titivate", "title", "titled", "titleholder", "titmouse", "titter", "tittivate", "tittle", "titty",
				"titular", "tizzy", "tnt", "toad", "toadstool", "toady", "toast", "toaster", "toastmaster", "tobacco",
				"tobacconist", "toboggan", "toccata", "tocsin", "tod", "today", "toddle", "toddler", "toddy", "toe",
				"toehold", "toenail", "toff", "toffee", "toffy", "tog", "toga", "together", "togetherness", "toggle",
				"togs", "toil", "toilet", "toiletries", "toiletry", "toils", "tokay", "token", "told", "tolerable",
				"tolerably", "tolerance", "tolerant", "tolerate", "toleration", "toll", "tollgate", "tollhouse",
				"tomahawk", "tomato", "tomb", "tombola", "tomboy", "tombstone", "tomcat", "tome", "tomfoolery",
				"tommyrot", "tomorrow", "tomtit", "ton", "tonal", "tonality", "tone", "toneless", "tong", "tongs",
				"tongue", "tonic", "tonight", "tonnage", "tonne", "tonsil", "tonsilitis", "tonsillitis", "tonsorial",
				"tonsure", "tontine", "too", "took", "tool", "toot", "tooth", "toothache", "toothbrush", "toothcomb",
				"toothpaste", "toothpick", "toothsome", "toothy", "tootle", "toots", "tootsie", "top", "topaz",
				"topcoat", "topdressing", "topee", "topgallant", "topi", "topiary", "topic", "topical", "topicality",
				"topknot", "topless", "topmast", "topmost", "topographer", "topographical", "topography", "topper",
				"topping", "topple", "tops", "topsail", "topside", "topsoil", "topspin", "toque", "tor", "torch",
				"torchlight", "tore", "toreador", "torment", "tormentor", "torn", "tornado", "torpedo", "torpid",
				"torpor", "torque", "torrent", "torrential", "torrid", "torsion", "torso", "tort", "tortilla",
				"tortoise", "tortoiseshell", "tortuous", "torture", "tory", "toss", "tot", "total", "totalisator",
				"totalitarian", "totalitarianism", "totality", "totalizator", "tote", "totem", "totter", "tottery",
				"toucan", "touch", "touchdown", "touched", "touching", "touchline", "touchstone", "touchy", "tough",
				"toughen", "toupee", "tour", "tourism", "tourist", "tournament", "tourney", "tourniquet", "tousle",
				"tout", "tow", "towards", "towel", "toweling", "towelling", "tower", "towering", "towline", "town",
				"townscape", "township", "townsman", "townspeople", "towpath", "toxaemia", "toxemia", "toxic",
				"toxicologist", "toxicology", "toxin", "toy", "toyshop", "trace", "tracer", "tracery", "trachea",
				"trachoma", "tracing", "track", "trackless", "tracksuit", "tract", "tractable", "traction", "tractor",
				"trad", "trade", "trademark", "trader", "trades", "tradesman", "tradespeople", "tradition",
				"traditional", "traditionalism", "traduce", "traffic", "trafficator", "trafficker", "tragedian",
				"tragedienne", "tragedy", "tragic", "tragicomedy", "trail", "trailer", "train", "trainbearer",
				"trainee", "training", "trainman", "traipse", "trait", "traitor", "traitorous", "trajectory", "tram",
				"tramline", "trammel", "trammels", "tramp", "trample", "trampoline", "trance", "tranny", "tranquil",
				"tranquiliser", "tranquillise", "tranquillize", "tranquillizer", "transact", "transaction",
				"transactions", "transalpine", "transatlantic", "transcend", "transcendence", "transcendent",
				"transcendental", "transcendentalism", "transcontinental", "transcribe", "transcript", "transcription",
				"transept", "transfer", "transference", "transfiguration", "transfigure", "transfix", "transform",
				"transformation", "transformer", "transfuse", "transgress", "tranship", "transience", "transient",
				"transistor", "transistorise", "transistorize", "transit", "transition", "transitive", "translate",
				"translator", "transliterate", "translucence", "translucent", "transmigration", "transmission",
				"transmit", "transmitter", "transmogrify", "transmute", "transoceanic", "transom", "transparency",
				"transparent", "transpiration", "transpire", "transplant", "transpolar", "transport", "transportation",
				"transporter", "transpose", "transship", "transubstantiation", "transverse", "transvestism",
				"transvestite", "trap", "trapdoor", "trapeze", "trapezium", "trapezoid", "trapper", "trappings",
				"trappist", "trapse", "trapshooting", "trash", "trashcan", "trashy", "trauma", "traumatic", "travail",
				"travel", "traveled", "traveler", "travelled", "traveller", "travelog", "travelogue", "travels",
				"travelsick", "traverse", "travesty", "trawl", "trawler", "tray", "treacherous", "treachery", "treacle",
				"treacly", "tread", "treadle", "treadmill", "treason", "treasonable", "treasure", "treasurer",
				"treasury", "treat", "treatise", "treatment", "treaty", "treble", "tree", "trefoil", "trek", "trellis",
				"tremble", "tremendous", "tremolo", "tremor", "tremulous", "trench", "trenchant", "trencher",
				"trencherman", "trend", "trendsetter", "trendy", "trepan", "trephine", "trepidation", "trespass",
				"tresses", "trestle", "trews", "triad", "trial", "triangle", "triangular", "tribal", "tribalism",
				"tribe", "tribesman", "tribulation", "tribunal", "tribune", "tributary", "tribute", "trice", "triceps",
				"trichinosis", "trick", "trickery", "trickle", "trickster", "tricky", "tricolor", "tricolour",
				"tricycle", "trident", "triennial", "trier", "trifle", "trifler", "trifling", "trigger", "trigonometry",
				"trike", "trilateral", "trilby", "trilingual", "trill", "trillion", "trilobite", "trilogy", "trim",
				"trimaran", "trimester", "trimmer", "trimming", "trinitrotoluene", "trinity", "trinket", "trio", "trip",
				"tripartite", "triple", "triplet", "triplex", "triplicate", "tripod", "tripos", "tripper", "tripping",
				"triptych", "tripwire", "trireme", "trisect", "trite", "triumph", "triumphal", "triumphant", "triumvir",
				"triumvirate", "trivet", "trivia", "trivial", "trivialise", "triviality", "trivialize", "trochaic",
				"trochee", "trod", "trodden", "troglodyte", "troika", "trojan", "troll", "trolley", "trolleybus",
				"trollop", "trombone", "trombonist", "troop", "trooper", "troops", "troopship", "trope", "trophy",
				"tropic", "tropical", "tropics", "trot", "troth", "trotskyist", "trotter", "troubadour", "trouble",
				"troublemaker", "troubleshooter", "troublesome", "trough", "trounce", "troupe", "trouper", "trouser",
				"trousers", "trousseau", "trout", "trove", "trowel", "truancy", "truant", "truce", "truck", "trucking",
				"truckle", "truculence", "truculent", "trudge", "true", "trueborn", "truehearted", "truelove",
				"truffle", "trug", "truism", "truly", "trump", "trumpery", "trumpet", "trumps", "truncate", "truncheon",
				"trundle", "trunk", "trunks", "truss", "trust", "trustee", "trusteeship", "trustful", "trustworthy",
				"trusty", "truth", "truthful", "try", "tryst", "tsar", "tsarina", "tsp", "tub", "tuba", "tubby", "tube",
				"tubeless", "tuber", "tubercular", "tuberculosis", "tubful", "tubing", "tubular", "tuck", "tucker",
				"tuckerbag", "tuesday", "tuft", "tug", "tugboat", "tuition", "tulip", "tulle", "tumble", "tumbledown",
				"tumbler", "tumbleweed", "tumbrel", "tumbril", "tumescent", "tumid", "tummy", "tumor", "tumour",
				"tumult", "tumultuous", "tumulus", "tun", "tuna", "tundra", "tune", "tuneful", "tuneless", "tuner",
				"tungsten", "tunic", "tunnel", "tunny", "tup", "tuppence", "tuppenny", "turban", "turbid", "turbine",
				"turbojet", "turboprop", "turbot", "turbulence", "turbulent", "turd", "tureen", "turf", "turgid",
				"turkey", "turmeric", "turmoil", "turn", "turnabout", "turncoat", "turncock", "turner", "turning",
				"turnip", "turnkey", "turnout", "turnover", "turnpike", "turnstile", "turntable", "turpentine",
				"turpitude", "turquoise", "turret", "turtle", "turtledove", "turtleneck", "tush", "tusk", "tusker",
				"tussle", "tussock", "tut", "tutelage", "tutelary", "tutor", "tutorial", "tutu", "tuxedo", "twaddle",
				"twain", "twang", "twat", "tweak", "twee", "tweed", "tweeds", "tweedy", "tweet", "tweeter", "tweezers",
				"twelfth", "twelve", "twelvemonth", "twenty", "twerp", "twice", "twiddle", "twig", "twilight", "twill",
				"twin", "twinge", "twinkle", "twinkling", "twirl", "twirp", "twist", "twister", "twit", "twitch",
				"twitter", "twixt", "two", "twofaced", "twopence", "twopenny", "twosome", "tycoon", "tyke", "tympanum",
				"type", "typecast", "typeface", "typescript", "typesetter", "typewriter", "typewritten", "typhoid",
				"typhoon", "typhus", "typical", "typically", "typify", "typist", "typographer", "typographic",
				"typography", "tyrannical", "tyrannise", "tyrannize", "tyrannosaurus", "tyranny", "tyrant", "tyre",
				"tyro", "tzar", "tzarina", "ubiquitous", "ucca", "udder", "ufo", "ugh", "ugly", "uhf", "ukulele",
				"ulcer", "ulcerate", "ulcerous", "ullage", "ulna", "ult", "ulterior", "ultimate", "ultimately",
				"ultimatum", "ultimo", "ultramarine", "ultrasonic", "ultraviolet", "umber", "umbrage", "umbrella",
				"umlaut", "umpire", "umpteen", "unabashed", "unabated", "unable", "unabridged", "unaccompanied",
				"unaccountable", "unaccustomed", "unadopted", "unadulterated", "unadvised", "unaffected", "unalloyed",
				"unanimous", "unannounced", "unanswerable", "unapproachable", "unarmed", "unasked", "unassuming",
				"unattached", "unattended", "unavailing", "unawares", "unbalance", "unbar", "unbearable", "unbearably",
				"unbeknown", "unbelief", "unbelievable", "unbeliever", "unbelieving", "unbend", "unbending", "unbidden",
				"unbind", "unblushing", "unborn", "unbosom", "unbounded", "unbowed", "unbridled", "unbuckle",
				"unburden", "unbuttoned", "uncanny", "unceremonious", "uncertain", "uncertainty", "uncharitable",
				"uncharted", "unchecked", "unchristian", "unclad", "uncle", "unclean", "unclouded", "uncolored",
				"uncoloured", "uncomfortable", "uncommitted", "uncommonly", "uncompromising", "unconcerned",
				"unconditional", "unconscionable", "unconscious", "unconsidered", "uncork", "uncouple", "uncouth",
				"uncover", "uncritical", "uncrowned", "uncrushable", "unction", "unctuous", "uncut", "undaunted",
				"undeceive", "undecided", "undeclared", "undeniable", "under", "underact", "underarm", "underbelly",
				"underbrush", "undercarriage", "undercharge", "underclothes", "undercoat", "undercover", "undercurrent",
				"undercut", "underdog", "underdone", "underestimate", "underfelt", "underfloor", "underfoot",
				"undergarment", "undergo", "undergraduate", "underground", "undergrowth", "underhand", "underhanded",
				"underhung", "underlay", "underlie", "underline", "underling", "underlying", "undermanned",
				"undermentioned", "undermine", "underneath", "undernourish", "underpants", "underpass", "underpin",
				"underplay", "underprivileged", "underproof", "underquote", "underrate", "underscore", "undersecretary",
				"undersell", "undersexed", "undershirt", "underside", "undersigned", "undersized", "underslung",
				"understaffed", "understand", "understanding", "understate", "understatement", "understudy",
				"undertake", "undertaker", "undertaking", "undertone", "undertow", "underwater", "underwear",
				"underweight", "underwent", "underworld", "underwrite", "underwriter", "undesirable", "undeveloped",
				"undies", "undischarged", "undistinguished", "undivided", "undo", "undoing", "undomesticated", "undone",
				"undoubted", "undress", "undressed", "undue", "undulate", "undulation", "unduly", "undying", "unearth",
				"unearthly", "unease", "uneasy", "uneconomic", "uneducated", "unemployed", "unemployment",
				"unenlightened", "unenviable", "unequal", "unequaled", "unequalled", "unequivocal", "unerring",
				"unesco", "uneven", "uneventful", "unexampled", "unexceptionable", "unfailing", "unfaithful",
				"unfaltering", "unfathomable", "unfathomed", "unfavorable", "unfavourable", "unfeeling", "unfettered",
				"unfit", "unflagging", "unflappable", "unflinching", "unfold", "unforeseen", "unforgettable",
				"unfortunate", "unfortunately", "unfounded", "unfrequented", "unfrock", "unfurl", "ungainly",
				"ungenerous", "ungodly", "ungovernable", "ungracious", "ungrateful", "ungrudging", "unguarded",
				"unguent", "unhallowed", "unhand", "unhappily", "unhappy", "unhealthy", "unheard", "unhinge", "unholy",
				"unhook", "unhorse", "unicef", "unicorn", "unidentified", "unification", "uniform", "uniformed",
				"unify", "unilateral", "unimpeachable", "uninformed", "uninhabitable", "uninhibited", "uninterested",
				"uninterrupted", "union", "unionise", "unionism", "unionist", "unionize", "unique", "unisex", "unison",
				"unit", "unitarian", "unite", "united", "unity", "universal", "universally", "universe", "university",
				"unkempt", "unkind", "unkindly", "unknowing", "unknown", "unlawful", "unlearn", "unleash", "unleavened",
				"unless", "unlettered", "unlike", "unlikely", "unload", "unlock", "unloose", "unloosen", "unmade",
				"unmannerly", "unmarried", "unmask", "unmatched", "unmeasured", "unmentionable", "unmentionables",
				"unmindful", "unmistakable", "unmitigated", "unmoved", "unnatural", "unnecessary", "unnerve",
				"unnumbered", "uno", "unobtrusive", "unofficial", "unorthodox", "unpack", "unparalleled",
				"unparliamentary", "unperson", "unpick", "unplaced", "unplayable", "unpleasant", "unplumbed",
				"unpracticed", "unpractised", "unprecedented", "unprejudiced", "unpretentious", "unprincipled",
				"unprintable", "unprofessional", "unprompted", "unprovoked", "unqualified", "unquestionable",
				"unquestioning", "unquiet", "unquote", "unravel", "unreadable", "unreal", "unreasonable", "unreasoning",
				"unrelenting", "unrelieved", "unremitting", "unrequited", "unreserved", "unrest", "unrestrained",
				"unrip", "unrivaled", "unrivalled", "unroll", "unruffled", "unruly", "unsaddle", "unsaid", "unsavory",
				"unsavoury", "unsay", "unscathed", "unschooled", "unscramble", "unscrew", "unscripted", "unscrupulous",
				"unseat", "unseeing", "unseemly", "unseen", "unserviceable", "unsettle", "unsettled", "unsex",
				"unsexed", "unshakable", "unshakeable", "unshod", "unsightly", "unskilled", "unsociable", "unsocial",
				"unsophisticated", "unsound", "unsparing", "unspeakable", "unspotted", "unstop", "unstrung", "unstuck",
				"unstudied", "unsullied", "unsung", "unswerving", "untangle", "untapped", "untenable", "unthinkable",
				"unthinking", "untie", "until", "untimely", "untinged", "untiring", "unto", "untold", "untouchable",
				"untoward", "untruth", "untruthful", "untutored", "unused", "unusual", "unusually", "unutterable",
				"unvarnished", "unveil", "unversed", "unvoiced", "unwarranted", "unwed", "unwell", "unwieldy", "unwind",
				"unwitting", "unwonted", "unzip", "upbeat", "upbraid", "upbringing", "upcoming", "update", "upend",
				"upgrade", "upheaval", "uphill", "uphold", "upholster", "upholsterer", "upholstery", "upkeep", "upland",
				"uplift", "upon", "upper", "uppercut", "uppermost", "uppish", "uppity", "upright", "uprising", "uproar",
				"uproarious", "uproot", "upset", "upshot", "upstage", "upstairs", "upstanding", "upstart", "upstream",
				"upsurge", "upswing", "uptake", "uptight", "uptown", "upturn", "upturned", "upward", "upwards",
				"uranium", "uranus", "urban", "urbane", "urbanise", "urbanize", "urchin", "urge", "urgent", "uric",
				"urinal", "urinary", "urinate", "urine", "urn", "usage", "use", "useful", "usefulness", "useless",
				"user", "usher", "usherette", "ussr", "usual", "usually", "usurer", "usurious", "usurp", "usury",
				"utensil", "uterine", "uterus", "utilise", "utilitarian", "utilitarianism", "utility", "utilize",
				"utmost", "utopia", "utopian", "utter", "utterance", "utterly", "uvula", "uvular", "uxorious", "vac",
				"vacancy", "vacant", "vacate", "vacation", "vaccinate", "vaccination", "vaccine", "vacillate",
				"vacuity", "vacuous", "vacuum", "vagabond", "vagary", "vagina", "vaginal", "vagrancy", "vagrant",
				"vague", "vain", "vainglorious", "vainglory", "valance", "vale", "valediction", "valedictory",
				"valency", "valentine", "valerian", "valet", "valetudinarian", "valiant", "valiantly", "valid",
				"validate", "valise", "valley", "valor", "valour", "valse", "valuable", "valuation", "value", "valuer",
				"valve", "valvular", "vamoose", "vamp", "vampire", "van", "vanadium", "vandal", "vandalise",
				"vandalism", "vandalize", "vane", "vanguard", "vanilla", "vanish", "vanity", "vanquish", "vantagepoint",
				"vapid", "vapidity", "vapor", "vaporise", "vaporize", "vaporous", "vapors", "vapour", "vapours",
				"variability", "variable", "variance", "variant", "variation", "varicolored", "varicoloured",
				"varicose", "varied", "variegated", "variegation", "variety", "variform", "variorum", "various",
				"variously", "varlet", "varmint", "varnish", "varsity", "vary", "vascular", "vase", "vasectomy",
				"vaseline", "vassal", "vassalage", "vast", "vastly", "vastness", "vat", "vatican", "vaudeville",
				"vault", "vaulted", "vaulting", "vaunt", "veal", "vector", "veer", "veg", "vegan", "vegetable",
				"vegetarian", "vegetarianism", "vegetate", "vegetation", "vehement", "vehicle", "vehicular", "veil",
				"veiled", "vein", "veined", "veining", "velar", "velarize", "veld", "veldt", "vellum", "velocipede",
				"velocity", "velour", "velours", "velvet", "velveteen", "velvety", "venal", "vend", "vendee", "vender",
				"vendetta", "vendor", "veneer", "venerable", "venerate", "venereal", "vengeance", "vengeful", "venial",
				"venison", "venom", "venomous", "venous", "vent", "ventilate", "ventilation", "ventilator", "ventricle",
				"ventriloquism", "ventriloquist", "venture", "venturer", "venturesome", "venue", "veracious",
				"veracity", "veranda", "verandah", "verb", "verbal", "verbalise", "verbalize", "verbally", "verbatim",
				"verbena", "verbiage", "verbose", "verbosity", "verdant", "verdict", "verdigris", "verdure", "verge",
				"verger", "verify", "verily", "verisimilitude", "veritable", "verity", "vermicelli", "vermiculite",
				"vermiform", "vermifuge", "vermilion", "vermin", "verminous", "vermouth", "vernacular", "vernal",
				"veronal", "veronica", "verruca", "versatile", "verse", "versed", "versification", "versify", "version",
				"verso", "versus", "vertebra", "vertebrate", "vertex", "vertical", "vertiginous", "vertigo", "verve",
				"very", "vesicle", "vesicular", "vesper", "vespers", "vessel", "vest", "vestibule", "vestige",
				"vestigial", "vestment", "vestry", "vestryman", "vesture", "vet", "vetch", "veteran", "veterinary",
				"veto", "vex", "vexation", "vexatious", "vhf", "via", "viable", "viaduct", "vial", "viands", "vibes",
				"vibrancy", "vibrant", "vibraphone", "vibrate", "vibration", "vibrato", "vibrator", "vicar", "vicarage",
				"vicarious", "vice", "vicelike", "viceregal", "vicereine", "viceroy", "vicinity", "vicious",
				"vicissitudes", "victim", "victimise", "victimize", "victor", "victorian", "victorious", "victory",
				"victual", "victualer", "victualler", "victuals", "vicuaa", "vicuana", "vide", "videlicet", "video",
				"videotape", "vie", "view", "viewer", "viewfinder", "viewless", "viewpoint", "vigil", "vigilance",
				"vigilant", "vigilante", "vignette", "vigor", "vigorous", "vigour", "viking", "vile", "vilification",
				"vilify", "villa", "village", "villager", "villain", "villainies", "villainous", "villainy", "villein",
				"villeinage", "villenage", "vim", "vinaigrette", "vindicate", "vindication", "vindictive", "vine",
				"vinegar", "vinegary", "vinery", "vineyard", "vino", "vinous", "vintage", "vintner", "vinyl", "viol",
				"viola", "violate", "violence", "violent", "violet", "violin", "violoncello", "vip", "viper", "virago",
				"virgin", "virginal", "virginals", "virginia", "virginity", "virgo", "virgule", "virile", "virility",
				"virologist", "virology", "virtu", "virtual", "virtually", "virtue", "virtuosity", "virtuoso",
				"virtuous", "virulence", "virulent", "virus", "visa", "visage", "viscera", "visceral", "viscosity",
				"viscount", "viscountcy", "viscountess", "viscous", "vise", "visibility", "visible", "visibly",
				"vision", "visionary", "visit", "visitant", "visitation", "visiting", "visitor", "visor", "vista",
				"visual", "visualise", "visualize", "visually", "vital", "vitalise", "vitality", "vitalize", "vitally",
				"vitals", "vitamin", "vitiate", "viticulture", "vitreous", "vitrify", "vitriol", "vitriolic",
				"vituperate", "vituperation", "vituperative", "vivace", "vivacious", "vivarium", "vivid", "viviparous",
				"vivisect", "vivisection", "vivisectionist", "vixen", "vixenish", "vizier", "vocab", "vocabulary",
				"vocal", "vocalise", "vocalist", "vocalize", "vocation", "vocational", "vocative", "vociferate",
				"vociferation", "vociferous", "vodka", "vogue", "voice", "voiceless", "void", "voile", "vol",
				"volatile", "volcanic", "volcano", "vole", "volition", "volitional", "volley", "volleyball", "volt",
				"voltage", "voluble", "volume", "volumes", "voluminous", "voluntary", "volunteer", "voluptuary",
				"voluptuous", "volute", "vomit", "voodoo", "voracious", "vortex", "votary", "vote", "voter", "votive",
				"vouch", "voucher", "vouchsafe", "vow", "vowel", "voyage", "voyager", "voyages", "voyeur", "vtol",
				"vulcanise", "vulcanite", "vulcanize", "vulgar", "vulgarian", "vulgarise", "vulgarism", "vulgarity",
				"vulgarize", "vulgate", "vulnerable", "vulpine", "vulture", "vulva", "wac", "wack", "wacky", "wad",
				"wadding", "waddle", "wade", "wader", "wadge", "wadi", "wady", "wafer", "waffle", "waft", "wag", "wage",
				"wager", "wages", "waggery", "waggish", "waggle", "waggon", "waggoner", "waggonette", "wagon",
				"wagoner", "wagonette", "wagtail", "waif", "wail", "wain", "wainscot", "waist", "waistband",
				"waistcoat", "waistline", "wait", "waiter", "waits", "waive", "waiver", "wake", "wakeful", "waken",
				"waking", "walk", "walkabout", "walkaway", "walker", "walking", "walkout", "walkover", "wall", "walla",
				"wallaby", "wallah", "wallet", "wallflower", "wallop", "walloping", "wallow", "wallpaper", "walnut",
				"walrus", "waltz", "wampum", "wan", "wand", "wander", "wanderer", "wandering", "wanderings",
				"wanderlust", "wane", "wangle", "wank", "wanker", "want", "wanting", "wanton", "wants", "wapiti", "war",
				"warble", "warbler", "ward", "warden", "warder", "wardrobe", "wardroom", "warehouse", "wares",
				"warfare", "warhead", "warhorse", "warily", "warlike", "warlock", "warlord", "warm", "warmonger",
				"warmth", "warn", "warning", "warp", "warpath", "warrant", "warrantee", "warrantor", "warranty",
				"warren", "warrior", "warship", "wart", "warthog", "wartime", "wary", "was", "wash", "washable",
				"washbasin", "washboard", "washbowl", "washcloth", "washday", "washer", "washerwoman", "washhouse",
				"washing", "washout", "washroom", "washstand", "washwoman", "washy", "wasp", "waspish", "wassail",
				"wast", "wastage", "waste", "wasteful", "waster", "wastrel", "watch", "watchband", "watchdog",
				"watches", "watchful", "watchmaker", "watchman", "watchtower", "watchword", "water", "waterborne",
				"watercolor", "watercolour", "watercourse", "watercress", "waterfall", "waterfowl", "waterfront",
				"waterhole", "waterline", "waterlogged", "waterloo", "waterman", "watermark", "watermelon", "watermill",
				"waterpower", "waterproof", "waters", "watershed", "waterside", "waterspout", "watertight", "waterway",
				"waterwheel", "waterwings", "waterworks", "watery", "watt", "wattage", "wattle", "wave", "wavelength",
				"waver", "wavy", "wax", "waxen", "waxworks", "waxy", "way", "waybill", "wayfarer", "wayfaring",
				"waylay", "ways", "wayside", "wayward", "weak", "weaken", "weakling", "weakness", "weal", "weald",
				"wealth", "wealthy", "wean", "weapon", "weaponry", "wear", "wearing", "wearisome", "weary", "weasel",
				"weather", "weatherboard", "weathercock", "weatherglass", "weatherman", "weatherproof", "weathers",
				"weave", "weaver", "web", "webbed", "webbing", "wed", "wedded", "wedding", "wedge", "wedged",
				"wedgwood", "wedlock", "wednesday", "wee", "weed", "weeds", "weedy", "week", "weekday", "weekend",
				"weekender", "weekly", "weeknight", "weeny", "weep", "weeping", "weepy", "weevil", "weft", "weigh",
				"weighbridge", "weight", "weighted", "weighting", "weightless", "weighty", "weir", "weird", "weirdie",
				"weirdo", "welch", "welcome", "weld", "welder", "welfare", "welkin", "well", "wellbeing", "wellborn",
				"wellington", "wellspring", "welsh", "welt", "weltanschauung", "welter", "welterweight", "wen", "wench",
				"wend", "wensleydale", "went", "wept", "were", "werewolf", "wert", "wesleyan", "west", "westbound",
				"westerly", "western", "westerner", "westernise", "westernize", "westernmost", "westward", "westwards",
				"wet", "wether", "wetting", "whack", "whacked", "whacker", "whacking", "whale", "whalebone", "whaler",
				"whaling", "wham", "wharf", "what", "whatever", "whatnot", "wheat", "wheaten", "wheedle", "wheel",
				"wheelbarrow", "wheelbase", "wheelchair", "wheelhouse", "wheeling", "wheels", "wheelwright", "wheeze",
				"wheezy", "whelk", "whelp", "when", "whence", "whenever", "where", "whereabouts", "whereas", "whereat",
				"whereby", "wherefore", "wherefores", "wherein", "whereof", "whereon", "wheresoever", "whereto",
				"whereupon", "wherever", "wherewithal", "wherry", "whet", "whether", "whetstone", "whew", "whey",
				"which", "whichever", "whiff", "whiffy", "whig", "while", "whim", "whimper", "whimsey", "whimsical",
				"whimsicality", "whimsy", "whin", "whine", "whiner", "whinny", "whip", "whipcord", "whiplash",
				"whippersnapper", "whippet", "whipping", "whippoorwill", "whippy", "whir", "whirl", "whirligig",
				"whirlpool", "whirlwind", "whirlybird", "whirr", "whisk", "whisker", "whiskered", "whiskers", "whiskey",
				"whisky", "whisper", "whist", "whistle", "whit", "white", "whitebait", "whitehall", "whiten",
				"whitening", "whites", "whitethorn", "whitethroat", "whitewash", "whither", "whiting", "whitlow",
				"whitsun", "whitsuntide", "whittle", "whiz", "whizz", "who", "whoa", "whodunit", "whoever", "whole",
				"wholemeal", "wholesale", "wholesaler", "wholesome", "wholly", "whom", "whoop", "whoopee", "whoosh",
				"whop", "whopper", "whopping", "whore", "whorehouse", "whoremonger", "whorl", "whortleberry", "whose",
				"whosoever", "why", "whys", "wick", "wicked", "wicker", "wickerwork", "wicket", "wide", "widely",
				"widen", "widespread", "widgeon", "widow", "widowed", "widower", "widowhood", "width", "wield", "wife",
				"wifely", "wig", "wigged", "wigging", "wiggle", "wight", "wigwam", "wilco", "wild", "wildcat",
				"wildebeest", "wilderness", "wildfire", "wildfowl", "wildlife", "wildly", "wile", "wiles", "wilful",
				"wiliness", "will", "willful", "willies", "willing", "willow", "willowy", "willpower", "wilt", "wily",
				"wimple", "wimpy", "win", "wince", "winceyette", "winch", "wind", "windbag", "windbreak", "windcheater",
				"windfall", "windily", "winding", "windjammer", "windlass", "windless", "windmill", "window",
				"windowpane", "windowsill", "windpipe", "windscreen", "windshield", "windsock", "windstorm",
				"windswept", "windward", "windy", "wine", "winebibbing", "wineglass", "winepress", "wineskin", "wing",
				"winger", "wings", "wingspan", "wink", "winkers", "winkle", "winner", "winning", "winnings", "winnow",
				"winsome", "winter", "wintergreen", "wintertime", "wintry", "wipe", "wiper", "wire", "wirecutters",
				"wireless", "wiretap", "wireworm", "wiring", "wiry", "wisdom", "wise", "wisecrack", "wish", "wishbone",
				"wisp", "wispy", "wisteria", "wistful", "wit", "witch", "witchcraft", "witchdoctor", "witchery",
				"witching", "with", "withal", "withdraw", "withdrawal", "withdrawn", "withe", "wither", "withering",
				"withers", "withhold", "within", "without", "withstand", "withy", "witless", "witness", "witticism",
				"witting", "witty", "wives", "wizard", "wizardry", "wizened", "woad", "wobble", "wobbly", "woe",
				"woebegone", "woeful", "wog", "woke", "woken", "wold", "wolf", "wolfhound", "wolfram", "wolfsbane",
				"woman", "womanhood", "womanise", "womanish", "womanize", "womankind", "womanly", "womb", "wombat",
				"womenfolk", "won", "wonder", "wonderful", "wonderland", "wonderment", "wonders", "wondrous", "wonky",
				"wont", "wonted", "woo", "wood", "woodbine", "woodblock", "woodcock", "woodcraft", "woodcut",
				"woodcutter", "wooded", "wooden", "woodenheaded", "woodland", "woodlouse", "woodpecker", "woodpile",
				"woodshed", "woodsman", "woodwind", "woodwork", "woodworm", "woody", "wooer", "woof", "woofer", "wool",
				"woolen", "woolens", "woolgather", "woolgathering", "woollen", "woollens", "woolly", "woolsack",
				"woozy", "wop", "word", "wording", "wordless", "wordplay", "words", "wordy", "wore", "work", "workable",
				"workaday", "workbag", "workbasket", "workbench", "workbook", "workday", "worker", "workhorse",
				"workhouse", "working", "workings", "workman", "workmanlike", "workmanship", "workout", "workpeople",
				"workroom", "works", "workshop", "worktop", "world", "worldly", "worldshaking", "worldwide", "worm",
				"wormhole", "wormwood", "wormy", "worn", "worried", "worrisome", "worry", "worse", "worsen", "worship",
				"worshipful", "worst", "worsted", "wort", "worth", "worthless", "worthwhile", "worthy", "wot",
				"wotcher", "would", "wouldst", "wound", "wove", "woven", "wow", "wrac", "wrack", "wraith", "wrangle",
				"wrangler", "wrap", "wrapper", "wrapping", "wrath", "wreak", "wreath", "wreathe", "wreck", "wreckage",
				"wrecker", "wren", "wrench", "wrest", "wrestle", "wretch", "wretched", "wriggle", "wright", "wring",
				"wringer", "wrinkle", "wrist", "wristband", "wristlet", "wristwatch", "wristy", "writ", "write",
				"writer", "writhe", "writing", "writings", "written", "wrong", "wrongdoing", "wrongful", "wrongheaded",
				"wrote", "wroth", "wrought", "wrung", "wry", "wurst", "wyvern", "xenon", "xenophobia", "xerox",
				"xylophone", "yacht", "yachting", "yachtsman", "yahoo", "yak", "yam", "yammer", "yang", "yank",
				"yankee", "yap", "yard", "yardage", "yardarm", "yardstick", "yarn", "yarrow", "yashmak", "yaw", "yawl",
				"yawn", "yaws", "yea", "yeah", "year", "yearbook", "yearling", "yearlong", "yearly", "yearn",
				"yearning", "years", "yeast", "yeasty", "yell", "yellow", "yelp", "yen", "yeoman", "yeomanry", "yes",
				"yesterday", "yet", "yeti", "yew", "yid", "yiddish", "yield", "yielding", "yin", "yippee", "yobbo",
				"yodel", "yoga", "yoghurt", "yogi", "yogurt", "yoke", "yokel", "yolk", "yonder", "yonks", "yore",
				"yorker", "you", "young", "younger", "youngster", "your", "yours", "yourself", "youth", "youthful",
				"yowl", "yoyo", "yucca", "yule", "yuletide", "zany", "zeal", "zealot", "zealotry", "zealous", "zebra",
				"zebu", "zed", "zeitgeist", "zen", "zenana", "zenith", "zephyr", "zeppelin", "zero", "zest", "ziggurat",
				"zigzag", "zinc", "zinnia", "zionism", "zip", "zipper", "zippy", "zither", "zizz", "zodiac", "zombi",
				"zombie", "zonal", "zone", "zoning", "zonked", "zoo", "zoologist", "zoology", "zoom", "zoophyte",
				"zouave", "zucchini", "zulu", "abbas", "activism", "agribusiness", "barfly", "basal", "batwing",
				"bedspring", "boric", "botfly", "bowstring", "centric", "cation", "clonic", "cryptanalysis", "cursor",
				"daybed", "deaconess", "defocus", "fiance", "flatbed", "formic", "gator", "grizzly", "jowly", "kelly",
				"lacewing", "later", "lioness", "sawfly", "shoofly", "weber", "arbutus", "pupal" };
	}

}
