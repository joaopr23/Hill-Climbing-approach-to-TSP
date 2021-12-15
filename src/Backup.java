import java.util.*;

class Backup {
	static int n; //numero de pontos
	static int m; //range de coordenadas
	
	static Ponto arrayPontos[];
	static HashSet<Ponto> pontosGerados;

	static HashSet<Ponto> pontosUsados;
	static ArrayList<Ponto> caminho;
	static ArrayList<Integer> caminhoIndex;

	static Aresta[] arestas;
	static HashSet<Aresta> setAresta;

	static ArrayList<ArrayList<Integer>> interceptions;

	static int iteracoesHCperimetro;
	static int iteracoesHCfi;
	static int iteracoesHCrandom;
	static int iteracoesHCcruzamentos;

	static class Node {
		Aresta[] arestasNo;
		LinkedList<Node> filhos;
		int perimetro;
		int cruzamentos;
		
		Node(Aresta[] arestasNo) {
			this.arestasNo = arestasNo;
		}


		void perimetro() {
			
			perimetro = 0;
			for(int v = 0 ; v < arestasNo.length ; v++) {
				Ponto p1 = arrayPontos[arestasNo[v].i];
				Ponto p2 = arrayPontos[arestasNo[v].j];

				int x1 = p1.x;
				int y1 = p1.y;
				int x2 = p2.x;
				int y2 = p2.y;
				perimetro = perimetro + ((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));	
			}
		}
	}

	public static class Ponto {

		int x;
		int y;

