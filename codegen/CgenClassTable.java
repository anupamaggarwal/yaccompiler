/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

// This is a project skeleton file

import javax.rmi.CORBA.Util;
import java.io.PrintStream;
import java.util.*;

/**
 * This class is used for representing the inheritance tree during code
 * generation. You will need to fill in some of its methods and
 * potentially extend it in other useful ways.
 */
class CgenClassTable extends SymbolTable {

    /**
     * All classes in the program, represented as CgenNode
     */
    private Vector nds;
    private Set<String>basicClassNames = new HashSet<String>();

    public static int getLabelCounter() {
        return labelCounter;
    }

    private static int  labelCounter = 0;
    public static void incrementLabelCounter(){
        labelCounter++;
    }


    /**
     * This is the stream to which assembly instructions are output
     */
    private PrintStream str;

    public static String getCurrentClass() {
        return currentClass;
    }

    private static String currentClass ;
    private int stringclasstag;
    private int intclasstag;
    private int boolclasstag;

    private Map<String,Integer> offsetTagClass;

    public static Map<String, List<String>> getDispatchList() {
        return dispatchList;
    }

    private static Map<String, List<String>>dispatchList;
    private int offsetPtr;


    // The following methods emit code for constants and global
    // declarations.

    /**
     * Emits code to start the .data segment and to
     * declare the global names.
     */
    private void codeGlobalData() {
        // The following global names must be defined first.

        str.print("\t.data\n" + CgenSupport.ALIGN);
        str.println(CgenSupport.GLOBAL + CgenSupport.CLASSNAMETAB);
        str.print(CgenSupport.GLOBAL);
        CgenSupport.emitProtObjRef(TreeConstants.Main, str);
        str.println("");
        str.print(CgenSupport.GLOBAL);
        CgenSupport.emitProtObjRef(TreeConstants.Int, str);
        str.println("");
        str.print(CgenSupport.GLOBAL);
        CgenSupport.emitProtObjRef(TreeConstants.Str, str);
        str.println("");
        str.print(CgenSupport.GLOBAL);
        BoolConst.falsebool.codeRef(str);
        str.println("");
        str.print(CgenSupport.GLOBAL);
        BoolConst.truebool.codeRef(str);
        str.println("");
        str.println(CgenSupport.GLOBAL + CgenSupport.INTTAG);
        str.println(CgenSupport.GLOBAL + CgenSupport.BOOLTAG);
        str.println(CgenSupport.GLOBAL + CgenSupport.STRINGTAG);

        // We also need to know the tag of the Int, String, and Bool classes
        // during code generation.

        str.println(CgenSupport.INTTAG + CgenSupport.LABEL
                + CgenSupport.WORD + intclasstag);
        str.println(CgenSupport.BOOLTAG + CgenSupport.LABEL
                + CgenSupport.WORD + boolclasstag);
        str.println(CgenSupport.STRINGTAG + CgenSupport.LABEL
                + CgenSupport.WORD + stringclasstag);

    }


    private void codeClassNameTab(){
        str.print(CgenSupport.CLASSNAMETAB + CgenSupport.LABEL);
        for (Enumeration e = nds.elements(); e.hasMoreElements(); ) {
            Class_ cl = (Class_) e.nextElement();
            StringSymbol s = (StringSymbol) StringTable.stringtable.lookup(cl.getName().toString());
             str.print(CgenSupport.WORD);s.codeRef(str); str.println();
        }

    }


    /**
     * Emits code to start the .text segment and to
     * declare the global names.
     */
    private void codeGlobalText() {
        str.println(CgenSupport.GLOBAL + CgenSupport.HEAP_START);
        str.print(CgenSupport.HEAP_START + CgenSupport.LABEL);
        str.println(CgenSupport.WORD + 0);
        str.println("\t.text");
        str.print(CgenSupport.GLOBAL);
        CgenSupport.emitInitRef(TreeConstants.Main, str);
        str.println("");
        str.print(CgenSupport.GLOBAL);
        CgenSupport.emitInitRef(TreeConstants.Int, str);
        str.println("");
        str.print(CgenSupport.GLOBAL);
        CgenSupport.emitInitRef(TreeConstants.Str, str);
        str.println("");
        str.print(CgenSupport.GLOBAL);
        CgenSupport.emitInitRef(TreeConstants.Bool, str);
        str.println("");
        str.print(CgenSupport.GLOBAL);
        CgenSupport.emitMethodRef(TreeConstants.Main, TreeConstants.main_meth, str);
        str.println("");
    }

