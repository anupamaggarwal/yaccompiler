// -*- mode: java -*- 
//
// file: cool-tree.m4
//
// This file defines the AST
//
//////////////////////////////////////////////////////////






import java.util.*;
import java.io.PrintStream;


/**
 * Defines simple phylum Program
 */
abstract class Program extends TreeNode {
    protected Program(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

    public abstract void semant();

}


/**
 * Defines simple phylum Class_
 */
abstract class Class_ extends TreeNode {
    protected Class_(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

}


/**
 * Defines list phylum Classes
 * <p>
 * See <a href="ListNode.html">ListNode</a> for full documentation.
 */
class Classes extends ListNode {
    public final static Class elementClass = Class_.class;
    //needed for self types
    public  static String class_self;

    /**
     * Returns class of this lists's elements
     */
    public Class getElementClass() {
        return elementClass;
    }

    protected Classes(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /**
     * Creates an empty "Classes" list
     */
    public Classes(int lineNumber) {
        super(lineNumber);
    }

    /**
     * Appends "Class_" element to this list
     */
    public Classes appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Classes(lineNumber, copyElements());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
          Set<String> classEncountered;

        classEncountered = new HashSet<String>();

        SymbolTable sym = classTable.getSymbolTable();
        //check for main
        if(!classTable.getClassMap().containsKey(TreeConstants.Main.getString())){
            classTable.semantError().
                    println("Class Main is not defined.");
        }

        for (Enumeration e = this.getElements(); e.hasMoreElements(); ) {
            class_c cl = (class_c) e.nextElement();
            if(classEncountered.contains(cl.name.getString())){
                debug_print("Multiple definition of class "+ cl.name.getString());
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                        println("Class "+ cl.name.getString() + " was previously defined");
                continue;
            }
            classEncountered.add(cl.name.getString());

            if(classTable.getDefinedClasses().contains(cl.getName().getString())){
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                        println("Redefinition of basic class "+cl.getName().getString()+".");
                continue;
            }
            cl.typeCheck(classTable);
            // TODO record the parent of this class to keep track of
            // methods used on identifier declared with this type
        }
    }
}


/**
 * Defines simple phylum Feature
 */
abstract class Feature extends TreeNode {
    protected Feature(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

    public abstract void populateScope(ClassTable ctable);
}


/**
 * Defines list phylum Features
 * <p>
 * See <a href="ListNode.html">ListNode</a> for full documentation.
 */
class Features extends ListNode {
    public final static Class elementClass = Feature.class;

    /**
     * Returns class of this lists's elements
     */
    public Class getElementClass() {
        return elementClass;
    }

    protected Features(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /**
     * Creates an empty "Features" list
     */
    public Features(int lineNumber) {
        super(lineNumber);
    }

    /**
     * Appends "Feature" element to this list
     */
    public Features appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Features(lineNumber, copyElements());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("In features type check");
        for (Enumeration e = getElements(); e.hasMoreElements(); ) {
            Feature ft = (Feature) e.nextElement();
            ft.typeCheck(classTable);
        }
    }
}


/**
 * Defines simple phylum Formal
 */
abstract class Formal extends TreeNode {
    protected Formal(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

}


/**
 * Defines list phylum Formals
 * <p>
 * See <a href="ListNode.html">ListNode</a> for full documentation.
 */
class Formals extends ListNode {
    public final static Class elementClass = Formal.class;
    /**
     * Returns class of this lists's elements
     */
    public Class getElementClass() {
        return elementClass;
    }

    protected Formals(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /**
     * Creates an empty "Formals" list
     */
    public Formals(int lineNumber) {
        super(lineNumber);
    }

    /**
     * Appends "Formal" element to this list
     */
    public Formals appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Formals(lineNumber, copyElements());
    }

    @Override
    public void typeCheck(ClassTable classTable) {

        Set<String> identifierEncountered = new HashSet<String>();
       // debug_print("Type checking for formals list ");
        for (Enumeration en = getElements(); en.hasMoreElements(); ) {
            formalc f = (formalc) en.nextElement();
            debug_print("Just parsed formal named " + f.name.getString());
            if(identifierEncountered.contains(f.name.getString())){
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).println("Formal parameter "+ f.name.getString() +
                        " is multiply defined." );
            }
            identifierEncountered.add(f.name.getString());
            f.typeCheck(classTable);

        }
        //debug_print("Scope after formals seen "+ classTable.getSymbolTable().toString());

    }
}


/**
 * Defines simple phylum Expression
 */
abstract class Expression extends TreeNode {
    protected Expression(int lineNumber) {
        super(lineNumber);
    }

    private AbstractSymbol type = null;

    public AbstractSymbol get_type() {
        return type;
    }

    public Expression set_type(AbstractSymbol s) {
        type = s;
        return this;
    }

    public abstract void dump_with_types(PrintStream out, int n);

    public void dump_type(PrintStream out, int n) {
        if (type != null) {
            out.println(Utilities.pad(n) + ": " + type.getString());
        } else {
            out.println(Utilities.pad(n) + ": _no_type");
        }
    }

}


/**
 * Defines list phylum Expressions
 * <p>
 * See <a href="ListNode.html">ListNode</a> for full documentation.
 */
class Expressions extends ListNode {
    public final static Class elementClass = Expression.class;

    /**
     * Returns class of this lists's elements
     */
    public Class getElementClass() {
        return elementClass;
    }

    protected Expressions(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /**
     * Creates an empty "Expressions" list
     */
    public Expressions(int lineNumber) {
        super(lineNumber);
    }

    /**
     * Appends "Expression" element to this list
     */
    public Expressions appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Expressions(lineNumber, copyElements());
    }

    @Override
    public void typeCheck(ClassTable classTable) {

    }
}


/**
 * Defines simple phylum Case
 */
abstract class Case extends TreeNode {
    protected Case(int lineNumber) {
        super(lineNumber);
    }

    public abstract void dump_with_types(PrintStream out, int n);

}


/**
 * Defines list phylum Cases
 * <p>
 * See <a href="ListNode.html">ListNode</a> for full documentation.
 */
class Cases extends ListNode {
    public final static Class elementClass = Case.class;

