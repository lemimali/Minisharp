import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

/**
 * Program on p��luokka, joka sis�lt�� k��nt�j�n rungon.
 * Paramlist ja Block k�yd��n l�pi rekursiivisesti eval metodissa.
 * Lopullinen tulos palautetaan evalin lopussa returnissa.
 */
public class Program {
    private Paramlist params;
    private Block block;

    /** 
     * Sijoitetaan parametreina tuodut arvot muuttujiin params ja block.
     * @param params parametrit, jotka sijoitetaan
     * @param block blokki, joka sijoitetaan
     */
    public Program(Paramlist params, Block block) {
        this.params = params;
        this.block = block;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     * @param pw Printwriter syntaksipuun kirjoittamiseen
     */
    public void printTree(PrintWriter pw) {
        pw.write("program ( ");
        if (params != null)
            params.printTree(pw);
        block.printTree(pw);
        pw.write(" )");
    }

    /**
     * Tehd��n params:in ja/tai block:in tyyppitarkastus rekursiivisesti.
     * @param tenv Tyyppiymp�rist�
     */
    public void typecheck(HashMap<String, Type> tenv) {
        if (params != null)
            params.typecheck(tenv);
        if (block != null)
            block.typecheck(tenv);
    }

    /**
     * Evaluoidaan sy�tteen� annetun ohjelman parametrit (params) ja ohjelmakoodi
     * (block). Palautetaan lopuksi HashMap envin arvo return.
     * @param env Ymp�rist�
     * @param args Parametrit, jotka k�ytt�j� on antanut
     * @return Envin arvo return
     */
    public Object eval(HashMap<String, Object> env, String[] args) {
        // Jos params on ei ole tyhj�, aloitetaan evaluoimalla params:in sis�lt�.
        if (params != null) {
            params.eval(env, args);
        }
        // Luodaan ReturnCheck apuolio.
        ReturnCheck returned = new ReturnCheck();
        
        // Aloitetaan evaluoimaan blockin sis�lt��.
        block.eval(env, returned);
        
        // Palautetaan Minisharp-luokalle env:iin laitettu returnin arvo
        // Jos envist� ei l�ydy arvoa avaimella return, palautetaan null.
        return env.get("return");
    }
}


/**
 * ReturnCheck on apuolio, jonka teht�v�n� on pit�� kirjaa siit�,
 * milloin sy�teohjelman koodissa ollaan palautettu jokin arvo komennolla
 * return. ReturnCheck olion returned arvoa tarkastellaan blockin l�pik�ynniss�,
 * jotta koodin suorittaminen osataan lopettaa.
 */
class ReturnCheck {

    private boolean returned = false;

    /**
     * Palautetaan returnedin arvo.
     * @return boolean arvo returned
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * Metodi, jossa returned asetetaan trueksi, koska
     * sy�teohjelmassa on k�sitelty statement Result.
     */
    public void setReturn() {
        returned = true;
    }
}


/**
 * Block olio sis�lt�� listan statementteja (Stmt).
 * Blockin p��teht�v�n� on rekursiivisesti evaluoida sy�teohjelman koodi.
 */
class Block {
    private List<Stmt> stmts;

    /**
     * Sijoitetaan parametrina tuotu arvo stmts:iin.
     * @param stmts Statementit, jotka sijoitetaan
     */
    public Block(List<Stmt> stmts) { 
        this.stmts = stmts; 
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     * @param pw Printwriter syntaksipuun kirjoittamiseen
     */
    public void printTree(PrintWriter pw) {
        pw.print(" ( ");
        for (Stmt statement : stmts) {
            statement.printTree(pw);
        }
        pw.print(" ) ");
    }
    
    /**
     * Suoritetaan stmts-listan sis�ll�n tarkastus rekursiivisesti silmukassa.
     * Mahdollisia aliluokkia ovat Decl, Assign, IfStatement tai ForLoop.
     * @param tenv Tyyppiymp�rist�
     */
    public void typecheck(HashMap<String, Type> tenv) {
        for (Stmt stmt : stmts) {
            stmt.typecheck(tenv);
        }
    }
 
    /**
     * Evaluoidaan stmts-listan statementit rekursiivisesti. Jokaisen evaluoinnin
     * j�lkeen tarkistetaan ReturnCheck olion tila. Jos ReturnCheck olion tila on true,
     * voidaan listan l�pik�ynti lopettaa.
     * @param env Ymp�rist�
     * @param r ReturnCheck-olio
     */
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        for (Stmt stmt : stmts) {
            if (r.isReturned())
                return;
            stmt.eval(env, r);
        }
    }
}


/**
 * Statementtien yl�luokka, jonka alle kuuluvat Decl, Assign, IfStatement, ForLoop
 * ja Return. T�m�n luokan metodeihin ei kuulu koskaan tulla. Luokka on luotu
 * perimisen takia.
 */
abstract class Stmt {

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     * @param pw Printwriter syntaksipuun kirjoittamiseen
     */
    public void printTree(PrintWriter pw) {
        pw.print(" ( statement ) ");
    }

    /**
     * Tyyppitarkastus. Metodi luotu perint�� varten.
     * @param tenv Tyyppiymp�rist�
     */
    public void typecheck(HashMap<String, Type> tenv) {
        //
    }

    /**
     * Evaluointi. Metodi luotu perint�� varten.
     * @param env Ymp�rist�
     * @param r ReturnCheck-olio
     */
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        //
    }
}


/**
 * Luokka, jonka p��teht�v�n� on evaluoida kaikki sy�teohjelman parametrit.
 */
class Paramlist {
    private List<Param> params;

