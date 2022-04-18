import extensions.CSVFile;
import extensions.File;

class GraineDExplorateur extends Program {

    /* ========================================================================== */
    /* CONSTANTS */
    /* ========================================================================== */

    /* ---------------------------------- Paths --------------------------------- */
    final String DATA_FOLDER_PATH = "data/";
    final String LOOTS_FILE_PATH = DATA_FOLDER_PATH + "loots.csv";
    final String CHARACTERS_FILE_PATH = DATA_FOLDER_PATH + "characters.csv";
    final String SENTENCES_FILE_PATH = DATA_FOLDER_PATH + "sentences.csv";
    final String QUESTIONS_FILE_PATH = DATA_FOLDER_PATH + "questions.csv";
    // final String REGIONS_FILE_PATH = DATA_FOLDER_PATH + "regions.csv";

    final String GRAPHICS_FOLDER_PATH = "graphics/";

    /* ---------------------------------- Types --------------------------------- */
    final int LOOT_PROPERTIES_COUNT = 6;
    final int CHARACTER_PROPERTIES_COUNT = 5;
    final int SENTENCE_PROPERTIES_COUNT = 4;
    final int QUESTION_PROPERTIES_COUNT = 10;
    // final int REGION_PROPERTIES_COUNT = 4;

    /* -------------------------------- Graphics -------------------------------- */
    final String EXTRA_SPACE = "   ";
    final String HORIZONTAL_SEPARATOR = " - ";
    final String VERTICAL_SEPARATOR = " | ";

    /* ------------------------------- Parameters ------------------------------- */
    // TODO : Change this
    final int MAX_TYPING_SPEED = 25;
    final int MIN_TYPING_SPEED = 15;
    final double AGGRO_RATE = 0.25;
    final int FLOWER_GROWING_STEPS_NUMBER = 6;
    final int ANSWER_ATTEMPTS = 3;
    final int LOOT_LOSS_QUANTITY = 8;
    final int MIN_RELATIONSHIP_LEVEL = -1;
    final int MAX_RELATIONSHIP_LEVEL = 2;

    final String POSITIVE_COLOR = "green";
    final String NEGATIVE_COLOR = "red";

    /* ---------------------------------- Menu ---------------------------------- */
    final MenuOption[] YES_NO_MENU_OPTIONS = new MenuOption[] {
            createMenuOption("Oui", "O"),
            createMenuOption("Non", "N")
    };

    /* ========================================================================== */
    /* GLOBAL VARIABLES */
    /* ========================================================================== */

    // We use global variables because we will need to use these variables very
    // often in the program
    Loot[] globalLoots = parseLootsData(readCSVFile(LOOTS_FILE_PATH));
    Sentence[] globalSentences = parseSentencesData(readCSVFile(SENTENCES_FILE_PATH));
    Character[] globalCharacters = parseCharactersData(readCSVFile(CHARACTERS_FILE_PATH));
    Question[] globalQuestions = parseQuestionsData(readCSVFile(QUESTIONS_FILE_PATH));
    // Region[] globalRegions = parseRegionsData(readCSVFile(REGIONS_FILE_PATH));

    /* ========================================================================== */
    /* ========================================================================== */
    /* ================== GAME AND GAMEPLAY SPECIFIC PARTS ====================== */
    /* ========================================================================== */
    /* ========================================================================== */

    /* ========================================================================== */
    /* MAIN */
    /* ========================================================================== */
    void algorithm() {
        mainMenu();
        saveGame();
    }

    void game() {
        printlnLikeTyping("Nous jouons !");

        do {
            clearScreen();
            if (random() > AGGRO_RATE) {
                encounterCharacter();
            } else {
                encounterAngrySquirrel();
            }
            saveGame();
        } while (askContinueExploration());
        mainMenu();
    }

    void mainMenu() {
        displayTitle();

        MenuOption[] options = new MenuOption[] {
                createMenuOption("Continuer la partie", "A", 2),
                // createMenuOption("Recommencer une nouvelle aventure", "B", 2),
                createMenuOption("Voir la collection", "C"),
                createMenuOption("Voir la progression", "P", 2),
                createMenuOption("Afficher la description", "W"),
                createMenuOption("Credits", "X", 1),
                createMenuOption("Quitter le jeu", "Q")
        };

        displayMenuOptions(options);

        switch (askMenuOptionChoice(options).code) {
            case "A":
                game();
                break;
            case "B":
                newGameMenu();
                break;
            case "C":
                displayScreen("collection");
                break;
            case "P":
                displayScreen("progression");
                break;
            case "W":
                displayScreen("readme");
                break;
            case "X":
                displayScreen("credits");
                break;
            case "Q":
                printLikeTyping("A bientôt !");
                break;
        }
    }

    void displayTitle() {
        clearScreen();
        displayFileContent(GRAPHICS_FOLDER_PATH + "title");
        printNLineBreak(2);
    }

