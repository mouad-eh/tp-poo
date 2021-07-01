import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//------------------------------ Interpreteur ------------------------------
class Interpreteur {
    static Map<String,Double> table_des_symboles = new HashMap<String,Double>();
    private LigneDeCommande ligne_de_commande;

    public void set_ligne_de_commande(String s) throws InvalidCommandException,UndeclaredVariableException,
	InvalidVariableNameException,ParentheseFermanteException,ParentheseOuvranteException,InvalidExpressionException{
        ligne_de_commande = new LigneDeCommande(s);
    }

    public void interpreter(){
        if(ligne_de_commande.getVariable() != null){
			Variable variable = ligne_de_commande.getVariable();
			table_des_symboles.put(variable.getNom(), variable.getValeur());
			System.out.println("Ok");
		}else{
			Double valeur = ligne_de_commande.getExpression().getValeur();
			System.out.println("La valeur est : " + valeur);
		}
    }
}
//------------------------------ LigneDeCommande ------------------------------
class LigneDeCommande{
    private Variable variable;
    private Expression expression;

    public LigneDeCommande(String s) throws InvalidCommandException,UndeclaredVariableException,
	InvalidVariableNameException,ParentheseFermanteException,ParentheseOuvranteException,InvalidExpressionException{
		if(LigneDeCommande.valide(s)){
			String decomp[] = s.split("\\s",2);
			String type = decomp[0];
			String secondPart = decomp[1].replaceAll("\\s", "");
			if(type.compareTo("let") == 0){
				decomp = secondPart.split("=",2);
				expression = new Expression(decomp[1]);
				expression.evaluer();
				variable = new Variable(decomp[0],expression.getValeur());
			}else{
				expression = new Expression(secondPart);
				expression.evaluer();
			}
		}else{
			throw new InvalidCommandException();
		}
    }

	static boolean valide(String s){
		Pattern let_pattern = Pattern.compile("^let(\\s+)(\\w+)(\\s*)=(\\s*)((\\w|[\\(\\)\\^\\*\\+-/]|\\s)+)$");
		Pattern print_pattern = Pattern.compile("^print(\\s+)((\\w|[\\(\\)\\^\\*\\+-/]|\\s)+)$");
		Matcher let_matcher = let_pattern.matcher(s);
		Matcher print_matcher = print_pattern.matcher(s);
		return let_matcher.find() || print_matcher.find();
	}
	public Variable getVariable(){
		return variable;
	}
	public Expression getExpression(){
		return expression;
	}
}
//------------------------------ Expression ------------------------------
class Expression{
    private List<String> decomposition = new ArrayList<String>();
    private Map<String,Terme> termes = new HashMap<String,Terme>();
    private double valeur;

    public Expression(String s) throws UndeclaredVariableException,
	ParentheseFermanteException,ParentheseOuvranteException,InvalidExpressionException{
		if(Expression.checkParenthese(s) > 0){
			throw new ParentheseFermanteException();
		}else if(Expression.checkParenthese(s) < 0){
			throw new ParentheseOuvranteException();
		}
		else{
			String list[] = s.split("((?<=\\+)|(?=\\+)|(?<=-)|(?=-))");
			String reg;
			Terme terme;
			int i = 0;
			boolean operateur = false;
			while( i< list.length ){
				if( Expression.checkParenthese(list[i]) != 0){
					operateur = false;
					reg = list[i];
					while(Expression.checkParenthese(reg) != 0){
					i++;	
					reg = reg + list[i];
					}
					decomposition.add(reg);
					terme = new Terme(reg);
					terme.evaluer();
					termes.put(reg, terme);
				}else{
					decomposition.add(list[i]);
					if(list[i].compareTo("+") != 0 && list[i].compareTo("-") != 0){
						operateur = false;
						terme = new Terme(list[i]);
						terme.evaluer();
						termes.put(list[i], terme);
					}else{
						if(operateur == true || i+1 == list.length){
							throw new InvalidExpressionException();
						}
						operateur = true;
					}
				}
				i++;
			}
		}
    }

    public void evaluer(){
		String terme;
		Double valeur = 0.0D;
		int counter = 0;
		while(counter < decomposition.size()) { 		      
			terme = decomposition.get(counter);

			if(terme.compareTo("-") == 0){
				valeur = valeur - termes.get(decomposition.get(counter+1)).getValeur();
				counter = counter + 2;
			}else if(terme.compareTo("+") == 0){
				valeur = valeur + termes.get(decomposition.get(counter+1)).getValeur();
				counter = counter + 2;
			}else{
				valeur = valeur + termes.get(terme).getValeur();
				counter = counter + 1;
			}
		}
		this.valeur = valeur;
    }

    public static int checkParenthese(String s){
		int open_count = s.length() - s.replace("(", "").length();
		int close_count = s.length() - s.replace(")", "").length();
		if( open_count == close_count ){
			return 0;
		}else if(open_count > close_count){
			return 1;
		}else{
			return -1;
		}
    }

