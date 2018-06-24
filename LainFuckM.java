import java.util.*;



//modeliert die Lainfuck maschine
public class LainFuckM{
  private int [][][] memory;
  private final int DIS_EB = 0;
  private final int MAXWERT = 8;//je ein bit fuer rgb
  /*
     0 0 0 --> schwarz -> 0
     0 0 1 --> blau -> 1  
     0 1 0 --> gruen -> 2
     0 1 1 --> tuerkis -> 3
     1 0 0 --> rot -> 4
     1 0 1 --> pink -> 5
     1 1 0 --> gelb -> 6
     1 1 1 --> weiss -> 7
     */
  private String code;
  /*
     1 + -> inc akt_P
     2 - -> dec akt_P
     3 > -> mv akt_P +x
     4 < -> mv akt_P -x
     5 ^ -> mv akt_P +y
     6 | -> mv akt_P -y
     7 / -> mv akt_P +z
     8 \ -> mv akt_P -z
     9 10 [ -> for akt_P != 0 do ... ]
     11 . -> cp akt_P to akt_P +x
     12 , -> cp akt_P to akt_P -x
     13 ! -> cp akt_P to akt_P +y
     14 = -> cp akt_P to akt_P -y
     15 $ -> cp akt_P to akt_P +z
     16 @ -> cp akt_P to akt_P -z
     17 % -> mv akt_P to helpR
     18 19 ( -> for helpR !=0 do ...)
20 : -> inv akt_P
21 ~ -> reset
22 # -> set akt_P to start
23 ? -> if dbg on gibt akt_P als dbg meldung aus
24 * -> eval akt_P
25 _ -> eval akt_P +x
26 ; -> eval akt_P -x
27 & -> eval akt_P +y
28 ` -> eval akt_P -y
29 } -> eval akt_P +z  
30 { -> eval akt_P -z  
31 ' ' -> nop

*/
  public static final String CMDSTR = "+-><^|/\\[].,!=$@%():~#?*_;&`{}";
  public static final int CMDL = CMDSTR.length();
  private int akt_x;
  private int akt_y;
  private int akt_z;

  private int akt_P;

  private int codePc;
  private int helpR;


  private Stack<Integer> eckLoopStack;
  private Stack<Integer> rouLoopStack;
  public static final int DIAM=64;
  private boolean noExec;
  private boolean dbg;
  private boolean exit;