    /**
     * Returns class of this lists's elements
     */
    public Class getElementClass() {
        return elementClass;
    }

    protected Cases(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }

    /**
     * Creates an empty "Cases" list
     */
    public Cases(int lineNumber) {
        super(lineNumber);
    }

    /**
     * Appends "Case" element to this list
     */
    public Cases appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }

    public TreeNode copy() {
        return new Cases(lineNumber, copyElements());
    }

    @Override
    public void typeCheck(ClassTable classTable) {

    }
}


/**
 * Defines AST constructor 'programc'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class programc extends Program {

    protected Classes classes;

    /**
     * Creates "programc" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for classes
     */
    public programc(int lineNumber, Classes a1) {
        super(lineNumber);
        classes = a1;
    }

    public TreeNode copy() {
        return new programc(lineNumber, (Classes) classes.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("In typeChecker for programc");
        this.classes.typeCheck(classTable);

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "programc\n");
        classes.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_program");
        for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
            // sm: changed 'n + 1' to 'n + 2' to match changes elsewhere
            ((Class_) e.nextElement()).dump_with_types(out, n + 2);
        }
    }

    /**
     * This method is the entry point to the semantic checker.  You will
     * need to complete it in programming assignment 4.
     * <p>
     * Your checker should do the following two things:
     * <ol>
     * <li>Check that the program is semantically correct
     * <li>Decorate the abstract syntax tree with type information
     * by setting the type field in each Expression node.
     * (see tree.h)
     * </ol>
     * <p>
     * You are free to first do (1) and make sure you catch all semantic
     * errors. Part (2) can be done in a second stage when you want
     * to test the complete compiler.
     */
    public void semant() {
    /* ClassTable constructor may do some semantic analysis */
         ClassTable classTable = new ClassTable(classes);
        this.typeCheck(classTable);

	/* some semantic analysis code may go here */
        if (classTable.errors()) {
            System.err.println("Compilation halted due to static semantic errors.");
            System.exit(1);
        }
    }

    private void classLevelSemanticChecks(ClassTable ctable) {
        Map<String,Object> classMap = ctable.getClassMap();
        boolean found = false;
        /** Has to be a main method **/
        for (String str : classMap.keySet()) {
            if ("Main".equals(str))
                found = true;
        }
        if (!found) {
            ctable.semantError().print("Class Main is not defined.\n");
        }
    }


}