	public Double getValeur(){
		return valeur;
	}
}
//------------------------------ Terme ------------------------------
class Terme {
	private List<String> decomposition = new ArrayList<String>();
	private Map<String, Facteur> facteurs = new HashMap<String, Facteur>();
	private double valeur;

	public Terme(String s) throws UndeclaredVariableException,ParentheseFermanteException
	,ParentheseOuvranteException,InvalidExpressionException{
		String list[] = s.split("((?<=\\*)|(?=\\*)|(?<=/)|(?=/))");
		String reg;
		Facteur facteur;
		int i = 0;
		boolean operateur = true;
		while (i < list.length ) {
			if ( Expression.checkParenthese(list[i]) != 0) {
				reg = list[i];
				while( Expression.checkParenthese(reg) != 0){
					i++;	
					reg = reg + list[i];
				}
				operateur = false;
				decomposition.add(reg);
				facteur = new Facteur(reg);
				facteur.evaluer();
				facteurs.put(reg, facteur);
			} else {
				decomposition.add(list[i]);
				if (list[i].compareTo("*") != 0 && list[i].compareTo("/") != 0) {
					operateur = false;
					facteur = new Facteur(list[i]);
					facteur.evaluer();
					facteurs.put(list[i], facteur);
				}else{
					if( operateur == true || i+1 == list.length){
						throw new InvalidExpressionException();
					}
					operateur = true;
				}
			}
			i++;
		}
	}

	public void evaluer(){
		String facteur;
		Double valeur = 1.0D;
		int counter = 0;
		while(counter < decomposition.size()) { 		      
			facteur = decomposition.get(counter);
			if(facteur.compareTo("/") == 0){
				valeur = valeur / facteurs.get(decomposition.get(counter + 1)).getValeur();
				counter = counter + 2;
			}else if(facteur.compareTo("*") == 0){
				valeur = valeur * facteurs.get(decomposition.get(counter + 1)).getValeur();
				counter = counter + 2;
			}else{
				valeur = valeur * facteurs.get(facteur).getValeur();
				counter = counter + 1;
			}
		}
		this.valeur = valeur;
    }

	public double getValeur() {
		return this.valeur;
	}
}
//------------------------------ Facteur ------------------------------
class Facteur {
	private List<String> decomposition = new ArrayList<String>();
	private HashMap<String, Element> elements = new HashMap<String, Element>();
	private double valeur;
	
	public Facteur(String s) throws UndeclaredVariableException,ParentheseFermanteException
	,ParentheseOuvranteException,InvalidExpressionException{
		String list[] = s.split("((?<=\\^)|(?=\\^))");
		String reg;
		Element element;
		int i = 0;
		boolean operateur = true;
		while( i< list.length ){
			if ( Expression.checkParenthese(list[i]) != 0){
				reg = list[i];
				while( Expression.checkParenthese(reg) != 0){
					i++;	
					reg = reg + list[i];
				}
				operateur = false;
				decomposition.add(reg);
				element = new Element(reg);
				element.evaluer();
				elements.put(reg, element);
			}else{
				decomposition.add(list[i]);
				if (list[i].compareTo("^") != 0){
					operateur = false;
					element = new Element(list[i]);
					element.evaluer();
					elements.put(list[i], element);
				}else{
					if( operateur == true || i+1 == list.length ){
						throw new InvalidExpressionException();
					}
					operateur = true;
				}
			}
			i++;
		}
    }

	public void evaluer(){
		String element;
		Double valeur = 1.0D;
		int counter = 0;
		while(counter < decomposition.size()) { 		      
			element = decomposition.get(counter);
			if(element.compareTo("^") == 0){
				valeur = Math.pow(valeur,elements.get(decomposition.get(counter+1)).getValeur());
				counter = counter + 2;
			}else{
				valeur = Math.pow(elements.get(element).getValeur(),valeur);
				counter = counter + 1;
			}
		}
		this.valeur = valeur;
	}

	public double getValeur() {
		return this.valeur;
	}
}
//------------------------------ Element ------------------------------
class Element {
	private Variable variable;
	private Expression expression;
	private FonctionStandard fonction;
	private Nombre nombre;
	private Double valeur;
	
	public Element(String element) throws UndeclaredVariableException,
	ParentheseFermanteException,ParentheseOuvranteException,InvalidExpressionException{	
		if(Nombre.valide(element)){
			nombre = new Nombre(element);
		}else if (Variable.valide(element)){
			variable = new Variable(element);
		}else{
			Pattern expression_pattern = Pattern.compile("^\\(((\\w|[\\(\\)\\^\\*\\+-/])+)\\)$");
			Pattern fonction_pattern = Pattern.compile("^(sin|cos|tab|abs|sqrt|log)\\(((\\w|[\\(\\)\\^\\*\\+-/])+)\\)$");
			Matcher expression_matcher = expression_pattern.matcher(element);
			Matcher fonction_matcher = fonction_pattern.matcher(element);
			if(expression_matcher.find()){
				expression = new Expression(expression_matcher.group(1));
				expression.evaluer();
			}else if(fonction_matcher.find()){
				fonction = FonctionStandard.valueOf(fonction_matcher.group(1).toUpperCase());
				expression = new Expression(fonction_matcher.group(2));
				expression.evaluer();
			}else{
				throw new InvalidExpressionException();
			}
		}
	}