    void displayScreen(String screenName) {
        displayTitle();
        switch (screenName) {
            case "collection":
                displayLootsCollection();
                break;
            case "progression":
                displayProgression();
                break;
            case "readme":
                displayDescription();
                break;
            case "credits":
                displayCredits();
                break;
        }
        printNLineBreak(3);
        printLikeTyping("Presser 'entrée' pour revenir au menu principal");
        readString(); // Wait for key press
        mainMenu();
    }

    /* ========================================================================== */
    /* OVERWRITE BACKUP AND NEW GAME */
    /* ========================================================================== */
    void newGameMenu() {
        printlnLikeTyping("Nouvelle partie !");
        printlnLikeTyping("Êtes-vous certain de vouloir écraser la sauvegarde existante ?");

        displayMenuOptions(YES_NO_MENU_OPTIONS);

        switch (askMenuOptionChoice(YES_NO_MENU_OPTIONS).code) {
            case "O":
                printlnLikeTyping("Ancienne sauvegarde écrasée !");
                game();
                break;
            case "N":
                printlnLikeTyping("Retour au menu principal !");
                mainMenu();
                break;
        }
    }

    /* ========================================================================== */
    /* GAME LOOP */
    /* ========================================================================== */
    boolean askContinueExploration() {
        printlnLikeTyping("Continuer l'exploration ?");
        displayMenuOptions(YES_NO_MENU_OPTIONS);
        return askMenuOptionChoice(YES_NO_MENU_OPTIONS).code == "O";
    }

    /* -------------------------- Encounter a character ------------------------- */

    void encounterCharacter() {
        Character character = getRandomCharacter(globalCharacters);

        displayCharacterGreeting(character);
        character.meetingCount += 1;

        Question question = getRandomQuestionByCharacter(globalQuestions, character);
        boolean isGoodAnswer = askQuestion(question);

        updateCharacterRelationshipLevel(character, isGoodAnswer);
        updateQuestionAnswersHistory(question, isGoodAnswer);

        displayQuestionGoodAnswer(question, isGoodAnswer);
        displayQuestionDidYouKnow(question);

        Loot loot = getRandomLootByCategories(question.lootsCategories);
        int lootQuantityAdjustment = isGoodAnswer ? 1 : -1;
        boolean isLootUpdated = updateLootQuantity(loot, lootQuantityAdjustment);
        if (isLootUpdated) {
            displayLootQuantityUpdate(loot, lootQuantityAdjustment);
        }

        displayRelationshipProgression(character, isGoodAnswer);
    }

    void displayCharacterGreeting(Character character) {
        printlnLikeTyping(toString(character), "", "underline");
        printNLineBreak(1);
        if (character.meetingCount == 0) {
            printlnLikeTyping(toString(getSentence(character.id, "bienvenue", 0)));
        } else {
            printlnLikeTyping(toString(getSentence(character.id, "salutations", character.relationshipLevel)));
        }
        printNLineBreak(1);

    }

    Sentence getSentence(int characterID, String sentenceCategory, int relationshipLevel) {
        relationshipLevel = clamp(relationshipLevel, MIN_RELATIONSHIP_LEVEL, relationshipLevel);

        int relationMax = 0;
        for (int i = 0; i < length(globalSentences); i++) {
            if (globalSentences[i].characterID == characterID && globalSentences[i].likingLevel > relationMax) {
                relationMax = globalSentences[i].likingLevel;
            }
        }
        relationshipLevel = clamp(relationshipLevel, MIN_RELATIONSHIP_LEVEL, relationMax);
        relationshipLevel = clamp(relationshipLevel, MIN_RELATIONSHIP_LEVEL, MAX_RELATIONSHIP_LEVEL);

        for (int i = 0; i < length(globalSentences); i++) {
            Sentence s = globalSentences[i];
            if (s.characterID == characterID && equals(s.category, sentenceCategory)
                    && s.likingLevel == relationshipLevel) {
                return s;
            }
        }

        return getSentence(0, sentenceCategory, relationshipLevel);

    }

    boolean askQuestion(Question question) {
        printlnLikeTyping(toString(question));

        MenuOption[] answers = new MenuOption[4];
        for (int i = 0; i < length(question.answers); i += 1) {
            answers[i] = createMenuOption(question.answers[i], "" + (char) ('A' + i));
        }

        displayMenuOptions(answers);

        return equals(askMenuOptionChoice(answers).code, question.goodAnswer);
    }

    String getQuestionGoodAnswer(Question question) {
        return question.answers[charAt(question.goodAnswer, 0) - 'A'];
    }

    void updateCharacterRelationshipLevel(Character character, boolean isPositive) {
        int value = isPositive ? 1 : -1;
        character.relationshipLevel += value;
    }

