import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * ASTGenVisitor on luokka, jossa ANTLR:n luoma luokka MinisharpBaseVisitor
 * laajennetaan k‰‰nt‰j‰‰n sopivaksi. Kaikki MinisharpBaseVisitorin metodit
 * on korvattu t‰ss‰ luokassa. Minisharp-k‰‰nt‰j‰ k‰ytt‰‰ toiminnassaan
 * t‰t‰ luokkaa.
 * @param <Object> object
 */
@SuppressWarnings("hiding")
public class ASTGenVisitor <Object> extends MinisharpBaseVisitor<Object>{
    
    /**
	 * VisitStart luokassa toteutus on jaettu nelj‰‰n mahdollisuuteen:
	 * Programilla on Paramlist ja Block,
	 * Programilla ei ole Paramlisti‰ ja Blockia,
	 * Programilla ei ole Paramlisti‰, mutta sill‰ on Block,
	 * Programilla on Paramlist, mutta sill‰ ei ole Blockkia.
	 * 
	 * Palautettu objekti on Program.
	 */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitStart(MinisharpParser.StartContext ctx) {
    	// Programilla on Paramlist ja Block
    	if (ctx.getChildCount() == 6) return (Object) new Program ((Paramlist)this.visit(ctx.paramlist()),
    			(Block)this.visit(ctx.block()));       
        
    	// Programilla ei ole Paramlisti‰ ja Blockia
        if (ctx.getChildCount() == 4) return (Object) new Program (null, null);        
        
        // Programilla ei ole Paramlisti‰, mutta sill‰ on Block
        if (ctx.getChild(1).toString().equalsIgnoreCase(")")) return (Object) new Program (null,
        		(Block)this.visit(ctx.block()));
        
        // Programilla on Paramlist, mutta sill‰ ei ole Blockkia
        return (Object) new Program ((Paramlist)this.visit(ctx.paramlist()), null);
    }
    
    
    /**
     * VisitParamlist luokassa ker‰t‰‰n ParamlistContextista
     * kaikki sen sis‰lt‰m‰t parametrit (Param).
     * 
     * Palautettava objekti on Paramlist, joka sis‰lt‰‰ listan
     * Param olioita.
     */
    @SuppressWarnings("unchecked")
    @Override 
    public Object visitParamlist(MinisharpParser.ParamlistContext ctx){ 
        // Luodaan tyhj‰ lista ja Paramlist
    	List<Param> list = new ArrayList<>();
        Paramlist paramlist = new Paramlist(list);
        
        // K‰yd‰‰n l‰pi ParamlistContextin kaikki Paramit ja lis‰t‰‰n ne listaan
        for (MinisharpParser.ParamContext p : ctx.param()){
            Param par = new Param((Type)this.visit(p.type()), p.ID().getText());
            list.add(par);
        }
        
        // Palautetaan valmis Paramlist
        return (Object) paramlist;
    }
    
    
    /**
     * VisitParam luokkaan ei kuulu koskaan tulla, koska parametrien
     * ker‰‰minen suoritetaan visitParamlist-luokassa. Luokka on laajennettu,
     * koska mahdolliset virheet halutaan huomata ohjelman ajamisessa. 
     * 
     * T‰h‰n luokkaan tultaessa palautetaan k‰ytt‰j‰lle error-viesti
     * tapahtuneesta virheest‰.
     */
    @Override
    public Object visitParam(MinisharpParser.ParamContext ctx){
        System.err.println("Error: visitParam class is not in use.\n"
        		+ "Parameter handling should happen in class visitParamlist.");
        return visitChildren(ctx);
    }
    
    
    /**
     * VisitTypeInt luokassa luodaan uusi TypeInt.
     * Palautettava objekti on TypeInt.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitTypeInt(MinisharpParser.TypeIntContext ctx){
        TypeInt t = new TypeInt();
        return (Object) t;
    }
    
    
    /**
     * VisitTypeIntSequence luokassa luodaan uusi TypeIntSequence.
     * Palautettava objekti on TypeIntSequence.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitTypeIntSequence(MinisharpParser.TypeIntSequenceContext ctx){
        TypeIntSequence seq = new TypeIntSequence();
        return (Object) seq;
    }
    
    
    /**
     * VisitTypeDouble luokassa luodaan uusi TypeDouble.
     * Palautettava objekti on TypeDouble.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitTypeDouble(MinisharpParser.TypeDoubleContext ctx){
        TypeDouble d = new TypeDouble();
        return (Object) d;
    }
    
    
    /**
     * VisitTypeBool luokassa luodaan uusi TypeBool.
     * Palautettava objekti on TypeBool.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitTypeBool(MinisharpParser.TypeBoolContext ctx){
        TypeBool b = new TypeBool();
        return (Object) b;
    }
    
    
    /**
     * VisitDeclExpr luokassa luodaan uusi DeclExpr.
     * DeclExprContextin avulla selvitet‰‰n type, id ja expr.
     * 
     * Palautettava objekti on DeclExpr.
     */
    @SuppressWarnings("unchecked")
    @Override 
    public Object visitDeclExpr(MinisharpParser.DeclExprContext ctx) { 
        DeclExpr de = new DeclExpr((Type)this.visit(ctx.type()), ctx.ID().getText(), (Expr)this.visit(ctx.expr()));
        return (Object) de;
    }
    
    
    /**
     * VisitBlock luokassa ker‰t‰‰n BlockContextista
     * kaikki sen sis‰lt‰m‰t lauseet/statementit (Stmt).
     * 
     * Palautettava objekti on Block, joka sis‰lt‰‰ listan
     * Stmt olioita.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitBlock(MinisharpParser.BlockContext ctx) {
    	// Luodaan tyhj‰ lista ja Block
        List<Stmt> stmts = new ArrayList<Stmt>();
        Block block = new Block(stmts);
        
        // K‰yd‰‰n l‰pi BlockContextin kaikki statementit ja lis‰t‰‰n ne listaan 
        for (MinisharpParser.StmtContext s : ctx.stmt()) {
            Stmt stmt = (Stmt) visit(s);
            stmts.add(stmt);
        }
        
        // Palautetaan valmis Block
        return (Object) block;
    }
    
    
    /**
     * VisitDeclArray luokassa luodaan uusi DeclArray.
     * Alussa DeclArrayn int-taulukko luodaan DeclArrayContextin avulla.
     * Lopussa DeclArrayn muut osat noudetaan DeclArrayContextin avulla
     * ja int-taulukko lis‰t‰‰n.
     * 
     * Palautettava objekti on DeclArray.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitDeclArray(MinisharpParser.DeclArrayContext ctx) {
        // Luodaan int-taulukko DeclArrayta varten
        List<Integer> consts = new ArrayList<Integer>();
        for (TerminalNode c : ctx.CONST()) {
            int i = Integer.parseInt(c.getText());
            consts.add(i);
        }
        int[] intArray = new int[consts.size()];
        for (int i = 0; i < consts.size(); i++) {
            intArray[i] = consts.get(i).intValue();
        }
        
        // Haetaan type ja ID DeclArrayContextista ja lis‰t‰‰n int-taulukko
        DeclArray declArray = new DeclArray((Type)this.visit(ctx.type()), ctx.ID().getText(), intArray);
        
        // Palautetaan valmis DeclArray
        return (Object) declArray;
    }
  
   
    /**
     * VisitAssignExpr luokassa luodaan uusi AssignExpr.
     * AssignExprContextin avulla selvitet‰‰n ID ja expr.
     * 
     * Palautettava objekti on AssignExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitAssignExpr(MinisharpParser.AssignExprContext ctx) {
        AssignExpr assignExpr = new AssignExpr(ctx.ID().getText(), (Expr)this.visit(ctx.expr()));
        return (Object) assignExpr;
    }
  
   
    /**
     * VisitAssignArray luokassa luodaan uusi AssignArray.
     * Alussa AssignArrayn int-taulukko luodaan AssignArrayContextin avulla.
     * Lopussa AssignArrayn ID noudetaan AssignArrayContextin avulla
     * ja int-taulukko lis‰t‰‰n.
     * 
     * Palautettava objekti on AssignArray.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitAssignArray(MinisharpParser.AssignArrayContext ctx) {
        // Luodaan int-taulukko AssignArrayta varten
        List<Integer> consts = new ArrayList<Integer>();
        for (TerminalNode c : ctx.CONST()) {
            int i = Integer.parseInt(c.getText());
            consts.add(i);
        }
        int[] intArray = new int[consts.size()];
        for (int i = 0; i < consts.size(); i++) {
            intArray[i] = consts.get(i).intValue();
        }
        
        // Haetaan ID AssignArrayContextista ja lis‰t‰‰n int-taulukko
        AssignArray assignArray = new AssignArray(ctx.ID().getText(), intArray);
        
        // Palautetaan valmis AssignArray
        return (Object) assignArray;
    }
   
    
    /**
     * VisitIterator luokassa luodaan uusi Iterator.
     * IteratorContextin avulla selvitet‰‰n ID ja op (++ tai --).
     * 
     * Palautettava objekti on Iterator.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitIterator(MinisharpParser.IteratorContext ctx) {
        Iterator iterator = new Iterator(ctx.ID().getText(), ctx.op.getText());
        return (Object) iterator;
    }
 
    
    /**
     * VisitIndexExpr luokassa luodaan uusi IndexExpr.
     * IndexExprContextin avulla selvitet‰‰n ID ja expr.
     * 
     * Palautettava objekti on IndexExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitIndexExpr(MinisharpParser.IndexExprContext ctx) {
        IndexExpr indexExpr = new IndexExpr(ctx.ID().getText(),
                                (Expr)this.visit(ctx.expr()));
        return (Object) indexExpr;
    }
  
    
    /**
     * VisitLengthExpr luokassa luodaan uusi LengthExpr.
     * AssignExprContextin avulla selvitet‰‰n ID ja expr.
     * 
     * Palautettava objekti on LengthExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitLengthExpr(MinisharpParser.LengthExprContext ctx) {
        LenghtExpr lenghtExpr = new LenghtExpr(ctx.ID().getText());
        return (Object) lenghtExpr;
    }
  
    
    /**
     * VisitVarExpr luokassa luodaan uusi VarExpr.
     * VarExprContextin avulla selvitet‰‰n ID.
     * 
     * Palautettava objekti on VarExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitVarExpr(MinisharpParser.VarExprContext ctx) {
        VarExpr varExpr = new VarExpr(ctx.ID().getText());
        return (Object) varExpr;
    }
   
    
    /**
     * VisitAddExpr luokassa luodaan uusi AddExpr tai MinExpr.
     * AddExprContextin avulla selvitet‰‰n op (+ tai -).
     * Op:n perusteella valitaan kumpi olioista luodaan. Switch casen
     * default arvoon ei tulisi koskaan p‰‰st‰, koska mahdollinen virhe tulisi
     * havaita jo ennen t‰h‰n metodiin tulemista.
     * 
     * Palautettava objekti on AddExpr tai MinExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitAddExpr(MinisharpParser.AddExprContext ctx) {
    	// Haetaan op AddExprContextista
        String str = new String(ctx.op.getText());
        
        // Luodaan palautettava olio op:n perusteella
        switch(str) {
            case "+":
                AddExpr addExpr = new AddExpr((Expr)this.visit(ctx.expr(0)),
                                    (Expr)this.visit(ctx.expr(1)));
                return (Object) addExpr;
            case "-":
                MinExpr minExpr = new MinExpr((Expr)this.visit(ctx.expr(0)),
                                    (Expr)this.visit(ctx.expr(1)));
                return (Object) minExpr;
            default:
                throw new Error("An error has occured!");
        }
    }
   
    
    /**
     * VisitMulExpr luokassa luodaan uusi DivExpr tai MulExpr.
     * MulExprContextin avulla selvitet‰‰n op (/ tai *).
     * Op:n perusteella valitaan kumpi olioista luodaan. Switch casen
     * default arvoon ei tulisi koskaan p‰‰st‰, koska mahdollinen virhe tulisi
     * havaita jo ennen t‰h‰n metodiin tulemista.
     * 
     * Palautettava objekti on DivExpr tai MulExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitMulExpr(MinisharpParser.MulExprContext ctx) {
    	// Haetaan op MulExprContextista
        String str = new String(ctx.op.getText());
        
        // Luodaan palautettava olio op:n perusteella
        switch(str) {
            case "/":
                DivExpr divExpr = new DivExpr((Expr)this.visit(ctx.expr(0)),
                                    (Expr)this.visit(ctx.expr(1)));
                return (Object) divExpr;
            case "*":
                MulExpr mulExpr = new MulExpr((Expr)this.visit(ctx.expr(0)),
                                    (Expr)this.visit(ctx.expr(1)));
                return (Object) mulExpr;
            default:
                throw new Error("An error has occured!");
        }
    }
   
   
    /**
     * VisitParenExpr luokassa luodaan uusi ParenExpr.
     * ParenExprContextin avulla selvitet‰‰n expr.
     * 
     * Palautettava objekti on ParenExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitParenExpr(MinisharpParser.ParenExprContext ctx) {
        ParenExpr parenExpr = new ParenExpr((Expr)this.visit(ctx.expr()));
        return (Object) parenExpr;
    }
   
    
    /**
     * VisitConstExpr luokassa luodaan uusi ConstExpr.
     * ConstExprContextin avulla selvitet‰‰n CONST (kokonaisluku arvo).
     * 
     * Palautettava objekti on ConstExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitConstExpr(MinisharpParser.ConstExprContext ctx) {
        ConstExpr constExpr = new ConstExpr(Integer.parseInt(ctx.CONST().getText()));
        return (Object) constExpr;
    }
   
    
    /**
     * VisitLessthanExpr luokassa luodaan uusi LessthanExpr.
     * LessthanExprContextin avulla selvitet‰‰n expr arvot.
     * 
     * Palautettava objekti on LessthanExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitLessthanExpr(MinisharpParser.LessthanExprContext ctx) {
        LessthanExpr lessthanExpr = new LessthanExpr((Expr)this.visit(ctx.expr(0)),
                                    (Expr)this.visit(ctx.expr(1)));
        return (Object) lessthanExpr;
    }
   
    
    /**
     * VisitGreaterthanExpr luokassa luodaan uusi GreaterthanExpr.
     * GreaterthanExprContextin avulla selvitet‰‰n expr arvot.
     * 
     * Palautettava objekti on GreaterthanExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitGreaterthanExpr(MinisharpParser.GreaterthanExprContext ctx) {
        GreaterthanExpr greaterthanExpr = new GreaterthanExpr((Expr)this.visit(ctx.expr(0)),
                                            (Expr)this.visit(ctx.expr(1)));
        return (Object) greaterthanExpr;
    }
    
    
    /**
     * VisitEqualExpr luokassa luodaan uusi EqualExpr.
     * EqualExprContextin avulla selvitet‰‰n expr arvot.
     * 
     * Palautettava objekti on EqualExpr.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitEqualExpr(MinisharpParser.EqualExprContext ctx) {
        EqualExpr equalExpr = new EqualExpr((Expr)this.visit(ctx.expr(0)),
                                (Expr)this.visit(ctx.expr(1)));
        return (Object) equalExpr;
    }
    
    
    /**
     * VisitIfstmt luokassa luodaan uusi IfStatement.
     * IfstmtContextin avulla selvitet‰‰n expr ja block arvot.
     * Block arvoja voi olla yksi tai kaksi. Arvoja on kaksi jos if-lause
     * sis‰lt‰‰ else osan.
     * 
     * Palautettava objekti on IfStatement.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitIfstmt(MinisharpParser.IfstmtContext ctx) {
        Block[] blocks;
        // Katsotaan mink‰ kokoisen listan ctx.block() palauttaa ja toimitaan sen mukaan
        List<MinisharpParser.BlockContext> ctxs = ctx.block();
        if (ctxs.size() == 2) {
            blocks = new Block[2];
            blocks[0] = (Block)this.visit(ctx.block(0));
            blocks[1] = (Block)this.visit(ctx.block(1));
        } else {
            blocks = new Block[1];
            blocks[0] = (Block)this.visit(ctx.block(0));
        }
        
        // Haetaan expr IfstmtContextista ja lis‰t‰‰n block-taulukko
        IfStatement ifstmt = new IfStatement((Expr)this.visit(ctx.expr()), blocks);
        
        // Palautetaan valmis IfStatement
        return (Object) ifstmt;
    }
    
   
    /**
     * VisitForstmt luokassa luodaan uusi Forloop.
     * ForstmtContextin avulla selvitet‰‰n decl, expr, iterator ja block.
     * 
     * Palautettava objekti on Forloop.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitForstmt(MinisharpParser.ForstmtContext ctx) {
        ForLoop forloop = new ForLoop((Decl)this.visit(ctx.decl()),
                                      (Expr)this.visit(ctx.expr()),
                                      (Iterator)this.visit(ctx.iterator()),
                                      (Block)this.visit(ctx.block()));
        return (Object) forloop;
    }
   
    
    /**
     * VisitStmtDecl luokassa toiminta delegoidaan
     * metodille visitDeclExpr tai visitDeclArray.
     * 
     * Palautettava objekti on luokan Decl aliluokan olio.
     */
    @Override
    public Object visitStmtDecl(MinisharpParser.StmtDeclContext ctx) {
        return this.visit(ctx.decl());
    }
   
    
    /**
     * VisitStmtAssign luokassa toiminta delegoidaan
     * metodille visitAssignExpr tai visitAssignArray.
     * 
     * Palautettava objekti on luokan Assign aliluokan olio.
     */
    @Override
    public Object visitStmtAssign(MinisharpParser.StmtAssignContext ctx) {
        return this.visit(ctx.assign());
    }
 
   
    /**
     * VisitStmtIfstmt luokassa toiminta delegoidaan
     * metodille visitIfstmt.
     * 
     * Palautettava objekti on IfStatement.
     */
    @Override
    public Object visitStmtIfstmt(MinisharpParser.StmtIfstmtContext ctx) {
        return this.visit(ctx.ifstmt());
    }
  
   
    /**
     * VisitStmtForstmt luokassa toiminta delegoidaan
     * metodille visitForstmt.
     * 
     * Palautettava objekti on Forloop.
     */
    @Override
    public Object visitStmtForstmt(MinisharpParser.StmtForstmtContext ctx) {
        return this.visit(ctx.forstmt());
    }
    
    
    /**
     * VisitStmtReturn luokassa luodaan uusi Return.
     * StmtReturnContextin avulla selvitet‰‰n expr.
     * 
     * Palautettava objekti on Return.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object visitStmtReturn(MinisharpParser.StmtReturnContext ctx) {
        Return ret = new Return((Expr)this.visit(ctx.expr()));
        return (Object) ret;
    }
}
    