    /**
     * Sijoitetaan parametrina tuotu arvo.
     * @param params Sijoitettavat parametrit
     */
    public Paramlist(List<Param> params) {
        this.params = params;   
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     * @param pw Printwriter syntaksipuun kirjoittamiseen
     */
    public void printTree(PrintWriter pw) {
        pw.print(" ( ");
        for (Param parameter : params) {
            parameter.printTree(pw);
        }
        pw.print(" ) ");
    }

    /**
     * Suoritetaan sy�teohjelmassa annettujen parametrien deklarointi. 
     * @param tenv Tyyppiymp�rist�
     */
    public void typecheck(HashMap<String, Type> tenv) {
        for (Param param : params) {
            param.declare(tenv);
        }
    }

    /**
     * Evaluoidaan sy�teohjelmassa annetut parametrit.
     * Minisharp-k��nt�j�lle on mahdollista antaa enemm�n parametreja argsien kautta,
     * kuin mit� sy�teohjelma niit� vaatii. K�ytt�j�n ylim��r�isi� parametreja ei
     * huomioida evalissa. K�ytt�j�n on kuitenkin annettava parametreja v�hint��n
     * yht� paljon kuin sy�teohjelma niit� vaatii.
     * @param env Ymp�rist�
     * @param args K�ytt�j�n antamat parametrit
     */
    public void eval(HashMap<String, Object> env, String[] args) {
        // Tarkistetaan, ett� argumentteja on tarpeeksi.
        if (params.size() > args.length) {
            System.err.println("Program needs more arguments!");
            return;
        }
        // Evaluoidaan parametrit silmukassa.
        for (int i = 0; i < params.size(); i++) {
            params.get(i).eval(env, args[i]);
        }
    }
}


/**
 * Luokka, joka deklaroi ja evaluoi parametrin.
 */
class Param {
    private Type type;
    private String id;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param type Parametrin tyyppi
     * @param id Parametrin tunniste
     */
    public Param(Type type, String id) {
        this.type = type;
        this.id = id;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     * @param pw Printwriter syntaksipuun kirjoittamiseen
     */
    public void printTree(PrintWriter pw) {
        pw.print(" ( ");
        type.printTree(pw);
        pw.printf(" %s ", id);
        pw.print(" ) ");
    }

    /**
     * Lis�t��n parametrin tyyppi ja id HashMappiin.
     * @param tenv Tyyppiymp�rist�
     */
    public void declare(HashMap<String, Type> tenv) {
        // Tarkistetaan onko jo deklaroitu.
        if (tenv.containsKey(id)) {
            System.err.printf("%s has already been declared.\n", id);
        }
        tenv.put(id, type);
        
    }
    
    /**
     * Evaluoidaan parametrin arvo muistiin. Minisharp hyv�ksyy parametreina
     * vain int ja double tyyppisi� arvoja, mutta laskutoimitusten helpottamiseksi
     * argsin arvo tallennetaan doublena.
     * @param env Ymp�rist�
     * @param arg K�ytt�j�n antama parametri
     */
    public void eval(HashMap<String, Object> env, String arg) {
        try {
            if (type.isInt()) {
                Integer integer = Integer.parseInt(arg);
                env.put(id, integer.doubleValue());
            } else {
                Double d = Double.parseDouble(arg);
                env.put(id, d.doubleValue());
            }
            
        } catch (NumberFormatException nfe) {
                System.err.printf("Given argument types don't match: %s\n", arg);
        }
    }
}

/**
 * Type-yliluokan metodit.
 * Luokkaa k�ytet��n p��asiassa vain silloin, kun aliluokalla
 * ei ole metodille omaa toteutusta ja vastauksesta
 * halutaan negatiivinen. 
 */
abstract class Type {

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     * @param pw Printwriter syntaksipuun kirjoittamiseen
     */
    public void printTree(PrintWriter pw) {
        pw.print("type ");
    }

    /**
     * Tarkistetaan onko tyyppi boolean. Yliluokan default-toteutus.
     * @return boolean arvon merkkaamaan onko tyyppi boolean
     */
    public boolean isBool() {
        return false;
    };

    /**
     * Tarkistetaan onko tyyppi int. Yliluokan default-toteutus.
     * @return boolean arvon merkkaamaan onko tyyppi int
     */
    public boolean isInt() {
        return false;
    };

    /**
     * Tarkistetaan onko tyyppi double. Yliluokan default-toteutus.
     * @return boolean arvon merkkaamaan onko tyyppi double
     */
    public boolean isDouble() {
        return false;
    };

    /**
     * Tarkistetaan onko tyyppi int-taulukko. Yliluokan default-toteutus.
     * @return boolean arvon merkkaamaan onko tyyppi int-taulukko
     */
    public boolean isIntSequence() {
        return false;
    };

    /**
     * Tarkastetaan, voidaanko parametrina tuotu tyyppi sijoittaa
     * haluttuun tyyppiin. Yliluokan default-metodi, johon ei ajon
     * aikana kuuluisi koskaan tulla.
     * @param type Tyyppi, jota tarkastellaan
     * @return true, jos yhteensopiva toisen tyypin kanssa, muuten false
     */
    public boolean canAssignTo(Type type) {
        System.err.println("Incorrect use of canAssignTo.");
        return false;
    }
}


/**
 * Tyyppiluokka, jolla merkataan tyyppi� int.
 */
class TypeInt extends Type {

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("int ");
    }
    
    /**
     * Tarkastetaan, voidaanko parametrina tuotu tyyppi sijoittaa
     * haluttuun tyyppiin.
     */
    @Override
    public boolean canAssignTo(Type type) {
        return type.isInt() || type.isDouble();
    }

    /**
     * Tarkistetaan onko tyyppi int.
     */
    @Override
    public boolean isInt() {
        return true;
    }
}

/**
 * Tyyppiluokka, jolla merkataan int-taulukkoa.
 */
class TypeIntSequence extends Type {

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("intsequence ");
    }

