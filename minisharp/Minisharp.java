    import java.io.PrintWriter;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashMap;

import org.antlr.v4.runtime.*;
    import org.antlr.v4.runtime.tree.*;
    public class Minisharp
    {
        public static void main(String[] args) throws Exception 
        {
            CharStream input;
            try {
                input = CharStreams.fromFileName(args[0]);
            } catch (NoSuchFileException e) {
                throw new NoSuchFileException("Tiedostoa ei löydy sijainnista " + args[0]);
            }
            
            MinisharpLexer lexer = new MinisharpLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MinisharpParser parser = new MinisharpParser(tokens);
            parser.setBuildParseTree(true);
            
            MinisharpParser.StartContext tree = parser.start();
            ASTGenVisitor<Object> visitor = new ASTGenVisitor<Object>();
            Program ast = (Program) visitor.visit(tree);

            // Printataan konsoliin puun sisältö
            //PrintWriter pw = new PrintWriter(System.out);
            //ast.printTree(pw); // print LISP-style tree
            //pw.flush();
            
            String[] trimmedArgs;
            if (args.length > 1) {
                trimmedArgs = Arrays.copyOfRange(args, 1, args.length);
            } else {
                trimmedArgs = new String[0];
            }
            
            ast.typecheck(new HashMap<String, Type>());
            
            Object r = ast.eval(new HashMap<String, Object>(), trimmedArgs);
            if (r!=null) System.out.println(r.toString());
            
            //pw.close();
        }
    }