    void displayRelationshipProgression(Character character, boolean isPositive) {
        printLikeTyping("Ta relation avec " + toString(character) + " s'est ");
        if (isPositive) {
            printlnLikeTyping("améliorée !", POSITIVE_COLOR);
        } else {
            printlnLikeTyping("détériorée !", NEGATIVE_COLOR);
        }
        printNLineBreak(1);
    }

    void displayQuestionGoodAnswer(Question question, boolean isGoodAnswer) {
        if (isGoodAnswer) {
            printlnLikeTyping("Bonne réponse !", POSITIVE_COLOR);
        } else {
            printlnLikeTyping("Mauvaise réponse ! La bonne réponse était: "
                    + getQuestionGoodAnswer(question), NEGATIVE_COLOR);
        }
        printNLineBreak(1);
    }

    void updateQuestionAnswersHistory(Question question, boolean isGoodAnswer) {
        int[] archive = new int[length(question.answersHistory) + 1];

        for (int i = 0; i < length(question.answersHistory); i += 1) {
            archive[i] = question.answersHistory[i];
        }

        archive[length(archive) - 1] = isGoodAnswer ? 1 : 0;

        question.answersHistory = archive;
    }

    void displayQuestionDidYouKnow(Question question) {
        printlnLikeTyping("Le savais-tu ? " + question.didYouKnow);
        printNLineBreak(1);
    }

    void displayLootQuantityUpdate(Loot loot, int qtyAdjustment) {
        String textColor = qtyAdjustment >= 0 ? POSITIVE_COLOR : NEGATIVE_COLOR;
        printlnLikeTyping(lootQuantityUpdateToString(loot, qtyAdjustment), textColor);
        printNLineBreak(1);
    }

    boolean updateLootQuantity(Loot loot, int qtyAdjustment) {
        if (loot.quantity + qtyAdjustment >= 0) {
            loot.quantity += qtyAdjustment;
            return true;
        }
        return false;
    }

    /* ---------------------- Encounter the angry squirrel ---------------------- */

    void encounterAngrySquirrel() {
        displayFileContent(GRAPHICS_FOLDER_PATH + "squirrel");
        printlnLikeTyping("L'écureuil vous regarde et vous fait un clin d'oeil !");
        printNLineBreak(1);

        Question question = getRandomQuestion(globalQuestions);
        boolean isGoodAnswer = askHardQuestion(question);

        displayQuestionGoodAnswer(question, isGoodAnswer);
        updateQuestionAnswersHistory(question, isGoodAnswer);

        if (!isGoodAnswer) {
            printlnLikeTyping("L'écureuil ricane, il vient de vous voler :");
            angrySquirrelStealsLoots();
            printNLineBreak(2);
            printlnLikeTyping("Pas la peine de lui courrir après, il court trop vite !");
        }

        displayQuestionDidYouKnow(question);
    }

    void angrySquirrelStealsLoots() {
        for (int i = 0; i < LOOT_LOSS_QUANTITY; i += 1) {
            Loot loot = getRandomLootWeightedByRarity(globalLoots);
            Boolean isLootUpdated = updateLootQuantity(loot, -1);
            if (isLootUpdated) {
                displayLootQuantityUpdate(loot, -1);
            }
        }
    }

    boolean askHardQuestion(Question question) {
        printlnLikeTyping(toString(question));
        boolean isGoodAnswer = false;
        int attempts = ANSWER_ATTEMPTS;
        while (!isGoodAnswer && attempts > 0) {
            printLikeTyping(attempts + " tentative(s) restantes: ", attempts == 1 ? "yellow" : "");
            attempts -= 1;
            isGoodAnswer = equalsCaseInsentive(getQuestionGoodAnswer(question), readString());
        }

        return isGoodAnswer;
    }

    /* ========================================================================== */
    /* COLLECTION */
    /* ========================================================================== */

    void displayLootsCollection() {
        // Headers
        print(padRight("Intitulé", 25, ' '));
        print(padRight("Catégorie", 15, ' '));
        print(padRight("Quantité", 10, ' '));
        print(padRight("Rareté", 10, ' '));
        println("Le saviez-vous?");

        for (int i = 0; i < length(globalLoots); i += 1) {
            Loot loot = globalLoots[i];

            String lootName = loot.quantity > 0 ? loot.name : "???";
            String lootDidYouKnow = loot.quantity > 0 ? loot.didYouKnow : "???";

            print(padRight(lootName, 25, ' '));
            print(padRight(loot.category, 15, ' '));
            print(padRight("" + loot.quantity, 10, ' '));
            print(padRight("" + loot.rarity, 10, ' '));
            println(lootDidYouKnow);
        }
    }

    /* ========================================================================== */
    /* PROGRESSION */
    /* ========================================================================== */