    /**
     * Tarkastetaan, voidaanko parametrina tuotu tyyppi sijoittaa
     * haluttuun tyyppiin.
     */
    @Override
    public boolean canAssignTo(Type type) {
        return type.isIntSequence();
    }

    /**
     * Tarkistetaan onko tyyppi int-taulukko.
     */
    @Override
    public boolean isIntSequence() {
        return true;
    }
}

/**
 * Tyyppiluokka, jolla merkataan doublea.
 */
class TypeDouble extends Type {

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("double ");
    }

    /**
     * Tarkastetaan, voidaanko parametrina tuotu tyyppi sijoittaa
     * haluttuun tyyppiin.
     */
    @Override
    public boolean canAssignTo(Type type) {
        return type.isDouble();
    }

    /**
     * Tarkistetaan onko tyyppi double.
     */
    @Override
    public boolean isDouble() {
        return true;
    }
}


/**
 * Tyyppiluokka, jolla merkataan booleania.
 */
class TypeBool extends Type {

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("boolean ");
    }

    /**
     * Tarkastetaan, voidaanko parametrina tuotu tyyppi sijoittaa
     * haluttuun tyyppiin.
     */
    @Override
    public boolean canAssignTo(Type type) {
        return type.isBool();
    }
    
    /**
     * Tarkistetaan onko tyyppi boolean.
     */
    @Override
    public boolean isBool() {
        return true;
    }
}


/**
 * Declien yl�luokka, jonka alle kuuluvat DeclExpr ja DeclArray.
 * T�m�n luokan metodeihin ei kuulu koskaan tulla forloopDeclCheck-metodia
 * lukuun ottamatta. Luokka on luotu p��asiassa perimisen takia.
 */
abstract class Decl extends Stmt {

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print(" ( decl ) ");
    }

    /**
     * Tyyppitarkastus. Metodi luotu perint�� varten.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        //
    }

    /**
     * Tyyppitarkastus silmukan muuttujan alustamiseen.
     * Default-metodi.
     * @param tenv Tyyppiymp�rist�
     * @return True, jos Decl on sopiva silmukan alustustoimenpiteeseen, muuten false
     */
    public boolean forloopDeclCheck(HashMap<String, Type> tenv) {
        return false;
    }

    /**
     * Default-metodi id:n kysymiseen. Metodi luotu perint�� varten.
     * @return id:n arvo merkkijonona
     */
    public String getID() {
        System.err.printf("Trying to ask id value from abstract Decl!\n");
        return "";
    }
}

/**
 * Luokka uuden arvon alustamista varten.
 * Luokalla on tyyppi ja id, sek� Expr arvo.
 */
class DeclExpr extends Decl {
    private Type type;
    private String id;
    private Expr expr;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param type Sijoitettava tyyppi
     * @param id Sijoitettava tunniste/id
     * @param expr Sijoitettava lauseke/expressions
     */
    public DeclExpr(Type type, String id, Expr expr) {
        this.type = type;
        this.id = id;
        this.expr = expr;
    }

    /**
     * Metodi ID:n kysymiseen.
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print(" ( ");
        type.printTree(pw);
        pw.printf(" %s ", id);
        expr.printTree(pw);
        pw.print(" ) ");
    }

    /**
     * Tyyppitarkastetaan DeclExpr vaihe vaiheelta.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        // Tarkastetaan onko ID jo tenviss�. 
        if (tenv.containsKey(id)) {
            System.err.printf("Variable name %s has already been declared\n",
                    id);
            return;
        }
        // Lis�t��n ID ja type tyyppiymp�rist��n.
        tenv.put(id, type);

        // Tarkastetaan onko expr:n tyyppi yhteensopiva DeclExpr:n tyypin kanssa.
        Type t = expr.type(tenv);
        if (!t.canAssignTo(type)) {
            System.err.println("Type mismatch on declaration");
            return;
        }
    }

    /**
     * Tarkastetaan, k�yk� DeclExpr silmukan muuttujan alustamiseen.
     */
    @Override
    public boolean forloopDeclCheck(HashMap<String, Type> tenv) {
        // Tarkastetaan onko DeclExpr:n tyyppi int. 
        if (!type.isInt()) {
            System.err.println("Decl type must be int!");
            return false;
        }
        
        // Tarkastetaan onko vastaava id jo lis�tty tenviin.
        if (tenv.containsKey(id)) {
            System.err.printf("Variable name %s has already been declared\n",
                    id);
            return false;
        }
        // Lis�t��n ID ja type tyyppiymp�rist��n.
        tenv.put(id, type);
        
        // Tarkastetaan, ett� Expr:n tyyppi on TypeInt.
        if (!expr.type(tenv).isInt()) {
            System.err.println("Expr type must be type TypeInt!");
            return false;
        }
        
        // Palautetaan tosi, jos DeclExpr:n tyyppi oli int, muuttujaa ei oltu
        // lis�tty tenviin aikaisemmin ja Expr:n tyyppi on TypeInt.
        return true;
    }

    /**
     * Lis�t��n env:iin id ja expr:n evaluoitu arvo.
     */
    @Override
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        env.put(id, expr.eval(env, r));
    }
}


/**
 * Luokka uuden int-taulukon alustamista varten.
 * Luokalla on tyyppi, id ja int-taulukko arvo.
 */
class DeclArray extends Decl {
    private Type type;
    private String id;
    private int[] consts;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param type Sijoitettava tyyppi
     * @param id Sijoitettava tunniste/id
     * @param consts Sijoitettava int-taulukko
     */
    public DeclArray(Type type, String id, int[] consts) {
        this.type = type;
        this.id = id;
        this.consts = consts;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        type.printTree(pw);
        pw.print(id);
        for (int a : consts) {
            pw.printf(" %s ", a);
        }
    }

