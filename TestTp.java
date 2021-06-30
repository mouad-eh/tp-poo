import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdk.management.jfr.RemoteRecordingStream;

//------------------------------ Interpreteur ------------------------------
class Interpreteur {
    Map<String,Double> table_des_symboles = new HashMap<String,Double>();
    private LigneDeCommande ligne_de_commande;

    public void set_ligne_de_commande(String s) throws InvalidCommandException{
        ligne_de_commande = new LigneDeCommande(s);
    }

    public void interpreter(){
		Double valeur = ligne_de_commande.getExpression().getValeur();
        if(ligne_de_commande.getType() == "let"){
			String nom = ligne_de_commande.getVariable().getNom();
			table_des_symboles.put(nom, valeur);
			System.out.println("Ok");
		}else{
			System.out.println("la valeur est : " + valeur);
		}
    }
}
//------------------------------ LigneDeCommande ------------------------------
class InvalidCommandException extends Exception{}
class LigneDeCommande{

	private String type;
    private Variable variable;
    private Expression expression;

    public LigneDeCommande(String s) throws InvalidCommandException{
		if(LigneDeCommande.valide(s)){
			String decomp[] = s.split("//s",2);
			type = decomp[0];
			String secondPart = decomp[1];
			if(type == "let"){
				decomp = secondPart.split("=",2);
				variable = new Variable(decomp[0]);
				expression = new Expression(decomp[1]);
				expression.evaluer();
			}else{
				expression = new Expression(secondPart);
				expression.evaluer();
			}
		}else{
			throw new InvalidCommandException();
		}
    }
	static boolean valide(String s){
		Pattern let_pattern = Pattern.compile("^let(\\s+)(\\w+)(\\s*)=(\\s*)((\\w|[\\(\\)\\^\\*\\+-/])+)$");
		Pattern print_pattern = Pattern.compile("^print(\\s+)((\\w|[\\(\\)\\*\\+-/])+)$");
		Matcher let_matcher = let_pattern.matcher(s);
		Matcher print_matcher = print_pattern.matcher(s);
		if(let_matcher.find() || print_matcher.find()){
			return true;
		}else{
			return false;
		}
	}
	public String getType(){
		return type;
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

    public Expression(String s){
		String list[] = s.split("((?<=\\+)|(?=\\+)|(?<=-)|(?=-))");
		String reg;
		Terme terme;
		int i = 0;
		while( i< list.length ){
			if(! Expression.valide(list[i])){
				reg = list[i];
				while(!Expression.valide(reg)){
				i++;	
				reg = reg + list[i];
				}
				decomposition.add(reg);
				terme = new Terme(reg);
				terme.evaluer();
				termes.put(reg, terme);
			}else{
				decomposition.add(list[i]);
				if(list[i] != "+" || list[i] != "-"){
					terme = new Terme(list[i]);
					terme.evaluer();
					termes.put(list[i], terme);
				}
			}
			i++;
		}
    }
    public void evaluer(){
		String terme;
		Double valeur = 0.0D;
		int counter = 0;
		while(counter < decomposition.size()) { 		      
			terme = decomposition.get(counter);
			if(terme == "-"){
				valeur =- termes.get(decomposition.get(counter+1)).getValeur();
				counter = +2;
			}else if(terme == "+"){
				valeur =+ termes.get(decomposition.get(counter+1)).getValeur();
				counter = +2;
			}else{
				valeur =+ termes.get(terme).getValeur();
				counter = +1;
			}
		}
		this.valeur = valeur;
    }

    public static boolean valide(String s){
		int open_count = s.length() - s.replace("(", "").length();
		int close_count = s.length() - s.replace(")", "").length();
		if(open_count != close_count){
			return false;
		}else{
			return true;
		}
    }
	public Double getValeur(){
		return valeur;
	}
	public List<String> getList(){
		return decomposition;
	}
}
//------------------------------ Termes ------------------------------
class Terme {
	private List<String> decomposition;
	private Map<String, Facteur> operandes;
	private double valeur;

	public Terme(String terme) {
		//le constructeur d'un terme
	}

	//les getters
	public List<String> getDecomposition() {
		return this.decomposition;
	}
	public Map<String, Facteur> getOperandes() {
		return this.operandes;
	}
	public double getValeur() {
		return this.valeur;
	}
	public void evaluer(){}
	
	//les setters
	public void setDecomposition(List<String> decomposition) {
		this.decomposition = decomposition;
	}
	public void setOperandes(Map<String, Facteur> operandes) {
		this.operandes = operandes;
	}
	public void setValeur(double valeur) {
		this.valeur = valeur;
	}
}

//la classe des facteurs
class Facteur {
	private List<String> decomposition;
	private HashMap<String, Element> operandes;
	private double valeur;
	
	public Facteur(String facteur) {
	}

	//les getters
	public List<String> getDecomposition() {
		return this.decomposition;
	}
	public HashMap<String, Element> getOperandes() {
		return this.operandes;
	}
	public double getValeur() {
		return this.valeur;
	}
	
	//les setters
	public void setDecomposition(List<String> decomposition) {
		this.decomposition = decomposition;
	}
	public void setOperandes(HashMap<String, Element> operandes) {
		this.operandes = operandes;
	}
	public void setValeur() {
		this.valeur = valeur;
	}
}

//la classe des elements
class Element {
	private List<String> decomposition;
	private Map<String, Variable> operandes;
	private double valeur;

	public Element(String element) {
		//le constructeur d'un element
	}

	//les getters
	public List<String> getDecomposition() {
		return this.decomposition;
	}
	public Map<String, Variable> getOperandes() {
		return this.operandes;
	}
	public double getValeur() {
		return this.valeur;
	}
	
	//les setters
	public void setDecomposition(List<String> decomposition) {
		this.decomposition = decomposition;
	}
	public void setOperandes(Map<String, Variable> operandes) {
		this.operandes = operandes;
	}
	public void setValeur() {
		this.valeur = valeur;
	}
}
//la classe des variables
class Variable {
    private String nom;
	private double valeur;

	public Variable(String nom, double valeur) {
        this.nom = nom;
		this.valeur = valeur;
	}
    public Variable(String nom){
        this.nom = nom;
    }
    public String getNom(){
        return this.nom;
    }
    public void setNom(String nom){
        this.nom = nom;
    }
	public double getValeur() {
		return this.valeur;
	}
	public void setValeur(Double valeur) {
		this.valeur = valeur;
	}
}
//------------------------------ FunctionStandard ------------------------------
enum FunctionStandard {
    SIN(java.lang.Math::sin),
    COS(java.lang.Math::cos),
    TAN(java.lang.Math::tan),
    ABS(java.lang.Math::abs),
    SQRT(java.lang.Math::sqrt),
    LOG(java.lang.Math::log);

    private UnaryOperator<Double> function;

    private FunctionStandard(UnaryOperator<Double> function) {
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
}

public class TestTp {
    public static void main(String[] args) {
		String s = "sin(3-(a/b)+3)-cos(3*(r+5))*(log(2*(x+y)))";
		Expression ex = new Expression(s);
		List<String> list = ex.getList();
		for (String l : list){
			System.out.println(l);
		}
		}
    }