  public LainFuckM(String code){
    this.code = code;
    this.akt_x = 0;
    this.akt_y = 0;
    this.akt_z = 0;
    this.codePc = 0;
    this.helpR =  0;
    this.noExec = false;
    eckLoopStack = new Stack<Integer>();
    rouLoopStack = new Stack<Integer>();
    memory = new int[DIAM][DIAM][DIAM];

  };
  public LainFuckM(int [][][] mem, String code, int xl, int yl, int zl){
    this(code);
    for(int x = 0;x<xl && x<DIAM;x++){
      for(int y = 0;y<yl && y<DIAM;y++){
	for(int z = 0;z<zl && z<DIAM;z++){
	  memory[x][y][z] = mem[x][y][z];  
	};
      };	
    };
  };
  public int[][][] getM(){return this.memory;}
  public void setM(int x, int y, int z,int v){
    this.memory[x%DIAM][y%DIAM][z%DIAM] = v%8;  
  };
  public void setCode(String s){
    code = s;  
  };
  public void setDbg(boolean i){
    dbg = i;  
  };
  public String powerOn(){
    if(this.code == null ||this.memory == null)return "machine not initialized";
    int len = this.code.length();
    char cmd;
    for(this.codePc=0;this.codePc<len && !exit;this.codePc++){
      update();
      cmd  = code.charAt(this.codePc);
      String err = process(cmd);
      if(err !=null){
	Debug.log("exit execution because " + err);
	return err;
      };
    };
    exit = false;
    return "Erfolgreich Ausgefuehrt bis "+codePc;
  };
  private String process(char cmd){
    String erg=null;
    if(!noExec){
      switch(cmd){
	case '+':
	  addP(1);
	  break;
	case '-':
	  addP(-1);
	  break;
	case '>':
	  mvX(1);
	  break;
	case '<':
	  mvX(-1);
	  break;
	case '^':
	  mvY(1);
	  break;
	case '|':
	  mvY(-1);
	  break;
	case '/':
	  mvZ(1);
	  break;
	case '\\':
	  mvZ(-1);
	  break;
	case '[':
	  if(akt_P == 0){
	    noExec = true;
	  };
	  eckLoopStack.push(codePc);
	  break;
	case ']':
	  if(eckLoopStack.isEmpty()){
	    erg="missing open parentethis at pos " + codePc; 
	  }else{
	    codePc = eckLoopStack.pop()-1;  
	  };
	  break;
	case '.':
	  this.memory[(this.akt_x+1)%DIAM][this.akt_y][this.akt_z]=this.akt_P;
	  break;
	case ',':
	  this.memory[(this.akt_x+DIAM-1)%DIAM][this.akt_y][this.akt_z]=this.akt_P;      
	  break;
	case '!':
	  this.memory[this.akt_x][(this.akt_y+1)%DIAM][this.akt_z]=this.akt_P;      
	  break;
	case '=':
	  this.memory[this.akt_x][(this.akt_y+DIAM-1)%DIAM][this.akt_z]=this.akt_P;      
	  break;
	case '$':
	  this.memory[this.akt_x][this.akt_y][(this.akt_z+1)%DIAM]=this.akt_P;
	  break;
	case '@':
	  this.memory[this.akt_x][this.akt_y][(this.akt_z+DIAM-1)%DIAM]=this.akt_P;
	  break;
	case '%':
	  this.helpR = this.akt_P;
	  break;
	case '(':
	  if(this.helpR == 0){
	    noExec = true;  
	  };
	  rouLoopStack.push(codePc);
	  break;
	case ')':
	  if(rouLoopStack.isEmpty()){
	    erg="missing open parentethis at pos " + codePc; 
	  }else{
	    codePc = rouLoopStack.pop()-1;
	  };	
	  break;
	case ':':
	  this.memory[this.akt_x][this.akt_y][this.akt_z] ^=1;
	  update();
	  break;
	case '~':
	  reset();
	  update();
	  break;
	case '#':
	  this.akt_x = 0;
	  this.akt_y = 0;
	  this.akt_z = 0;
	  update();
	  break;
	case '?':
	  if(dbg){
	    Debug.log(this.akt_P+"");  
	    if(pause()){
	      erg = "Debug err"; 
	    };
	  };
	case '*':
	  erg = process(LainFuckM.CMDSTR.charAt(this.akt_P%LainFuckM.CMDL));
	  if(erg !=null){
	    erg = "faild at eval "+codePc+" "+erg;  
	  };
	  break;
	case '_':
	  mvX(1);
	  erg = process(LainFuckM.CMDSTR.charAt(this.akt_P%LainFuckM.CMDL));
	  if(erg !=null){
	    erg = "faild at eval "+codePc+" "+erg;  
	  };
	  break;
	case ';':
	  mvX(-1);
	  erg = process(LainFuckM.CMDSTR.charAt(this.akt_P%LainFuckM.CMDL));
	  if(erg !=null){
	    erg = "faild at eval "+codePc+" "+erg;  
	  };
	  break;
	case '&':
	  mvY(1);
	  erg = process(LainFuckM.CMDSTR.charAt(this.akt_P%LainFuckM.CMDL));
	  if(erg !=null){
	    erg = "faild at eval "+codePc+" "+erg;  
	  };
	  break;
	case '`':
	  mvY(-1);
	  erg = process(LainFuckM.CMDSTR.charAt(this.akt_P%LainFuckM.CMDL));
	  if(erg !=null){
	    erg = "faild at eval "+codePc+" "+erg;  
	  };
	  break;
	case '}':
	  mvZ(1);
	  erg = process(LainFuckM.CMDSTR.charAt(this.akt_P%LainFuckM.CMDL));
	  if(erg !=null){
	    erg = "faild at eval "+codePc+" "+erg;  
	  };
	  break;
	case '{':
	  mvZ(-1);
	  erg = process(LainFuckM.CMDSTR.charAt(this.akt_P%LainFuckM.CMDL));
	  if(erg !=null){
	    erg = "faild at eval "+codePc+" "+erg;  
	  };
	  break;
	case ' ':
	  break;
	default:
	  erg = "unknown char";
      };  
    }else{
      switch(cmd){
	case ']':
	  if(eckLoopStack.isEmpty()){
	    erg = "missing open parentethis at pos " + codePc; 
	  }else{
	    eckLoopStack.pop();
	    noExec = false;	
	  };
	  break;
	case ')':
	  if(rouLoopStack.isEmpty()){
	    erg = "missing open parentethis at pos " + codePc;
	  }else{
	    rouLoopStack.pop();  
	    noExec = false;
	  };
	  break;
	default:
	  Debug.log("exec paused "+cmd);
	  break;
      };	
    };
    return erg;
  };























































































































































































































































































































































































































































































































































































































































































































































































































































  
  private boolean pause(){
    boolean dbgerr = false;
    char cmd = Debug.getCMD();
    boolean quit=false;
    int len = this.code.length();
    while(!quit){
      switch(cmd){
	case 'c':
	  Debug.log("return execution at pc "+ codePc);
	  quit = true;
	  break;
	case 's':
	  Debug.log("step to pc "+codePc);
	  codePc += 1;
	  if(codePc >= len){
	    Debug.log("reached eoc at pc "+codePc);  
	    quit = true;
	  }else{
	    String err =  process(code.charAt(codePc));  
	    if(err !=null){
	      Debug.log("error acured at pc "+codePc);
	      dbgerr = true;
	      quit = true;  
	    }else{
	      Debug.log("executed "+code.charAt(codePc)+" pc "+codePc);  
	    };
	    codePc++;
	  };
	  break;
	case 'p':
	  Debug.log("akt_P at pc " + codePc + " " + akt_P);
	  break;
	case 'x':
	  Debug.log("akt_x at pc " + codePc + " " + akt_x);
	  break;
	case 'y':
	  Debug.log("akt_y at pc " + codePc + " " + akt_y);
	  break;
	case 'z':
	  Debug.log("akt_z at pc " + codePc + " " + akt_z);
	  break;
	case 'r':
	  Debug.log("helpR at pc " + codePc + " " + helpR);
	  break;
	case 'i':
	  Debug.log("inc akt_P");
	  addP(1);
	  break;
	case 'd':
	  Debug.log("inc akt_P");
	  addP(-1);
	  break;
	case 'h':
	  Debug.log("cpxyzridhq");
	  break;
	case 'q':
	  Debug.log("exit at pc "+codePc);
	  exit = true;
	  quit = true;
	  break;
	case 'o':
	  Debug.log("code: "+code+" "+code.length());
	  break;
	default:
	  Debug.log("dont know char "+cmd);
	  break;
      };
      cmd = Debug.getCMD();
    };
    return dbgerr;
  };
  private void reset(){
    for(int x = 0;x<DIAM;x++){
      for(int y = 0;y<DIAM;y++){
	for(int z = 0;z<DIAM;z++){
	  this.memory[x][y][z]=0;
	};  
      };
    };  
  };

































































































  private void update(){
    this.akt_P = this.memory[this.akt_x][this.akt_y][this.akt_z];
  };
  private void mvX(int i){
    this.akt_x += i+DIAM;
    this.akt_x %= DIAM;	
    this.update();
  };
  private void mvY(int i){
    this.akt_y += i+DIAM;
    this.akt_y %= DIAM;	
    this.update();
  };


















  private void mvZ(int i){
    this.akt_z += i+DIAM;
    this.akt_z %= DIAM;	
    this.update();
  };
  private void addP(int i){
    this.memory[this.akt_x][this.akt_y][this.akt_z] += i+0xffff;
    this.memory[this.akt_x][this.akt_y][this.akt_z] %=0xffff;
    this.update();
  };
};
//interpretation mit beschrenkter debug funktion kaum error