    /**
     * Tyyppitarkastetaan DeclArray vaihe vaiheelta.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        // Tarkastetaan, onko type tyyppi� int.
        if (!type.isInt()) {
            System.err.println("Array type must be int.");
            return;
        }
        // Tarkastetaan onko vastaava id jo lis�tty tenviin.
        if (tenv.containsKey(id)) {
            System.err.printf("Variable name %s has already been declared.\n",
                    id);
            return;
        }
        type = new TypeIntSequence();
        // Lis�t��n tenv:iin id ja type
        tenv.put(id, type);
    }

    /**
     * Lis�t��n env:iin id ja consts.
     */
    @Override
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        env.put(id, consts);
    }
}


/**
 * Assignien yl�luokka, jonka alle kuuluvat AssignExpr ja AssignArray.
 * T�m�n luokan metodeihin ei kuulu koskaan tulla. Luokka on luotu perimisen takia.
 */
abstract class Assign extends Stmt {

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print(" ( assign ) ");
    }

    /**
     * Tyyppitarkastus. Metodi luotu perint�� varten.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        //
    }
    
}


/**
 * Luokka arvon sijoittamista varten.
 * Luokalla on id, sek� Expr arvo.
 */
class AssignExpr extends Assign {
    private String id;
    private Expr expr;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param id Sijoitettava tunniste/id
     * @param expr Sijoitettava lauseke/expression
     */
    public AssignExpr(String id, Expr expr) {
        this.id = id;
        this.expr = expr;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print(" ( ");
        pw.printf(" %s ", id);
        expr.printTree(pw);
        pw.print(" ) ");
    }

    /**
     * Tyyppitarkastetaan AssignExpr vaihe vaiheelta.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        // Tarkastetaan, onko vastaava id jo lis�tty tenviin.
        if (!tenv.containsKey(id)) {
            System.err.printf("Variable name %s has not been declared.\n", id);
            return;
        }
        Type t = expr.type(tenv);
        
        // Tarkastetaan vastaavatko tyypit toisiaan.
        if (!t.canAssignTo(tenv.get(id))) {
            System.err.println("Types missmatch");
            return;
        }
    }

    /**
     * Lis�t��n env:iin id ja expr:n evaluoitu arvo.
     */
    @Override
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        env.put(id, expr.eval(env, r));
    }
}


/**
 * Luokka int-taulukon sijoittamista varten.
 * Luokalla on id ja int-taulukko.
 */
class AssignArray extends Assign {
    private String id;
    private int[] consts;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param id Sijoitettava tunniste/id
     * @param consts Sijoitettava int-taulukko
     */
    public AssignArray(String id, int[] consts) {
        this.id = id;
        this.consts = consts;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print(" ( ");
        pw.printf(" %s ", id);
        for (int a : consts) {
            pw.printf(" %s ", a);
        }
        pw.print(" ) ");
    }
    
    /**
     * Tyyppitarkastetaan AssignArray vaihe vaiheelta.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        // Tarkastetaan, onko vastaava id jo lis�tty tenviin.
        if (!tenv.containsKey(id)) {
            System.err.printf("Variable name %s has not been declared.\n", id);
        }
        // Tarkastetaan, onko id:n tyyppi IntSequence.
        if (!tenv.get(id).isIntSequence()) {
            System.err.printf("%s type is not IntSequence type.\n", id);
        }
    }

    /**
     * Lis�t��n env:iin id ja consts.
     */
    @Override
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        env.put(id, consts);
    }
}

/**
 * Luokka, jolla merkataan silmukan (ForLoop) toiston j�lkeist� indeksin
 * kasvatus tai v�hennys operaatiota (esim. perinteinen i++).
 */
class Iterator {
    private String id;
    private String op;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param id Sijoitettava id
     * @param op Sijoitettava operaation merkkijonona (++ tai --)
     */
    public Iterator(String id, String op) {
        this.id = id;
        this.op = op;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     * @param pw Printwriter syntaksipuun kirjoittamiseen
     */
    public void printTree(PrintWriter pw) {
        pw.print(" ( ");
        pw.printf("%s", id);
        pw.print(op);
        pw.print(" ) ");
    }

    /**
     * Tyyppitarkastetaan AssignArray vaihe vaiheelta.
     * @param tenv Tyyppiymp�rist�
     */
    public void typecheck(HashMap<String, Type> tenv) {
        // Tarkastetaan, onko vastaava id jo lis�tty tenviin. 
        if (!tenv.containsKey(id)) {
            System.err.printf("%s has not been declared.\n", id);
        }
        // Tarkastetaan, ett� id:n tyyppi on int.
        if (!tenv.get(id).isInt()) {
            System.err.printf(
                    "Incompatible type in iterator, requires int was %s.\n",
                    tenv.get(id));
        }
    }

    /**
     * Evaluoidaan Iteraattorin arvo ja p�ivitet��n uusi arvo enviin.
     * @param env Ymrp�rist�
     * @param r ReturnCheck-olio
     */
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        // Haetaan Iteraattorin arvo envist� id:n avulla.
        Double idvalue = (Double) env.get(id);
        double intval = idvalue.doubleValue();
        
        // Jos op on ++, korotetaan arvoa yhdell�, muuten v�hennet��n yhdell�.
        if (op.equals("++"))
            intval++;
        else 
            intval--;
        
        // Lis�t��n env:iin id ja p�ivitetty intval.
        // Id-avain l�ytyy envist� jo ennest��n, joten id:lle p�ivitet��n uusi arvo.
        env.put(id, Double.valueOf(intval));
    }
}


/**
 * Exprien (lauseke/expression) abstrakti yl�luokka, jonka alle kuuluvat VarExpr,
 * IndexExpr, ConstExpr, ParenExpr, MulExpr, AddExpr, LessthanExpr, GreaterthanExpr,
 * EqualExpr ja LengthExpr.
 * T�m�n luokan metodeihin ei kuulu koskaan tulla. Luokka on luotu perimisen takia. 
 */
