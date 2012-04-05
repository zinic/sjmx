from sjmx.filter import JMXFilterlet

# Python <3
class PythonFilter(JMXFilterlet):
   def perform(self, context):
      print "Context size: " + `len(context.contextInfo())`

      for mbeanInfo in context.contextInfo():
         output = ""

         if mbeanInfo.getName() is not None:
            output += mbeanInfo.getName()

         if mbeanInfo.getType() is not None:
            output += "(" + mbeanInfo.getType() + ")"

         print output

         for attrInfo in mbeanInfo.getAttributes():
            if attrInfo.isReadable():
               print "\tAttr: " + attrInfo.getName()