    /**
     * Emits code definitions for boolean constants.
     */
    private void codeBools(int classtag) {
        BoolConst.falsebool.codeDef(classtag, str);
        BoolConst.truebool.codeDef(classtag, str);
    }

    /**
     * Generates GC choice constants (pointers to GC functions)
     */
    private void codeSelectGc() {
        str.println(CgenSupport.GLOBAL + "_MemMgr_INITIALIZER");
        str.println("_MemMgr_INITIALIZER:");
        str.println(CgenSupport.WORD
                + CgenSupport.gcInitNames[Flags.cgen_Memmgr]);

        str.println(CgenSupport.GLOBAL + "_MemMgr_COLLECTOR");
        str.println("_MemMgr_COLLECTOR:");
        str.println(CgenSupport.WORD
                + CgenSupport.gcCollectNames[Flags.cgen_Memmgr]);

        str.println(CgenSupport.GLOBAL + "_MemMgr_TEST");
        str.println("_MemMgr_TEST:");
        str.println(CgenSupport.WORD
                + ((Flags.cgen_Memmgr_Test == Flags.GC_TEST) ? "1" : "0"));
    }

    /**
     * Emits code to reserve space for and initialize all of the
     * constants.  Class names should have been added to the string
     * table (in the supplied code, is is done during the construction
     * of the inheritance graph), and code for emitting string constants
     * as a side effect adds the string's length to the integer table.
     * The constants are emmitted by running through the stringtable and
     * inttable and producing code for each entry.
     */
    private void codeConstants() {
        // Add constants that are required by the code generator.
        AbstractTable.stringtable.addString("");
        AbstractTable.inttable.addString("0");
        AbstractTable.stringtable.codeStringTable(stringclasstag, str);
        AbstractTable.inttable.codeStringTable(intclasstag, str);
        codeBools(boolclasstag);
    }


