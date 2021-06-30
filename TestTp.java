import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

//------------------------------ Interpreteur ------------------------------
class Interpreteur {
    Map<String,Double> table_des_symboles;
    private LigneDeCommande ligne_de_commande;
    public void set_ligne_de_commande(String s){
    }
    public void interpreter(){}
}
//------------------------------ LigneDeCommande ------------------------------
class LigneDeCommande{
    private Variable variable;
    private Expression expression;
    public LigneDeCommande(String s){

    }
    public boolean valide(String s){
        return false;
    }
}
//------------------------------ Expression ------------------------------
class Expression{
    private List<String> decomposition;
    private Map<String,Terme> termes;
    private double valeur;

    public Expression(String s){

    }
    public double evaluer(){
        return 1;
    }
    public boolean valide(String s){
        return false;
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
        
    }
}