    void displayProgression() {
        int[] progressionCount = new int[] {
                countCharactersEncountered(globalCharacters),
                countDiscoveredQuestions(globalQuestions),
                countGoodAnsweredQuestions(globalQuestions),
                countLootsOwned(globalLoots) };

        int[] progressionTotals = new int[] {
                length(globalCharacters),
                length(globalQuestions),
                length(globalQuestions),
                length(globalLoots) };

        String[] progressionCategories = new String[] {
                "Personnages rencontrés",
                "Questions posées",
                "Bonnes réponses",
                "Récompenses obtenues" };

        double[] progressionRates = divide(progressionCount, progressionTotals);
        double progressionTotal = average(progressionRates);

        displayFlower(progressionTotal);

        displayProgressionRates(progressionCategories, progressionRates);
        printNLineBreak(1);
        displayProgression(progressionTotal, "Progression totale");
    }

    void displayFlower(double progression) {
        int flowerGrowingStep = (int) (progression * FLOWER_GROWING_STEPS_NUMBER);
        displayFileContent(GRAPHICS_FOLDER_PATH + (int) flowerGrowingStep);
        printNLineBreak(2);
    }

    void displayProgressionRates(String[] progressionCategories, double[] progressionRates) {
        for (int i = 0; i < length(progressionCategories); i += 1) {
            displayProgression(progressionRates[i], progressionCategories[i]);
        }
    }

    void displayProgression(double progression, String text) {
        int percentage = (int) (progression * 100);
        String color = percentage == 100 ? "green" : "";

        printLikeTyping(padRight(text + ":", 30, ' '));
        printlnLikeTyping(percentage + "%", color);
    }

    int countCharactersEncountered(Character[] characters) {
        int count = 0;
        for (int i = 0; i < length(characters); i += 1) {
            if (characters[i].meetingCount > 0) {
                count += 1;
            }
        }
        return count;
    }

    int countGoodAnsweredQuestions(Question[] questions) {
        int count = 0;
        for (int i = 0; i < length(questions); i += 1) {
            if (isLastAnswerGood(questions[i])) {
                count += 1;
            }
        }
        return count;
    }

    int countDiscoveredQuestions(Question[] questions) {
        int count = 0;
        for (int i = 0; i < length(questions); i += 1) {
            if (length(questions[i].answersHistory) > 1) {
                count += 1;
            }
        }
        return count;
    }

    boolean isLastAnswerGood(Question question) {
        return (length(question.answersHistory) > 1
                && question.answersHistory[length(question.answersHistory) - 1] == 1);

    }

    int countLootsOwned(Loot[] loots) {
        int count = 0;
        for (int i = 0; i < length(loots); i += 1) {
            if (loots[i].quantity > 0) {
                count += 1;
            }
        }
        return count;
    }

    /* ========================================================================== */
    /* DESCRIPTION & CREDITS */
    /* ========================================================================== */
    void displayCredits() {
        displayFileContent(GRAPHICS_FOLDER_PATH + "credits");
    }

    void displayDescription() {
        displayFileContent(GRAPHICS_FOLDER_PATH + "description");
    }

    /* ========================================================================== */
    /* ========================================================================== */
    /* ========================== UTILITIES FUNTIONS ============================ */
    /* ========================================================================== */
    /* ========================================================================== */

    /* ========================================================================== */
    /* CSV FILE READING AND PARSING */
    /* ========================================================================== */

    String[][] readCSVFile(String path) {
        CSVFile file = loadCSV(path, ';');
        int linesNb = rowCount(file);
        int colsNb = columnCount(file);

        String[][] data = new String[linesNb][colsNb];

        for (int l = 0; l < linesNb; l += 1) {
            for (int c = 0; c < colsNb; c += 1) {
                data[l][c] = getCell(file, l, c);
            }
        }

        return data;
    }

    Character[] parseCharactersData(String data[][]) {
        Character[] characters = new Character[length(data, 1)];
        for (int l = 0; l < length(data, 1); l += 1) {
            String[] lineData = data[l];
            characters[l] = createCharacter(lineData);
        }
        return characters;
    }

    Loot[] parseLootsData(String data[][]) {
        Loot[] loots = new Loot[length(data, 1)];
        for (int l = 0; l < length(data, 1); l += 1) {
            String[] lineData = data[l];
            loots[l] = createLoot(lineData);
        }
        return loots;
    }

    Sentence[] parseSentencesData(String data[][]) {
        Sentence[] sentences = new Sentence[length(data, 1)];
        for (int l = 0; l < length(data, 1); l += 1) {
            String[] lineData = data[l];
            sentences[l] = createSentence(lineData);
        }
        return sentences;
    }

    Question[] parseQuestionsData(String data[][]) {
        Question[] questions = new Question[length(data, 1)];
        for (int l = 0; l < length(data, 1); l += 1) {
            String[] lineData = data[l];
            questions[l] = createQuestion(lineData);
        }
        return questions;
    }