    /**
     * Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     */
    private void installBasicClasses() {
        AbstractSymbol filename
                = AbstractTable.stringtable.addString("<basic class>");

        // A few special class names are installed in the lookup table
        // but not the class list.  Thus, these classes exist, but are
        // not part of the inheritance hierarchy.  No_class serves as
        // the parent of Object and the other special classes.
        // SELF_TYPE is the self class; it cannot be redefined or
        // inherited.  prim_slot is a class known to the code generator.

        addId(TreeConstants.No_class,
                new CgenNode(new class_(0,
                        TreeConstants.No_class,
                        TreeConstants.No_class,
                        new Features(0),
                        filename),
                        CgenNode.Basic, this));


        addId(TreeConstants.SELF_TYPE,
                new CgenNode(new class_(0,
                        TreeConstants.SELF_TYPE,
                        TreeConstants.No_class,
                        new Features(0),
                        filename),
                        CgenNode.Basic, this));

        addId(TreeConstants.prim_slot,
                new CgenNode(new class_(0,
                        TreeConstants.prim_slot,
                        TreeConstants.No_class,
                        new Features(0),
                        filename),
                        CgenNode.Basic, this));

        // The Object class has no parent class. Its methods are
        //        cool_abort() : Object    aborts the program
        //        type_name() : Str        returns a string representation
        //                                 of class name
        //        copy() : SELF_TYPE       returns a copy of the object

        class_ Object_class =
                new class_(0,
                        TreeConstants.Object_,
                        TreeConstants.No_class,
                        new Features(0)
                                .appendElement(new method(0,
                                        TreeConstants.cool_abort,
                                        new Formals(0),
                                        TreeConstants.Object_,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.type_name,
                                        new Formals(0),
                                        TreeConstants.Str,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.copy,
                                        new Formals(0),
                                        TreeConstants.SELF_TYPE,
                                        new no_expr(0))),
                        filename);

        installClass(new CgenNode(Object_class, CgenNode.Basic, this));
        basicClassNames.add(Object_class.getName().getString());

        // The IO class inherits from Object. Its methods are
        //        out_string(Str) : SELF_TYPE  writes a string to the output
        //        out_int(Int) : SELF_TYPE      "    an int    "  "     "
        //        in_string() : Str            reads a string from the input
        //        in_int() : Int                "   an int     "  "     "

        class_ IO_class =
                new class_(0,
                        TreeConstants.IO,
                        TreeConstants.Object_,
                        new Features(0)
                                .appendElement(new method(0,
                                        TreeConstants.out_string,
                                        new Formals(0)
                                                .appendElement(new formal(0,
                                                        TreeConstants.arg,
                                                        TreeConstants.Str)),
                                        TreeConstants.SELF_TYPE,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.out_int,
                                        new Formals(0)
                                                .appendElement(new formal(0,
                                                        TreeConstants.arg,
                                                        TreeConstants.Int)),
                                        TreeConstants.SELF_TYPE,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.in_string,
                                        new Formals(0),
                                        TreeConstants.Str,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.in_int,
                                        new Formals(0),
                                        TreeConstants.Int,
                                        new no_expr(0))),
                        filename);

        installClass(new CgenNode(IO_class, CgenNode.Basic, this));
        basicClassNames.add(IO_class.getName().getString());

        // The Int class has no methods and only a single attribute, the
        // "val" for the integer.

        class_ Int_class =
                new class_(0,
                        TreeConstants.Int,
                        TreeConstants.Object_,
                        new Features(0)
                                .appendElement(new attr(0,
                                        TreeConstants.val,
                                        TreeConstants.prim_slot,
                                        new no_expr(0))),
                        filename);

        installClass(new CgenNode(Int_class, CgenNode.Basic, this));
        basicClassNames.add(Int_class.getName().getString());

        // Bool also has only the "val" slot.
        class_ Bool_class =
                new class_(0,
                        TreeConstants.Bool,
                        TreeConstants.Object_,
                        new Features(0)
                                .appendElement(new attr(0,
                                        TreeConstants.val,
                                        TreeConstants.prim_slot,
                                        new no_expr(0))),
                        filename);

        installClass(new CgenNode(Bool_class, CgenNode.Basic, this));
        basicClassNames.add(Bool_class.getName().getString());

        // The class Str has a number of slots and operations:
        //       val                              the length of the string
        //       str_field                        the string itself
        //       length() : Int                   returns length of the string
        //       concat(arg: Str) : Str           performs string concatenation
        //       substr(arg: Int, arg2: Int): Str substring selection

        class_ Str_class =
                new class_(0,
                        TreeConstants.Str,
                        TreeConstants.Object_,
                        new Features(0)
                                .appendElement(new attr(0,
                                        TreeConstants.val,
                                        TreeConstants.Int,
                                        new no_expr(0)))
                                .appendElement(new attr(0,
                                        TreeConstants.str_field,
                                        TreeConstants.prim_slot,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.length,
                                        new Formals(0),
                                        TreeConstants.Int,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.concat,
                                        new Formals(0)
                                                .appendElement(new formal(0,
                                                        TreeConstants.arg,
                                                        TreeConstants.Str)),
                                        TreeConstants.Str,
                                        new no_expr(0)))
                                .appendElement(new method(0,
                                        TreeConstants.substr,
                                        new Formals(0)
                                                .appendElement(new formal(0,
                                                        TreeConstants.arg,
                                                        TreeConstants.Int))
                                                .appendElement(new formal(0,
                                                        TreeConstants.arg2,
                                                        TreeConstants.Int)),
                                        TreeConstants.Str,
                                        new no_expr(0))),
                        filename);

        installClass(new CgenNode(Str_class, CgenNode.Basic, this));
        basicClassNames.add(Str_class.getName().getString());

    }

    // The following creates an inheritance graph from
    // a list of classes.  The graph is implemented as
    // a tree of `CgenNode', and class names are placed
    // in the base class symbol table.