abstract class Expr {
    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     * @param pw Printwriter syntaksipuun kirjoittamiseen
     */
    public void printTree(PrintWriter pw) {
        pw.print(" ( expr ) ");
    }

    /**
     * Tyyppitarkastus. Metodi luotu perint�� varten.
     * @param tenv Tyyppiymp�rist�
     */
    public void typecheck(HashMap<String, Type> tenv) {
        System.err.println("Incorrect typecheck of expr.");
    }

    /**
     * Palautetaan expr:n tyyppi. Metodi luotu perint�� varten.
     * @param tenv Tyyppiymp�rist�
     * @return Expr-olion tyypin
     */
    public Type type(HashMap<String, Type> tenv) {
        System.err.println("Incorrect use of expr in method type.");
        return null;
    }

    /**
     * Evaluointi. Metodi luotu perint�� varten.
     * @param env Ymp�rist�
     * @param r ReturnCheck-olio
     * @return Expr:n evaluoitu arvo osbjectina
     */
    public Object eval(HashMap<String, Object> env, ReturnCheck r) {
        System.err.println("Incorrect use of expr in method eval.");
        return null;
    }
}


/**
 * Luokka, joka edustaa muuttujaa.
 */
class VarExpr extends Expr {
    private String id;

    /**
     * Sijoitetaan parametrina tuotu id.
     * @param id Sijoitettava tunniste/id
     */
    public VarExpr(String id) {
        this.id = id;
    }

    /**
     * Palautetaan expr:n tyyppi.
     * Expr:n tulee l�yty� tyyppiymp�rist�st�, jotta tyyppi voidaan palauttaa. 
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        if (tenv.containsKey(id)) {
            return tenv.get(id);
        }
        System.err.printf("Variable name %s has not been declared.\n", id);
        return null;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.printf(" %s ", id);
    }

    /**
     * Tyyppitarkastus. Id:n tulee l�yty� tenvist�.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        if (!tenv.containsKey(id)) {
            System.err.printf("Variable name %s has not been declared.\n", id);
        }
    }
    
    /**
     * Evaluointi. Palautetaan envist� id:ll� l�ytyv� arvo.
     */
    @Override
    public Object eval(HashMap<String, Object> env, ReturnCheck r) {
        return env.get(id);
    }
}

/**
 * Luokka, joka edustaa int-taulukon indeksi�.
 * Id edustaa muuttujan nime� ja index indeksi�, jolla osoitetaan tietty� taulukon arvoa.
 */
class IndexExpr extends Expr {
    private String id;
    private Expr index;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param id Sijoitettava tunniste/id
     * @param index Sijoitettava Expr indeksi
     */
    public IndexExpr(String id, Expr index) {
        this.id = id;
        this.index = index;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print(" ( ");
        pw.print(id);
        pw.print("[");
        index.printTree(pw);
        pw.print("]");
        pw.print(" ) ");
    }

    /**
     * Tyyppitarkastus, jossa tarkastetaan, ett� id l�ytyy tenvist�,
     * id:n arvon tyyppi on IntSequence (int-taulukko) ja indexin arvolla pystyt��n
     * osoittamaan paikkaa taulukossa.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        if (!tenv.containsKey(id)) {
            System.err.printf("Variable name %s has not been declared.\n", id);
        }
        if (!tenv.get(id).isIntSequence()) {
            System.err.printf("Variable %s is not int array.\n", id);
        }
        Type i = index.type(tenv);
        if (!i.isInt()) {
            System.err.println("Array index must evaluate to integer");
        }
    }

    /**
     * Palautetaan expr:n tyyppi.
     * Expr:n tulee l�yty� tyyppiymp�rist�st�, jotta tyyppi voidaan palauttaa. 
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        return index.type(tenv);
    }

    /**
     * Evaluoidaan int-taulukosta arvo kohdasta index.
     */
    @Override
    public Double eval(HashMap<String, Object> env, ReturnCheck r) {
        // Alustetaan taulukko t noutamalla id:n arvo env:st�
        int[] t = (int[]) env.get(id);
        // Evaluoidaan indexin arvo
        Double d = (Double) index.eval(env, r);

        // Tarkistetaan onko d:n arvo taulukon t rajojen sis�ll�.
        if (d.doubleValue() > t.length - 1 || d.doubleValue() < 0) {
            System.err.printf("Index out of bounds, index was %s.\n",
                    d.doubleValue());
            return null;
        }
        // Palautetaan taulukosta t arvo, joka sijaitsee indeksiss� d.
        return Double.valueOf(t[d.intValue()]);
    }
}


/**
 * Luokka, joka edustaa kokonaislukuarvoja.
 * Val kertoo kokonaislukuarvon.
 */
class ConstExpr extends Expr {
    private int val;

    /**
     * Sijoitetaan parametrina tuotu arvo.
     * @param val Sijoitettava kokonaislukuarvo
     */
    public ConstExpr(int val) { // 
        this.val = val;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.printf(" %s ", val);
    }

    /**
     * Tyyppitarkastus, jossa ei tehd� mit��n, koska virhe tulisi huomata jo
     * aikaisemmin ANTLR:n generoimissa Java-tiedostoissa.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        // Tyhj� toteutus
    }

    /**
     * Palautetaan expr:n tyyppi.
     * Expr:n tulee l�yty� tyyppiymp�rist�st�, jotta tyyppi voidaan palauttaa. 
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        return new TypeInt();
    }

    /**
     * Palautetaan val:n arvo Double-oliona.
     */
    @Override
    public Double eval(HashMap<String, Object> env, ReturnCheck r) {
        return Double.valueOf(val);
    }
}