    /*
     * Region[] parseRegionsData(String data[][]) {
     * Region[] regions = new Region[length(data, 1)];
     * for (int l = 0; l < length(data, 1); l += 1) {
     * String[] lineData = data[l];
     * regions[l] = createRegion(lineData);
     * }
     * return regions;
     * }
     */

    /* ========================================================================== */
    /* CSV FILES SAVING */
    /* ========================================================================== */
    void saveCSVFile(String path, String[][] data) {
        saveCSV(data, path, ';');
    }

    String[][] convertCharactersToCSVArray(Character[] characters) {
        String[][] CSVArray = new String[length(characters)][CHARACTER_PROPERTIES_COUNT];
        for (int i = 0; i < length(characters); i += 1) {
            CSVArray[i] = convertCharacterToStrArray(characters[i]);
        }
        return CSVArray;
    }

    String[][] convertLootsToCSVArray(Loot[] loots) {
        String[][] CSVArray = new String[length(loots)][LOOT_PROPERTIES_COUNT];
        for (int i = 0; i < length(loots); i += 1) {
            CSVArray[i] = convertLootToStrArray(loots[i]);
        }
        return CSVArray;
    }

    String[][] convertSentencesToCSVArray(Sentence[] sentences) {
        String[][] CSVArray = new String[length(sentences)][SENTENCE_PROPERTIES_COUNT];
        for (int i = 0; i < length(sentences); i += 1) {
            CSVArray[i] = convertSentenceToStrArray(sentences[i]);
        }
        return CSVArray;
    }

    String[][] convertQuestionsToCSVArray(Question[] questions) {
        String[][] CSVArray = new String[length(questions)][QUESTION_PROPERTIES_COUNT];
        for (int i = 0; i < length(questions); i += 1) {
            CSVArray[i] = convertQuestionToStrArray(questions[i]);

        }
        return CSVArray;
    }

    /*
     * String[][] convertRegionsToCSVArray(Region[] regions) {
     * String[][] CSVArray = new String[length(regions)][REGION_PROPERTIES_COUNT];
     * for (int i = 0; i < length(regions); i += 1) {
     * CSVArray[i] = convertRegionToStrArray(regions[i]);
     * }
     * return CSVArray;
     * }
     */

    /* ========================================================================== */
    /* TYPES TO STR ARRAY */
    /* ========================================================================== */
    String[] convertCharacterToStrArray(Character character) {
        String[] strArray = new String[CHARACTER_PROPERTIES_COUNT];

        strArray[0] = "" + character.id;
        strArray[1] = character.name;
        strArray[2] = character.job;
        strArray[3] = "" + character.meetingCount;
        strArray[4] = "" + character.relationshipLevel;

        return strArray;
    }

    String[] convertLootToStrArray(Loot loot) {
        String[] strArray = new String[LOOT_PROPERTIES_COUNT];

        strArray[0] = "" + loot.id;
        strArray[1] = loot.name;
        strArray[2] = loot.category;
        strArray[3] = "" + loot.rarity;
        strArray[4] = loot.didYouKnow;
        strArray[5] = "" + loot.quantity;

        return strArray;

    }

    String[] convertSentenceToStrArray(Sentence sentence) {
        String[] strArray = new String[SENTENCE_PROPERTIES_COUNT];

        strArray[0] = join(splitToString(sentence.sentence, '\n'), '\\');
        strArray[1] = sentence.category;
        strArray[2] = "" + sentence.characterID;
        strArray[3] = "" + sentence.likingLevel;

        return strArray;

    }

    String[] convertQuestionToStrArray(Question question) {
        String[] strArray = new String[QUESTION_PROPERTIES_COUNT];

        strArray[0] = question.question;
        strArray[1] = question.answers[0];
        strArray[2] = question.answers[1];
        strArray[3] = question.answers[2];
        strArray[4] = question.answers[3];
        strArray[5] = question.goodAnswer;
        strArray[6] = question.didYouKnow;
        strArray[7] = join(question.charactersJobs, '-');
        strArray[8] = join(question.lootsCategories, '-');
        strArray[9] = join(question.answersHistory, '-');

        return strArray;
    }

    /*
     * String[] convertRegionToStrArray(Region region) {
     * String[] strArray = new String[REGION_PROPERTIES_COUNT];
     * 
     * strArray[0] = "" + region.id;
     * strArray[1] = region.name;
     * strArray[2] = booleanToString(region.isDiscovered);
     * strArray[3] = booleanToString(region.isGardenInstalled);
     * 
     * return strArray;
     * 
     * }
     */

