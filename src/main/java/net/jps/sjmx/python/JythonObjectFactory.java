package net.jps.sjmx.python;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * Jython Object Factory using PySystemState
 */
public class JythonObjectFactory<T> {

   private final PythonInterpreter interpreter;
   private final Class<T> interfaceType;

   public JythonObjectFactory(PythonInterpreter interpreter, Class<T> interfaceType) {
      this.interpreter = interpreter;
      this.interfaceType = interfaceType;
   }

   /**
    * The createObject() method is responsible for the actual creation of the
    * Jython object into Java bytecode.
    */
   public T createObject(String pyClassName) throws ClassNotFoundException {
      Object javaInt = null;

      // Create a PythonInterpreter object and import our Jython module
      // to obtain a reference.
//      interpreter.exec("from " + pyClassName + " import " + pyClassName);

      final PyObject pyClass = interpreter.get(pyClassName);

      // Create a new object reference of the Jython class store into PyObject
      final PyObject newObj = pyClass.__call__();

      // Call __tojava__ method on the new object along with the interface name
      // to create the java bytecode
      return (T) newObj.__tojava__(interfaceType);
   }
}