    private void installClass(CgenNode nd) {
        AbstractSymbol name = nd.getName();
        if (probe(name) != null) return;
        Utilities.debug_print("[Adding name]" + name);
        nds.addElement(nd);
        addId(name, nd);
    }

    private void installClasses(Classes cs) {
        for (Enumeration e = cs.getElements(); e.hasMoreElements(); ) {
            Class_ cl = (Class_) e.nextElement();
            Utilities.debug_print("[User Class installed] " + cl.getName());

            installClass(new CgenNode(cl,
                    CgenNode.NotBasic, this));
           if(offsetTagClass.get(cl.getName())==null)
               this.offsetTagClass.put(cl.getName().getString(),offsetPtr++);
        }
    }

    private void buildInheritanceTree() {
        for (Enumeration e = nds.elements(); e.hasMoreElements(); ) {
            setRelations((CgenNode) e.nextElement());
        }
    }

    private void setRelations(CgenNode nd) {
        CgenNode parent = (CgenNode) probe(nd.getParent());
        nd.setParentNd(parent);
        parent.addChild(nd);
    }

    /**
     * Constructs a new class table and invokes the code generator
     */
    public CgenClassTable(Classes cls, PrintStream str) {
        nds = new Vector();
        this.offsetTagClass = new HashMap<String,Integer>();
        dispatchList  = new HashMap<String,List<String>>();
        //add predefined tags for all the declared types

        this.str = str;

        stringclasstag = 4 /* Change to your String class tag here */;
        intclasstag = 2 /* Change to your Int class tag here */;
        boolclasstag = 3 /* Change to your Bool class tag here */;

        this.offsetTagClass.put(TreeConstants.Str.getString(),stringclasstag);
        this.offsetTagClass.put(TreeConstants.Int.getString(),intclasstag);
        this.offsetTagClass.put(TreeConstants.Bool.getString(),boolclasstag);
        this.offsetTagClass.put(TreeConstants.IO.getString(),1);
        this.offsetTagClass.put(TreeConstants.Object_.getString(),0);
        this.offsetPtr = 5;

        enterScope();
        if (Flags.cgen_debug) System.out.println("Building CgenClassTable");

        installBasicClasses();
        installClasses(cls);
        buildInheritanceTree();

        code();

        exitScope();
    }

    /**
     * This method is the meat of the code generator.  It is to be
     * filled in programming assignment 5
     */
    public void code() {
        if (Flags.cgen_debug) System.out.println("coding global data");
        codeGlobalData();

        if (Flags.cgen_debug) System.out.println("choosing gc");
        codeSelectGc();

        if (Flags.cgen_debug) System.out.println("coding constants");
        codeConstants();
        codeDispatchTable();

        //                 Add your code to emit
        //                   - dispatch tables
        codeClassNameTab();
        codeProtoObjects();

        //TODO complete methods and corresponding dispatch

        if (Flags.cgen_debug) System.out.println("coding global text");
        codeGlobalText();
        codeInitMethods();
        codeClassMethods();

        //                 Add your code to emit
        //                   - object initializer
        //                   - the class methods
        //                   - etc...
    }

    private void codeInitMethods() {
        Utilities.debug_print("[Code Initialization]");
        for (Enumeration e = nds.elements(); e.hasMoreElements(); ) {
            CgenNode cN = (CgenNode) e.nextElement();
            writeInitMethod(cN, cN.getParentNd());


        }
    }

    private void codeClassMethods() {
        //TODO has to be done in 2 passes first pass just collects scope



        Utilities.debug_print("[Code class methods]");
        //write code for all classes except for object and built in classes
        for (Enumeration e = nds.elements(); e.hasMoreElements(); ) {
            CgenNode cN = (CgenNode) e.nextElement();
            //if not part of basic class we generate code
            if(!basicClassNames.contains(cN.getName().getString()))
                 writeCodeForMethodInClass(cN);


        }
    }