    /* ========================================================================== */
    /* GAME LOADING AND SAVING */
    /* ========================================================================== */
    void saveGame() {
        saveCSVFile(LOOTS_FILE_PATH, convertLootsToCSVArray(globalLoots));
        saveCSVFile(CHARACTERS_FILE_PATH,
                convertCharactersToCSVArray(globalCharacters));
        saveCSVFile(SENTENCES_FILE_PATH, convertSentencesToCSVArray(globalSentences));
        saveCSVFile(QUESTIONS_FILE_PATH, convertQuestionsToCSVArray(globalQuestions));
        // saveCSVFile(REGIONS_FILE_PATH, convertRegionsToCSVArray(globalRegions));
    }

    /* ========================================================================== */
    /* TYPES FACTORIES (WITH RAW STRING DATA) */
    /* ========================================================================== */
    Character createCharacter(String[] data) {
        Character character = new Character();

        character.id = stringToInt(data[0]);
        character.name = data[1];
        character.job = data[2];
        character.meetingCount = stringToInt(data[3]);
        character.relationshipLevel = stringToInt(data[4]);

        return character;
    }

    Loot createLoot(String[] data) {
        Loot loot = new Loot();

        loot.id = stringToInt(data[0]);
        loot.name = data[1];
        loot.category = data[2];
        loot.rarity = stringToInt(data[3]);
        loot.didYouKnow = data[4];
        loot.quantity = stringToInt(data[5]);

        return loot;
    }

    Sentence createSentence(String[] data) {
        Sentence sentence = new Sentence();

        sentence.sentence = join(splitToString(data[0], '\\'), '\n');
        sentence.category = data[1];
        sentence.characterID = stringToInt(data[2]);
        sentence.likingLevel = stringToInt(data[3]);

        return sentence;
    }

    Question createQuestion(String[] data) {
        Question question = new Question();

        question.question = data[0];
        question.answers = new String[] { data[1], data[2], data[3], data[4] };
        question.goodAnswer = data[5];
        question.didYouKnow = data[6];
        question.charactersJobs = splitToString(data[7], '-');
        question.lootsCategories = splitToString(data[8], '-');
        question.answersHistory = splitToInt(data[9], '-');

        return question;
    }

    /*
     * Region createRegion(String[] data) {
     * Region region = new Region();
     * 
     * region.id = stringToInt(data[0]);
     * region.name = data[1];
     * region.isDiscovered = stringToBoolean(data[2]);
     * region.isGardenInstalled = stringToBoolean(data[3]);
     * 
     * return region;
     * }
     */

    MenuOption createMenuOption(String text, String code, int spaceAfter) {
        MenuOption menuOption = new MenuOption();

        menuOption.text = text;
        menuOption.code = code;
        menuOption.spaceAfter = spaceAfter;

        return menuOption;
    }

    MenuOption createMenuOption(String text, String code) {
        return createMenuOption(text, code, 0);
    }

    /* ========================================================================== */
    /* TO STRING FUNCTIONS */
    /* ========================================================================== */
    String toString(Loot loot) {
        return loot.name + " (" + loot.category + ")";
    }

    String toString(Character character) {
        return character.name + " [" + character.relationshipLevel + "]";
    }

    String toString(Sentence sentence) {
        return sentence.sentence;
    }

    String toString(Question question) {
        return question.question;
    }

    String lootQuantityUpdateToString(Loot loot, int qtyAdjustment) {
        String operator = qtyAdjustment >= 0 ? " + " : " ";
        return toString(loot) + operator + qtyAdjustment + " [" + loot.quantity + "]";
    }

    /* ========================================================================== */
    /* UTILITY FUNCTIONS FOR STRINGS AND CHARACTERS */
    /* ========================================================================== */
    String[] splitToString(String str, char separator) {
        if (length(str) == 0) {
            return new String[] {};
        }
        // Seems to be more efficient than creating an array the size of the string and
        // slicing it after
        int numberOfItems = countChar(str, separator) + 1;
        String[] result = new String[numberOfItems];

        int separatorCounter = 0;
        String accumulator = "";

        for (int i = 0; i < length(str); i += 1) {
            char currentChar = charAt(str, i);
            if (currentChar == separator) {
                result[separatorCounter] = accumulator;
                accumulator = "";
                separatorCounter += 1;
            } else {
                accumulator += charAt(str, i);
            }
        }

        result[separatorCounter] = accumulator;

        return result;
    }

    int[] splitToInt(String str, char separator) {
        String[] splitedInString = splitToString(str, separator);
        int[] result = new int[length(splitedInString)];

        for (int i = 0; i < length(splitedInString); i += 1) {

            if (!equals(splitedInString[i], "")) {
                result[i] = stringToInt(splitedInString[i]);
            } else {
                result[i] = 0;
            }
        }

        return result;
    }

    int countChar(String str, char car) {
        int count = 0;
        for (int i = 0; i < length(str); i += 1) {
            if (charAt(str, i) == car) {
                count += 1;
            }
        }
        return count;
    }

    char toUpper(char c) {
        if (c >= 'a' && c <= 'z') {
            return (char) (c - 'a' + 'A');
        }
        return c;
    }

