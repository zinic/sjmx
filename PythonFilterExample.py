from sjmx.filter import JMXFilterlet

# Python <3
class PythonFilter(JMXFilterlet):
   def perform(self, context):
      jmxInfo = context.jmxInfo();

      for domain in jmxInfo:
         if domain == "java.lang":
            self.readMbeans(jmxInfo[domain], context)
            break

   def readMbeans(self, mbeans, context):
      for mbean in mbeans:
         if mbean.getType() == "Threading":
            self.readMbean(mbean, context)
            break

   def readMbean (self, mbean, context):
      for mbeanAttr in mbean.getAttributes():
         if mbeanAttr.getName() == "PeakThreadCount":
            context.builder().alias(mbean, mbeanAttr.getName(), "PeakThreads")
            break