    private void writeCodeForMethodInClass(CgenNode cN) {
        Utilities.debug_print("Writing dispatch code for class "+cN.getName().getString());
        Features f = cN.getFeatures();
        for(Enumeration e =f.getElements();e.hasMoreElements();){
            Feature ft = (Feature) e.nextElement();
            if(ft instanceof  method){
                method m = (method) ft;
                Utilities.debug_print("********** In method " + m.name.getString()+ " ************");


                //add knowledge of formals for this method with their offsets
                //this is 12 because of a reason.....
                /*
                 sp is sp - 12
                 fp is sp+4...ie 8 in offset
                 first word is pushed at double word offset from fp...12
                 arg 2 is pushed at 16 and so on
                */

                int offsetFunctionParam = 2;
                int numArg = 0;
                for(Enumeration em= m.formals.getElements();em.hasMoreElements();){
                    formal fm = (formal) em.nextElement();
                    CgenSupport.addToScope(CgenSupport.ScopeType.FUNCTION_ARGS,fm.name.getString(),offsetFunctionParam);
                    offsetFunctionParam = offsetFunctionParam+1;
                    numArg++;
                }

                //find offset in the table

                str.print( cN.getName().getString()+"."+m.name.getString()+ CgenSupport.LABEL);
                //generate code for all expressions here
                //push stack contents here
                CgenClassTable.saveEnvironment(str);
                CgenClassTable.currentClass = cN.getName().getString();
                m.expr.code(str);
                //stack pointer is responsibilty of code to restore
                CgenClassTable.loadEnvironment(numArg,this.str);

            }
        }

    }

    public static void loadEnvironment(int numArg,PrintStream str) {
        Utilities.debug_print("Restoring stack with offset " +numArg,str);
        CgenSupport.emitMove(CgenSupport.ACC,CgenSupport.SELF,str);
        CgenSupport.emitLoad(CgenSupport.FP,3,CgenSupport.SP,str);
        CgenSupport.emitLoad(CgenSupport.SELF,2,CgenSupport.SP,str);
        CgenSupport.emitLoad(CgenSupport.RA,1,CgenSupport.SP,str);
        CgenSupport.emitAddiu(CgenSupport.SP,CgenSupport.SP,12+numArg*4,str);
        CgenSupport.emitReturn(str);
    }

    public static void saveEnvironment(PrintStream str) {
        CgenSupport.emitAddiu(CgenSupport.SP,CgenSupport.SP,-12,str);
        CgenSupport.emitStore(CgenSupport.FP,3,CgenSupport.SP,str);
        CgenSupport.emitStore(CgenSupport.SELF,2,CgenSupport.SP,str);
        CgenSupport.emitStore(CgenSupport.RA,1,CgenSupport.SP,str);
        CgenSupport.emitAddiu(CgenSupport.FP,CgenSupport.SP,4,str);

    }

    private void writeInitMethod(CgenNode cN, CgenNode parentNd) {
        str.print(cN.getName().getString() + CgenSupport.CLASSINIT_SUFFIX+CgenSupport.LABEL);
        //if parentNd is NoClass we won't write it out otherwise we can write it
        if(parentNd!=null && !parentNd.getName().equals(TreeConstants.No_class)){
            initTemplate(true, parentNd);
        }else{
            //this is probably an object class
            initTemplate(false, parentNd);
        }
    }

    private void initTemplate(boolean initializeParent, CgenNode parent){
       CgenSupport.emitAddiu(CgenSupport.SP,CgenSupport.SP,-12,str);
        CgenSupport.emitStore(CgenSupport.FP,3,CgenSupport.SP,str);
        CgenSupport.emitStore(CgenSupport.SELF,2,CgenSupport.SP,str);
        CgenSupport.emitStore(CgenSupport.RA,1,CgenSupport.SP,str);
        CgenSupport.emitAddiu(CgenSupport.FP,CgenSupport.SP,4,str);
        CgenSupport.emitMove(CgenSupport.SELF,CgenSupport.ACC,str);
        if(initializeParent)
            CgenSupport.emitJal(parent.getName().getString()+CgenSupport.CLASSINIT_SUFFIX,str);
        CgenSupport.emitMove(CgenSupport.ACC,CgenSupport.SELF,str);
        CgenSupport.emitLoad(CgenSupport.FP,3,CgenSupport.SP,str);
        CgenSupport.emitLoad(CgenSupport.SELF,2,CgenSupport.SP,str);
        CgenSupport.emitLoad(CgenSupport.RA,1,CgenSupport.SP,str);
        CgenSupport.emitAddiu(CgenSupport.SP,CgenSupport.SP,12,str);
        CgenSupport.emitReturn(str);
    }