    String toUpper(String s) {
        String retour = "";
        for (int i = 0; i < length(s); i += 1) {
            retour += toUpper(charAt(s, i));
        }
        return retour;
    }

    int indexOf(String[] arr, String str) {
        for (int i = 0; i < length(arr); i += 1) {
            if (equals(arr[i], str)) {
                return i;
            }
        }
        return -1;
    }

    String padRight(String s, int len, char padCar) {
        String result = s;
        for (int i = 0; i + length(s) < len; i += 1) {
            result += padCar;
        }
        return result;
    }

    boolean equalsCaseInsentive(String s1, String s2) {
        return equals(toUpper(s1), toUpper(s2));
    }

    /* ========================================================================== */
    /* UTILITY FUNCTIONS FOR ARRAYS */
    /* ========================================================================== */
    String join(int[] list, char separator) {
        if (length(list) == 0) {
            return "";
        }

        String result = "" + list[0];
        for (int i = 1; i < length(list); i += 1) {
            result += "" + separator + list[i];
        }
        return result;
    }

    String join(String[] list, char separator) {
        if (length(list) == 0) {
            return "";
        }

        String result = "" + list[0];
        for (int i = 1; i < length(list); i += 1) {
            result += separator + list[i];
        }
        return result;
    }

    /* ========================================================================== */
    /* UTILITY FUNCTIONS FOR BOOLEANS */
    /* ========================================================================== */
    String booleanToString(boolean b) {
        if (b) {
            return "true";
        }
        return "false";
    }

    boolean stringToBoolean(String s) {
        if (equals(s, "true")) {
            return true;
        }
        return false;
    }

    /* ========================================================================== */
    /* UTILITY FUNCTIONS FOR NUMBER */
    /* ========================================================================== */
    int clamp(int value, int min, int max) {
        return min(max(value, min), max);
    }

    double[] divide(int values1[], int values2[]) {
        if (length(values1) != length(values2)) {
            return new double[0];
        }

        double[] rates = new double[length(values1)];

        for (int i = 0; i < length(rates); i += 1) {
            rates[i] = (double) values1[i] / (double) values2[i];
        }
        return rates;
    }

    double average(double[] values) {
        double total = 0;
        for (int i = 0; i < length(values); i += 1) {
            total += values[i];
        }
        return total / length(values);
    }

    /* ========================================================================== */
    /* PRINTING */
    /* ========================================================================== */
    void printLikeTyping(String str) {
        for (int i = 0; i < length(str); i += 1) {
            print(charAt(str, i));

            int randomDelay = random(MIN_TYPING_SPEED, MAX_TYPING_SPEED);
            delay(randomDelay);
        }
    }

    void printlnLikeTyping(String str) {
        printLikeTyping(str);
        println();
    }

    void printLikeTyping(String str, String color) {
        text(color);
        printLikeTyping(str);
        reset();
    }

    void printlnLikeTyping(String str, String color) {
        printLikeTyping(str, color);
        println();
    }

    void printLikeTyping(String str, String color, String style) {
        textStyle(style);
        printLikeTyping(str, color);
        reset();
    }

    void printlnLikeTyping(String str, String color, String style) {
        printLikeTyping(str, color, style);
        println();
    }

    void printNLineBreak(int n) {
        for (int i = 0; i < n; i += 1) {
            println();
        }
    }

    void textStyle(String style) {
        switch (style) {
            case "underline":
                print("\033[4m");
                break;
        }
    }

    void clearTerminal() {
        print("\033[H\033[2J");
    }

    /* ========================================================================== */
    /* FILE DISPLAYING */
    /* ========================================================================== */
    void displayFileContent(String path) {
        File content = newFile(path);
        // on s'arrête dès qu'on lit une ligne null (fin du fichier) affichage du
        // contenu de la ligne suivante
        while (ready(content)) {
            println(readLine(content));
        }
    }

    /* ========================================================================== */
    /* OPTIONS MENU */
    /* ========================================================================== */

    void displayMenuOptions(MenuOption[] options) {
        for (int i = 0; i < length(options); i += 1) {
            print(options[i].code + HORIZONTAL_SEPARATOR);
            println(options[i].text);
            printNLineBreak(options[i].spaceAfter);
        }
        printNLineBreak(2);
    }

    MenuOption askMenuOptionChoice(MenuOption[] options) {
        String userInput;
        do {
            displayMenuQuestion(options);
            userInput = toUpper(readString());
        } while (indexOf(options, userInput) == -1);
        return options[indexOf(options, userInput)];
    }

    void displayMenuQuestion(MenuOption[] options) {
        print("Faites votre choix ! (");
        for (int i = 0; i < length(options); i += 1) {
            print(options[i].code);
            if (i != length(options) - 1) {
                print("|");
            }
        }
        print(") >> ");
    }

