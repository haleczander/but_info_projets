class enigma extends Program {

    // CONSTANTES DE LA MACHINE
    String ALPHABET =	"ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String ROTOR1 = 	"EKMFLGDQVZNTOWYHXUSPAIBRCJ";
    String ROTOR2 = 	"AJDKSIRUXBLHWTMCQGZNPYFVOE"; 
    String ROTOR3 = 	"BDFHJLCPRTXVZNYEIWGAKMUSQO";
    String ROTOR4 = 	"ESOVPZJAYQUIRHXLNFTGKDCMWB";
    String ROTOR5 = 	"VZBRGITYUPSDNHLXAWMJQOFECK";
    String REFLECTEURA ="YRUHQSLDPXNGOKMIEBFZCWVJAT";
    String REFLECTEURB ="RDOBJNTKVEHMLFCWZAXGYIPSUQ";

	// ----------------------------------------------------------------------------------------
    // Fonction qui retourne un entier correspondant à la position d'une lettre donnée dans l'alphabet (A=0, B=1, C=2, ...)
    // Exemples :
    // entrée : 'A' -> sortie 0
    // entrée : 'B' -> sortie 1
    // ...
    // entrée : 'Z' -> sortie 25

    int lettreEnNombre(char lettre){
        //soustraction du A majuscule afin d'avoir la position dans l'alphabet ('A'-'A'=0)
    	//Forçage de type via le retour de la fonction
        return (int) (lettre - 'A');
    }


    // ----------------------------------------------------------------------------------------
    // Fonction qui retourne la lettre associée à une position (un entier) dans l'alphabet (0=A, 1=B, 2=C, ...)
    // Exemples :
    // entrée : 0  -> sortie : 'A'
    // entrée : 1  -> sortie : 'B'
    // ...
    // entrée : 25 -> sortie : 'Z'

    char nombreEnLettre(int nombre){
        //Addition du A majuscule afin d'avoir la position dans l'alphabet ('A'+ 0='A')
        //Forçage de type via le retour de la fonction
        return (char) (nombre+'A');
    }

	// -----------------------------------------------------
	//	Fonction test pour lettreEnNombre et nombreEnLettre
	// -----------------------------------------------------
    void testConversionsLettresNombres() {
		for (int i = 0; i < 26; i++){
			assertEquals(i, lettreEnNombre(charAt(ALPHABET, i)));
			assertEquals(charAt(ALPHABET, i), nombreEnLettre(i));
		}
    }

    // ----------------------------------------------------------------------------------------
    // Fonction qui permet de sélectionner un rotor
    // A partir d'un entier (entre 1 et 5) passé en paramètre, cette fonction retourne le rotor correspondant (la chaîne de caractère correspondante) 
    // Exemples :
    // entrée : 1 -> sortie : "EKMFLGDQVZNTOWYHXUSPAIBRCJ"   (ROTOR1)
    // entrée : 4 -> sortie : "ESOVPZJAYQUIRHXLNFTGKDCMWB"   (ROTOR4) 
    // ...

    String choixRotor(int numeroRotor){
	    // Alternatives en cascade afin de retourner la chaine correspondant au rotor selectionné
        String retour="";
        if (numeroRotor==1) {
            retour= ROTOR1;
        }
        else if (numeroRotor==2){
            retour= ROTOR2;
        }
        else if (numeroRotor==3){
            retour= ROTOR3;
        }
        else if (numeroRotor==4){
            retour= ROTOR4;
        }
        else if (numeroRotor==5){
            retour= ROTOR5;
        }  
            
        return retour;    
    }

	void testChoixRotor() {
		assertEquals(ROTOR1, choixRotor(1));
		assertEquals(ROTOR2, choixRotor(2));
		assertEquals(ROTOR3, choixRotor(3));
		assertEquals(ROTOR4, choixRotor(4));
		assertEquals(ROTOR5, choixRotor(5));
	}

    // ----------------------------------------------------------------------------------------
    // Fonction qui permet à l'utilisateur de sélectionner le réflecteur
    // A partir d'une lettre ('A' ou 'B') passée en paramètre, cette fonction retourne le réflecteur correspondant (la chaîne de caractère)
    // Exemples :
    // entrée : 'A' -> sortie : "YRUHQSLDPXNGOKMIEBFZCWVJAT"    (REFLECTEURA)
    // entrée : 'B' -> sortie : "RDOBJNTKVEHMLFCWZAXGYIPSUQ"    (REFLECTEURB) 
    String choixReflecteur(char lettreReflecteur){
		// Alternative : A, si B ou erreur: retour reflecteurB
        String retour="";
        if (lettreReflecteur=='A'){
			retour = REFLECTEURA;
		}
        else{
			retour = REFLECTEURB;
		}

        return retour;
    }

    // ----------------------------------------------------------------------------------------
    // Fonction qui permet à l'utilisateur de la machine de brancher les câbles reliant les paires (6) de lettres
    // Cette fonction doit retourner une chaîne de caractères de 6 lettres majuscules saisies au clavier par l'utilisateur (on supposera que ces 6 lettres sont distinctes)
    // Exemple :
    // Si l'utilisateur saisit les 6 paires suivantes : AV puis DE puis HO puis JK puis LS puis XQ, la fonction doit retourner "AVDEHOJKLSXQ"
    String cablageInitial(){
		String listeCablage = "", entree = "";

		while (length(listeCablage) < 12) {
			print("Entrez le cablage " + (int) (length(listeCablage) / 2 + 1) + ": ");
			entree = readString();
			// Le programme vérifie si l'utilisateur entre une bonne valeur
			// Entrée = 2 caractères et chaque caractère est une lettre ('A'<=char<='Z')
			if (length(entree) == 2 && (charAt(entree, 0) >= 'A' && charAt(entree, 0) <= 'Z') && (charAt(entree, 1) >= 'A' && charAt(entree, 1) <= 'Z') ) {
				listeCablage = listeCablage + entree;
			} else {
				println("Entrez une valeur correcte!");
			}
		}
		return listeCablage;

		// Version totale confiance à l'utilisateur (mauvaise idée)
		//Saisie des 6 paires sans vérification
		// String listeCablage = "";
		// for (int cpt = 0; cpt < 6; cpt++) {
		// 	listeCablage = listeCablage + readString();
		// }
		// return listeCablage;
    }

    void affichageCablageInitial(){
		//Imprime le cablage initial puis retourne à la ligne
		println(cablageInitial());
    }

    // ----------------------------------------------------------------------------------------
    // Fonction qui permet de décaler le rotor d'un rang vers la gauche
    // A partir d'une chaîne de caractères passée en paramètre, cette fonction retourne la chaîne de caractères décalée d'un cran vers la gauche, c'est-à-dire que la première lettre est déplacée à la fin de la chaîne.
    // Exemples :
    // entrée : "ABCDEFGHIJKLMNOPQRSTUVWXYZ" -> sortie : "BCDEFGHIJKLMNOPQRSTUVWXYZA"
    // entrée : "IFHUQSMDVHNQOIVHZ" -> sortie : "FHUQSMDVHNQOIVHZI"
    String decalageUnRang(String rotor){
		// La lettre du tout début de la chaîne va tout à la fin
		// sous-chaine de l'indice 1 à la fin puis ajout de l'indice 0 à la fin
		return substring(rotor, 1, length(rotor)) + charAt(rotor, 0);
    }
    // ----------------------------------------------------------------------------------------
    // Fonction qui retourne le rotor après avoir défini sa position initiale, c'est-à-dire après nb décalages.
    // A partir d'un rotor donné (une chaîne de caractères) et d'un entier nb donné, cette fonction retourne le rotor décalé de nb crans vers la gauche
    // Exemples :
    // entrées : "ABCDEFGHIJKLMNOPQRSTUVWXYZ" et 3 -> sortie : "DEFGHIJKLMNOPQRSTUVWXYZABC"
    // entrées : "IFHUQSMDVHNQOIVHZ" et 5 -> sortie : "SMDVHNQOIVHZIFHUQ"
    String positionInitialeRotor(String rotor, int position){
		// On appelle la fonction decalageUnRang "position" fois
		
		for (int cpt = 0; cpt < position; cpt++) {
			rotor = decalageUnRang(rotor);
		}
		//alternative : déplacement d'une sous-chaine allant de l'indice 0 à position à la fin de la chaine (cf voir decalageUnRang)
		//substring(rotor, position+1, length(rotor)) + substring(rotor,0,position);
		return rotor;
    }

	// Test des fonctions decalageUnRang et positionInitialeRotor
	void testDecalageRotors() {
		assertEquals("BCDEFGHIJKLMNOPQRSTUVWXYZA", decalageUnRang("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		assertEquals("FHUQSMDVHNQOIVHZI", decalageUnRang("IFHUQSMDVHNQOIVHZ"));

		assertEquals("DEFGHIJKLMNOPQRSTUVWXYZABC", positionInitialeRotor("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 3));
		assertEquals("SMDVHNQOIVHZIFHUQ", positionInitialeRotor("IFHUQSMDVHNQOIVHZ", 5));
	}

    // ----------------------------------------------------------------------------------------
    // Fonction qui recherche une lettre dans une chaîne de caractères
    // A partir d'une lettre donnée et d'une chaîne de caractère donnée, cette fonction retourne l'indice (la position) de la lettre dans la chaîne (-1 si absent)
    // Exemples : 
    // entrées 'C' et "ABCDE" -> sortie 2
    // entrées 'A' et "ABCDE" -> sortie 0
    // entrées 'E' et "ABCDE" -> sortie 4
    // entrées 'F' et "ABCDE" -> sortie -1
    // entrées 'F' et ROTOR1 -> sortie 3
    int indiceLettre(char lettre, String cablage){
		int indiceLettre = 0, resultat = -1;
		//Tant que l'on n'a pas trouvé la lettre dans le cablage et tant qu'il reste des lettres à balayer dans le cablage,
		//on compare l'indice du cablage à la lettre puis on incrémente de 1
		while (resultat == -1 && indiceLettre < length(cablage)) {
			//Si la lettre testée se trouve dans le cablage, le résultat devient l'indice en cours -> fin du while
			if (charAt(cablage, indiceLettre) == lettre) {
				resultat = indiceLettre;
			}
			indiceLettre = indiceLettre + 1;
		}
		return resultat;
    }

	void testIndiceLettre() {
		assertEquals(2, indiceLettre('C',"ABCDE"));
		assertEquals(0, indiceLettre('A',"ABCDE"));
		assertEquals(4, indiceLettre('E',"ABCDE"));
		assertEquals(-1, indiceLettre('F',"ABCDE"));
		assertEquals(3, indiceLettre('F',ROTOR1));
	}

    // ----------------------------------------------------------------------------------------
    // Fonction qui permet de vérifier si la lettre à décoder est reliée par un câble à une autre lettre. Si oui, elle est transformée en cette lettre, sinon elle reste identique.
    // A partir d'une lettre donnée et d'un cablâge donné (une chaîne de 12 caractères), cette fonction retourne la lettre transformée si elle fait partie d'une paire de lettres d'un des 6 câbles, la même lettre sinon.
    // Exemples : 
    // entrées : 'H' et "AVDEHOJKLSXQ" -> sortie 'O'    (car 3ème paire HO)
    // entrées : 'A' et "ABCDEFGHIJKL" -> sortie 'B'    (car 1ère paire AB)
    // entrées : 'B' et "ABCDEFGHIJKL" -> sortie 'A'    (car 1ère paire AB)
    // entrées : 'K' et "ABCDEFGHIJKL" -> sortie 'L'    (car 6ème paire KL)
    // entrées : 'D' et "ABCDEFGHIJKL" -> sortie 'C'    (car 2ème paire CD)
    // entrées : 'M' et "ABCDEFGHIJKL" -> sortie 'M'    (car M absent de la chaîne du cablâge)
    char valeurApresCablageDeDepart(char lettre, String cablage){
		// Si lettre dans cablage alors permutation entre les deux lettres de la paire
		int indiceLettre = indiceLettre(lettre, cablage);
		char resultat = lettre;
		//-1 = pas dans le cablage, rien ne se produit
		//si l'indice est pair, on prend le suivant (2 devient 3) ABCD : C devient D
		//Si l'indice est impair, on prend le précédent (1 devient 0) ABCD : B devient A
		if (indiceLettre != -1) {
			if (indiceLettre % 2 == 0) {
				resultat = charAt(cablage, indiceLettre + 1);
			} else {
				resultat = charAt(cablage, indiceLettre - 1);
			}
		}
		return resultat;
    }

	void testValeurApresCablageDeDepart() {
		assertEquals('O', valeurApresCablageDeDepart('H',"AVDEHOJKLSXQ"));
		assertEquals('B', valeurApresCablageDeDepart('A',"ABCDEFGHIJKL"));
		assertEquals('A', valeurApresCablageDeDepart('B',"ABCDEFGHIJKL"));
		assertEquals('L', valeurApresCablageDeDepart('K',"ABCDEFGHIJKL"));
		assertEquals('C', valeurApresCablageDeDepart('D',"ABCDEFGHIJKL"));
		assertEquals('M', valeurApresCablageDeDepart('M',"ABCDEFGHIJKL"));
	}

    // ----------------------------------------------------------------------------------------
    // Fonction qui retourne la nouvelle valeur après le passage dans un rotor
    // A partir d'une lettre donnée et d'un rotor donné (une chaîne de caractères), cette fonction retourne la lettre correspondante à la lettre passée en paramètre après passage dans le rotor donné.
    // Exemples :
    // entrées : 'A' et ROTOR1 -> sortie : 'E'
    // entrées : 'B' et ROTOR1 -> sortie : 'K'
    // entrées : 'Z' et ROTOR1 -> sortie : 'J'   
    // entrées : 'E' et "AJDKSIRUXBLHWTMCQGZNPYFVOE" -> sortie : S
    char passageDansUnRotor(char lettre, String rotor){
		//Recupération de la place de lettre dans l'alphabet classique
		//Récupération de la lettre de rotor à l'indice correspondant à la valeur récupérée ci-dessus
		return charAt(rotor, lettreEnNombre(lettre));
    }

    // ----------------------------------------------------------------------------------------
    // Fonction qui retourne la nouvelle valeur après le passage dans le réflecteur
    // A partir d'une lettre donnée et d'un réflecteur donné (une chaîne de caractères), cette fonction retourne la lettre correspondante à la lettre passée en paramètre après passage dans le réflecteur.
    // Exemples :
    // entrées : 'A' et REFLECTEURA -> sortie : 'Y'
    // entrées : 'B' et REFLECTEURA -> sortie : 'R'
    // entrées : 'Z' et "YRUHQSLDPXNGOKMIEBFZCWVJAT" -> sortie : 'T'   
    char passageDansLeReflecteur(char lettre, String reflecteur){
		//Recupération de la place de lettre dans l'alphabet classique
		//Récupération de la lettre de reflecteur à l'indice correspondant à la valeur récupérée ci-dessus
		return charAt(reflecteur, lettreEnNombre(lettre));
    }

	void testPassageDansUnRotorOuReflecteur() {
		assertEquals('E', passageDansUnRotor('A', ROTOR1));
		assertEquals('K', passageDansUnRotor('B', ROTOR1));
		assertEquals('J', passageDansUnRotor('Z', ROTOR1));
		assertEquals('S', passageDansUnRotor('E', "AJDKSIRUXBLHWTMCQGZNPYFVOE"));

		assertEquals('Y', passageDansLeReflecteur('A', REFLECTEURA));
		assertEquals('R', passageDansLeReflecteur('B', REFLECTEURA));
		assertEquals('T', passageDansLeReflecteur('Z', "YRUHQSLDPXNGOKMIEBFZCWVJAT"));
	}

	void testInverseRotor() {
		assertEquals('A', inverseRotor('E', ROTOR1));
		assertEquals('B', inverseRotor('K', ROTOR1));
		assertEquals('Z', inverseRotor('J', ROTOR1));
		assertEquals('E', inverseRotor('S', "AJDKSIRUXBLHWTMCQGZNPYFVOE"));
	}
    // ----------------------------------------------------------------------------------------
    // Fonction qui retourne la nouvelle valeur après le passage dans un rotor dans le sens inverse (pour le retour)
    // A partir d'une lettre donnée et d'un rotor donné (une chaîne de caractères), cette fonction retourne la lettre correspondante à la lettre passée en paramètre après passage en sens inverse dans le rotor.
    // Exemples :
    // entrées : 'E' et ROTOR1 -> sortie : 'A'
    // entrées : 'K' et ROTOR1 -> sortie : 'B'
    // entrées : 'J' et ROTOR1 -> sortie : 'Z'   
    // entrées : 'S' et "AJDKSIRUXBLHWTMCQGZNPYFVOE" -> sortie : E
    char inverseRotor(char lettre, String rotor){
		return charAt(ALPHABET, indiceLettre(lettre, rotor));
    }

	void testEnMajuscule() {
		assertEquals("HELLO WORLD!", enMajuscule("hello world!"));
		assertEquals("ENIGMA", enMajuscule("EnIgMa"));
		assertEquals("ALAN TURING", enMajuscule("alan turing"));
	}
    // ----------------------------------------------------------------------------------------
    // Fonction qui transforme une chaîne de caractères en majuscule
    String enMajuscule(String message){
		String resultat = "";
		char car;
		for (int indice = 0; indice < length(message); indice++) {
			car = charAt(message, indice);
			//Si la mettre à l'indice est une minuscule ( 'a'<=car<='z')
			if (car >= 'a' && car <= 'z') {
				// Transforme le caractère en majuscule
				//car - 'a' renvoie la place dans l'alphabet de la lettre (a=0;b=1)
				//+'A'= code ASCII majuscule -- nombreEnLettres
				resultat = resultat + (char) (car - 'a' + 'A');
			} else {
				// Laisse les autres caractères tels qu'ils sont
				resultat += car;
			}
			//resultat+=car
		}
		return resultat;	
    }
    // ----------------------------------------------------------------------------------------
    // PROGRAMME PRINCIPAL
    void algorithm(){
		println(" ------------------------------------\n| Simulation d'une machine Enigma M3 |\n ------------------------------------");
		
		// Initialisation des éléments
		// ---------------------------
		println("Quel type de configuration souhaitez-vous ? \n 1 : Configuration par défaut (Rotor 1 : III en position W, Rotor 2 : I en position D, Rotor 3 : V en position E, Réflecteur : B, Cablâge : AV - DE - HO - JK - LS - XQ, Message à décoder par défaut)\n 2 : Configuration personnalisée");
		int choix = readInt();
		while (choix<1 || choix>2){
			println("Erreur de saisie, choix : 1 ou 2");
			choix = readInt();
		}
		
		int rotor1,rotor2,rotor3;
		String R1,R2,R3;
		char position1,position2,position3;
		int decalage1,decalage2,decalage3;
		char choixRef;
		String refl;
		String cables;
		
		String message="";
		
		if (choix == 1){// Configuration pré-établie
			// // Rotors choisis
			rotor1 = 3;
			rotor2 = 1;
			rotor3 = 5;
			R1=choixRotor(rotor1);
			R2=choixRotor(rotor2);
			R3=choixRotor(rotor3);
			
			// // Position initiale des rotors choisis
			position1 = 'W';
			position2 = 'D';
			position3 = 'E';
			decalage1 = indiceLettre(position1,R1);
			R1 = positionInitialeRotor(R1,decalage1);
			decalage2 = indiceLettre(position2,R2);
			R2 = positionInitialeRotor(R2,decalage2);
			decalage3 = indiceLettre(position3,R3);
			R3 = positionInitialeRotor(R3,decalage3);
			
			// // Réflecteur choisi
			choixRef = 'B';
			refl = choixReflecteur(choixRef);
			
			// // Initialisation de la configuration du cablage de la machine par l'utilisateur
			cables = "AVDEHOJKLSXQ";
			
			// // Message à tester 
			message = "AKBAOKETGPVYHGWBSGSVUDTZEBNOXGFOBVYOJVTWFPIKC";
		}
		else{// Configuration choisie par l'utilisateur
			// // Choix des 3 rotors (distincts) parmi les 5
			// NB : On supposera que les numéros des 3 rotors sont bien compris entre 1 et 5 et qu'ils sont tous différents
			println("Entrez le numéro du premier rotor choisi (1, 2, 3, 4, 5)");
			rotor1 = readInt();
			println("Entrez le numéro du deuxième rotor choisi");
			rotor2 = readInt();
			println("Entrez le numéro du troisième rotor choisi");
			rotor3 = readInt();
			
			R1=choixRotor(rotor1);
			R2=choixRotor(rotor2);
			R3=choixRotor(rotor3);
			
			// // Choix de la position initiale des rotors choisis
			println("Entrez la position initiale du premier rotor choisi (A à Z)");
			position1 = readChar();
			println("Entrez la position initiale du deuxième rotor choisi (A à Z)");
			position2 = readChar();
			println("Entrez la position initiale du troisième rotor choisi (A à Z)");
			position3 = readChar();
			
			decalage1 = indiceLettre(position1,R1);
			R1 = positionInitialeRotor(R1,decalage1);
			decalage2 = indiceLettre(position2,R2);
			R2 = positionInitialeRotor(R2,decalage2);
			decalage3 = indiceLettre(position3,R3);
			R3 = positionInitialeRotor(R3,decalage3);
			
			// // Choix du réflecteur parmi les 2
			println("Entrez la lettre du réflecteur choisi (A ou B)");
			choixRef = readChar();
			refl = choixReflecteur(choixRef);
			
			// // Initialisation de la configuration du cablage de la machine par l'utilisateur
			println("Entrez les 6 couples de cablage:");
			cables = cablageInitial();
			
			// // Le message à coder
			println("Entrez le message à coder :");
			message = readString();
			message = enMajuscule(message);
		}
		
		String messageDecode="";

		char car;
		
		// Boucle principale du programme Enigma
		// -------------------------------------
		for (int tour=1 ; tour <= length(message) ; tour=tour+1){// la boucle s'arrête quand on a codé chaque lettre du message
			// 1. Récupération de la lettre courante dans le message à décoder
			// A COMPLETER => charAt
			car = charAt(message, tour-1);
			// 2. Passage par le câblage
			// A COMPLETER => valeurApresCablageDeDepart
			car = valeurApresCablageDeDepart(car, cables);
			// 3. Passage par les 3 rotors (premier, deuxième, troisième)
			// A COMPLETER => passageDansUnRotor
			car = passageDansUnRotor(car, R1);
			car = passageDansUnRotor(car, R2);
			car = passageDansUnRotor(car, R3);
			// 4. Passage par le réflecteur
			// A COMPLETER => passageDansLeReflecteur
			car = passageDansLeReflecteur(car, refl);
			// 5. Passage par les 3 rotors (dans le sens inverse : troisième, deuxième, premier)
			// A COMPLETER => inverseRotor
			car = inverseRotor(car, R3);
			car = inverseRotor(car, R2);
			car = inverseRotor(car, R1);
			// 6. Passage par le cablâge
			// A COMPLETER => valeurApresCablageDeDepart
			car = valeurApresCablageDeDepart(car, cables);
			
			// 7. Ajout de la lettre décodée au message
			// A COMPLETER => concaténation
			messageDecode += car;
			
			// 8. Préparation de l'itération suivante : décalage du premier rotor (à chaque fois); décalage du deuxième rotor si le premier a fait un tour complet (après 26 itérations) ; décalage du troisième rotor si le deuxième a fait un tour complet (après 26*26 itérations) 
			// 8.1 Le rotor 1 tourne d'un rang vers la gauche après chaque lettre (donc à chaque tour)
			// A COMPLETER => decalageUnRang sur R1
			R1 = decalageUnRang(R1);
			// 8.2 Si le rotor 1 a effectué un tour (toutes les 26 itérations) alors le rotor 2 tourne d'un cran vers la gauche
			// A COMPLETER
			if (tour % 26 == 0) {
				R2 = decalageUnRang(R2);
			}
			// 8.3 Si le rotor 2 a effectué un tour (toutes les 26*26 itérations) alors le rotor 3 tourne d'un cran vers la gauche 
			// A COMPLETER
			if (tour % (26*26) == 0) {
				R3 = decalageUnRang(R3);
			}
		}
		println();
		println("Le message décodé est : \n" + messageDecode);
    }	
}