/**
 * Luokka, joka edustaa sulutettua Expr-oliota.
 * ParenExpr on k�sittely j�rjestyksess� ennen aritmetiikkaa ja vertailua
 * suorittavia expr-olioita. J�rjestys on asetettu Minisharp.g4 -tiedostossa.
 */
class ParenExpr extends Expr {
    private Expr paren;

    /**
     * Sijoitetaan parametrina tuotu arvo.
     * @param paren Sijoitettava expr
     */
    public ParenExpr(Expr paren) { // 
        this.paren = paren;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        paren.printTree(pw);
        pw.print(" )");
    }

    /**
     * Suoritetaan tyyppitarkastus rekursiivisesti.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        paren.typecheck(tenv);
    }

    /**
     * Palautetaan parenin tyyppi rekursiivisesti.
     * Expr:n tulee l�yty� tyyppiymp�rist�st�, jotta tyyppi voidaan palauttaa. 
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        return paren.type(tenv);
    }

    /**
     * Evaluoidaan parenin arvo rekursiivisesti ja palautetaan arvo Doublena.
     */
    @Override
    public Double eval(HashMap<String, Object> env, ReturnCheck r) {
        return (Double) paren.eval(env, r);
    }

}

/**
 * Luokka, joka edustaa aritmetiikan suorittamista.
 * ArithExpr on abstrakti yliluokka, josta MulExpr ja AddExpr perit��n.
 */
abstract class ArithExpr extends Expr {
    protected Expr left;
    protected Expr right;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param left Sijoitettava vasen arvo
     * @param right Sijoitettava oikea arvo
     */
    public ArithExpr(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print(" ( arithexpr ) ");
    }

    /**
     * Palautetaan expr arvoille yhteinen tyyppi.
     * Jos yhteist� tyyppi� ei l�ydet�, palautetaan null ja tyypitysvirhe.
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        Type lty = left.type(tenv);
        Type rty = right.type(tenv);
        if (lty.isInt()) {
            if (rty.isInt()) {
                return lty;
            } else if (rty.isDouble()) {
                return rty;
            }
        } else if (lty.isDouble()) {
            if (rty.isInt()) {
                return lty;
            } else if (rty.isDouble()) {
                return rty;
            }
        }
        // Tyyppi on t�nne jouduttaessa virheellinen.
        System.err.println("Arithmetic type error");
        return null;
    }

    /**
     * Tyyppitarkastetaan molemmat expr:t rekursiivisesti.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        left.typecheck(tenv);
        right.typecheck(tenv);
    }
}

/**
 * Luokka, joka suorittaa jakolaskun.
 * Suorittaminen tapahtuu varmasti ennen yhteen- ja
 * v�hennyslaskua. J�rjestys m��ritelty Minisharp.g4-tiedostossa. 
 */
class DivExpr extends ArithExpr {

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param left Sijoitettava vasen arvo
     * @param right Sijoitettava oikea arvo
     */
    public DivExpr(Expr left, Expr right) {
        super(left, right);
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        left.printTree(pw);
        pw.print(" / ");
        right.printTree(pw);
        pw.print(" )");
    }

    /**
     * Suoritetaan jakolasku.
     */
    @Override
    public Double eval(HashMap<String, Object> env, ReturnCheck ret) {
        Double l = (Double) left.eval(env, ret);
        Double r = (Double) right.eval(env, ret);
        return l.doubleValue() / r.doubleValue();
    }
}


/**
 * Luokka, joka suorittaa kertolaskun.
 * Suorittaminen tapahtuu varmasti ennen yhteen- ja
 * v�hennyslaskua. J�rjestys m��ritelty Minisharp.g4-tiedostossa. 
 */
class MulExpr extends ArithExpr {

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param left Sijoitettava vasen arvo
     * @param right Sijoitettava oikea arvo
     */
    public MulExpr(Expr left, Expr right) {
        super(left, right);
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        left.printTree(pw);
        pw.print(" * ");
        right.printTree(pw);
        pw.print(" )");
    }

    /**
     * Suoritetaan kertolasku.
     */
    @Override
    public Double eval(HashMap<String, Object> env, ReturnCheck ret) {
        Double l = (Double) left.eval(env, ret);
        Double r = (Double) right.eval(env, ret);
        return l.doubleValue() * r.doubleValue();
    }
}


/**
 * Luokka, joka suorittaa v�hennyslaskun.
 * Suorittaminen tapahtuu varmasti kerto- ja
 * jakolaskun j�lkeen. J�rjestys m��ritelty Minisharp.g4-tiedostossa. 
 */
class MinExpr extends ArithExpr {

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param left Sijoitettava vasen arvo
     * @param right Sijoitettava oikea arvo
     */
    public MinExpr(Expr left, Expr right) {
        super(left, right);
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        left.printTree(pw);
        pw.print(" - ");
        right.printTree(pw);
        pw.print(" )");
    }

    /**
     * Suoritetaan v�hennyslasku.
     */
    @Override
    public Double eval(HashMap<String, Object> env, ReturnCheck ret) {
        Double l = (Double) left.eval(env, ret);
        Double r = (Double) right.eval(env, ret);
        return l.doubleValue() - r.doubleValue();
    }
}


/**
 * Luokka, joka suorittaa yhteenlaskun.
 * Suorittaminen tapahtuu varmasti kerto- ja
 * jakolaskun j�lkeen. J�rjestys m��ritelty Minisharp.g4-tiedostossa. 
 */
class AddExpr extends ArithExpr {

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param left Sijoitettava vasen arvo
     * @param right Sijoitettava oikea arvo
     */
    public AddExpr(Expr left, Expr right) {
        super(left, right);
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        left.printTree(pw);
        pw.print(" + ");
        right.printTree(pw);
        pw.print(" )");
    }