	public void evaluer(){
		if(this.nombre != null){
			this.valeur = nombre.getValeur();
		}else if(this.variable != null){
			this.valeur = variable.getValeur();
		}else if(this.fonction==null && this.expression!= null){
			this.valeur = expression.getValeur();
		}else{
			if(this.fonction == FonctionStandard.COS || this.fonction == FonctionStandard.SIN || this.fonction == FonctionStandard.TAN ){
				this.valeur = fonction.call(Math.toRadians(expression.getValeur()));
			}else{
				this.valeur = fonction.call(expression.getValeur());
			}
		}
	}

	public Double getValeur(){
		return this.valeur;
	}
}
////------------------------------ Variable ------------------------------
class Variable {
    private String nom;
	private double valeur;

	public Variable(String nom, double valeur) throws InvalidVariableNameException {
		//ce constructeur est utilisé lors de la commande let
		if( Variable.valide(nom)){
        	this.nom = nom;
			this.valeur = valeur;}
		else{
			throw new InvalidVariableNameException();
		}
	}

    public Variable(String nom) throws UndeclaredVariableException{
		//ce constructeur est utilisé lors de l'évaluation de l'expression
		this.nom = nom;
		if(Interpreteur.table_des_symboles.get(nom) != null){
		this.valeur = Interpreteur.table_des_symboles.get(nom);}
		else{
			System.out.print("Erreur : " + nom + " ");
			throw new UndeclaredVariableException();
		}
    }

	public static boolean valide(String s){
		Pattern variable_pattern1 = Pattern.compile("^[a-z](\\w*)$");
		Pattern variable_pattern2 = Pattern.compile("^(let|print|sin|cos|tan|abs|sqrt|log)$");
		Matcher variable_matcher1 = variable_pattern1.matcher(s);
		Matcher variable_matcher2 = variable_pattern2.matcher(s);
		return variable_matcher1.find() && !variable_matcher2.find();
	}

	public String getNom(){
        return this.nom;
    }

	public double getValeur() {
		return this.valeur;
	}
}
//------------------------------ FunctionStandard ------------------------------
enum FonctionStandard {
    SIN(java.lang.Math::sin),
    COS(java.lang.Math::cos),
    TAN(java.lang.Math::tan),
    ABS(java.lang.Math::abs),
    SQRT(java.lang.Math::sqrt),
    LOG(java.lang.Math::log);

    private UnaryOperator<Double> function;

    private FonctionStandard(UnaryOperator<Double> function) {
        this.function = function;
    }

    public double call(double input) {
        return this.function.apply(input);
    }
}
//------------------------------ Nombre ------------------------------
class Nombre {
    private double valeur;
    public Nombre(String s) throws NumberFormatException{
        valeur = Double.parseDouble(s);
    }
	public static boolean valide(String s){
		Pattern nombre_pattern = Pattern.compile("^(\\d)+(\\.)?(\\d)*$");
		Matcher nombre_matcher = nombre_pattern.matcher(s);
		return nombre_matcher.find();
	}
	public double getValeur(){
		return this.valeur;
	}
}
//------------------------------ Les exceptions ------------------------------
class InvalidCommandException extends Exception{}
class InvalidVariableNameException extends Exception{}
class UndeclaredVariableException extends Exception{
	String nom;
}
class ParentheseFermanteException extends Exception{}
class ParentheseOuvranteException extends Exception{}
class InvalidExpressionException extends Exception{}

public class TestTp {
    public static void main(String[] args) {
		System.out.println("Entrez vos commandes. Tapez end pour terminer votre programme.\n");
		System.out.println("Une commande doit etre de la forme\n");
		System.out.println("let <variable> = <expression>\nou\nprint <expression>\n");
		Interpreteur interpreteur = new Interpreteur();
		Scanner scanner = new Scanner(System.in);
		String input = "";
		while( true ){
			System.out.print(">  ");
			input = scanner.nextLine();
			if (input.compareTo("end") == 0){
				scanner.close();
				System.out.print("fin du programme");
				break;
			}
			try{
			interpreteur.set_ligne_de_commande(input);
			interpreteur.interpreter();
			}
			catch(InvalidCommandException e){
				System.out.println("Erreur : commande invalide");
			}
			catch(InvalidVariableNameException e){
				System.out.println("Erreur : nom de variable invalide");
			}
			catch(UndeclaredVariableException e){
				System.out.println("variable non declaree");
			}
			catch(ParentheseOuvranteException e){
				System.out.println("Erreur : parenthese ouvrante manquante");
			}
			catch(ParentheseFermanteException e){
				System.out.println("Erreur : parenthese fermante manquante");
			}
			catch(InvalidExpressionException e){
				System.out.println("Erreur : Expression erronee");
			}
		}
    }
}