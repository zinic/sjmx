from sjmx.filter import JMXFilterlet

# Python <3
class PythonFilter(JMXFilterlet):
   def perform(self, context):
      jmxInfo = context.jmxInfo();

      for domain in jmxInfo:
         print "Found domain: " + domain.getName()

         if domain.getName() == "java.lang":
            self.readMbeans(domain.getManagementBeans(), context)
            break

   def readMbeans(self, mbeans, context):
      for mbean in mbeans:
         output = "\tFound mbean: "

         if mbean.getName() is not None:
            output += mbean.getName()

         if mbean.getType() is not None:
            output += " - Type: " + mbean.getType()

         print output

         if mbean.getType() == "Threading":
            self.readMbean(mbean, context)
            break

   def readMbean (self, mbean, context):
      for mbeanAttr in mbean.getAttributes():
         print "\t\tFound attribute: " + mbeanAttr.getName()

         if mbeanAttr.getName() == "DaemonThreadCount":
            context.builder().alias(mbean, mbeanAttr.getName(), "TotoalThreads")
         elif mbeanAttr.getName() == "PeakThreadCount":
            context.builder().alias(mbean, mbeanAttr.getName(), "PeakThreads")