    int indexOf(MenuOption[] options, String optionCode) {
        for (int i = 0; i < length(options); i += 1) {
            if (equals(optionCode, options[i].code)) {
                return i;
            }
        }
        return -1;
    }

    /* ========================================================================== */
    /* GET RANDOMLY */
    /* ========================================================================== */
    int random(int min, int max) {
        return (int) ((random() * (max - min + 1)) + min);
    }

    Question getRandomQuestionByCharacter(Question[] questions, Character character) {
        Question question;
        do {
            question = getRandomQuestion(questions);
        } while (indexOf(question.charactersJobs, character.job) == -1);
        return question;
    }

    Question getRandomQuestion(Question[] questions) {
        return questions[random(0, length(questions) - 1)];
    }

    Character getRandomCharacter(Character[] characters) {
        return characters[random(0, length(characters) - 1)];
    }

    Loot getRandomLootWeightedByRarity(Loot[] loots) {
        // Rarity = weight
        // More higher is the weight, more common is the loot

        // Sum of all the weights
        int weightsSum = 0;
        for (int i = 0; i < length(loots); i += 1) {
            weightsSum += loots[i].rarity;
        }

        int rnd = random(0, weightsSum - 1);

        for (int i = 0; i < length(loots); i += 1) {
            if (rnd < loots[i].rarity) {
                return loots[i];
            }
            rnd -= loots[i].rarity;
        }
        // This case will never happen (except if loots is empty) but we need to
        // return something
        return null;
    }

    // To simplify the process of getting a random loot with a category in
    // the lootCategories array, we just pick randomly a Loot and if his category
    // isn't in lootCategories, we retry the process (the other option was to filter
    // the array of loots by category and then slice it because we don't know the
    // length of next array in advance)
    Loot getRandomLootByCategories(String[] lootCategories) {

        Loot randomLoot = getRandomLootWeightedByRarity(globalLoots);
        // randomLoot will never be null because globalLoots is never empty

        if (indexOf(lootCategories, randomLoot.category) == -1 && length(lootCategories) != 0) {
            return getRandomLootByCategories(lootCategories);
        }

        return randomLoot;
    }

    /* ========================================================================== */
    /* ========================================================================== */
    /* ================================ TESTS =================================== */
    /* ========================================================================== */
    /* ========================================================================== */

    void testJoinInt() {
        assertEquals("4-8-9-10", join(new int[] { 4, 8, 9, 10 }, '-'));
        assertEquals("2", join(new int[] { 2 }, '-'));
        assertEquals("", join(new int[] {}, '-'));
    }

    void testJoinString() {
        assertEquals("this is a test", join(new String[] { "this", "is", "a", "test" }, ' '));
        assertEquals("incredible", join(new String[] { "incredible" }, '-'));
        assertEquals("", join(new String[] {}, '-'));
    }

    void testSplitToInt() {
        assertArrayEquals(new int[] { 4, 8, 9, 10 }, splitToInt("4-8-9-10", '-'));
        assertArrayEquals(new int[] { 0, 7, 2, 0 }, splitToInt("-7-2-", '-'));
        assertArrayEquals(new int[] { 0, 0 }, splitToInt("-", '-'));
        assertArrayEquals(new int[] {}, splitToInt("", '-'));
    }

    void testSplitToString() {
        assertArrayEquals(new String[] { "this", "is", "a", "test" }, splitToString("this is a test", ' '));
        assertArrayEquals(new String[] { "", "this", "is", "a", "test", "" }, splitToString("@this@is@a@test@", '@'));
        assertArrayEquals(new String[] { "", "", "a", "", "test", "", "" }, splitToString("@@a@@test@@", '@'));

        assertArrayEquals(new String[] { "incredible" }, splitToString("incredible", ' '));
        // assertArrayEquals(new String[] {}, splitToString("", '-')); =>
        // assertArrayEquals considers two empty arrays aren't equals ?
    }

    // Ce test permet de vérifier que la fonction testGetRandomLootWeightedByRarity
    // renvoie bien les loots très rares moins souvent que les loots moyennement
    // rares moins souvent que les
    // loots communs
    void testGetRandomLootWeightedByRarity() {
        Loot[] loots = new Loot[] {
                createLoot(new String[] { "0", "très rare", "test", "1", "test", "0" }),
                createLoot(new String[] { "1", "moyennement rare", "test", "2", "test", "0" }),
                createLoot(new String[] { "2", "commun", "test", "3", "test", "0" }),
        };

        int[] counts = new int[] { 0, 0, 0 };

        for (int i = 0; i < 100000; i += 1) {
            Loot randomLoot = getRandomLootWeightedByRarity(loots);
            counts[randomLoot.id] += 1;
        }
        assertTrue(counts[0] < counts[1] && counts[1] < counts[2]);
    }
}