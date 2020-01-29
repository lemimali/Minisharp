Työn tekemiseen osallistuivat Suvi Kaasalainen, Leevi Liimatainen ja 
Petra Nevalainen.

Ohjelman ajamiseen tarvitaan Java-kääntäjä ja ANTLR4. Huom. ohjeet ovat Windowsille.
    - Hae GitHubista tiedostot Minisharp.g4, Minisharp.java, ASTGenVisitor.java,
    ja Program.java.
    - Mene komentorivillä kansioon, jossa hakemasi tiedostot ovat.  
    - Aja komentorivillä komento: antlr4 -visitor Minisharp.g4, jotta ANTLR luo tarvittavat
    java-tiedostot kääntämiseen.
    - Käännä kaikki kansiossa olevat java-tiedostot komennolla javac *.java .
    - Anna komento: java Minisharp [testiohjelman tiedostopolku] [mahdolliset parametrit]
    Esim. ilman parametreja: java Minisharp C:\MyTemp\kaantaja\mallisyötteet\arith.txt
    Tai parametrien kanssa: java Minisharp C:\MyTemp\kaantaja\mallisyötteet\conditional.txt 5 6 
    - Testiohjelmia löytyy mallisyötteet-kansiosta.
    - Ohjelmalle voi antaa kokonaislukuparametreja. Ks. tarkemmin lähdekielen 
    kuvauksesta tai testiohjelmista.