/**
 * Defines AST constructor 'class_c'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class class_c extends Class_ {
    protected AbstractSymbol name;
    protected AbstractSymbol parent;

    public Features getFeatures() {
        return features;
    }

    protected Features features;
    protected AbstractSymbol filename;

    /**
     * Creates "class_c" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for name
     * @param a1         initial value for parent
     * @param a2         initial value for features
     * @param a3         initial value for filename
     */
    public class_c(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Features a3, AbstractSymbol a4) {
        super(lineNumber);
        name = a1;
        parent = a2;
        features = a3;
        filename = a4;
    }

    public TreeNode copy() {
        return new class_c(lineNumber, copy_AbstractSymbol(name),
                copy_AbstractSymbol(parent), (Features) features.copy(), copy_AbstractSymbol(filename));
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("\n\n\n ==== Type checking for individual class  " + name.getString());
        if(name.getString().equals(TreeConstants.SELF_TYPE.getString())){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this)
                    .println("Redefinition of basic class SELF_TYPE.");
        }

        if(parent.getString().equals(TreeConstants.Bool.getString())){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("Class" + name.getString()+" cannot inherit class Bool");
        }


        if(parent.getString().equals(TreeConstants.Str.getString())){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("Class" + name.getString()+" cannot inherit class String");
        }
        Classes.class_self = name.getString();
        //Entering class scope here
        classTable.getSymbolTable().enterScope();
        checkClassScope(classTable);
        //debug_print("\n\n[class_c] "+name+" Inherited scope "+ classTable.getSymbolTable().toString());
        //this class has many features we now will have to typecheck them
        features.typeCheck(classTable);
        classTable.getSymbolTable().exitScope();;

    }
    private final void checkClassScope(ClassTable ctable) {
        //Check if class's parent is defined
        //Populate all symbols from classes parents

        if (!ctable.getClassMap().containsKey(this.getParent().getString())) {
            ctable.semantError(this.getFilename(), this);
            ctable.semantError(filename,this).print("Class" + this.getName() +
                    " inherits from an undefined class "
                    + this.getParent().getString() + "\n");
        }else{
            //populate with parent symbols
            //TODO uncomment this
            populateInheritedScope(ctable,parent.getString(),1);
        }
    }

    //Populates the symbol table with the inherited scope
    private void populateInheritedScope(ClassTable ctable, String strClassNode, int depth) {
        if(strClassNode.equals(TreeConstants.No_class.getString())){
            return;
        }
        class_c parentClass = (class_c) ctable.getClassMap().get(strClassNode);
        String indent=" ";
        for(int i =0; i< depth; i++)
            indent = indent + " ";
        for(int i =0; i< depth; i++)
            indent = indent + ">>";

        debug_print(indent+" " + strClassNode);

        populateInheritedScope(ctable,parentClass.getParent().getString(),++depth);
        //debug_print("[class_c]Entering scope for class " + parentClass.getName().getString());
        Features fs = parentClass.getFeatures();
        ctable.getSymbolTable().enterScope();
        for (Enumeration e = fs.getElements(); e.hasMoreElements(); ) {
            Feature f = (Feature) e.nextElement();
            f.populateScope(ctable);
        }
        //debug_print("Scope at end of parsing class "+strClassNode+" " +ctable.getSymbolTable().toString());
            //find parent of this and populate scope
        //parse parent and add to current scope


    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "class_c\n");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, parent);
        features.dump(out, n + 2);
        dump_AbstractSymbol(out, n + 2, filename);
    }


    public AbstractSymbol getFilename() {
        return filename;
    }

    public AbstractSymbol getName() {
        return name;
    }

    public AbstractSymbol getParent() {
        return parent;
    }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_class");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, parent);
        out.print(Utilities.pad(n + 2) + "\"");
        Utilities.printEscapedString(out, filename.getString());
        out.println("\"\n" + Utilities.pad(n + 2) + "(");
        for (Enumeration e = features.getElements(); e.hasMoreElements(); ) {
            ((Feature) e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
    }

    //checks for proper method invocation (expressions are contained in actual) and name is methodName
    //Assumption is that we have already determined type of actual passed in here

    //return type of checkMethodInvocation is the method return type
    public  boolean checkMethodInvocation(ClassTable classTable, String methodName,
                                                Expressions actual, dispatch typeInferred, AbstractSymbol caller) {

        debug_print("Method Invocation check for name:"  + methodName +
                " arguments:" + actual.toString()
        + " type:"+ this.getName().getString());

        //check if method is defined in this class
        AbstractSymbol returnType = TreeConstants.Object_;

        boolean isFound = false;
        for (Enumeration e = features.getElements(); e.hasMoreElements(); ) {
            Feature f = (Feature) e.nextElement();
            if (f instanceof method && ((method) f).name.getString().equals(methodName)) {
                debug_print("Found method to typecheck " + methodName);
                method m = (method) f;
                debug_print(" Return type " + m.return_type.getString());
                returnType = m.return_type;
                isFound = true;
                Enumeration eActuals = actual.getElements();
                for (Enumeration em = m.formals.getElements(); em.hasMoreElements(); ) {
                    formalc fml = (formalc) em.nextElement();
                    String expectedArg = fml.type_decl.getString();
                    if (!eActuals.hasMoreElements())
                        break;
                    Expression eActual = (Expression) eActuals.nextElement();
                    String actualArg = eActual.get_type().getString();

                    //check for inheritance here (really inheritance hiearchy)
                    class_c actualClassOfArg = (class_c) classTable.getClassMap().get(actualArg);
                    class_c expectedClassOfArg = (class_c) classTable.getClassMap().get(expectedArg);

                    if(actualClassOfArg!=null && actualClassOfArg.getParent().getString().equals
                            (expectedClassOfArg.getName().getString())){
                        debug_print("[class dispatch ] in inheritance hiearchy");
                        //hacky
                        actualArg = expectedArg;
                    }

                      debug_print("Actual argument type passed " + actualArg);
                    if(actualArg.equals(TreeConstants.SELF_TYPE.getString())){
                        class_c actualarg = (class_c) classTable.getClassMap().get(Classes.class_self);
                        actualArg = actualarg.getName().getString();
                        debug_print("Actual arg set to "+ actualArg);
                    }
                    //check for self type

                    if (!actualArg.equals(expectedArg)) {
                        classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename), this).
                                println("In call of method " + methodName +
                                        ", type " + actualArg + " of parameter " + fml.name.getString() +
                                        " does not conform to declared type " + expectedArg + ".");
                    }

                }
                if(returnType.equals(TreeConstants.SELF_TYPE)){
                    //set type to type of object
                    returnType = this.getName();
                    typeInferred.set_type(caller);
                }else
                     typeInferred.set_type(returnType);

                debug_print(",<<< -- Setting inferred type to " + returnType + " on class:" + name.getString());

            }
        }
        String parentStr = parent.getString();

        while(!isFound && !parentStr.equals(TreeConstants.No_class.getString())){
            debug_print("Currently in class " + parentStr);
             class_c parentClass = (class_c) classTable.getClassMap().get(parentStr);
            isFound = isFound ||
                        parentClass.checkMethodInvocation(classTable,methodName,actual,typeInferred,caller);

            parentStr = ((class_c)classTable.getClassMap().get(parentStr)).getParent().getString();
        }


        if(!isFound){
            //check its parent
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("Dispatch to undefined method "+ methodName+".");
        }

        return isFound;
        }

    public AbstractSymbol getReturnTypeForMethod(ClassTable classTable, String methodName) {
        debug_print("Inferring type on "+ methodName + " For class "+ name.getString());
        for (Enumeration e = features.getElements(); e.hasMoreElements(); ) {
            Feature f = (Feature) e.nextElement();
            if (f instanceof method && ((method) f).name.getString().equals(methodName)) {
                method m = (method) f;
                debug_print("[class getReturnTypeForMethod] found type "+ m.return_type);
                return m.return_type;
            }
        }
        //if you come here  you will need to search for parent
        class_c parentClass = (class_c) classTable.getClassMap().get(parent.getString());

        if (parentClass.getName().equals(TreeConstants.No_class.getString())){
            return TreeConstants.Object_;
        }else
          return parentClass.getReturnTypeForMethod(classTable, methodName);
    }
}


/**
 * Defines AST constructor 'method'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class method extends Feature {
    protected AbstractSymbol name;
    protected Formals formals;
    protected AbstractSymbol return_type;
    protected Expression expr;

    /**
     * Creates "method" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for name
     * @param a1         initial value for formals
     * @param a2         initial value for return_type
     * @param a3         initial value for expr
     */
    public method(int lineNumber, AbstractSymbol a1, Formals a2, AbstractSymbol a3, Expression a4) {
        super(lineNumber);
        name = a1;
        formals = a2;
        return_type = a3;
        expr = a4;
    }

    public TreeNode copy() {
        return new method(lineNumber, copy_AbstractSymbol(name), (Formals) formals.copy(), copy_AbstractSymbol(return_type), (Expression) expr.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {

       // System.out.println("Added Method name " +name.getString());
        // TODO check method name with different arguments for overloading

        debug_print("In method type check"+ name.getString());

        if(   !return_type.getString().equals(TreeConstants.SELF_TYPE.getString()) &&
                !classTable.getClassMap().containsKey(return_type.getString())){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this)
                    .print("Undefined returnType "+ return_type.getString()+ " in method "
            + name+".\n");
            return;
        }

        //debug_print("Current scope before entering method  "+name.getString()+ " "+classTable.getSymbolTable().toString());
        //check formals
        classTable.getSymbolTable().enterScope();
        formals.typeCheck(classTable);
        expr.typeCheck(classTable);
        //TODO type of expr should be type of return_type

        if(return_type.getString().equals(TreeConstants.SELF_TYPE.getString())
                && !expr.get_type().getString().equals(return_type.getString())){
            debug_print("REturn type does not match evaluaed");
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("Inferred return type "+return_type.getString() +
                            " of method "+name.getString() + " does not conform to declared return type "+
                    expr.get_type().getString()+".");
            classTable.getSymbolTable().exitScope();
            return;
        }

        classTable.getSymbolTable().exitScope();

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "method\n");
        dump_AbstractSymbol(out, n + 2, name);
        formals.dump(out, n + 2);
        dump_AbstractSymbol(out, n + 2, return_type);
        expr.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_method");
        dump_AbstractSymbol(out, n + 2, name);
        for (Enumeration e = formals.getElements(); e.hasMoreElements(); ) {
            ((Formal) e.nextElement()).dump_with_types(out, n + 2);
        }
        dump_AbstractSymbol(out, n + 2, return_type);
        expr.dump_with_types(out, n + 2);
    }

    @Override
    public void populateScope(ClassTable ctable) {

    }

}