    /**
     * Suoritetaan yhteenlasku.
     */
    @Override
    public Double eval(HashMap<String, Object> env, ReturnCheck ret) {
        Double l = (Double) left.eval(env, ret);
        Double r = (Double) right.eval(env, ret);
        return l.doubleValue() + r.doubleValue();
    }
}


/**
 * Luokka, joka suorittaa pienempi kuin -vertailun.
 */
class LessthanExpr extends Expr {
    private Expr left;
    private Expr right;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param left Sijoitettava vasen arvo
     * @param right Sijoitettava oikea arvo
     */
    public LessthanExpr(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        left.printTree(pw);
        pw.print(" < ");
        right.printTree(pw);
        pw.print(" )");
    }

    /**
     * Palautetaan tyyppiolio TypeBool, jos tyypit ovat yhteensopivat.
     * Muuten palautetaan null ja virheilmoitus.
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        Type lty = left.type(tenv);
        Type rty = right.type(tenv);
        
        // Tarkastetaan, ovatko molemmat puolet ovat samaa tyyppi�,
        // eli ovatko ne vertailukelpoiset kesken��n.
        if (lty.isInt() && rty.isInt() || lty.isInt() && rty.isDouble() || lty.isDouble() && rty.isInt()
                || lty.isDouble() && rty.isDouble()) {
            return new TypeBool();
        }
        System.err.println("Incompatiple types in comparison");
        return null;
    }

    /**
     * Suoritetaan tyyppitarkastus rekursiivisesti.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        left.typecheck(tenv);
        right.typecheck(tenv);
    }

    /**
     * Suoritetaan vertailu.
     */
    @Override
    public Boolean eval(HashMap<String, Object> env, ReturnCheck ret) {
        Double l = (Double) left.eval(env, ret);
        Double r = (Double) right.eval(env, ret);
        return l.doubleValue() < r.doubleValue();
    }
}


/**
 * Luokka, joka suorittaa suurempi kuin -vertailun.
 */
class GreaterthanExpr extends Expr {
    private Expr left;
    private Expr right;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param left Sijoitettava vasen arvo
     * @param right Sijoitettava oikea arvo
     */
    public GreaterthanExpr(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        left.printTree(pw);
        pw.print(" > ");
        right.printTree(pw);
        pw.print(" )");
    }

    /**
     * Palautetaan tyyppiolio TypeBool, jos tyypit ovat yhteensopivat.
     * Muuten palautetaan null ja virheilmoitus.
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        Type lty = left.type(tenv);
        Type rty = right.type(tenv);
        
        // Tarkastetaan, ovatko molemmat puolet ovat samaa tyyppi�,
        // eli ovatko ne vertailukelpoiset kesken��n.
        if (lty.isInt() && rty.isInt() || lty.isInt() && rty.isDouble() || lty.isDouble() && rty.isInt()
                || lty.isDouble() && rty.isDouble()) {
            return new TypeBool();
        }
        System.err.println("Incompatiple types in comparison");
        return null;
    }

    /**
     * Suoritetaan tyyppitarkastus rekursiivisesti.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        left.typecheck(tenv);
        right.typecheck(tenv);
    }

    /**
     * Suoritetaan vertailu.
     */ 
    @Override
    public Boolean eval(HashMap<String, Object> env, ReturnCheck ret) {
        Double l = (Double) left.eval(env, ret);
        Double r = (Double) right.eval(env, ret);
        return l.doubleValue() > r.doubleValue();
    }
}


/**
 * Luokka, joka suorittaa yht� kuin -vertailun.
 */
class EqualExpr extends Expr {
    private Expr left;
    private Expr right;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param left Sijoitettava vasen arvo
     * @param right Sijoitettava oikea arvo
     */
    public EqualExpr(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        left.printTree(pw);
        pw.print(" == ");
        right.printTree(pw);
        pw.print(" )");
    }

    /**
     * Palautetaan tyyppiolio TypeBool, jos tyypit ovat yhteensopivat.
     * Muuten palautetaan null ja virheilmoitus.
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        Type lty = left.type(tenv);
        Type rty = right.type(tenv);
        
        // Tarkastetaan, ovatko molemmat puolet ovat samaa tyyppi�,
        // eli ovatko ne vertailukelpoiset kesken��n.
        if (lty.isInt() && rty.isInt()) {
            return new TypeBool();
        } else if (lty.isDouble() && rty.isDouble()) {
            return new TypeBool();
        } else {
            System.err.println("Incompatiple types in comparison");
            return null;
        }
    }

    /**
     * Suoritetaan tyyppitarkastus rekursiivisesti.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        left.typecheck(tenv);
        right.typecheck(tenv);
    }

    /**
     * Suoritetaan vertailu.
     */
    @Override
    public Boolean eval(HashMap<String, Object> env, ReturnCheck ret) {
        Double l = (Double) left.eval(env, ret);
        Double r = (Double) right.eval(env, ret);
        return (l.doubleValue() == r.doubleValue());
    }
}


/**
 * Luokka, joka suorittaa taulukon pituuden laskemisen.
 */
class LenghtExpr extends Expr {
    private String id;