		Ponto(int x,int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode () {
			return Objects.hash(x,y);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Ponto other = (Ponto) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	static class Aresta {
		int i;
		int j;

		@Override
		public int hashCode () {
			return Objects.hash(i,j);
		}

		Aresta (int i, int j) {
			this.i = i;
			this.j = j;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Aresta other = (Aresta) obj;
			if (i != other.i)
				return false;
			if (j != other.j)
				return false;
			return true;
		}
	}

	static int getRandomNumber(int m) {
		return (int) ((Math.random() * (m - (-m)) + (-m)));
	}

	static boolean collinear(int x1, int y1, int x2,int y2, int x3, int y3) {
		
		int a = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
	
		if (a == 0)
			return true;
		else
			return false;
	}
		
	static void generatePoints() {
		arrayPontos = new Ponto[n];
		pontosGerados = new HashSet<Ponto>();

		for(int i = 0 ; i < n ; i++) {
			Ponto ponto = new Ponto(getRandomNumber(m+1),getRandomNumber(m+1));

			if(i<2) {
				arrayPontos[i] = ponto;
			}
			else {

				outerloop:
				for(int u = 0 ; u < i ; u++) {
					for(int v = 0 ; v < i ; v++) {	
						if (u!=v) {
							if(!collinear(arrayPontos[u].x,arrayPontos[u].y,arrayPontos[v].x,arrayPontos[v].y,ponto.x,ponto.y)) {
								arrayPontos[i] = ponto;
							}
							else {
								i--;
								break outerloop;
							}
						}
					}
				}
			}
		}
		printPontosGerados();
	}

	static void printPontosGerados() {
		System.out.println("\n1 - Pontos gerados aleatoriamente:");
		for(int i = 0 ; i < n ; i++) {
			System.out.println("Ponto "  + i + ": (" + arrayPontos[i].x + "," + arrayPontos[i].y + ")");
		}
	}

	static void generateNNFpermutation () {
		caminhoIndex = new ArrayList<Integer>();
		pontosUsados = new HashSet<Ponto>();

		Random rand = new Random();
		int randomIndex = rand.nextInt(n); 

		caminhoIndex.add(randomIndex);
		pontosUsados.add(arrayPontos[randomIndex]);

		while(caminhoIndex.size()!=n) {
			Ponto p1 = arrayPontos[caminhoIndex.get(caminhoIndex.size()-1)];

			int minDistance = Integer.MAX_VALUE;
			int bestIndex = -1;

			for(int i = 0 ; i < n ; i++) {
				if((!p1.equals(arrayPontos[i])) && (!pontosUsados.contains(arrayPontos[i]))) {
					int x1 = p1.x;
					int y1 = p1.y;
					int x2 = arrayPontos[i].x;
					int y2 = arrayPontos[i].y;
					int distance = ((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

					if (distance < minDistance) {
						minDistance = distance;
						bestIndex = i;
					}
				}
			}
			caminhoIndex.add(bestIndex);
			pontosUsados.add(arrayPontos[bestIndex]);
		}
		printNNFpermutation();
	}

	static void printNNFpermutation () {
		System.out.println("\n2 - b) Permutacao de pontos gerada pela heuristica nearest-neighbour first: " + caminhoIndex.toString());
	}

	static void arestasConversion () {
		arestas = new Aresta[n];
		setAresta = new HashSet<Aresta>();

		for(int i = 0 ; i < n-1; i++) {
			arestas[i] = new Aresta(caminhoIndex.get(i),caminhoIndex.get(i+1));
			setAresta.add(arestas[i]);
		}
		arestas[n-1] = new Aresta(caminhoIndex.get(caminhoIndex.size()-1), caminhoIndex.get(0));

		printArestas();
	}

	static void printArestas () {
		System.out.println("\nArestas resultantes:");
		for(int i = 0 ; i < arestas.length;i++) {
			System.out.println(i + ": (Ponto " + arestas[i].i + ",Ponto " + arestas[i].j + ")");
		}
	}

	static void generateRandomPermutation () {
		ArrayList<Integer> ordered = new ArrayList<Integer>();
		for(int i = 0 ; i < n ; i++) {
			ordered.add(i);
		}
		ArrayList<Integer> random = new ArrayList<>(ordered);
		Collections.shuffle(random);

		caminhoIndex = random;

		System.out.println("\n2 - a) Permutacao aleatoria de pontos: " + random.toString());
	}

	static void calculateInterceptions () {

		System.out.println("\n3 - Intersecoes: ");
		interceptions = new ArrayList<ArrayList<Integer>>();
		int index = 0;
		HashSet<String> arestasmarcadas = new HashSet<String>();

		for(int i = 0 ; i < arestas.length; i++) {
			for(int j = 0 ; j < arestas.length ; j++) {
				if((i!=j) && (j!=(i-1)) && (j!=(i+1))) {
					if(!((i==(arestas.length-1) && j == 0) || (i==0 && j==(arestas.length-1)))) {
						Aresta aresta1 = arestas[i];
						Aresta aresta2 = arestas[j];

						Ponto p1 = arrayPontos[arestas[i].i];
						Ponto p2 = arrayPontos[arestas[i].j];
						Ponto q1 = arrayPontos[arestas[j].i];
						Ponto q2 = arrayPontos[arestas[j].j];


						if(line_intersection_old(p1.x,p1.y,p2.x,p2.y,q1.x,q1.y,q2.x,q2.y)) {

							String testString1 = "";
							testString1 = testString1 + j;
							testString1 = testString1 + i;
							String testString2 = "";
							testString2 = testString2 + i;
							testString2 = testString2 + j;
							if(!arestasmarcadas.contains(testString1) && !arestasmarcadas.contains(testString2)) {
								System.out.println("Aresta " + i + " (" + arrayPontos[arestas[i].i].x + "," + arrayPontos[arestas[i].i].y + "),(" + arrayPontos[arestas[i].j].x + "," + arrayPontos[arestas[i].j].y + ")"  + " interseta com" + " aresta " + j + " (" + arrayPontos[arestas[j].i].x + "," + arrayPontos[arestas[j].i].y + "),(" + arrayPontos[arestas[j].j].x + "," + arrayPontos[arestas[j].j].y + ")");
								interceptions.add(new ArrayList<Integer>(Arrays.asList(i,j)));
								arestasmarcadas.add(testString1);
								arestasmarcadas.add(testString2);
							}
						}
					}
				}
			}
		}
	}

	static int calculateInterceptionsFilhosSize (Aresta[] arestas) {

		interceptions = new ArrayList<ArrayList<Integer>>();
		int index = 0;
		HashSet<String> arestasmarcadas = new HashSet<String>();

		for(int i = 0 ; i < arestas.length; i++) {
			for(int j = 0 ; j < arestas.length ; j++) {
				if((i!=j) && (j!=(i-1)) && (j!=(i+1))) {
					if(!((i==(arestas.length-1) && j == 0) || (i==0 && j==(arestas.length-1)))) {
						Aresta aresta1 = arestas[i];
						Aresta aresta2 = arestas[j];

						Ponto p1 = arrayPontos[arestas[i].i];
						Ponto p2 = arrayPontos[arestas[i].j];
						Ponto q1 = arrayPontos[arestas[j].i];
						Ponto q2 = arrayPontos[arestas[j].j];

						if(line_intersection_old(p1.x,p1.y,p2.x,p2.y,q1.x,q1.y,q2.x,q2.y)) {
							
							String testString1 = "";
							testString1 = testString1 + j;
							testString1 = testString1 + i;
							String testString2 = "";
							testString2 = testString2 + i;
							testString2 = testString2 + j;
							if(!arestasmarcadas.contains(testString1) && !arestasmarcadas.contains(testString2)) {
								//System.out.println("*Aresta " + i + " (" + arrayPontos[arestas[i].i].x + "," + arrayPontos[arestas[i].i].y + "),(" + arrayPontos[arestas[i].j].x + "," + arrayPontos[arestas[i].j].y + ")"  + " interseta com" + " aresta " + j + " (" + arrayPontos[arestas[j].i].x + "," + arrayPontos[arestas[j].i].y + "),(" + arrayPontos[arestas[j].j].x + "," + arrayPontos[arestas[j].j].y + ")");
								interceptions.add(new ArrayList<Integer>(Arrays.asList(i,j)));
								arestasmarcadas.add(testString1);
								arestasmarcadas.add(testString2);
							}
						}
					}
				}
			}
		}
		return interceptions.size();
	}


	static ArrayList<ArrayList<Integer>> calculateInterceptionsFilhosList (Aresta[] arestasf) {
		ArrayList<ArrayList<Integer>> interceptionsf = new ArrayList<ArrayList<Integer>>();
		int index = 0;
		HashSet<String> arestasmarcadasf = new HashSet<String>();

		for(int i = 0 ; i < arestasf.length; i++) {
			for(int j = 0 ; j < arestasf.length ; j++) {
				if((i!=j) && (j!=(i-1)) && (j!=(i+1))) {
					if(!((i==(arestasf.length-1) && j == 0) || (i==0 && j==(arestasf.length-1)))) {
						Aresta aresta1 = arestasf[i];
						Aresta aresta2 = arestasf[j];

						Ponto p1 = arrayPontos[arestasf[i].i];
						Ponto p2 = arrayPontos[arestasf[i].j];
						Ponto q1 = arrayPontos[arestasf[j].i];
						Ponto q2 = arrayPontos[arestasf[j].j];

						if(line_intersection_old(p1.x,p1.y,p2.x,p2.y,q1.x,q1.y,q2.x,q2.y)) {
							
							String testString1 = "";
							testString1 = testString1 + j;
							testString1 = testString1 + i;
							String testString2 = "";
							testString2 = testString2 + i;
							testString2 = testString2 + j;
							if(!arestasmarcadasf.contains(testString1) && !arestasmarcadasf.contains(testString2)) {
								interceptionsf.add(new ArrayList<Integer>(Arrays.asList(i,j)));
								arestasmarcadasf.add(testString1);
								arestasmarcadasf.add(testString2);
							}
						}
					}
				}
			}
		}
		return interceptionsf;
	}

	static Aresta[] twoExchange(Aresta[] arestas, ArrayList<ArrayList<Integer>> interceptions) {
		Aresta[] arestasTeste1;
        Aresta[] arestasTeste2;
		
    	int aresta1Index = interceptions.get(interceptions.size()-1).get(0);
		int aresta2Index = interceptions.get(interceptions.size()-1).get(1);

  		arestasTeste1 = arestas.clone();
  		arestasTeste2 = arestas.clone();


		HashSet<Aresta> setArestaF = new HashSet<Aresta>();

  		for(int i =0 ; i < arestas.length ; i++) {
  			setArestaF.add(arestas[i]);
  		}

		arestasTeste1[aresta1Index] = new Aresta(arestas[aresta1Index].i,(arestas[aresta2Index].i));
		arestasTeste1[aresta2Index] = new Aresta(arestas[aresta1Index].j,(arestas[aresta2Index].j));

		boolean verify = true;

		if(!setArestaF.contains(arestasTeste1[aresta1Index]) && !setArestaF.contains(arestasTeste1[aresta2Index])) {
            
            int i=aresta1Index;
            int j= aresta2Index; 

            int start = i+1;
            int end = j-1;
             
            
            for(int v = 0 ; v <= (end-start) ; v++) {
                arestasTeste1[start+v] = new Aresta(arestas[end-v].j,(arestas[end-v].i)); 
            }
                      
            for(int x = 0 ; x < arestas.length-2 ; x++) {

                if(arestasTeste1[x].j != arestasTeste1[x+1].i)  {
                    verify = false;
                    break;
                }
            }
            
            if(arestasTeste1[arestas.length-1].j!=arestasTeste1[0].i) {//verificar se a ultima aresta liga com a primeira
                verify=false;
            }
        }
        else{
            verify=false;
            	
        	}

        	if(!verify){



				boolean verify2 = true;
				
            	arestasTeste2[aresta1Index] = new Aresta(arestas[aresta1Index].i,(arestas[aresta2Index].j));//AD
		    	arestasTeste2[aresta2Index] = new Aresta(arestas[aresta2Index].i,(arestas[aresta1Index].j));//CB       
            
            	verify2=true;
            	if(verify2==true){
                	
		        }
			}	
		interceptions.remove(interceptions.size()-1);
			if(verify) return arestasTeste1;
			return arestasTeste2;
	}

		
	//https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
	static boolean line_intersection_old(int p0_x, int p0_y, int p1_x, int p1_y, int p2_x, int p2_y, int p3_x, int p3_y) {
    	float s1_x, s1_y, s2_x, s2_y;
    	s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
    	s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

    	float s, t;
    	s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
    	t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

   		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
 			return true;
 		}
		return false  ; // No collision 

	}

	static void HCperimetro (Node pai) {//4 a)

		iteracoesHCperimetro++;

		pai.perimetro();


		int bestPerimetroIndex = -1;
		int minPerimetro = Integer.MAX_VALUE;

		ArrayList<ArrayList<Integer>> interceptions = calculateInterceptionsFilhosList(pai.arestasNo);//calcula as intersecoes 
		
		if(interceptions.size()==0) {
			System.out.println("solucao encontrada:");

			for(int j = 0 ; j < pai.arestasNo.length;j++) {
				System.out.println(j + "(Ponto " + pai.arestasNo[j].i + ",Ponto " + pai.arestasNo[j].j + ")");
			}
			System.out.println();
			return;
		}
		else{
			
			LinkedList<Node> filhos = new LinkedList<Node>();

			while(interceptions.size()>0) {
				filhos.add(new Node(twoExchange(pai.arestasNo, interceptions)));//mete as solucoes a resolver na lista
			}

			for(int i = 0 ; i < filhos.size() ; i++) {
				Node filho = filhos.get(i);
				filho.perimetro();
				System.out.println("Perimetro filho "+i+": "+filho.perimetro);
				if(filho.perimetro < minPerimetro) {
					bestPerimetroIndex = i;
					minPerimetro = filho.perimetro;
				}						
			}
			HCperimetro(filhos.get(bestPerimetroIndex));
		}
	}

	static void HCfi (Node pai) {//4 b)

		iteracoesHCfi++;


		ArrayList<ArrayList<Integer>> interceptions = calculateInterceptionsFilhosList(pai.arestasNo); 
		if(interceptions.size()==0) {
			System.out.println("Solucao encontrada:");

			for(int j = 0 ; j < pai.arestasNo.length;j++) {
				System.out.println(j + "(Ponto " + pai.arestasNo[j].i + ",Ponto " + pai.arestasNo[j].j + ")");
			}
			System.out.println();


			return;
		}
		LinkedList<Node> filhos = new LinkedList<Node>();

		while(interceptions.size()>0) {
			filhos.add(new Node(twoExchange(pai.arestasNo, interceptions)));
		}


		HCfi(filhos.get(0));
	}

	static void HCcruzamentos (Node pai) {//4 c)

		iteracoesHCcruzamentos++;


		ArrayList<ArrayList<Integer>> interceptions = calculateInterceptionsFilhosList(pai.arestasNo); 
		if(interceptions.size()==0) {
			System.out.println("Solucao encontrada:");

			for(int j = 0 ; j < pai.arestasNo.length;j++) {
				System.out.println(j + "(Ponto " + pai.arestasNo[j].i + ",Ponto " + pai.arestasNo[j].j + ")");
			}
			System.out.println();

			return;
		}
		LinkedList<Node> filhos = new LinkedList<Node>();

		while(interceptions.size()>0) {
			filhos.add(new Node(twoExchange(pai.arestasNo, interceptions)));
		}

		int bestInterceptionIndex = -1;
		int minInterceptions = Integer.MAX_VALUE;

		for(int i = 0 ; i < filhos.size() ; i++) {
			Node filho = filhos.get(i);
			filho.cruzamentos = calculateInterceptionsFilhosSize(filho.arestasNo);

			if(filho.cruzamentos < minInterceptions) {
				bestInterceptionIndex = i;
				 minInterceptions = filho.perimetro;
			}						
		}

		HCcruzamentos(filhos.get(bestInterceptionIndex));
	}

	static void HCrandom (Node pai) {//4 d)

		iteracoesHCrandom++;


		ArrayList<ArrayList<Integer>> interceptions = calculateInterceptionsFilhosList(pai.arestasNo); 
		if(interceptions.size()==0) {
			System.out.println("Solucao encontrada:");

			for(int j = 0 ; j < pai.arestasNo.length;j++) {
				System.out.println(j + "(Ponto " + pai.arestasNo[j].i + ",Ponto " + pai.arestasNo[j].j + ")");
			}
			System.out.println();

			return;
		}
		LinkedList<Node> filhos = new LinkedList<Node>();

		while(interceptions.size()>0) {
			filhos.add(new Node(twoExchange(pai.arestasNo, interceptions)));
		}


		Random rand = new Random();
		int randomIndex = rand.nextInt(filhos.size());

		HCrandom(filhos.get(randomIndex));
	}

	static void SA(Node og){

		double temperatura=100.0;
		
		int delta=0;
		double probabilidade;

		og.cruzamentos = calculateInterceptionsFilhosSize(og.arestasNo);

		ArrayList<ArrayList<Integer>> interceptions = calculateInterceptionsFilhosList(og.arestasNo); 
		if(interceptions.size()==0) {
			System.out.println("solucao encontrada, nao tem intercecees23");
			return ;
		}
		
		LinkedList<Node> filhos = new LinkedList<Node>();

		while(interceptions.size()>0) {
			filhos.add(new Node(twoExchange(og.arestasNo, interceptions)));
		}

		if (filhos.size()==0) {
			System.out.println("solucao encontrda, nao tem filhos");
			return;
		}

		while(true) {
	
			temperatura=temperatura*0.9;//reducao geometrica t=t*α
			
			int temp=(int)temperatura;

			

			if(temp==0.0){

				System.out.println("Solucao encontrada");
				
				break;
			}

			Random rand = new Random();
			int randomIndex = rand.nextInt(filhos.size());

			Node filho = filhos.get(randomIndex);//filho aleatorio de og

			filho.cruzamentos = calculateInterceptionsFilhosSize(filho.arestasNo);


			delta=filho.cruzamentos-interceptions.size();//ΔE

			if(delta>0){
				og=filho;
			}
			else{
				probabilidade=Math.exp(Math.abs(delta)/temperatura);//e^(ΔE/T)
				
				double randomNumber = rand.nextDouble();//[0-1[
				if(randomNumber<=probabilidade){
					og=filho;
				}
			}
		}
		for(int i=0;i<og.arestasNo.length;i++){
			System.out.println("aresta:"+i+" ("+og.arestasNo[i].i+","+og.arestasNo[i].j+")");
		}
	}


	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out.println("Inserir range de coordenadas (m)");

		m = sc.nextInt();

		System.out.println("Inserir numero de pontos (n)");
		n = sc.nextInt();

		generatePoints();

		int choice;

		System.out.println("\nPartir de candidato aleatorio(0) ou gerado por nearest neighbour first(1)?");

		choice = sc.nextInt();

		if(choice==0) {

			generateRandomPermutation();
			

		}
		else generateNNFpermutation();



		//converter a permutacao obtida por nearest neighbour first para um conjunto de arestas
		arestasConversion();

		//calcular os pares de arestas que se intercetam
		calculateInterceptions();
		

		Node s = new Node(arestas);

		s.filhos = new LinkedList<Node>();//caminhos resolvido

		
		while(interceptions.size() > 0) {
			s.filhos.add(new Node (twoExchange(arestas,interceptions)));
		}

		int size = s.filhos.size();

		if(size>0){

			iteracoesHCperimetro = 1;
			iteracoesHCcruzamentos = 1;
			iteracoesHCrandom = 1;
			iteracoesHCfi = 1;

		
			System.out.println("\nvizinhanca:\n");

			int bestPerimetroIndex = -1;
			int bestInterceptionIndex = -1;
			int minInterceptions =  Integer.MAX_VALUE;
			int minPerimetro = Integer.MAX_VALUE;

			for(int i = 0 ; i < size ; i++) {
				Node filho = s.filhos.get(i);
				filho.perimetro();//calcula valor de perimetro
				filho.cruzamentos = calculateInterceptionsFilhosSize(filho.arestasNo);//num de intersecoes deste filho 
				if(filho.cruzamentos < minInterceptions) {//filho com menos intersecoes
					bestInterceptionIndex = i;
					minInterceptions = calculateInterceptionsFilhosSize(filho.arestasNo);
				}
				if(filho.perimetro < minPerimetro) {//filho com menor perimetro
					bestPerimetroIndex = i;
					minPerimetro = filho.perimetro;
				}

				System.out.println("filho " + i +  " (arestas): \n");

				for(int j = 0 ; j < filho.arestasNo.length;j++) {
					System.out.println(j + "(Ponto " + filho.arestasNo[j].i + ",Ponto " + filho.arestasNo[j].j + ")");
				}

				System.out.println("perimetro: " +  filho.perimetro);
				System.out.println("cruzamentos de arestas: " + filho.cruzamentos + "\n");
			}
			
			System.out.println();

			Random rand = new Random();
			int randomIndex = rand.nextInt(size);

			System.out.println("Hill climbing, 4 - a)  best-improvement first");

			//novo pai -> 
			Node pai = s.filhos.get(bestPerimetroIndex);
			HCperimetro(pai);


			System.out.println("4 - b) first-improvement");

			pai = s.filhos.get(0); 	
			HCfi(pai);

			System.out.println("4 - c) menos cruzamentos de arestas)");

			pai = s.filhos.get(bestInterceptionIndex);	
			HCcruzamentos(pai);

			System.out.println("4 - d) qualquer candidato na vizinhanca");

			HCrandom(s.filhos.get(randomIndex));

			System.out.println("Numero de iteracoes Hill CLimbing por cada heuristica:");
			System.out.println("a) Best-improvement first: " + iteracoesHCperimetro);
			System.out.println("b) First-improvement: " + iteracoesHCfi);
			System.out.println("c) Menos cruzamentos de arestas: " + iteracoesHCcruzamentos);
			System.out.println("d) Qualquer candidato na vizinhanca: " + iteracoesHCrandom);

			System.out.println("\n5 - Simulated annealing");

			SA(s);

		}
		else{
			System.out.println("O candidato inicial nao tem intersecoes.");
		}	
	}
}
	