/**
 * Defines AST constructor 'attr'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class attr extends Feature {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;
    protected Expression init;

    /**
     * Creates "attr" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for name
     * @param a1         initial value for type_decl
     * @param a2         initial value for init
     */
    public attr(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        init = a3;
    }

    public TreeNode copy() {
        return new attr(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression) init.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        //TODO take care of overriding items from parent
        debug_print("In attribute type checker " + name.getString()
                +" of type "+ type_decl.getString());
        //attributes have a particular structure
        //We add symbol table entries here and also check for valid type identifiers
        if (!classTable.getClassMap().containsKey(type_decl.getString())) {
            System.out.println("New unrecognized type encountered " + type_decl);
        } else {
            //everything is  in class scope here, we add the identifier to the symbol table
            //check if already defined in this class
            if(name.getString().equals(TreeConstants.self.getString())){
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                        println("'self' cannot be the name of an attribute.");
            }

            if(classTable.getSymbolTable().probe(name)==null){
                classTable.getSymbolTable().addId(name,type_decl);
                debug_print("Adding TYPE " + type_decl+" Name " + name + " Init expr "+init);

                if(init instanceof  no_expr){
                    debug_print("Empty initializer for name "+ name.getString());
                }else{
                    //now we have added the symbol to the symbol table , we also need to check for right assignment
                    init.typeCheck(classTable);
                    debug_print("IN init type checker " + init.get_type() + " class here:" + Classes.class_self);
                    if(init.get_type().getString().equals(TreeConstants.SELF_TYPE.getString())){
                        //set type to something else
                        AbstractSymbol type = ((class_c)classTable.getClassMap().get(Classes.class_self)).getName();
                        debug_print("Setting type to " + type.getString() + " typedecl "+
                                type_decl.getString());
                        init.set_type(TreeConstants.SELF_TYPE);

                    }

                    class_c initClass = (class_c)classTable.getClassMap().get(init.get_type().getString());

                    //if init is self type then we need to change the type used here
                    if(init.get_type().equals(TreeConstants.SELF_TYPE)){
                            initClass = (class_c) classTable.getClassMap().get(Classes.class_self);
                    }

                    //set init type to self although use main class as checker for equality
                    if(init.get_type().getString().equals(type_decl.getString()) ||
                            ( initClass.getName().getString().equals(type_decl.getString()) ||
                            initClass.getParent().getString().equals(type_decl.getString()))){
                        debug_print("Types for initialization matches");
                    }else{
                        System.out.println("Mis matching types of initialization, expected:"+
                                type_decl.getString() );
                    }
                }



            }else{
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this)
                        .println("Attribute "+ name.getString()+"" +
                        " is an attribute of an inherited class");
                //System.out.println("Error Symbol:"+name.getString()  +" already defined ");
            }
        }
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "attr\n");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
        init.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_attr");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
        init.dump_with_types(out, n + 2);
    }

    @Override
    public void populateScope(ClassTable ctable) {
        ctable.getSymbolTable().addId(name,type_decl);
    }


}


/**
 * Defines AST constructor 'formalc'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class formalc extends Formal {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;

    /**
     * Creates "formalc" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for name
     * @param a1         initial value for type_decl
     */
    public formalc(int lineNumber, AbstractSymbol a1, AbstractSymbol a2) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
    }

    public TreeNode copy() {
        return new formalc(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl));
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("Type checking for formal "  + name.getString()+ " type "+ type_decl.getString());


        if(type_decl.getString().equals(TreeConstants.SELF_TYPE.getString())){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("SELF_TYPE given as a formal parameter");
            return;
        }


        if(name.getString().equals(TreeConstants.self.getString())){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("'self' cannot be the name of a formal parameter");
            return;
        }

        if(!classTable.getClassMap().containsKey(type_decl.getString())){
            System.out.println("Method formal param type not recognized");
        }else
             classTable.getSymbolTable().addId(name, type_decl);
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "formalc\n");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_formal");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
    }

}


/**
 * Defines AST constructor 'branch'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class branch extends Case {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;
    protected Expression expr;

    /**
     * Creates "branch" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for name
     * @param a1         initial value for type_decl
     * @param a2         initial value for expr
     */
    public branch(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        expr = a3;
    }

    public TreeNode copy() {
        return new branch(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression) expr.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[branch] type checker");
        classTable.getSymbolTable().addId(name,type_decl);
        expr.typeCheck(classTable);

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "branch\n");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
        expr.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_branch");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
        expr.dump_with_types(out, n + 2);
    }

}