    /**
     * Sijoitetaan parametrina tuotu arvo.
     * @param id Sijoitettava tunniste/id
     */
    public LenghtExpr(String id) {
        this.id = id;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( ");
        pw.printf("%s .length", id);
        pw.print(" )");
    }

    /**
     * Tyyppitarkastus, jossa tarkastetaan, ett� id l�ytyy tenvist� ja
     * ett� id:n arvo on tyypilt��n IntSequence (int-taulukko).
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        if (!tenv.containsKey(id)) {
            System.err.printf("Variable name %s has not been declared.\n", id);
        }
        if (!tenv.get(id).isIntSequence()) {
            System.err.printf("Variable %s must be an int array.\n", id);
        }
    }

    /**
     * Palautetaan TypeInt-olio, jossa id:n arvo tenviss� on int-taulukko.
     */
    @Override
    public Type type(HashMap<String, Type> tenv) {
        if (!tenv.get(id).isIntSequence()) {
            System.err.printf("Variable %s must be an int array.\n", id);
            return null;
        }
        return new TypeInt();
    }

    /**
     * Lasketaan id:ll� l�ytyv�n int-taulukon pituus.
     */
    @Override
    public Double eval(HashMap<String, Object> env, ReturnCheck r) {
        int[] t = (int[]) env.get(id);
        return Double.valueOf(t.length);
    }
}


/**
 * Luokka, joka on vastuussa ehtolauseiden toiminnasta.
 * Luokka sis�lt�� aina ehdon (expr), joka suoritetaan ja
 * 1-2 blockia riippuen siit�, onko sy�teohjelmaan kirjoitettu
 * mahdollista else-haaraa.
 */
class IfStatement extends Stmt {
    private Expr expr;
    private Block[] blocks;

    /**
     * sijoitetaan parametreina tuodut arvot.
     * @param expr Sijoitettava expr
     * @param statements Sijoitettavat blockit
     */
    public IfStatement(Expr expr, Block[] statements) {
        this.expr = expr;
        this.blocks = statements;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( if ");
        expr.printTree(pw);
        blocks[0].printTree(pw);
        if (blocks.length == 2) {
            pw.print(" else ");
            blocks[1].printTree(pw);
        }
        pw.print(" )");
    }

    /**
     * Tyyppitarkastetaan, ett� expr on varmasti tyyppi� boolean.
     * Blockien sis�lt�m�t statementit tyyppitarkastetaan rekursiivisesti.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        expr.typecheck(tenv);
        if (!expr.type(tenv).isBool()) {
            System.err.println("If statement requires type boolean!");
        }
        for (Block b : blocks) {
            b.typecheck(tenv);
        }
    }

    /**
     * Evaluoidaan IfStamentti exprist� evaluoidun arvon mukaan.
     */
    @Override
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        Boolean b = (Boolean) expr.eval(env, r);
        // Jos ehto on true evaluoidaan blocks-taulukon ensimm�inen indeksi.
        // Jos ehto on false evaluoidaan blocks-taulukon toinen indeksi.
        if (b.booleanValue()) {
            blocks[0].eval(env, r);
        } else {
            // Jos else-haara l�ytyy (blocks-taulukon pituus on enemm�n kuin 1)
            // suoritetaan se.
            if (blocks.length > 1) {
                blocks[1].eval(env, r);
            }
        }
    }
}


/**
 * Luokka, joka on vastuussa silmukan toiminnasta.
 */
class ForLoop extends Stmt {
    private Decl decl;
    private Expr expr;
    private Iterator iterator;
    private Block block;

    /**
     * Sijoitetaan parametreina tuodut arvot.
     * @param decl Sijoitettava Decl alustustoimenpiteeseen
     * @param expr Sijoitettava Expr
     * @param iterator Sijoitettava Iteraattori
     * @param statement Sijoitettava Blocks
     */
    public ForLoop(Decl decl, Expr expr, Iterator iterator, Block statement) {
        this.decl = decl;
        this.expr = expr;
        this.iterator = iterator;
        this.block = statement;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( for (");
        decl.printTree(pw);
        expr.printTree(pw);
        pw.print(";");
        iterator.printTree(pw);
        pw.print(")");
        block.printTree(pw);
        pw.print(" )");
    }

    /**
     * Tyyppitarkastetaan silmukan kaikki osat.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        if (!decl.forloopDeclCheck(tenv)) {
            System.err.println("For statement variable type can't be an array!");
        }
        expr.typecheck(tenv);
        if (!expr.type(tenv).isBool()) {
            System.err.println("For statement requires expr to be type boolean!");
        }
        iterator.typecheck(tenv);
        block.typecheck(tenv);
    }

    /**
     * 
     */
    @Override
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        // Evaluoidaan silmukkamuuttuja
        decl.eval(env, r);

        // Aloitetaan for-silmukan evaluointi
        while (true) {
            // Tarkistetaan t�yttyyk� silmukan ehto
            Boolean b = (Boolean) expr.eval(env, r);
            if (!b.booleanValue())
                break;
            // Evaluoidaan block
            block.eval(env, r);
            // Tarkistetaan onko silmukan sis�ll� suoritettu return-statementtia
            if (r.isReturned())
                break;
            // Evaluoidaan iteraattori (esim. silmukan perinteinen i++)
            iterator.eval(env, r);
        }
    }
}


/**
 * Luokka, jonka teht�v�n� k�sitell� sy�teohjelmassa mahdollisesti esiintynyt
 * return-statement.
 */
class Return extends Stmt {
    private Expr expr;

    /**
     * Sijoitetaan parametrina tuotu arvo.
     * @param expr Sijoitettava expr
     */
    public Return(Expr expr) { // 
        this.expr = expr;
    }

    /**
     * PrinTree tulostaa sy�tteen� annetun ohjelman syntaksipuun rekursiivisesti.
     */
    @Override
    public void printTree(PrintWriter pw) {
        pw.print("( return ");
        expr.printTree(pw);
        pw.print(" )");
    }

    /**
     * Expr tyyppitarkastetaan rekursiivisesti.
     */
    @Override
    public void typecheck(HashMap<String, Type> tenv) {
        expr.typecheck(tenv);
    }

    /**
     * Lis�t��n enviin expr evaluoita arvo avaimella return.
     * Asetetaan ReturnCheck-olion tila.
     */
    @Override
    public void eval(HashMap<String, Object> env, ReturnCheck r) {
        env.put("return", expr.eval(env, r));
        r.setReturn();
    }
}