    private void codeDispatchTable() {
        for (Enumeration e = nds.elements(); e.hasMoreElements(); ) {
            CgenNode cN = (CgenNode) e.nextElement();
            Utilities.debug_print("\n-------\nCurrent Node:"+cN.getName().getString());
            List<String> methodList = new ArrayList<String>();
            writeMethodAttribs(cN,methodList);
            //Utilities.debug_print(methodList.toString());
            str.print(cN.getName().getString() + CgenSupport.DISPTAB_SUFFIX+CgenSupport.LABEL);
            for(String s: methodList){
                str.println(CgenSupport.WORD + s);
            }
            this.dispatchList.put(cN.getName().getString(),methodList);

        }
    }

    private void writeMethodAttribs(CgenNode n, List<String> dispatchList){
        //if this is the base Object class add all methods
        if(n!=null && n.getParentNd()!=null &&
                !n.getParentNd().getName().equals(TreeConstants.No_class)){
            writeMethodAttribs(n.getParentNd(),dispatchList);
        }
        //possibly object print all methods check for existance in list
        for(Enumeration e = n.getFeatures().getElements(); e.hasMoreElements(); ){
            Feature f = (Feature) e.nextElement();
            if(f instanceof  method){
                // Print this out
                method m = (method) f;

                //iterate through the list to check if this is already present in base class
                // in which we
                //override the base class definition
                int index = 0;
                for(String name: dispatchList){
                    //present in list

                    if(name.contains(m.name.getString())){
                        Utilities.debug_print("Overridden method "+m.name.getString());
                        dispatchList.set(index,n.getName()+"."+m.name.getString());

                    }
                    index++;

                }

                dispatchList.add(n.getName() + "."+m.name.getString());
            }
        }

    }

    /**
     * Create prototype objects
     */
    private void codeProtoObjects() {
        Utilities.debug_print("Offset ptr"+ this.offsetTagClass);
        for (Enumeration e = nds.elements(); e.hasMoreElements(); ) {
            Class_ cl = (Class_) e.nextElement();
            emitProtoObject(cl);
        }
        //Also emit a class_objTab according to the offsets
        str.print(CgenSupport.CLASSOBJTAB+CgenSupport.LABEL);

        for(int i = 0; i < offsetPtr;i++){
            for(String cl: offsetTagClass.keySet()){
                if(offsetTagClass.get(cl)==i){
                    //write this out
                    str.println(CgenSupport.WORD+ cl+CgenSupport.PROTOBJ_SUFFIX);
                    str.println(CgenSupport.WORD+ cl+CgenSupport.CLASSINIT_SUFFIX);
                }
            }
        }


    }

    private void emitProtoObject(Class_ cl) {
        //emits defintion for the prototype object for this class
        int size=3;
        for (Enumeration e = cl.getFeatures().getElements(); e.hasMoreElements(); ) {
           Feature f = (Feature) e.nextElement();
            if(f instanceof  attr){
                size++;

            }
        }
        str.print(cl.getName()+CgenSupport.PROTOBJ_SUFFIX + CgenSupport.LABEL);
        str.println(CgenSupport.WORD + this.offsetTagClass.get(cl.getName().getString()));
        str.println(CgenSupport.WORD+Integer.toString(size));
        str.println(CgenSupport.WORD + cl.getName()+CgenSupport.DISPTAB_SUFFIX);
        if(size!=3)
            str.println(CgenSupport.WORD+"0");
        str.println(CgenSupport.WORD  + "-1");
        //get size for this and tag for this class

    }

    /**
     * Gets the root of the inheritance tree
     */
    public CgenNode root() {
        return (CgenNode) probe(TreeConstants.Object_);
    }
}

    