/**
 * Defines AST constructor 'assign'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class assign extends Expression {
    protected AbstractSymbol name;
    protected Expression expr;

    /**
     * Creates "assign" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for name
     * @param a1         initial value for expr
     */
    public assign(int lineNumber, AbstractSymbol a1, Expression a2) {
        super(lineNumber);
        name = a1;
        expr = a2;
    }

    public TreeNode copy() {
        return new assign(lineNumber, copy_AbstractSymbol(name), (Expression) expr.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        //set return type of this expression to return type of identifier
        debug_print("[ASSIGN] In type checking for "+ name.getString());
        if(name.getString().equals(TreeConstants.self.getString())){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this)
                    .println("Cannot assign to 'self'.");
            return;
        }
        if(classTable.getSymbolTable().lookup(name)==null){
            //error we have not encountered this identifier
            System.out.println("Undefined identifier encountered");
        }else{
            //name is in scope
            //typecheck the expression
            expr.typeCheck(classTable);
            //type of assign node is type of expr
            set_type(expr.get_type());
            AbstractSymbol type = (AbstractSymbol) classTable.getSymbolTable().lookup(name);

            //TODO cleanup this mess
            class_c clstypeName = (class_c)classTable.getClassMap().get(type.getString());
            debug_print("[Assign] class of assigned "+ clstypeName.getName());

            class_c clstypeExpr = (class_c)classTable.getClassMap().get(expr.get_type().getString());
            debug_print("[Assign] class of asignee "+ clstypeExpr.getName());

            if(clstypeExpr.getName().getString().equals(clstypeName.getName().getString()) ||
                    clstypeExpr.getParent().getString().equals(clstypeName.getName().getString())){
                debug_print("Class types match");
            }else{
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).println("Type "+ clstypeExpr.getName().getString() +
                        " of assigned expression does not conform to declared type "+
                        clstypeName.getName().getString() + " of identifier "+name+".");
                debug_print("Class types of assigned and asignee don't match");
            }

            //check for assign correctness
        }


    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "assign\n");
        dump_AbstractSymbol(out, n + 2, name);
        expr.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_assign");
        dump_AbstractSymbol(out, n + 2, name);
        expr.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'static_dispatch'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class static_dispatch extends Expression {
    protected Expression expr;
    protected AbstractSymbol type_name;
    protected AbstractSymbol name;
    protected Expressions actual;

    /**
     * Creates "static_dispatch" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for expr
     * @param a1         initial value for type_name
     * @param a2         initial value for name
     * @param a3         initial value for actual
     */
    public static_dispatch(int lineNumber, Expression a1, AbstractSymbol a2, AbstractSymbol a3, Expressions a4) {
        super(lineNumber);
        expr = a1;
        type_name = a2;
        name = a3;
        actual = a4;
    }

    public TreeNode copy() {
        return new static_dispatch(lineNumber, (Expression) expr.copy(), copy_AbstractSymbol(type_name), copy_AbstractSymbol(name), (Expressions) actual.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("In type check for static dispatch");
        expr.typeCheck(classTable);
        debug_print("Expression for left:" + expr.get_type().getString());
        debug_print("Expr for right:" + type_name.getString());

        for (Enumeration e = actual.getElements(); e.hasMoreElements(); ) {
            Expression ex = (Expression) e.nextElement();
            ex.typeCheck(classTable);

        }
        if(expr.get_type().equals(TreeConstants.SELF_TYPE)){
            debug_print("Currently parsing " + Classes.class_self);
            if(!type_name.getString().equals(Classes.class_self)){
                //Error
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                        println("Expression type "+ TreeConstants.SELF_TYPE.getString()+
                " does not conform to declared static dispatch type "+ type_name.getString()+".");
            }
        }
        debug_print("MethodName:"+ name.getString() + " ClasstypeName:"+ type_name.getString());
        class_c methodClass = (class_c) classTable.getClassMap().get(type_name.getString());
        set_type(methodClass.getReturnTypeForMethod(classTable,name.getString()));

        //Infer return type from these two things

        //set_type(actual.);

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "static_dispatch\n");
        expr.dump(out, n + 2);
        dump_AbstractSymbol(out, n + 2, type_name);
        dump_AbstractSymbol(out, n + 2, name);
        actual.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_static_dispatch");
        expr.dump_with_types(out, n + 2);
        dump_AbstractSymbol(out, n + 2, type_name);
        dump_AbstractSymbol(out, n + 2, name);
        out.println(Utilities.pad(n + 2) + "(");
        for (Enumeration e = actual.getElements(); e.hasMoreElements(); ) {
            ((Expression) e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'dispatch'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class dispatch extends Expression {
    protected Expression expr;
    protected AbstractSymbol name;
    protected Expressions actual;

    /**
     * Creates "dispatch" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for expr
     * @param a1         initial value for name
     * @param a2         initial value for actual
     */
    public dispatch(int lineNumber, Expression a1, AbstractSymbol a2, Expressions a3) {
        super(lineNumber);
        expr = a1;
        name = a2;
        actual = a3;
    }

    public TreeNode copy() {
        return new dispatch(lineNumber, (Expression) expr.copy(), copy_AbstractSymbol(name), (Expressions) actual.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[dispatch]In type check for dispatch");
        //typecheck the expression on which method is called
        expr.typeCheck(classTable);
        debug_print("Type of Object: " + expr.get_type() + " method Name:" + name.getString());

        AbstractSymbol typeOnWhichMethodCalled = expr.get_type();
        debug_print("Type on which method called "+ typeOnWhichMethodCalled.getString());
        //if no object explicitly called then assume its self type
        //if self type assume dispatch is on the same class
        //check after traversing class definition if method is defined and if so
        //whether argument type and return type match what is declared
        if(classTable.getSymbolTable().probe(name)!=null){
            debug_print("Name "+ name.getString() + " is in scope");
        }
        class_c obj = (class_c) classTable.getClassMap().get(typeOnWhichMethodCalled.getString());
        if(typeOnWhichMethodCalled.equals(TreeConstants.SELF_TYPE)){
            obj = (class_c) classTable.getClassMap().get(Classes.class_self);
        }

        for (Enumeration e = actual.getElements(); e.hasMoreElements(); ) {
            Expression el = (Expression) e.nextElement();
            el.typeCheck(classTable);
            debug_print("Dumping el");
           // el.dump_with_types(System.out,5);
            debug_print("Evaluating type of formal argument passed as " + el.get_type().getString());
        }


       obj.checkMethodInvocation(classTable,name.getString(), actual, this,expr.get_type());
       // set_type()
         //we havn't type checked the actual arguments yet

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "dispatch\n");
        expr.dump(out, n + 2);
        dump_AbstractSymbol(out, n + 2, name);
        actual.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_dispatch");
        expr.dump_with_types(out, n + 2);
        dump_AbstractSymbol(out, n + 2, name);
        out.println(Utilities.pad(n + 2) + "(");
        for (Enumeration e = actual.getElements(); e.hasMoreElements(); ) {
            ((Expression) e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'cond'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class cond extends Expression {
    protected Expression pred;
    protected Expression then_exp;
    protected Expression else_exp;

    /**
     * Creates "cond" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for pred
     * @param a1         initial value for then_exp
     * @param a2         initial value for else_exp
     */
    public cond(int lineNumber, Expression a1, Expression a2, Expression a3) {
        super(lineNumber);
        pred = a1;
        then_exp = a2;
        else_exp = a3;
    }

    public TreeNode copy() {
        return new cond(lineNumber, (Expression) pred.copy(), (Expression) then_exp.copy(), (Expression) else_exp.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[if] condition typecheker");
        pred.typeCheck(classTable);
        then_exp.typeCheck(classTable);
        else_exp.typeCheck(classTable);
        set_type(TreeConstants.Object_);
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "cond\n");
        pred.dump(out, n + 2);
        then_exp.dump(out, n + 2);
        else_exp.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_cond");
        pred.dump_with_types(out, n + 2);
        then_exp.dump_with_types(out, n + 2);
        else_exp.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'loop'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class loop extends Expression {
    protected Expression pred;
    protected Expression body;

    /**
     * Creates "loop" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for pred
     * @param a1         initial value for body
     */
    public loop(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        pred = a1;
        body = a2;
    }

    public TreeNode copy() {
        return new loop(lineNumber, (Expression) pred.copy(), (Expression) body.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("In while loop typecheck");
        pred.typeCheck(classTable);
        debug_print("Predicate type "+ pred.get_type());
        if(!pred.get_type().equals(TreeConstants.Bool)){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("Loop condition does not have type Bool.");
        }
        body.typeCheck(classTable);
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "loop\n");
        pred.dump(out, n + 2);
        body.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_loop");
        pred.dump_with_types(out, n + 2);
        body.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'typcase'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class typcase extends Expression {
    protected Expression expr;
    protected Cases cases;
    Set<String> typesSeen;

    /**
     * Creates "typcase" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for expr
     * @param a1         initial value for cases
     */
    public typcase(int lineNumber, Expression a1, Cases a2) {
        super(lineNumber);
        expr = a1;
        typesSeen = new HashSet<String>() ;
        cases = a2;
    }

    public TreeNode copy() {
        return new typcase(lineNumber, (Expression) expr.copy(), (Cases) cases.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[typecase] type checker");
        expr.typeCheck(classTable);

        for (Enumeration e = cases.getElements(); e.hasMoreElements(); ) {
            branch c = (branch) e.nextElement();
            c.typeCheck(classTable);

            if(typesSeen.contains(c.type_decl.getString())){
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                        println("Duplicate branch "+ c.type_decl.getString()+ " in case statement.");
            }
            typesSeen.add(c.type_decl.getString());

            debug_print("In branch details name:"+ c.name.getString() + " type:" + c.type_decl.getString() +
                    " expr:" + expr.get_type().getString());
            set_type(TreeConstants.Object_);
        }

        }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "typcase\n");
        expr.dump(out, n + 2);
        cases.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_typcase");
        expr.dump_with_types(out, n + 2);
        for (Enumeration e = cases.getElements(); e.hasMoreElements(); ) {
            ((Case) e.nextElement()).dump_with_types(out, n + 2);
        }
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'block'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class block extends Expression {
    protected Expressions body;

    /**
     * Creates "block" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for body
     */
    public block(int lineNumber, Expressions a1) {
        super(lineNumber);
        body = a1;
    }

    public TreeNode copy() {
        return new block(lineNumber, (Expressions) body.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        body.typeCheck(classTable);
        debug_print("In block type check for expr  "+ body.toString());
        Expression lastExpr =null;
        //type of block is last of the type of expressions in the Expressions clause
        for (Enumeration e = body.getElements(); e.hasMoreElements(); ) {
            Expression expr = (Expression) e.nextElement();
            expr.typeCheck(classTable);
            debug_print("Expr_print" + expr.toString());
            //expr.dump_with_types(System.out,3);
            lastExpr = expr;

        }
        set_type(lastExpr.get_type());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "block\n");
        body.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_block");
        for (Enumeration e = body.getElements(); e.hasMoreElements(); ) {
            ((Expression) e.nextElement()).dump_with_types(out, n + 2);
        }
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'let'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class let extends Expression {
    protected AbstractSymbol identifier;
    protected AbstractSymbol type_decl;
    protected Expression init;
    protected Expression body;

    /**
     * Creates "let" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for identifier
     * @param a1         initial value for type_decl
     * @param a2         initial value for init
     * @param a3         initial value for body
     */
    public let(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3, Expression a4) {
        super(lineNumber);
        identifier = a1;
        type_decl = a2;
        init = a3;
        body = a4;
    }

    public TreeNode copy() {
        return new let(lineNumber, copy_AbstractSymbol(identifier), copy_AbstractSymbol(type_decl), (Expression) init.copy(), (Expression) body.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        classTable.getSymbolTable().enterScope();

        //typecheck initialization and body
        debug_print("[Let] typechecker "+ identifier.getString() + " typedecl " + type_decl.getString()
         + " init:" + init.toString());


        if(identifier.getString().equals(TreeConstants.self.getString())){
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename), this).
                    println("'self' cannot be bound in a let expression.");
        }

        classTable.getSymbolTable().addId(identifier,type_decl);
        init.typeCheck(classTable);
        //body.typeCheck(classTable);
        debug_print("In let type checker init type:" + init.get_type()  +
                " declared type:"+type_decl.getString() );
        if(!init.get_type().getString().equals(TreeConstants.No_type.getString())){
            if(!init.get_type().getString().equals(type_decl.getString())){
                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                        println("Inferred type "+ init.get_type() + " of initialization of "+
                                identifier.getString()+ " does not conform to identifier's declared type " + type_decl.getString()+".");
            }
        }

        //now variable has type of declared type in symbol table verify thats its actually true
        //check if body type same as type_decl of identifier

        body.typeCheck(classTable);
        set_type(body.get_type());
        classTable.getSymbolTable().exitScope();

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "let\n");
        dump_AbstractSymbol(out, n + 2, identifier);
        dump_AbstractSymbol(out, n + 2, type_decl);
        init.dump(out, n + 2);
        body.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_let");
        dump_AbstractSymbol(out, n + 2, identifier);
        dump_AbstractSymbol(out, n + 2, type_decl);
        init.dump_with_types(out, n + 2);
        body.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'plus'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class plus extends Expression {
    protected Expression e1;
    protected Expression e2;

    /**
     * Creates "plus" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     * @param a1         initial value for e2
     */
    public plus(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new plus(lineNumber, (Expression) e1.copy(), (Expression) e2.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[plus]type check ");
        e1.typeCheck(classTable);
        e2.typeCheck(classTable);
    //    debug_print("PLUS DEBUGGING PRINTING");
    //    e1.dump_with_types(System.out,4);
    //    e2.dump_with_types(System.out,4);
    //    debug_print("PLUS DEBUGGING PRINTING oVER");

        if(e1.get_type().getString().equals(e2.get_type().getString())){
            set_type(e1.get_type());
        }else{
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("non-Int arguments:" + e1.get_type().getString() + "+"+
            e2.get_type().getString());

         }

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "plus\n");
        e1.dump(out, n + 2);
        e2.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_plus");
        e1.dump_with_types(out, n + 2);
        e2.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'sub'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class sub extends Expression {
    protected Expression e1;
    protected Expression e2;

    /**
     * Creates "sub" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     * @param a1         initial value for e2
     */
    public sub(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new sub(lineNumber, (Expression) e1.copy(), (Expression) e2.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("In [sub] type checker");
        e1.typeCheck(classTable);
        e2.typeCheck(classTable);
        set_type(e1.get_type());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "sub\n");
        e1.dump(out, n + 2);
        e2.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_sub");
        e1.dump_with_types(out, n + 2);
        e2.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'mul'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class mul extends Expression {
    protected Expression e1;
    protected Expression e2;

    /**
     * Creates "mul" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     * @param a1         initial value for e2
     */
    public mul(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new mul(lineNumber, (Expression) e1.copy(), (Expression) e2.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[mul]type check ");
        e1.typeCheck(classTable);
        e2.typeCheck(classTable);
        if(e1.get_type() == e2.get_type()){
            set_type(e1.get_type());
        }else{
            System.out.println("Types of mult don't match");
        }
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "mul\n");
        e1.dump(out, n + 2);
        e2.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_mul");
        e1.dump_with_types(out, n + 2);
        e2.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'divide'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class divide extends Expression {
    protected Expression e1;
    protected Expression e2;

    /**
     * Creates "divide" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     * @param a1         initial value for e2
     */
    public divide(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new divide(lineNumber, (Expression) e1.copy(), (Expression) e2.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[div]type check ");
        e1.typeCheck(classTable);
        e2.typeCheck(classTable);
        if(e1.get_type() == e2.get_type()){
            set_type(e1.get_type());
        }else{
            System.out.println("Types of div don't match");
        }

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "divide\n");
        e1.dump(out, n + 2);
        e2.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_divide");
        e1.dump_with_types(out, n + 2);
        e2.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'neg'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class neg extends Expression {
    protected Expression e1;

    /**
     * Creates "neg" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     */
    public neg(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }

    public TreeNode copy() {
        return new neg(lineNumber, (Expression) e1.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        e1.typeCheck(classTable);
        debug_print("[NEG] type check");
        set_type(e1.get_type());

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "neg\n");
        e1.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_neg");
        e1.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'lt'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class lt extends Expression {
    protected Expression e1;
    protected Expression e2;

    /**
     * Creates "lt" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     * @param a1         initial value for e2
     */
    public lt(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new lt(lineNumber, (Expression) e1.copy(), (Expression) e2.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        e1.typeCheck(classTable);
        e2.typeCheck(classTable);
        this.set_type(TreeConstants.Bool);
         debug_print("[lt] type checker "+ this.get_type().getString());
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "lt\n");
        e1.dump(out, n + 2);
        e2.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_lt");
        e1.dump_with_types(out, n + 2);
        e2.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'eq'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class eq extends Expression {
    protected Expression e1;
    protected Expression e2;

    /**
     * Creates "eq" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     * @param a1         initial value for e2
     */
    public eq(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new eq(lineNumber, (Expression) e1.copy(), (Expression) e2.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[equality]type checker called");
        //check if they are of the same types
        e1.typeCheck(classTable);
        e2.typeCheck(classTable);
        debug_print("[eq printing] first:" + e1.get_type() + " second:"+ e2.get_type());

        //you can have unequal comparisions here
        if(!e1.get_type().getString().equals(e2.get_type().getString())){
            //check if they are empty
            class_c e1class = (class_c) classTable.getClassMap().get(e1.get_type().getString());
            class_c e2class = (class_c) classTable.getClassMap().get(e2.get_type().getString());
            if(e1class.getFeatures().getLength()==0 && e2class.getFeatures().getLength()==0){
                debug_print("Special case");
            }else{

                classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                        println("Illegal comparison with a basic type.");
            }
        }
        set_type(TreeConstants.Bool);
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "eq\n");
        e1.dump(out, n + 2);
        e2.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_eq");
        e1.dump_with_types(out, n + 2);
        e2.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'leq'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class leq extends Expression {
    protected Expression e1;
    protected Expression e2;

    /**
     * Creates "leq" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     * @param a1         initial value for e2
     */
    public leq(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }

    public TreeNode copy() {
        return new leq(lineNumber, (Expression) e1.copy(), (Expression) e2.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        e1.typeCheck(classTable);
        e2.typeCheck(classTable);
        set_type(TreeConstants.Bool);
        debug_print("[leq] type checker set return type to " + this.get_type().getString());

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "leq\n");
        e1.dump(out, n + 2);
        e2.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_leq");
        e1.dump_with_types(out, n + 2);
        e2.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'comp'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class comp extends Expression {
    protected Expression e1;

    /**
     * Creates "comp" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     */
    public comp(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }

    public TreeNode copy() {
        return new comp(lineNumber, (Expression) e1.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        e1.typeCheck(classTable);
        set_type(e1.get_type());
        debug_print("[Compare] type check for "+  e1.get_type());

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "comp\n");
        e1.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_comp");
        e1.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'int_const'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class int_const extends Expression {
    protected AbstractSymbol token;

    /**
     * Creates "int_const" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for token
     */
    public int_const(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        token = a1;
    }

    public TreeNode copy() {
        return new int_const(lineNumber, copy_AbstractSymbol(token));
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[INT expr]In int type check for "+ token.getString());
        this.set_type(TreeConstants.Int);

    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "int_const\n");
        dump_AbstractSymbol(out, n + 2, token);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_int");
        dump_AbstractSymbol(out, n + 2, token);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'bool_const'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class bool_const extends Expression {
    protected Boolean val;

    /**
     * Creates "bool_const" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for val
     */
    public bool_const(int lineNumber, Boolean a1) {
        super(lineNumber);
        val = a1;
    }

    public TreeNode copy() {
        return new bool_const(lineNumber, copy_Boolean(val));
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[Bool expr]In bool type check");
        set_type(TreeConstants.Bool);
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "bool_const\n");
        dump_Boolean(out, n + 2, val);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_bool");
        dump_Boolean(out, n + 2, val);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'string_const'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class string_const extends Expression {
    protected AbstractSymbol token;

    /**
     * Creates "string_const" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for token
     */
    public string_const(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        token = a1;
    }

    public TreeNode copy() {
        return new string_const(lineNumber, copy_AbstractSymbol(token));
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[String expr]In string type check");
        set_type(TreeConstants.Str);
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "string_const\n");
        dump_AbstractSymbol(out, n + 2, token);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_string");
        out.print(Utilities.pad(n + 2) + "\"");
        Utilities.printEscapedString(out, token.getString());
        out.println("\"");
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'new_'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class new_ extends Expression {
    protected AbstractSymbol type_name;

    /**
     * Creates "new_" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for type_name
     */
    public new_(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        type_name = a1;
    }

    public TreeNode copy() {
        return new new_(lineNumber, copy_AbstractSymbol(type_name));
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        //class used must be present in scope
        debug_print("In new type checker");


        if(!classTable.getClassMap().containsKey(type_name.getString())){
            //check for self type
            if(type_name.equals(TreeConstants.SELF_TYPE)){
                class_c classType = (class_c) classTable.getClassMap().get(Classes.class_self);
                debug_print("[new type self] "+ classType.getName().getString());
                set_type(TreeConstants.SELF_TYPE);
                return;
            }

            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("'new' used with undefined class "+ type_name+".");
        }else{
            set_type(type_name);
        }


    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "new_\n");
        dump_AbstractSymbol(out, n + 2, type_name);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_new");
        dump_AbstractSymbol(out, n + 2, type_name);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'isvoid'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class isvoid extends Expression {
    protected Expression e1;

    /**
     * Creates "isvoid" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for e1
     */
    public isvoid(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }

    public TreeNode copy() {
        return new isvoid(lineNumber, (Expression) e1.copy());
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[Is void] type check");
        e1.typeCheck(classTable);
        set_type(TreeConstants.Bool);
        //todo how to check for isvoid
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "isvoid\n");
        e1.dump(out, n + 2);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_isvoid");
        e1.dump_with_types(out, n + 2);
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'no_expr'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class no_expr extends Expression {
    /**
     * Creates "no_expr" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     */
    public no_expr(int lineNumber) {
        super(lineNumber);
    }

    public TreeNode copy() {
        return new no_expr(lineNumber);
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        debug_print("[no_expr] type check");
          set_type(TreeConstants.No_type);
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "no_expr\n");
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_no_expr");
        dump_type(out, n);
    }

}


/**
 * Defines AST constructor 'object'.
 * <p>
 * See <a href="TreeNode.html">TreeNode</a> for full documentation.
 */
class object extends Expression {
    protected AbstractSymbol name;

    /**
     * Creates "object" AST node.
     *
     * @param lineNumber the line in the source file from which this node came.
     * @param a0         initial value for name
     */
    public object(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        name = a1;
    }

    public TreeNode copy() {
        return new object(lineNumber, copy_AbstractSymbol(name));
    }

    @Override
    public void typeCheck(ClassTable classTable) {
        //set type of this to be object
        debug_print("[Object expr] typechecker called");
        if(name.getString().equals(TreeConstants.self.getString())){
            //type is self
         //   debug_print("Encounntered a self type ");
            set_type(TreeConstants.SELF_TYPE);
            return;
        }


        set_type(TreeConstants.Object_);
        //name should be in scope
        if(classTable.getSymbolTable().lookup(name)==null){
            //debug_print("[OBJECT] not in scope "+ name);
            classTable.semantError(AbstractTable.idtable.addString(Flags.in_filename),this).
                    println("Undeclared identifier "+ name+".");
        }else{
            set_type((AbstractSymbol) classTable.getSymbolTable().lookup(name));
        }
    }

    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "object\n");
        dump_AbstractSymbol(out, n + 2, name);
    }


    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_object");
        dump_AbstractSymbol(out, n + 2, name);
        dump_type(out, n);
    }

}


